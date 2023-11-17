package site.bzyl.shortlink.admin.remote.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import site.bzyl.shortlink.admin.common.convention.result.Result;
import site.bzyl.shortlink.admin.remote.dto.req.ShortLinkPageReqDTO;
import site.bzyl.shortlink.admin.remote.dto.resp.ShortLinkPageRespDTO;

/**
 * 短链接中台远程调用业务层接口
 */
public interface ShortLinkService {
    Result<IPage<ShortLinkPageRespDTO>> pageShortLink(ShortLinkPageReqDTO requestParam);
}
