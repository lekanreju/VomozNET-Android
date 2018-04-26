package com.vomozsystems.apps.android.vomoznet.entity;

public class Address {
    private Long addressId;
    private String addressLine1;
    private String addressLine2;
    private String cityOrTown;
    private String stateOrProvince;
    private String zipOrPostCode;
    private String country;
    private Long centerCardId;

    public Long getAddressId() {
        return addressId;
    }

    public void setAddressId(Long addressId) {
        this.addressId = addressId;
    }

    /**
     * @return the addressLine1
     */
    public String getAddressLine1() {
        if (null != addressLine1 && !addressLine1.equalsIgnoreCase("0"))
            return addressLine1;
        else
            return "";
    }

    /**
     * @param addressLine1 the addressLine1 to set
     */
    public void setAddressLine1(String addressLine1) {
        this.addressLine1 = addressLine1;
    }

    public String getAddressLine2() {
        if (null != addressLine2 && !addressLine2.equalsIgnoreCase("0"))
            return addressLine2;
        else
            return "";
    }

    public void setAddressLine2(String addressLine2) {
        this.addressLine2 = addressLine2;
    }

    /**
     * @return the cityOrTown
     */
    public String getCityOrTown() {
        if (null != cityOrTown && !cityOrTown.equalsIgnoreCase("0"))
            return cityOrTown;
        else
            return "";
    }

    /**
     * @param cityOrTown the cityOrTown to set
     */
    public void setCityOrTown(String cityOrTown) {
        this.cityOrTown = cityOrTown;
    }

    /**
     * @return the stateOrProvince
     */
    public String getStateOrProvince() {
        if (null != stateOrProvince && !stateOrProvince.equalsIgnoreCase("0"))
            return stateOrProvince;
        else
            return "";
    }

    /**
     * @param stateOrProvince the stateOrProvince to set
     */
    public void setStateOrProvince(String stateOrProvince) {
        this.stateOrProvince = stateOrProvince;
    }

    /**
     * @return the zipOrPostCode
     */
    public String getZipOrPostCode() {
        if (null != zipOrPostCode && !zipOrPostCode.equalsIgnoreCase("0"))
            return zipOrPostCode;
        else
            return "";
    }

    /**
     * @param zipOrPostCode the zipOrPostCode to set
     */
    public void setZipOrPostCode(String zipOrPostCode) {
        this.zipOrPostCode = zipOrPostCode;
    }

    /**
     * @return the country
     */
    public String getCountry() {
        if (null != country && !country.equalsIgnoreCase("0"))
            return country;
        else
            return "";
    }

    /**
     * @param country the country to set
     */
    public void setCountry(String country) {
        this.country = country;
    }

    public Long getCenterCardId() {
        return centerCardId;
    }

    public void setCenterCardId(Long centerCardId) {
        this.centerCardId = centerCardId;
    }
}