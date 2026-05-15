package repository;

import model.person.InsuredPerson;

import java.util.List;
import java.util.Optional;

// 피보험자 저장소 인터페이스
public interface InsuredPersonRepository {

    Optional<InsuredPerson> findByRRN(String residentRegistrationNumber);

    Optional<InsuredPerson> findByName(String name);

    List<InsuredPerson> findAll();

    InsuredPerson save(InsuredPerson person);
}
