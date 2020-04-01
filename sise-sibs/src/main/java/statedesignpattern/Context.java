package statedesignpattern;

import pt.ulisboa.tecnico.learnjava.bank.exceptions.AccountException;
import pt.ulisboa.tecnico.learnjava.bank.services.Services;
import pt.ulisboa.tecnico.learnjava.sibs.exceptions.OperationException;

public class Context {

	private final String sourceIban;
	private final String targetIban;
	private final int value;
	private final int commission;

	private Services services;

	private TransferOperationState currentState;

	public Context(String sourceIban, String targetIban, int value, int commission) {

		this.sourceIban = sourceIban;
		this.targetIban = targetIban;
		this.value = value;
		this.commission = (int) (1 + this.value * 0.05);
		this.currentState = new Registered();
	}

	public void setState(TransferOperationState state) {
		this.currentState = state;
	}

	public void process(Services services) throws AccountException, OperationException {
		this.services = services;
		this.currentState.process(this);
	}

	public void deposit() throws AccountException {
		this.services.deposit(this.targetIban, this.value);
	}

	public void returnDeposit() throws AccountException {
		this.services.withdraw(this.targetIban, this.value);
	}

	public void withdraw() throws AccountException {
		this.services.withdraw(this.sourceIban, this.value);
	}

	public void returnWithdraw() throws AccountException {
		this.services.deposit(this.sourceIban, this.value);
	}

	public void withdrawCommission() throws AccountException {
		this.services.withdraw(this.sourceIban, this.commission);
	}

	public void returnWithdrawCommission() throws AccountException {
		this.services.deposit(this.sourceIban, this.commission);
	}

	public Boolean checkSameBank() throws AccountException {
		return this.services.checkSameBank(this.sourceIban, this.targetIban);
	}

	public void cancel() throws OperationException, AccountException {
		this.currentState.cancel(this);
	}

	public TransferOperationState getCurrentState() {
		return this.currentState;
	}

	public Services getServices() {
		return this.services;
	}
}