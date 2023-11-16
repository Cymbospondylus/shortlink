package site.bzyl.shortlink.project.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import site.bzyl.shortlink.project.dao.entity.ShortLinkDO;
import site.bzyl.shortlink.project.dao.mapper.ShortLinkMapper;
import site.bzyl.shortlink.project.dto.req.RecycleBinSaveReqDTO;
import site.bzyl.shortlink.project.service.RecycleBinService;

import static site.bzyl.shortlink.project.common.constants.ShortLinkConstant.ENABLE_STATUS_RECYCLE_BIN;

/**
 * 短链接回收站业务实现类
 */
@Service
public class RecycleBinServiceImpl extends ServiceImpl<ShortLinkMapper, ShortLinkDO> implements RecycleBinService {

    @Override
    public void addShortLinkToRecycleBin(RecycleBinSaveReqDTO requestParam) {
        LambdaUpdateWrapper<ShortLinkDO> updateWrapper = Wrappers.lambdaUpdate(ShortLinkDO.class)
                .eq(ShortLinkDO::getGid, requestParam.getGid())
                .eq(ShortLinkDO::getFullShortUri, requestParam.getFullShortUri());

        ShortLinkDO shortLinkDO = ShortLinkDO
                .builder()
                .enableStatus(ENABLE_STATUS_RECYCLE_BIN)
                .build();

        baseMapper.update(shortLinkDO, updateWrapper);
    }
}
