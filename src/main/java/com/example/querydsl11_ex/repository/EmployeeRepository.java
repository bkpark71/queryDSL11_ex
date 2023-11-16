package com.example.querydsl11_ex.repository;


import com.example.querydsl11_ex.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EmployeeRepository extends JpaRepository<Employee, Integer> {
    public List<Employee> findByEmpName(String empName);
    public List<Employee> findBySalaryGreaterThanEqual(Integer salary);

}
