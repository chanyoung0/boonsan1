package db;

import model.underwriting.UnderwritingRequest;

import java.util.List;

public interface UnderwritingRequestMapper {

    UnderwritingRequest findById(String requestId);

    List<UnderwritingRequest> findAll();

    int insert(UnderwritingRequest request);
}
