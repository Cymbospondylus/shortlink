package site.bzyl.shortlink.admin.controller;


import cn.hutool.core.bean.BeanUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import site.bzyl.shortlink.admin.common.convention.result.Result;
import site.bzyl.shortlink.admin.common.convention.result.Results;
import site.bzyl.shortlink.admin.dto.req.UserUpdateReqDTO;
import site.bzyl.shortlink.admin.dto.resp.UserActualRespDTO;
import site.bzyl.shortlink.admin.dto.req.UserRegisterReqDTO;
import site.bzyl.shortlink.admin.dto.resp.UserRespDTO;
import site.bzyl.shortlink.admin.service.UserService;

@RestController
@RequiredArgsConstructor
public class UserController {
    public final UserService userService;

    /**
     * 根据用户名查用户信息
     */
    @GetMapping("/api/short-link/v1/user/{username}")
    public Result<UserRespDTO> getUserByUsername(@PathVariable("username") String username) {
        return Results. success(userService.getUserByUsername(username));
    }

    /**
     * 根据用户名查用户未脱敏信息
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
     * @param requestParam 用户注册请求参数
     */
    @PostMapping("/api/short-link/v1/user/register")
    public Result<Void> register(@RequestBody UserRegisterReqDTO requestParam) {
        userService.register(requestParam);
        return Results.success();
    }

    @PutMapping("/api/short-link/v1/user")
    public Result<Void> update(@RequestBody UserUpdateReqDTO requestParam) {
        userService.updateUserByUsername(requestParam);
        return Results.success();
    }
}
