package careerhub.services.staff;

import careerhub.models.CompanyRep;
import careerhub.storage.DataManager;

import java.util.List;
import java.util.Optional;
import java.util.Scanner;

/**
 * Service used by Career Center Staff to approve or reject pending
 * {@link CompanyRep} accounts. 
 *
 * <p>This class handles the interactive selection of a pending representative,
 * validates input (email or serial number), and delegates approval decisions
 * to {@link DataManager}.</p>
 *
 * <p>Responsibilities:</p>
 * <ul>
 *     <li>Retrieve all unapproved Company Representatives</li>
 *     <li>Allow Staff to select a representative to evaluate</li>
 *     <li>Prompt Staff to approve or reject the account</li>
 *     <li>Update account approval status in the system</li>
 * </ul>
 */
public class ApproveCompanyRepService {

    /** Shared DataManager used for retrieving and updating representatives. */
    private final DataManager dm;

    /** Scanner used for interactive CLI input. */
    private final Scanner sc;

    /**
     * Creates a service instance for approving Company Representatives.
     *
     * @param dm DataManager providing user data access
     * @param sc shared Scanner for CLI input
     */
    public ApproveCompanyRepService(DataManager dm, Scanner sc) {
        this.dm = dm;
        this.sc = sc;
    }

    /**
     * Starts the interactive approval/rejection flow.
     *
     * <p>Process:</p>
     * <ol>
     *     <li>List all pending Company Representative accounts</li>
     *     <li>Allow Staff to select by S/N (list number) or email (ID)</li>
     *     <li>Prompt to approve or reject</li>
     *     <li>Calls {@code DataManager.approveCompanyRep()}</li>
     * </ol>
     *
     * <p>If no pending accounts exist, the method exits immediately.</p>
     */
    public void approveOrReject() {

        List<CompanyRep> pending = dm.getPendingCompanyReps();
        if (pending.isEmpty()) {
            System.out.println("No pending reps.");
            return;
        }

        int idx = 1;
        for (CompanyRep r : pending) {
            System.out.printf("S/N: %d | Email(ID): %s | Name: %s | Company: %s\n",
                    idx, r.getId(), r.getName(), r.getCompanyName());
            idx++;
        }

        String rid = null;

        while (true) {
            System.out.print("Select rep by S/N or email (blank to cancel): ");
            String input = sc.nextLine().trim();

            if (input.isBlank()) {
                System.out.println("Cancelled.");
                return;
            }

            if (input.matches("\\d+")) {
                int sel = Integer.parseInt(input);
                if (sel < 1 || sel > pending.size()) {
                    System.out.println("Invalid number.");
                    continue;
                }
                rid = pending.get(sel - 1).getId();
                break;
            } else {
                Optional<CompanyRep> match =
                        pending.stream().filter(r -> r.getId().equalsIgnoreCase(input)).findFirst();
                if (match.isPresent()) {
                    rid = match.get().getId();
                    break;
                }
                System.out.println("No such representative.");
            }
        }

        String dec;
        while (true) {
            System.out.print("Approve this representative? (Y/N): ");
            dec = sc.nextLine().trim();
            if (dec.equalsIgnoreCase("Y") || dec.equalsIgnoreCase("N")) break;
            System.out.println("Invalid choice.");
        }

        boolean approve = dec.equalsIgnoreCase("Y");
        dm.approveCompanyRep(rid, approve);

        System.out.println("Done.");
    }
}
