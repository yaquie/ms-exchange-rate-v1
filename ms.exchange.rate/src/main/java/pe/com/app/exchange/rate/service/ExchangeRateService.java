package pe.com.app.exchange.rate.service;

import io.reactivex.Observable;
import pe.com.app.exchange.rate.model.RequestCoin;
import pe.com.app.exchange.rate.model.ResponseCoin;

public interface ExchangeRateService {
	public Observable<ResponseCoin> convertAmount(RequestCoin request);

}
