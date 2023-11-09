package site.bzyl.shortlink.admin.controller;


import cn.hutool.core.bean.BeanUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import site.bzyl.shortlink.admin.common.convention.result.Result;
import site.bzyl.shortlink.admin.common.convention.result.Results;
import site.bzyl.shortlink.admin.dto.req.UserLoginReqDTO;
import site.bzyl.shortlink.admin.dto.req.UserRegisterReqDTO;
import site.bzyl.shortlink.admin.dto.req.UserUpdateReqDTO;
import site.bzyl.shortlink.admin.dto.resp.UserActualRespDTO;
import site.bzyl.shortlink.admin.dto.resp.UserLoginRespDTO;
import site.bzyl.shortlink.admin.dto.resp.UserRespDTO;
import site.bzyl.shortlink.admin.service.UserService;

@RestController
@RequiredArgsConstructor
public class UserController {
    public final UserService userService;

    /**
     * 根据用户名查询用户信息
     */
    @GetMapping("/api/short-link/v1/user/{username}")
    public Result<UserRespDTO> getUserByUsername(@PathVariable("username") String username) {
        return Results. success(userService.getUserByUsername(username));
    }

    /**
     * 根据用户名查询用户未脱敏信息
     */
    @GetMapping("/api/short-link/v1/user/actual/{username}")
    public Result<UserActualRespDTO> getActualUserByUsername(@PathVariable("username") String username) {
        return Results.success(BeanUtil.toBean(userService.getUserByUsername(username), UserActualRespDTO.class));
    }

    /**
     * 查询用户名是否存在
     * @return True表示不存在, False表示存在
     */
    @GetMapping("/api/short-link/v1/user/has-username")
    public Result<Boolean> hasUsername(@RequestParam("username") String username) {
        return Results.success(userService.hasUsername(username));
    }

    /**
     * 用户注册
     */
    @PostMapping("/api/short-link/v1/user/register")
    public Result<Void> register(@RequestBody UserRegisterReqDTO requestParam) {
        userService.register(requestParam);
        return Results.success();
    }

    /**
     * 根据用户名修改用户信息
     */
    @PutMapping("/api/short-link/v1/user")
    public Result<Void> update(@RequestBody UserUpdateReqDTO requestParam) {
        userService.updateUserByUsername(requestParam);
        return Results.success();
    }

    /**
     * 用户登录
     * @return 用户 token
     */
    @PostMapping("/api/short-link/v1/user/login")
    public Result<UserLoginRespDTO> login(@RequestBody UserLoginReqDTO requestParam) {
        return Results.success(userService.login(requestParam));
    }

    /**
     * 查询用户是否登录
     * @param username
     * @return True:已登录, False:未登录
     */
    @GetMapping("/api/short-link/v1/user/check-login")
    public Result<Boolean> checkLogin(@RequestParam("username") String username) {
        return Results.success(userService.checkLogin(username));
    }

    @DeleteMapping("/api/short-link/v1/user/logout")
    public Result<Void> logout(@RequestParam("username") String username) {
        userService.logout(username);
        return Results.success();
    }

}
