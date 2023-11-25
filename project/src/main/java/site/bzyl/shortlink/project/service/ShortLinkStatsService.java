package site.bzyl.shortlink.project.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import site.bzyl.shortlink.project.dto.req.ShortLinkStatsAccessRecordReqDTO;
import site.bzyl.shortlink.project.dto.req.ShortLinkStatsReqDTO;
import site.bzyl.shortlink.project.dto.resp.ShortLinkStatsAccessRecordRespDTO;
import site.bzyl.shortlink.project.dto.resp.ShortLinkStatsRespDTO;

/**
 * 短链接监控统计业务层接口
 */
public interface ShortLinkStatsService {
    ShortLinkStatsRespDTO oneShortLinkStats(ShortLinkStatsReqDTO requestParam);

    /**
     * 访问单个短链接指定时间内访问记录监控数据
     *
     * @param requestParam 获取短链接监控访问记录数据入参
     * @return 访问记录监控数据
     */
    IPage<ShortLinkStatsAccessRecordRespDTO> shortLinkStatsAccessRecord(ShortLinkStatsAccessRecordReqDTO requestParam);
}
