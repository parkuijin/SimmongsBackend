package com.simmongs.department;

import jakarta.persistence.*;
import lombok.*;

@Entity(name = "DEPARTMENT_TB")
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Departments {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "DEPARTMENT_ID")
    private Long departmentId;

    @Column(name = "DEPARTMENT_NAME")
    private String departmentName;

    public Departments(String department_name) {
        this.departmentName = department_name;
    }

}
