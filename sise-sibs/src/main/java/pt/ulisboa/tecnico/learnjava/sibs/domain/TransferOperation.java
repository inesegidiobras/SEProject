package pt.ulisboa.tecnico.learnjava.sibs.domain;

import pt.ulisboa.tecnico.learnjava.bank.exceptions.AccountException;
import pt.ulisboa.tecnico.learnjava.bank.services.Services;
import pt.ulisboa.tecnico.learnjava.sibs.exceptions.OperationException;
import statedesignpattern.Retry;
import statedesignpattern.TransferOperationState;

public class TransferOperation extends Operation {
	private final String sourceIban;
	private final String targetIban;
	private Context stateContext;

	public TransferOperation(String sourceIban, String targetIban, int value) throws OperationException {
		super(Operation.OPERATION_TRANSFER, value);

		if (invalidString(sourceIban) || invalidString(targetIban)) {
			throw new OperationException();
		}

		this.sourceIban = sourceIban;
		this.targetIban = targetIban;
		this.stateContext = new Context(sourceIban, targetIban, getValue(), commission());
	}

	private boolean invalidString(String name) {
		return name == null || name.length() == 0;
	}

	@Override
	public int commission() {
		return (int) Math.round(super.commission() + getValue() * 0.05);
	}

	public String getSourceIban() {
		return this.sourceIban;
	}

	public String getTargetIban() {
		return this.targetIban;
	}

	@Override
	public void process(Services services) throws OperationException, AccountException {
		try {
			this.stateContext.process(services);
		} catch (AccountException e) {
			this.stateContext.setState(new Retry(this.stateContext.getCurrentState()));
		}
	}

	public void cancel() throws OperationException, AccountException {

		this.stateContext.cancel();
	}

	public Context getStateContext() {
		return this.stateContext;
	}

	public TransferOperationState getState() {
		return this.stateContext.getCurrentState();
	}

}
