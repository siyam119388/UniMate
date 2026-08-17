package com.unimate.model;

public class Opportunity {

    private int opportunityId;
    private String title;
    private String type;        // INTERNSHIP, HACKATHON, SCHOLARSHIP, CONTEST
    private String company;
    private String description;
    private String stipend;
    private String deadline;
    private String applyLink;

    public int getOpportunityId() { return opportunityId; }
    public void setOpportunityId(int opportunityId) { this.opportunityId = opportunityId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getCompany() { return company; }
    public void setCompany(String company) { this.company = company; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getStipend() { return stipend; }
    public void setStipend(String stipend) { this.stipend = stipend; }

    public String getDeadline() { return deadline; }
    public void setDeadline(String deadline) { this.deadline = deadline; }

    public String getApplyLink() { return applyLink; }
    public void setApplyLink(String applyLink) { this.applyLink = applyLink; }
}
