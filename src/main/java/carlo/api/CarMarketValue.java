package carlo.api;

import org.json.JSONObject;

import java.lang.reflect.Field;

public class CarMarketValue {
    private String vin;
    private boolean success;
    private Long retail;
    private Long tradeIn;
    private Long roughTradeIn;
    private Long averageTradeIn;
    private Long loanValue;
    private Long msrp;
    private Long tradeInValues;
    private Long auctionValues;

    public CarMarketValue(String vin, boolean success, Long retail, Long tradeIn, Long roughTradeIn, Long averageTradeIn, Long loanValue, Long msrp, Long tradeInValues, Long auctionValues) {
        this.vin = vin;
        this.success = success;
        this.retail = retail;
        this.tradeIn = tradeIn;
        this.roughTradeIn = roughTradeIn;
        this.averageTradeIn = averageTradeIn;
        this.loanValue = loanValue;
        this.msrp = msrp;
        this.tradeInValues = tradeInValues;
        this.auctionValues = auctionValues;
    }

    public CarMarketValue() {
    }

    public String getVin() {
        return vin;
    }

    public void setVin(String vin) {
        this.vin = vin;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public Long getRetail() {
        return retail;
    }

    public void setRetail(Long retail) {
        this.retail = retail;
    }

    public Long getTradeIn() {
        return tradeIn;
    }

    public void setTradeIn(Long tradeIn) {
        this.tradeIn = tradeIn;
    }

    public Long getRoughTradeIn() {
        return roughTradeIn;
    }

    public void setRoughTradeIn(Long roughTradeIn) {
        this.roughTradeIn = roughTradeIn;
    }

    public Long getAverageTradeIn() {
        return averageTradeIn;
    }

    public void setAverageTradeIn(Long averageTradeIn) {
        this.averageTradeIn = averageTradeIn;
    }

    public Long getLoanValue() {
        return loanValue;
    }

    public void setLoanValue(Long loanValue) {
        this.loanValue = loanValue;
    }

    public Long getMsrp() {
        return msrp;
    }

    public void setMsrp(Long msrp) {
        this.msrp = msrp;
    }

    public Long getTradeInValues() {
        return tradeInValues;
    }

    public void setTradeInValues(Long tradeInValues) {
        this.tradeInValues = tradeInValues;
    }

    public Long getAuctionValues() {
        return auctionValues;
    }

    public void setAuctionValues(Long auctionValues) {
        this.auctionValues = auctionValues;
    }

    public JSONObject toJson() {
        JSONObject jsonObject = new JSONObject();
        for (Field i : CarMarketValue.class.getDeclaredFields()){
            try {
                jsonObject.put(i.getName(),i.get(String.class));
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }

        }
        return jsonObject;
    }
}

