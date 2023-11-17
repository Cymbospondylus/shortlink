package site.bzyl.shortlink.admin.remote.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import site.bzyl.shortlink.admin.common.convention.result.Result;
import site.bzyl.shortlink.admin.remote.dto.req.RecycleBinPageReqDTO;
import site.bzyl.shortlink.admin.remote.dto.resp.RecycleBinPageRespDTO;

/**
 * 短链接回收站业务层接口
 */
public interface ShortLinkRecycleBinService {
    Result<IPage<RecycleBinPageRespDTO>> pageRecycleBinLink(RecycleBinPageReqDTO requestParam);

}
