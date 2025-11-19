package careerhub.storage;

import careerhub.models.*;
import careerhub.services.dataManagement.ApplicationDataService;
import careerhub.services.dataManagement.CsvPersistenceService;
import careerhub.services.dataManagement.InternshipDataService;
import careerhub.services.dataManagement.UserDataService;

import java.time.LocalDate;
import java.util.*;

/**
 * Central coordinator for all data-related operations.
 *
 * <p>The {@code DataManager} acts as the façade of the storage layer,
 * providing a unified API for the rest of the system (menus and services).
 * Internally, it composes:</p>
 *
 * <ul>
 *     <li>In-memory repositories ({@link UserRepository}, {@link InternshipRepository}, {@link ApplicationRepository})</li>
 *     <li>Data services for domain logic
 *          ({@link UserDataService}, {@link InternshipDataService}, {@link ApplicationDataService})</li>
 *     <li>CSV persistence management ({@link CsvPersistenceService})</li>
 * </ul>
 *
 * <p>All higher-level logic (menus, services) depend only on
 * {@code DataManager}, achieving loose coupling and clean layering.</p>
 */
public class DataManager {

    /** Directory where CSV files are stored. */
    private final String dataDir;

    // Repositories
    /** In-memory storage for users. */
    private final UserRepository userRepo;

    /** In-memory storage for internships. */
    private final InternshipRepository internshipRepo;

    /** In-memory storage for applications. */
    private final ApplicationRepository applicationRepo;

    // Services
    /** User-related data utilities and domain logic. */
    private final UserDataService userService;

    /** Internship lifecycle and filtering logic. */
    private final InternshipDataService internshipService;

    /** Application creation, withdrawal, acceptance handling. */
    private final ApplicationDataService applicationService;

    /** CSV loading and saving for all models. */
    private final CsvPersistenceService csvService;

    /**
     * Constructs a new DataManager and initializes repositories,
     * domain services, and CSV persistence utilities.
     *
     * @param dataDir directory under which CSV files are stored
     */
    public DataManager(String dataDir) {
        this.dataDir = dataDir;

        this.userRepo = new InMemoryUserRepository();
        this.internshipRepo = new InMemoryInternshipRepository();
        this.applicationRepo = new InMemoryApplicationRepository();

        this.userService = new UserDataService(userRepo);
        this.internshipService = new InternshipDataService(internshipRepo);
        this.applicationService = new ApplicationDataService(applicationRepo, internshipRepo, userRepo);
        this.csvService = new CsvPersistenceService(dataDir, userRepo, internshipRepo, applicationRepo);
    }

    // -------------------------------------------------
    // Seed CSV loading (legacy compatibility)
    // -------------------------------------------------

    /**
     * Loads student accounts from a CSV file located under {@code dataDir}.
     *
     * @param filename the CSV file name within the data directory
     */
    public void loadStudentsFromCsv(String filename) {
        csvService.loadStudentsFromCsv(filename);
    }

    /**
     * Loads staff accounts from a CSV file located under {@code dataDir}.
     *
     * @param filename the CSV file name within the data directory
     */
    public void loadStaffFromCsv(String filename) {
        csvService.loadStaffFromCsv(filename);
    }

    // -------------------------------------------------
    // User management (delegated to UserDataService)
    // -------------------------------------------------

    /**
     * Finds a user by ID.
     *
     * @param id user ID
     * @return optional user
     */
    public Optional<User> findUserById(String id) {
        return userService.findUserById(id);
    }

    /**
     * Registers a new company representative.
     *
     * @param rep the representative to save
     */
    public void registerCompanyRep(CompanyRep rep) {
        userService.registerCompanyRep(rep);
    }

    /**
     * Approves or rejects a pending company representative.
     *
     * @param id      the representative's user ID
     * @param approve true to approve, false to reject
     */
    public void approveCompanyRep(String id, boolean approve) {
        userService.approveCompanyRep(id, approve);
    }

    /**
     * Retrieves all pending company representatives.
     *
     * @return list of unapproved company reps
     */
    public List<CompanyRep> getPendingCompanyReps() {
        return userService.getPendingCompanyReps();
    }

    // -------------------------------------------------
    // Internships (delegated to InternshipDataService)
    // -------------------------------------------------

    /**
     * Creates and registers a new internship opportunity.
     *
     * @param it internship object
     * @return generated internship ID
     */
    public String createInternship(Internship it) {
        return internshipService.createInternship(it);
    }

    /**
     * Returns all internships in the system.
     */
    public List<Internship> getAllInternships() {
        return internshipService.getAllInternships();
    }

    /**
     * Retrieves an internship by ID.
     *
     * @param id internship ID
     * @return optional internship
     */
    public Optional<Internship> getInternshipById(String id) {
        return internshipService.getInternshipById(id);
    }

