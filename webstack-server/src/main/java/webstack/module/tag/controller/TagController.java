package webstack.module.tag.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import webstack.common.annotation.RequireRole;
import webstack.common.constant.RoleConstant;
import webstack.common.result.Result;
import webstack.module.tag.dto.TagQueryDTO;
import webstack.module.tag.dto.TagSaveDTO;
import webstack.module.tag.dto.TagUpdateDTO;
import webstack.module.tag.service.TagService;
import webstack.module.tag.vo.TagVO;

import java.util.List;

@RestController
@RequestMapping("/api/tag")
@RequiredArgsConstructor
@RequireRole(RoleConstant.ADMIN)
public class TagController {

    private final TagService tagService;

    @GetMapping("/page")
    public Result<IPage<TagVO>> page(TagQueryDTO dto) {
        return Result.success(tagService.pageQuery(dto));
    }

    @GetMapping("/{id}")
    public Result<TagVO> detail(@PathVariable Integer id) {
        return Result.success(tagService.getDetail(id));
    }

    @GetMapping("/list")
    @RequireRole({RoleConstant.ADMIN, RoleConstant.USER})
    public Result<List<TagVO>> list() {
        return Result.success(tagService.listAll());
    }

    @PostMapping("/save")
    public Result<TagVO> save(@RequestBody @Valid TagSaveDTO dto) {
        return Result.success(tagService.saveTag(dto));
    }

    @PutMapping("/update")
    public Result<Void> update(@RequestBody @Valid TagUpdateDTO dto) {
        tagService.updateTag(dto);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Integer id) {
        tagService.removeTagWithRelations(id);
        return Result.success();
    }

    @PostMapping("/batch-delete")
    public Result<Void> deleteBatch(@RequestBody List<Integer> ids) {
        if (ids == null || ids.isEmpty()) {
            return Result.error("请选择要删除的数据");
        }
        tagService.removeTagsWithRelations(ids);
        return Result.success();
    }
}
