package site.bzyl.shortlink.admin.dao.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;


/**
 * 用户持久层实体
 */
@Data
@TableName("t_user")
public class UserDO implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    /**
    * id
    */
    private Long id;

    /**
    * 用户名
    */
    private String username;

    /**
    * 密码
    */
    private String password;

    /**
    * 真实姓名
    */
    private String realName;

    /**
    * 手机号
    */
    private String phone;

    /**
    * 邮箱
    */
    private String mail;

    /**
    * 注销时间戳
    */
    private Long deletionTime;

    /**
    * 创建时间
    */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
    * 修改时间
    */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /**
    * 逻辑删除 0：未删除 1：已删除
    */
    @TableLogic
    private int delFlag;
}