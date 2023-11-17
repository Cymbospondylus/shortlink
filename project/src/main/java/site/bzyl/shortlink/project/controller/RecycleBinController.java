package site.bzyl.shortlink.project.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import site.bzyl.shortlink.project.common.convention.result.Result;
import site.bzyl.shortlink.project.common.convention.result.Results;
import site.bzyl.shortlink.project.dto.req.RecycleBinPageReqDTO;
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
    public Result<IPage<RecycleBinPageRespDTO>> pageRecycleBinLink(@RequestBody RecycleBinPageReqDTO requestParam) {
        return Results.success(recycleBinService.pageRecycleBinLink(requestParam));
    }
}
