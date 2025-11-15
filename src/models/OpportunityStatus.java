package careerhub.models;

/**
 * Represents the lifecycle status of an internship opportunity.
 * This determines whether students may apply and whether the
 * opportunity is still open or filled.
 */
public enum OpportunityStatus {

    /** Newly created opportunity; awaiting Career Center Staff approval. */
    PENDING,

    /** Approved and visible to eligible students. */
    APPROVED,

    /** Rejected by Career Center Staff. */
    REJECTED,

    /** All available slots have been filled by students. */
    FILLED
}
