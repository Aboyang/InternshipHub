package careerhub.services.dataManagement;

import careerhub.models.Application;
import careerhub.models.Internship;
import careerhub.models.Student;
import careerhub.models.User;
import careerhub.storage.ApplicationRepository;
import careerhub.storage.InternshipRepository;
import careerhub.storage.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service layer responsible for higher-level application management
 * operations, sitting above the repository layer.
 *
 * <p>This class coordinates data across multiple repositories
 * ({@link ApplicationRepository}, {@link InternshipRepository},
 * {@link UserRepository}) and provides domain-level behaviours
 * used by menus and workflow services.</p>
 *
 * <p>Responsibilities include:</p>
 * <ul>
 *     <li>Creating applications and maintaining cross-entity consistency</li>
 *     <li>Fetching applications by student or internship</li>
 *     <li>Updating application status</li>
 *     <li>Handling student acceptance workflow</li>
 *     <li>Processing withdrawal requests</li>
 *     <li>Synchronising relations after CSV loading</li>
 * </ul>
 */
public class ApplicationDataService {

    /** Repository responsible for storing and retrieving Application records. */
    private final ApplicationRepository appRepo;

    /** Repository responsible for storing and retrieving Internship records. */
    private final InternshipRepository internshipRepo;

    /** Repository responsible for storing and retrieving User/Student records. */
    private final UserRepository userRepo;

    /**
     * Constructs the ApplicationDataService with required repositories.
     *
     * @param appRepo         repository for Application entities
     * @param internshipRepo  repository for Internship entities
     * @param userRepo        repository for User entities
     */
    public ApplicationDataService(ApplicationRepository appRepo,
                                  InternshipRepository internshipRepo,
                                  UserRepository userRepo) {
        this.appRepo = appRepo;
        this.internshipRepo = internshipRepo;
        this.userRepo = userRepo;
    }

    /**
     * Creates a new application for a student applying to an internship.
     *
     * <p>This method also updates the related Internship entity by adding the
     * student's ID to its {@code applicantIds} list, maintaining the consistency
     * expected by legacy logic and reporting features.</p>
     *
     * @param internshipId the target internship
     * @param studentId    the student applying
     * @return the generated application ID
     */
    public String createApplication(String internshipId, String studentId) {
        String id = appRepo.create(internshipId, studentId);

        // Maintain applicantIds on Internship (as before)
        internshipRepo.findById(internshipId)
                .ifPresent(it -> it.getApplicantIds().add(studentId));

        return id;
    }

    /**
     * Retrieves all applications submitted by a specific student.
     *
     * @param studentId ID of the student
     * @return list of applications submitted by the student
     */
    public List<Application> getApplicationsByStudent(String studentId) {
        return appRepo.findByStudentId(studentId);
    }

    /**
     * Retrieves all applications submitted for a specific internship.
     *
     * @param internshipId ID of the internship
     * @return list of applications related to the internship
     */
    public List<Application> getApplicationsForInternship(String internshipId) {
        return appRepo.findByInternshipId(internshipId);
    }

    /**
     * Updates the status of a given application.
     *
     * <p>This method delegates to {@link Application#setStatus(String)}
     * and does not perform additional workflow logic.</p>
     *
     * @param applicationId ID of the application
     * @param status        new status (case-insensitive string)
     */
    public void setApplicationStatus(String applicationId, String status) {
        appRepo.findById(applicationId).ifPresent(a -> {
            a.setStatus(status);
        });
    }

    /**
     * Handles the student acceptance workflow:
     *
     * <ul>
     *     <li>Confirms the successful application</li>
     *     <li>Increments internship confirmed count</li>
     *     <li>Automatically marks all other applications from the student as unsuccessful</li>
     * </ul>
     *
     * <p>This enforces the rule that a student may accept only one internship.</p>
     *
     * @param studentId     ID of the student accepting
     * @param internshipId  ID of the internship being accepted
     */
    public void studentAcceptPlacement(String studentId, String internshipId) {

        for (Application a : appRepo.findAll()) {
            if (a.getStudentId().equals(studentId)
                    && a.getInternshipId().equals(internshipId)
                    && "Successful".equalsIgnoreCase(a.getStatus())) {

                a.confirmByStudent();

                internshipRepo.findById(internshipId)
                        .ifPresent(Internship::incrementConfirmed);

                // set other applications of student to Unsuccessful
                for (Application other : appRepo.findByStudentId(studentId)) {
                    if (!other.getId().equals(a.getId())) {
                        other.setStatus("Unsuccessful");
                    }
                }
                break;
            }
        }
    }

    /**
     * Marks an application as having requested a withdrawal.
     * Actual approval is handled separately by staff.
     *
     * @param applicationId ID of the application requesting withdrawal
     */
    public void markWithdrawalRequest(String applicationId) {
        appRepo.findById(applicationId)
                .ifPresent(Application::requestWithdrawal);
    }

    /**
     * Returns all applications that have an active withdrawal request.
     *
     * @return list of applications where withdrawal has been requested
     */
    public List<Application> getWithdrawalRequests() {
        return appRepo.findAll().stream()
                .filter(Application::isWithdrawalRequested)
                .collect(Collectors.toList());
    }

    /**
     * Approves a withdrawal request by:
     * <ul>
     *     <li>Setting status to {@code Unsuccessful}</li>
     *     <li>Removing the student from the internship’s applicant list</li>
     * </ul>
     *
     * @param applicationId ID of the application being approved
     */
    public void approveWithdrawal(String applicationId) {
        appRepo.findById(applicationId).ifPresent(a -> {
            a.setStatus("Unsuccessful");

            internshipRepo.findById(a.getInternshipId())
                    .ifPresent(it -> it.getApplicantIds().remove(a.getStudentId()));
        });
    }

    /**
     * Clears all application data from the repository.
     * Used during data resets or test operations.
     */
    public void clearApplications() {
        appRepo.clear();
    }

    /**
     * Deletes all applications for a specific internship.
     *
     * @param internshipId ID of the internship
     */
    public void deleteApplicationsByInternshipId(String internshipId) {
        appRepo.deleteByInternshipId(internshipId);
    }

    /**
     * Returns all applications for saving to CSV during shutdown.
     *
     * @return iterable collection of all Application entities
     */
    public Iterable<Application> getAllForSave() {
        return appRepo.findAll();
    }

    /**
     * Restores cross-entity relations after loading applications from CSV.
     *
     * <p>Because CSV loading does not automatically rebuild references between
     * applications, internships, and students, this method performs relational fixes:</p>
     *
     * <ul>
     *     <li>Add missing student IDs into {@code Internship.applicantIds}</li>
     *     <li>Call {@link Student#applySilently(String)} to rebuild applied lists</li>
     * </ul>
     *
     * <p>This ensures the in-memory model is consistent with expectations
     * of legacy code and menus.</p>
     */
    public void restoreRelationsAfterLoad() {
        for (Application app : appRepo.findAll()) {
            String sid = app.getStudentId();
            String iid = app.getInternshipId();

            internshipRepo.findById(iid).ifPresent(it -> {
                if (!it.getApplicantIds().contains(sid)) {
                    it.getApplicantIds().add(sid);
                }
            });

            userRepo.findById(sid).ifPresent(u -> {
                if (u instanceof Student st) {
                    st.applySilently(iid);
                }
            });
        }
    }
}
