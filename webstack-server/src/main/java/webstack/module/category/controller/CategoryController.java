package webstack.module.category.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import webstack.common.annotation.RequireRole;
import webstack.common.constant.RoleConstant;
import webstack.common.result.Result;
import webstack.module.category.dto.CategorySaveDTO;
import webstack.module.category.dto.CategoryUpdateDTO;
import webstack.module.category.service.CategoryService;
import webstack.module.category.vo.CategoryTreeVO;

import java.util.List;

@RestController
@RequestMapping("/api/category")
@RequiredArgsConstructor
@RequireRole(RoleConstant.ADMIN)
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping("/tree")
    @RequireRole({RoleConstant.ADMIN, RoleConstant.USER})
    public Result<List<CategoryTreeVO>> tree() {
        return Result.success(categoryService.tree());
    }

    @PostMapping("/save")
    public Result<Void> save(@RequestBody @Valid CategorySaveDTO dto) {
        categoryService.saveCategory(dto);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Integer id) {
        boolean flag = categoryService.removeCascade(id);
        return flag ? Result.success() : Result.error("删除失败");
    }

    @PostMapping("/batch-delete")
    public Result<Void> deleteBatch(@RequestBody List<Integer> ids) {
        if (ids == null || ids.isEmpty()) {
            return Result.error("请选择要删除的分类");
        }
        boolean flag = categoryService.removeBatchCascade(ids);
        return flag ? Result.success() : Result.error("删除失败");
    }

    @PutMapping("/update")
    public Result<Void> update(@RequestBody @Valid CategoryUpdateDTO dto) {
        categoryService.updateCategory(dto);
        return Result.success();
    }
}
