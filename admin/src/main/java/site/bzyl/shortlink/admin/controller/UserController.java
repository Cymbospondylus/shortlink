package site.bzyl.shortlink.admin.controller;


import cn.hutool.core.bean.BeanUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import site.bzyl.shortlink.admin.common.convention.result.Result;
import site.bzyl.shortlink.admin.common.convention.result.Results;
import site.bzyl.shortlink.admin.dto.resp.UserActualRespDTO;
import site.bzyl.shortlink.admin.dto.resp.UserRespDTO;
import site.bzyl.shortlink.admin.service.UserService;

@RestController
@RequiredArgsConstructor
public class UserController {
    public final UserService userService;

    /**
     * 根据用户名查用户信息
     */
    @GetMapping("/api/shortlink/user/{username}")
    public Result<UserRespDTO> getUserByUsername(@PathVariable("username") String username) {
        return Results.success(userService.getUserByUsername(username));
    }

    /**
     * 根据用户名查用户未脱敏信息
     */
    @GetMapping("/api/shortlink/user/actual/{username}")
    public Result<UserActualRespDTO> getActualUserByUsername(@PathVariable("username") String username) {
        return Results.success(BeanUtil.toBean(userService.getUserByUsername(username), UserActualRespDTO.class));
    }
}
