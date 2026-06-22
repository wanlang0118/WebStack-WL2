package webstack.module.siteLike.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import webstack.common.annotation.RequireRole;
import webstack.common.constant.RoleConstant;
import webstack.common.result.Result;
import webstack.module.siteLike.dto.SiteLikeQueryDTO;
import webstack.module.siteLike.service.SiteLikeService;
import webstack.module.siteLike.vo.SiteLikeVO;

import java.util.List;

@RestController
@RequestMapping("/api/site/like")
@RequiredArgsConstructor
public class SiteLikeController {

    private final SiteLikeService siteLikeService;

    /**
     * 分页查询点赞记录（管理端，需要管理员）
     */
    @GetMapping("/page")
    @RequireRole(RoleConstant.ADMIN)
    public Result<IPage<SiteLikeVO>> page(SiteLikeQueryDTO dto) {
        return Result.success(siteLikeService.pageQuery(dto));
    }

    /**
     * 点赞（匿名可访问，登录用户按 userId 防重，游客按 IP 防重）
     */
    @PostMapping("/{siteId}")
    public Result<Void> like(@PathVariable Integer siteId, HttpServletRequest request) {
        siteLikeService.like(siteId, request);
        return Result.success();
    }

    /**
     * 取消点赞（匿名可访问）
     */
    @DeleteMapping("/{siteId}")
    public Result<Void> unlike(@PathVariable Integer siteId, HttpServletRequest request) {
        siteLikeService.unlike(siteId, request);
        return Result.success();
    }

    /**
     * 查询是否已点赞（匿名可访问，根据登录态或 IP 判断）
     */
    @GetMapping("/status/{siteId}")
    public Result<Boolean> status(@PathVariable Integer siteId, HttpServletRequest request) {
        return Result.success(siteLikeService.isLiked(siteId, request));
    }

    /**
     * 管理端删除单条点赞记录
     */
    @DeleteMapping("/record/{id}")
    @RequireRole(RoleConstant.ADMIN)
    public Result<Void> deleteRecord(@PathVariable Long id) {
        siteLikeService.removeRecord(id);
        return Result.success();
    }

    /**
     * 管理端批量删除点赞记录
     */
    @PostMapping("/batch-delete")
    @RequireRole(RoleConstant.ADMIN)
    public Result<Void> deleteBatch(@RequestBody List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return Result.error("请选择要删除的数据");
        }
        siteLikeService.removeBatch(ids);
        return Result.success();
    }
}
