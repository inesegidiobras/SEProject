package mvcdesignpattern;

public class MbwayMVCPattern {

	public static void main(String[] args) {

		Mbway model = retrieveMbwayAssociation();

		MbwayView view = new MbwayView();

		MbwayController controller = new MbwayController(model, view);

	}

	private static Mbway retrieveMbwayAssociation() {
		Mbway mbway = new Mbway();
		mbway.associateMbway("phoneNumber", "iban");

		return course;
	}
}
