package careerhub.storage;

import careerhub.models.*;
import careerhub.utils.CSVUtil;

import java.io.IOException;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

public class DataManager {
    private final String dataDir;

    // in-memory storage
    private final Map<String, User> users = new HashMap<>();
    private final Map<String, Internship> internships = new HashMap<>();
    private final Map<String, Application> applications = new HashMap<>();

    // simple counters for ids
    private int internCounter = 0;
    private int appCounter = 0;

    public DataManager(String dataDir) {
        this.dataDir = dataDir;
    }

    // -------- CSV loading (students & staff) ----------
    public void loadStudentsFromCsv(String filename) {
        String path = dataDir + "/" + filename;
        List<String[]> rows;
        try {
            rows = CSVUtil.readCsv(path);
        } catch (IOException e) { System.out.println("students.csv not found at " + path); return; }
        // Expect header: StudentID,Name,Major,Year,Email
        for (String[] r : rows) {
            if (r.length < 4) continue;
            String id = r[0].trim();
            String name = r[1].trim();
            String major = r[2].trim();
            int year = 1;
            try { year = Integer.parseInt(r[3].trim()); } catch (Exception ignored) {}
            Student s = new Student(id, name, "password", year, major);
            users.put(id, s);
        }
        System.out.println("Loaded " + rows.size() + " students (from " + path + ")");
    }

    public void loadStaffFromCsv(String filename) {
        String path = dataDir + "/" + filename;
        List<String[]> rows;
        try {
            rows = CSVUtil.readCsv(path);
        } catch (IOException e) { System.out.println("staff.csv not found at " + path); return; }
        // Expect header: StaffID,Name,Role,Department,Email
        for (String[] r : rows) {
            if (r.length < 2) continue;
            String id = r[0].trim();
            String name = r[1].trim();
            String dept = r.length >= 4 ? r[3].trim() : "";
            Staff st = new Staff(id, name, "password", dept);
            users.put(id, st);
        }
        System.out.println("Loaded " + rows.size() + " staff (from " + path + ")");
    }

    // -------- user management ----------
    public Optional<User> findUserById(String id) {
        return Optional.ofNullable(users.get(id));
    }

    public void registerCompanyRep(CompanyRep rep) {
        users.put(rep.getId(), rep);
    }

    public void approveCompanyRep(String id, boolean approve) {
        User u = users.get(id);
        if (u instanceof CompanyRep) ((CompanyRep)u).setApproved(approve);
    }

    public List<CompanyRep> getPendingCompanyReps() {
        return users.values().stream().filter(u -> u instanceof CompanyRep)
                .map(u->(CompanyRep)u).filter(r->!r.isApproved()).collect(Collectors.toList());
    }

    // -------- internships ----------
    public String createInternship(Internship it) {
        internCounter++;
        String id = "I" + internCounter;
        it.setId(id);
        internships.put(id, it);
        return id;
    }

    public Optional<Internship> getInternshipById(String id) {
        return Optional.ofNullable(internships.get(id));
    }

    public List<Internship> getVisibleInternshipsForStudent(Student s, String statusFilter, String prefMajorFilter, String levelFilter) {
        LocalDate today = LocalDate.now();
        return internships.values().stream().filter(it -> {
            if (!it.isVisible()) return false;
            if (!"Approved".equalsIgnoreCase(it.getStatus())) return false;
            if (it.getOpenDate() != null && today.isBefore(it.getOpenDate())) return false;
            if (it.getCloseDate() != null && today.isAfter(it.getCloseDate())) return false;
            if (!statusFilter.isBlank() && !it.getStatus().equalsIgnoreCase(statusFilter)) return false;
            if (!prefMajorFilter.isBlank() && !it.getPreferredMajor().equalsIgnoreCase(prefMajorFilter)) return false;
            if (!levelFilter.isBlank() && !it.getLevel().equalsIgnoreCase(levelFilter)) return false;
            return true;
        }).sorted(Comparator.comparing(Internship::getTitle)).collect(Collectors.toList());
    }

    public List<Internship> getInternshipsByCompanyRep(String repId) {
        return internships.values().stream().filter(i -> repId.equals(i.getCompanyRepId())).collect(Collectors.toList());
    }

    public List<Internship> getPendingInternships() {
        return internships.values().stream().filter(i -> "Pending".equalsIgnoreCase(i.getStatus())).collect(Collectors.toList());
    }

    public void setInternshipStatus(String id, String status) {
        Internship it = internships.get(id);
        if (it != null) {
            it.setStatus(status);
            if ("Approved".equalsIgnoreCase(status)) it.setVisible(true);
        }
    }

    // -------- applications ----------
    public String createApplication(String internshipId, String studentId) {
        appCounter++;
        String id = "A" + appCounter;
        Application a = new Application(id, internshipId, studentId);
        applications.put(id, a);
        Internship it = internships.get(internshipId);
        if (it != null) it.getApplicantIds().add(studentId);
        return id;
    }

    public List<Application> getApplicationsByStudent(String studentId) {
        return applications.values().stream().filter(a->a.getStudentId().equals(studentId)).collect(Collectors.toList());
    }

    public List<Application> getApplicationsForInternship(String internshipId) {
        return applications.values().stream().filter(a->a.getInternshipId().equals(internshipId)).collect(Collectors.toList());
    }

    public void setApplicationStatus(String applicationId, String status) {
        Application a = applications.get(applicationId);
        if (a != null) {
            a.setStatus(status);
            if ("Successful".equalsIgnoreCase(status)) {
                // nothing auto here; student must accept placement
            }
        }
    }

    public void studentAcceptPlacement(String studentId, String internshipId) {
        for (Application a : applications.values()) {
            if (a.getStudentId().equals(studentId) && a.getInternshipId().equals(internshipId) && "Successful".equalsIgnoreCase(a.getStatus())) {
                a.confirmByStudent();
                Internship it = internships.get(internshipId);
                if (it != null) it.incrementConfirmed();
                // set other applications of student to Unsuccessful
                for (Application other : getApplicationsByStudent(studentId)) {
                    if (!other.getId().equals(a.getId())) other.setStatus("Unsuccessful");
                }
                break;
            }
        }
    }

    // withdrawal handling
    public void markWithdrawalRequest(String applicationId) {
        Application a = applications.get(applicationId);
        if (a != null) a.requestWithdrawal();
    }

    public List<Application> getWithdrawalRequests() {
        return applications.values().stream().filter(Application::isWithdrawalRequested).collect(Collectors.toList());
    }

    public void approveWithdrawal(String applicationId) {
        Application a = applications.get(applicationId);
        if (a != null) {
            a.setStatus("Unsuccessful");
            // optionally remove applicant from internship applicant list
            Internship it = internships.get(a.getInternshipId());
            if (it != null) it.getApplicantIds().remove(a.getStudentId());
        }
    }
}
