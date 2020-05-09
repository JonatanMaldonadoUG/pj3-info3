import java.io.*;
import java.util.*;

public class AFN {

	// formato de GLD
	ArrayList<String> terminales = new ArrayList<String>();
	String alfabeto;
	String terminalInicial;
	ArrayList<String> reglasProduccion = new ArrayList<String>();

	public AFN(String[] args) throws IOException {
		System.out.println("===== AFN =====");
		System.out.println();

		// lee el gld y lo pasa a un ArrayList
		FileReader fr = new FileReader(args[0]);
		BufferedReader br = new BufferedReader(fr);
		ArrayList<String> documento = new ArrayList<String>();
		String linea;

		while ((linea = br.readLine()) != null) {
			documento.add(linea);
		}
		br.close();

		// declara los formatos del GLD
		for (String string : documento.get(0).split(",")) {
			terminales.add(string);
		}
		alfabeto = documento.get(1);
		terminalInicial = documento.get(2);
		String[] reglaProduccion;

		// factoriza las reglas de produccion necesarias
		for (int i = 3; i < documento.size(); i++) {
			reglaProduccion = documento.get(i).split("->");

			if (!terminales.contains(Character.toString(reglaProduccion[1].charAt(reglaProduccion[1].length() - 1)))
					&& reglaProduccion[1].length() > 1) {

				this.factorizar(reglaProduccion, false, i);

			} else if (reglaProduccion[1].length() > 2) {

				this.factorizar(reglaProduccion, true, i);

			} else {
				System.out.println(documento.get(i));
				reglasProduccion.add(documento.get(i));
			}
		}

		System.out.println("--------------- Reglas de produccion factorizadas ---------------");
		for (String string : reglasProduccion) {
			System.out.println(string);
		}

		this.crearAFN(this.crearArchivo(args));

		if (args[1].equals("-afd")) {
			AFD afd = new AFD(args);
		}
	}

	private void factorizar(String[] rp, boolean recursividad, int id) {

		String nrp; // nueva regla produccion
		String terminal; // nueva terminal

		if (recursividad) { // recursividad true: ["S", "abbX"]
			int cont = 1;
			for (int j = 0; j < (rp[1].length() - 1); j++) {
				if (j != (rp[1].length() - 2)) {
					if (cont != 1) {
						terminal = rp[0] + id + (cont - 1);
						nrp = terminal + "->" + rp[1].charAt(j) + rp[0] + id + cont++;
						this.nuevaTerminal(terminal);
					} else {
						nrp = rp[0] + "->" + rp[1].charAt(j) + rp[0] + id + cont++;
					}
				} else {
					if (cont != 1) {
						terminal = rp[0] + id + (cont - 1);
						nrp = terminal + "->" + rp[1].charAt(j) + rp[1].charAt(rp[1].length() - 1);
						this.nuevaTerminal(terminal);
					} else {
						nrp = rp[0] + id + "->" + rp[1].charAt(j) + rp[1].charAt(rp[1].length() - 1);
					}
				}
				this.reglasProduccion.add(nrp);
			}
		} else { // recursividad false: ["S", "abb"]
			int cont = 1;
			for (int i = 0; i < rp[1].length(); i++) {
				if (i != (rp[1].length() - 1)) {
					if (cont != 1) {
						terminal = rp[0] + id + (cont - 1);
						nrp = terminal + "->" + rp[1].charAt(i) + rp[0] + id + cont++;
						this.nuevaTerminal(terminal);
					} else {
						nrp = rp[0] + "->" + rp[1].charAt(i) + rp[0] + id + cont++;
					}
				} else {
					if (cont != 1) {
						terminal = rp[0] + id + (cont - 1);
						nrp = terminal + "->" + rp[1].charAt(i);
						this.nuevaTerminal(terminal);
					} else {
						nrp = rp[0] + id + "->" + rp[1].charAt(i);
					}
				}
				this.reglasProduccion.add(nrp);
			}
		}
	}

