package com.epam.rd.autocode.dao.impl;

import com.epam.rd.autocode.ConnectionSource;
import com.epam.rd.autocode.dao.EmployeeDao;
import com.epam.rd.autocode.domain.Department;
import com.epam.rd.autocode.domain.Employee;
import com.epam.rd.autocode.domain.FullName;
import com.epam.rd.autocode.domain.Position;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SimpleEmployeeDao implements EmployeeDao {
    private static final String SELECT_BY_ID = "SELECT * FROM EMPLOYEE WHERE ID = ?";
    private static final String SELECT_BY_MANAGER = "SELECT * FROM EMPLOYEE WHERE MANAGER = ?";
    private static final String SELECT_BY_DEPARTMENT =
            "SELECT * FROM EMPLOYEE WHERE DEPARTMENT = ?";
    private static final String DELETE = "DELETE FROM EMPLOYEE WHERE ID = ?";
    private static final String INSERT = "INSERT INTO EMPLOYEE VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
    private static final String SELECT_ALL = "SELECT * FROM EMPLOYEE";
    private static final String UPDATE = "UPDATE EMPLOYEE SET "
                                         + "FIRSTNAME = ?, LASTNAME = ?, MIDDLENAME = ?, "
                                         + "POSITION = ?, MANAGER = ?, HIREDATE = ?, "
                                         + "SALARY = ?, DEPARTMENT = ? "
                                         + "WHERE ID = ?";
    private static final int FIRSTNAME_UPDATE_INDEX = 1;
    private static final int LASTNAME_UPDATE_INDEX = 2;
    private static final int MIDDLENAME_UPDATE_INDEX = 3;
    private static final int POSITION_UPDATE_INDEX = 4;
    private static final int MANAGER_UPDATE_INDEX = 5;
    private static final int HIREDATE_UPDATE_INDEX = 6;
    private static final int SALARY_UPDATE_INDEX = 7;
    private static final int DEPARTMENT_UPDATE_INDEX = 8;
    private static final int ID_UPDATE_INDEX = 9;
    private static final int ID_INSERT_INDEX = 1;
    private static final int FIRSTNAME_INSERT_INDEX = 2;
    private static final int LASTNAME_INSERT_INDEX = 3;
    private static final int MIDDLENAME_INSERT_INDEX = 4;
    private static final int POSITION_INSERT_INDEX = 5;
    private static final int MANAGER_INSERT_INDEX = 6;
    private static final int HIREDATE_INSERT_INDEX = 7;
    private static final int SALARY_INSERT_INDEX = 8;
    private static final int DEPARTMENT_INSERT_INDEX = 9;
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_SALARY = "salary";
    private static final String COLUMN_HIREDATE = "hiredate";
    private static final String COLUMN_FIRSTNAME = "firstname";
    private static final String COLUMN_LASTNAME = "lastname";
    private static final String COLUMN_MIDDLENAME = "middlename";
    private static final String COLUMN_POSITION = "position";
    private static final String COLUMN_MANAGER = "manager";
    private static final String COLUMN_DEPARTMENT = "department";


    @Override
    public Employee getById(BigInteger id) {
        Employee employee = null;
        try (Connection connection = ConnectionSource.instance().createConnection();
             PreparedStatement statement = connection.prepareStatement(SELECT_BY_ID)) {
            statement.setLong(1, id.longValue());
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    employee = createEmployee(resultSet);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return employee;
    }

    private Employee createEmployee(ResultSet resultSet) {
        Employee employee = null;
        try {
            Position position = getPosition(resultSet);
            FullName fullName = getFullName(resultSet);
            BigInteger id = BigInteger.valueOf(resultSet.getInt(COLUMN_ID));
            LocalDate hired = getHired(resultSet);
            BigDecimal salary = resultSet.getBigDecimal(COLUMN_SALARY);
            BigInteger managerId = BigInteger.valueOf(resultSet.getInt(COLUMN_MANAGER));
            Employee manager = getManagerById(managerId);
            BigInteger departmentId = BigInteger.valueOf(resultSet.getInt(COLUMN_DEPARTMENT));
            Department department = new SimpleDepartmentDao().getById(departmentId);
            employee = new Employee(id, fullName, position, hired, salary, manager, department);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return employee;
    }

    private Employee getManagerById(BigInteger id) {
        Employee employee = null;
        try (Connection connection = ConnectionSource.instance().createConnection();
             PreparedStatement statement = connection.prepareStatement(SELECT_BY_ID)) {
            statement.setLong(1, id.longValue());
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    employee = createManager(resultSet);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return employee;
    }

    private Employee createManager(ResultSet resultSet) {
        Employee employee = null;
        try {
            Position position = getPosition(resultSet);
            FullName fullName = getFullName(resultSet);
            BigInteger id = BigInteger.valueOf(resultSet.getInt(COLUMN_ID));
            LocalDate hired = getHired(resultSet);
            BigDecimal salary = resultSet.getBigDecimal(COLUMN_SALARY);
            BigInteger departmentId = BigInteger.valueOf(resultSet.getInt(COLUMN_DEPARTMENT));
            Department department = new SimpleDepartmentDao().getById(departmentId);
            employee = new Employee(id, fullName, position, hired, salary, null, department);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return employee;
    }

    private LocalDate getHired(ResultSet resultSet) throws SQLException {
        Date rawDate = resultSet.getDate(COLUMN_HIREDATE);
        return LocalDate.parse(rawDate.toString());
    }

    private FullName getFullName(ResultSet resultSet) throws SQLException {
        String firstName = resultSet.getString(COLUMN_FIRSTNAME);
        String lastName = resultSet.getString(COLUMN_LASTNAME);
        String middleName = resultSet.getString(COLUMN_MIDDLENAME);
        return new FullName(firstName, lastName, middleName);
    }

    private Position getPosition(ResultSet resultSet) throws SQLException {
        String rawPosition = resultSet.getString(COLUMN_POSITION);
        return Position.valueOf(rawPosition.toUpperCase());
    }

    @Override
    public List<Employee> getAll() {
        List<Employee> employees = new ArrayList<>();
        try (Connection connection = ConnectionSource.instance().createConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(SELECT_ALL)) {
            while (resultSet.next()) {
                Employee employee = createEmployee(resultSet);
                employees.add(employee);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return employees;
    }

    @Override
    public Employee save(Employee employee) {
        if (getById(employee.getId()) != null) {
            update(employee);
        } else {
            insert(employee);
        }
        return employee;
    }

    private void insert(Employee employee) {
        try (Connection connection = ConnectionSource.instance().createConnection();
             PreparedStatement statement = connection.prepareStatement(INSERT)) {
            statement.setLong(ID_INSERT_INDEX, employee.getId().longValue());
            statement.setString(FIRSTNAME_INSERT_INDEX, employee.getFullName().getFirstName());
            statement.setString(LASTNAME_INSERT_INDEX, employee.getFullName().getLastName());
            statement.setString(MIDDLENAME_INSERT_INDEX, employee.getFullName().getMiddleName());
            statement.setString(POSITION_INSERT_INDEX, employee.getPosition().toString());
            statement.setLong(MANAGER_INSERT_INDEX, employee.getManager().getId().longValue());
            statement.setDate(HIREDATE_INSERT_INDEX, java.sql.Date.valueOf(employee.getHired()));
            statement.setBigDecimal(SALARY_INSERT_INDEX, employee.getSalary());
            statement.setLong(DEPARTMENT_INSERT_INDEX, employee.getDepartment().getId().longValue());
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void update(Employee employee) {
        try (Connection connection = ConnectionSource.instance().createConnection();
             PreparedStatement statement = connection.prepareStatement(UPDATE)) {
            statement.setString(FIRSTNAME_UPDATE_INDEX, employee.getFullName().getFirstName());
            statement.setString(LASTNAME_UPDATE_INDEX, employee.getFullName().getLastName());
            statement.setString(MIDDLENAME_UPDATE_INDEX, employee.getFullName().getMiddleName());
            statement.setString(POSITION_UPDATE_INDEX, employee.getPosition().toString());
            statement.setLong(MANAGER_UPDATE_INDEX, employee.getManager().getId().longValue());
            statement.setDate(HIREDATE_UPDATE_INDEX, java.sql.Date.valueOf(employee.getHired()));
            statement.setBigDecimal(SALARY_UPDATE_INDEX, employee.getSalary());
            statement.setLong(DEPARTMENT_UPDATE_INDEX, employee.getDepartment().getId().longValue());
            statement.setLong(ID_UPDATE_INDEX, employee.getId().longValue());
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delete(Employee employee) {
        try (Connection connection = ConnectionSource.instance().createConnection();
             PreparedStatement statement = connection.prepareStatement(DELETE)) {
            statement.setLong(1, employee.getId().longValue());
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Employee> getByDepartment(Department department) {
        List<Employee> employees = new ArrayList<>();
        try (Connection connection = ConnectionSource.instance().createConnection();
             PreparedStatement statement = connection.prepareStatement(SELECT_BY_DEPARTMENT)) {
            statement.setLong(1, department.getId().longValue());
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    Employee employee = createEmployee(resultSet);
                    employees.add(employee);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return employees;
    }

    @Override
    public List<Employee> getByManager(Employee manager) {
        List<Employee> employees = new ArrayList<>();
        try (Connection connection = ConnectionSource.instance().createConnection();
             PreparedStatement statement = connection.prepareStatement(SELECT_BY_MANAGER)) {
            statement.setLong(1, manager.getId().longValue());
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    Employee employee = createEmployee(resultSet);
                    employees.add(employee);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return employees;
    }

    @Override
    public Employee getByIdWithFullChain(BigInteger id) {
        Employee employee = null;
        try (Connection connection = ConnectionSource.instance().createConnection();
             PreparedStatement statement = connection.prepareStatement(SELECT_BY_ID)) {
            statement.setLong(1, id.longValue());
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    employee = createEmployeeWithFullChain(resultSet);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return employee;
    }

    private Employee createEmployeeWithFullChain(ResultSet resultSet) {
        Employee employee = null;
        try {
            Position position = getPosition(resultSet);
            FullName fullName = getFullName(resultSet);
            BigInteger id = BigInteger.valueOf(resultSet.getInt(COLUMN_ID));
            LocalDate hired = getHired(resultSet);
            BigDecimal salary = resultSet.getBigDecimal(COLUMN_SALARY);
            BigInteger managerId = BigInteger.valueOf(resultSet.getInt(COLUMN_MANAGER));
            Employee manager = getByIdWithFullChain(managerId);
            BigInteger departmentId = BigInteger.valueOf(resultSet.getInt(COLUMN_DEPARTMENT));
            Department department = new SimpleDepartmentDao().getById(departmentId);
            employee = new Employee(id, fullName, position, hired, salary, manager, department);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return employee;
    }
}
