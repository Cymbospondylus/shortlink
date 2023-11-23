package site.bzyl.shortlink.project.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import site.bzyl.shortlink.project.dao.entity.LinkAccessStatsDO;

/**
 * 短链接访问统计 Mapper
 */
public interface ShortLinkAccessStatsMapper extends BaseMapper<LinkAccessStatsDO> {
    void accessShortLink(@Param("linkAccessStats") LinkAccessStatsDO linkAccessStatsDO);
}
