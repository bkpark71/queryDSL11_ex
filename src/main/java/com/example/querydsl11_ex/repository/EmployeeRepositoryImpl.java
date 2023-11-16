package com.example.querydsl11_ex.repository;

import com.example.querydsl11_ex.dto.DynamicSearchCond;
import com.example.querydsl11_ex.dto.EmployeeDto;
import com.example.querydsl11_ex.dto.QEmployeeDto;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import javax.persistence.EntityManager;
import java.util.List;

import static com.example.querydsl11_ex.entity.QDepartment.department;
import static com.example.querydsl11_ex.entity.QEmployee.employee;

@Repository

public class EmployeeRepositoryImpl implements EmployeeRepositoryQuery{
    private final EntityManager em;
    private final JPAQueryFactory queryFactory;

    public EmployeeRepositoryImpl(EntityManager em) {
        this.em = em;
        queryFactory = new JPAQueryFactory(em);
    }

    public List<EmployeeDto> dynamicSearch(DynamicSearchCond searchCond) {
        List<EmployeeDto> fetch = queryFactory
                .select(new QEmployeeDto(
                        employee.empName,
                        employee.salary))
                .from(employee)
                .where(
                        empNameEq(searchCond.getEmpName()),
                        salaryGoe(searchCond.getSalary()),
                        deptNameEq(searchCond.getDeptName())
                )
                .fetch();
        return fetch;
    }
    private BooleanExpression empNameEq(String empName) {
        return StringUtils.hasText(empName) ? employee.empName.eq(empName) : null;
    }

    private BooleanExpression salaryGoe(Integer salary) {
        return salary != null ? employee.salary.goe(salary) : null;
    }

    private BooleanExpression deptNameEq(String deptName) {
        return StringUtils.hasText(deptName) ? department.deptName.eq(deptName) : null;
    }
}
