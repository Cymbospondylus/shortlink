package site.bzyl.shortlink.project.service;

import com.baomidou.mybatisplus.extension.service.IService;
import site.bzyl.shortlink.project.dao.entity.LinkDO;
import site.bzyl.shortlink.project.dto.req.ShortLinkCreateReqDTO;
import site.bzyl.shortlink.project.dto.resp.ShortLinkCreateRespDTO;

/**
 * 短链接业务层接口
 */
public interface ShortLinkService extends IService<LinkDO> {

    ShortLinkCreateRespDTO createShortLink(ShortLinkCreateReqDTO requestParam);
}
