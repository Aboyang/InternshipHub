package careerhub.services.dataManagement;

import careerhub.models.Internship;
import careerhub.models.Student;
import careerhub.storage.InternshipRepository;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Service layer that manages all domain-level operations related to
 * Internship entities. This class provides higher-level behaviours
 * on top of the {@link InternshipRepository}, including creation,
 * filtering, visibility logic, approval handling, and student-eligible
 * internship retrieval.
 *
 * <p>This class is used by multiple menu flows (CompanyRep, Staff,
 * Student) through DataManager. It keeps business rules consolidated
 * in one place, preserving SRP and reducing duplication.</p>
 */
public class InternshipDataService {

    /** Repository responsible for persistence and retrieval of Internship objects. */
    private final InternshipRepository repo;

    /**
     * Creates an InternshipDataService with the given repository.
     *
     * @param repo the repository used to store and read internships
     */
    public InternshipDataService(InternshipRepository repo) {
        this.repo = repo;
    }

    /**
     * Creates a new internship and stores it in the repository.
     *
     * @param it the internship to create
     * @return the generated internship ID
     */
    public String createInternship(Internship it) {
        return repo.create(it);
    }

    /**
     * Retrieves all internships in the system.
     *
     * @return a list of all Internship objects
     */
    public List<Internship> getAllInternships() {
        return new ArrayList<>(repo.findAll());
    }

    /**
     * Retrieves an internship by its ID.
     *
     * @param id internship unique identifier
     * @return an Optional containing the internship if found
     */
    public Optional<Internship> getInternshipById(String id) {
        return repo.findById(id);
    }

    /**
     * Retrieves all internships created by a specific Company Representative.
     *
     * @param repId the representative's user ID
     * @return list of internships created by the rep
     */
    public List<Internship> getInternshipsByCompanyRep(String repId) {
        return repo.findAll().stream()
                .filter(i -> repId.equals(i.getCompanyRepId()))
                .collect(Collectors.toList());
    }

    /**
     * Retrieves all internships that are currently in Pending state.
     * Used by Staff to approve or reject postings.
     *
     * @return list of pending internships
     */
    public List<Internship> getPendingInternships() {
        return repo.findAll().stream()
                .filter(i -> "Pending".equalsIgnoreCase(i.getStatus()))
                .collect(Collectors.toList());
    }

    /**
     * Updates the status of an internship. If the status becomes Approved,
     * the internship is automatically made visible to students.
     *
     * @param id     internship ID
     * @param status new status string
     */
    public void setInternshipStatus(String id, String status) {
        repo.findById(id).ifPresent(it -> {
            it.setStatus(status);
            if ("Approved".equalsIgnoreCase(status)) {
                it.setVisible(true);
            }
        });
    }

    /**
     * Returns all internships that a student is allowed to view and apply for.
     *
     * <p>This method applies all student-side eligibility rules:
     * visibility, approval status, date constraints, major matching,
     * year restrictions, apply limits, and menu filters.</p>
     *
     * @param s               the student
     * @param statusFilter    extra filter for status
     * @param prefMajorFilter extra filter for major
     * @param levelFilter     extra filter for level
     * @return list of eligible internships sorted by title
     */
    public List<Internship> getVisibleInternshipsForStudent(
            Student s,
            String statusFilter,
            String prefMajorFilter,
            String levelFilter
    ) {
        LocalDate today = LocalDate.now();

        return repo.findAll().stream().filter(it -> {

            // 1) Must be visible
            if (!it.isVisible()) return false;

            // 2) Must be approved
            if (!"Approved".equalsIgnoreCase(it.getStatus())) return false;

            // 3) Date range
            if (it.getOpenDate() != null && today.isBefore(it.getOpenDate()))
                return false;
            if (it.getCloseDate() != null && today.isAfter(it.getCloseDate()))
                return false;

            // 4) Major match
            if (!s.getMajor().equalsIgnoreCase(it.getPreferredMajor()))
                return false;

            // 5) Year eligibility
            int year = s.getYear();
            if (year == 1 || year == 2) {
                if (!"Basic".equalsIgnoreCase(it.getLevel())) return false;
            }

            // 6) Student can still apply
            if (!s.canApplyMore()) return false;

            // 7) Extra filters
            if (!statusFilter.isBlank() &&
                    !it.getStatus().equalsIgnoreCase(statusFilter)) return false;

            if (!prefMajorFilter.isBlank() &&
                    !it.getPreferredMajor().equalsIgnoreCase(prefMajorFilter)) return false;

            if (!levelFilter.isBlank() &&
                    !it.getLevel().equalsIgnoreCase(levelFilter)) return false;

            return true;
        }).sorted(Comparator.comparing(Internship::getTitle))
                .collect(Collectors.toList());
    }

