package com.epam.rd.autocode.service;

import com.epam.rd.autocode.service.impl.SimpleEmployeeService;

public class ServiceFactory {

    public EmployeeService employeeService(){
        return new SimpleEmployeeService();
    }
}
