package db;

import model.underwriting.UnderwritingResult;

import java.util.List;

public interface UnderwritingResultMapper {

    UnderwritingResult findById(String resultId);

    List<UnderwritingResult> findAll();

    int insert(UnderwritingResult result);
}
