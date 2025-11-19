package careerhub.services.student;

import careerhub.models.Internship;
import careerhub.models.Student;
import careerhub.storage.DataManager;

import java.util.List;
import java.util.Scanner;

/**
 * Provides filtering functionality for students to search internships
 * based on previously selected or newly entered filter criteria.
 *
 * <p>This service supports:</p>
 * <ul>
 *     <li>Using and updating the student's persistent filter preferences</li>
 *     <li>Filtering by status, preferred major, level, and closing date</li>
 *     <li>Displaying only internships that are visible to students</li>
 *     <li>Delegating actual filtering logic to {@link DataManager#filterInternships}</li>
 * </ul>
 *
 * <p>Only visible internships are shown, and company-based filtering is
 * intentionally omitted for student users.</p>
 */
public class StudentFilterInternshipsService {

    /** Provides access to application, internship, and user data. */
    private final DataManager dm;

    /** Scanner used to accept user input from the console. */
    private final Scanner sc;

    /**
     * Constructs an instance of the student internship filter service.
     *
     * @param dm the shared {@link DataManager} used for filtering operations
     * @param sc the shared {@link Scanner} for reading console input
     */
    public StudentFilterInternshipsService(DataManager dm, Scanner sc) {
        this.dm = dm;
        this.sc = sc;
    }

    /**
     * Allows a student to filter internships by updating and using their
     * stored filter preferences.
     *
     * <p>Workflow:</p>
     * <ol>
     *     <li>Prompt the student for each filter field, allowing Enter to keep the previous value</li>
     *     <li>Update the student's saved filter settings</li>
     *     <li>Call {@link DataManager#filterInternships} with the student's filters</li>
     *     <li>Display matching internships, or notify if none found</li>
     * </ol>
     *
     * <p>Students only see internships that are visible; company name is not included
     * as a filter option in the student view.</p>
     *
     * @param student the student whose filter preferences and eligibility apply
     */
    public void filter(Student student) {

        System.out.println("\nFilter by (press Enter to keep previous values):");

        System.out.print("Status [" + student.getLastStatusFilter() + "]: ");
        String stf = sc.nextLine().trim();
        if (!stf.isBlank()) student.setLastStatusFilter(stf);

        System.out.print("Preferred Major [" + student.getLastMajorFilter() + "]: ");
        String mf = sc.nextLine().trim();
        if (!mf.isBlank()) student.setLastMajorFilter(mf);

        System.out.print("Level (Basic/Intermediate/Advanced) [" + student.getLastLevelFilter() + "]: ");
        String lf = sc.nextLine().trim();
        if (!lf.isBlank()) student.setLastLevelFilter(lf);

        System.out.print(
                "Closing Date (<YYYY-MM-DD / >YYYY-MM-DD / YYYY-MM-DD) [" 
                + student.getLastCloseFilter() + "]: "
        );
        String cf = sc.nextLine().trim();
        if (!cf.isBlank()) student.setLastCloseFilter(cf);

        // ---- Run filter query ----
        List<Internship> list = dm.filterInternships(
                student.getLastStatusFilter(),
                student.getLastMajorFilter(),
                student.getLastLevelFilter(),
                "",            // no company filter for students
                "visible",     // only visible ones
                student.getLastCloseFilter()
        );

        // ---- Results ----
        if (list.isEmpty()) {
            System.out.println("No internships found.");
            return;
        }

        System.out.println("\nFiltered internships:");
        for (Internship it : list) {
            System.out.printf(
                "%s | %s | Level:%s | Major:%s | Status:%s | Close:%s\n",
                it.getId(),
                it.getTitle(),
                it.getLevel(),
                it.getPreferredMajor(),
                it.getStatus(),
                it.getCloseDate()
            );
        }
    }
}
