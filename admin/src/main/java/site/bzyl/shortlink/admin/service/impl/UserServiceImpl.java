package site.bzyl.shortlink.admin.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;
import site.bzyl.shortlink.admin.common.convention.exception.ClientException;
import site.bzyl.shortlink.admin.common.enums.UserErrorCodeEnum;
import site.bzyl.shortlink.admin.dao.entity.UserDO;
import site.bzyl.shortlink.admin.dao.mapper.UserMapper;
import site.bzyl.shortlink.admin.dto.resp.UserReqDTO;
import site.bzyl.shortlink.admin.dto.resp.UserRespDTO;
import site.bzyl.shortlink.admin.service.UserService;

import static site.bzyl.shortlink.admin.common.constants.RedisCacheConstant.LOCK_USER_REGISTER_USERNAME;
import static site.bzyl.shortlink.admin.common.enums.UserErrorCodeEnum.USER_EXIST;
import static site.bzyl.shortlink.admin.common.enums.UserErrorCodeEnum.USER_SAVE_ERROR;

/**
 * 用户业务实现
 */
@Service
@RequiredArgsConstructor
public class UserServiceImpl extends ServiceImpl<UserMapper, UserDO> implements UserService {
    public final RBloomFilter<String> userRegisterCachePenetrationBloomFilter;
    public final RedissonClient redissonClient;
    @Override
    public UserRespDTO getUserByUsername(String username) {
        LambdaQueryWrapper<UserDO> queryWrapper = Wrappers.lambdaQuery(UserDO.class)
                .eq(UserDO::getUsername, username);
        UserDO userDO = baseMapper.selectOne(queryWrapper);
        if (userDO == null) {
            ClientException.cast(UserErrorCodeEnum.USER_NULL);
        }

        UserRespDTO userRespDTO = new UserRespDTO();

        BeanUtil.copyProperties(userDO, userRespDTO);
        return userRespDTO;
    }

    @Override
    public Boolean hasUsername(String username) {
        return userRegisterCachePenetrationBloomFilter.contains(username);
    }

    @Override
    public void register(UserReqDTO requestParam) {
        if (hasUsername(requestParam.getUsername())) {
            ClientException.cast(UserErrorCodeEnum.USERNAME_EXIST);
        }
        RLock lock = redissonClient.getLock(LOCK_USER_REGISTER_USERNAME + requestParam.getUsername());
        try {
            if (lock.tryLock()) {
                int insert = baseMapper.insert(BeanUtil.toBean(requestParam, UserDO.class));
                if (insert < 1) {
                    ClientException.cast(USER_SAVE_ERROR);
                }
                userRegisterCachePenetrationBloomFilter.add(requestParam.getUsername());
            }
            else {
                ClientException.cast(USER_EXIST);
            }
        } finally {
            lock.unlock();
        }
    }
}
