package site.bzyl.shortlink.project.dao.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Builder;
import lombok.Data;
import site.bzyl.shortlink.project.database.BaseDO;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 短链接持久层实体
 */
@Data
@Builder
@TableName("t_link")
public class LinkDO extends BaseDO implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    /**
    * id
    */
    private Long id;

    /**
    * 域名
    */
    private String domain;

    /**
    * 短链接
    */
    private String shortUri;

    /**
    * 完整短链接
    */
    private String fullShortUri;

    /**
    * 原始链接
    */
    private String originUri;

    /**
    * 点击量
    */
    private Integer clickNum;

    /**
    * 分组标识
    */
    private String gid;

    /**
    * 启用标识 0：未启用 1：已启用
    */
    private int enableStatus;

    /**
    * 创建类型 0：控制台 1：接口
    */
    private Integer createdType;

    /**
    * 有效期类型 0：永久有效 1：用户自定义
    */
    private Integer validDateType;

    /**
    * 有效期
    */
    private LocalDateTime validDate;

    /**
    * 描述
    */
    private String description;

}