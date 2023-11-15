package com.example.querydsl11_ex.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@ToString(of={"deptId", "deptName"})
@Table(name = "departments")
public class Department {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "dept_id")
    private int deptId;
    @Column(name = "dept_name", length=10)
    private String deptName;
    @OneToMany(mappedBy = "department")
    private List<Employee> employees = new ArrayList<>();
}
