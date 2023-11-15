package com.example.querydsl11_ex.repository;

import com.example.querydsl11_ex.entity.Department;
import com.example.querydsl11_ex.entity.Employee;
import com.example.querydsl11_ex.entity.QDepartment;
import com.example.querydsl11_ex.entity.QEmployee;
import com.querydsl.core.QueryResults;
import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceContext;
import java.util.List;

import static com.example.querydsl11_ex.entity.QEmployee.*;

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
        Assertions.assertThat(findEmp.getDepartment().getDeptName()).isEqualTo("dept1");
        System.out.println(findEmp);
        System.out.println(findEmp.getDepartment());
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
        Assertions.assertThat(findEmp.getDepartment().getDeptName()).isEqualTo("dept1");
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
        Assertions.assertThat(findEmp.size()).isEqualTo(3);
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
        Assertions.assertThat(empCnt).isEqualTo(2);
    }


    @Test
    public void fetchResult테스트(){
        //when
        QueryResults<Employee> qryResults = qryFactory
                .selectFrom(employee)
                .fetchResults();
        //then

        Assertions.assertThat(qryResults.getTotal()).isEqualTo(5);
        Assertions.assertThat(qryResults.getResults().size()).isEqualTo(5);
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
                .fetch();

        Tuple tuple1 = tuples.get(0);
        Tuple tuple2 = tuples.get(1);

        Double dept1_avg = tuple1.get(employee.salary.avg());
        Double dept2_avg = tuple2.get(employee.salary.avg());

        System.out.println("tuple1 ==> " + tuple1);
        System.out.println("tuple2 ==> " + tuple2);


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

}
