package careerhub.services.company;

import careerhub.models.*;
import careerhub.storage.DataManager;

import java.util.List;
import java.util.Optional;
import java.util.Scanner;

/**
 * Service class responsible for allowing Company Representatives
 * to review, approve, or reject applications submitted for their
 * internship postings.
 *
 * <p>This class belongs to the Control layer and performs the following:</p>
 * <ul>
 *     <li>Lists all internships owned by the representative</li>
 *     <li>Displays all applications for a selected internship</li>
 *     <li>Displays applicant information (name, major, year)</li>
 *     <li>Allows approval or rejection of applications</li>
 *     <li>Delegates status updates to {@link DataManager}</li>
 * </ul>
 *
 * <p>Applications move into {@code SUCCESSFUL} or {@code UNSUCCESSFUL}
 * states depending on the representative’s decision.</p>
 */
public class ReviewApplicationsService {

    /** Shared DataManager used to load and update application data. */
    private final DataManager dm;

    /** Shared Scanner for reading menu input. */
    private final Scanner sc;

    /**
     * Constructs the service using dependency injection.
     *
     * @param dm the shared DataManager instance
     * @param sc the shared Scanner instance
     */
    public ReviewApplicationsService(DataManager dm, Scanner sc) {
        this.dm = dm;
        this.sc = sc;
    }

    /**
     * Executes the full workflow for reviewing applications belonging
     * to the representative's internships.
     *
     * <p>The steps include:</p>
     * <ol>
     *     <li>List internships created by the Company Representative</li>
     *     <li>Prompt for internship ID</li>
     *     <li>List all applications for that internship</li>
     *     <li>Display applicant details (name, major, year)</li>
     *     <li>Prompt for an application ID to approve or reject</li>
     *     <li>Update the application status using {@link DataManager}</li>
     * </ol>
     *
     * <p>If the internship has no applications, or if the entered ID is blank,
     * the method returns gracefully.</p>
     *
     * @param rep the Company Representative performing the review
     */
    public void review(CompanyRep rep) {

        List<Internship> mine = dm.getInternshipsByCompanyRep(rep.getId());
        if (mine.isEmpty()) {
            System.out.println("You have no internships.");
            return;
        }

        System.out.println("\nYour Internships:");
        for (Internship it : mine) {
            System.out.printf(
                "%s | %s | Status:%s | Visible:%s | Confirmed:%d/%d\n",
                it.getId(), it.getTitle(), it.getStatus(),
                it.isVisible(), it.getConfirmedCount(), it.getSlots()
            );
        }

        System.out.print("Enter internship ID to view applications: ");
        String iid = sc.nextLine().trim();

        List<Application> apps = dm.getApplicationsForInternship(iid);
        if (apps.isEmpty()) {
            System.out.println("No applications for this internship.");
            return;
        }

        System.out.println("\nApplications for Internship " + iid + ":");
        for (Application a : apps) {

            Optional<User> uo = dm.findUserById(a.getStudentId());
            String name = "(unknown)", major = "(unknown)", year = "(unknown)";

            if (uo.isPresent() && uo.get() instanceof Student stu) {
                name = stu.getName();
                major = stu.getMajor();
                year = String.valueOf(stu.getYear());
            }

            System.out.printf(
                "%s | Student:%s (%s) | Major:%s | Year:%s | Status:%s | WithdrawalReq:%s | ConfirmedByStudent:%s\n",
                a.getId(), a.getStudentId(), name, major, year,
                a.getStatus(), a.isWithdrawalRequested(), a.isConfirmedByStudent()
            );
        }

        System.out.print("Enter application ID to approve/reject (blank to cancel): ");
        String aid = sc.nextLine().trim();
        if (aid.isBlank()) return;

        System.out.print("Approve this application? (Y/N): ");
        String dec = sc.nextLine().trim();

        if (dec.equalsIgnoreCase("Y")) {
            dm.setApplicationStatus(aid, "Successful");
            System.out.println("Application approved → Successful.");
        } else {
            dm.setApplicationStatus(aid, "Unsuccessful");
            System.out.println("Application rejected → Unsuccessful.");
        }
    }
}
