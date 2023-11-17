package site.bzyl.shortlink.admin.remote.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import site.bzyl.shortlink.admin.common.convention.result.Result;
import site.bzyl.shortlink.admin.common.convention.result.Results;
import site.bzyl.shortlink.admin.remote.dto.RecycleBinSaveReqDTO;
import site.bzyl.shortlink.admin.remote.service.RecycleBinRemoteService;

/**
 * 短链接回收站远程调用接口
 */
@RestController
@RequiredArgsConstructor
public class RecycleBinController {
    RecycleBinRemoteService recycleBinRemoteService = new RecycleBinRemoteService(){};

    /**
     * 短链接移入回收站接口
     * @param requestParam 短链接加入回收站请求参数
     */
    @PostMapping("/api/short-link/admin/v1/recycle-bin/save")
    public Result<Void> addShortLinkToRecycleBin(@RequestBody RecycleBinSaveReqDTO requestParam) {
        recycleBinRemoteService.addShortLinkToRecycleBin(requestParam);
        return Results.success();
    }
}
