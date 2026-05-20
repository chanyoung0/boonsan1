package db.mapper;

import model.contract.PaymentCollection;
import org.apache.ibatis.annotations.Param;

import java.util.List;

// 분납/수금 MyBatis Mapper — PaymentCollectionDBO가 위임하는 SQL 인터페이스
public interface PaymentCollectionMapper {

    PaymentCollection findById(String collectionId);

    List<PaymentCollection> findAll();

    List<String> findAllIds();

    String findPolicyNumberById(String collectionId);

    String findStatusById(String collectionId);

    String findTransferTypeById(String collectionId);

    int insert(@Param("pc") PaymentCollection collection,
               @Param("collectionId") String collectionId,
               @Param("policyNumber") String policyNumber,
               @Param("collectionStatus") String collectionStatus,
               @Param("transferType") String transferType,
               @Param("processingResult") String processingResult);

    int update(@Param("pc") PaymentCollection collection,
               @Param("collectionId") String collectionId,
               @Param("policyNumber") String policyNumber,
               @Param("collectionStatus") String collectionStatus,
               @Param("transferType") String transferType,
               @Param("processingResult") String processingResult);

    int delete(String collectionId);
}
