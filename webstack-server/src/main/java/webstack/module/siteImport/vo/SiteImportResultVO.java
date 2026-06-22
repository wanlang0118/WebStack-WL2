package webstack.module.siteImport.vo;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class SiteImportResultVO {

    private int total;
    private int successCount;
    private int failCount;
    private List<ImportError> errors = new ArrayList<>();

    @Data
    public static class ImportError {
        private int index;
        private String name;
        private String reason;

        public ImportError(int index, String name, String reason) {
            this.index = index;
            this.name = name;
            this.reason = reason;
        }
    }
}
