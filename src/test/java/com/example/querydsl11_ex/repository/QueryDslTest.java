package com.example.querydsl11_ex.repository;

import com.example.querydsl11_ex.dto.DynamicSearchCond;
import com.example.querydsl11_ex.dto.EmployeeDto;
import com.example.querydsl11_ex.dto.QEmployeeDto;
import com.example.querydsl11_ex.entity.Department;
import com.example.querydsl11_ex.entity.Employee;
import com.example.querydsl11_ex.entity.QDepartment;
import com.example.querydsl11_ex.entity.QEmployee;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.QueryResults;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceContext;
import java.util.List;

import static com.example.querydsl11_ex.entity.QEmployee.*;
import static com.example.querydsl11_ex.entity.QDepartment.*;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
//@Rollback(value=false)
public class QueryDslTest {
    @PersistenceContext
    EntityManager em;

    JPAQueryFactory qryFactory;


    @BeforeEach
    public void factoryCreate(){
        qryFactory = new JPAQueryFactory(em);
    }

    @Test
    public void jpqlTest(){
        //given
        createTestData();

        //when
        Employee findEmp = em.createQuery("select e from Employee e where e.empName = :empName", Employee.class)
                .setParameter("empName", "emp1")
                .getSingleResult();

        //then
        assertThat(findEmp.getDepartment().getDeptName()).isEqualTo("dept1");
        System.out.println(findEmp);
        System.out.println(findEmp.getDepartment());
    }

    @Test
    public void jpqlDtoTest(){
        //given
        //when
        List<EmployeeDto> findEmps = em.createQuery("select " +
                        "new com.example.querydsl11_ex.dto.EmployeeDto(e.empName, e.salary) " +
                        "from Employee e " +
                        "where e.empName = :empName", EmployeeDto.class)
                .setParameter("empName", "emp1")
                .getResultList();
        //then
        assertThat(findEmps.size()).isGreaterThan(1);
        for (EmployeeDto findEmp : findEmps) {
            System.out.println(findEmp);
        }
    }

    @Test
    public void queryDSLDtoTest(){
//        EmployeeDto emp1 = qryFactory.select(Projections
//                        .fields(EmployeeDto.class,
//                                employee.empName.as("name"),
//                                employee.salary))
//                .from(employee)
//                .where(employee.empName.eq("emp1"))
//                .fetchOne();

        EmployeeDto emp1 = qryFactory.select(Projections
                        .constructor(EmployeeDto.class,
                                employee.empName,
                                employee.salary))
                .from(employee)
                .where(employee.empName.eq("emp1"))
                .fetchOne();

        System.out.println(emp1);
    }

    @Test
    public void querydslTest(){
        //createTestData();
        //QEmployee e = employee; // 직접 만든 별칭
        Employee findEmp = qryFactory
                .select(employee)
                .from(employee)
                .where(employee.empName.eq("emp1"))
                .fetchOne();
        assertThat(findEmp.getDepartment().getDeptName()).isEqualTo("dept1");
        System.out.println(findEmp);
        System.out.println(findEmp.getDepartment());
    }

    @Test
    public void 검색(){
        //when
        List<Employee> findEmp = qryFactory
                .select(employee)
                .from(employee)
                .where(
                        employee.department.deptId.eq(1)
                                .or(employee.salary.goe(100))
                )
                .fetch();
        //then
        assertThat(findEmp.size()).isEqualTo(3);
    }

    @Test
    public void fetch테스트(){
        //when
        long empCnt = qryFactory
                .selectFrom(employee)
                .where(
                        (employee.salary.eq(200))
                )
                .fetchCount();
        //then
        assertThat(empCnt).isEqualTo(2);
    }


    @Test
    public void fetchResult테스트(){
        //when
        QueryResults<Employee> qryResults = qryFactory
                .selectFrom(employee)
                .fetchResults();
        //then

        assertThat(qryResults.getTotal()).isEqualTo(5);
        assertThat(qryResults.getResults().size()).isEqualTo(5);
        for (Employee e : qryResults.getResults()) {
            System.out.println(e);
        }
    }

    @Test
    public void 정렬테스트(){
        List<Employee> employees = qryFactory
                .selectFrom(employee)
                .orderBy(employee.salary.desc(), employee.empName.asc().nullsFirst())
                .fetch();
        for (Employee e : employees) {
            System.out.println(e);
        }
    }

    @Test
    public void 페이징테스트(){
        List<Employee> employees = qryFactory
                .selectFrom(employee)
                .orderBy(employee.empName.asc().nullsLast())
                .offset(2*(3-1))
                .limit(2)
                .fetch();
        for (Employee e : employees) {
            System.out.println(e);
        }
    }

    @Test
    public void 집합함수테스트(){
        List<Tuple> empTuple = qryFactory
                .select(
                        employee.count(),
                        employee.salary.sum(),
                        employee.salary.avg(),
                        employee.salary.max(),
                        employee.salary.min()
                )
                .from(employee)
                .fetch();
        
        for (Tuple tuple : empTuple) {
            System.out.println(tuple);
        }
    }

