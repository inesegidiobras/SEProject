package statedesignpattern;

import pt.ulisboa.tecnico.learnjava.bank.exceptions.AccountException;
import pt.ulisboa.tecnico.learnjava.sibs.exceptions.OperationException;

public class Error implements TransferOperationState {

	@Override
	public void process(Context state) throws AccountException, OperationException {
		state.setState(this);

	}

	@Override
	public void cancel(Context state) throws OperationException, AccountException {
		state.setState(this);

	}

}
