package careerhub.models;

import java.util.ArrayList;
import java.util.List;

public class Student extends User {
    private int year;
    private String major;
    private List<String> appliedInternships = new ArrayList<>();
    private String acceptedInternshipId = null;

    public Student(String id, String name, String password, int year, String major) {
        super(id, name, password);
        this.year = year;
        this.major = major;
    }

    public int getYear() { return year; }
    public String getMajor() { return major; }

    public boolean canApplyLevel(String level) {
        if (year <= 2) return "Basic".equalsIgnoreCase(level);
        return true;
    }

    public boolean canApplyMore() {
        return appliedInternships.size() < 3 && acceptedInternshipId == null;
    }

    public void apply(String internshipId) {
        if (!appliedInternships.contains(internshipId)) appliedInternships.add(internshipId);
    }

    public void withdrawApplication(String internshipId) {
        appliedInternships.remove(internshipId);
    }

    public void acceptPlacement(String internshipId) {
        acceptedInternshipId = internshipId;
    }

    public String getAcceptedInternshipId() { return acceptedInternshipId; }

    public List<String> getAppliedInternships() { return appliedInternships; }

    @Override
    public String getType() { return "Student"; }
}