    @Test
    public void groupbyTest(){
        List<Tuple> tuples = qryFactory
                .select(employee.department.deptId, employee.salary.avg())
                .from(employee)
                .groupBy(employee.department.deptId)
                .having(employee.salary.avg().goe(200))
                .fetch();

//        Tuple tuple1 = tuples.get(0);
//        Tuple tuple2 = tuples.get(1);

//        Double dept1_avg = tuple1.get(employee.salary.avg());
//        Double dept2_avg = tuple2.get(employee.salary.avg());

        System.out.println("tuple1 ==> " + tuples.size());
        assertThat(tuples.get(0).get(employee.department.deptId)).isEqualTo(2);
    }

    @Test
    public void joinTest(){
        QEmployee e = employee; // ���� ���� ����
        QDepartment d = department; // ���� ���� ����

//        List<Employee> emps = qryFactory
//                .select(e)
//                .from(e)
//                .join(e.department,d)
//                .where(e.department.deptId.eq(1))
//                .fetch();
//
//        assertThat(emps.size()).isEqualTo(2);
//        for (Employee emp : emps) {
//            System.out.println(emp);
//        }

                List<Employee> emps = qryFactory
                .select(e)
                .from(e)
                .leftJoin(e.department,d)
                .where(e.department.deptName.startsWith("dept"))
                .fetch();

                assertThat(emps.size()).isEqualTo(6);
    }

    @Test
    public void thetajoinTest(){
        QEmployee e = employee; // ���� ���� ����
        QDepartment d = department; // ���� ���� ����\

        List<Employee> fetch = qryFactory
                .select(e)
                .from(e, d)
                .where(e.department.deptId.eq(e.empId))
                .fetch();

        assertThat(fetch.size()).isEqualTo(3);
    }

    @Test
    public void subqueryTest(){
        QEmployee emp = new QEmployee("emp");
        QEmployee sub_emp = new QEmployee("sub_emp");

//        List<Employee> emps = qryFactory
//                .selectFrom(emp)
//                .where(emp.salary.goe(
//                        JPAExpressions
//                                .select(sub_emp.salary.avg())
//                                .from(sub_emp))
//                )
//                .fetch();

//        List<Employee> emps = qryFactory
//                .selectFrom(emp)
//                .where(emp.salary.in(
//                        JPAExpressions
//                                .select(sub_emp.salary)
//                                .from(sub_emp)
//                                .where(sub_emp.salary.loe(200)))
//                )
//                .fetch();
//
//        assertThat(emps.size()).isEqualTo(5);
       // assertThat(emps).extracting("salary").containsExactly(300,300,300);

        List<Tuple> tuples = qryFactory
                .select(emp.empName,
                        JPAExpressions
                                .select(sub_emp.salary.avg())
                                .from(sub_emp)
                )
                .from(emp)
                .fetch();

        for (Tuple tuple : tuples) {
            System.out.println(tuple.get(emp.empName) + ":" +
                        tuple.get(JPAExpressions.select(sub_emp.salary.avg()).from(sub_emp)));
        };
    }

    private void createTestData() {
        Department dept1 = new Department();
        dept1.setDeptName("dept1");
        em.persist(dept1);

        Department dept2 = new Department();
        dept2.setDeptName("dept2");
        em.persist(dept2);

        Employee emp1 = new Employee();
        emp1.setEmpName("emp1");
        emp1.setDepartment(dept1);
        emp1.setSalary(100);
        em.persist(emp1);

        Employee emp2 = new Employee();
        emp2.setEmpName("emp2");
        emp2.setDepartment(dept1);
        emp1.setSalary(200);
        em.persist(emp2);

        Employee emp3 = new Employee();
        emp3.setEmpName("emp3");
        emp3.setDepartment(dept2);
        emp1.setSalary(200);
        em.persist(emp3);
    }

    @Test
    public void dynamicQueryBooleanBuilder(){
        DynamicSearchCond searchCond = new DynamicSearchCond();
        //searchCond.setEmpName("emp1");
        searchCond.setSalary(300);
        List<EmployeeDto> employees = getEmployeesByBooleanBuilder(searchCond);
        assertThat(employees.size()).isEqualTo(3);
    }

    public List<EmployeeDto> getEmployeesByBooleanBuilder(DynamicSearchCond searchCond){
        BooleanBuilder builder = new BooleanBuilder();
        if(StringUtils.hasText(searchCond.getEmpName())){
            builder.and(employee.empName.eq(searchCond.getEmpName()));
        }
        if(searchCond.getSalary() != null){
            builder.and(employee.salary.goe(searchCond.getSalary()));
        }
        if(StringUtils.hasText(searchCond.getDeptName())){
            builder.and(department.deptName.eq(searchCond.getDeptName()));
        }
        List<EmployeeDto> fetch = qryFactory
                .select(Projections.constructor(
                        EmployeeDto.class,
                        employee.empName,
                        employee.salary))
                .from(employee)
                .where(builder)
                .fetch();
        return fetch;
    }

    @Test
    public void dynamicQueryWhereTest(){
        DynamicSearchCond searchCond = new DynamicSearchCond();
        //searchCond.setEmpName("emp1");
        searchCond.setDeptName("dept2");
        List<EmployeeDto> employees = getEmployeesByWhereParam(searchCond);
        assertThat(employees.size()).isEqualTo(4);
    }

    public List<EmployeeDto> getEmployeesByWhereParam(DynamicSearchCond searchCond) {
        List<EmployeeDto> fetch = qryFactory
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