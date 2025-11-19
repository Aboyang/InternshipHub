package careerhub.services.dataManagement;

import careerhub.models.*;
import careerhub.storage.ApplicationRepository;
import careerhub.storage.InternshipRepository;
import careerhub.storage.UserRepository;
import careerhub.utils.CSVUtil;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Service responsible for all CSV persistence operations in the system.
 *
 * <p>This class provides a high-level abstraction for:</p>
 * <ul>
 *     <li>Loading initial seed data (students.csv, staff.csv)</li>
 *     <li>Loading persisted files (users.csv, internships.csv, applications.csv)</li>
 *     <li>Saving all system data back to CSV on exit</li>
 *     <li>Ensuring relationships between Users, Internships, and Applications remain valid</li>
 * </ul>
 *
 * <p>This service interacts directly with repositories, keeping DataManager
 * simpler and focused on domain-level operations.</p>
 */
public class CsvPersistenceService {

    /** Path to the data directory where CSV files are stored. */
    private final String dataDir;

    /** Repository responsible for persistence of User objects. */
    private final UserRepository userRepo;

    /** Repository responsible for persistence of Internship objects. */
    private final InternshipRepository internshipRepo;

    /** Repository responsible for persistence of Application objects. */
    private final ApplicationRepository applicationRepo;

    /**
     * Constructs the CSV persistence service.
     *
     * @param dataDir          the base directory containing all CSV files
     * @param userRepo         user repository instance
     * @param internshipRepo   internship repository instance
     * @param applicationRepo  application repository instance
     */
    public CsvPersistenceService(String dataDir,
                                 UserRepository userRepo,
                                 InternshipRepository internshipRepo,
                                 ApplicationRepository applicationRepo) {
        this.dataDir = dataDir;
        this.userRepo = userRepo;
        this.internshipRepo = internshipRepo;
        this.applicationRepo = applicationRepo;
    }

    // ------------ File existence helper ------------

    /**
     * Checks if a file exists within the data directory.
     *
     * @param relativePath file path relative to the data directory
     * @return true if the file exists, false otherwise
     */
    public boolean fileExists(String relativePath) {
        String path = dataDir + "/" + relativePath;
        java.io.File f = new java.io.File(path);
        return f.exists() && f.isFile();
    }

    // ------------ Seed CSV (students.csv / staff.csv) ------------

    /**
     * Loads student records from a seed CSV file (used on first run).
     *
     * @param filename the CSV filename located inside dataDir
     */
    public void loadStudentsFromCsv(String filename) {
        String path = dataDir + "/" + filename;
        List<String[]> rows;
        try {
            rows = CSVUtil.readCsv(path);
        } catch (Exception e) {
            System.out.println("students.csv not found at " + path);
            return;
        }

        for (String[] r : rows) {
            if (r.length < 4) continue;
            String id = r[0].trim();
            String name = r[1].trim();
            String major = r[2].trim();
            int year = 1;
            try {
                year = Integer.parseInt(r[3].trim());
            } catch (Exception ignored) {}
            Student s = new Student(id, name, "password", year, major);
            userRepo.save(s);
        }
    }

    /**
     * Loads staff records from a seed CSV file (used on first run).
     *
     * @param filename the CSV filename located inside dataDir
     */
    public void loadStaffFromCsv(String filename) {
        String path = dataDir + "/" + filename;
        List<String[]> rows;
        try {
            rows = CSVUtil.readCsv(path);
        } catch (Exception e) {
            System.out.println("staff.csv not found at " + path);
            return;
        }

        for (String[] r : rows) {
            if (r.length < 2) continue;
            String id = r[0].trim();
            String name = r[1].trim();
            String dept = r.length >= 4 ? r[3].trim() : "";
            Staff st = new Staff(id, name, "password", dept);
            userRepo.save(st);
        }
    }

    // ------------ Load persisted users.csv ------------

