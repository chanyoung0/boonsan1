package db;

import model.document.AccidentDocument;
import model.document.Document;
import model.document.PaymentApprovalDocument;
import org.apache.ibatis.annotations.Param;

import java.util.List;

// 문서 MyBatis Mapper — single-table 상속, discriminator로 AccidentDocument / PaymentApprovalDocument 분기
public interface DocumentMapper {

    Document findById(@Param("documentId") String documentId);

    List<Document> findByParentId(@Param("parentId") String parentId);

    int insertAccidentDocument(@Param("doc") AccidentDocument doc,
                               @Param("documentId") String documentId,
                               @Param("parentId") String parentId);

    int insertPaymentApprovalDocument(@Param("doc") PaymentApprovalDocument doc,
                                      @Param("documentId") String documentId,
                                      @Param("parentId") String parentId);
}
