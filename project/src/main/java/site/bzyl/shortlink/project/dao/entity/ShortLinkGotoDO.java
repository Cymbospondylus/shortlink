package site.bzyl.shortlink.project.dao.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Builder;
import lombok.Data;

/**
 * 短链接跳转持久层实体
 */
@Data
@Builder
@TableName("t_link_goto")
public class ShortLinkGotoDO {
    @TableId(type = IdType.AUTO)
    /**
     * id
     */
    private Long id;

    /**
     * 完整短链接
     */
    private String fullShortUri;

    /**
     * 分组标识
     */
    private String gid;
}
