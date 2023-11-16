package com.example.querydsl11_ex.repository;

import com.example.querydsl11_ex.dto.*;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
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

    @Override
    public Page<EmployeeDepartmentDto> dynamicSearchPagination(
                DynamicSearchCond searchCond,
                Pageable pageable) {
        List<EmployeeDepartmentDto> content = queryFactory
                .select(new QEmployeeDepartmentDto(
                        employee.empName,
                        employee.salary,
                        department.deptName
                ))
                .from(employee)
                .leftJoin(employee.department, department)
                .where(
                        empNameEq(searchCond.getEmpName()),
                        salaryGoe(searchCond.getSalary()),
                        deptNameEq(searchCond.getDeptName())
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();// offset, limit 추가

        long count = queryFactory
                .select(employee.count())
                .from(employee)
                .from(employee)
                .leftJoin(employee.department, department)
                .where(
                        empNameEq(searchCond.getEmpName()),
                        salaryGoe(searchCond.getSalary()),
                        deptNameEq(searchCond.getDeptName())
                ).fetchOne(); // JPAQuery<Long> 으로 변경

            return new PageImpl<>(content, pageable, count);
        // PageableExecutionUtils.getPage(queryFactory.select(employee.count()).from(employee).where(employee.emp))로 변환
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
