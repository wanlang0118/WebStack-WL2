package webstack.module.siteSection.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import webstack.common.annotation.RequireRole;
import webstack.common.constant.RoleConstant;
import webstack.common.result.Result;
import webstack.module.siteSection.dto.SiteSectionSaveDTO;
import webstack.module.siteSection.dto.SiteSectionUpdateDTO;
import webstack.module.siteSection.service.SiteSectionService;
import webstack.module.siteSection.vo.SiteSectionVO;

import java.util.List;

@RestController
@RequestMapping("/api/site-section")
@RequiredArgsConstructor
@RequireRole(RoleConstant.ADMIN)
public class SiteSectionController {

    private final SiteSectionService siteSectionService;

    @GetMapping("/list")
    @RequireRole({RoleConstant.ADMIN, RoleConstant.USER})
    public Result<List<SiteSectionVO>> list(@RequestParam Integer siteId) {
        return Result.success(siteSectionService.listBySiteId(siteId));
    }

    @GetMapping("/{id}")
    @RequireRole({RoleConstant.ADMIN, RoleConstant.USER})
    public Result<SiteSectionVO> detail(@PathVariable Integer id) {
        return Result.success(siteSectionService.getDetail(id));
    }

    @PostMapping("/save")
    public Result<SiteSectionVO> save(@RequestBody @Valid SiteSectionSaveDTO dto) {
        return Result.success(siteSectionService.saveSection(dto));
    }

    @PutMapping("/update")
    public Result<Void> update(@RequestBody @Valid SiteSectionUpdateDTO dto) {
        siteSectionService.updateSection(dto);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Integer id) {
        boolean flag = siteSectionService.removeById(id);
        return flag ? Result.success() : Result.error("删除失败");
    }

    @PostMapping("/batch-delete")
    public Result<Void> deleteBatch(@RequestBody List<Integer> ids) {
        if (ids == null || ids.isEmpty()) {
            return Result.error("请选择要删除的数据");
        }
        boolean flag = siteSectionService.removeByIds(ids);
        return flag ? Result.success() : Result.error("删除失败");
    }
}
