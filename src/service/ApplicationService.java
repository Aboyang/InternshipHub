package service;

import repository.ApplicationRepository;
import repository.InternshipRepository;
import repository.UserRepository;
import model.Application;
import model.Internship;
import model.Student;
import model.enums.ApplicationStatus;
import model.enums.InternshipLevel;

import java.util.*;
// import java.util.stream.Collectors;

public class ApplicationService {
    private ApplicationRepository applicationRepo;
    private InternshipRepository internshipRepo;
    private UserRepository userRepo;

    public ApplicationService(ApplicationRepository ar, InternshipRepository ir, UserRepository ur){
        this.applicationRepo = ar;
        this.internshipRepo = ir;
        this.userRepo = ur;
    }

    public List<Application> getStudentApplications(String studentId){
        return applicationRepo.findByStudent(studentId);
    }

    public Optional<Application> findById(String id) {
        return applicationRepo.findById(id);
    }

    public List<Application> findAll() {
        return new ArrayList<>(applicationRepo.findAll()); // convert Collection → List
    }


    public boolean apply(String studentId, String internshipId){
        Optional<Student> sOpt = userRepo.findStudent(studentId);
        if (!sOpt.isPresent()) return false;
        Student s = sOpt.get();
        Optional<Internship> iOpt = internshipRepo.findById(internshipId);
        if (!iOpt.isPresent()) return false;
        Internship i = iOpt.get();

        // visibility and status checks
        if (!i.isVisible()) return false;
        if (i.getStatus() != model.enums.InternshipStatus.Approved) return false;
        if (!i.getPreferredMajor().equalsIgnoreCase(s.getMajor())) return false;

        if (s.getYear() <= 2 && i.getLevel() != InternshipLevel.Basic) return false;

        // max 3 applications active (Pending or Successful)
        long activeCount = applicationRepo.findByStudent(studentId).stream()
                .filter(a -> a.getStatus() == ApplicationStatus.Pending || a.getStatus() == ApplicationStatus.Successful)
                .count();
        if (activeCount >= 3) return false;

        // cannot apply if deadline passed
        if (!i.isOpenToday()) return false;

        Application a = new Application(internshipId, studentId);
        applicationRepo.add(a);
        i.addApplicationId(a.getId());
        return true;
    }

    public boolean companyUpdateApplication(String companyRepId, String applicationId, boolean approve){
        Optional<Application> aOpt = applicationRepo.findById(applicationId);
        if (!aOpt.isPresent()) return false;
        Application a = aOpt.get();
        Optional<Internship> iOpt = internshipRepo.findById(a.getInternshipId());
        if (!iOpt.isPresent()) return false;
        Internship i = iOpt.get();
        if (!i.getCompanyRepId().equals(companyRepId)) return false;

        if (approve){
            a.setStatus(ApplicationStatus.Successful);
        } else {
            a.setStatus(ApplicationStatus.Unsuccessful);
        }
        // after approvals, staff/rep will need to set Confirmed when student accepts
        return true;
    }

    public boolean studentAcceptPlacement(String studentId, String applicationId){
        Optional<Application> aOpt = applicationRepo.findById(applicationId);
        if (!aOpt.isPresent()) return false;
        Application a = aOpt.get();
        if (!a.getStudentId().equals(studentId)) return false;
        if (a.getStatus() != ApplicationStatus.Successful) return false;

        // student accepts => Confirmed, and withdraw other applications
        a.setStatus(ApplicationStatus.Confirmed);
        // withdraw other apps
        for (Application other : applicationRepo.findByStudent(studentId)){
            if (!other.getId().equals(a.getId())){
                other.setStatus(ApplicationStatus.Withdrawn);
                // remove from internship lists
                internshipRepo.findById(other.getInternshipId()).ifPresent(i -> i.removeApplicationId(other.getId()));
            }
        }
        // check slots and mark internship filled if necessary
        internshipRepo.findById(a.getInternshipId()).ifPresent(i -> {
            int confirmed = i.confirmedCount(new ArrayList<>(applicationRepo.findAll()));
            if (confirmed >= i.getSlots()){
                i.setStatus(model.enums.InternshipStatus.Filled);
                i.setVisible(false);
            }
        });

        return true;
    }

    public boolean requestWithdrawal(String studentId, String applicationId){
        Optional<Application> aOpt = applicationRepo.findById(applicationId);
        if (!aOpt.isPresent()) return false;
        Application a = aOpt.get();
        if (!a.getStudentId().equals(studentId)) return false;
        // students can request withdrawal; actual approval by staff is separate
        a.setStatus(ApplicationStatus.Withdrawn);
        internshipRepo.findById(a.getInternshipId()).ifPresent(i -> i.removeApplicationId(a.getId()));
        return true;
    }

    public boolean staffApproveWithdrawal(String applicationId, boolean approve){
        Optional<Application> aOpt = applicationRepo.findById(applicationId);
        if (!aOpt.isPresent()) return false;
        Application a = aOpt.get();
        if (approve) {
            a.setStatus(ApplicationStatus.Withdrawn);
            internshipRepo.findById(a.getInternshipId()).ifPresent(i -> i.removeApplicationId(a.getId()));
        } else {
            // revert to pending (or keep previous status) - here we set to Pending for simplicity
            a.setStatus(ApplicationStatus.Pending);
        }
        return true;
    }
}
