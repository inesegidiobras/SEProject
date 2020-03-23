package mvcdesignpattern;

public class ClientMbway {

	private final String iban;
	private final Integer code;

	public ClientMbway(String iban, Integer code) {

		this.iban = iban;
		this.code = code;
	}

	public String getIban() {
		return this.iban;
	}

	public Integer getCode() {
		return this.code;
	}

}
