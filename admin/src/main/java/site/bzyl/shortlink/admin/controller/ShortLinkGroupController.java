package site.bzyl.shortlink.admin.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import site.bzyl.shortlink.admin.common.convention.result.Result;
import site.bzyl.shortlink.admin.common.convention.result.Results;
import site.bzyl.shortlink.admin.dto.req.ShortLinkGroupDeleteReqDTO;
import site.bzyl.shortlink.admin.dto.req.ShortLinkGroupSaveReqDTO;
import site.bzyl.shortlink.admin.dto.req.ShortLinkGroupUpdateReqDTO;
import site.bzyl.shortlink.admin.dto.resp.ShortLinkGroupRespDTO;
import site.bzyl.shortlink.admin.service.ShortLinkGroupService;

import java.util.List;


/**
 * 短链接分组接口
 */
@RestController
@RequiredArgsConstructor
public class ShortLinkGroupController {
    private final ShortLinkGroupService shortLinkGroupService;


    /**
     * 短链接分组新增接口
     * @param requestParam 短链接分组新增请求参数
     */
    @PostMapping("/api/short-link/v1/group")
    public Result<Void> saveShortLinkGroup(@RequestBody ShortLinkGroupSaveReqDTO requestParam) {
        shortLinkGroupService.saveShortLinkGroup(requestParam);
        return Results.success();
    }

    /**
     * 短链接分组查询接口
     * @return 当前登录用户的短链接分组信息
     */
    @GetMapping("/api/short-link/v1/group")
    public Result<List<ShortLinkGroupRespDTO>> listShortLinkGroup() {
        return Results.success(shortLinkGroupService.listShortLinkGroup());
    }

    /**
     * 短链接分组修改接口
     * @param requestParam 短链接分组修改请求参数
     * @return
     */
    @PutMapping("/api/short-link/v1/group")
    public Result<Void> updateShortLinkGroup(@RequestBody ShortLinkGroupUpdateReqDTO requestParam) {
        shortLinkGroupService.updateShortLinkGroup(requestParam);
        return Results.success();
    }

    /**
     * 短链接分组删除接口
     * @param requestParam 短链接分组删除请求参数
     * @return
     */
    @DeleteMapping("/api/short-link/v1/group")
    public Result<Void> deleteShortLinkGroup(@RequestBody ShortLinkGroupDeleteReqDTO requestParam) {
        shortLinkGroupService.deleteGroupById(requestParam);
        return Results.success();
    }
}
