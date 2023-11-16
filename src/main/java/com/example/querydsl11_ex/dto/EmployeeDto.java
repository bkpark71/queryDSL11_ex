package com.example.querydsl11_ex.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class EmployeeDto {
    private String name;
    private int salary;

    @QueryProjection
    public EmployeeDto(String name, int salary) {
        this.name = name;
        this.salary = salary;
    }
}