    /**
     * General multi-field filtering for Staff and CompanyRep use cases.
     *
     * <p>Supports filters for:</p>
     * <ul>
     *     <li>Status</li>
     *     <li>Preferred major</li>
     *     <li>Internship level</li>
     *     <li>Company name</li>
     *     <li>Visibility</li>
     *     <li>Closing date (exact, &lt;date, or &gt;date)</li>
     * </ul>
     *
     * @param status          status filter
     * @param major           major filter
     * @param level           level filter
     * @param company         company name filter
     * @param visibility      “visible” or “hidden”
     * @param closeDateString date filter (e.g. "2024-01-01", "&lt;2024-01-01")
     * @return list of internships matching the filter criteria
     */
    public List<Internship> filterInternships(
            String status,
            String major,
            String level,
            String company,
            String visibility,
            String closeDateString
    ) {
        LocalDate closeDate = null;

        try {
            if (closeDateString != null && !closeDateString.isBlank()) {
                String clean = closeDateString.replace("<", "").replace(">", "");
                closeDate = LocalDate.parse(clean);
            }
        } catch (Exception ignored) {}

        final String fStatus = status == null ? "" : status;
        final String fMajor = major == null ? "" : major;
        final String fLevel = level == null ? "" : level;
        final String fCompany = company == null ? "" : company;
        final String fVisibility = visibility == null ? "" : visibility;
        final String fCloseDateString = closeDateString == null ? "" : closeDateString;
        final LocalDate fCloseDate = closeDate;

        return repo.findAll().stream().filter(it -> {

            if (!fStatus.isBlank() &&
                    !it.getStatus().equalsIgnoreCase(fStatus)) return false;

            if (!fMajor.isBlank() &&
                    !it.getPreferredMajor().equalsIgnoreCase(fMajor)) return false;

            if (!fLevel.isBlank() &&
                    !it.getLevel().equalsIgnoreCase(fLevel)) return false;

            if (!fCompany.isBlank() &&
                    !it.getCompanyName().equalsIgnoreCase(fCompany)) return false;

            if (!fVisibility.isBlank()) {
                boolean wantVisible = fVisibility.equalsIgnoreCase("visible");
                if (it.isVisible() != wantVisible) return false;
            }

            if (fCloseDate != null) {
                if (it.getCloseDate() == null) return false;

                if (fCloseDateString.startsWith("<"))
                    return it.getCloseDate().isBefore(fCloseDate);
                if (fCloseDateString.startsWith(">"))
                    return it.getCloseDate().isAfter(fCloseDate);
                return it.getCloseDate().isEqual(fCloseDate);
            }

            return true;
        }).sorted(Comparator.comparing(Internship::getTitle))
                .toList();
    }

    /**
     * Deletes an internship entirely from the repository.
     *
     * @param id the internship ID to delete
     */
    public void deleteInternship(String id) {
        repo.delete(id);
    }

    /**
     * Removes all internship records from the repository.
     * Used during resets and testing.
     */
    public void clearInternships() {
        repo.clear();
    }

    /**
     * Returns all internships for saving to CSV.
     *
     * @return iterable collection of all internships
     */
    public Iterable<Internship> getAllForSave() {
        return repo.findAll();
    }
}
