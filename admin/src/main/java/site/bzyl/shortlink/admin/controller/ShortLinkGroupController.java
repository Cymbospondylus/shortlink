package site.bzyl.shortlink.admin.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import site.bzyl.shortlink.admin.common.convention.result.Result;
import site.bzyl.shortlink.admin.common.convention.result.Results;
import site.bzyl.shortlink.admin.dto.req.ShortLinkGroupSaveReqDTO;
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

    @GetMapping("/api/short-link/v1/group")
    public Result<List<ShortLinkGroupRespDTO>> listShortLinkGroup() {
        return Results.success(shortLinkGroupService.listShortLinkGroup());
    }
}
