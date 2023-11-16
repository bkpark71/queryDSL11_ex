package com.example.querydsl11_ex.repository;

import com.example.querydsl11_ex.entity.Employee;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class EmployeeRepositoryTest {

    @PersistenceContext  //수정
    EntityManager em;

    @Autowired
    EmployeeRepository employeeRepository;//수정 - 오류가 있어서 entitymanager autowired ==> persistContext 로 수정함

    @Test
    public void findAll() {
        List<Employee> all = employeeRepository.findAll();
        assertThat(all.size()).isEqualTo(8);
    }

    @Test
    public void findBySalaryTest(){
        List<Employee> dept2 = employeeRepository.findBySalaryGreaterThanEqual(300);
        assertThat(dept2.size()).isEqualTo(3);
    }
}