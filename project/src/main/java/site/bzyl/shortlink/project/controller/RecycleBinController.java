package site.bzyl.shortlink.project.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import site.bzyl.shortlink.project.common.convention.result.Result;
import site.bzyl.shortlink.project.common.convention.result.Results;
import site.bzyl.shortlink.project.dto.req.RecycleBinSaveReqDTO;
import site.bzyl.shortlink.project.service.RecycleBinService;

/**
 * 短链接回收站接口
 */
@RestController
@RequiredArgsConstructor
public class RecycleBinController {
    private final RecycleBinService recycleBinService;
    @PostMapping("/api/short-link/v1/recycle-bin/save")
    public Result<Void> addShortLinkToRecycleBin(@RequestBody RecycleBinSaveReqDTO requestParam) {
        recycleBinService.addShortLinkToRecycleBin(requestParam);
        return Results.success();
    }
}
