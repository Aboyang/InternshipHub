package careerhub.models;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Represents a concrete internship opportunity created by a Company Representative.
 * This class encapsulates all internship attributes such as description, academic
 * level, preferred major, date range, company details, and visibility rules.
 *
 * <p>The opportunity goes through several lifecycle statuses (e.g., PENDING,
 * APPROVED, FILLED) and supports rules defined by the SC2002 assignment:
 * visibility toggling, date-based application windows, and limited slot
 * capacity. It also maintains lists of applicant IDs and provides mechanisms
 * for tracking confirmed placements.</p>
 *
 * <p>This class is designed to integrate directly with Menu and DataManager logic,
 * while still supporting extensibility through object-oriented principles.</p>
 */
public class Internship extends Opportunity {

    /** Human-readable description of the internship role or duties. */
    private String description;

    /** Required academic level (Basic / Intermediate / Advanced). */
    private InternshipLevel level;

    /** Preferred major for eligible applicants. */
    private String preferredMajor;

    /** Current lifecycle status of the internship opportunity. */
    private OpportunityStatus status;

    /** Maximum number of students allowed (clamped between 1 and 10). */
    private int slots;

    /** Indicates whether students can view this opportunity. */
    private boolean visible;

    /** Display name of the company offering this internship. */
    private String companyName;

    /** ID of the Company Representative who created this internship. */
    private String companyRepId;

    /**
     * List of student IDs who have applied to this internship.
     * Used for reporting and efficient lookups in the DataManager.
     */
    private List<String> applicantIds;

    /**
     * Number of students who have accepted the internship offer.
     * When this equals {@code slots}, the internship is considered filled.
     */
    private int confirmedCount = 0;

    /**
     * Optional list of Application objects. Not required for core functionality
     * but kept to support future enhancements and richer modelling.
     */
    private List<Application> applications;

    /**
     * Constructs a new Internship instance with all required attributes.
     * This constructor aligns with how Menu creates internship listings.
     *
     * @param id             unique internship ID (may be null if auto-generated later)
     * @param title          internship title
     * @param description    job description
     * @param levelText      internship level text (Basic/Intermediate/Advanced)
     * @param preferredMajor preferred major filter
     * @param openingDate    when applications open (nullable)
     * @param closingDate    when applications close (nullable)
     * @param companyName    name of the offering company
     * @param companyRepId   creator's representative ID
     * @param slots          available positions (will be clamped to range 1–10)
     */
    public Internship(
            String id,
            String title,
            String description,
            String levelText,
            String preferredMajor,
            LocalDate openingDate,
            LocalDate closingDate,
            String companyName,
            String companyRepId,
            int slots) {

        super(
                id,
                title,
                toDateOrNull(openingDate),
                toDateOrNull(closingDate)
        );

        this.description = description;
        this.level = parseLevel(levelText);
        this.preferredMajor = preferredMajor;
        this.status = OpportunityStatus.PENDING;

        // Clamp slot values within valid range
        if (slots < 1) slots = 1;
        if (slots > 10) slots = 10;
        this.slots = slots;

        this.visible = false;
        this.companyName = companyName;
        this.companyRepId = companyRepId;
        this.applicantIds = new ArrayList<>();
        this.applications = new ArrayList<>();
    }

    // ------------------------------------------------------------------------
    // Helper conversion methods for LocalDate <-> Date
    // ------------------------------------------------------------------------

