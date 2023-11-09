package site.bzyl.shortlink.admin.dao.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Builder;
import lombok.Data;
import site.bzyl.shortlink.admin.database.BaseDO;

import java.io.Serializable;

/**
 * 短链接分组持久层实体类
 */
@Data
@Builder
@TableName("t_group")
public class GroupDO extends BaseDO implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    /**
    * id
    */
    private Long id;

    /**
    * 分组标识
    */
    private String gid;

    /**
    * 分组名称
    */
    private String name;

    /**
    * 创建分组用户标识
    */
    private String username;

}