package webstack.module.category.service;

import com.baomidou.mybatisplus.extension.service.IService;
import webstack.module.category.dto.CategorySaveDTO;
import webstack.module.category.dto.CategoryUpdateDTO;
import webstack.module.category.entity.Category;
import webstack.module.category.vo.CategoryTreeVO;

import java.util.List;

public interface CategoryService extends IService<Category> {

    List<CategoryTreeVO> tree();

    void saveCategory(CategorySaveDTO dto);

    void updateCategory(CategoryUpdateDTO dto);

    boolean removeCascade(Integer id);

    boolean removeBatchCascade(List<Integer> ids);

    /**
     * 按分类名称查找顶级分类，不存在则自动创建并返回 ID
     */
    Integer getOrCreateByTitle(String title);
}
