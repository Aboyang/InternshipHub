package careerhub.models;

import java.util.Date;

/**
 * Abstract base class for various types of opportunities offered in the system
 * (currently implemented only as internships). This class provides shared
 * attributes such as ID, title, and opening/closing dates, allowing subclasses
 * to build on common behaviour while defining their own application rules.
 *
 * <p>The {@code isOpen()} method is intentionally abstract because each
 * subclass (e.g., {@link Internship}) has its own eligibility logic based on
 * status, visibility, date range, and capacity constraints.</p>
 */
public abstract class Opportunity {

    /** Unique identifier for the opportunity (e.g., "I1", "I2"). */
    protected String id;

    /** Title or name of the opportunity. */
    protected String title;

    /** Date when the opportunity becomes available for applications. */
    protected Date openingDate;

    /** Date when the opportunity stops accepting applications. */
    protected Date closingDate;

    /**
     * Constructs a generic Opportunity with shared attributes.
     *
     * @param id          unique identifier (may be null initially for auto-generation)
     * @param title       title or name of the opportunity
     * @param openingDate date when applications open (nullable)
     * @param closingDate date when applications close (nullable)
     */
    public Opportunity(String id, String title, Date openingDate, Date closingDate) {
        this.id = id;
        this.title = title;
        this.openingDate = openingDate;
        this.closingDate = closingDate;
    }

    /**
     * Returns the unique identifier of this opportunity.
     *
     * @return the opportunity ID
     */
    public String getId() { return id; }

    /**
     * Assigns a system-generated identifier after the object is constructed.
     * Used by DataManager when loading or persisting opportunities.
     *
     * @param id system-generated opportunity ID
     */
    public void setId(String id) { this.id = id; }

    /**
     * Retrieves the title of this opportunity.
     *
     * @return the opportunity title
     */
    public String getTitle() { return title; }

    /**
     * Retrieves the opening date when applications begin.
     *
     * @return the opening {@link Date}, or null if not set
     */
    public Date getOpeningDate() { return openingDate; }

    /**
     * Retrieves the closing date when applications end.
     *
     * @return the closing {@link Date}, or null if not set
     */
    public Date getClosingDate() { return closingDate; }

    /**
     * Determines whether the opportunity is currently open for applications.
     * Subclasses must implement specific rules, which may include:
     * <ul>
     *     <li>Approval status</li>
     *     <li>Visibility settings</li>
     *     <li>Date range checks</li>
     *     <li>Remaining slot availability</li>
     * </ul>
     *
     * @return true if applications are currently allowed
     */
    public abstract boolean isOpen();
}
