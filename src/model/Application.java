package model;

import model.enums.ApplicationStatus;
import java.time.LocalDate;

public class Application {
    private static int NEXT_ID = 5000;
    private final String id;
    private String internshipId;
    private String studentId;
    private ApplicationStatus status = ApplicationStatus.Pending;
    private LocalDate appliedAt = LocalDate.now();

    public Application(String internshipId, String studentId){
        this.id = "A" + NEXT_ID++;
        this.internshipId = internshipId;
        this.studentId = studentId;
    }

    public String getId(){ return id; }
    public String getInternshipId(){ return internshipId; }
    public String getStudentId(){ return studentId; }
    public ApplicationStatus getStatus(){ return status; }
    public LocalDate getAppliedAt(){ return appliedAt; }

    public void setStatus(ApplicationStatus s){ status = s; }
}
