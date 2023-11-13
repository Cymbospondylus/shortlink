package site.bzyl.shortlink.project.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import site.bzyl.shortlink.project.dao.entity.LinkDO;
import site.bzyl.shortlink.project.dto.req.ShortLinkCreateReqDTO;
import site.bzyl.shortlink.project.dto.req.ShortLinkPageReqDTO;
import site.bzyl.shortlink.project.dto.resp.ShortLinkCountRespDTO;
import site.bzyl.shortlink.project.dto.resp.ShortLinkCreateRespDTO;
import site.bzyl.shortlink.project.dto.resp.ShortLinkPageRespDTO;

import java.util.List;

/**
 * 短链接业务层接口
 */
public interface ShortLinkService extends IService<LinkDO> {

    ShortLinkCreateRespDTO createShortLink(ShortLinkCreateReqDTO requestParam);

    IPage<ShortLinkPageRespDTO> pageShortLink(ShortLinkPageReqDTO requestParam);

    List<ShortLinkCountRespDTO> countShortLink(List<String> requestParam);
}
