package pe.com.app.exchange.rate.service.impl;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import io.reactivex.Observable;
import pe.com.app.exchange.rate.client.ApiClient;
import pe.com.app.exchange.rate.model.RequestCoin;
import pe.com.app.exchange.rate.model.ResponseCoin;
import pe.com.app.exchange.rate.service.ExchangeRateService;
import pe.com.app.exchange.rate.util.DomainExceptions;
import pe.com.app.exchange.rate.util.RateConstant;

@Service
public class ExchangeRateServiceImpl implements ExchangeRateService {
	private static org.slf4j.Logger logger = LoggerFactory.getLogger(ExchangeRateServiceImpl.class);

	private static final Map<String, Double> exchangeRates = new HashMap();
	
	@Autowired
	private ApiClient apiClient;

	@Cacheable(value = "cacheExchangeRates", key = "#request.originalCurrency + '-' + #request.targetCurrency")
	@Override
	public Observable<ResponseCoin> convertAmount(RequestCoin request) {
	    logger.info("Calculating exchange rate for: {} to {}", request.getOriginalCurrency(), request.getTargetCurrency());

		Double exchangeRate = getExchangeRate(request.getOriginalCurrency(), request.getTargetCurrency());
		if (exchangeRate == 0.0) {
			return Observable.error(new DomainExceptions(RateConstant.COD_EX0001, RateConstant.SMS_EX0001));
		}
		String totalAmount = String.valueOf(convertAmount(request.getAmount(), exchangeRate));
		
		ResponseCoin response = new ResponseCoin();
		response.setAmount(request.getAmount());
		response.setTotalAmount(Double.parseDouble(totalAmount));
		response.setOriginalCurrency(request.getOriginalCurrency());
		response.setTargetCurrency(request.getTargetCurrency());	
		response.setExchangeRate(exchangeRate);
		
		return Observable.just(response);
	}


	public double getExchangeRate(String originalCurrency, String targetCurrency) {
		logger.info("======= init -  exchangeRate =======");
		String key = originalCurrency + "-" + targetCurrency;
		return apiClient.getApiClient().getOrDefault(key, 0.0);
	}

	public String convertAmount(Double amount, Double rateExhange) {
		logger.info("======= init - convertAmount =======");
		DecimalFormat decimalFormat = new DecimalFormat("##.###");
		return decimalFormat.format(amount * rateExhange);
	}


}
