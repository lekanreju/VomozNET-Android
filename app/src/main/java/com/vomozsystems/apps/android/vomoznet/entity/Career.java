package com.vomozsystems.apps.android.vomoznet.entity;

public class Career {
    private Long careerId;
    private String occupation;
    private String jobTitle;
    private String employmentStatus;
    private Long centerCardId;

    public Long getCareerId() {
        return careerId;
    }

    public void setCareerId(Long careerId) {
        this.careerId = careerId;
    }

    /**
     * @return the occupation
     */
    public String getOccupation() {
        return occupation;
    }

    /**
     * @param occupation the occupation to set
     */
    public void setOccupation(String occupation) {
        this.occupation = occupation;
    }

    /**
     * @return the jobTitle
     */
    public String getJobTitle() {
        return jobTitle;
    }

    /**
     * @param jobTitle the jobTitle to set
     */
    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }

    /**
     * @return the employmentStatus
     */
    public String getEmploymentStatus() {
        return employmentStatus;
    }

    /**
     * @param employmentStatus the employmentStatus to set
     */
    public void setEmploymentStatus(String employmentStatus) {
        this.employmentStatus = employmentStatus;
    }

    public Long getCenterCardId() {
        return centerCardId;
    }

    public void setCenterCardId(Long centerCardId) {
        this.centerCardId = centerCardId;
    }
}