    /**
     * Retrieves visible internships that the student may apply for.
     */
    public List<Internship> getVisibleInternshipsForStudent(
            Student s,
            String statusFilter,
            String prefMajorFilter,
            String levelFilter
    ) {
        return internshipService.getVisibleInternshipsForStudent(
                s, statusFilter, prefMajorFilter, levelFilter
        );
    }

    /**
     * Retrieves internships created by a specific company representative.
     */
    public List<Internship> getInternshipsByCompanyRep(String repId) {
        return internshipService.getInternshipsByCompanyRep(repId);
    }

    /**
     * Retrieves internships pending staff approval.
     */
    public List<Internship> getPendingInternships() {
        return internshipService.getPendingInternships();
    }

    /**
     * Updates the status of an internship and adjusts visibility
     * if needed (e.g., Approved → visible).
     *
     * @param id internship ID
     * @param status new status text
     */
    public void setInternshipStatus(String id, String status) {
        internshipService.setInternshipStatus(id, status);
    }

    /**
     * Applies complex multi-criteria filtering rules to internships.
     *
     * @return filtered list of internships
     */
    public List<Internship> filterInternships(
            String status,
            String major,
            String level,
            String company,
            String visibility,
            String closeDateString
    ) {
        return internshipService.filterInternships(
                status, major, level, company, visibility, closeDateString
        );
    }

    /**
     * Deletes an internship and all related applications.
     *
     * @param id internship ID
     */
    public void deleteInternship(String id) {
        internshipService.deleteInternship(id);
        applicationService.deleteApplicationsByInternshipId(id);
    }

    /**
     * Clears all internships from storage.
     */
    public void clearInternships() {
        internshipService.clearInternships();
    }

    // -------------------------------------------------
    // Applications (delegated to ApplicationDataService)
    // -------------------------------------------------

    /**
     * Creates a new application for the given (internship, student) pair.
     */
    public String createApplication(String internshipId, String studentId) {
        return applicationService.createApplication(internshipId, studentId);
    }

    /**
     * Retrieves all applications submitted by a student.
     */
    public List<Application> getApplicationsByStudent(String studentId) {
        return applicationService.getApplicationsByStudent(studentId);
    }

    /**
     * Retrieves all applications submitted for an internship.
     */
    public List<Application> getApplicationsForInternship(String internshipId) {
        return applicationService.getApplicationsForInternship(internshipId);
    }

    /**
     * Updates the status of an application.
     */
    public void setApplicationStatus(String applicationId, String status) {
        applicationService.setApplicationStatus(applicationId, status);
    }

    /**
     * Handles student acceptance of a successful offer.
     */
    public void studentAcceptPlacement(String studentId, String internshipId) {
        applicationService.studentAcceptPlacement(studentId, internshipId);
    }

    /**
     * Marks an application as having a withdrawal request from the student.
     */
    public void markWithdrawalRequest(String applicationId) {
        applicationService.markWithdrawalRequest(applicationId);
    }

    /**
     * Returns all applications with pending withdrawal requests.
     */
    public List<Application> getWithdrawalRequests() {
        return applicationService.getWithdrawalRequests();
    }

    /**
     * Approves a withdrawal request and updates related data.
     */
    public void approveWithdrawal(String applicationId) {
        applicationService.approveWithdrawal(applicationId);
    }

    /**
     * Removes all applications from storage.
     */
    public void clearApplications() {
        applicationService.clearApplications();
    }

    // -------------------------------------------------
    // Persistence loading
    // -------------------------------------------------

    /**
     * Loads stored CSV files if they exist; otherwise loads seed data.
     */
    public void loadAll() {

        boolean hasUsers = csvService.fileExists("users.csv");
        boolean hasInternships = csvService.fileExists("internships.csv");
        boolean hasApplications = csvService.fileExists("applications.csv");

        if (!hasUsers || !hasInternships || !hasApplications) {
            System.out.println("Missing files → loading seed data.");
            loadStudentsFromCsv("students.csv");
            loadStaffFromCsv("staff.csv");
            return;
        }

        csvService.loadUsersFromFile();
        csvService.loadInternships();
        csvService.loadApplicationsRaw();

        // Restore relationships between models after loading applications
        applicationService.restoreRelationsAfterLoad();
    }

    // -------------------------------------------------
    // Persistence saving
    // -------------------------------------------------

    /**
     * Saves all user data to users.csv.
     */
    public void saveUsers() {
        csvService.saveUsers();
    }

    /**
     * Saves all internships to internships.csv.
     */
    public void saveInternships() {
        csvService.saveInternships();
    }

    /**
     * Saves all applications to applications.csv.
     */
    public void saveApplications() {
        csvService.saveApplications();
    }
}
