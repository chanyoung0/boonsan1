package db;

import model.contract.Transfer;

import java.util.List;

public interface TransferMapper {

    Transfer findById(String transferId);

    List<Transfer> findAll();

    int insert(Transfer transfer);
}
