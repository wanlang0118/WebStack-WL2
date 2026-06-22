package webstack.module.siteSectionContent.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import webstack.common.annotation.RequireRole;
import webstack.common.constant.RoleConstant;
import webstack.common.result.Result;
import webstack.module.siteSectionContent.dto.SiteSectionContentSaveDTO;
import webstack.module.siteSectionContent.dto.SiteSectionContentUpdateDTO;
import webstack.module.siteSectionContent.service.SiteSectionContentService;
import webstack.module.siteSectionContent.vo.SiteSectionContentVO;

import java.util.List;

@RestController
@RequestMapping("/api/site-section-content")
@RequiredArgsConstructor
@RequireRole(RoleConstant.ADMIN)
public class SiteSectionContentController {

    private final SiteSectionContentService siteSectionContentService;

    @GetMapping("/list")
    @RequireRole({RoleConstant.ADMIN, RoleConstant.USER})
    public Result<List<SiteSectionContentVO>> list(@RequestParam Integer sectionId) {
        return Result.success(siteSectionContentService.listBySectionId(sectionId));
    }

    @GetMapping("/{id}")
    @RequireRole({RoleConstant.ADMIN, RoleConstant.USER})
    public Result<SiteSectionContentVO> detail(@PathVariable Integer id) {
        return Result.success(siteSectionContentService.getDetail(id));
    }

    @PostMapping("/save")
    public Result<SiteSectionContentVO> save(@RequestBody @Valid SiteSectionContentSaveDTO dto) {
        return Result.success(siteSectionContentService.saveContent(dto));
    }

    @PutMapping("/update")
    public Result<Void> update(@RequestBody @Valid SiteSectionContentUpdateDTO dto) {
        siteSectionContentService.updateContent(dto);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Integer id) {
        boolean flag = siteSectionContentService.removeById(id);
        return flag ? Result.success() : Result.error("删除失败");
    }

    @PostMapping("/batch-delete")
    public Result<Void> deleteBatch(@RequestBody List<Integer> ids) {
        if (ids == null || ids.isEmpty()) {
            return Result.error("请选择要删除的数据");
        }
        boolean flag = siteSectionContentService.removeByIds(ids);
        return flag ? Result.success() : Result.error("删除失败");
    }
}
