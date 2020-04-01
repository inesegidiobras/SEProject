package statedesignpattern;

import pt.ulisboa.tecnico.learnjava.bank.exceptions.AccountException;
import pt.ulisboa.tecnico.learnjava.bank.services.Services;
import pt.ulisboa.tecnico.learnjava.sibs.exceptions.OperationException;

public class Retry implements TransferOperationState {

	private int count = 3;
	public TransferOperationState stateBeforeRetry;
	Services services;

	public Retry(TransferOperationState stateBeforeRetry) {
		this.stateBeforeRetry = stateBeforeRetry;
	}

	@Override
	public void process(Context state) throws AccountException, OperationException {

		try {
			state.setState(this.stateBeforeRetry);
			state.process(state.getServices());
		} catch (AccountException e) {
			this.count--;
			state.setState(this);
		}
		if (this.count == 0) {
			this.cancel(state);
			state.setState(new Error());
		}
	}

	@Override
	public void cancel(Context state) throws OperationException, AccountException {
		state.setState(this.stateBeforeRetry);
		state.cancel();
	}
}
