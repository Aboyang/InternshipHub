package careerhub.services.student;

import careerhub.models.Internship;
import careerhub.models.Student;
import careerhub.storage.DataManager;

import java.util.List;
import java.util.Optional;
import java.util.Scanner;

/**
 * Provides the functionality for students to view all currently available
 * internships and apply to them. This service displays only internships
 * that are visible, approved, within date range, and match the student's
 * eligibility (major and level restrictions).
 *
 * <p>Responsibilities:</p>
 * <ul>
 *     <li>Retrieve all visible internships using
 *         {@link DataManager#getVisibleInternshipsForStudent(Student, String, String, String)}</li>
 *     <li>Display each internship's details in a student-friendly format</li>
 *     <li>Allow the student to select an internship and apply</li>
 *     <li>Validate eligibility (year, level, slots, visibility)</li>
 *     <li>Create the application via {@link DataManager#createApplication}</li>
 * </ul>
 */
public class StudentViewAllInternshipsService {

    /** Shared data manager for internship and application operations. */
    private final DataManager dm;

    /** Scanner for reading console input. */
    private final Scanner sc;

    /**
     * Constructs the service.
     *
     * @param dm shared {@link DataManager} instance
     * @param sc shared {@link Scanner} for user input
     */
    public StudentViewAllInternshipsService(DataManager dm, Scanner sc) {
        this.dm = dm;
        this.sc = sc;
    }

    /**
     * Shows all internships that the student is currently eligible to apply for.
     *
     * <p>Workflow:</p>
     * <ol>
     *     <li>Retrieve visible, approved, eligible internships from the data manager</li>
     *     <li>If none exist, print a message and return</li>
     *     <li>Display details (ID, title, company, dates, slots, visibility, status)</li>
     *     <li>Prompt the student to enter an internship ID to apply</li>
     *     <li>Validate visibility, approval, level eligibility, and slot limits</li>
     *     <li>Create the application and update the student record</li>
     * </ol>
     *
     * @param student the student viewing and applying for internships
     */
    public void viewAll(Student student) {

        List<Internship> list = dm.getVisibleInternshipsForStudent(
                student,
                "",
                "",
                ""
        );

        if (list.isEmpty()) {
            System.out.println("No available internships at the moment.");
            return;
        }

        System.out.println("Available internships:");
        for (Internship it : list) {
            System.out.printf(
                "%s | %s | %s | Level:%s | Major:%s | Open:%s | Close:%s | Slots:%d/%d | Status:%s | Visibility:%s\n",
                it.getId(),
                it.getTitle(),
                it.getCompanyName(),
                it.getLevel(),
                it.getPreferredMajor(),
                it.getOpenDate(),
                it.getCloseDate(),
                it.getConfirmedCount(),
                it.getSlots(),
                it.getStatus(),
                it.isVisible()
            );
        }

        System.out.print("Enter internship ID to apply (or press enter to exit): ");
        String id = sc.nextLine().trim();
        if (id.isEmpty()) return;

        Optional<Internship> opt = dm.getInternshipById(id);
        if (opt.isEmpty()) {
            System.out.println("Invalid internship ID.");
            return;
        }

        Internship it = opt.get();

        if (!it.isVisible() || !"Approved".equalsIgnoreCase(it.getStatus())) {
            System.out.println("Not open.");
            return;
        }

        if (!student.canApplyLevel(it.getLevel())) {
            System.out.println("Your year cannot apply for this level.");
            return;
        }

        if (!student.canApplyMore()) {
            System.out.println("Cannot apply: max 3 or already accepted placement.");
            return;
        }

        String appId = dm.createApplication(it.getId(), student.getId());
        student.apply(it.getId());
        System.out.println("Applied. Application ID: " + appId + " (status Pending).");
    }
}
