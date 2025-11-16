package service;

import repository.InternshipRepository;
import repository.ApplicationRepository;
import model.Internship;
// import model.Application;

import java.util.*;
import java.util.stream.Collectors;

public class ReportService {
    private InternshipRepository internshipRepo;
    private ApplicationRepository applicationRepo;

    public ReportService(InternshipRepository ir, ApplicationRepository ar){
        this.internshipRepo = ir;
        this.applicationRepo = ar;
    }

    public List<Internship> filter(String status, String major, String level){
        Map<String,String> f = new HashMap<>();
        if (status != null) f.put("status", status);
        if (major != null) f.put("major", major);
        if (level != null) f.put("level", level);
        InternshipService tmp = new InternshipService(internshipRepo, null); // quick reuse
        return tmp.filter(f);
    }

    public void printSummary(){
        System.out.println("=== Internship Summary ===");
        Map<String, List<Internship>> byCompany = internshipRepo.findAll().stream()
                .collect(Collectors.groupingBy(Internship::getCompanyName));
        for (String c : byCompany.keySet()){
            System.out.println("Company: " + c);
            for (Internship i: byCompany.get(c)){
                long apps = applicationRepo.findByInternship(i.getId()).size();
                System.out.printf("  %s (%s) - Status: %s - %d apps - Visible:%s\n",
                        i.getTitle(), i.getId(), i.getStatus(), apps, i.isVisible());
            }
        }
    }
}
