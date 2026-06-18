package com.cts.adstudio.mediaplan.shared;

import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
public class PagedResponse<T> {
    private List<T> content;       // the rows on this page
    private int page;              // current page number (0-based)
    private int size;              // rows per page
    private long totalElements;    // total rows across all pages
    private int totalPages;        // total number of pages
    private boolean last;          // is this the last page?
}