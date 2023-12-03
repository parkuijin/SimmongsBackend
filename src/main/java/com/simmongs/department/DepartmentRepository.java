package com.simmongs.department;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DepartmentRepository extends JpaRepository<Departments, Long> {

    Optional<Departments> findByDepartmentName(String departmentName);

    Departments getByDepartmentName(String departmentName);
}
