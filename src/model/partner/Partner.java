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

    public void register()       {
        if (this.evaluationGrade == null) {
            this.evaluationGrade = EvaluationGrade.AVERAGE;
        }
    }

    public void save()           {
        register();
    }

    public void searchPartner()  {}

    public void update()         {
        register();
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
