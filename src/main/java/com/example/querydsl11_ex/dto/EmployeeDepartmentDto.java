package com.example.querydsl11_ex.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class EmployeeDepartmentDto {
    private String name;
    private int salary;
    private String deptName;

    @QueryProjection
    public EmployeeDepartmentDto(String name, int salary, String deptName) {
        this.name = name;
        this.salary = salary;
        this.deptName = deptName;
    }
}
