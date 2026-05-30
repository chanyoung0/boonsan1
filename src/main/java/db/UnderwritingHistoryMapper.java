package db;

import model.underwriting.UnderwritingHistory;

import java.util.List;

public interface UnderwritingHistoryMapper {

    UnderwritingHistory findById(String historyId);

    List<UnderwritingHistory> findByInsuredPerson(String residentRegistrationNumber);

    int insert(UnderwritingHistory history);
}
