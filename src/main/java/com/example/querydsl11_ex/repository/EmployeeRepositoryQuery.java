package com.example.querydsl11_ex.repository;

import com.example.querydsl11_ex.dto.DynamicSearchCond;
import com.example.querydsl11_ex.dto.EmployeeDto;

import java.util.List;

public interface EmployeeRepositoryQuery {
    List<EmployeeDto> dynamicSearch(DynamicSearchCond searchCond);
}
