public class AFN {

	public AFN(String[] args) {
		System.out.println("----- AFN -----");
		for (String value : args) {
			System.out.println(value);
		}

		if (args[1].equals("-afd")) {
			AFD afd = new AFD(args);
		}
	}

}