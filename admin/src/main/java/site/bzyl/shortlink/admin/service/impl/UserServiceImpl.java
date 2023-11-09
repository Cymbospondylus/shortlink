package site.bzyl.shortlink.admin.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.IdUtil;
import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import site.bzyl.shortlink.admin.common.convention.exception.ClientException;
import site.bzyl.shortlink.admin.common.convention.exception.ServiceException;
import site.bzyl.shortlink.admin.common.enums.UserErrorCodeEnum;
import site.bzyl.shortlink.admin.dao.entity.UserDO;
import site.bzyl.shortlink.admin.dao.mapper.UserMapper;
import site.bzyl.shortlink.admin.dto.req.UserLoginReqDTO;
import site.bzyl.shortlink.admin.dto.req.UserRegisterReqDTO;
import site.bzyl.shortlink.admin.dto.req.UserUpdateReqDTO;
import site.bzyl.shortlink.admin.dto.resp.UserLoginRespDTO;
import site.bzyl.shortlink.admin.dto.resp.UserRespDTO;
import site.bzyl.shortlink.admin.service.UserService;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static site.bzyl.shortlink.admin.common.constants.RedisCacheConstant.LOCK_USER_REGISTER_USERNAME;
import static site.bzyl.shortlink.admin.common.constants.RedisCacheConstant.USER_LOGIN_PREFIX;
import static site.bzyl.shortlink.admin.common.enums.UserErrorCodeEnum.*;

/**
 * 用户业务实现
 */
@Service
@RequiredArgsConstructor
public class UserServiceImpl extends ServiceImpl<UserMapper, UserDO> implements UserService {
    public final RBloomFilter<String> userRegisterCachePenetrationBloomFilter;
    public final RedissonClient redissonClient;
    public final StringRedisTemplate stringRedisTemplate;

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
    public void register(UserRegisterReqDTO requestParam) {
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
            } else {
                ClientException.cast(USER_EXIST);
            }
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void updateUserByUsername(UserUpdateReqDTO requestParam) {
        // todo 验证当前用户是否为登录用户
        UserDO userDO = BeanUtil.toBean(requestParam, UserDO.class);
        LambdaUpdateWrapper<UserDO> updateWrapper = Wrappers.lambdaUpdate(UserDO.class)
                .eq(UserDO::getUsername, requestParam.getUsername());
        int update = baseMapper.update(userDO, updateWrapper);
        if (update < 1) {
            ServiceException.cast(USER_UPDATE_ERROR);
        }
    }

    @Override
    public UserLoginRespDTO login(UserLoginReqDTO requestParam) {
        if (checkLogin(requestParam.getUsername())) {
            String token = Optional.ofNullable(requestParam.getToken())
                    .orElseThrow(() -> new ClientException(USER_TOKEN_INVALID));
            Object userLoginInfo = Optional
                    .ofNullable(stringRedisTemplate.opsForHash().get(USER_LOGIN_PREFIX + requestParam.getUsername(), token))
                    .orElseThrow(() -> new ClientException(USER_TOKEN_INVALID));
            UserLoginRespDTO userLoginRespDTO = Optional
                    .ofNullable(JSON.parseObject(userLoginInfo.toString(), UserLoginRespDTO.class))
                    .orElseThrow(() -> new ServiceException(USER_CACHE_NULL));
            return userLoginRespDTO;
        }

        if (!userRegisterCachePenetrationBloomFilter.contains(requestParam.getUsername())) {
            ClientException.cast(USER_NULL);
        }

        LambdaQueryWrapper<UserDO> queryWrapper = Wrappers.lambdaQuery(UserDO.class)
                .eq(UserDO::getUsername, requestParam.getUsername())
                .eq(UserDO::getPassword, requestParam.getPassword());
        UserDO userDO = Optional.ofNullable(baseMapper.selectOne(queryWrapper)).
                orElseThrow(() -> new ClientException(USER_NULL));
        UserLoginRespDTO result = BeanUtil.toBean(userDO, UserLoginRespDTO.class);
        String token = IdUtil.randomUUID();
        result.setToken(token);
        String redisKey = USER_LOGIN_PREFIX + requestParam.getUsername();

        stringRedisTemplate.opsForHash().put(
                redisKey,
                token,
                JSON.toJSONString(result)
        );
        stringRedisTemplate.expire(redisKey, 30, TimeUnit.MINUTES);
        return result;
    }

    @Override
    public Boolean checkLogin(String username) {
        return stringRedisTemplate.hasKey(USER_LOGIN_PREFIX + username);
    }

    @Override
    public void logout(String username) {
        // todo 判断是否是当前登录用户的用户名
        stringRedisTemplate.delete(USER_LOGIN_PREFIX + username);
    }
}
