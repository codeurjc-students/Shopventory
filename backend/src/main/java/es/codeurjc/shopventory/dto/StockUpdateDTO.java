package es.codeurjc.shopventory.dto;

import jakarta.validation.constraints.NotNull;

public class StockUpdateDTO {

    @NotNull
    private int quantity;

    private String reason;

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
}
