package db;

import db.mapper.PaymentCollectionMapper;
import db.mybatis.MyBatisSessionFactory;
import enums.ProcessingResult;
import model.contract.PaymentCollection;
import org.apache.ibatis.session.SqlSession;

import java.util.ArrayList;
import java.util.List;

// PaymentCollection 엔티티 DB 매핑 — payment_collection 테이블 CRUD 담당 (MyBatis 위임)
public class PaymentCollectionDBO extends DBA {

    public PaymentCollection findById(String collectionId) {
        try (SqlSession session = MyBatisSessionFactory.openSession()) {
            return session.getMapper(PaymentCollectionMapper.class).findById(collectionId);
        } catch (Exception e) {
            System.out.println("[DB 오류] 분납수금 조회 실패: " + e.getMessage());
            return null;
        }
    }

    public List<PaymentCollection> findAll() {
        try (SqlSession session = MyBatisSessionFactory.openSession()) {
            return session.getMapper(PaymentCollectionMapper.class).findAll();
        } catch (Exception e) {
            System.out.println("[DB 오류] 분납수금 목록 조회 실패: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    public List<String> findAllIds() {
        try (SqlSession session = MyBatisSessionFactory.openSession()) {
            return session.getMapper(PaymentCollectionMapper.class).findAllIds();
        } catch (Exception e) {
            System.out.println("[DB 오류] 분납수금 ID 목록 조회 실패: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    public String findPolicyNumberById(String collectionId) {
        try (SqlSession session = MyBatisSessionFactory.openSession()) {
            return session.getMapper(PaymentCollectionMapper.class).findPolicyNumberById(collectionId);
        } catch (Exception e) {
            System.out.println("[DB 오류] 분납수금 부가정보 조회 실패: " + e.getMessage());
            return null;
        }
    }

    public String findStatusById(String collectionId) {
        try (SqlSession session = MyBatisSessionFactory.openSession()) {
            String status = session.getMapper(PaymentCollectionMapper.class).findStatusById(collectionId);
            return resolveCollectionStatus(status);
        } catch (Exception e) {
            System.out.println("[DB 오류] 분납수금 부가정보 조회 실패: " + e.getMessage());
            return null;
        }
    }

    public String findTransferTypeById(String collectionId) {
        try (SqlSession session = MyBatisSessionFactory.openSession()) {
            return session.getMapper(PaymentCollectionMapper.class).findTransferTypeById(collectionId);
        } catch (Exception e) {
            System.out.println("[DB 오류] 분납수금 부가정보 조회 실패: " + e.getMessage());
            return null;
        }
    }

    public boolean save(PaymentCollection collection) {
        throw new UnsupportedOperationException("collectionId, policyNumber, collectionStatus, transferType 파라미터가 필요합니다.");
    }

    public boolean save(PaymentCollection collection, String collectionId,
                        String policyNumber, String collectionStatus, String transferType) {
        if (collection == null || collectionId == null || policyNumber == null) {
            return false;
        }
        String processingResult = resolveProcessingResultName(collection);
        String transferTypeName = resolveTransferType(transferType);
        String statusName = resolveCollectionStatus(collectionStatus);
        try (SqlSession session = MyBatisSessionFactory.openSession()) {
            return session.getMapper(PaymentCollectionMapper.class)
                    .insert(collection, collectionId, policyNumber, statusName, transferTypeName, processingResult) > 0;
        } catch (Exception e) {
            System.out.println("[DB 오류] 분납수금 저장 실패: " + e.getMessage());
            return false;
        }
    }

    public boolean update(PaymentCollection collection) {
        throw new UnsupportedOperationException("collectionId, policyNumber, collectionStatus, transferType 파라미터가 필요합니다.");
    }

    public boolean update(PaymentCollection collection, String collectionId,
                          String policyNumber, String collectionStatus, String transferType) {
        if (collection == null || collectionId == null || policyNumber == null) {
            return false;
        }
        String processingResult = resolveProcessingResultName(collection);
        String transferTypeName = resolveTransferType(transferType);
        String statusName = resolveCollectionStatus(collectionStatus);
        try (SqlSession session = MyBatisSessionFactory.openSession()) {
            return session.getMapper(PaymentCollectionMapper.class)
                    .update(collection, collectionId, policyNumber, statusName, transferTypeName, processingResult) > 0;
        } catch (Exception e) {
            System.out.println("[DB 오류] 분납수금 수정 실패: " + e.getMessage());
            return false;
        }
    }

    public boolean delete(String collectionId) {
        try (SqlSession session = MyBatisSessionFactory.openSession()) {
            return session.getMapper(PaymentCollectionMapper.class).delete(collectionId) > 0;
        } catch (Exception e) {
            System.out.println("[DB 오류] 분납수금 삭제 실패: " + e.getMessage());
            return false;
        }
    }

    private String resolveProcessingResultName(PaymentCollection collection) {
        if (collection == null || collection.getProcessingResult() == null) {
            return ProcessingResult.PENDING.name();
        }
        return collection.getProcessingResult().name();
    }

    private String resolveCollectionStatus(String value) {
        if ("COLLECTED".equals(value) || "UNPAID".equals(value)
                || "TRANSFERRED".equals(value) || "CREATED".equals(value)) {
            return value;
        }
        return "CREATED";
    }

    private String resolveTransferType(String value) {
        if ("VISIT_COLLECTION".equals(value) || "TRANSFER".equals(value)) {
            return value;
        }
        return null;
    }
}
