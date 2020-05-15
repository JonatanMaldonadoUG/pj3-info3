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
	HashMap<String, ArrayList<String>> estadosAFD = new HashMap<String, ArrayList<String>>();

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

		this.estadosFinales();
		this.crearAFN(this.crearArchivo(args));

		if (args[1].equals("-afd")) {
			AFD afd = new AFD(args, this.AFN);
		}
	}

	/**
	 * Separa y crea nuevos estados en las reglas de produccion que tengan mas de
	 * una letra del alfabeto.
	 *
	 * @param rp           regla de produccion del GLD con split de "->".
	 * @param recursividad true si la rp se dirige hacia otro estado.
	 * @param id           identificador para crear los nuevos estados.
	 */
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

	/**
	 * Verifica si el estado existe para agregarlo.
	 *
	 * @param terminal estado que se desea agregar.
	 */
	private void nuevaTerminal(String terminal) {
		if (!this.terminales.contains(terminal)) {
			this.terminales.add(terminal);
		}
	}

	/**
	 * Separa el alfabeto del estado al que se dirige.
	 *
	 * @param rp contradominio de la regla de produccion.
	 * @return String[] con el alfabeto separado del estado al que se dirige.
	 */
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

	/**
	 * Edita el archivo_salida y sino existe lo crea.
	 *
	 * @param args argumentos de la clase main.
	 * @return File del AFN.
	 */
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

	/**
	 * Se coloca toda la data dentro del archivo AFN.
	 *
	 * @param file archivo en el que se insertara el AFN.
	 */
	private void crearAFN(File file) throws IOException {
		this.arreglarEstados();
		FileWriter fw = new FileWriter(file);
		PrintWriter pw = new PrintWriter(fw);
		String estadosFinales = Integer.toString(this.estadosAFD.size());
		String transicionesLambda = this.transicionesLambda();

		pw.println(this.alfabeto);
		pw.println(this.estadosAFD.size() + 1);
		pw.println(estadosFinales);
		pw.println(transicionesLambda);

		for (int i = 0; i < this.alfabeto.split(",").length; i++) {
			String fila = "0,";
			fila = this.transiciones(fila, 0, this.alfabeto.split(",")[i]);
			if (i != this.alfabeto.split(",").length - 1) {
				pw.println(fila.substring(0, fila.length() - 1));
			} else {
				pw.print(fila.substring(0, fila.length() - 1));
			}
		}

		this.AFN = file;
		pw.close();
	}

	/**
	 * Detecta los estados finales del GLD y los apunta a un solo estado final.
	 *
	 */
	private void estadosFinales() {
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
	}

	/**
	 * Guarda las reglas de produccion en un HashMap segun su simbolo terminal.
	 *
	 */
	private void arreglarEstados() {
		System.out.println("----- Estados AFN -----");

		// guarda las reglas de produccion en su respectiva terminal
		for (String string : this.terminales) {
			ArrayList<String> array = new ArrayList<String>();
			Stream<String> stream = this.reglasProduccion.stream().filter(x -> x.split("->")[0].equals(string));
			String[] rrpp = stream.toArray(String[]::new);
			for (String rp : rrpp) {
				String[] nrp = rp.split("->");
				array.add(nrp[1]);
			}
			this.estadosAFD.put(string, array);
		}

		// elimina los estados que se encuentran vacios
		for (Map.Entry<String, ArrayList<String>> entry : this.estadosAFD.entrySet()) {
			if (entry.getValue().size() == 0) {
				this.estadosAFD.remove(entry.getKey());
				this.terminales.remove(this.terminales.indexOf(entry.getKey()));
			}
		}

		int cont = 0;
		for (String string : terminales) {
			System.out.println(
					string + " = " + Arrays.toString(this.estadosAFD.get(string).toArray()) + " <----- " + ++cont);
		}
	}

	/**
	 * Recorre las terminales para detectar si alguna hace transicion con lambda.
	 *
	 */
	private String transicionesLambda() {
		String tl = "0,";

		for (int i = 0; i < this.terminales.size(); i++) {
			tl += (i + 1);
			String terminal = this.terminales.get(i);
			ArrayList<String> opciones = this.estadosAFD.get(terminal);
			for (String opcion : opciones) {
				if (opcion.length() == 1 && this.terminales.contains(opcion)) {
					for (int j = 0; j < this.terminales.size(); j++) {
						if (this.terminales.get(j).equals(opcion))
							tl += ";" + (j + 1);
					}
				}
			}
			if (i != (this.terminales.size() - 1))
				tl += ",";
		}
		return tl;
	}

	/**
	 * Funcion recurciva para obtener las transiciones de una letra del GLD.
	 *
	 * @param fila  String de las transiciones segun la letra.
	 * @param cont  contador.
	 * @param letra letra de la que se obtendra las transiciones.
	 * @return String de las transiciones que hace la letra separadas por ",".
	 */
	private String transiciones(String fila, int cont, String letra) {
		if (cont >= (this.terminales.size())) {
			return fila;
		}

		String estado = this.terminales.get(cont); // formato: W | X | Y
		ArrayList<String> opciones = this.estadosAFD.get(estado); // formato: ["bX", "aY", "W"]
		String str = "";

		for (int i = 0; i < opciones.size(); i++) {
			String opcion = opciones.get(i); // formato: "bX" | "X"
			String[] nrp = {}; // formato: ["b", "X"]
			String srp = ""; // formato: "X"
			String index;

			if (opcion.length() > 1) {
				nrp = this.reglaProduccion(opcion);
				index = Integer.toString(this.terminales.indexOf(nrp[1]) + 1);
			} else {
				srp = Character.toString(opcion.charAt(0));
				index = Integer.toString(this.terminales.indexOf(srp) + 1);
			}

			if ((nrp.length != 0 && nrp[0].equals(letra)) || (srp.length() != 0 && srp.equals(letra))) {
				if (str.length() != 0) {
					str += ";" + index;
				} else {
					str += index;
				}
			}
		}

		if (str.length() != 0) {
			fila = this.transiciones(this.concatenarFila(fila, str), cont + 1, letra);
		} else {
			fila = this.transiciones(this.concatenarFila(fila, "0"), cont + 1, letra);
		}

		return fila;
	}

	/**
	 * Concatena el estado a la fila.
	 *
	 * @param fila   String de las transiciones segun la letra.
	 * @param estado estado que se debe concatenar en la matriz.
	 * @return String de las transiciones que hace la letra separadas por ",".
	 */
	private String concatenarFila(String fila, String estado) {
		return fila += estado + ",";
	}

}