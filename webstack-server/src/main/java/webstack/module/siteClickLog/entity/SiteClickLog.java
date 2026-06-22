package webstack.module.siteClickLog.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 点击记录实体
 * 对应表：site_click_log
 */
@Data
@TableName("site_click_log")
public class SiteClickLog {

    /** bigint → Long，防止高并发场景下 21 亿溢出 */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 被点击的网站 ID */
    private Integer siteId;

    /** 登录用户 ID，未登录为 null */
    private Integer userId;

    /** 客户端 IP */
    private String ip;

    /** 记录创建时间 */
    private LocalDateTime createTime;
}
