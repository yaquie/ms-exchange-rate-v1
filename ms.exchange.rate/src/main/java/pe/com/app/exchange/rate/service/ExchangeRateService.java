package pe.com.app.exchange.rate.service;

import pe.com.app.exchange.rate.model.RequestCoin;
import pe.com.app.exchange.rate.model.ResponseCoin;
import pe.com.app.exchange.rate.model.Transaccion;
import reactor.core.publisher.Mono;

public interface ExchangeRateService {
	public Mono<ResponseCoin> convertAmount(RequestCoin request);
	Mono<Transaccion> getTransaccion(Long id);
	Mono<Transaccion> saveTransaccion(Transaccion transaccion);

}
