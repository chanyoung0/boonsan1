package service.partner;

import enums.EvaluationGrade;
import model.partner.Partner;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class PartnerService {

    private static final Map<String, Partner> partnerMap = new LinkedHashMap<>();

    public static Partner registerPartner(String id, String partnerName, String partnerType,
                                          String contact, String responsibility,
                                          EvaluationGrade evaluationGrade) {
        String partnerId = isBlank(id) ? generatePartnerId() : id;
        Partner partner = new Partner(partnerId, partnerName, partnerType, contact,
                responsibility, evaluationGrade);
        partner.register();
        partner.save();
        partnerMap.put(partnerId, partner);
        return partner;
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
        partnerMap.put(id, partner);
        return partner;
    }

    public static Partner findPartnerById(String id) {
        Partner partner = partnerMap.get(id);
        if (partner != null) {
            partner.searchPartner();
        }
        return partner;
    }

    public static List<Partner> getPartnerList() {
        return new ArrayList<>(partnerMap.values());
    }

    public static List<Partner> searchPartners(String partnerName, String partnerType,
                                               EvaluationGrade evaluationGrade,
                                               String availabilityStatus) {
        List<Partner> result = new ArrayList<>();
        for (Partner partner : partnerMap.values()) {
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
        for (Partner partner : partnerMap.values()) {
            if (partner.getEvaluationGrade() != EvaluationGrade.SUSPENDED) {
                availablePartnerList.add(partner);
            }
        }
        return availablePartnerList;
    }

    public static boolean isAvailable(Partner partner) {
        return partner != null && partner.getEvaluationGrade() != EvaluationGrade.SUSPENDED;
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
