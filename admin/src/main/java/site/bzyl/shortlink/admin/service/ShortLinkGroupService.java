package site.bzyl.shortlink.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import site.bzyl.shortlink.admin.dao.entity.GroupDO;
import site.bzyl.shortlink.admin.dto.req.ShortLinkGroupDeleteReqDTO;
import site.bzyl.shortlink.admin.dto.req.ShortLinkGroupSaveReqDTO;
import site.bzyl.shortlink.admin.dto.req.ShortLinkGroupUpdateReqDTO;
import site.bzyl.shortlink.admin.dto.resp.ShortLinkGroupRespDTO;

import java.util.List;

/**
 * 短链接分组业务接口
 */
public interface ShortLinkGroupService extends IService<GroupDO> {

    /**
     * 短链接分组新增接口
     * @param requestParam 短链接分组新增请求参数
     */
    void saveShortLinkGroup(ShortLinkGroupSaveReqDTO requestParam);

    List<ShortLinkGroupRespDTO> listShortLinkGroup();

    void updateShortLinkGroup(ShortLinkGroupUpdateReqDTO requestParam);

    void deleteGroupById(ShortLinkGroupDeleteReqDTO requestParam);
}
