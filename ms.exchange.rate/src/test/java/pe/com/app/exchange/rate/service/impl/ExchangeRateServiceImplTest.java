package pe.com.app.exchange.rate.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import pe.com.app.exchange.rate.client.ApiClient;
import pe.com.app.exchange.rate.model.RequestCoin;
import pe.com.app.exchange.rate.util.DomainExceptions;

@ExtendWith(MockitoExtension.class)
public class ExchangeRateServiceImplTest {

	@Mock
	private ApiClient apiClient;

	@InjectMocks
	private ExchangeRateServiceImpl exchangeRateService;

	private Map<String, Double> exchangeRates;

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

		when(apiClient.getApiClient()).thenReturn(exchangeRates);

		exchangeRateService.convertAmount(request)
		.test()
		.assertComplete()
		.assertValue(response -> {
			assertEquals(100.0, response.getAmount());
			assertEquals(32364.39, response.getTotalAmount()); 
			assertEquals("PEN", response.getOriginalCurrency());
			assertEquals("ARS", response.getTargetCurrency());
			return true;
		});

	}
	
	
    @Test
    @DisplayName("return exception when no exchange rate available")
    void returnExceptionWhenNoExchaneRateAvainable() {
        RequestCoin request = new RequestCoin();
        request.setAmount(100.0);
        request.setOriginalCurrency("PEN");
        request.setTargetCurrency("USD");

        when(apiClient.getApiClient()).thenReturn(exchangeRates);

        exchangeRateService.convertAmount(request)
                .test()
                .assertError(DomainExceptions.class)
                .assertError(throwable -> ((DomainExceptions) throwable)
                		.getCode().equals("EX0001"));
    }
    
    @Test
    @DisplayName("return same request when works with cache")
    public void returnSameRequestWhenWorkWithCache() {
        RequestCoin request = new RequestCoin();
        request.setAmount(100.0);
        request.setOriginalCurrency("PEN");
        request.setTargetCurrency("ARS");

        when(apiClient.getApiClient()).thenReturn(exchangeRates);

        exchangeRateService.convertAmount(request)
                .test()
                .assertComplete();

        exchangeRates.put("PEN-ARS", 400.0);

        exchangeRateService.convertAmount(request)
                .test()
                .assertComplete()
                .assertValue(response -> {
                    assertEquals(40000.0, response.getTotalAmount()); 
                    return true;
                });
    }

}