    /**
     * Converts a LocalDate into a Date object using the system default timezone.
     *
     * @param d a LocalDate to convert (nullable)
     * @return corresponding Date instance or null
     */
    private static Date toDateOrNull(LocalDate d) {
        if (d == null) return null;
        return Date.from(d.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    /**
     * Converts a Date into a LocalDate using the system default timezone.
     *
     * @param d a Date to convert (nullable)
     * @return corresponding LocalDate instance or null
     */
    private static LocalDate toLocalDateOrNull(Date d) {
        if (d == null) return null;
        return d.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }

    /**
     * Parses a level string into an InternshipLevel enum.
     * Falls back to BASIC if parsing fails.
     *
     * @param levelText textual level input
     * @return parsed InternshipLevel value
     */
    private static InternshipLevel parseLevel(String levelText) {
        if (levelText == null || levelText.isBlank()) {
            return InternshipLevel.BASIC;
        }
        String s = levelText.trim().toUpperCase();
        try {
            return InternshipLevel.valueOf(s);
        } catch (IllegalArgumentException ex) {
            // Fallback safe defaults for common user input deviations
            if ("BASIC".equalsIgnoreCase(s)) return InternshipLevel.BASIC;
            if ("INTERMEDIATE".equalsIgnoreCase(s)) return InternshipLevel.INTERMEDIATE;
            if ("ADVANCED".equalsIgnoreCase(s)) return InternshipLevel.ADVANCED;
            return InternshipLevel.BASIC;
        }
    }

    // ------------------------------------------------------------------------
    // Getters used directly by Menu / DataManager
    // ------------------------------------------------------------------------

    /**
     * Retrieves the internship description.
     *
     * @return description of the internship
     */
    public String getDescription() {
        return description;
    }

    /**
     * Retrieves the internship level enum.
     *
     * @return InternshipLevel value
     */
    public InternshipLevel getLevelEnum() {
        return level;
    }

    /**
     * Retrieves the internship level as a text string (e.g., "BASIC").
     *
     * @return level name string
     */
    public String getLevel() {
        return level.name();
    }

    /**
     * Retrieves the preferred major required for this internship.
     *
     * @return preferred major string
     */
    public String getPreferredMajor() {
        return preferredMajor;
    }

    /**
     * Retrieves the OpportunityStatus enum.
     *
     * @return current OpportunityStatus
     */
    public OpportunityStatus getStatusEnum() {
        return status;
    }

    /**
     * Retrieves the internship status as a string (e.g., "APPROVED").
     *
     * @return current status in text form
     */
    public String getStatus() {
        return status.name();
    }

    /**
     * Updates the opportunity status using an enum.
     *
     * @param status new status value
     */
    public void setStatus(OpportunityStatus status) {
        this.status = status;
    }

    /**
     * Updates the opportunity status from a text value.
     * Invalid inputs are ignored.
     *
     * @param statusText textual representation of the status
     */
    public void setStatus(String statusText) {
        if (statusText == null) return;
        String s = statusText.trim().toUpperCase();
        try {
            this.status = OpportunityStatus.valueOf(s);
        } catch (IllegalArgumentException ex) {
            // Ignore invalid string
        }
    }

    /**
     * Retrieves the number of available slots.
     *
     * @return number of slots
     */
    public int getSlots() {
        return slots;
    }

    /**
     * Checks whether this internship listing is visible to students.
     *
     * @return true if visible
     */
    public boolean isVisible() {
        return visible;
    }

    /**
     * Sets visibility of the internship to students.
     *
     * @param visible true to make visible
     */
    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    /**
     * Toggles visibility of this internship listing.
     */
    public void toggleVisibility() {
        this.visible = !this.visible;
    }

    /**
     * Retrieves the offering company's name.
     *
     * @return company name
     */
    public String getCompanyName() {
        return companyName;
    }

    /**
     * Retrieves the ID of the Company Representative who created this listing.
     *
     * @return creator's representative ID
     */
    public String getCompanyRepId() {
        return companyRepId;
    }

    /**
     * Retrieves the internal list of applicant student IDs.
     *
     * @return list of student IDs
     */
    public List<String> getApplicantIds() {
        return applicantIds;
    }

    /**
     * Retrieves how many students have accepted this internship so far.
     *
     * @return confirmed student count
     */
    public int getConfirmedCount() {
        return confirmedCount;
    }

    /**
     * Increments the number of confirmed students. If the count reaches the
     * slot limit, this internship is automatically marked as FILLED.
     */
    public void incrementConfirmed() {
        if (confirmedCount < slots) {
            confirmedCount++;
            if (confirmedCount >= slots) {
                markAsFilled();
            }
        }
    }

    // ------------------------------------------------------------------------
    // Date helpers
    // ------------------------------------------------------------------------

    /**
     * Retrieves the opening date as a LocalDate.
     *
     * @return opening date LocalDate or null
     */
    public LocalDate getOpenDate() {
        return toLocalDateOrNull(openingDate);
    }

    /**
     * Retrieves the closing date as a LocalDate.
     *
     * @return closing date LocalDate or null
     */
    public LocalDate getCloseDate() {
        return toLocalDateOrNull(closingDate);
    }

    // ------------------------------------------------------------------------
    // Setters for Staff Editing
    // ------------------------------------------------------------------------

    /**
     * Updates the internship title (non-empty only).
     *
     * @param t new title
     */
    public void setTitle(String t) {
        if (t != null && !t.trim().isEmpty()) {
            this.title = t.trim();
        }
    }

    /**
     * Updates the internship description (non-empty only).
     *
     * @param d new description
     */
    public void setDescription(String d) {
        if (d != null && !d.trim().isEmpty()) {
            this.description = d.trim();
        }
    }

    /**
     * Updates the internship level based on text input.
     *
     * @param lvl new level text
     */
    public void setLevel(String lvl) {
        if (lvl != null && !lvl.trim().isEmpty()) {
            this.level = parseLevel(lvl);
        }
    }

    /**
     * Updates the preferred major.
     *
     * @param pm new preferred major
     */
    public void setPreferredMajor(String pm) {
        if (pm != null && !pm.trim().isEmpty()) {
            this.preferredMajor = pm.trim();
        }
    }

    /**
     * Updates the opening date.
     *
     * @param d new LocalDate for opening
     */
    public void setOpenDate(LocalDate d) {
        this.openingDate = toDateOrNull(d);
    }

    /**
     * Updates the closing date.
     *
     * @param d new LocalDate for closing
     */
    public void setCloseDate(LocalDate d) {
        this.closingDate = toDateOrNull(d);
    }

    /**
     * Updates the number of slots, clamped to 1–10.
     *
     * @param s new slot count
     */
    public void setSlots(int s) {
        if (s < 1) s = 1;
        if (s > 10) s = 10;
        this.slots = s;
    }

    /**
     * Sets the confirmed count for this internship (allowed range 0–slots).
     *
     * @param n new confirmed count
     */
    public void setConfirmedCount(int n) {
        if (n < 0) n = 0;
        if (n > slots) n = slots;
        this.confirmedCount = n;
    }

    // ------------------------------------------------------------------------
    // Core domain behavior
    // ------------------------------------------------------------------------

    /**
     * Determines if the internship is currently open for applications.
     *
     * <p>An internship is considered open if all conditions are met:</p>
     * <ul>
     *     <li>Status is APPROVED</li>
     *     <li>Visibility is enabled</li>
     *     <li>Today's date is within the opening and closing dates</li>
     *     <li>Remaining slots are available</li>
     * </ul>
     *
     * @return true if students may apply
     */
    @Override
    public boolean isOpen() {
        LocalDate today = LocalDate.now();
        LocalDate open = getOpenDate();
        LocalDate close = getCloseDate();

        if (status != OpportunityStatus.APPROVED) return false;
        if (!visible) return false;
        if (open != null && today.isBefore(open)) return false;
        if (close != null && today.isAfter(close)) return false;
        return slots > 0;
    }

    /**
     * Adds a student ID to the applicant list if not already present.
     *
     * @param studentId the ID of the student applying
     * @return true if added successfully; false if duplicate
     */
    public boolean addApplicant(String studentId) {
        if (!applicantIds.contains(studentId)) {
            applicantIds.add(studentId);
            return true;
        }
        return false;
    }

    /**
     * Marks the internship as FILLED and sets visibility to false.
     * Called automatically when slot capacity is fully taken.
     */
    public void markAsFilled() {
        this.status = OpportunityStatus.FILLED;
        this.visible = false;
    }
}