    /**
     * Loads all user records from users.csv.
     * <p>Reconstructs:</p>
     * <ul>
     *     <li>Students and their applied internships</li>
     *     <li>Staff members</li>
     *     <li>Company reps and created internships</li>
     * </ul>
     *
     * @return true if load was successful, false otherwise
     */
    public boolean loadUsersFromFile() {
        String path = dataDir + "/users.csv";
        List<String[]> rows;
        try {
            rows = CSVUtil.readCsv(path, false);
        } catch (Exception e) {
            System.out.println("Failed loading users.csv");
            return false;
        }

        userRepo.clear();

        for (String[] r : rows) {
            if (r.length < 4) continue;

            String type = r[0];
            String id = r[1];
            String name = r[2];
            String pw = r[3];

            switch (type) {
                case "Student": {
                    int year = Integer.parseInt(r[4]);
                    String major = r[5];
                    Student s = new Student(id, name, pw, year, major);

                    // Previously applied internships
                    if (r.length >= 11) {
                        String applied = r[10];
                        if (applied != null && !applied.isBlank()) {
                            for (String iid : applied.split(";")) {
                                if (!iid.isBlank()) s.applySilently(iid);
                            }
                        }
                    }
                    userRepo.save(s);
                    break;
                }

                case "Staff": {
                    String dept = r[6];
                    Staff st = new Staff(id, name, pw, dept);
                    userRepo.save(st);
                    break;
                }

                case "CompanyRep": {
                    String company = r[6];
                    String dept = r[7];
                    String position = r[8];
                    boolean approved = Boolean.parseBoolean(r[9]);

                    CompanyRep rep = new CompanyRep(id, name, pw, company, dept, position);
                    rep.setApproved(approved);
                    rep.dedupeInternships();

                    if (r.length >= 11) {
                        String created = r[10];
                        if (created != null && !created.isBlank()) {
                            for (String iid : created.split(";")) {
                                if (!iid.isBlank()) rep.addCreatedInternship(iid);
                            }
                        }
                    }

                    userRepo.save(rep);
                    break;
                }
            }
        }

        System.out.println("Loaded users from users.csv");
        return true;
    }

    // ------------ Load internships.csv ------------

    /**
     * Loads all internship records from internships.csv.
     *
     * <p>This reconstructs:</p>
     * <ul>
     *     <li>Full Internship objects</li>
     *     <li>Applicant ID lists</li>
     *     <li>Confirmed counts</li>
     *     <li>Status and visibility flags</li>
     *     <li>Links to Company Representatives</li>
     * </ul>
     */
    public void loadInternships() {
        String path = dataDir + "/internships.csv";
        List<String[]> rows;

        try {
            rows = CSVUtil.readCsv(path, false);
        } catch (Exception e) {
            System.out.println("internships.csv missing.");
            return;
        }

        internshipRepo.clear();

        for (String[] r : rows) {
            if (r.length < 14) continue;

            String id = r[0];
            String title = r[1];
            String desc = r[2];
            String level = r[3];
            String pref = r[4];

            LocalDate open = r[5].isBlank() ? null : LocalDate.parse(r[5]);
            LocalDate close = r[6].isBlank() ? null : LocalDate.parse(r[6]);

            String company = r[7];
            String repId = r[8];
            int slots = Integer.parseInt(r[9]);

            boolean visible = Boolean.parseBoolean(r[10]);
            String status = r[11];
            int confirmed = Integer.parseInt(r[12]);
            String applicantsStr = r[13];

            Internship it = new Internship(
                    id, title, desc, level, pref,
                    open, close,
                    company, repId, slots
            );

            it.setVisible(visible);
            it.setStatus(status);
            it.setConfirmedCount(confirmed);

            if (!applicantsStr.isBlank()) {
                for (String s : applicantsStr.split(";")) {
                    if (!s.isBlank()) it.getApplicantIds().add(s);
                }
            }

            internshipRepo.save(it);

            // Reconnect link to company rep
            userRepo.findById(repId).ifPresent(u -> {
                if (u instanceof CompanyRep cr) {
                    cr.addCreatedInternship(id);
                }
            });
        }

        System.out.println("Loaded internships from internships.csv");
    }

