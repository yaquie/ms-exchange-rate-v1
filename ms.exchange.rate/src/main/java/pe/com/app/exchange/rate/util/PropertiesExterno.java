package pe.com.app.exchange.rate.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class PropertiesExterno {
	
	@Value("${rate.cuppon.discount}")
	private String discount;
	
	@Value("${rate.cuppon}")
	private String cuppon;

	public String getDiscount() {
		return discount;
	}

	public String getCuppon() {
		return cuppon;
	}

	
	

}
