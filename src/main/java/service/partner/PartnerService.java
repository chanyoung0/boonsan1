package service.partner;

import db.mapper.PartnerMapper;
import db.mybatis.MyBatisSessionFactory;
import enums.EvaluationGrade;
import model.partner.Partner;
import org.apache.ibatis.session.SqlSession;

import java.util.ArrayList;
import java.util.List;

public class PartnerService {

    public static Partner registerPartner(String id, String partnerName, String partnerType,
                                          String contact, String responsibility,
                                          EvaluationGrade evaluationGrade) {
        String partnerId = isBlank(id) ? generatePartnerId() : id;
        Partner partner = new Partner(partnerId, partnerName, partnerType, contact,
                responsibility, evaluationGrade);
        partner.register();
        partner.save();
        return savePartner(partner) ? partner : null;
    }

    public static Partner updatePartner(String id, String partnerName, String partnerType,
                                        String contact, String responsibility,
                                        EvaluationGrade evaluationGrade) {
        Partner partner = findPartnerById(id);
        if (partner == null) {
            return null;
        }

        if (!isBlank(partnerName)) {
            partner.setPartnerName(partnerName);
        }
        if (!isBlank(partnerType)) {
            partner.setPartnerType(partnerType);
        }
        if (!isBlank(contact)) {
            partner.setContact(contact);
        }
        if (!isBlank(responsibility)) {
            partner.setResponsibility(responsibility);
        }
        if (evaluationGrade != null) {
            partner.setEvaluationGrade(evaluationGrade);
        }
        partner.update();
        return updatePartnerInternal(partner) ? partner : null;
    }

    public static Partner findPartnerById(String id) {
        Partner partner;
        try (SqlSession s = MyBatisSessionFactory.openSession()) {
            partner = s.getMapper(PartnerMapper.class).findById(id);
        } catch (Exception e) {
            System.out.println("[DB 오류] 협력업체 조회 실패: " + e.getMessage());
            return null;
        }
        if (partner != null) {
            partner.searchPartner();
        }
        return partner;
    }

    public static List<Partner> getPartnerList() {
        try (SqlSession s = MyBatisSessionFactory.openSession()) {
            return s.getMapper(PartnerMapper.class).findAll();
        } catch (Exception e) {
            System.out.println("[DB 오류] 협력업체 목록 조회 실패: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    public static List<Partner> searchPartners(String partnerName, String partnerType,
                                               EvaluationGrade evaluationGrade,
                                               String availabilityStatus) {
        List<Partner> result = new ArrayList<>();
        for (Partner partner : getPartnerList()) {
            if (!containsText(partner.getPartnerName(), partnerName)) {
                continue;
            }
            if (!containsText(partner.getPartnerType(), partnerType)) {
                continue;
            }
            if (evaluationGrade != null && partner.getEvaluationGrade() != evaluationGrade) {
                continue;
            }
            if (!matchesAvailability(partner, availabilityStatus)) {
                continue;
            }
            result.add(partner);
        }
        return result;
    }

    public static List<Partner> getAvailablePartnerList() {
        List<Partner> availablePartnerList = new ArrayList<>();
        for (Partner partner : getPartnerList()) {
            if (partner.getEvaluationGrade() != EvaluationGrade.SUSPENDED) {
                availablePartnerList.add(partner);
            }
        }
        return availablePartnerList;
    }

    public static boolean isAvailable(Partner partner) {
        return partner != null && partner.getEvaluationGrade() != EvaluationGrade.SUSPENDED;
    }

    private static boolean savePartner(Partner partner) {
        if (partner == null) {
            return false;
        }
        applyDefaultEvaluationGrade(partner);
        try (SqlSession s = MyBatisSessionFactory.openSession()) {
            return s.getMapper(PartnerMapper.class).insert(partner) > 0;
        } catch (Exception e) {
            System.out.println("[DB 오류] 협력업체 등록 실패: " + e.getMessage());
            return false;
        }
    }

    private static boolean updatePartnerInternal(Partner partner) {
        if (partner == null) {
            return false;
        }
        applyDefaultEvaluationGrade(partner);
        try (SqlSession s = MyBatisSessionFactory.openSession()) {
            return s.getMapper(PartnerMapper.class).update(partner) > 0;
        } catch (Exception e) {
            System.out.println("[DB 오류] 협력업체 수정 실패: " + e.getMessage());
            return false;
        }
    }

    private static void applyDefaultEvaluationGrade(Partner partner) {
        if (partner.getEvaluationGrade() == null) {
            partner.setEvaluationGrade(EvaluationGrade.AVERAGE);
        }
    }

    private static String generatePartnerId() {
        return "PT-" + System.currentTimeMillis();
    }

    private static boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    private static boolean containsText(String source, String condition) {
        if (isBlank(condition)) {
            return true;
        }
        if (source == null) {
            return false;
        }
        return source.toLowerCase().contains(condition.trim().toLowerCase());
    }

    private static boolean matchesAvailability(Partner partner, String availabilityStatus) {
        if (isBlank(availabilityStatus)) {
            return true;
        }
        if ("AVAILABLE".equals(availabilityStatus)) {
            return isAvailable(partner);
        }
        if ("SUSPENDED".equals(availabilityStatus)) {
            return !isAvailable(partner);
        }
        return true;
    }
}
