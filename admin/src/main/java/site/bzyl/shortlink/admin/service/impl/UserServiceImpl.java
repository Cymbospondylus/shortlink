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
import org.springframework.transaction.annotation.Transactional;
import site.bzyl.shortlink.admin.common.biz.user.UserContext;
import site.bzyl.shortlink.admin.common.biz.user.UserInfoDTO;
import site.bzyl.shortlink.admin.common.convention.exception.ClientException;
import site.bzyl.shortlink.admin.common.convention.exception.ServiceException;
import site.bzyl.shortlink.admin.common.enums.UserErrorCodeEnum;
import site.bzyl.shortlink.admin.dao.entity.UserDO;
import site.bzyl.shortlink.admin.dao.mapper.UserMapper;
import site.bzyl.shortlink.admin.dto.req.ShortLinkGroupSaveReqDTO;
import site.bzyl.shortlink.admin.dto.req.UserLoginReqDTO;
import site.bzyl.shortlink.admin.dto.req.UserRegisterReqDTO;
import site.bzyl.shortlink.admin.dto.req.UserUpdateReqDTO;
import site.bzyl.shortlink.admin.dto.resp.UserLoginRespDTO;
import site.bzyl.shortlink.admin.dto.resp.UserRespDTO;
import site.bzyl.shortlink.admin.service.ShortLinkGroupService;
import site.bzyl.shortlink.admin.service.UserService;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static site.bzyl.shortlink.admin.common.constants.RedisCacheConstant.LOCK_USER_REGISTER_USERNAME;
import static site.bzyl.shortlink.admin.common.constants.RedisCacheConstant.USER_LOGIN_PREFIX;
import static site.bzyl.shortlink.admin.common.constants.ShortLinkGroupConstant.DEFAULT_GROUP_NAME;
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
    public final ShortLinkGroupService shortLinkGroupService;

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
    @Transactional
    public String register(UserRegisterReqDTO requestParam) {
        if (hasUsername(requestParam.getUsername())) {
            ClientException.cast(UserErrorCodeEnum.USERNAME_EXIST);
        }
        RLock lock = redissonClient.getLock(LOCK_USER_REGISTER_USERNAME + requestParam.getUsername());
        String token = null;
        try {
            if (lock.tryLock()) {
                UserDO userDO = BeanUtil.toBean(requestParam, UserDO.class);
                int insert = baseMapper.insert(userDO);
                if (insert < 1) {
                    ClientException.cast(USER_SAVE_ERROR);
                }
                userRegisterCachePenetrationBloomFilter.add(requestParam.getUsername());
                // 注册后直接登录
                UserLoginReqDTO userLoginReqDTO = UserLoginReqDTO
                        .builder()
                        .username(userDO.getUsername())
                        .password(userDO.getPassword())
                        .build();
                token = login(userLoginReqDTO);
                // 保存到用户上下文
                UserInfoDTO userInfoDTO = UserInfoDTO
                        .builder()
                        .userId(userDO.getId())
                        .username(userDO.getUsername())
                        .realName(userDO.getRealName())
                        .token(token)
                        .build();
                UserContext.setUser(userInfoDTO);
                // 用户注册后创建一个默认分组
                ShortLinkGroupSaveReqDTO shortLinkGroupSaveReqDTO = ShortLinkGroupSaveReqDTO
                        .builder()
                        .name(DEFAULT_GROUP_NAME)
                        .build();
                shortLinkGroupService.saveShortLinkGroup(shortLinkGroupSaveReqDTO);
            } else {
                ClientException.cast(USER_EXIST);
            }
        } finally {
            lock.unlock();
        }
        return token;
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
    public String login(UserLoginReqDTO requestParam) {
        if (checkLogin(requestParam.getUsername())) {
            ClientException.cast("用户已登录");
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
        String redisKey = USER_LOGIN_PREFIX + requestParam.getUsername();

        stringRedisTemplate.opsForHash().put(
                redisKey,
                token,
                JSON.toJSONString(result)
        );
        // todo 开发完后要改回30分钟
        stringRedisTemplate.expire(redisKey, 30, TimeUnit.DAYS);
        return token;
    }

    @Override
    public Boolean checkLogin(String username) {
        return stringRedisTemplate.hasKey(USER_LOGIN_PREFIX + username);
    }

    @Override
    public void logout() {
        // todo 不太合理, 先当一个接口用来删除测试，因为每次重启服务ThreadLocal里的信息会丢失
        String key = USER_LOGIN_PREFIX + UserContext.getUsername();
        stringRedisTemplate.delete(key);
    }
}
