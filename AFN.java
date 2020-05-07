import java.io.*;
import java.util.*;

public class AFN {

	// formato de GLD
	ArrayList<String> terminales = new ArrayList<String>();
	String[] alfabeto;
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
		alfabeto = documento.get(1).split(",");
		terminalInicial = documento.get(2);
		String[] reglaProduccion;

		// factoriza las reglas de produccion necesarias
		System.out.println("--------------- Reglas de produccion sin factorizar ---------------");
		for (int i = 3; i < documento.size(); i++) {
			System.out.print(documento.get(i));
			reglaProduccion = documento.get(i).split("->");

			if (!terminales.contains(Character.toString(reglaProduccion[1].charAt(reglaProduccion[1].length() - 1)))
					&& reglaProduccion[1].length() > 1) {
				System.out.print("<-------- factorizar recursividad false");

				this.factorizar(reglaProduccion, false);

			} else if (reglaProduccion[1].length() > 2) {
				System.out.print("<-------- factorizar recursividad true");

				this.factorizar(reglaProduccion, true);

			} else {
				reglasProduccion.add(documento.get(i));
			}
			System.out.println();
		}
		System.out.println();
		System.out.println("--------------- Reglas de produccion ---------------");
		for (String string : reglasProduccion) {
			System.out.println(string);
		}

		this.crearArchivo(args);

		if (args[1].equals("-afd")) {
			AFD afd = new AFD(args);
		}
	}

	private void factorizar(String[] rp, boolean recursividad) {
		// recursividad true: ["S", "abbX"]
		// recursividad false: ["S", "abb"]

		String nrp;

		if (recursividad) {
			for (int j = 0; j < (rp[1].length() - 1); j++) {
				if (j == (rp[1].length() - 2)) {
					nrp = rp[0] + "->" + rp[1].charAt(j) + rp[1].charAt(rp[1].length() - 1);
				} else {
					nrp = rp[0] + "->" + rp[1].charAt(j) + rp[0];
				}
				this.reglasProduccion.add(nrp);
			}
		} else {
			for (int i = 0; i < rp[1].length(); i++) {
				if (i != (rp[1].length() - 1)) {
					nrp = rp[0] + "->" + rp[1].charAt(i) + rp[0];
				} else {
					nrp = rp[0] + "->" + rp[1].charAt(i);
				}
				this.reglasProduccion.add(nrp);
			}
		}
	}

	private void crearArchivo(String[] args) throws IOException {
		System.out.println(args[2]);
		File file = new File(args[2]);
		if (file.exists()) {
			System.out.println("archivo existe");
		} else {
			System.out.println("archivo no existe");
			
			if (file.createNewFile()) {
				System.out.println(file.getName() + " fue creado");
			} else {
				System.out.println("Yo creo que no se va apoder");
			}
		}
		
	}

}