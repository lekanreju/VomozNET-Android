package com.vomozsystems.apps.android.vomoznet.entity;

public class Contact {
    private Long contactId;
    private String contact1LastName;
    private String contact1FirstName;
    private String contact1Phone;
    private String contact1Email;
    private String contact2LastName;
    private String contact2FirstName;
    private String contact2Phone;
    private String contact2Email;
    private Long centerCardId;

    public Long getContactId() {
        return contactId;
    }

    public void setContactId(Long contactId) {
        this.contactId = contactId;
    }

    /**
     * @return the contact1LastName
     */
    public String getContact1LastName() {
        if (null != contact1LastName && !contact1LastName.equalsIgnoreCase("0"))
            return contact1LastName;
        else
            return "";
    }

    /**
     * @param contact1LastName the contact1LastName to set
     */
    public void setContact1LastName(String contact1LastName) {
        this.contact1LastName = contact1LastName;
    }

    /**
     * @return the contact1FirstName
     */
    public String getContact1FirstName() {
        if (null != contact1FirstName && !contact1FirstName.equalsIgnoreCase("0"))
            return contact1FirstName;
        else
            return "";
    }

    /**
     * @param contact1FirstName the contact1FirstName to set
     */
    public void setContact1FirstName(String contact1FirstName) {
        this.contact1FirstName = contact1FirstName;
    }

    /**
     * @return the contact2LastName
     */
    public String getContact2LastName() {
        if (null != contact2LastName && !contact2LastName.equalsIgnoreCase("0"))
            return contact2LastName;
        else
            return "";
    }

    /**
     * @param contact2LastName the contact2LastName to set
     */
    public void setContact2LastName(String contact2LastName) {
        this.contact2LastName = contact2LastName;
    }

    /**
     * @return the contact2FirstName
     */
    public String getContact2FirstName() {
        if (null != contact2FirstName && !contact2FirstName.equalsIgnoreCase("0"))
            return contact2FirstName;
        else
            return "";
    }

    /**
     * @param contact2FirstName the contact2FirstName to set
     */
    public void setContact2FirstName(String contact2FirstName) {
        this.contact2FirstName = contact2FirstName;
    }

    /**
     * @return the contact1Phone
     */
    public String getContact1Phone() {
        if (null != contact1Phone && !contact1Phone.equalsIgnoreCase("0"))
            return contact1Phone;
        else
            return "";
    }

    /**
     * @param contact1Phone the contact1Phone to set
     */
    public void setContact1Phone(String contact1Phone) {
        this.contact1Phone = contact1Phone;
    }

    /**
     * @return the contact1Email
     */
    public String getContact1Email() {
        if (null != contact1Email && !contact1Email.equalsIgnoreCase("0"))
            return contact1Email;
        else
            return "";
    }

    /**
     * @param contact1Email the contact1Email to set
     */
    public void setContact1Email(String contact1Email) {
        this.contact1Email = contact1Email;
    }

    /**
     * @return the contact2Phone
     */
    public String getContact2Phone() {
        if (null != contact2Phone && !contact2Phone.equalsIgnoreCase("0"))
            return contact2Phone;
        else
            return "";
    }

    /**
     * @param contact2Phone the contact2Phone to set
     */
    public void setContact2Phone(String contact2Phone) {
        this.contact2Phone = contact2Phone;
    }

    /**
     * @return the contact2Email
     */
    public String getContact2Email() {
        if (null != contact2Email && !contact2Email.equalsIgnoreCase("0"))
            return contact2Email;
        else
            return "";
    }

    /**
     * @param contact2Email the contact2Email to set
     */
    public void setContact2Email(String contact2Email) {
        this.contact2Email = contact2Email;
    }

    public Long getCenterCardId() {
        return centerCardId;
    }

    public void setCenterCardId(Long centerCardId) {
        this.centerCardId = centerCardId;
    }
}
