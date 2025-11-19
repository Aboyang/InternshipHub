package careerhub.services.student;

import careerhub.models.Application;
import careerhub.models.Internship;
import careerhub.models.Student;
import careerhub.storage.DataManager;

import java.util.List;
import java.util.Optional;
import java.util.Scanner;

/**
 * Provides the functionality for students to view and manage their internship
 * applications. This includes accepting successful placements, requesting
 * withdrawals, and viewing the current status of all applications.
 *
 * <p>This service performs the following roles:</p>
 * <ul>
 *     <li>Display all applications belonging to the logged-in student</li>
 *     <li>Display internship information associated with each application</li>
 *     <li>Allow acceptance of successful offers</li>
 *     <li>Allow withdrawal requests regardless of status (subject to workflow rules)</li>
 *     <li>Delegate status changes to {@link DataManager}</li>
 * </ul>
 *
 * <p>The service is invoked by {@code StudentMenu} whenever the student selects
 * “My applications”.</p>
 */
public class StudentManageApplicationsService {

    /** Shared DataManager for retrieving and updating application data. */
    private final DataManager dm;

    /** Scanner used for reading console input. */
    private final Scanner sc;

    /**
     * Constructs a new service instance.
     *
     * @param dm the shared {@link DataManager} for application operations
     * @param sc the shared {@link Scanner} for user input
     */
    public StudentManageApplicationsService(DataManager dm, Scanner sc) {
        this.dm = dm;
        this.sc = sc;
    }

    /**
     * Main workflow for managing student applications.
     *
     * <p>This method will:</p>
     * <ol>
     *     <li>Retrieve all applications belonging to the student</li>
     *     <li>Display each application together with internship details
     *         (title, visibility, status, withdrawal request, confirmation)</li>
     *     <li>Allow the student to select one application to act upon</li>
     *     <li>If the application is successful:
     *         <ul>
     *             <li>Allow accepting the offer</li>
     *             <li>Allow requesting withdrawal</li>
     *         </ul>
     *     </li>
     *     <li>If the application is not successful:
     *         <ul>
     *             <li>Allow requesting withdrawal</li>
     *         </ul>
     *     </li>
     *     <li>Submit actions to {@link DataManager} for persistence and logic updates</li>
     * </ol>
     *
     * @param student the student whose applications are being managed
     */
    public void manage(Student student) {

        List<Application> apps = dm.getApplicationsByStudent(student.getId());
        if (apps.isEmpty()) {
            System.out.println("No applications.");
            return;
        }

        System.out.println("Your applications:");
        for (Application a : apps) {

            Optional<Internship> io = dm.getInternshipById(a.getInternshipId());
            String visibility = io.map(i -> String.valueOf(i.isVisible())).orElse("Unknown");
            String title      = io.map(Internship::getTitle).orElse("Unknown");

            System.out.printf(
                "%s | Internship:%s (%s) | Visible:%s | Status:%s | WithdrawalReq:%s | ConfirmedByStudent:%s\n",
                a.getId(),
                a.getInternshipId(),
                title,
                visibility,
                a.getStatus(),
                a.isWithdrawalRequested(),
                a.isConfirmedByStudent()
            );
        }

        System.out.print("Enter application id to act on (withdraw / accept) or blank: ");
        String aid = sc.nextLine().trim();
        if (aid.isEmpty()) return;

        Optional<Application> ao = apps.stream().filter(x -> x.getId().equals(aid)).findFirst();
        if (ao.isEmpty()) {
            System.out.println("Invalid ID.");
            return;
        }

        Application a = ao.get();

        if ("Successful".equalsIgnoreCase(a.getStatus())) {

            System.out.print("Type 'accept' to accept placement or 'withdraw' to request withdrawal: ");
            String act = sc.nextLine().trim();

            if (act.equalsIgnoreCase("accept")) {
                dm.studentAcceptPlacement(student.getId(), a.getInternshipId());
                student.acceptPlacement(a.getInternshipId());
                System.out.println("Placement accepted. Other applications withdrawn.");
            }
            else if (act.equalsIgnoreCase("withdraw")) {
                a.requestWithdrawal();
                dm.markWithdrawalRequest(a.getId());
                System.out.println("Withdrawal requested (awaiting staff approval).");
            }
        }
        else {

            System.out.print("Type 'withdraw' to request withdrawal or blank: ");
            String act = sc.nextLine().trim();

            if (act.equalsIgnoreCase("withdraw")) {
                a.requestWithdrawal();
                dm.markWithdrawalRequest(a.getId());
                System.out.println("Withdrawal requested (awaiting staff approval).");
            }
        }
    }
}
