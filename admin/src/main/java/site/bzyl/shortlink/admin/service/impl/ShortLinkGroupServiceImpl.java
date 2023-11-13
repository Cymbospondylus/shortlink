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
import site.bzyl.shortlink.admin.dto.req.ShortLinkGroupSaveReqDTO;
import site.bzyl.shortlink.admin.dto.req.ShortLinkGroupSortReqDTO;
import site.bzyl.shortlink.admin.dto.req.ShortLinkGroupUpdateReqDTO;
import site.bzyl.shortlink.admin.dto.resp.ShortLinkGroupRespDTO;
import site.bzyl.shortlink.admin.remote.dto.ShortLinkCountRespDTO;
import site.bzyl.shortlink.admin.remote.service.ShortLinkRemoteService;
import site.bzyl.shortlink.admin.service.ShortLinkGroupService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static site.bzyl.shortlink.admin.common.constants.ShortLinkGroupConstant.DEFAULT_SORT_ORDER;

/**
 * 短链接分组业务实现类
 */
@Service
@Slf4j
public class ShortLinkGroupServiceImpl extends ServiceImpl<ShortLinkGroupMapper, GroupDO> implements ShortLinkGroupService {
    // todo  后续重构为Spring Feign 远程调用
    ShortLinkRemoteService shortLinkRemoteService = new ShortLinkRemoteService(){};

    @Override
    public void saveShortLinkGroup(ShortLinkGroupSaveReqDTO requestParam) {
        String gid;
        GroupDO groupDO;
        do  {
            // 生成6位字符+数字的gid, 每一位36种可能, 共2176782336(21亿)
            gid = RandomUtil.randomString(6);
            LambdaQueryWrapper<GroupDO> queryWrapper = Wrappers.lambdaQuery(GroupDO.class)
                    // 使用username做分片键, 查询时要带上用户名, 同时保证了一个用户的分组只会在一个表里
                    .eq(GroupDO::getUsername, UserContext.getUsername())
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
        List<ShortLinkGroupRespDTO> shortLinkGroupRespDTOList =
                BeanUtil.copyToList(groupDOList, ShortLinkGroupRespDTO.class);
        // 存入 map 中在查找到短链接个数后直接 set 值
        Map<String, ShortLinkGroupRespDTO> map = new HashMap<>();
        shortLinkGroupRespDTOList.forEach(each -> map.put(each.getGid(), each));

        List<String> gidList = shortLinkGroupRespDTOList.stream()
                .map(each -> each.getGid())
                .collect(Collectors.toList());
        // 传 gidList 的好处是只用一次远程调用
        List<ShortLinkCountRespDTO> countShortLinkList =
                shortLinkRemoteService.countShortLink(gidList).getData();
        // todo 17:09有时间换空间的写法
        countShortLinkList.forEach(each -> {
            ShortLinkGroupRespDTO shortLinkGroupRespDTO = map.get(each.getGid());
            shortLinkGroupRespDTO.setShortLinkCount(each.getShortLinkCount());
        });

        return shortLinkGroupRespDTOList;
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
    public void deleteGroupById(String gid) {

        LambdaQueryWrapper<GroupDO> queryWrapper = Wrappers.lambdaQuery(GroupDO.class)
                .eq(GroupDO::getGid, gid);
        int delete = baseMapper.delete(queryWrapper);
        if (delete < 1) {
            log.error("短链接分组删除失败, 用户名:{}, 分组gid:{}", UserContext.getUsername(), gid);
            ServiceException.cast("短链接分组删除失败");
        }
    }

    @Override
    public void sortShortLinkGroup(List<ShortLinkGroupSortReqDTO> requestParam) {
        requestParam.forEach(each -> {
            GroupDO groupDO = GroupDO.builder().sortOrder(each.getSortOrder()).build();
            LambdaUpdateWrapper<GroupDO> updateWrapper = Wrappers.lambdaUpdate(GroupDO.class)
                    .eq(GroupDO::getUsername, UserContext.getUsername())
                    .eq(GroupDO::getGid, each.getGid());
            int update = baseMapper.update(groupDO, updateWrapper);
            if (update < 1) {
                log.error("分组排序值修改失败, 分组gid:{}, 用户id:{}", each.getGid(),UserContext.getUsername());
                ServiceException.cast("分组排序值修改失败");
            }
        });
    }
}
