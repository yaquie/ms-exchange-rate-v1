/*package pe.com.app.exchange.rate.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import pe.com.app.exchange.rate.client.ApiClient;
import pe.com.app.exchange.rate.model.RequestCoin;
import pe.com.app.exchange.rate.model.ResponseCoin;
import pe.com.app.exchange.rate.util.DomainExceptions;
import pe.com.app.exchange.rate.util.PropertiesExterno;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
public class ExchangeRateServiceImplTest {

	@Mock
	private ApiClient apiClient;

	@InjectMocks
	private ExchangeRateServiceImpl exchangeRateService;

	private Map<String, Double> exchangeRates;
	
	@Mock
	private PropertiesExterno properties;

	@BeforeEach
	void setup() {
		exchangeRates = new HashMap<>();
		exchangeRates.put("PEN-ARS", 323.6439);
		exchangeRates.put("ARS-PEN", 0.0031);

	}

	@Test
	@DisplayName("return amount when exchane from PEN to ARS")
	void testConvertAmount_Success() {
		RequestCoin request = new RequestCoin();
		request.setAmount(100.0);
		request.setOriginalCurrency("PEN");
		request.setTargetCurrency("ARS");
		request.setCuppon("ABC");

		when(apiClient.getApiClient()).thenReturn(exchangeRates);
		
		when(properties.getCuppon()).thenReturn("ABC");
		when(properties.getCuppon()).thenReturn("0.005");
		
		Mono<ResponseCoin> responseMono = exchangeRateService.convertAmount(request);
		
		 StepVerifier.create(responseMono)
		 .assertNext(response -> {
				assertEquals(100.0, response.getAmount());
				assertEquals(32364.39, response.getTotalAmount()); 
				assertEquals("PEN", response.getOriginalCurrency());
				assertEquals("ARS", response.getTargetCurrency());
			})
		 .verifyComplete();


	}
	
	
	@Test
	@DisplayName("return exception when no exchange rate available")
	void returnExceptionWhenNoExchaneRateAvainable() {
		RequestCoin request = new RequestCoin();
		request.setAmount(100.0);
		request.setOriginalCurrency("PEN");
		request.setTargetCurrency("USD");

		when(apiClient.getApiClient()).thenReturn(exchangeRates);

		Mono<ResponseCoin> responseMono = exchangeRateService.convertAmount(request);

		StepVerifier.create(responseMono)
				.consumeErrorWith(throwable ->
				((DomainExceptions) throwable).getCode().equals("EX0001"));

	}
    

	@Test
	@DisplayName("return same request when works with cache")
	public void returnSameRequestWhenWorkWithCache() {
		RequestCoin request = new RequestCoin();
		request.setAmount(100.0);
		request.setOriginalCurrency("PEN");
		request.setTargetCurrency("ARS");
		request.setCuppon("ABC");

		when(apiClient.getApiClient()).thenReturn(exchangeRates);

		when(properties.getCuppon()).thenReturn("ABC");
		when(properties.getCuppon()).thenReturn("0.005");

		Mono<ResponseCoin> responseMono = exchangeRateService.convertAmount(request);
		StepVerifier.create(responseMono).assertNext(response -> {
			assertEquals(100.0, response.getAmount());
			assertEquals(32364.39, response.getTotalAmount()); // Verificamos el valor total esperado
			assertEquals("PEN", response.getOriginalCurrency());
			assertEquals("ARS", response.getTargetCurrency());
		}).verifyComplete();

		exchangeRates.put("PEN-ARS", 400.0);
		Mono<ResponseCoin> responseMono2 = exchangeRateService.convertAmount(request);

		StepVerifier.create(responseMono2).assertNext(response -> {
			assertEquals(40000.0, response.getTotalAmount());
		}).verifyComplete();

	}

}*/
