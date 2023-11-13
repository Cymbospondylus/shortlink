package site.bzyl.shortlink.project.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.text.StrBuilder;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBloomFilter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import site.bzyl.shortlink.project.common.convention.exception.ClientException;
import site.bzyl.shortlink.project.common.convention.exception.ServiceException;
import site.bzyl.shortlink.project.dao.entity.LinkDO;
import site.bzyl.shortlink.project.dao.mapper.ShortLinkMapper;
import site.bzyl.shortlink.project.dto.req.ShortLinkCreateReqDTO;
import site.bzyl.shortlink.project.dto.req.ShortLinkPageReqDTO;
import site.bzyl.shortlink.project.dto.req.ShortLinkUpdateReqDTO;
import site.bzyl.shortlink.project.dto.resp.ShortLinkCountRespDTO;
import site.bzyl.shortlink.project.dto.resp.ShortLinkCreateRespDTO;
import site.bzyl.shortlink.project.dto.resp.ShortLinkPageRespDTO;
import site.bzyl.shortlink.project.service.ShortLinkService;
import site.bzyl.shortlink.project.toolkit.HashUtil;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import static site.bzyl.shortlink.project.common.constants.ShortLinkConstant.*;
import static site.bzyl.shortlink.project.common.enums.ShortLinkValidDateType.PERMANENT;

