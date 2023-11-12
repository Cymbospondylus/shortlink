package site.bzyl.shortlink.project.service.impl;

import cn.hutool.core.text.StrBuilder;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBloomFilter;
import org.springframework.stereotype.Service;
import site.bzyl.shortlink.project.common.convention.exception.ServiceException;
import site.bzyl.shortlink.project.dao.entity.LinkDO;
import site.bzyl.shortlink.project.dao.mapper.ShortLinkMapper;
import site.bzyl.shortlink.project.dto.req.ShortLinkCreateReqDTO;
import site.bzyl.shortlink.project.dto.req.ShortLinkPageReqDTO;
import site.bzyl.shortlink.project.dto.resp.ShortLinkCreateRespDTO;
import site.bzyl.shortlink.project.dto.resp.ShortLinkPageRespDTO;
import site.bzyl.shortlink.project.service.ShortLinkService;
import site.bzyl.shortlink.project.toolkit.HashUtil;

import static site.bzyl.shortlink.project.common.constants.ShortLinkConstant.*;

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
                .eq(LinkDO::getEnableStatus, ENABLE_STATUS)
                .orderByDesc(LinkDO::getCreateTime);
        IPage<ShortLinkPageRespDTO> resultPage = baseMapper.selectPage(requestParam, queryWrapper);

        return resultPage;
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
