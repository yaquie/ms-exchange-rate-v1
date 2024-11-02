package pe.com.app.exchange.rate.client;

import java.util.Map;

import io.reactivex.Flowable;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;


@Component
public class ApiClient {

	public Flowable<Map<String, Double>> getApiClient() {
		String uri = "http://localhost:9797/api/exchange/rates";
		RestTemplate template = new RestTemplate();
		Map<String, Double>  response = template.getForObject(uri, Map.class);
		return Flowable.just(response);
	}	
	
}
