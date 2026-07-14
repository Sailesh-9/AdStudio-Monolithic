package com.cts.adstudio.mediaplan.shared;

import org.springframework.data.domain.Page;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class PaginationHelper {

    //param page   the Page returned by a repository (e.g. repo.findAll(pageable))
    //param mapper a function that converts each entity into its response DTO
    
    public static <E, D> PagedResponse<D> toPagedResponse(Page<E> page, Function<E, D> mapper) {
        List<D> content = page.getContent().stream()
                .map(mapper)
                .collect(Collectors.toList());

        return PagedResponse.<D>builder()
                .content(content)
                .page(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .last(page.isLast())
                .build();
    }
}