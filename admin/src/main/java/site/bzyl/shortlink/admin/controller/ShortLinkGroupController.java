package site.bzyl.shortlink.admin.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import site.bzyl.shortlink.admin.common.convention.result.Result;
import site.bzyl.shortlink.admin.common.convention.result.Results;
import site.bzyl.shortlink.admin.dto.req.GroupSaveReqDTO;
import site.bzyl.shortlink.admin.service.ShortLinkGroupService;


/**
 * 短链接分组接口
 */
@RestController
@RequiredArgsConstructor
public class ShortLinkGroupController {
    private final ShortLinkGroupService shortLinkGroupService;


    @PostMapping("/api/short-link/v1/group")
    public Result<Void> saveShortLinkGroup(@RequestBody GroupSaveReqDTO requestParam) {
        shortLinkGroupService.saveShortLinkGroup(requestParam);
        return Results.success();
    }
}
