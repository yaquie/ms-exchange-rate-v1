package pe.com.app.exchange.rate.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class TransaccionResponse {
    private Long id;
    private Double amount;
    private Double totalAmount;
    private String originalCurrency;
    private String targetCurrency;
    private Double exchangeRate;
}
