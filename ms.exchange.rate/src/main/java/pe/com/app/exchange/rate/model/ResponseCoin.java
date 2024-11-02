package pe.com.app.exchange.rate.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResponseCoin {
	private Double amount;
	private Double totalAmount;
	private String originalCurrency;
	private String targetCurrency;
	private Double exchangeRate;
}
