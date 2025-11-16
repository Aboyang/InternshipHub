package model;

import model.enums.InternshipLevel;
import model.enums.InternshipStatus;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Internship {
    private static int NEXT_ID = 1000;
    private final String id;
    private String title;
    private String description;
    private InternshipLevel level;
    private String preferredMajor;
    private LocalDate openDate;
    private LocalDate closeDate;
    private InternshipStatus status = InternshipStatus.Pending;
    private String companyName;
    private String companyRepId;
    private int slots;
    private boolean visible = false;
    private List<String> applicationIds = new ArrayList<>();

    public Internship(String title, String description, InternshipLevel level, String preferredMajor,
                      LocalDate openDate, LocalDate closeDate, String companyName, String companyRepId, int slots) {
        this.id = "I" + NEXT_ID++;
        this.title = title;
        this.description = description;
        this.level = level;
        this.preferredMajor = preferredMajor;
        this.openDate = openDate;
        this.closeDate = closeDate;
        this.companyName = companyName;
        this.companyRepId = companyRepId;
        this.slots = Math.max(1, Math.min(10, slots));
    }

    public String getId(){ return id; }
    public String getTitle(){ return title; }
    public String getDescription(){ return description; }
    public InternshipLevel getLevel(){ return level; }
    public String getPreferredMajor(){ return preferredMajor; }
    public LocalDate getOpenDate(){ return openDate; }
    public LocalDate getCloseDate(){ return closeDate; }
    public InternshipStatus getStatus(){ return status; }
    public String getCompanyName(){ return companyName; }
    public String getCompanyRepId(){ return companyRepId; }
    public int getSlots(){ return slots; }
    public boolean isVisible(){ return visible; }
    public List<String> getApplicationIds(){ return applicationIds; }

    public void setStatus(InternshipStatus s){ status = s; }
    public void setVisible(boolean v){ visible = v; }
    public void setTitle(String t){ title = t; }
    public void setDescription(String d){ description = d; }
    public void setLevel(InternshipLevel l){ level = l; }
    public void setPreferredMajor(String m){ preferredMajor = m; }
    public void setOpenDate(LocalDate d){ openDate = d; }
    public void setCloseDate(LocalDate d){ closeDate = d; }
    public void setSlots(int s){ slots = Math.max(1, Math.min(10, s)); }

    public void addApplicationId(String aid){ applicationIds.add(aid); }
    public void removeApplicationId(String aid){ applicationIds.remove(aid); }

    public int confirmedCount(List<Application> allApps){
        int cnt = 0;
        for (String aid: applicationIds){
            for (Application a: allApps){
                if (a.getId().equals(aid) && a.getStatus() == model.enums.ApplicationStatus.Confirmed) cnt++;
            }
        }
        return cnt;
    }

    public boolean isOpenToday(){
        LocalDate now = LocalDate.now();
        return !now.isBefore(openDate) && !now.isAfter(closeDate);
    }
}
