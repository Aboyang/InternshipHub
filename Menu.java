package careerhub;

import careerhub.models.*;
import careerhub.storage.DataManager;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class Menu {
    private final DataManager dm;
    private final Scanner sc = new Scanner(System.in);
    public Menu(DataManager dm) { this.dm = dm; }

    public void start() {
        while (true) {
            System.out.println("\n=== Internship Hub ===");
            System.out.println("1) Login");
            System.out.println("2) Register Company Representative");
            System.out.println("3) Exit");
            System.out.print("> ");
            String cmd = sc.nextLine().trim();
            if (cmd.equals("1")) loginFlow();
            else if (cmd.equals("2")) registerCompanyRepFlow();
            else if (cmd.equals("3")) break;
            else System.out.println("Invalid option.");
        }
    }

    private void loginFlow() {
        System.out.print("User ID: ");
        String id = sc.nextLine().trim();
        System.out.print("Password: ");
        String pw = sc.nextLine().trim();
        Optional<User> uo = dm.findUserById(id);
        if (uo.isEmpty()) { System.out.println("No such user."); return; }
        User u = uo.get();
        if (!u.checkPassword(pw)) { System.out.println("Wrong password."); return; }
        System.out.println("Welcome, " + u.getName() + " (" + u.getType() + ")");
        if (u instanceof Student) studentMenu((Student) u);
        else if (u instanceof CompanyRep) companyMenu((CompanyRep) u);
        else if (u instanceof Staff) staffMenu((Staff) u);
        else System.out.println("Unknown user type.");
    }

    private void registerCompanyRepFlow() {
        System.out.println("Register Company Representative (needs staff approval later).");
        System.out.print("ID (use your company email): ");
        String id = sc.nextLine().trim();
        if (dm.findUserById(id).isPresent()) { System.out.println("ID already exists."); return; }
        System.out.print("Name: ");
        String name = sc.nextLine().trim();
        System.out.print("Company name: ");
        String company = sc.nextLine().trim();
        System.out.print("Department: ");
        String dept = sc.nextLine().trim();
        System.out.print("Position: ");
        String pos = sc.nextLine().trim();

        CompanyRep rep = new CompanyRep(id, name, "password", company, dept, pos);
        dm.registerCompanyRep(rep);
        System.out.println("Registered. Wait for Career Center Staff approval before you can create internships.");
    }

    // ---------------- Student menu ----------------
    private void studentMenu(Student s) {
        while (true) {
            System.out.println("\nStudent Menu:");
            System.out.println("1) View internships (with filters)");
            System.out.println("2) My applications");
            System.out.println("3) Change password");
            System.out.println("4) Logout");
            System.out.print("> ");
            String o = sc.nextLine().trim();
            if (o.equals("1")) {
                handleViewInternshipsForStudent(s);
            } else if (o.equals("2")) {
                handleStudentApplications(s);
            } else if (o.equals("3")) {
                changePasswordFlow(s);
            } else if (o.equals("4")) break;
            else System.out.println("Invalid.");
        }
    }

    private void handleViewInternshipsForStudent(Student s) {
        System.out.println("Filter by (press Enter to skip):");
        System.out.print("Status (Pending/Approved/Rejected/Filled): ");
        String status = sc.nextLine().trim();
        System.out.print("Preferred Major: ");
        String major = sc.nextLine().trim();
        System.out.print("Level (Basic/Intermediate/Advanced): ");
        String level = sc.nextLine().trim();

        List<Internship> list = dm.getVisibleInternshipsForStudent(s, status, major, level);
        if (list.isEmpty()) { System.out.println("No internships found."); return; }
        System.out.println("Available internships:");
        for (Internship it : list) {
            System.out.printf("%s | %s | Level:%s | PrefMajor:%s | Status:%s | Slots:%d | Visible:%s\n",
                    it.getId(), it.getTitle(), it.getLevel(), it.getPreferredMajor(), it.getStatus(), it.getSlots(), it.isVisible());
        }
        System.out.print("Enter internship ID to apply (or blank): ");
        String id = sc.nextLine().trim();
        if (id.isEmpty()) return;
        if (!dm.getInternshipById(id).isPresent()) { System.out.println("Invalid internship id."); return; }
        Internship it = dm.getInternshipById(id).get();
        if (!it.isVisible() || !"Approved".equalsIgnoreCase(it.getStatus())) { System.out.println("Not open."); return; }
        if (!s.canApplyLevel(it.getLevel())) { System.out.println("Your year cannot apply for this level."); return; }
        if (!s.canApplyMore()) { System.out.println("Cannot apply: max 3 or already accepted placement."); return; }
        String appId = dm.createApplication(it.getId(), s.getId());
        s.apply(it.getId());
        System.out.println("Applied. Application id: " + appId + " (status Pending).");
    }

    private void handleStudentApplications(Student s) {
        List<Application> apps = dm.getApplicationsByStudent(s.getId());
        if (apps.isEmpty()) { System.out.println("No applications."); return; }
        System.out.println("Your applications:");
        for (Application a : apps) {
            System.out.printf("%s | Internship:%s | Status:%s | WithdrawalReq:%s | ConfirmedByStudent:%s\n",
                    a.getId(), a.getInternshipId(), a.getStatus(), a.isWithdrawalRequested(), a.isConfirmedByStudent());
        }
        System.out.print("Enter application id to act on (withdraw / accept) or blank: ");
        String aid = sc.nextLine().trim();
        if (aid.isEmpty()) return;
        Optional<Application> ao = apps.stream().filter(x->x.getId().equals(aid)).findFirst();
        if (ao.isEmpty()) { System.out.println("Invalid id."); return; }
        Application a = ao.get();
        if ("Successful".equalsIgnoreCase(a.getStatus())) {
            System.out.print("Type 'accept' to accept placement or 'withdraw' to request withdrawal: ");
            String act = sc.nextLine().trim();
            if (act.equalsIgnoreCase("accept")) {
                dm.studentAcceptPlacement(s.getId(), a.getInternshipId());
                s.acceptPlacement(a.getInternshipId());
                System.out.println("Placement accepted. Other applications withdrawn.");
            } else if (act.equalsIgnoreCase("withdraw")) {
                a.requestWithdrawal();
                dm.markWithdrawalRequest(a.getId());
                System.out.println("Withdrawal requested (awaiting staff approval).");
            }
        } else {
            System.out.print("Type 'withdraw' to request withdrawal or blank: ");
            String act = sc.nextLine().trim();
            if (act.equalsIgnoreCase("withdraw")) {
                a.requestWithdrawal();
                dm.markWithdrawalRequest(a.getId());
                System.out.println("Withdrawal requested (awaiting staff approval).");
            }
        }
    }

    // ---------------- CompanyRep menu ----------------
    private void companyMenu(CompanyRep rep) {
        if (!rep.isApproved()) {
            System.out.println("Your account is not approved yet by Career Center Staff. You cannot create internships.");
            return;
        }
        while (true) {
            System.out.println("\nCompanyRep Menu:");
            System.out.println("1) Create internship");
            System.out.println("2) View my internships");
            System.out.println("3) Toggle internship visibility");
            System.out.println("4) View applications for an internship and approve/reject");
            System.out.println("5) Change password");
            System.out.println("6) Logout");
            System.out.print("> ");
            String o = sc.nextLine().trim();
            if (o.equals("1")) {
                if (!rep.canCreateMoreInternships()) { System.out.println("Reached max 5 internships."); continue; }
                System.out.print("Title: "); String title = sc.nextLine().trim();
                System.out.print("Description: "); String desc = sc.nextLine().trim();
                System.out.print("Level (Basic/Intermediate/Advanced): "); String level = sc.nextLine().trim();
                System.out.print("Preferred major: "); String pref = sc.nextLine().trim();
                System.out.print("Open date (YYYY-MM-DD) or blank: "); String openS = sc.nextLine().trim();
                System.out.print("Close date (YYYY-MM-DD) or blank: "); String closeS = sc.nextLine().trim();
                LocalDate open = openS.isBlank() ? null : LocalDate.parse(openS);
                LocalDate close = closeS.isBlank() ? null : LocalDate.parse(closeS);
                System.out.print("Slots (1-10): "); int slots = Integer.parseInt(sc.nextLine().trim());
                Internship it = new Internship(null, title, desc, level, pref, open, close, rep.getCompanyName(), rep.getId(), slots);
                String iid = dm.createInternship(it);
                rep.addCreatedInternship(iid);
                System.out.println("Created internship " + iid + " (Pending staff approval).");
            } else if (o.equals("2")) {
                List<Internship> mine = dm.getInternshipsByCompanyRep(rep.getId());
                if (mine.isEmpty()) { System.out.println("No internships created."); continue; }
                for (Internship i : mine) {
                    System.out.printf("%s | %s | Status:%s | Visible:%s | Confirmed:%d/%d\n",
                            i.getId(), i.getTitle(), i.getStatus(), i.isVisible(), i.getConfirmedCount(), i.getSlots());
                }
            } else if (o.equals("3")) {
                System.out.print("Internship id to toggle visibility: "); String iid = sc.nextLine().trim();
                Optional<Internship> io = dm.getInternshipById(iid);
                if (io.isEmpty()) { System.out.println("Invalid id."); continue; }
                Internship i = io.get();
                i.setVisible(!i.isVisible());
                System.out.println("Visibility now: " + i.isVisible());
            } else if (o.equals("4")) {
                System.out.print("Internship id to view applications: "); String iid = sc.nextLine().trim();
                List<Application> apps = dm.getApplicationsForInternship(iid);
                if (apps.isEmpty()) { System.out.println("No applications."); continue; }
                for (Application a : apps) {
                    System.out.printf("%s | Student:%s | Status:%s | WithdrawalReq:%s\n", a.getId(), a.getStudentId(), a.getStatus(), a.isWithdrawalRequested());
                }
                System.out.print("Enter application id to Approve/Reject or blank: ");
                String aid = sc.nextLine().trim();
                if (aid.isBlank()) continue;
                System.out.print("Type 'approve' or 'reject': "); String dec = sc.nextLine().trim();
                if (dec.equalsIgnoreCase("approve")) {
                    dm.setApplicationStatus(aid, "Successful");
                    System.out.println("Application approved -> Successful.");
                } else {
                    dm.setApplicationStatus(aid, "Unsuccessful");
                    System.out.println("Application rejected -> Unsuccessful.");
                }
            } else if (o.equals("5")) changePasswordFlow(rep);
            else if (o.equals("6")) break;
            else System.out.println("Invalid.");
        }
    }

    // ---------------- Staff menu ----------------
    private void staffMenu(Staff st) {
        while (true) {
            System.out.println("\nStaff Menu:");
            System.out.println("1) Approve/reject company representatives");
            System.out.println("2) Approve/reject internships");
            System.out.println("3) Process withdrawal requests");
            System.out.println("4) Change password");
            System.out.println("5) Logout");
            System.out.print("> ");
            String o = sc.nextLine().trim();
            if (o.equals("1")) {
                List<CompanyRep> pending = dm.getPendingCompanyReps();
                if (pending.isEmpty()) { System.out.println("No pending reps."); continue; }
                for (CompanyRep r : pending) System.out.printf("%s | %s | %s\n", r.getId(), r.getName(), r.getCompanyName());
                System.out.print("Enter rep id to approve/reject or blank: ");
                String rid = sc.nextLine().trim();
                if (rid.isBlank()) continue;
                System.out.print("Type 'approve' or 'reject': "); String dec = sc.nextLine().trim();
                dm.approveCompanyRep(rid, dec.equalsIgnoreCase("approve"));
                System.out.println("Done.");
            } else if (o.equals("2")) {
                List<Internship> pending = dm.getPendingInternships();
                if (pending.isEmpty()) { System.out.println("No pending internships."); continue; }
                for (Internship i : pending) System.out.printf("%s | %s | Company:%s\n", i.getId(), i.getTitle(), i.getCompanyName());
                System.out.print("Enter internship id to approve/reject or blank: ");
                String iid = sc.nextLine().trim();
                if (iid.isBlank()) continue;
                System.out.print("Type 'approve' or 'reject': "); String dec = sc.nextLine().trim();
                dm.setInternshipStatus(iid, dec.equalsIgnoreCase("approve") ? "Approved" : "Rejected");
                System.out.println("Done.");
            } else if (o.equals("3")) {
                List<Application> requests = dm.getWithdrawalRequests();
                if (requests.isEmpty()) { System.out.println("No withdrawal requests."); continue; }
                for (Application a : requests) System.out.printf("%s | Student:%s | Internship:%s | Status:%s\n", a.getId(), a.getStudentId(), a.getInternshipId(), a.getStatus());
                System.out.print("Enter application id to approve/reject withdrawal or blank: ");
                String aid = sc.nextLine().trim();
                if (aid.isBlank()) continue;
                System.out.print("Type 'approve' or 'reject': "); String dec = sc.nextLine().trim();
                if (dec.equalsIgnoreCase("approve")) {
                    dm.approveWithdrawal(aid);
                    System.out.println("Withdrawal approved; application set to Unsuccessful.");
                } else System.out.println("Withdrawal rejected (no change).");
            } else if (o.equals("4")) changePasswordFlow(st);
            else if (o.equals("5")) break;
            else System.out.println("Invalid.");
        }
    }

    private void changePasswordFlow(User u) {
        System.out.print("Enter new password: ");
        String np = sc.nextLine().trim();
        u.changePassword(np);
        System.out.println("Password changed (in-memory).");
    }
}
