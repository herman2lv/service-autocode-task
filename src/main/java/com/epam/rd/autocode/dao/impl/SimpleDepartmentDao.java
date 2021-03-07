package com.epam.rd.autocode.dao.impl;

import com.epam.rd.autocode.ConnectionSource;
import com.epam.rd.autocode.dao.DepartmentDao;
import com.epam.rd.autocode.domain.Department;

import java.math.BigInteger;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class SimpleDepartmentDao implements DepartmentDao {
    private static final String SELECT_BY_ID = "SELECT * FROM DEPARTMENT WHERE ID = ?";
    private static final String DELETE = "DELETE FROM DEPARTMENT WHERE ID = ?";
    private static final String INSERT = "INSERT INTO DEPARTMENT VALUES (?, ?, ?)";
    private static final String SELECT_ALL = "SELECT * FROM DEPARTMENT";
    private static final String UPDATE =
            "UPDATE DEPARTMENT SET NAME = ?, LOCATION = ? WHERE ID = ?";
    private static final int ID_INSERT_INDEX = 1;
    private static final int NAME_INSERT_INDEX = 2;
    private static final int LOCATION_INSERT_INDEX = 3;
    private static final String ID_COLUMN = "id";
    private static final String NAME_COLUMN = "name";
    private static final String LOCATION_COLUMN = "location";

    @Override
    public Department getById(BigInteger id) {
        Department department = null;
        try (Connection connection = ConnectionSource.instance().createConnection();
             PreparedStatement statement = connection.prepareStatement(SELECT_BY_ID)) {
            statement.setLong(1, id.longValue());
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    department = createDepartment(resultSet);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return department;
    }

    private Department createDepartment(ResultSet resultSet) {
        Department department = null;
        try {
            BigInteger id = BigInteger.valueOf(resultSet.getInt(ID_COLUMN));
            String name = resultSet.getString(NAME_COLUMN);
            String location = resultSet.getString(LOCATION_COLUMN);
            department = new Department(id, name, location);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return department;
    }

    @Override
    public List<Department> getAll() {
        List<Department> departments = new ArrayList<>();
        try (Connection connection = ConnectionSource.instance().createConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(SELECT_ALL)) {
            while (resultSet.next()) {
                Department department = createDepartment(resultSet);
                departments.add(department);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return departments;
    }

    @Override
    public Department save(Department department) {
        if (getById(department.getId()) != null) {
            update(department);
        } else {
            insert(department);
        }
        return department;
    }

    private void insert(Department department) {
        try (Connection connection = ConnectionSource.instance().createConnection();
             PreparedStatement statement = connection.prepareStatement(INSERT)) {
            statement.setLong(ID_INSERT_INDEX, department.getId().longValue());
            statement.setString(NAME_INSERT_INDEX, department.getName());
            statement.setString(LOCATION_INSERT_INDEX, department.getLocation());
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void update(Department department) {
        try (Connection connection = ConnectionSource.instance().createConnection();
             PreparedStatement statement = connection.prepareStatement(UPDATE)) {
            statement.setString(1, department.getName());
            statement.setString(2, department.getLocation());
            statement.setLong(3, department.getId().longValue());
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delete(Department department) {
        try (Connection connection = ConnectionSource.instance().createConnection();
             PreparedStatement statement = connection.prepareStatement(DELETE)) {
            statement.setLong(1, department.getId().longValue());
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
