package pe.com.app.exchange.rate.controller;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import pe.com.app.exchange.rate.model.RequestCoin;
import pe.com.app.exchange.rate.model.ResponseCoin;
import pe.com.app.exchange.rate.service.ExchangeRateService;
import pe.com.app.exchange.rate.util.DomainExceptions;
import pe.com.app.exchange.rate.util.RateConstant;

@RestController
@RequestMapping("/exchange")
public class ExchangeRateController {
	private static org.slf4j.Logger logger = LoggerFactory.getLogger(ExchangeRateController.class);

	@Autowired
	private ExchangeRateService exchangeRateService;

	@GetMapping("/currency")
	@CircuitBreaker(name = "currency", fallbackMethod = "fallbackGetApiClient")
	public Flowable<ResponseEntity<ResponseCoin>> getCurrency(@RequestBody RequestCoin request) {
		logger.info("Iniciando el proceso tipo de cambio.");
		return exchangeRateService.convertAmount(request)
				.toFlowable(BackpressureStrategy.BUFFER)
				.map(response -> {
			return ResponseEntity.ok() 
					.body(response);
		}).onErrorResumeNext((Throwable throwable) -> handleException(throwable));
	}
	
	private Flowable<ResponseEntity<ResponseCoin>> handleException(Throwable throwable){
		if (throwable instanceof DomainExceptions) {
			HttpHeaders headers = new HttpHeaders();
			DomainExceptions exception = (DomainExceptions) throwable;

			headers.add("code", exception.getCode());
			headers.add("mesage", exception.getMessage());
			return Flowable
					.just(ResponseEntity.status(HttpStatus.CONFLICT)
							.headers(headers)
								.build());
		}
		logger.error("Handle exception: " + throwable.getMessage());
		return Flowable.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
				.build());
	}
	
	public Flowable<ResponseEntity<ResponseCoin>> fallbackGetApiClient(Throwable throwable) {
        logger.info("Reestableciendo nuestro servicio. " + throwable.getMessage());
        HttpHeaders headers = new HttpHeaders();
		headers.add("code", RateConstant.COD_EX0002);
		headers.add("mesage", RateConstant.SMS_EX0002);
        return Flowable.just(ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
        		.headers(headers)
        		.build());
    }
	
	
}
