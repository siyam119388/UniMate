package com.unimate.model;

public class StudyGroup {

    private int groupId;
    private String name;
    private String courseCode;
    private String university;
    private String description;
    private int maxMembers;
    private int memberCount;
    private int createdBy;

    public int getGroupId() { return groupId; }
    public void setGroupId(int groupId) { this.groupId = groupId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getCourseCode() { return courseCode; }
    public void setCourseCode(String courseCode) { this.courseCode = courseCode; }

    public String getUniversity() { return university; }
    public void setUniversity(String university) { this.university = university; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public int getMaxMembers() { return maxMembers; }
    public void setMaxMembers(int maxMembers) { this.maxMembers = maxMembers; }

    public int getMemberCount() { return memberCount; }
    public void setMemberCount(int memberCount) { this.memberCount = memberCount; }

    public int getCreatedBy() { return createdBy; }
    public void setCreatedBy(int createdBy) { this.createdBy = createdBy; }

    public boolean isFull() { return memberCount >= maxMembers; }
}
