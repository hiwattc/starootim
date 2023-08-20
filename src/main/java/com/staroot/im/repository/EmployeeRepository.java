package com.staroot.im.repository;


import com.staroot.im.entity.Employee;
import com.staroot.im.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    Employee findByUserid(String empid);
}
