package site.bzyl.shortlink.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import site.bzyl.shortlink.admin.dao.entity.GroupDO;
import site.bzyl.shortlink.admin.dto.req.GroupSaveReqDTO;

/**
 * 短链接分组业务接口
 */
public interface ShortLinkGroupService extends IService<GroupDO> {
    void saveShortLinkGroup(GroupSaveReqDTO requestParam);
}
