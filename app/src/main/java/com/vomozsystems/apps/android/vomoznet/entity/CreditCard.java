package com.vomozsystems.apps.android.vomoznet.entity;

/**
 * Created by leksrej on 7/11/16.
 */
public class CreditCard {
    private String creditCardNumber;
    private String firstName;
    private String lastName;
    private String expiration;
    private boolean defaultCard;
    private String ccv;
    private Long id;
    private Long creditCardId;
    private String merchantIdCode;
    private String address;
    private String city;
    private String state;
    private String country;
    private String zipCode;
    private Boolean saveCard;
    private Boolean cardStatus;
    private Boolean cardType;
    private String last4Digits;
    private String accountNumber;
    private String routingNumber;
    private Integer donationCenterId;
    private String authToken;

    public CreditCard() {
        saveCard = false;
    }
    public String getCcv() {
        return ccv;
    }

    public void setCcv(String ccv) {
        this.ccv = ccv;
    }

    public boolean isDefaultCard() {
        return defaultCard;
    }

    public void setDefaultCard(boolean defaultCard) {
        this.defaultCard = defaultCard;
    }

    public String getExpiration() {
        return expiration;
    }

    public void setExpiration(String expiration) {
        this.expiration = expiration;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getCreditCardNumber() {
        return creditCardNumber;
    }

    public void setCreditCardNumber(String creditCardNumber) {
        this.creditCardNumber = creditCardNumber;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Boolean getCardStatus() {
        return cardStatus;
    }

    public void setCardStatus(Boolean cardStatus) {
        this.cardStatus = cardStatus;
    }

    public Boolean getCardType() {
        return cardType;
    }

    public void setCardType(Boolean cardType) {
        this.cardType = cardType;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public Long getCreditCardId() {
        return creditCardId;
    }

    public void setCreditCardId(Long creditCardId) {
        this.creditCardId = creditCardId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMerchantIdCode() {
        return merchantIdCode;
    }

    public void setMerchantIdCode(String merchantIdCode) {
        this.merchantIdCode = merchantIdCode;
    }

    public Boolean getSaveCard() {
        //if(saveCard==null) return false;
        return saveCard;
    }

    public void setSaveCard(Boolean saveCard) {
        this.saveCard = saveCard;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getRoutingNumber() {
        return routingNumber;
    }

    public void setRoutingNumber(String routingNumber) {
        this.routingNumber = routingNumber;
    }

    public String getLast4Digits() {
        if(last4Digits !=null && !last4Digits.equalsIgnoreCase(("0")))
            return last4Digits;
        else if (null != creditCardNumber && creditCardNumber.length()>4) {
            return creditCardNumber.substring(creditCardNumber.length() - 4);
        }
        else if (null != accountNumber && accountNumber.length() > 4) {
            return accountNumber.substring(accountNumber.length() - 4);
        }
        else
            return null;
    }

    public void setLast4Digits(String last4Digits) {
        this.last4Digits = last4Digits;
    }

    public Integer getDonationCenterId() {
        return donationCenterId;
    }

    public void setDonationCenterId(Integer donationCenterId) {
        this.donationCenterId = donationCenterId;
    }

    public String getAuthToken() {
        return authToken;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CreditCard that = (CreditCard) o;

        return creditCardNumber.equals(that.creditCardNumber);

    }

    @Override
    public int hashCode() {
        return creditCardNumber.hashCode();
    }
}
