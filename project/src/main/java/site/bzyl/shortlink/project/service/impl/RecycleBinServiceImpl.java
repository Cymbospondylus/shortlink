package site.bzyl.shortlink.project.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import site.bzyl.shortlink.project.common.convention.exception.ClientException;
import site.bzyl.shortlink.project.dao.entity.ShortLinkDO;
import site.bzyl.shortlink.project.dao.mapper.ShortLinkMapper;
import site.bzyl.shortlink.project.dto.req.RecycleBinPageReqDTO;
import site.bzyl.shortlink.project.dto.req.RecycleBinSaveReqDTO;
import site.bzyl.shortlink.project.dto.resp.RecycleBinPageRespDTO;
import site.bzyl.shortlink.project.service.RecycleBinService;

import static site.bzyl.shortlink.project.common.constants.RedisKeyConstant.SHORT_LINK_REDIRECT_KEY;
import static site.bzyl.shortlink.project.common.constants.ShortLinkConstant.ENABLE_STATUS_RECYCLE_BIN;

/**
 * 短链接回收站业务实现类
 */
@Service
@RequiredArgsConstructor
public class RecycleBinServiceImpl extends ServiceImpl<ShortLinkMapper, ShortLinkDO> implements RecycleBinService {
    private final StringRedisTemplate stringRedisTemplate;
    @Override
    public void addShortLinkToRecycleBin(RecycleBinSaveReqDTO requestParam) {
        LambdaUpdateWrapper<ShortLinkDO> updateWrapper = Wrappers.lambdaUpdate(ShortLinkDO.class)
                .eq(ShortLinkDO::getGid, requestParam.getGid())
                .eq(ShortLinkDO::getFullShortUri, requestParam.getFullShortUri());

        ShortLinkDO shortLinkDO = ShortLinkDO
                .builder()
                .enableStatus(ENABLE_STATUS_RECYCLE_BIN)
                .build();

        int update = baseMapper.update(shortLinkDO, updateWrapper);
        if (update < 1) {
            ClientException.cast("短链接移入回收站失败");
        }
        stringRedisTemplate.delete(String.format(SHORT_LINK_REDIRECT_KEY, requestParam.getFullShortUri()));
    }

    @Override
    public IPage<RecycleBinPageRespDTO> pageRecycleBinLink(RecycleBinPageReqDTO requestParam) {
        LambdaQueryWrapper<ShortLinkDO> queryWrapper = Wrappers.lambdaQuery(ShortLinkDO.class)
                .eq(ShortLinkDO::getGid, requestParam.getGid())
                .eq(ShortLinkDO::getEnableStatus, ENABLE_STATUS_RECYCLE_BIN)
                .orderByDesc(ShortLinkDO::getCreateTime);
        IPage<ShortLinkDO> resultPage = baseMapper.selectPage(requestParam, queryWrapper);
        return resultPage.convert(each -> BeanUtil.toBean(each, RecycleBinPageRespDTO.class));
    }
}
