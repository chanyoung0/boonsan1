package db;

import model.person.InsuredPerson;

// 피보험자 MyBatis Mapper
public interface InsuredPersonMapper {

    InsuredPerson findById(String residentRegistrationNumber);

    int insert(InsuredPerson insuredPerson);
}
