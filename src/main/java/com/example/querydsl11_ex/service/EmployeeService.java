package com.example.querydsl11_ex.service;

import com.example.querydsl11_ex.dto.DynamicSearchCond;
import com.example.querydsl11_ex.dto.EmployeeDto;
import com.example.querydsl11_ex.repository.EmployeeRepositoryQuery;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class EmployeeService {
    private final EmployeeRepository employeeRepository;

    public List<EmployeeDto> getEmployeesByDynamicCondition(
            DynamicSearchCond searchCond) {
        return employeeRepository.dynamicSearch(searchCond);
    }
}
