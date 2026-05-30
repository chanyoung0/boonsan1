package db;

import model.person.InsuredPerson;

import java.util.List;

public interface InsuredPersonMapper {

    InsuredPerson findById(String residentRegistrationNumber);

    List<InsuredPerson> findAll();

    int insert(InsuredPerson insuredPerson);
}
