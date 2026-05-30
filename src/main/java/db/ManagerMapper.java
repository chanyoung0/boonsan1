package db;

import model.person.Manager;

import java.util.List;

public interface ManagerMapper {

    Manager findById(String employeeNo);

    List<Manager> findAll();

    int insert(Manager manager);
}
