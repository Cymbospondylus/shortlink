package site.bzyl.shortlink.admin.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import site.bzyl.shortlink.admin.dao.entity.GroupDO;

/**
 * 短链接分组持久层
 */
@Mapper
public interface ShortLinkGroupMapper extends BaseMapper<GroupDO> {
}
