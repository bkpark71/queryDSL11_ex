package com.example.querydsl11_ex.repository;

import com.example.querydsl11_ex.entity.Department;
import com.example.querydsl11_ex.entity.Employee;
import com.example.querydsl11_ex.entity.QDepartment;
import com.example.querydsl11_ex.entity.QEmployee;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceContext;
import java.util.List;

@SpringBootTest
@Transactional
public class QueryDslTest {
    @PersistenceContext
    EntityManager em;

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
        createTestData();
        JPAQueryFactory qryFactory = new JPAQueryFactory(em);
        //QEmployee e = new QEmployee("e"); // 직접 만든 별칭
        QDepartment d = QDepartment.department; // 기본별칭
        QEmployee e = QEmployee.employee; // 직접 만든 별�

        Employee findEmp = qryFactory.select(e).from(e).where(e.empName.eq("emp1")).fetchOne();
        Assertions.assertThat(findEmp.getDepartment().getDeptName()).isEqualTo("dept1");
        System.out.println(findEmp);
        System.out.println(findEmp.getDepartment());

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
        em.persist(emp1);

        Employee emp2 = new Employee();
        emp2.setEmpName("emp2");
        emp2.setDepartment(dept1);
        em.persist(emp2);

        Employee emp3 = new Employee();
        emp3.setEmpName("emp3");
        emp3.setDepartment(dept2);
        em.persist(emp3);
    }

}
