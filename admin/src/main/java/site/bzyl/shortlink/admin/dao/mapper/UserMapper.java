package site.bzyl.shortlink.admin.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import site.bzyl.shortlink.admin.dao.entity.UserDO;

/**
 * 用户持久层
 */
@Mapper
public interface UserMapper extends BaseMapper<UserDO> {
}
