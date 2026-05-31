package db;

import model.contract.Transfer;
import org.apache.ibatis.annotations.Param;

import java.util.List;

// 이관 MyBatis Mapper
public interface TransferMapper {

    Transfer findById(@Param("transferId") String transferId);

    List<Transfer> findByCollectionId(@Param("collectionId") String collectionId);

    int insert(@Param("tr") Transfer transfer,
               @Param("transferId") String transferId,
               @Param("collectionId") String collectionId,
               @Param("employeeNo") String employeeNo);
}
