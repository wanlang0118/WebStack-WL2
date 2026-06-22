package webstack.module.category.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import webstack.common.exception.BizException;
import webstack.module.category.dto.CategorySaveDTO;
import webstack.module.category.dto.CategoryUpdateDTO;
import webstack.module.category.entity.Category;
import webstack.module.category.mapper.CategoryMapper;
import webstack.module.category.service.CategoryService;
import webstack.module.category.vo.CategoryTreeVO;
import webstack.module.site.service.SiteService;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {

    /** 跨模块依赖走 Service 层，不直接依赖 SiteMapper，保持模块隔离 */
    private final SiteService siteService;

    @Override
    public List<CategoryTreeVO> tree() {
        return buildTree(list());
    }

    @Override
    public void saveCategory(CategorySaveDTO dto) {
        Category category = new Category();
        BeanUtils.copyProperties(dto, category);
        save(category);
    }

    @Override
    public void updateCategory(CategoryUpdateDTO dto) {
        Category category = new Category();
        BeanUtils.copyProperties(dto, category);
        updateById(category);
    }

    private List<CategoryTreeVO> buildTree(List<Category> all) {
        List<CategoryTreeVO> allVO = all.stream()
                .map(item -> {
                    CategoryTreeVO vo = new CategoryTreeVO();
                    BeanUtils.copyProperties(item, vo);
                    return vo;
                })
                .sorted(Comparator.comparing(CategoryTreeVO::getSort).reversed())
                .collect(Collectors.toList());

        List<CategoryTreeVO> root = new ArrayList<>();
        for (CategoryTreeVO vo : allVO) {
            if (vo.getParentId() == null || vo.getParentId() == 0) {
                root.add(vo);
            }
        }

        for (CategoryTreeVO node : root) {
            fillChildren(node, allVO);
        }
        return root;
    }

    private void fillChildren(CategoryTreeVO parent, List<CategoryTreeVO> all) {
        List<CategoryTreeVO> children = new ArrayList<>();
        for (CategoryTreeVO vo : all) {
            if (parent.getId().equals(vo.getParentId())) {
                children.add(vo);
                fillChildren(vo, all);
            }
        }
        children.sort(Comparator.comparing(CategoryTreeVO::getSort).reversed());
        parent.setChildren(children);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean removeCascade(Integer id) {
        return removeBatchCascade(List.of(id));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean removeBatchCascade(List<Integer> ids) {
        List<Integer> idsToDelete = new ArrayList<>(ids);

        List<Integer> currentLevel = new ArrayList<>(ids);
        while (!currentLevel.isEmpty()) {
            LambdaQueryWrapper<Category> wrapper = new LambdaQueryWrapper<>();
            wrapper.in(Category::getParentId, currentLevel);
            List<Category> children = list(wrapper);
            if (children.isEmpty()) {
                break;
            }
            List<Integer> childIds = children.stream().map(Category::getId).toList();
            idsToDelete.addAll(childIds);
            currentLevel = childIds;
        }

        // 检查是否存在关联网站（跨模块查询走 SiteService）
        List<String> siteTitles = siteService.listTitlesByCategoryIds(idsToDelete);
        if (!siteTitles.isEmpty()) {
            String siteNames = String.join("、", siteTitles);
            throw new BizException(
                    "删除失败：选中的分类或其子分类下存在关联网站 [" + siteNames + "]，请先删除或转移这些网站！"
            );
        }

        return removeByIds(idsToDelete);
    }

    @Override
    public Integer getOrCreateByTitle(String title) {
        Category existing = lambdaQuery()
                .eq(Category::getTitle, title)
                .eq(Category::getParentId, 0)
                .last("LIMIT 1")
                .one();
        if (existing != null) {
            return existing.getId();
        }
        Category category = new Category();
        category.setTitle(title);
        category.setParentId(0);
        category.setVisible(1);
        category.setSort(0);
        save(category);
        return category.getId();
    }
}
