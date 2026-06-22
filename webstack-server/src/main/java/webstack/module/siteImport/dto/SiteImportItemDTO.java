package webstack.module.siteImport.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Data;

import java.util.List;

@Data
public class SiteImportItemDTO {

    @JsonAlias("title")
    private String name;

    @JsonAlias("thumb")
    private String logo;

    @JsonAlias({"short_description", "description"})
    private String shortDescription;

    @JsonAlias({"official_url", "url"})
    private String officialUrl;

    private String category;

    private List<String> tags;

    private List<ImportSectionDTO> sections;

    @Data
    public static class ImportSectionDTO {

        private String title;

        private List<String> paragraphs;

        private List<String> images;

        @JsonAlias("list_items")
        private List<ImportListItemDTO> listItems;

        private List<List<String>> table;
    }

    @Data
    public static class ImportListItemDTO {

        private String text;
    }
}
