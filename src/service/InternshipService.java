package service;

import repository.InternshipRepository;
import repository.UserRepository;
import model.Internship;
import model.CompanyRep;
import model.enums.InternshipLevel;
import model.enums.InternshipStatus;
import model.Application;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

public class InternshipService {
    private InternshipRepository internshipRepo;
    private UserRepository userRepo;

    public InternshipService(InternshipRepository ir, UserRepository ur){
        this.internshipRepo = ir;
        this.userRepo = ur;
    }

    public Optional<Internship> find(String id){ 
        return internshipRepo.findById(id); 
    }

    public boolean createInternship(String title, String desc, InternshipLevel level, String prefMajor,
                                    LocalDate open, LocalDate close, String companyName, String repId, int slots){
        Optional<CompanyRep> rep = userRepo.findCompanyRep(repId);
        if (!rep.isPresent() || !rep.get().isApproved()) return false;

        long count = internshipRepo.findAll().stream()
                .filter(i -> i.getCompanyRepId().equals(repId))
                .count();
        if (count >= 5) return false;

        Internship i = new Internship(title, desc, level, prefMajor.trim(), open, close, companyName, repId, slots);
        i.setStatus(InternshipStatus.Pending); // needs staff approval
        i.setVisible(false); // initially invisible until toggled
        internshipRepo.add(i);
        return true;
    }

    public boolean toggleVisibility(String internshipId, String repId, boolean visible){
        Optional<Internship> o = internshipRepo.findById(internshipId);
        if (!o.isPresent()) return false;
        Internship it = o.get();
        if (!it.getCompanyRepId().equals(repId)) return false;
        it.setVisible(visible);
        return true;
    }

    /**
     * List internships visible to a student based on major, year, visibility, status, and closing date.
     */
    public List<Internship> listVisibleForStudent(String studentMajor, int studentYear){
        LocalDate now = LocalDate.now();
        String majorNormalized = studentMajor.trim().toLowerCase();

        return internshipRepo.findAll().stream()
                .filter(i -> i.isVisible())
                .filter(i -> i.getStatus() == InternshipStatus.Approved) // show only approved internships
                .filter(i -> !now.isAfter(i.getCloseDate())) // still open
                .filter(i -> {
                    // Level eligibility
                    if (studentYear <= 2) return i.getLevel() == InternshipLevel.Basic;
                    return true;
                })
                .filter(i -> i.getPreferredMajor() != null && i.getPreferredMajor().trim().toLowerCase().equals(majorNormalized))
                .sorted(Comparator.comparing(Internship::getTitle))
                .collect(Collectors.toList());
    }

    public boolean staffApproveInternship(String internshipId, boolean approve){
        Optional<Internship> o = internshipRepo.findById(internshipId);
        if (!o.isPresent()) return false;
        Internship it = o.get();
        it.setStatus(approve ? InternshipStatus.Approved : InternshipStatus.Rejected);
        return true;
    }

    public boolean markFilledIfFull(Internship i, List<Application> allApps){
        int confirmed = i.confirmedCount(allApps);
        if (confirmed >= i.getSlots()){
            i.setStatus(InternshipStatus.Filled);
            i.setVisible(false);
            return true;
        }
        return false;
    }

    public List<Internship> findAll(){ 
        return new ArrayList<>(internshipRepo.findAll()); 
    }

    public List<Internship> filter(Map<String,String> filters){
        return internshipRepo.findAll().stream().filter(i -> {
            if (filters.containsKey("status")){
                if (!i.getStatus().toString().equalsIgnoreCase(filters.get("status"))) return false;
            }
            if (filters.containsKey("major")){
                if (i.getPreferredMajor() == null || 
                    !i.getPreferredMajor().trim().equalsIgnoreCase(filters.get("major").trim())) return false;
            }
            if (filters.containsKey("level")){
                if (!i.getLevel().toString().equalsIgnoreCase(filters.get("level"))) return false;
            }
            return true;
        }).sorted(Comparator.comparing(Internship::getTitle)).collect(Collectors.toList());
    }
}
