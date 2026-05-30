package db;

import model.document.AccidentDocument;
import model.document.Document;
import model.document.PaymentApprovalDocument;

import java.util.List;

public interface DocumentMapper {

    Document findById(String documentId);

    List<Document> findAll();

    int insertAccidentDocument(AccidentDocument document);

    int insertPaymentApprovalDocument(PaymentApprovalDocument document);
}
