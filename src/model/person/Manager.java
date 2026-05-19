package model.person;

// 담당자 도메인 모델 — 계약 관리 및 이관 처리 담당자 정보 관리
public class Manager {

    private String employeeNo;
    private String name;
    private String department;

    public Manager() {}

    public Manager(String employeeNo, String name, String department) {
        this.employeeNo = employeeNo;
        this.name = name;
        this.department = department;
    }

    public String getEmployeeNo()              { return employeeNo; }
    public void   setEmployeeNo(String v)      { this.employeeNo = v; }
    public String getName()                    { return name; }
    public void   setName(String v)            { this.name = v; }
    public String getDepartment()              { return department; }
    public void   setDepartment(String v)      { this.department = v; }

    @Override
    public String toString() {
        return "Manager{employeeNo='" + employeeNo + "', name='" + name + "', department='" + department + "'}";
    }
}
