package site.bzyl.shortlink.project.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import site.bzyl.shortlink.project.common.convention.exception.ServiceException;
import site.bzyl.shortlink.project.dao.entity.LinkDO;
import site.bzyl.shortlink.project.dao.mapper.ShortLinkMapper;
import site.bzyl.shortlink.project.dto.req.ShortLinkCreateReqDTO;
import site.bzyl.shortlink.project.dto.resp.ShortLinkCreateRespDTO;
import site.bzyl.shortlink.project.service.ShortLinkService;
import site.bzyl.shortlink.project.toolkit.HashUtil;

import static site.bzyl.shortlink.project.common.constants.ShortLinkConstant.DEFAULT_DOMAIN;
import static site.bzyl.shortlink.project.common.constants.ShortLinkConstant.DEFAULT_PROTOCOL;

/**
 * 短链接业务层实现
 */
@Service
public class ShortLinkServiceImpl extends ServiceImpl<ShortLinkMapper, LinkDO> implements ShortLinkService {
    @Override
    public ShortLinkCreateRespDTO createShortLink(ShortLinkCreateReqDTO requestParam) {
        String suffix = generateShortLinkSuffix(requestParam.getOriginUri());
        String fullShortUri = generateFullShortUri(DEFAULT_DOMAIN, suffix);
        LinkDO linkDO = LinkDO
                .builder()
                .shortUri(suffix)
                .fullShortUri(fullShortUri)
                .gid(requestParam.getGid())
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

    private static String generateShortLinkSuffix(String originUri) {
        String suffix = HashUtil.hashToBase62(originUri);
        return suffix;
    }

    private static String generateFullShortUri(String domain, String shortUri) {
        StringBuilder builder = new StringBuilder();
        builder.append(DEFAULT_PROTOCOL)
                .append(domain)
                .append("/")
                .append(shortUri);
        return builder.toString();
    }
}
