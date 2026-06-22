package webstack.module.siteSectionContent.vo.contentdata;

import lombok.Data;

import java.util.List;

@Data
public class TableData {

    private List<String> headers;

    private List<List<String>> rows;
}
