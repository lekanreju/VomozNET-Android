package com.vomozsystems.apps.android.vomoznet.entity;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class DonationCenterTopic extends RealmObject {

    @PrimaryKey
    private Long id;
    private String topicName;
    private String topicDescription;
    private Long donationCenterId;
    private Boolean selected = true;

    /**
     * @return the id
     */
    public Long getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * @return the topicName
     */
    public String getTopicName() {
        return topicName;
    }

    /**
     * @param topicName the topicName to set
     */
    public void setTopicName(String topicName) {
        this.topicName = topicName;
    }

    /**
     * @return the topicDescription
     */
    public String getTopicDescription() {
        return topicDescription;
    }

    /**
     * @param topicDescription the topicDescription to set
     */
    public void setTopicDescription(String topicDescription) {
        this.topicDescription = topicDescription;
    }

    /**
     * @return the donationCenterId
     */
    public Long getDonationCenterId() {
        return donationCenterId;
    }

    /**
     * @param donationCenterId the donationCenterId to set
     */
    public void setDonationCenterId(Long donationCenterId) {
        this.donationCenterId = donationCenterId;
    }

    public Boolean getSelected() {
        return selected;
    }

    public void setSelected(Boolean selected) {
        this.selected = selected;
    }
}
