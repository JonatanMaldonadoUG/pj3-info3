import java.io.*;
import java.util.*;
import java.util.stream.Stream;

public class AFN {

	// formato de GLD
	ArrayList<String> terminales = new ArrayList<String>();
	String alfabeto;
	String terminalInicial;
	ArrayList<String> reglasProduccion = new ArrayList<String>();
	File AFN;

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
				reglasProduccion.add(documento.get(i));
			}
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
		String estadosFinales = this.estadosFinales();
		String transicionesLambda = this.transicionesLambda();

		pw.println(this.alfabeto);
		pw.println(this.reglasProduccion.size() + 1);
		pw.println(estadosFinales);
		pw.println(transicionesLambda);

		for (int i = 0; i < this.alfabeto.split(",").length; i++) {
			String fila = "0,";
			fila = this.transiciones(fila, 0, this.alfabeto.split(",")[i]);
			pw.print(this.alfabeto.split(",")[i] + " ----> ");
			if (i != this.alfabeto.split(",").length - 1) {
				pw.println(fila.substring(0, fila.length() - 1));
			} else {
				pw.print(fila.substring(0, fila.length() - 1));
			}
		}

		this.AFN = file;
		pw.close();
	}

	private String estadosFinales() {
		ArrayList<String> nrp = new ArrayList<String>();
		for (int i = 0; i < this.reglasProduccion.size(); i++) {
			String[] rp = this.reglasProduccion.get(i).split("->");
			if (rp[1].length() == 1 && !this.terminales.contains(Character.toString(rp[1].charAt(0)))) {
				nrp.add(this.reglasProduccion.get(i) + this.terminalInicial + "@"); // estado final termina en @
			} else {
				nrp.add(this.reglasProduccion.get(i));
			}
			if (i == this.reglasProduccion.size() - 1) {
				this.nuevaTerminal(this.terminalInicial + "@");
				nrp.add(this.terminalInicial + "@->@");
			}
		}
		this.reglasProduccion = nrp;

		System.out.println("---------- Reglas de produccion factorizadas ----------");
		for (int i = 0; i < nrp.size(); i++) {
			System.out.println(nrp.get(i) + " <---- " + (i + 1));
		}

		return Integer.toString(this.reglasProduccion.size());
	}

	private String transicionesLambda() {
		System.out.println("=================");
		String tl = "0,";
		for (int i = 0; i < this.reglasProduccion.size(); i++) {
			tl += (i + 1);

			String[] rp = this.reglasProduccion.get(i).split("->");
			if (rp[1].length() == 1 && this.terminales.contains(rp[1])) {
				Stream<String> stream = this.reglasProduccion.stream().filter(x -> x.split("->")[0].equals(rp[1]));
				String[] array = stream.toArray(String[]::new);
				for (int j = 0; j < this.reglasProduccion.size(); j++) {
					for (int k = 0; k < array.length; k++) {
						if (this.reglasProduccion.get(j).equals(array[k])
								&& !this.reglasProduccion.get(i).equals(array[k])) {
							tl += ";" + (j + 1);
						}
					}
				}
			}
			if (i != (this.reglasProduccion.size() - 1))
				tl += ",";
		}
		return tl;
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