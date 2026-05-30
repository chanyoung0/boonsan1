package db;

import model.accident.AccidentHistory;

import java.util.List;

public interface AccidentHistoryMapper {

    AccidentHistory findById(String receiptNumber);

    List<AccidentHistory> findByHistoryId(String historyId);

    int insert(AccidentHistory accidentHistory);
}
