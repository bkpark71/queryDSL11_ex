package com.example.querydsl11_ex.repository;

import com.example.querydsl11_ex.dto.DynamicSearchCond;
import com.example.querydsl11_ex.dto.EmployeeDepartmentDto;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.data.web.SpringDataWebProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest
@Transactional
class EmployeeRepositoryImplTest {

    @Autowired
    EmployeeRepositoryImpl  employeeRepository;

    @Test
    void dynamicSearchPagination() {
        //given
        DynamicSearchCond cond = new DynamicSearchCond();
        PageRequest pageable = PageRequest.of(1, 5);
        //when
        Page<EmployeeDepartmentDto> resultPage = employeeRepository.dynamicSearchPagination(cond, pageable);
        //then
        System.out.println(resultPage.getTotalPages());
        System.out.println(resultPage.getTotalElements());
        System.out.println(resultPage.getSize());

        assertThat(resultPage.getContent()).extracting("name")
                .containsExactly(null, "emp7", "emp8");
    }
}