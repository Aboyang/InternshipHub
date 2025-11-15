package careerhub.models;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class InternshipOpportunity extends Opportunity {

    /** Required academic level. */
    private InternshipLevel level;

    /** Preferred major of eligible students. */
    private String preferredMajor;

    /** Current status of the opportunity. */
    private OpportunityStatus status;

    /** Maximum number of students allowed. */
    private int slots;

    /** Whether the opportunity is visible to students. */
    private boolean visibility;

    /** List of applications submitted for this internship. */
    private List<Application> applications;

    /**
     * Constructs a new InternshipOpportunity.
     *
     * @param id            unique ID
     * @param title         opportunity title
     * @param description   job description
     * @param level         required internship level
     * @param preferredMajor preferred student major
     * @param openingDate   date applications open
     * @param closingDate   date applications close
     * @param slots         maximum number of available slots (max 10)
     */
    public InternshipOpportunity(
            String id,
            String title,
            String description,
            InternshipLevel level,
            String preferredMajor,
            Date openingDate,
            Date closingDate,
            int slots) {

        super(id, title, openingDate, closingDate);

        this.level = level;
        this.preferredMajor = preferredMajor;
        this.status = OpportunityStatus.PENDING;
        this.slots = Math.min(slots, 10);
        this.visibility = false;
        this.applications = new ArrayList<>();
    }

    public InternshipLevel getLevel() { return level; }
    public String getPreferredMajor() { return preferredMajor; }
    public OpportunityStatus getStatus() { return status; }
    public boolean isVisible() { return visibility; }
    public int getSlots() { return slots; }
    public List<Application> getApplicationList() { return applications; }

    /**
     * Updates the opportunity's status.
     *
     * @param status new status value
     */
    public void setStatus(OpportunityStatus status) {
        this.status = status;
    }

    /**
     * Toggles visibility of the internship posting.
     */
    public void toggleVisibility() {
        this.visibility = !this.visibility;
    }

    /**
     * Determines if the internship is currently open.
     *
     * @return true if applications are accepted
     */
    @Override
    public boolean isOpen() {
        Date now = new Date();
        return status == OpportunityStatus.APPROVED
                && visibility
                && now.after(openingDate)
                && now.before(closingDate)
                && slots > 0;
    }

    /**
     * Adds a student's application if the opportunity is open.
     *
     * @param app the submitted application
     * @return true if added successfully
     */
    public boolean addApplicant(Application app) {
        if (isOpen()) {
            applications.add(app);
            return true;
        }
        return false;
    }

    /**
     * Approves a student's application and updates remaining slots.
     * Marks the opportunity as FILLED when all slots are taken.
     *
     * @param application the application to approve
     */
    public void approveApplicant(Application application) {
        if (slots > 0) {
            application.updateStatus(ApplicationStatus.SUCCESSFUL);
            slots--;

            if (slots == 0) {
                markAsFilled();
            }
        }
    }

    /**
     * Marks this opportunity as filled and disables visibility.
     */
    public void markAsFilled() {
        this.status = OpportunityStatus.FILLED;
        this.visibility = false;
    }
}

