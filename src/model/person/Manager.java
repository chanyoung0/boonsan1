package model.person;

// 직원(담당자) 도메인 모델 — 사원번호/이름/부서 정보 관리
public class Manager {

    private String employeeNo;
    private String name;
    private String department;

    public Manager() {}

    // 직원 기본 정보로 초기화
    public Manager(String employeeNo, String name, String department) {
        this.employeeNo = employeeNo;
        this.name = name;
        this.department = department;
    }

    public String getEmployeeNo() { return employeeNo; }
    public String getName() { return name; }
    public String getDepartment() { return department; }

    public void setEmployeeNo(String employeeNo) { this.employeeNo = employeeNo; }
    public void setName(String name) { this.name = name; }
    public void setDepartment(String department) { this.department = department; }

    @Override
    public String toString() {
        return "Manager{employeeNo='" + employeeNo + "', name='" + name + "', dept='" + department + "'}";
    }
}
