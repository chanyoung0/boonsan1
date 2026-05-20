package db;

import db.mapper.MaturityNoticeMapper;
import db.mybatis.MyBatisSessionFactory;
import enums.DeliveryMethod;
import model.contract.MaturityNotice;
import org.apache.ibatis.session.SqlSession;

import java.util.ArrayList;
import java.util.List;

// MaturityNotice 엔티티 DB 매핑 — maturity_notice 테이블 CRUD 담당 (MyBatis 위임)
public class MaturityNoticeDBO extends DBA {

    public MaturityNotice findById(String noticeId) {
        try (SqlSession session = MyBatisSessionFactory.openSession()) {
            return session.getMapper(MaturityNoticeMapper.class).findById(noticeId);
        } catch (Exception e) {
            System.out.println("[DB 오류] 만기 안내 조회 실패: " + e.getMessage());
            return null;
        }
    }

    public List<MaturityNotice> findAll() {
        try (SqlSession session = MyBatisSessionFactory.openSession()) {
            return session.getMapper(MaturityNoticeMapper.class).findAll();
        } catch (Exception e) {
            System.out.println("[DB 오류] 만기 안내 목록 조회 실패: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    public List<String> findAllIds() {
        try (SqlSession session = MyBatisSessionFactory.openSession()) {
            return session.getMapper(MaturityNoticeMapper.class).findAllIds();
        } catch (Exception e) {
            System.out.println("[DB 오류] 만기 안내 ID 목록 조회 실패: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    public boolean save(MaturityNotice notice) {
        throw new UnsupportedOperationException("noticeId 파라미터가 필요합니다.");
    }

    public boolean save(MaturityNotice notice, String noticeId) {
        if (notice == null || noticeId == null) {
            return false;
        }
        String deliveryMethod = resolveDeliveryMethodName(notice);
        String renewalIntention = resolveRenewalIntention(notice.getRenewalIntention());
        try (SqlSession session = MyBatisSessionFactory.openSession()) {
            return session.getMapper(MaturityNoticeMapper.class)
                    .insert(notice, noticeId, deliveryMethod, renewalIntention) > 0;
        } catch (Exception e) {
            System.out.println("[DB 오류] 만기 안내 저장 실패: " + e.getMessage());
            return false;
        }
    }

    public boolean update(MaturityNotice notice) {
        throw new UnsupportedOperationException("noticeId 파라미터가 필요합니다.");
    }

    public boolean update(MaturityNotice notice, String noticeId) {
        if (notice == null || noticeId == null) {
            return false;
        }
        String deliveryMethod = resolveDeliveryMethodName(notice);
        String renewalIntention = resolveRenewalIntention(notice.getRenewalIntention());
        try (SqlSession session = MyBatisSessionFactory.openSession()) {
            return session.getMapper(MaturityNoticeMapper.class)
                    .update(notice, noticeId, deliveryMethod, renewalIntention) > 0;
        } catch (Exception e) {
            System.out.println("[DB 오류] 만기 안내 수정 실패: " + e.getMessage());
            return false;
        }
    }

    public boolean delete(String noticeId) {
        try (SqlSession session = MyBatisSessionFactory.openSession()) {
            return session.getMapper(MaturityNoticeMapper.class).delete(noticeId) > 0;
        } catch (Exception e) {
            System.out.println("[DB 오류] 만기 안내 삭제 실패: " + e.getMessage());
            return false;
        }
    }

    public String findRenewalIntentionById(String noticeId) {
        try (SqlSession session = MyBatisSessionFactory.openSession()) {
            return session.getMapper(MaturityNoticeMapper.class).findRenewalIntentionById(noticeId);
        } catch (Exception e) {
            System.out.println("[DB 오류] 만기 안내 부가정보 조회 실패: " + e.getMessage());
            return null;
        }
    }

    private String resolveDeliveryMethodName(MaturityNotice notice) {
        if (notice == null || notice.getDeliveryMethod() == null) {
            return DeliveryMethod.SMS.name();
        }
        return notice.getDeliveryMethod().name();
    }

    private String resolveRenewalIntention(Boolean value) {
        if (value == null) return null;
        return value ? "YES" : "NO";
    }
}
