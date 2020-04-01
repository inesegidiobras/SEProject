package statedesignpattern;

import pt.ulisboa.tecnico.learnjava.bank.exceptions.AccountException;
import pt.ulisboa.tecnico.learnjava.sibs.domain.Context;
import pt.ulisboa.tecnico.learnjava.sibs.exceptions.OperationException;

public class Withdrawn implements TransferOperationState {

	@Override
	public void process(Context state) throws AccountException, OperationException {
		state.deposit();
		state.setState(new Deposited());
	}

	@Override
	public void cancel(Context state) throws OperationException, AccountException {
		state.returnWithdraw();
		if (!state.checkSameBank()) {
			state.returnWithdrawCommission();
		}
		state.setState(new Cancelled());
	}
}
