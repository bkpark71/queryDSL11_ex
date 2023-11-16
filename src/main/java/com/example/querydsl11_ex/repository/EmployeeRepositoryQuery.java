package com.example.querydsl11_ex.repository;

import com.example.querydsl11_ex.dto.DynamicSearchCond;
import com.example.querydsl11_ex.dto.EmployeeDepartmentDto;
import com.example.querydsl11_ex.dto.EmployeeDto;
import org.springframework.boot.autoconfigure.data.web.SpringDataWebProperties;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


import java.util.List;

public interface EmployeeRepositoryQuery {
    List<EmployeeDto> dynamicSearch(DynamicSearchCond searchCond);
    Page<EmployeeDepartmentDto> dynamicSearchPagination(DynamicSearchCond searchCond, Pageable pageable);
}
