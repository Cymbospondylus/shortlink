package site.bzyl.shortlink.admin.remote.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import site.bzyl.shortlink.admin.common.biz.user.UserContext;
import site.bzyl.shortlink.admin.common.convention.exception.ClientException;
import site.bzyl.shortlink.admin.common.convention.result.Result;
import site.bzyl.shortlink.admin.dao.entity.GroupDO;
import site.bzyl.shortlink.admin.dao.mapper.ShortLinkGroupMapper;
import site.bzyl.shortlink.admin.remote.dto.req.RecycleBinPageReqDTO;
import site.bzyl.shortlink.admin.remote.dto.resp.RecycleBinPageRespDTO;
import site.bzyl.shortlink.admin.remote.service.RecycleBinRemoteService;
import site.bzyl.shortlink.admin.remote.service.ShortLinkRecycleBinService;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 短链接回收站业务层实现类
 */
@Service
@RequiredArgsConstructor
public class ShortLinkRecycleBinServiceImpl implements ShortLinkRecycleBinService {

    private final ShortLinkGroupMapper shortLinkGroupMapper;

    RecycleBinRemoteService recycleBinRemoteService = new RecycleBinRemoteService(){};

    @Override
    public Result<IPage<RecycleBinPageRespDTO>> pageRecycleBinLink(RecycleBinPageReqDTO requestParam) {
        LambdaQueryWrapper<GroupDO> queryWrapper = Wrappers.lambdaQuery(GroupDO.class)
                .eq(GroupDO::getUsername, UserContext.getUsername());

        List<GroupDO> groupDOList = shortLinkGroupMapper.selectList(queryWrapper);
        if (CollUtil.isEmpty(groupDOList)) {
            ClientException.cast("用户分组为空");
        }
        requestParam.setGidList(groupDOList.stream().map(GroupDO::getGid).collect(Collectors.toList()));
        return recycleBinRemoteService.pageRecycleBinLink(requestParam);
    }
}
