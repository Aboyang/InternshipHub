package ui;

import service.*;
import model.*;
import model.enums.*;
import repository.*;
import java.util.*;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

public class CLI {
    private AuthService auth;
    private UserService userService;
    private InternshipService internshipService;
    private ApplicationService applicationService;
    private ReportService reportService;
    private Scanner sc = new Scanner(System.in);

    public CLI(AuthService a, UserService us, InternshipService is, ApplicationService aps, ReportService rs){
        this.auth = a;
        this.userService = us;
        this.internshipService = is;
        this.applicationService = aps;
        this.reportService = rs;
    }

    public void start(){
        System.out.println("Welcome to Internship Placement Management System (CLI)");
        while (true){
            if (!auth.currentUser().isPresent()) showUnauthMenu();
            else showAuthMenu();
        }
    }

    private void showUnauthMenu(){
        System.out.println("\n1) Login\n2) Register Company Rep\n3) Exit");
        System.out.print("> ");
        String o = sc.nextLine().trim();
        switch(o){
            case "1": doLogin(); break;
            case "2": doRegisterRep(); break;
            case "3": System.exit(0); break;
            default: System.out.println("Unknown option");
        }
    }

    private void doLogin(){
        System.out.print("ID: "); String id = sc.nextLine().trim();
        System.out.print("Password: "); String pw = sc.nextLine().trim();
        Optional<User> u = auth.login(id, pw);
        if (!u.isPresent()){
            System.out.println("Login failed. Check ID/password or account approval.");
            return;
        }
        System.out.println("Welcome " + u.get().getName() + " (" + u.get().getRole() + ")");
    }

    private void doRegisterRep(){
        System.out.println("Register as Company Representative");
        System.out.print("Email (will be your ID): "); String id = sc.nextLine().trim();
        System.out.print("Name: "); String name = sc.nextLine().trim();
        System.out.print("Company Name: "); String comp = sc.nextLine().trim();
        System.out.print("Department: "); String dept = sc.nextLine().trim();
        System.out.print("Position: "); String pos = sc.nextLine().trim();
        System.out.print("Contact Email: "); String email = sc.nextLine().trim();
        boolean ok = userService.registerCompanyRep(id, name, comp, dept, pos, email);
        if (ok) System.out.println("Registered. Await Career Center Staff approval before login.");
        else System.out.println("Registration failed (ID might already exist).");
    }

    private void showAuthMenu(){
        User me = auth.currentUser().get();
        System.out.println("\n--- Menu (" + me.getRole() + ") ---");
        System.out.println("1) Logout  2) Change password  3) View internships / filters");
        switch(me.getRole()){
            case "Student": showStudentMenu((Student)me); break;
            case "CompanyRep": showCompanyMenu((CompanyRep)me); break;
            case "Staff": showStaffMenu((Staff)me); break;
            default: System.out.println("Unknown role"); auth.logout();
        }
    }

    // ================== STUDENT ==================

    private void showStudentMenu(Student s){
        System.out.println("4) List visible internships");
        System.out.println("5) Apply to internship");
        System.out.println("6) View my applications");
        System.out.println("7) Accept a successful placement");
        System.out.println("8) Request withdrawal");
        System.out.print("> ");
        String o = sc.nextLine().trim();
        switch (o){
            case "1": auth.logout(); break;
            case "2": changePasswordCLI(s.getId()); break;
            case "3": showFilterMenu(s); break;
            case "4": listVisibleForStudent(s); break;
            case "5": studentApply(s); break;
            case "6": viewStudentApplications(s); break;
            case "7": studentAccept(s); break;
            case "8": studentRequestWithdrawal(s); break;
            default: System.out.println("Unknown option");
        }
    }

    private void showFilterMenu(Student s){
        System.out.println("Saved filters: " + s.getSavedFilters());
        System.out.print("Set filters (status=...,major=...,level=...) or leave blank: ");
        String f = sc.nextLine().trim();
        s.setSavedFilters(f);
        System.out.println("Filters saved.");
    }

    private void listVisibleForStudent(Student s){
        List<Internship> list;

        if(s.getSavedFilters() == null || s.getSavedFilters().isBlank()){
            list = internshipService.listVisibleForStudent(s.getMajor(), s.getYear());
        } else {
            Map<String,String> filters = parseFilters(s.getSavedFilters());
            list = internshipService.filter(filters);
        }

        if(list.isEmpty()){
            System.out.println("No internships matching your profile.");
            return;
        }

        for(Internship i: list){
            System.out.printf("[%s] %s | %s | Level:%s | Major:%s | Status:%s | Visible:%s | Slots:%d\n",
                    i.getId(), i.getTitle(), i.getCompanyName(), i.getLevel(), i.getPreferredMajor(), i.getStatus(), i.isVisible(), i.getSlots());
        }
    }

