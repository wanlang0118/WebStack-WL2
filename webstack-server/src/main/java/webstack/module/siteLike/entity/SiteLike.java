package webstack.module.siteLike.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户点赞记录实体
 * 对应表：site_like
 */
@Data
@TableName("site_like")
public class SiteLike {

    /** bigint → Long，防止高并发场景下溢出 */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 被点赞的网站 ID */
    private Integer siteId;

    /** 登录用户 ID，游客为 null */
    private Integer userId;

    /** 客户端 IP，游客防重用 */
    private String ip;

    /** 记录创建时间 */
    private LocalDateTime createTime;
}
