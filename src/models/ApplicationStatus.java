package careerhub.models;

/**
 * Represents the various states an internship application can be in.
 * This enum is used by the Application class to track the current
 * status of a student's internship application.
 */
public enum ApplicationStatus {

    /** Application submitted but not yet reviewed by a Company Representative. */
    PENDING,

    /** Application approved by the Company Representative. */
    SUCCESSFUL,

    /** Application rejected by the Company Representative. */
    UNSUCCESSFUL,

    /** Student has requested to withdraw from the application. */
    WITHDRAW_REQUESTED,

    /** Career Center Staff has approved the withdrawal request. */
    WITHDRAW_APPROVED,

    /** Career Center Staff has rejected the withdrawal request. */
    WITHDRAW_REJECTED
}

