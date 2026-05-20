package db.mapper;

import model.accident.AccidentReport;
import org.apache.ibatis.annotations.Param;

import java.util.List;

// 사고 접수 MyBatis Mapper
public interface AccidentReportMapper {

    AccidentReport findByReportNo(String reportNo);

    List<AccidentReport> findAll();

    String findPolicyNumberByReportNo(String reportNo);

    String findStatusByReportNo(String reportNo);

    String findDocumentSubmissionStatusByReportNo(String reportNo);

    int insert(@Param("ar") AccidentReport report,
               @Param("policyNumber") String policyNumber,
               @Param("accidentStatus") String accidentStatus,
               @Param("documentSubmissionStatus") String documentSubmissionStatus,
               @Param("accidentAtText") String accidentAtText);

    int update(@Param("ar") AccidentReport report,
               @Param("policyNumber") String policyNumber,
               @Param("accidentStatus") String accidentStatus,
               @Param("documentSubmissionStatus") String documentSubmissionStatus,
               @Param("accidentAtText") String accidentAtText);

    int delete(String reportNo);
}
