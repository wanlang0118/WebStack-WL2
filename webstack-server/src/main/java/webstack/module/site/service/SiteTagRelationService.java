package webstack.module.site.service;

import com.baomidou.mybatisplus.extension.service.IService;
import webstack.module.site.entity.SiteTagRelation;

import java.util.List;

public interface SiteTagRelationService extends IService<SiteTagRelation> {

    void saveRelations(Integer siteId, List<Integer> tagIds);

    void removeBySiteId(Integer siteId);
}
