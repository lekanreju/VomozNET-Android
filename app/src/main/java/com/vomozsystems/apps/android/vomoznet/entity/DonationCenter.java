package com.vomozsystems.apps.android.vomoznet.entity;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by leksrej on 7/6/16.
 */
public class DonationCenter extends RealmObject {
    @PrimaryKey
    private Integer id;
    private Long cardId;
    private String tokenId;
    private String domainName;
    private String name;
    private String bannerImage;
    private String shortName;
    private String yearJoined;
    private String logoName;
    private String dedicatedUrl;
    private String address;
    private String slogan;
    private String telephoneNumber;
    private String emailAddress;
    private String faceBookUrl;
    private String twitterUrl;
    private String youtubeUrl;
    private String faxAddress;
    private String webUrl;
    private String merchantIdCode;
    private RealmList<DonationType> donationTypes;
    private boolean homeDonationCenter;
    private Long texterCardId;
    private String organizationType;
    private Float latitude;
    private Float longitude;
    private String paypalClientId;
    private String paypalClientSecret;
    private String paypalLiveMode;

    public Long getCardId() {
        return cardId;
    }

    public void setCardId(Long cardId) {
        this.cardId = cardId;
    }

    public String getDomainName() {
        return domainName;
    }

    public void setDomainName(String domainName) {
        this.domainName = domainName;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getLogoName() {
        return logoName;
    }

    public void setLogoName(String logoName) {
        this.logoName = logoName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public String getTokenId() {
        return tokenId;
    }

    public void setTokenId(String tokenId) {
        this.tokenId = tokenId;
    }

    public String getYearJoined() {
        return yearJoined;
    }

    public void setYearJoined(String yearJoined) {
        this.yearJoined = yearJoined;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getDedicatedUrl() {
        return dedicatedUrl;
    }

    public void setDedicatedUrl(String dedicatedUrl) {
        this.dedicatedUrl = dedicatedUrl;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public String getFaceBookUrl() {
        if (null != faceBookUrl && faceBookUrl.equalsIgnoreCase("0"))
            return "Not Available";
        else
            return faceBookUrl;
    }

    public void setFaceBookUrl(String faceBookUrl) {
        this.faceBookUrl = faceBookUrl;
    }

    public String getFaxAddress() {
        if (null != faxAddress && faxAddress.equalsIgnoreCase("0"))
            return "Not Available";
        else
            return faxAddress;
    }

    public void setFaxAddress(String faxAddress) {
        this.faxAddress = faxAddress;
    }

    public String getSlogan() {
        return slogan;
    }

    public void setSlogan(String slogan) {
        this.slogan = slogan;
    }

    public String getTelephoneNumber() {
        return telephoneNumber;
    }

    public void setTelephoneNumber(String telephoneNumber) {
        this.telephoneNumber = telephoneNumber;
    }

    public String getTwitterUrl() {
        if (null != twitterUrl && twitterUrl.equalsIgnoreCase("0"))
            return "Not Available";
        else
            return twitterUrl;
    }

    public void setTwitterUrl(String twitterUrl) {
        this.twitterUrl = twitterUrl;
    }

    public String getWebUrl() {
        if (null != webUrl && webUrl.equalsIgnoreCase("0"))
            return "Not Available";
        else
            return webUrl;
    }

    public void setWebUrl(String webUrl) {
        this.webUrl = webUrl;
    }

    public String getYoutubeUrl() {
        if (null != youtubeUrl && youtubeUrl.equalsIgnoreCase("0"))
            return "Not Available";
        else
            return youtubeUrl;
    }

    public void setYoutubeUrl(String youtubeUrl) {
        this.youtubeUrl = youtubeUrl;
    }

    public RealmList<DonationType> getDonationTypes() {
        return donationTypes;
    }

    public void setDonationTypes(RealmList<DonationType> donationTypes) {
        this.donationTypes = donationTypes;
    }

    public String getMerchantIdCode() {
        return merchantIdCode;
    }

    public void setMerchantIdCode(String merchantIdCode) {
        this.merchantIdCode = merchantIdCode;
    }

    public boolean getHomeDonationCenter() {
        return homeDonationCenter;
    }

    public void setHomeDonationCenter(boolean homeDonationCenter) {
        this.homeDonationCenter = homeDonationCenter;
    }

    public Long getTexterCardId() {
        return texterCardId;
    }

    public void setTexterCardId(Long texterCardId) {
        this.texterCardId = texterCardId;
    }

    public String getOrganizationType() {
        return organizationType;
    }

    public void setOrganizationType(String organizationType) {
        this.organizationType = organizationType;
    }

    public String getBannerImage() {
        return bannerImage;
    }

    public void setBannerImage(String bannerImage) {
        this.bannerImage = bannerImage;
    }

    public Float getLatitude() {
        return latitude;
    }

    public void setLatitude(Float latitude) {
        this.latitude = latitude;
    }

    public Float getLongitude() {
        return longitude;
    }

    public void setLongitude(Float longitude) {
        this.longitude = longitude;
    }

    public String getPaypalClientId() {
        return paypalClientId;
    }

    public void setPaypalClientId(String paypalClientId) {
        this.paypalClientId = paypalClientId;
    }

    public String getPaypalClientSecret() {
        return paypalClientSecret;
    }

    public void setPaypalClientSecret(String paypalClientSecret) {
        this.paypalClientSecret = paypalClientSecret;
    }

    public String getPaypalLiveMode() {
        return paypalLiveMode;
    }

    public void setPaypalLiveMode(String paypalLiveMode) {
        this.paypalLiveMode = paypalLiveMode;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DonationCenter that = (DonationCenter) o;

        if (!cardId.equals(that.cardId)) return false;
        if (!name.equals(that.name)) return false;
        return shortName != null ? shortName.equals(that.shortName) : that.shortName == null;

    }

    @Override
    public int hashCode() {
        int result = cardId.hashCode();
        result = 31 * result + name.hashCode();
        result = 31 * result + (shortName != null ? shortName.hashCode() : 0);
        return result;
    }
}
