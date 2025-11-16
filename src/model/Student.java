package model;

public class Student extends User {
    private String major;
    private int year;
    private String email; // added
    // saved filters per requirement
    private String savedFilters = "";

    public Student(String id, String name, String password, String major, int year, String email) {
        super(id, name, password);
        this.major = major;
        this.year = year;
        this.email = email;
    }

    public String getMajor(){ return major; }
    public int getYear(){ return year; }

    public String getEmail(){ return email; } // getter
    public void setEmail(String email){ this.email = email; } // optional setter

    public String getSavedFilters(){ return savedFilters; }
    public void setSavedFilters(String f){ savedFilters = f; }

    @Override
    public String getRole(){ return "Student"; }
}
