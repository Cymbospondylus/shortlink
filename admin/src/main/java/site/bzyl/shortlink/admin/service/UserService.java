package site.bzyl.shortlink.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.stereotype.Service;
import site.bzyl.shortlink.admin.dao.entity.UserDO;
import site.bzyl.shortlink.admin.dto.req.UserLoginReqDTO;
import site.bzyl.shortlink.admin.dto.req.UserRegisterReqDTO;
import site.bzyl.shortlink.admin.dto.req.UserUpdateReqDTO;
import site.bzyl.shortlink.admin.dto.resp.UserLoginRespDTO;
import site.bzyl.shortlink.admin.dto.resp.UserRespDTO;

/**
 * 用户业务接口
 */
@Service
public interface UserService extends IService<UserDO> {
    /**
     * 根据用户名查询用户信息
     * @return 用户信息返回响应
     */
    UserRespDTO getUserByUsername(String username);

    /**
     * 根据用户名查询用户是否存在
     * @return True:存在 False:不存在
     */
    Boolean hasUsername(String username);

    /**
     * 用户注册
     * @param requestParam 用户注册请求参数
     */
    void register(UserRegisterReqDTO requestParam);

    /**
     * 根据用户名修改用户信息
     * @param requestParam  用户修改请求参数
     */
    void updateUserByUsername(UserUpdateReqDTO requestParam);

    /**
     * 根据用户名密码登录
     * @param requestParam 用户登录请求参数
     * @return 用户登录token
     */
    UserLoginRespDTO login(UserLoginReqDTO requestParam);

    /**
     * 检查用户是否登录
     * @param username 用户名
     * @return
     */
    Boolean checkLogin(String username);

    /**
     * 用户登出
     * @param username 用户名
     */
    void logout(String username);
}
