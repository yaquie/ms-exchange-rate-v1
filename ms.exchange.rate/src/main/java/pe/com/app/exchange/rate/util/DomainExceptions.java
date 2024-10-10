package pe.com.app.exchange.rate.util;

public class DomainExceptions extends Throwable{
	private String code;
	private String message;
	
	public DomainExceptions() {
		super();
	}
	
	public DomainExceptions(String code, String message) {
		super();
		this.code = code;
		this.message = message;
	}

	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	

}
