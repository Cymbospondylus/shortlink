package site.bzyl.shortlink.project.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import site.bzyl.shortlink.project.dao.entity.LinkDO;

/**
 * 短链接持久层 Mapper
 */
@Mapper
public interface ShortLinkMapper extends BaseMapper<LinkDO> {
}
