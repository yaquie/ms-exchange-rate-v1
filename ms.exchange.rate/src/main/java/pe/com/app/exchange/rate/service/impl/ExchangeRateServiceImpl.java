package pe.com.app.exchange.rate.service.impl;

import java.text.DecimalFormat;

import jakarta.persistence.EntityNotFoundException;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import pe.com.app.exchange.rate.client.ApiClient;
import pe.com.app.exchange.rate.model.RequestCoin;
import pe.com.app.exchange.rate.model.ResponseCoin;
import pe.com.app.exchange.rate.model.Transaccion;
import pe.com.app.exchange.rate.repository.model.TransaccionInterface;
import pe.com.app.exchange.rate.service.ExchangeRateService;
import pe.com.app.exchange.rate.util.DomainExceptions;
import pe.com.app.exchange.rate.util.PropertiesExterno;
import pe.com.app.exchange.rate.util.RateConstant;
import reactor.core.publisher.Mono;

@Service
public class ExchangeRateServiceImpl implements ExchangeRateService {
	private static org.slf4j.Logger logger = LoggerFactory.getLogger(ExchangeRateServiceImpl.class);
	
	//private static final Map<String, Double> exchangeRates = new HashMap();
	
	@Autowired
	private ApiClient apiClient;
	
	@Autowired
    private PropertiesExterno properties;

	@Autowired
	TransaccionInterface transaccionInterface;

	//@Autowired
	//private Mapper mapper;
	
	public static boolean containUsd = false;
	
	@Cacheable(value = "cacheExchangeRates", key = "#request.originalCurrency + '-' + #request.targetCurrency")
	@Override
	public Mono<ResponseCoin> convertAmount(RequestCoin request) {
	    logger.info("Calculating exchange rate for: {} to {}", request.getOriginalCurrency(), request.getTargetCurrency());

		Double exchangeRate = getExchangeRate(request.getOriginalCurrency(), request.getTargetCurrency());

		if (exchangeRate == 0.0) {
			return Mono.error(new DomainExceptions(RateConstant.COD_EX0001, RateConstant.SMS_EX0001));
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
		
		return Mono.just(response);
	}

	@Override
	public Mono<Transaccion> getTransaccion(Long id) {
		//return Mono.just(mapper.convert(transaccion.getTransaction(id)));
		logger.info("service: " + id);
		return Mono.justOrEmpty(transaccionInterface.findById(id))
				.switchIfEmpty(Mono.empty());
				//.switchIfEmpty(Mono.error(new DomainExceptions(RateConstant.COD_EX0003, RateConstant.SMS_EX0003)));
	}

	@Override
	public Mono<Transaccion> saveTransaccion(Transaccion transaccion) {
		logger.info("service: " + transaccion.getOriginalCurrency());
		return Mono.just(transaccionInterface.save(transaccion));
	}


	public double getExchangeRate(String originalCurrency, String targetCurrency) {
		String key = originalCurrency + "-" + targetCurrency;
		containUsd = key.contains("USD") ? true: false;
		logger.info("containUsd: " + containUsd);
		return apiClient.getApiClient()
				.map(rate -> rate.getOrDefault(key, 0.0))
				.toObservable()
				.blockingSingle();
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

	/*
	static {
		exchangeRates.put("PEN-ARS", 323.6439);
		exchangeRates.put("ARS-PEN", 0.00309);
		exchangeRates.put("BRL-PEN", 0.67);// reales brasileños
		exchangeRates.put("MXN-PEN", 0.19); // pesos mexicanos
		exchangeRates.put("PEN-USD", 0.27);
		exchangeRates.put("USD-PEN", 3.76);

	}*/

}
