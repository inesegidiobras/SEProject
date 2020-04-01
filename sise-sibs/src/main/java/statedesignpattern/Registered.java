package statedesignpattern;

import pt.ulisboa.tecnico.learnjava.bank.exceptions.AccountException;
import pt.ulisboa.tecnico.learnjava.sibs.exceptions.OperationException;

public class Registered implements TransferOperationState {

	@Override
	public void process(Context state) throws AccountException, OperationException {
		if (!state.checkSameBank()) {
			state.withdraw();
			state.withdrawCommission();
		} else {
			state.withdraw();
		}
		state.setState(new Withdrawn());
	}

	@Override
	public void cancel(Context state) throws OperationException, AccountException {
		state.setState(new Cancelled());
	}
}
