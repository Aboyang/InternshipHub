package careerhub.models;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a Student user in the Internship Management System.
 * Students may view internship opportunities, apply for them
 * (subject to eligibility rules), withdraw applications, and accept
 * a single internship placement.
 *
 * <p>This class also stores last-used filter preferences for student
 * menu navigation, as well as lists of applied internships and any
 * accepted placement ID.</p>
 */
public class Student extends User {

    /** Year of study (1–4). Determines eligibility for internship levels. */
    private int year;

    /** Student's major (e.g., CSC, EEE, MAE). */
    private String major;

    /**
     * List of internship IDs the student has applied for.
     * A student may apply for at most 3 internships at any time.
     */
    private List<String> appliedInternships = new ArrayList<>();

    /**
     * ID of the internship the student has accepted.
     * A student may accept only one placement.
     */
    private String acceptedInternshipId = null;

    /** Last selected status filter used by the student. */
    private String lastStatusFilter = "";

    /** Last selected major filter used by the student. */
    private String lastMajorFilter = "";

    /** Last selected internship-level filter used by the student. */
    private String lastLevelFilter = "";

    /** Last selected closing-date filter used by the student. */
    private String lastCloseFilter = "";

    /**
     * Constructs a Student with profile details.
     *
     * @param id        student ID (e.g., U1234567A)
     * @param name      student name
     * @param password  login password
     * @param year      year of study
     * @param major     student major
     */
    public Student(String id, String name, String password, int year, String major) {
        super(id, name, password);
        this.year = year;
        this.major = major;
    }

    /**
     * Retrieves the student's academic year.
     *
     * @return year of study
     */
    public int getYear() { return year; }

    /**
     * Retrieves the student's major.
     *
     * @return major string
     */
    public String getMajor() { return major; }

    /**
     * Determines whether a student is eligible to apply for an internship
     * based on academic level requirements.
     *
     * <p>Assignment rules:</p>
     * <ul>
     *     <li>Year 1–2: may only apply to BASIC level internships</li>
     *     <li>Year 3–4: may apply to any level</li>
     * </ul>
     *
     * @param level internship level text
     * @return true if the student can apply for this level
     */
    public boolean canApplyLevel(String level) {
        if (year <= 2) return "Basic".equalsIgnoreCase(level);
        return true;
    }

    /**
     * Determines whether the student may apply for more internships.
     *
     * <p>Students may have at most 3 active applications and cannot
     * apply to any more once they have accepted a placement.</p>
     *
     * @return true if the student may submit another application
     */
    public boolean canApplyMore() {
        return appliedInternships.size() < 3 && acceptedInternshipId == null;
    }

    /**
     * Adds an internship ID to the list of applied internships, if not already present.
     *
     * @param internshipId ID of the internship being applied for
     */
    public void apply(String internshipId) {
        if (!appliedInternships.contains(internshipId)) appliedInternships.add(internshipId);
    }

    /**
     * Adds an internship application without validation.
     * Used when loading persistent data from files to restore application history.
     *
     * @param internshipId internship ID to add
     */
    public void applySilently(String internshipId) {
        apply(internshipId);
    }

    /**
     * Removes an internship from the student's active applications.
     *
     * @param internshipId ID of internship to withdraw from
     */
    public void withdrawApplication(String internshipId) {
        appliedInternships.remove(internshipId);
    }

    /**
     * Records that the student has accepted a specific internship placement.
     * Only one internship may be accepted.
     *
     * @param internshipId ID of the accepted internship
     */
    public void acceptPlacement(String internshipId) {
        acceptedInternshipId = internshipId;
    }

    /**
     * Retrieves the ID of the internship the student has accepted.
     *
     * @return accepted internship ID or null if none accepted
     */
    public String getAcceptedInternshipId() { return acceptedInternshipId; }

    /**
     * Retrieves a list of internship IDs the student has applied for.
     *
     * @return list of applied internship IDs
     */
    public List<String> getAppliedInternships() { return appliedInternships; }

    /**
     * Identifies the user type for menu routing.
     *
     * @return "Student"
     */
    @Override
    public String getType() { return "Student"; }

    // ---------------- Last-used filter preferences ----------------

    /** @return last selected status filter */
    public String getLastStatusFilter() { return lastStatusFilter; }

    /** Updates the last used status filter. */
    public void setLastStatusFilter(String x) { lastStatusFilter = x; }

    /** @return last selected major filter */
    public String getLastMajorFilter() { return lastMajorFilter; }

    /** Updates the last used major filter. */
    public void setLastMajorFilter(String x) { lastMajorFilter = x; }

    /** @return last selected level filter */
    public String getLastLevelFilter() { return lastLevelFilter; }

    /** Updates the last used level filter. */
    public void setLastLevelFilter(String x) { lastLevelFilter = x; }

    /** @return last selected closing-date filter */
    public String getLastCloseFilter() { return lastCloseFilter; }

    /** Updates the last used closing-date filter. */
    public void setLastCloseFilter(String x) { lastCloseFilter = x; }
}
