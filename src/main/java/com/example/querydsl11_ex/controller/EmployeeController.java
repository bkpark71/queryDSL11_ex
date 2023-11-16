package com.example.querydsl11_ex.controller;

import com.example.querydsl11_ex.dto.DynamicSearchCond;
import com.example.querydsl11_ex.dto.EmployeeDto;
import com.example.querydsl11_ex.entity.Employee;
import com.example.querydsl11_ex.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class EmployeeController {
    private final EmployeeService employeeService;

    @GetMapping("/employees")
    public List<EmployeeDto> getEmployeeByDynamicCond(
            @RequestParam(name="name" , required = false) String empName,
            @RequestParam(required = false) Integer salary,
            @RequestParam(name="dept", required = false) String deptName) {
        DynamicSearchCond cond = new DynamicSearchCond();
        cond.setEmpName(empName);
        cond.setSalary(salary);
        cond.setDeptName(deptName);
        return employeeService.getEmployeesByDynamicCondition(cond);
    }
}
