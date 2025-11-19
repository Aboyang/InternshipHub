package careerhub.storage;

import careerhub.models.Application;

import java.util.*;
import java.util.stream.Collectors;

/**
 * In-memory implementation of {@link ApplicationRepository}.
 *
 * <p>This repository stores all {@link Application} objects inside a
 * {@code HashMap}, using IDs such as A1, A2, A3... which auto-increment.
 * It is used by the system as the main runtime storage and also supports
 * syncing the ID counter when loading from CSV (where IDs already exist).</p>
 *
 * <p>This class contains no business logic; it is purely data storage.</p>
 */
public class InMemoryApplicationRepository implements ApplicationRepository {

    /** Internal map storing applications by their IDs. */
    private final Map<String, Application> applications = new HashMap<>();

    /** Counter used to generate sequential IDs (A1, A2...). */
    private int counter = 0;

    /**
     * Generates a new ID and creates a new {@link Application}.
     *
     * @param internshipId the internship the student is applying to
     * @param studentId    the student who is applying
     * @return the generated application ID
     */
    @Override
    public String create(String internshipId, String studentId) {
        counter++;
        String id = "A" + counter;
        Application app = new Application(id, internshipId, studentId);
        applications.put(id, app);
        return id;
    }

    /**
     * Saves an existing application into the repository.
     * Used when loading from CSV where IDs already exist.
     *
     * @param app application instance to store
     */
    @Override
    public void save(Application app) {
        applications.put(app.getId(), app);
        syncCounterFromId(app.getId());
    }

    /**
     * Retrieves a single application by ID.
     *
     * @param id application ID
     * @return optional application
     */
    @Override
    public Optional<Application> findById(String id) {
        return Optional.ofNullable(applications.get(id));
    }

    /**
     * Returns all applications stored in the repository.
     *
     * @return collection of all applications
     */
    @Override
    public Collection<Application> findAll() {
        return applications.values();
    }

    /**
     * Clears all applications and resets the ID counter.
     */
    @Override
    public void clear() {
        applications.clear();
        counter = 0;
    }

    /**
     * Retrieves all applications submitted by a specific student.
     *
     * @param studentId student ID
     * @return list of applications
     */
    @Override
    public List<Application> findByStudentId(String studentId) {
        return applications.values().stream()
                .filter(a -> a.getStudentId().equals(studentId))
                .collect(Collectors.toList());
    }

    /**
     * Retrieves all applications submitted for a specific internship.
     *
     * @param internshipId internship ID
     * @return list of applications
     */
    @Override
    public List<Application> findByInternshipId(String internshipId) {
        return applications.values().stream()
                .filter(a -> a.getInternshipId().equals(internshipId))
                .collect(Collectors.toList());
    }

    /**
     * Deletes all applications associated with a given internship.
     *
     * @param internshipId internship ID
     */
    @Override
    public void deleteByInternshipId(String internshipId) {
        applications.values().removeIf(a -> a.getInternshipId().equals(internshipId));
    }

    /**
     * Ensures the internal ID counter is at least as large as the
     * numeric portion of the given ID (e.g., A15 → counter >= 15).
     *
     * <p>This is needed so that after loading from CSV, newly created
     * applications continue the correct numbering sequence.</p>
     *
     * @param id application ID
     */
    private void syncCounterFromId(String id) {
        try {
            if (id != null && id.length() > 1 && id.charAt(0) == 'A') {
                int n = Integer.parseInt(id.substring(1));
                counter = Math.max(counter, n);
            }
        } catch (Exception ignored) {}
    }

    /**
     * Exposes the internal storage map.
     * <p>Used only for debugging or diagnostics.</p>
     *
     * @return internal map of application ID → Application
     */
    public Map<String, Application> getInternalMap() {
        return applications;
    }
}
