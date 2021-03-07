package com.epam.rd.autocode.dao;

import com.epam.rd.autocode.domain.Department;
import com.epam.rd.autocode.domain.Employee;

import java.math.BigInteger;
import java.util.List;

public interface EmployeeDao extends Dao<Employee, BigInteger> {
    List<Employee> getByDepartment(Department department);

    List<Employee> getByManager(Employee employee);

    Employee getByIdWithFullChain(BigInteger id);

}
