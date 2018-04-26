package com.vomozsystems.apps.android.vomoznet.entity;

/**
 * Created by leksrej on 3/7/18.
 */

public class AmountContributed {
    private Long texterCardId;
    private Double amountInNaira;
    private Double amountInDollars;
    private Double amountInPounds;
    private Double amountInEuro;
    private Double amountInCedi;

    public Long getTexterCardId() {
        return texterCardId;
    }
    public void setTexterCardId(Long texterCardId) {
        this.texterCardId = texterCardId;
    }
    public Double getAmountInNaira() {
        return amountInNaira;
    }
    public void setAmountInNaira(Double amountInNaira) {
        this.amountInNaira = amountInNaira;
    }
    public Double getAmountInDollars() {
        return amountInDollars;
    }
    public void setAmountInDollars(Double amountInDollars) {
        this.amountInDollars = amountInDollars;
    }
    public Double getAmountInPounds() {
        return amountInPounds;
    }
    public void setAmountInPounds(Double amountInPounds) {
        this.amountInPounds = amountInPounds;
    }
    public Double getAmountInEuro() {
        return amountInEuro;
    }
    public void setAmountInEuro(Double amountInEuro) {
        this.amountInEuro = amountInEuro;
    }
    public Double getAmountInCedi() {
        return amountInCedi;
    }
    public void setAmountInCedi(Double amountInCedi) {
        this.amountInCedi = amountInCedi;
    }
}

