public class Gramatica {

	public static void main(String[] args) throws Exception {
		
		//Siempre van 3 argumentos como minimo
		if (args[1].equals("-check")) {
			Check check = new Check(args);
		} else {
			AFN afn = new AFN(args);
		}

	}

}