package careerhub.models;

/**
 * Represents an application submitted by a student for a specific internship
 * opportunity. Each application tracks the student ID, internship ID, and
 * current status, which evolves as Company Representatives and Career Center
 * Staff review and process the request.
 *
 * <p>This class also manages special state transitions such as withdrawal
 * requests and student confirmation after a successful offer. These transitions
 * must comply with assignment rules: students may withdraw only under certain
 * statuses, and confirmation is only valid after an application becomes
 * "SUCCESSFUL".</p>
 */
public class Application {

    /** Unique identifier for this application. */
    private String id;

    /** ID of the internship opportunity the student is applying for. */
    private String internshipId;

    /** ID of the student who submitted this application. */
    private String studentId;

    /**
     * Current status of this application, represented by the ApplicationStatus enum.
     * Tracks states such as PENDING, SUCCESSFUL, UNSUCCESSFUL,
     * WITHDRAW_REQUESTED, etc.
     */
    private ApplicationStatus status;

    /**
     * Indicates whether the student has confirmed (accepted) this internship
     * placement after receiving a successful offer.
     */
    private boolean confirmedByStudent = false;

    /**
     * Constructs a new Application object with an initial status of PENDING.
     *
     * @param id           the unique identifier for the application
     * @param internshipId the ID of the internship opportunity being applied to
     * @param studentId    the ID of the student submitting the application
     */
    public Application(String id, String internshipId, String studentId) {
        this.id = id;
        this.internshipId = internshipId;
        this.studentId = studentId;
        this.status = ApplicationStatus.PENDING;
    }

    /**
     * Retrieves the unique application ID.
     *
     * @return the application ID
     */
    public String getId() {
        return id;
    }

    /**
     * Retrieves the internship ID associated with this application.
     *
     * @return the internship ID
     */
    public String getInternshipId() {
        return internshipId;
    }

    /**
     * Retrieves the ID of the student who submitted this application.
     *
     * @return the student ID
     */
    public String getStudentId() {
        return studentId;
    }

    /**
     * Retrieves the current application status as an enum value.
     *
     * @return the ApplicationStatus enum representing the current state
     */
    public ApplicationStatus getStatusEnum() {
        return status;
    }

    /**
     * Retrieves the application status as a String. This is used when printing
     * or comparing status text in menus or persistent storage.
     *
     * @return the status name (e.g. "PENDING", "SUCCESSFUL")
     */
    public String getStatus() {
        return status.name();
    }

    /**
     * Updates the application status using an enum value. This is typically used
     * by Company Representatives or Career Center Staff during the approval and
     * review process.
     *
     * @param newStatus the new application status to assign
     */
    public void updateStatus(ApplicationStatus newStatus) {
        this.status = newStatus;
    }

    /**
     * Updates the application status using a textual value. Commonly used by
     * DataManager when reading or interpreting text-based decisions.
     * The input is case-insensitive and trimmed before processing.
     *
     * <p>If the provided text is invalid and does not match any enum value,
     * the current status remains unchanged.</p>
     *
     * @param newStatus the status text to parse and apply
     */
    public void setStatus(String newStatus) {
        if (newStatus == null) return;
        String s = newStatus.trim().toUpperCase();
        try {
            this.status = ApplicationStatus.valueOf(s);
        } catch (IllegalArgumentException ex) {
            // Invalid input ignored; status remains unchanged.
        }
    }

    /**
     * Initiates a withdrawal request for the application. A withdrawal may only
     * be requested when the application is currently in PENDING or SUCCESSFUL
     * status. The request will later be processed by Career Center Staff.
     *
     * @return true if a withdrawal request was successfully initiated;
     *         false if withdrawal is not allowed in the current status
     */
    public boolean requestWithdrawal() {
        if (status == ApplicationStatus.SUCCESSFUL ||
            status == ApplicationStatus.PENDING) {
            status = ApplicationStatus.WITHDRAW_REQUESTED;
            return true;
        }
        return false;
    }

    /**
     * Alias for {@link #requestWithdrawal()} kept for backward compatibility with
     * older parts of the system that may still call withdraw().
     *
     * @return true if a withdrawal request was successfully initiated; false otherwise
     */
    public boolean withdraw() {
        return requestWithdrawal();
    }

    /**
     * Checks whether the application is currently in the WITHDRAW_REQUESTED state.
     * Used to list pending withdrawal requests that require Career Center Staff
     * approval.
     *
     * @return true if a withdrawal request has been submitted; false otherwise
     */
    public boolean isWithdrawalRequested() {
        return status == ApplicationStatus.WITHDRAW_REQUESTED;
    }

    /**
     * Marks the application as confirmed by the student. Confirmation is only valid
     * if the application is in the SUCCESSFUL state, which represents an accepted offer.
     * This method does nothing if the application is not successful.
     */
    public void confirmByStudent() {
        if (status == ApplicationStatus.SUCCESSFUL) {
            this.confirmedByStudent = true;
        }
    }

    /**
     * Checks whether the student has confirmed (accepted) this internship placement.
     *
     * @return true if the student has accepted the offer; false otherwise
     */
    public boolean isConfirmedByStudent() {
        return confirmedByStudent;
    }

}