    // ------------ Load applications.csv ------------

    /**
     * Loads raw application objects from applications.csv.
     *
     * <p>This method loads only the Application objects themselves.
     * Relationship reconstruction is performed later by:
     * {@link careerhub.services.dataManagement.ApplicationDataService#restoreRelationsAfterLoad()}. </p>

     */
    public void loadApplicationsRaw() {
        String path = dataDir + "/applications.csv";
        List<String[]> rows;

        try {
            rows = CSVUtil.readCsv(path, false);
        } catch (Exception e) {
            System.out.println("applications.csv missing.");
            return;
        }

        applicationRepo.clear();

        for (String[] r : rows) {
            if (r.length < 5) continue;

            String id = r[0];
            String iid = r[1];
            String sid = r[2];
            String status = r[3];
            boolean confirmed = Boolean.parseBoolean(r[4]);

            Application app = new Application(id, iid, sid);
            app.setStatus(status);
            if (confirmed) app.confirmByStudent();

            applicationRepo.save(app);
        }

        System.out.println("Loaded applications from applications.csv");
    }

    // ------------ Save methods ------------

    /**
     * Saves all user records to users.csv.
     * <p>Serializes:</p>
     * <ul>
     *     <li>Student fields (year, major, applied internships)</li>
     *     <li>Staff fields (department)</li>
     *     <li>CompanyRep fields (metadata + created internships)</li>
     * </ul>
     */
    public void saveUsers() {
        String path = dataDir + "/users.csv";
        List<String[]> rows = new ArrayList<>();

        for (User u : userRepo.findAll()) {

            if (u instanceof Student s) {
                String applied = String.join(";", s.getAppliedInternships());

                rows.add(new String[]{
                        "Student", s.getId(), s.getName(), s.getPassword(),
                        String.valueOf(s.getYear()), s.getMajor(),
                        "", "", "", "",
                        applied
                });

            } else if (u instanceof Staff st) {

                rows.add(new String[]{
                        "Staff", st.getId(), st.getName(), st.getPassword(),
                        "", "", st.getDepartment(),
                        "", "", "", ""
                });

            } else if (u instanceof CompanyRep r) {

                String created = String.join(";", r.getCreatedInternshipIds());

                rows.add(new String[]{
                        "CompanyRep", r.getId(), r.getName(), r.getPassword(),
                        "", "", r.getCompanyName(),
                        r.getDepartment(), r.getPosition(),
                        String.valueOf(r.isApproved()),
                        created
                });
            }
        }

        CSVUtil.writeCsv(path, rows);
    }

    /**
     * Saves all internship records to internships.csv.
     */
    public void saveInternships() {
        String path = dataDir + "/internships.csv";
        List<String[]> rows = new ArrayList<>();

        for (Internship it : internshipRepo.findAll()) {
            String applicants = String.join(";", it.getApplicantIds());

            rows.add(new String[]{
                    it.getId(),
                    it.getTitle(),
                    it.getDescription(),
                    it.getLevel(),
                    it.getPreferredMajor(),
                    it.getOpenDate() == null ? "" : it.getOpenDate().toString(),
                    it.getCloseDate() == null ? "" : it.getCloseDate().toString(),
                    it.getCompanyName(),
                    it.getCompanyRepId(),
                    String.valueOf(it.getSlots()),
                    String.valueOf(it.isVisible()),
                    it.getStatus(),
                    String.valueOf(it.getConfirmedCount()),
                    applicants
            });
        }

        CSVUtil.writeCsv(path, rows);
    }

    /**
     * Saves all application records to applications.csv.
     */
    public void saveApplications() {
        String path = dataDir + "/applications.csv";
        List<String[]> rows = new ArrayList<>();

        for (Application a : applicationRepo.findAll()) {
            rows.add(new String[]{
                    a.getId(),
                    a.getInternshipId(),
                    a.getStudentId(),
                    a.getStatus(),
                    String.valueOf(a.isConfirmedByStudent())
            });
        }

        CSVUtil.writeCsv(path, rows);
    }
}
