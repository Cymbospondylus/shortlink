package site.bzyl.shortlink.project.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
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
}
