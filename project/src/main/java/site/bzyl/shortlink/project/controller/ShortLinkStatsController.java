package site.bzyl.shortlink.project.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import site.bzyl.shortlink.project.common.convention.result.Result;
import site.bzyl.shortlink.project.common.convention.result.Results;
import site.bzyl.shortlink.project.dto.req.ShortLinkStatsAccessRecordReqDTO;
import site.bzyl.shortlink.project.dto.req.ShortLinkStatsReqDTO;
import site.bzyl.shortlink.project.dto.resp.ShortLinkStatsAccessRecordRespDTO;
import site.bzyl.shortlink.project.dto.resp.ShortLinkStatsRespDTO;
import site.bzyl.shortlink.project.service.ShortLinkStatsService;

@RestController
@RequiredArgsConstructor
public class ShortLinkStatsController {
    private final ShortLinkStatsService shortLinkStatsService;

    /**
     * 访问单个短链接指定时间内监控数据
     */
    @GetMapping("/api/short-link/v1/stats")
    public Result<ShortLinkStatsRespDTO> shortLinkStats(@RequestBody ShortLinkStatsReqDTO requestParam) {
        return Results.success(shortLinkStatsService.oneShortLinkStats(requestParam));
    }


    /**
     * 访问单个短链接指定时间内访问记录监控数据
     */
    @GetMapping("/api/short-link/v1/stats/access-record")
    public Result<IPage<ShortLinkStatsAccessRecordRespDTO>> shortLinkStatsAccessRecord(ShortLinkStatsAccessRecordReqDTO requestParam) {
        return Results.success(shortLinkStatsService.shortLinkStatsAccessRecord(requestParam));
    }
}