/**
 * 短链接业务层实现
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class ShortLinkServiceImpl extends ServiceImpl<ShortLinkMapper, LinkDO> implements ShortLinkService {
    private final RBloomFilter<String> shortLinkCreationCachePenetrationBloomFilter;

    @Override
    public ShortLinkCreateRespDTO createShortLink(ShortLinkCreateReqDTO requestParam) {
        String suffix = generateShortLinkSuffix(requestParam);
        // TODO 这里拿到一个布隆过滤器不存在的 suffix 应该再根据suffix加个分布式锁保证不会重复添加，虽然是小概率事件
        String fullShortUri = generateFullShortUri(DEFAULT_DOMAIN, suffix);
        LinkDO linkDO = LinkDO
                .builder()
                .shortUri(suffix)
                .fullShortUri(fullShortUri)
                .gid(requestParam.getGid())
                .enableStatus(ENABLE_STATUS)
                .domain(DEFAULT_DOMAIN)
                .originUri(requestParam.getOriginUri())
                .createdType(requestParam.getCreatedType())
                .validDateType(requestParam.getValidDateType())
                .validDate(requestParam.getValidDate())
                .description(requestParam.getDescription())
                .build();

        int insert = baseMapper.insert(linkDO);
        if (insert < 1) {
            ServiceException.cast("短链接创建失败");
        }
        shortLinkCreationCachePenetrationBloomFilter.add(fullShortUri);
        return ShortLinkCreateRespDTO
                .builder()
                .shortUri(linkDO.getShortUri())
                .fullShortUri(linkDO.getFullShortUri())
                .originUri(linkDO.getOriginUri())
                .domain(linkDO.getDomain())
                .gid(linkDO.getGid())
                .validDate(linkDO.getValidDate())
                .description(linkDO.getDescription())
                .build();
    }

    @Override
    public IPage<ShortLinkPageRespDTO> pageShortLink(ShortLinkPageReqDTO requestParam) {
        LambdaQueryWrapper<LinkDO> queryWrapper = Wrappers.lambdaQuery(LinkDO.class)
                .eq(LinkDO::getGid, requestParam.getGid())
                .orderByDesc(LinkDO::getCreateTime);
        IPage<LinkDO> resultPage = baseMapper.selectPage(requestParam, queryWrapper);
        return resultPage.convert(each -> BeanUtil.toBean(each, ShortLinkPageRespDTO.class));
    }

    @Override
    public List<ShortLinkCountRespDTO> countShortLink(List<String> requestParam) {
        List<ShortLinkCountRespDTO> resultList = requestParam.stream()
                .map(each -> {
                    LambdaQueryWrapper<LinkDO> queryWrapper = Wrappers.lambdaQuery(LinkDO.class)
                            .eq(LinkDO::getGid, each);
                    Integer shortLinkCount = Math.toIntExact(baseMapper.selectCount(queryWrapper));
                    return ShortLinkCountRespDTO
                            .builder()
                            .gid(each)
                            .shortLinkCount(shortLinkCount)
                            .build();
                })
                .collect(Collectors.toList());
        return resultList;
    }

    @Override
    @Transactional
    public void updateShortLink(ShortLinkUpdateReqDTO requestParam) {
        LambdaQueryWrapper<LinkDO> queryWrapper = Wrappers.lambdaQuery(LinkDO.class)
                .eq(LinkDO::getFullShortUri, requestParam.getFullShortUri());
        LinkDO hasShortLink = baseMapper.selectOne(queryWrapper);
        Optional.ofNullable(hasShortLink).orElseThrow(() -> new ClientException("短链接记录不存在"));

        LinkDO shortLinkDO = LinkDO
                .builder()
                .domain(hasShortLink.getDomain())
                .shortUri(hasShortLink.getShortUri())
                .clickNum(hasShortLink.getClickNum())
                .gid(requestParam.getGid())
                .fullShortUri(requestParam.getFullShortUri())
                .favicon(requestParam.getFavicon())
                .originUri(requestParam.getOriginUri())
                .enableStatus(requestParam.getEnableStatus())
                .validDateType(requestParam.getValidDateType())
                .validDate(requestParam.getValidDate())
                .description(requestParam.getDescription())
                .createdType(hasShortLink.getCreatedType())
                .build();
        // gid 相等表示没有修改分组，只是修改短链接信息
        if (Objects.equals(hasShortLink.getGid(), shortLinkDO.getGid())) {
            LambdaUpdateWrapper<LinkDO> updateWrapper = Wrappers.lambdaUpdate(LinkDO.class)
                    .eq(LinkDO::getGid, shortLinkDO.getGid())
                    .eq(LinkDO::getFullShortUri, shortLinkDO.getFullShortUri())
                    // 根据有效期类型判断, 如果是永久有效则把有效期设置为 null
                    .set(shortLinkDO.getValidDateType().equals(PERMANENT.getType()), LinkDO::getValidDate, null);
            int update = baseMapper.update(shortLinkDO, updateWrapper);
            if (update < 1) {
                log.error("修改短链接信息失败, gid:{}, fullShortUri:{}", requestParam.getGid(), requestParam.getFullShortUri());
                ServiceException.cast("修改短链接信息失败");
            }
        } // gid 不相等, 表示当前短链接移动到了新的分组
        else {
            LambdaQueryWrapper<LinkDO> deleteQueryWrapper = Wrappers.lambdaQuery(LinkDO.class)
                    .eq(LinkDO::getGid, hasShortLink.getGid())
                    .eq(LinkDO::getFullShortUri, hasShortLink.getFullShortUri());
            int insert = baseMapper.insert(shortLinkDO);
            int delete = baseMapper.delete(deleteQueryWrapper);
            if (insert < 1 || delete < 1) {
                log.error("修改短链接信息失败, gid:{}, fullShortUri:{}", requestParam.getGid(), requestParam.getFullShortUri());
                ServiceException.cast("修改短链接信息失败");
            }
        }
    }

    private String generateShortLinkSuffix(ShortLinkCreateReqDTO requestParam) {
        int retryCount = 0;
        while (true) {
            // 使用 原始长链接 + 系统当前毫秒数, 每次尝试获取hash值都不同
            String suffix = HashUtil.hashToBase62(requestParam.getOriginUri() + System.currentTimeMillis());
            String fullShortUri = generateFullShortUri(DEFAULT_DOMAIN, suffix);
            if (!shortLinkCreationCachePenetrationBloomFilter.contains(fullShortUri)) {
                return suffix;
            }
            retryCount++;
            if (retryCount >= SHORT_LINK_CREATION_MAX_RETRY) {
                ServiceException.cast("短链接创建重试次数过多");
            }
        }
    }

    private String generateFullShortUri(String domain, String shortUri) {
        return StrBuilder.create()  // new StringBuilder()
                .append(DEFAULT_PROTOCOL)
                .append(domain)
                .append("/")
                .append(shortUri)
                .toString();
    }
}
