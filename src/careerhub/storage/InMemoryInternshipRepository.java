package careerhub.storage;

import careerhub.models.Internship;

import java.util.*;

/**
 * In-memory implementation of {@link InternshipRepository}.
 *
 * <p>This repository stores all {@link Internship} objects inside a
 * {@code HashMap}, using IDs such as I1, I2, I3... which auto-increment.</p>
 *
 * <p>It supports creating new internships, loading existing ones (e.g. from CSV),
 * finding by ID, retrieving all, and deleting. It contains no business logic,
 * only data storage.</p>
 */
public class InMemoryInternshipRepository implements InternshipRepository {

    /** Internal storage of internships mapped by ID. */
    private final Map<String, Internship> internships = new HashMap<>();

    /** Auto-increment counter for generating IDs (I1, I2...). */
    private int counter = 0;

    /**
     * Creates a new internship entry.
     *
     * <p>If the internship does not already have an ID, this method will generate
     * a new one using the counter (I1, I2...). If the internship already has an
     * ID (e.g. when loaded from CSV), the counter will be synced to ensure future
     * IDs continue the sequence.</p>
     *
     * @param internship the internship to create
     * @return the assigned or existing internship ID
     */
    @Override
    public String create(Internship internship) {
        if (internship.getId() == null || internship.getId().isBlank()) {
            counter++;
            internship.setId("I" + counter);
        } else {
            // If an ID is already present (CSV load), sync counter
            syncCounterFromId(internship.getId());
        }
        internships.put(internship.getId(), internship);
        return internship.getId();
    }

    /**
     * Saves an existing internship object into the repository.
     * Used when loading from CSV where IDs must be preserved.
     *
     * @param internship the internship to store
     */
    @Override
    public void save(Internship internship) {
        internships.put(internship.getId(), internship);
        syncCounterFromId(internship.getId());
    }

    /**
     * Finds a specific internship by its ID.
     *
     * @param id internship ID
     * @return optional internship
     */
    @Override
    public Optional<Internship> findById(String id) {
        return Optional.ofNullable(internships.get(id));
    }

    /**
     * Returns all internships currently stored.
     *
     * @return collection of internships
     */
    @Override
    public Collection<Internship> findAll() {
        return internships.values();
    }

    /**
     * Deletes an internship from storage.
     *
     * @param id internship ID
     */
    @Override
    public void delete(String id) {
        internships.remove(id);
    }

    /**
     * Removes all internships and resets the ID counter.
     */
    @Override
    public void clear() {
        internships.clear();
        counter = 0;
    }

    /**
     * Ensures the internal counter matches the largest ID
     * seen so far (e.g., I12 → counter >= 12).
     *
     * <p>This prevents ID duplication when loading pre-existing data.</p>
     *
     * @param id Internship ID
     */
    private void syncCounterFromId(String id) {
        try {
            if (id != null && id.length() > 1 && id.charAt(0) == 'I') {
                int n = Integer.parseInt(id.substring(1));
                counter = Math.max(counter, n);
            }
        } catch (Exception ignored) {}
    }

    /**
     * Exposes the internal map for debugging purposes.
     *
     * @return internal ID → Internship map
     */
    public Map<String, Internship> getInternalMap() {
        return internships;
    }
}
