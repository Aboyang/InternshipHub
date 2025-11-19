package careerhub.services.staff;

import careerhub.models.Internship;
import careerhub.storage.DataManager;

import java.util.List;
import java.util.Optional;
import java.util.Scanner;

/**
 * Service used by Career Center Staff to review and approve or reject
 * pending {@link Internship} submissions created by Company Representatives.
 *
 * <p>This class handles:</p>
 * <ul>
 *     <li>Listing all internships that are currently in PENDING status</li>
 *     <li>Allowing Staff to select a specific internship by S/N or ID</li>
 *     <li>Prompting for approval or rejection</li>
 *     <li>Updating the internship's status in the system</li>
 * </ul>
 *
 * <p>Approving an internship automatically sets it to visible for students,
 * as enforced inside {@link DataManager#setInternshipStatus(String, String)}.</p>
 */
public class ApproveInternshipService {

    /** DataManager providing access to internship and user data. */
    private final DataManager dm;

    /** Shared Scanner for receiving CLI input. */
    private final Scanner sc;

    /**
     * Constructs the internship approval service.
     *
     * @param dm DataManager instance used for reading/updating internships
     * @param sc shared Scanner used for user input
     */
    public ApproveInternshipService(DataManager dm, Scanner sc) {
        this.dm = dm;
        this.sc = sc;
    }

    /**
     * Starts the approval/rejection flow for pending internships.
     *
     * <p>Process steps:</p>
     * <ol>
     *     <li>Retrieve all internships with status PENDING</li>
     *     <li>Display list for Staff to choose from</li>
     *     <li>Allow selection by serial number or internship ID</li>
     *     <li>Prompt Staff to approve or reject</li>
     *     <li>Update status using {@link DataManager#setInternshipStatus(String, String)}</li>
     * </ol>
     *
     * <p>If no pending internships exist, the method exits immediately.</p>
     */
    public void approveOrReject() {

        List<Internship> pending = dm.getPendingInternships();
        if (pending.isEmpty()) {
            System.out.println("No pending internships.");
            return;
        }

        int idx = 1;
        for (Internship i : pending) {
            System.out.printf("S/N: %d | ID: %s | Title: %s | Company: %s\n",
                    idx, i.getId(), i.getTitle(), i.getCompanyName());
            idx++;
        }

        String iid = null;

        while (true) {
            System.out.print("Select internship by S/N or ID (blank to cancel): ");
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
                iid = pending.get(sel - 1).getId();
                break;
            } else {
                Optional<Internship> match =
                        pending.stream().filter(i -> i.getId().equalsIgnoreCase(input)).findFirst();
                if (match.isPresent()) {
                    iid = match.get().getId();
                    break;
                }
                System.out.println("No such internship.");
            }
        }

        String dec;
        while (true) {
            System.out.print("Approve this internship? (Y/N): ");
            dec = sc.nextLine().trim();
            if (dec.equalsIgnoreCase("Y") || dec.equalsIgnoreCase("N")) break;
            System.out.println("Invalid choice.");
        }

        boolean approve = dec.equalsIgnoreCase("Y");
        dm.setInternshipStatus(iid, approve ? "Approved" : "Rejected");

        System.out.println("Done.");
    }
}
