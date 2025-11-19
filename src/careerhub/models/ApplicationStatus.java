package careerhub.models;

/**
 * Defines all possible states an internship application may transition through
 * during its lifecycle. These statuses are used throughout the system by the
 * Application class, Company Representatives, and Career Center Staff to manage
 * approval decisions, withdrawal requests, and final outcomes.
 *
 * <p>The states reflect the assignment requirements: applications begin in
 * {@code PENDING}, may be marked {@code SUCCESSFUL} or {@code UNSUCCESSFUL}
 * by Company Representatives, and may transition through withdrawal-related
 * statuses depending on actions taken by students and Career Center Staff.</p>
 */
public enum ApplicationStatus {

    /**
     * The application has been submitted by the student but has not yet been
     * reviewed or decided on by the Company Representative.
     */
    PENDING,

    /**
     * The Company Representative has approved the application and offered the
     * internship to the student. At this point, the student may choose to accept
     * the placement.
     */
    SUCCESSFUL,

    /**
     * The Company Representative has rejected the application, and the student
     * will not be considered further for this internship opportunity.
     */
    UNSUCCESSFUL,

    /**
     * The student has requested to withdraw the application. This request must be
     * reviewed and acted upon by the Career Center Staff before it becomes final.
     */
    WITHDRAW_REQUESTED,

    /**
     * The Career Center Staff has approved the student's withdrawal request,
     * removing the student from consideration for the internship.
     */
    WITHDRAW_APPROVED,

    /**
     * The Career Center Staff has rejected the student's withdrawal request,
     * meaning the application remains active in its previous state.
     */
    WITHDRAW_REJECTED
}
