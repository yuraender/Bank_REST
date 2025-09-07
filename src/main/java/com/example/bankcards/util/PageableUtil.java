package com.example.bankcards.util;

import lombok.experimental.UtilityClass;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@UtilityClass
public class PageableUtil {

    public Pageable makePageable(int page, int limit, String direction, String... properties) {
        Sort.Direction sortDirection;
        if (direction.equalsIgnoreCase("asc")) {
            sortDirection = Sort.Direction.ASC;
        } else {
            sortDirection = Sort.Direction.DESC;
        }
        return PageRequest.of(page, limit, Sort.by(sortDirection, properties));
    }
}
