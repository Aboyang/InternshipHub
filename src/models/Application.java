package careerhub.models;

/**
 * Represents an application submitted by a student for a specific
 * InternshipOpportunity. This class stores the IDs needed to identify
 * the student and the opportunity, along with the current application
 * status. The application status evolves as Company Representatives
 * and Career Center Staff review and process the application.
 */
public class Application {

    /** Unique identifier for this application. */
    private String id;

    /** ID of the student who submitted this application. */
    private String studentId;

    /** ID of the internship opportunity being applied for. */
    private String opportunityId;

    /** Current status of this application. */
    private ApplicationStatus status;

    /**
     * Constructs a new Application with default status PENDING.
     *
     * @param id            unique application ID
     * @param studentId     ID of the student applying
     * @param opportunityId ID of the internship opportunity
     */
    public Application(String id, String studentId, String opportunityId) {
        this.id = id;
        this.studentId = studentId;
        this.opportunityId = opportunityId;
        this.status = ApplicationStatus.PENDING;
    }

    public String getId() {
        return id;
    }

    public String getStudentId() {
        return studentId;
    }

    public String getOpportunityId() {
        return opportunityId;
    }

    public ApplicationStatus getStatus() {
        return status;
    }

    /**
     * Updates the application status (e.g., PENDING → SUCCESSFUL).
     * This method is typically invoked by Company Representatives or
     * Career Center Staff depending on the workflow.
     *
     * @param newStatus the new application status to assign
     */
    public void updateStatus(ApplicationStatus newStatus) {
        this.status = newStatus;
    }

    /**
     * Allows the student to request a withdrawal of their application.
     * The request must later be approved or rejected by Career Center Staff.
     *
     * @return true if withdrawal request was allowed; false otherwise
     */
    public boolean withdraw() {
        if (status == ApplicationStatus.SUCCESSFUL ||
            status == ApplicationStatus.PENDING) {
            status = ApplicationStatus.WITHDRAW_REQUESTED;
            return true;
        }
        return false;
    }
}
