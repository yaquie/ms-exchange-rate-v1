package pe.com.app.exchange.rate.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RequestCoin {
	private Double amount;
	private String originalCurrency;
	private String targetCurrency;
	private String cuppon;
	
}
