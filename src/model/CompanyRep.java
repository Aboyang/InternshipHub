package model;

public class CompanyRep extends User {
    private String companyName;
    private String department;
    private String position;
    private boolean approved = false;
    private String email;

    public CompanyRep(String id, String name, String password, String companyName, String department, String position, String email) {
        super(id, name, password);
        this.companyName = companyName;
        this.department = department;
        this.position = position;
        this.email = email;
    }

    public String getCompanyName(){ return companyName; }
    public String getDepartment(){ return department; }
    public String getPosition(){ return position; }
    public String getEmail(){ return email; }

    public boolean isApproved(){ return approved; }
    public void setApproved(boolean a){ approved = a; }

    @Override
    public String getRole(){ return "CompanyRep"; }
}
