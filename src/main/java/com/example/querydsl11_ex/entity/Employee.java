package com.example.querydsl11_ex.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.jmx.export.annotation.ManagedAttribute;

import javax.persistence.*;

@Entity
@Getter
@Setter
@ToString(of={"empId", "empName"})
@Table(name = "employees")
public class Employee {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "emp_id")
    private int empId;
    @Column(name="emp_name", length=10)
    private String empName;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dept_id")
    private Department department;
}
