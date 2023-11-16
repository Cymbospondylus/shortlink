package site.bzyl.shortlink.project.service;

import com.baomidou.mybatisplus.extension.service.IService;
import site.bzyl.shortlink.project.dao.entity.ShortLinkDO;
import site.bzyl.shortlink.project.dto.req.RecycleBinSaveReqDTO;

/**
 * 短链接回收站业务层接口
 */
public interface RecycleBinService extends IService<ShortLinkDO> {
    void addShortLinkToRecycleBin(RecycleBinSaveReqDTO requestParam);

}
