package careerhub.models;

public class FilterSettings {
    private String statusFilter;
    private String majorFilter;
    private String levelFilter;
    private String sortOrder;

    public FilterSettings(String status, String major, String level, String sort) {
        this.statusFilter = status;
        this.majorFilter = major;
        this.levelFilter = level;
        this.sortOrder = sort;
    }

    public String getStatusFilter() {
        return statusFilter;
    }

    public void setStatusFilter(String statusFilter) {
        this.statusFilter = statusFilter;
    }

    public String getMajorFilter() {
        return majorFilter;
    }

    public void setMajorFilter(String majorFilter) {
        this.majorFilter = majorFilter;
    }

    public String getLevelFilter() {
        return levelFilter;
    }

    public void setLevelFilter(String levelFilter) {
        this.levelFilter = levelFilter;
    }

    public String getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(String sortOrder) {
        this.sortOrder = sortOrder;
    }
}
