package model.partner;

import enums.EvaluationGrade;

// 협력업체 도메인 모델 — 손해조사 위탁 협력업체 정보 관리
public class Partner {

    private String contact;
    private EvaluationGrade evaluationGrade;
    private String id;
    private String partnerName;
    private String partnerType;
    private String responsibility;

    public Partner() {}

    public Partner(String id, String partnerName, String partnerType, String contact,
                   String responsibility, EvaluationGrade evaluationGrade) {
        this.id = id;
        this.partnerName = partnerName;
        this.partnerType = partnerType;
        this.contact = contact;
        this.responsibility = responsibility;
        this.evaluationGrade = evaluationGrade;
    }

    // 협력업체 등록
    public void register() {
        if (id == null || id.isEmpty())
            throw new IllegalArgumentException("협력업체 ID가 필요합니다.");
        if (partnerName == null || partnerName.isEmpty())
            throw new IllegalArgumentException("협력업체명이 필요합니다.");
    }

    // 협력업체 저장
    public void save() {
        if (id == null || id.isEmpty())
            throw new IllegalArgumentException("협력업체 ID가 필요합니다.");
        if (partnerType == null || partnerType.isEmpty())
            throw new IllegalArgumentException("협력업체 유형이 필요합니다.");
    }

    // 협력업체 조회
    public void searchPartner() {
        if (id == null || id.isEmpty())
            throw new IllegalStateException("협력업체 ID가 없습니다.");
    }

    // 협력업체 정보 수정
    public void update() {
        if (id == null || id.isEmpty())
            throw new IllegalStateException("수정할 협력업체 ID가 없습니다.");
    }

    public String          getId()                     { return id; }
    public void            setId(String v)             { this.id = v; }
    public String          getPartnerName()            { return partnerName; }
    public void            setPartnerName(String v)    { this.partnerName = v; }
    public String          getPartnerType()            { return partnerType; }
    public void            setPartnerType(String v)    { this.partnerType = v; }
    public String          getContact()                { return contact; }
    public void            setContact(String v)        { this.contact = v; }
    public String          getResponsibility()         { return responsibility; }
    public void            setResponsibility(String v) { this.responsibility = v; }
    public EvaluationGrade getEvaluationGrade()        { return evaluationGrade; }
    public void            setEvaluationGrade(EvaluationGrade v) { this.evaluationGrade = v; }

    @Override
    public String toString() {
        return "Partner{id='" + id + "', partnerName='" + partnerName + "', partnerType='" + partnerType + "'}";
    }
}
