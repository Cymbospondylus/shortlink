package site.bzyl.shortlink.admin.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import site.bzyl.shortlink.admin.common.biz.user.UserContext;
import site.bzyl.shortlink.admin.common.convention.exception.ClientException;
import site.bzyl.shortlink.admin.common.convention.exception.ServiceException;
import site.bzyl.shortlink.admin.dao.entity.GroupDO;
import site.bzyl.shortlink.admin.dao.mapper.ShortLinkGroupMapper;
import site.bzyl.shortlink.admin.dto.req.ShortLinkGroupDeleteReqDTO;
import site.bzyl.shortlink.admin.dto.req.ShortLinkGroupSaveReqDTO;
import site.bzyl.shortlink.admin.dto.req.ShortLinkGroupUpdateReqDTO;
import site.bzyl.shortlink.admin.dto.resp.ShortLinkGroupRespDTO;
import site.bzyl.shortlink.admin.service.ShortLinkGroupService;

import java.util.List;

import static site.bzyl.shortlink.admin.common.constants.ShortLinkGroupConstant.DEFAULT_SORT_ORDER;

/**
 * 短链接分组业务实现类
 */
@Service
@Slf4j
public class ShortLinkGroupServiceImpl extends ServiceImpl<ShortLinkGroupMapper, GroupDO> implements ShortLinkGroupService {

    @Override
    public void saveShortLinkGroup(ShortLinkGroupSaveReqDTO requestParam) {
        String gid;
        GroupDO groupDO;
        do  {
            // 生成6位字符+数字的gid, 每一位36种可能, 共2176782336(21亿)
            gid = RandomUtil.randomString(6);
            LambdaQueryWrapper<GroupDO> queryWrapper = Wrappers.lambdaQuery(GroupDO.class)
                    .eq(GroupDO::getGid, gid);
            groupDO = baseMapper.selectOne(queryWrapper);
            // 直到找到一个数据库中不存在的分组gid
        } while (groupDO != null);

        GroupDO shortLinkGroupDO = GroupDO.builder()
                .gid(gid)
                .username(UserContext.getUsername())
                .name(requestParam.getName())
                .sortOrder(DEFAULT_SORT_ORDER)
                .build();

        int insert = baseMapper.insert(shortLinkGroupDO);
        if (insert < 1) {
            ServiceException.cast("短链接分组新增失败");
        }

    }

    @Override
    public List<ShortLinkGroupRespDTO> listShortLinkGroup() {
        LambdaQueryWrapper<GroupDO> queryWrapper = Wrappers.lambdaQuery(GroupDO.class)
                .eq(GroupDO::getUsername, UserContext.getUsername())
                .orderByDesc(GroupDO::getSortOrder, GroupDO::getUpdateTime);
        List<GroupDO> groupDOList = baseMapper.selectList(queryWrapper);

        return BeanUtil.copyToList(groupDOList, ShortLinkGroupRespDTO.class);
    }

    @Override
    public void updateShortLinkGroup(ShortLinkGroupUpdateReqDTO requestParam) {
        if (!requestParam.getUsername().equals(UserContext.getUsername())) {
            ClientException.cast("分组不属于当前登录用户");
        }
        LambdaUpdateWrapper<GroupDO> updateWrapper = Wrappers.lambdaUpdate(GroupDO.class)
                .eq(GroupDO::getGid, requestParam.getGid())
                .eq(GroupDO::getUsername, requestParam.getUsername());
        GroupDO groupDO = GroupDO.builder()
                .name(requestParam.getName())
                .sortOrder(requestParam.getSortOrder())
                .build();
        int update = baseMapper.update(groupDO, updateWrapper);
        if (update < 1) {
            ServiceException.cast("分组更新失败");
        }
    }

    @Override
    public void deleteGroupById(ShortLinkGroupDeleteReqDTO requestParam) {
        if(!requestParam.getUsername().equals(UserContext.getUsername())) {
            ClientException.cast("只允许修改当前登录用户的分组");
        }
        LambdaQueryWrapper<GroupDO> queryWrapper = Wrappers.lambdaQuery(GroupDO.class)
                .eq(GroupDO::getGid, requestParam.getGid())
                .eq(GroupDO::getUsername, requestParam.getUsername());
        int delete = baseMapper.delete(queryWrapper);
        if (delete < 1) {
            log.error("短链接分组删除失败, 用户名:{}, 分组gid:{}", requestParam.getUsername(), requestParam.getGid());
            ServiceException.cast("短链接分组删除失败");
        }
    }
}