    private Map<String,String> parseFilters(String filterStr){
        Map<String,String> map = new HashMap<>();
        String[] parts = filterStr.split(",");
        for(String p : parts){
            String[] kv = p.split("=");
            if(kv.length == 2) map.put(kv[0].trim().toLowerCase(), kv[1].trim());
        }
        return map;
    }

    private void studentApply(Student s){
        System.out.print("Internship ID to apply: "); String iid = sc.nextLine().trim();
        boolean ok = applicationService.apply(s.getId(), iid);
        System.out.println(ok ? "Application submitted." : "Application failed (eligibility/visibility/deadline/limits).");
    }

    private void viewStudentApplications(Student s){
        List<model.Application> apps = applicationService.getStudentApplications(s.getId());
        if (apps.isEmpty()){ System.out.println("No applications."); return; }
        for (model.Application a : apps){
            System.out.printf("[%s] Internship:%s | Status:%s | Applied:%s\n", a.getId(), a.getInternshipId(), a.getStatus(), a.getAppliedAt());
        }
    }

    private void studentAccept(Student s){
        System.out.print("Application ID to accept: "); String aid = sc.nextLine().trim();
        boolean ok = applicationService.studentAcceptPlacement(s.getId(), aid);
        System.out.println(ok ? "Placement accepted. Other applications withdrawn." : "Accept failed (not successful / wrong id).");
    }

    private void studentRequestWithdrawal(Student s){
        System.out.print("Application ID to withdraw: "); String aid = sc.nextLine().trim();
        boolean ok = applicationService.requestWithdrawal(s.getId(), aid);
        System.out.println(ok ? "Withdrawal requested." : "Withdrawal failed.");
    }

    // ================== COMPANY ==================

    private void showCompanyMenu(CompanyRep rep){
        System.out.println("4) Create internship");
        System.out.println("5) View my internships");
        System.out.println("6) Toggle visibility");
        System.out.println("7) View applications for one internship");
        System.out.println("8) Approve/Reject application");
        System.out.print("> ");
        String o = sc.nextLine().trim();
        switch(o){
            case "1": auth.logout(); break;
            case "2": changePasswordCLI(rep.getId()); break;
            case "3": break;
            case "4": companyCreateInternship(rep); break;
            case "5": companyListInternships(rep); break;
            case "6": companyToggleVisibility(rep); break;
            case "7": companyViewApplications(rep); break;
            case "8": companyDecideApplication(rep); break;
            default: System.out.println("Unknown option");
        }
    }

    private void companyCreateInternship(CompanyRep rep){
        if (!rep.isApproved()){ System.out.println("Your account is not approved by Career Center Staff yet."); return; }
        try {
            System.out.print("Title: "); String title = sc.nextLine().trim();
            System.out.print("Description: "); String desc = sc.nextLine().trim();
            System.out.print("Level (Basic/Intermediate/Advanced): "); String lvl = sc.nextLine().trim();
            System.out.print("Preferred Major: "); String major = sc.nextLine().trim();
            System.out.print("Open date (YYYY-MM-DD): "); String open = sc.nextLine().trim();
            System.out.print("Close date (YYYY-MM-DD): "); String close = sc.nextLine().trim();
            System.out.print("Number of slots (1-10): "); int slots = Integer.parseInt(sc.nextLine().trim());

            boolean ok = internshipService.createInternship(title, desc, InternshipLevel.fromString(lvl), major,
                    LocalDate.parse(open), LocalDate.parse(close), rep.getCompanyName(), rep.getId(), slots);
            System.out.println(ok ? "Internship created (Pending approval by Staff)." : "Creation failed (limits/approval).");
        } catch (DateTimeParseException | IllegalArgumentException e){
            System.out.println("Invalid input: " + e.getMessage());
        }
    }

    private void companyListInternships(CompanyRep rep){
        List<Internship> list = internshipService.findAll();
        for (Internship i: list){
            if (i.getCompanyRepId().equals(rep.getId())){
                System.out.printf("[%s] %s | Status:%s | Visible:%s | Slots:%d\n", i.getId(), i.getTitle(), i.getStatus(), i.isVisible(), i.getSlots());
            }
        }
    }

