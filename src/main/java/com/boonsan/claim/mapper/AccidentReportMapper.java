package com.boonsan.claim.mapper;

import com.boonsan.model.accident.AccidentReport;
import org.apache.ibatis.annotations.Param;

public interface AccidentReportMapper {

    void insertAccidentReport(AccidentReport accidentReport);

    AccidentReport findByAccidentNumber(@Param("accidentNumber") String accidentNumber);
}
