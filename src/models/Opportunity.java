package careerhub.models;

import java.util.Date;

public abstract class Opportunity {

    /** Unique identifier for the opportunity. */
    protected String id;

    /** Title or name of the opportunity. */
    protected String title;

    /** Date when opportunity opens for applications. */
    protected Date openingDate;

    /** Date when opportunity closes for applications. */
    protected Date closingDate;

    /**
     * Constructs a new Opportunity.
     *
     * @param id unique identifier
     * @param title opportunity title
     * @param openingDate date applications open
     * @param closingDate date applications close
     */
    public Opportunity(String id, String title, Date openingDate, Date closingDate) {
        this.id = id;
        this.title = title;
        this.openingDate = openingDate;
        this.closingDate = closingDate;
    }

    public String getId() { return id; }
    public String getTitle() { return title; }
    public Date getOpeningDate() { return openingDate; }
    public Date getClosingDate() { return closingDate; }

    /**
     * Determines whether the opportunity is currently open.
     * Subclasses must define their own logic.
     *
     * @return true if eligible for applications
     */
    public abstract boolean isOpen();
}

