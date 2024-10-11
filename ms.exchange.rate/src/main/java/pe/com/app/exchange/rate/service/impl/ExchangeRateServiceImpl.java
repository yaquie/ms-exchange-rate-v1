package pe.com.app.exchange.rate.service.impl;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import io.reactivex.Observable;
import pe.com.app.exchange.rate.client.ApiClient;
import pe.com.app.exchange.rate.model.RequestCoin;
import pe.com.app.exchange.rate.model.ResponseCoin;
import pe.com.app.exchange.rate.service.ExchangeRateService;
import pe.com.app.exchange.rate.util.DomainExceptions;
import pe.com.app.exchange.rate.util.PropertiesExterno;
import pe.com.app.exchange.rate.util.RateConstant;

@Service
public class ExchangeRateServiceImpl implements ExchangeRateService {
	private static org.slf4j.Logger logger = LoggerFactory.getLogger(ExchangeRateServiceImpl.class);
	
	@Autowired
	private ApiClient apiClient;
	
	@Autowired
    private PropertiesExterno properties;
	
	public static boolean containUsd = false;
	
	@Cacheable(value = "cacheExchangeRates", key = "#request.originalCurrency + '-' + #request.targetCurrency")
	@Override
	public Observable<ResponseCoin> convertAmount(RequestCoin request) {
	    logger.info("Calculating exchange rate for: {} to {}", request.getOriginalCurrency(), request.getTargetCurrency());

		Double exchangeRate = getExchangeRate(request.getOriginalCurrency(), request.getTargetCurrency());
		
		if (exchangeRate == 0.0) {
			return Observable.error(new DomainExceptions(RateConstant.COD_EX0001, RateConstant.SMS_EX0001));
		}
		String totalAmount = String.valueOf(
				convertAmount(request.getAmount(), exchangeRate, 
						request.getCuppon()));
		
		ResponseCoin response = new ResponseCoin();
		response.setAmount(request.getAmount());
		response.setTotalAmount(Double.parseDouble(totalAmount));
		response.setOriginalCurrency(request.getOriginalCurrency());
		response.setTargetCurrency(request.getTargetCurrency());	
		response.setExchangeRate(exchangeRate);
		
		return Observable.just(response);
	}


	public double getExchangeRate(String originalCurrency, String targetCurrency) {
		String key = originalCurrency + "-" + targetCurrency;
		containUsd = key.contains("USD") ? true: false;
		logger.info("containUsd: " + containUsd);
		return apiClient.getApiClient().getOrDefault(key, 0.0);
	}
	
	

	public String convertAmount(Double amount, Double rateExhange, String cupon) {
		
		DecimalFormat decimalFormat = new DecimalFormat("##.###");		
		if(cupon.equals(properties.getCuppon()) && containUsd) {
			logger.info("ingresando con cuppon!");
			return decimalFormat.format((amount * rateExhange) + 
					(amount * rateExhange*Double.parseDouble(properties.getDiscount())));
			
		}
	
		return decimalFormat.format(amount * rateExhange);
	}


}
