package careerhub.services.staff;

import careerhub.models.Application;
import careerhub.storage.DataManager;

import java.util.List;
import java.util.Scanner;

/**
 * Service that allows Career Center Staff to process withdrawal requests
 * submitted by students for their internship applications.
 *
 * <p>This service enables staff to:</p>
 * <ul>
 *     <li>Retrieve all applications where a withdrawal was requested</li>
 *     <li>Display each request with student and internship details</li>
 *     <li>Approve or reject a withdrawal request</li>
 * </ul>
 *
 * <p>When a withdrawal is approved, the underlying behavior is delegated to
 * {@link DataManager#approveWithdrawal(String)}, which updates application
 * status and removes the student from the internship’s applicant list.</p>
 */
public class ProcessWithdrawalService {

    /** DataManager used to query and update application and internship data. */
    private final DataManager dm;

    /** Shared Scanner instance used for user input from the console. */
    private final Scanner sc;

    /**
     * Creates a new withdrawal-processing service.
     *
     * @param dm DataManager for executing workflow operations
     * @param sc shared Scanner for reading console input
     */
    public ProcessWithdrawalService(DataManager dm, Scanner sc) {
        this.dm = dm;
        this.sc = sc;
    }

    /**
     * Allows staff to process pending withdrawal requests.
     *
     * <p>Workflow:</p>
     * <ol>
     *     <li>Retrieve all withdrawal requests using {@link DataManager#getWithdrawalRequests()}</li>
     *     <li>Display each application's ID, student ID, internship ID, and current status</li>
     *     <li>Prompt staff to select an application ID for action</li>
     *     <li>Input "approve" will trigger {@link DataManager#approveWithdrawal(String)}</li>
     *     <li>Input "reject" will keep the application unchanged</li>
     * </ol>
     *
     * <p>If no withdrawal requests exist, the method exits immediately.</p>
     */
    public void process() {

        List<Application> requests = dm.getWithdrawalRequests();
        if (requests.isEmpty()) {
            System.out.println("No withdrawal requests.");
            return;
        }

        for (Application a : requests) {
            System.out.printf(
                    "%s | Student:%s | Internship:%s | Status:%s\n",
                    a.getId(),
                    a.getStudentId(),
                    a.getInternshipId(),
                    a.getStatus()
            );
        }

        System.out.print("Enter application id to approve/reject withdrawal: ");
        String aid = sc.nextLine().trim();
        if (aid.isBlank()) return;

        System.out.print("Type 'approve' or 'reject': ");
        String dec = sc.nextLine().trim();

        if (dec.equalsIgnoreCase("approve")) {
            dm.approveWithdrawal(aid);
            System.out.println("Withdrawal approved; application set to Unsuccessful.");
        } else {
            System.out.println("Withdrawal rejected (no change).");
        }
    }
}
