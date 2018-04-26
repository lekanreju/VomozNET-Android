package com.vomozsystems.apps.android.vomoznet.entity;

import java.io.Serializable;

public class Item implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 7379288724355917696L;
    private Long id;
    private Long donationCenterCardId;
    private String title;
    private String description;
    private String url;
    private String type;
    private String viewUrl;

    public String getViewUrl() {
        return viewUrl;
    }

    public void setViewUrl(String viewUrl) {
        this.viewUrl = viewUrl;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Long getDonationCenterCardId() {
        return donationCenterCardId;
    }

    public void setDonationCenterCardId(Long donationCenterCardId) {
        this.donationCenterCardId = donationCenterCardId;
    }
}
