package site.bzyl.shortlink.project.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import site.bzyl.shortlink.project.common.convention.result.Result;
import site.bzyl.shortlink.project.common.convention.result.Results;
import site.bzyl.shortlink.project.dto.req.RecycleBinDeleteReqDTO;
import site.bzyl.shortlink.project.dto.req.RecycleBinPageReqDTO;
import site.bzyl.shortlink.project.dto.req.RecycleBinRestoreReqDTO;
import site.bzyl.shortlink.project.dto.req.RecycleBinSaveReqDTO;
import site.bzyl.shortlink.project.dto.resp.RecycleBinPageRespDTO;
import site.bzyl.shortlink.project.service.RecycleBinService;

/**
 * 短链接回收站接口
 */
@RestController
@RequiredArgsConstructor
public class RecycleBinController {
    private final RecycleBinService recycleBinService;

    /**
     * 短链接加入回收站接口
     * @param requestParam 短链接加入回收站请求参数
     */
    @PostMapping("/api/short-link/v1/recycle-bin/save")
    public Result<Void> addShortLinkToRecycleBin(@RequestBody RecycleBinSaveReqDTO requestParam) {
        recycleBinService.addShortLinkToRecycleBin(requestParam);
        return Results.success();
    }

    /**
     * 分页查询回收站中短链接
     * @param requestParam 短链接分页查询请求参数
     * @return 短链接分页查询响应参数
     */
    @GetMapping("/api/short-link/v1/recycle-bin/page")
    public Result<IPage<RecycleBinPageRespDTO>> pageRecycleBinLink(RecycleBinPageReqDTO requestParam) {
        return Results.success(recycleBinService.pageRecycleBinLink(requestParam));
    }

    /**
     * 恢复回收站短链接接口
     * @param requestParam 恢复回收站短链接请求参数
     */
    @PostMapping("/api/short-link/v1/recycle-bin/restore")
    public Result<Void> restoreShortLinkFromRecycleBin(@RequestBody RecycleBinRestoreReqDTO requestParam) {
        recycleBinService.restoreShortLinkFromRecycleBin(requestParam);
        return Results.success();
    }

    /**
     * 移除回收站短链接接口
     * @param requestParam 移除回收站短链接请求参数
     */
    @DeleteMapping("/api/short-link/v1/recycle-bin")
    public Result<Void> deleteShortLinkInRecycleBin(@RequestBody RecycleBinDeleteReqDTO requestParam) {
        recycleBinService.deleteShortLinkInRecycleBin(requestParam);
        return Results.success();
    }
}
