import java.io.*;
import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;

public class AFD {

	String[] args;
	String alfabeto = new String();
	String estadoFinal = new String();
	HashMap<String, ArrayList<String>> transicionesLambda = new HashMap<>();
	HashMap<String, ArrayList<String>> matrizTransicion = new HashMap<>();
	HashMap<String, ArrayList<String>> clausurasBase = new HashMap<>();

	HashMap<String, Boolean> estadosAFD = new HashMap<>();
	HashMap<String, HashMap<String, ArrayList<String>>> algoritmo = new HashMap<>();

	public AFD(String[] args, File AFN) throws IOException {
		this.args = args;
		System.out.println();
		System.out.println("===== AFD =====");

		this.leerDocumento(AFN);
		this.clausurasLambda();

		// caso base
		this.estadosAFD.put(this.clausuraToString(clausurasBase.get("1")), false);

		this.algoritmoXd();

		System.out.println();
		System.out.println("Clausuras lambda cocn sus cambios");
		for (Entry<String, HashMap<String, ArrayList<String>>> entry : this.algoritmo.entrySet()) {
			System.out.println("=================");
			System.out.println(entry.getKey());
			for (Entry<String, ArrayList<String>> entry2 : entry.getValue().entrySet()) {
				System.out.println(entry2.getKey() + " = " + entry2.getValue());
			}
		}
	}

	private void leerDocumento(File file) throws IOException {
		FileReader fr = new FileReader(file);
		BufferedReader br = new BufferedReader(fr);
		ArrayList<String> documento = new ArrayList<>();
		String linea = "";

		while ((linea = br.readLine()) != null) {
			documento.add(linea);
		}

		this.alfabeto = documento.get(0);
		this.estadoFinal = documento.get(2);

		String[] ttll = documento.get(3).split(",");
		String[] tl;
		for (int i = 0; i < ttll.length; i++) {
			tl = ttll[i].split(";");
			ArrayList<String> transiciones = new ArrayList<>();

			for (String transicion : tl) {
				transiciones.add(transicion);
			}

			this.transicionesLambda.put(Integer.toString(i), transiciones);
		}

		int cont = 0;
		String[] fila;
		for (int i = 4; i < documento.size(); i++) {
			fila = documento.get(i).split(",");
			ArrayList<String> estados = new ArrayList<>();

			for (String estado : fila) {
				estados.add(estado);
			}
			this.matrizTransicion.put(this.alfabeto.split(",")[cont++], estados);
		}

		System.out.println("----- Matriz de tansiciones -----");
		for (Map.Entry<String, ArrayList<String>> entry : this.matrizTransicion.entrySet()) {
			System.out.println(entry.getKey() + " = " + Arrays.toString(entry.getValue().toArray()));
		}

		br.close();
	}

	private void clausurasLambda() {
		for (int i = 1; i < this.transicionesLambda.size(); i++) {
			Stack<String> estados = new Stack<>();
			Stack<String> array = new Stack<>();
			estados.add(Integer.toString(i));
			array.addAll(this.transicionesLambda.get(Integer.toString(i)));

			estados = this.primerasClausuras(estados, array, i);
			this.clausurasBase.put(Integer.toString(i), new ArrayList<>(estados));
		}

		HashMap<String, ArrayList<String>> clausuras = new HashMap<>();
		for (Map.Entry<String, ArrayList<String>> entry : this.clausurasBase.entrySet()) {
			String key = entry.getKey();
			ArrayList<String> value = entry.getValue();

			for (Map.Entry<String, ArrayList<String>> entry2 : this.clausurasBase.entrySet()) {
				String key2 = entry2.getKey();
				ArrayList<String> value2 = entry2.getValue();

				if (!key.equals(key2) && value.contains(key2)) {
					value.addAll(value2);
				}
			}
			clausuras.put(key, this.ordernarArray(value));
		}
		this.clausurasBase = clausuras;
	}

	private Stack<String> primerasClausuras(Stack<String> estados, Stack<String> array, int id) {

		if (array.size() == 1 && array.get(0).equals(Integer.toString(id))) {
			return estados;
		}

		for (int i = 0; i < array.size(); i++) {
			String str = array.pop();

			for (String string : this.transicionesLambda.get(str)) {
				if (!string.equals(str)) {
					this.addStack(estados, string);
				}
			}

			estados = this.primerasClausuras(this.addStack(estados, str), array, id);
		}

		return estados;
	}

	private Stack<String> addStack(Stack<String> a, String b) {
		a.add(b);
		return a;
	}

	private void algoritmoXd() {

		ArrayList<String> condicion = this.condicion();

		while (!condicion.get(0).equals("false")) {
			this.estadosAFD.replace(condicion.get(1), true); 
			this.algoritmo.put(condicion.get(1), this.cambios(condicion.get(1)));

			condicion = this.condicion();
		}

	}

	private String clausuras(String estado) {
		String[] estados = estado.split(",");
		ArrayList<String> array = new ArrayList<>();

		for (String str : estados) {
			array.addAll(this.clausurasBase.get(str));
		}

		String str = this.ordernarArray(array).toString().substring(1);
		str = str.substring(0, (str.length() - 1));
		str = str.replaceAll(", ", ",");
		return str;
	}

	private ArrayList<String> condicion() {
		ArrayList<String> result = new ArrayList<>();

		for (Map.Entry<String, Boolean> entry : this.estadosAFD.entrySet()) {
			if (!entry.getValue()) {
				result.add("true");
				result.add(entry.getKey());
				return result;
			}
		}
		result.add("false");
		return result;
	}

	private String clausuraToString(ArrayList<String> clausura) {
		String result = "";
		for (int i = 0; i < clausura.size(); i++) {
			result += clausura.get(i);
			if (i != (clausura.size() - 1)) {
				result += ",";
			}
		}
		return result;
	}

	private boolean nuevoEstado(String estado) {
		if (!this.estadosAFD.containsKey(estado)) {
			this.estadosAFD.put(estado, false);
			return true;
		}
		return false;
	}

	private HashMap<String, ArrayList<String>> cambios(String estado) {
		// [1, 2, 3]
		ArrayList<String> estados = new ArrayList<>(Arrays.asList(estado.split(",")));
		String[] alfabeto = this.alfabeto.split(",");
		HashMap<String, ArrayList<String>> cambios = new HashMap<>();

		for (int i = 0; i < alfabeto.length; i++) {
			// [0, 0, 2;7, 0, 0, 7, 2, 0]
			ArrayList<String> transiciones = this.matrizTransicion.get(alfabeto[i]);
			ArrayList<String> result = new ArrayList<>();

			for (int j = 0; j < transiciones.size(); j++) {
				// [2, 7]
				ArrayList<String> estadosTransiciones = new ArrayList<>(Arrays.asList(transiciones.get(j).split(";")));
				for (String string : estadosTransiciones) {
					if (estados.contains(string)) {
						result.add(string);
					}
				}
			}
			cambios.put(alfabeto[i], this.ordernarArray(result));
			if (this.ordernarArray(result).size() > 0) {
				String str = this.ordernarArray(result).toString().substring(1);
				str = str.substring(0, (str.length() - 1));
				str = str.replaceAll(", ", ",");
				this.nuevoEstado(this.clausuras(str));
			}
		}

		return cambios;
	}

	private ArrayList<String> ordernarArray(ArrayList<String> array) {
		List<String> newArray = array.stream().distinct().collect(Collectors.toList());
		ArrayList<String> result = new ArrayList<>(newArray);
		Collections.sort(result);

		return result;
	}
}