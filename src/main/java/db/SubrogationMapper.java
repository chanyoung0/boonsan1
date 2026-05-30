package db;

import model.accident.Subrogation;

import java.util.List;

public interface SubrogationMapper {

    Subrogation findById(String subrogationId);

    List<Subrogation> findAll();

    String findStatusById(String subrogationId);

    int insert(Subrogation subrogation);
}
