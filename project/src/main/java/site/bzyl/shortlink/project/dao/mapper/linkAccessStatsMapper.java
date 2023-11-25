package site.bzyl.shortlink.project.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import site.bzyl.shortlink.project.dao.entity.LinkAccessStatsDO;
import site.bzyl.shortlink.project.dto.req.ShortLinkStatsReqDTO;

import java.util.List;

/**
 * 短链接访问统计 Mapper
 */
public interface linkAccessStatsMapper extends BaseMapper<LinkAccessStatsDO> {
    /**
     * 短链接跳转后进行统计
     * @param linkAccessStatsDO
     */
    void accessShortLink(@Param("linkAccessStats") LinkAccessStatsDO linkAccessStatsDO);

    List<LinkAccessStatsDO> listStatsByShortLink(@Param("param") ShortLinkStatsReqDTO requestParam);

    List<LinkAccessStatsDO> listHourStatsByShortLink(@Param("param") ShortLinkStatsReqDTO requestParam);

    /**
     * 根据短链接获取指定日期内小时基础监控数据
     */
    @Select("SELECT " +
            "    weekday, " +
            "    SUM(pv) AS pv " +
            "FROM " +
            "    t_link_access_stats " +
            "WHERE " +
            "    full_short_url = #{param.fullShortUrl} " +
            "    AND gid = #{param.gid} " +
            "    AND date BETWEEN #{param.startDate} and #{param.endDate} " +
            "GROUP BY " +
            "    full_short_url, gid, weekday;")
    List<LinkAccessStatsDO> listWeekdayStatsByShortLink(@Param("param") ShortLinkStatsReqDTO requestParam);
}