	private void nuevaTerminal(String terminal) {
		if (!this.terminales.contains(terminal)) {
			this.terminales.add(terminal);
		}
	}

	private String[] reglaProduccion(String rp) {
		// formato rp: abbX | abbX2
		Stack<String> stack = new Stack<String>();
		String[] str = {};

		for (int i = rp.length() - 1; i >= 0; i--) {
			if (this.terminales.contains(Character.toString(rp.charAt(i)))) {
				str = rp.split(Character.toString(rp.charAt(i)));
				for (int j = 0; j < str.length; j++) {
					if (str.length > 1) {
						if (j == str.length - 1) {
							stack.add(rp.charAt(i) + str[j]);
						} else {
							stack.add(str[j]);
						}
					} else {
						stack.add(str[j]);
						stack.add(Character.toString(rp.charAt(i)));
					}
				}
				break;
			} else if (rp.length() == 1) {
				stack.add(Character.toString(rp.charAt(i)));
			}
		}

		String values = Arrays.toString(stack.toArray());
		values = values.substring(1);
		values = values.substring(0, values.length() - 1);

		// retorna: {abb, X} | {abb, X21}
		return values.split(", ");
	}

	private File crearArchivo(String[] args) throws IOException {
		File file = new File(args[2]);
		if (!file.exists()) {
			if (!file.createNewFile()) {
				System.out.println("No se pudo crear el archivo de ruta: " + args[2]);
				System.exit(0);
			}
		}
		return file;
	}

	private void crearAFN(File file) throws IOException {
		FileWriter fw = new FileWriter(file);
		PrintWriter pw = new PrintWriter(fw);
		String estadosFinales = "";
		String transicionesLambda = "";

		for (int i = 0; i < this.reglasProduccion.size(); i++) {
			String[] rp = reglasProduccion.get(i).split("->");
			if (rp[1].length() == 1 && !this.terminales.contains(Character.toString(rp[1].charAt(0)))) {
				estadosFinales += i + 1;
				if (i != this.reglasProduccion.size() - 1)
					estadosFinales += ",";
			}
		}

		for (int i = 0; i <= this.reglasProduccion.size(); i++) {
			transicionesLambda += i;

			if (i != this.reglasProduccion.size())
				transicionesLambda += ",";
		}

		pw.println(this.alfabeto);
		pw.println(this.reglasProduccion.size() + 1);
		pw.println(estadosFinales);
		pw.println(transicionesLambda);

		for (String letra : this.alfabeto.split(",")) {
			String fila = "0,";
			fila = this.transiciones(fila, 0, letra);
			System.out.print(letra + " ----> ");
			System.out.println(fila);
		}

		/*
		 * for (String letra : this.alfabeto.split(",")) { String fila = "0,"; for (int
		 * i = 0; i < this.reglasProduccion.size(); i++) { String[] rp =
		 * this.reglasProduccion.get(i).split("->"); if (rp[1].length() == 2) { if
		 * (terminales.contains(Character.toString(rp[1].charAt(1)))) { fila += "0,"; }
		 * } } pw.println(fila); }
		 */

		pw.close();
	}

	private String transiciones(String fila, int cont, String letra) {
		if (cont >= (this.reglasProduccion.size())) {
			return fila;
		}

		String[] rp = this.reglasProduccion.get(cont).split("->"); // formato: ["S", "bX"]
		String[] nrp = {}; // formato: ["b", "X"]
		String srp = ""; // formato: "b"

		if (rp[1].length() > 1) {
			nrp = this.reglaProduccion(rp[1]);
		} else {
			srp = Character.toString(rp[1].charAt(0));
		}

		if ((nrp.length != 0 && nrp[0].equals(letra)) || (srp.length() != 0 && srp.equals(letra))) {
			fila = this.transiciones(this.concatenarFila(fila, cont + 1), cont + 1, letra);
		} else {
			fila = this.transiciones(this.concatenarFila(fila, 0), cont + 1, letra);
		}

		return fila;
	}

	private String concatenarFila(String fila, int estado) {
		return fila += estado + ",";
	}

}