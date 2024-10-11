package pe.com.app.exchange.rate.model;

public class RequestCoin {
	private Double amount;
	private String originalCurrency;
	private String targetCurrency;
	private String cuppon;
	public Double getAmount() {
		return amount;
	}
	public void setAmount(Double amount) {
		this.amount = amount;
	}
	public String getOriginalCurrency() {
		return originalCurrency;
	}
	public void setOriginalCurrency(String originalCurrency) {
		this.originalCurrency = originalCurrency;
	}
	public String getTargetCurrency() {
		return targetCurrency;
	}
	public void setTargetCurrency(String targetCurrency) {
		this.targetCurrency = targetCurrency;
	}
	public String getCuppon() {
		return cuppon;
	}
	public void setCuppon(String cuppon) {
		this.cuppon = cuppon;
	}
	
	
}
