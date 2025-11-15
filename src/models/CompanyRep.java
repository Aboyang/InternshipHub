package careerhub.models;

import java.util.ArrayList;
import java.util.List;

public class CompanyRep extends User {
    private String companyName;
    private String department;
    private String position;
    private boolean approved = false;
    private List<String> internships = new ArrayList<>();

    public CompanyRep(String id, String name, String password, String companyName, String department, String position) {
        super(id, name, password);
        this.companyName = companyName;
        this.department = department;
        this.position = position;
    }

    public String getCompanyName() { return companyName; }
    public String getDepartment() { return department; }
    public String getPosition() { return position; }

    public boolean isApproved() { return approved; }
    public void setApproved(boolean v) { approved = v; }

    public boolean canCreateMoreInternships() { return internships.size() < 5; }
    public void addCreatedInternship(String id) { internships.add(id); }

    public List<String> getCreatedInternshipIds() { return internships; }

    @Override
    public String getType() { return "CompanyRep"; }
}
