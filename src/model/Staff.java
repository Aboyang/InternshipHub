package model;

public class Staff extends User {
    private String department;

    public Staff(String id, String name, String password, String department, String email) {
        super(id, name, password);
        this.department = department;
    }

    public String getDepartment(){ return department; }

    @Override
    public String getRole(){ return "Staff"; }
}
