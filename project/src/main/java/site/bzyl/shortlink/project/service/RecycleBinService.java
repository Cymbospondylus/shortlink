package site.bzyl.shortlink.project.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import site.bzyl.shortlink.project.dao.entity.ShortLinkDO;
import site.bzyl.shortlink.project.dto.req.RecycleBinDeleteReqDTO;
import site.bzyl.shortlink.project.dto.req.RecycleBinPageReqDTO;
import site.bzyl.shortlink.project.dto.req.RecycleBinRestoreReqDTO;
import site.bzyl.shortlink.project.dto.req.RecycleBinSaveReqDTO;
import site.bzyl.shortlink.project.dto.resp.RecycleBinPageRespDTO;

/**
 * 短链接回收站业务层接口
 */
public interface RecycleBinService extends IService<ShortLinkDO> {
    void addShortLinkToRecycleBin(RecycleBinSaveReqDTO requestParam);

    IPage<RecycleBinPageRespDTO> pageRecycleBinLink(RecycleBinPageReqDTO requestParam);

    void restoreShortLinkFromRecycleBin(RecycleBinRestoreReqDTO requestParam);

    void deleteShortLinkInRecycleBin(RecycleBinDeleteReqDTO requestParam);
}
