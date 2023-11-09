package site.bzyl.shortlink.admin.service.impl;

import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import site.bzyl.shortlink.admin.common.convention.exception.ServiceException;
import site.bzyl.shortlink.admin.dao.entity.GroupDO;
import site.bzyl.shortlink.admin.dao.mapper.ShortLinkGroupMapper;
import site.bzyl.shortlink.admin.dto.req.GroupSaveReqDTO;
import site.bzyl.shortlink.admin.service.ShortLinkGroupService;

/**
 * 短链接分组业务实现类
 */
@Service
public class ShortLinkGroupServiceImpl extends ServiceImpl<ShortLinkGroupMapper, GroupDO> implements ShortLinkGroupService {

    @Override
    public void saveShortLinkGroup(GroupSaveReqDTO requestParam) {
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
                .username(requestParam.getUsername())
                .name(requestParam.getName())
                .build();

        int insert = baseMapper.insert(shortLinkGroupDO);
        if (insert < 1) {
            ServiceException.cast("短链接新增失败");
        }

    }
}
