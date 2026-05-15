package model.partner;

import enums.EvaluationGrade;

// 협력업체 도메인 모델 — 손해조사 위탁 등에 참여하는 외부 업체 정보 관리
public class Partner {

    private String id;
    private String partnerName;
    private String partnerType;
    private String contact;
    private String responsibility;
    private EvaluationGrade evaluationGrade;

    public Partner() {}

    // 협력업체 기본 정보로 초기화
    public Partner(String id, String partnerName, String partnerType, String contact, String responsibility, EvaluationGrade evaluationGrade) {
        this.id = id;
        this.partnerName = partnerName;
        this.partnerType = partnerType;
        this.contact = contact;
        this.responsibility = responsibility;
        this.evaluationGrade = evaluationGrade;
    }

    // 협력업체 등록
    public void register() {}

    // 협력업체 정보 저장
    public void save() {}

    // 협력업체 검색
    public void searchPartner() {}

    // 협력업체 정보 수정
    public void update() {}

    public String getId() { return id; }
    public String getPartnerName() { return partnerName; }
    public String getPartnerType() { return partnerType; }
    public String getContact() { return contact; }
    public String getResponsibility() { return responsibility; }
    public EvaluationGrade getEvaluationGrade() { return evaluationGrade; }

    public void setId(String id) { this.id = id; }
    public void setPartnerName(String partnerName) { this.partnerName = partnerName; }
    public void setPartnerType(String partnerType) { this.partnerType = partnerType; }
    public void setContact(String contact) { this.contact = contact; }
    public void setResponsibility(String responsibility) { this.responsibility = responsibility; }
    public void setEvaluationGrade(EvaluationGrade evaluationGrade) { this.evaluationGrade = evaluationGrade; }

    @Override
    public String toString() {
        return "Partner{id='" + id + "', name='" + partnerName + "', grade=" + evaluationGrade + "}";
    }
}