    private void companyToggleVisibility(CompanyRep rep){
        System.out.print("Internship ID: "); String iid = sc.nextLine().trim();
        System.out.print("Visible? (true/false): "); boolean v = Boolean.parseBoolean(sc.nextLine().trim());
        boolean ok = internshipService.toggleVisibility(iid, rep.getId(), v);
        System.out.println(ok ? "Visibility updated." : "Failed (not owner or invalid id).");
    }

    private void companyViewApplications(CompanyRep rep){
        System.out.print("Internship ID: "); String iid = sc.nextLine().trim();
        internshipService.find(iid).ifPresent(i -> {
            System.out.println("Applications:");
            for (String aid : i.getApplicationIds()){
                applicationService.findById(aid).ifPresent(app -> {
                    System.out.printf("[%s] Student:%s | Status:%s\n",
                            app.getId(), app.getStudentId(), app.getStatus());
                });
            }
        });
    }

    private void companyDecideApplication(CompanyRep rep){
        System.out.print("Application ID: "); String aid = sc.nextLine().trim();
        System.out.print("Approve? (true/false): "); boolean approve = Boolean.parseBoolean(sc.nextLine().trim());
        boolean ok = applicationService.companyUpdateApplication(rep.getId(), aid, approve);
        System.out.println(ok ? "Decision recorded." : "Failed (not allowed or invalid id).");
    }

    // ================== STAFF ==================

    private void showStaffMenu(Staff st){
        System.out.println("4) Approve Company Representatives");
        System.out.println("5) Approve/Reject internships");
        System.out.println("6) Approve/Reject withdrawal requests");
        System.out.println("7) Generate report");
        System.out.print("> ");
        String o = sc.nextLine().trim();
        switch(o){
            case "1": auth.logout(); break;
            case "2": changePasswordCLI(st.getId()); break;
            case "3": break;
            case "4": staffApproveReps(st); break;
            case "5": staffApproveInternships(st); break;
            case "6": staffProcessWithdrawals(st); break;
            case "7": reportService.printSummary(); break;
            default: System.out.println("Unknown option");
        }
    }

    private void staffApproveReps(Staff st){
        List<CompanyRep> pending = userService.pendingCompanyReps();
        if (pending.isEmpty()){ System.out.println("No pending reps."); return; }
        for (CompanyRep r: pending){
            System.out.printf("[%s] %s | Company:%s | Dept:%s | Position:%s\n", r.getId(), r.getName(), r.getCompanyName(), r.getDepartment(), r.getPosition());
            System.out.print("Approve? (y/n): "); String ans = sc.nextLine().trim();
            boolean ok = userService.approveCompanyRep(r.getId(), ans.equalsIgnoreCase("y"));
            System.out.println(ok ? "Decision saved" : "Failed");
        }
    }

    private void staffApproveInternships(Staff st){
        List<Internship> pending = internshipService.findAll();
        for (Internship i: pending){
            if (i.getStatus() == InternshipStatus.Pending){
                System.out.printf("[%s] %s by %s | Level:%s | Major:%s | Open:%s Close:%s\n",
                        i.getId(), i.getTitle(), i.getCompanyName(), i.getLevel(), i.getPreferredMajor(), i.getOpenDate(), i.getCloseDate());
                System.out.print("Approve? (y/n): "); String ans = sc.nextLine().trim();
                boolean ok = internshipService.staffApproveInternship(i.getId(), ans.equalsIgnoreCase("y"));
                System.out.println(ok ? "Decision saved" : "Failed");
            }
        }
    }

    private void staffProcessWithdrawals(Staff st){
        boolean any = false;
        for (model.Application a: applicationService.findAll()){
            if (a.getStatus() == model.enums.ApplicationStatus.Withdrawn){
                any = true;
                System.out.printf("[%s] Student:%s Internship:%s RequestedWithdraw\n", a.getId(), a.getStudentId(), a.getInternshipId());
                System.out.print("Approve withdrawal? (y/n): "); String ans = sc.nextLine().trim();
                boolean ok = applicationService.staffApproveWithdrawal(a.getId(), ans.equalsIgnoreCase("y"));
                System.out.println(ok ? "Processed" : "Failed");
            }
        }
        if (!any) System.out.println("No withdrawal requests.");
    }

    // ================== UTILITY ==================

    private void changePasswordCLI(String id){
        System.out.print("Old password: "); String oldpw = sc.nextLine().trim();
        System.out.print("New password: "); String newpw = sc.nextLine().trim();
        boolean ok = auth.changePassword(id, oldpw, newpw);
        System.out.println(ok ? "Password changed. Please re-login." : "Change failed (old password wrong).");
        if (ok) auth.logout();
    }
}
