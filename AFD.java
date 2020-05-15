import java.io.*;
import java.util.*;

public class AFD {

	String[] args;
	String alfabeto = new String();
	String estadoFinal = new String();
	HashMap<String, ArrayList<String>> transicionesLambda = new HashMap<>();
	HashMap<String, ArrayList<String>> matrizTransicion = new HashMap<>();

	public AFD(String[] args, File AFN) throws IOException {
		this.args = args;
		System.out.println();
		System.out.println("===== AFD =====");

		this.leerDocumento(AFN);
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
		System.out.println("----- Transiciones lambda -----");
		for (Map.Entry<String, ArrayList<String>> entry : this.transicionesLambda.entrySet()) {
			System.out.println(entry.getKey() + " = " + Arrays.toString(entry.getValue().toArray()));
		}

		System.out.println("----- Matriz de tansiciones -----");
		for (Map.Entry<String, ArrayList<String>> entry : this.matrizTransicion.entrySet()) {
			System.out.println(entry.getKey() + " = " + Arrays.toString(entry.getValue().toArray()));
		}

		br.close();
	}

}