package careerhub.models;

/**
 * Represents the lifecycle status of an internship opportunity. The status
 * determines whether the opportunity is visible, whether students may apply,
 * and when the opportunity is considered closed or unavailable.
 *
 * <p>This enum is used extensively in the {@link Internship} class and
 * throughout the system to enforce SC2002 business rules such as approval
 * requirements, student eligibility, and slot-based filling behaviour.</p>
 */
public enum OpportunityStatus {

    /**
     * Opportunity has been created by a Company Representative but has not yet been
     * reviewed by Career Center Staff. Students cannot apply at this stage.
     */
    PENDING,

    /**
     * Opportunity has been approved by Career Center Staff. If also marked visible
     * and within the application date range, it may now accept student applications.
     */
    APPROVED,

    /**
     * Opportunity was reviewed by Career Center Staff but was rejected. It will not
     * be shown to students or accept any applications.
     */
    REJECTED,

    /**
     * All available slots for this opportunity have been filled by confirmed
     * student acceptances. The opportunity is now closed and hidden from listings.
     */
    FILLED
}
