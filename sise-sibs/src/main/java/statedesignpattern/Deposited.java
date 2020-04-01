package statedesignpattern;

import pt.ulisboa.tecnico.learnjava.bank.exceptions.AccountException;
import pt.ulisboa.tecnico.learnjava.sibs.exceptions.OperationException;

public class Deposited implements TransferOperationState {

	@Override
	public void process(Context state) throws AccountException, OperationException {
		state.setState(new Processed());
	}

	@Override
	public void cancel(Context state) throws OperationException, AccountException {
		state.returnWithdraw();
		if (!state.checkSameBank()) {
			state.returnWithdrawCommission();
		}
		state.returnDeposit();
		state.setState(new Cancelled());
	}
}
