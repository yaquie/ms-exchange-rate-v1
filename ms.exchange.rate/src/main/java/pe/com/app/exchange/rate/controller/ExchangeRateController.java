package pe.com.app.exchange.rate.controller;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import pe.com.app.exchange.rate.model.RequestCoin;
import pe.com.app.exchange.rate.model.ResponseCoin;
import pe.com.app.exchange.rate.model.Transaccion;
import pe.com.app.exchange.rate.model.TransaccionResponse;
import pe.com.app.exchange.rate.service.ExchangeRateService;
import pe.com.app.exchange.rate.util.DomainExceptions;
import pe.com.app.exchange.rate.util.RateConstant;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/exchange")
public class ExchangeRateController {
	private static org.slf4j.Logger logger = LoggerFactory.getLogger(ExchangeRateController.class);

	@Autowired
	private ExchangeRateService exchangeRateService;

	@GetMapping("/currency")
	@CircuitBreaker(name = "currency", fallbackMethod = "fallbackGetApiClient")
	public Mono<ResponseEntity<ResponseCoin>> getCurrency(@RequestBody RequestCoin request) {
		logger.info("Iniciando el proceso  de tipo de cambio.");
		return exchangeRateService.convertAmount(request)
				.map(response -> {
			return ResponseEntity.ok() 
					.body(response);
		}).onErrorResume((Throwable throwable) -> handleException(throwable));
	}

	@GetMapping("/transaccion/{id}")
	public Mono<ResponseEntity<Transaccion>> getTransaccion(@PathVariable(name = "id") Long id) {
		logger.info("controller: " + id);
		return exchangeRateService.getTransaccion(id)
				.map(response -> ResponseEntity.ok().body(response))
				.defaultIfEmpty(ResponseEntity.noContent().build());
				//.onErrorResume((Throwable throwable) -> handleException2(throwable));
	}

	@PostMapping("/transaccion")
	public Mono<ResponseEntity<Transaccion>> saveTransaccion(@RequestBody Transaccion transaccion){
		logger.info("controller: " + transaccion.getOriginalCurrency());
		return exchangeRateService.saveTransaccion(transaccion)
				.map(response -> {
					return ResponseEntity.ok()
							.body(response);
				});
	}
	
	private Mono<ResponseEntity<ResponseCoin>> handleException(Throwable throwable){
		if (throwable instanceof DomainExceptions) {
			HttpHeaders headers = new HttpHeaders();
			DomainExceptions exception = (DomainExceptions) throwable;

			headers.add("code", exception.getCode());
			headers.add("mesage", exception.getMessage());
			return Mono
					.just(ResponseEntity.status(HttpStatus.CONFLICT)
							.headers(headers)
								.build());
		}
		logger.error("Handle exception: " + throwable.getMessage());
		return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
				.build());
	}

	private Mono<ResponseEntity<Transaccion>> handleException2(Throwable throwable){
		if (throwable instanceof DomainExceptions) {
			HttpHeaders headers = new HttpHeaders();
			DomainExceptions exception = (DomainExceptions) throwable;

			headers.add("code", exception.getCode());
			headers.add("mesage", exception.getMessage());
			return Mono
					.just(ResponseEntity.status(HttpStatus.CONFLICT)
							.headers(headers)
							.build());
		}
		logger.error("Handle exception: " + throwable.getMessage());
		return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
				.build());
	}
	
	public Mono<ResponseEntity<ResponseCoin>> fallbackGetApiClient(Throwable throwable) {
        logger.info("Reestableciendo nuestro servicio. " + throwable.getMessage());
        HttpHeaders headers = new HttpHeaders();
		headers.add("code", RateConstant.COD_EX0002);
		headers.add("mesage", RateConstant.SMS_EX0002);
        return Mono.just(ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
        		.headers(headers)
        		.build());
    }
	
	
}
