package com.vomozsystems.apps.android.vomoznet.entity;

/**
 * Created by leksrej on 1/20/17.
 */

public class BankAccount {
    private String routingNumber;
    private String accountNumber;
    private String last4Digits;
    private String expiration;
    private Long id;
    private Long creditCardId;
    private String merchantIdCode;
    private Boolean saveCard;

    public BankAccount() {
        saveCard = false;
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

    public Long getCreditCardId() {
        return creditCardId;
    }

    public void setCreditCardId(Long creditCardId) {
        this.creditCardId = creditCardId;
    }

    public String getExpiration() {
        return expiration;
    }

    public void setExpiration(String expiration) {
        this.expiration = expiration;
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
        return saveCard;
    }

    public void setSaveCard(Boolean saveCard) {
        this.saveCard = saveCard;
    }

    public String getLast4Digits() {
        return last4Digits;
    }

    public void setLast4Digits(String last4Digits) {
        this.last4Digits = last4Digits;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BankAccount that = (BankAccount) o;

        if (!routingNumber.equals(that.routingNumber)) return false;
        return accountNumber.equals(that.accountNumber);

    }

    @Override
    public int hashCode() {
        int result = routingNumber.hashCode();
        result = 31 * result + accountNumber.hashCode();
        return result;
    }


}
