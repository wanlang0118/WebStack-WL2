package webstack.module.site.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import webstack.common.annotation.RequireRole;
import webstack.common.constant.RoleConstant;
import webstack.common.result.Result;
import webstack.module.site.dto.SiteQueryDTO;
import webstack.module.site.dto.SiteSaveDTO;
import webstack.module.site.dto.SiteUpdateDTO;
import webstack.module.site.service.SiteService;
import webstack.module.site.vo.SiteVO;

import java.util.List;

@RestController
@RequestMapping("/api/site")
@RequiredArgsConstructor
@RequireRole(RoleConstant.ADMIN)
public class SiteController {

    private final SiteService siteService;

    @GetMapping("/page")
    @RequireRole({RoleConstant.ADMIN, RoleConstant.USER})
    public Result<IPage<SiteVO>> page(SiteQueryDTO dto) {
        return Result.success(siteService.pageQuery(dto));
    }

    @GetMapping("/{id}")
    @RequireRole({RoleConstant.ADMIN, RoleConstant.USER})
    public Result<SiteVO> detail(@PathVariable Integer id) {
        return Result.success(siteService.getSiteDetail(id));
    }

    @PostMapping("/save")
    public Result<Void> save(@RequestBody @Valid SiteSaveDTO dto) {
        siteService.saveSite(dto);
        return Result.success();
    }

    @PutMapping("/update")
    public Result<Void> update(@RequestBody @Valid SiteUpdateDTO dto) {
        siteService.updateSite(dto);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Integer id) {
        boolean flag = siteService.removeById(id);
        return flag ? Result.success() : Result.error("删除失败");
    }

    @PostMapping("/batch-delete")
    public Result<Void> deleteBatch(@RequestBody List<Integer> ids) {
        if (ids == null || ids.isEmpty()) {
            return Result.error("请选择要删除的数据");
        }
        boolean flag = siteService.removeByIds(ids);
        return flag ? Result.success() : Result.error("删除失败");
    }
}
