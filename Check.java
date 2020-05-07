import java.io.BufferedReader;
import java.io.*;
import java.io.FileInputStream;
import java.util.Arrays;
import java.io.IOException;
import java.util.Scanner.*;
import java.util.*;

public class Check {
	public int comparar;
	public LinkedList<String> estados = new LinkedList<String>();
	public LinkedList<String> total = new LinkedList<String>();
	public LinkedList<String> finales = new LinkedList<String>();
	public LinkedList<String> conjutoEstados = new LinkedList<String>();// conjunto-transiciones
	public LinkedList<String> almacen = new LinkedList<String>();
	public BufferedReader br;

	// public Check(String[] args) {
	// System.out.println("----- Check -----");
	// for (String value : args) {
	// System.out.println(value);
	// }
	// }

	public Check(String[] args) throws IOException {
		System.out.println("----- Check -----");
		// for (String value : args) {
		// System.out.println(value);
		// }
		FileInputStream fstreamEstados = new FileInputStream(args[0]);// lee archivo con estados
		br = new BufferedReader(new InputStreamReader(fstreamEstados));
		String lineaCheck;
		int linea = 0;

		while ((lineaCheck = br.readLine()) != null) {
			if (linea == 0) { // conjunto de estados
				String[] estadosa = lineaCheck.split(",");
				estados = new LinkedList<String>(Arrays.asList(estadosa));
				estados = new LinkedList<String>(Arrays.asList(estadosa));
				estados = new LinkedList<String>(Arrays.asList(estadosa));
				estados = new LinkedList<String>(Arrays.asList(estadosa));
				estados = new LinkedList<String>(Arrays.asList(estadosa));
			} else if (linea == 1) { // numero total de estados incluido el absorvente(total)
				String[] totala = lineaCheck.split(",");
				total = new LinkedList<String>(Arrays.asList(totala));
			} else if (linea == 2) { // conjunto de estados finales
				String[] finalesa = lineaCheck.split(",");
				finales = new LinkedList<String>(Arrays.asList(finalesa));
			} else { // transiciones de estados

				conjutoEstados.add(lineaCheck);

				System.out.println(lineaCheck);
			}

			linea++;
		}
		br.close();

		//////////////////////////////////////////////////////////////////////////

		FileInputStream fStream2 = new FileInputStream(args[3]);
		br = new BufferedReader(new InputStreamReader(fStream2));
		String str;
		LinkedList<String> cuerdas = new LinkedList<String>();

		while ((str = br.readLine()) != null) {
			cuerdas.add(str);
		}

		for (String cuerda : cuerdas) {

			System.out.println(this.accept(cuerda));

		}

	}

	/*
	 * Implemente el metodo transition, que recibe de argumento un entero que
	 * representa el estado actual del Check y un caracter que representa el simbolo
	 * a consumir, y devuelve un entero que representa el siguiente estado
	 */
	public int getTransition(int currentState, char symbol) {
		almacen.clear();
		int int_estado = estados.indexOf(Character.toString(symbol));
		if(estados.indexOf(Character.toString(symbol)) == -1) return 0;
		String [] leido = conjutoEstados.get(int_estado).split(",");
		for (String string : leido) {
			almacen.add(string);
			System.out.println(string);
		}
		System.out.println(currentState);
		return Integer.parseInt(almacen.get(currentState));
	}

	/*
	 * Implemente el metodo accept, que recibe como argumento un String que
	 * representa la cuerda a evaluar, y devuelve un boolean dependiendo de si la
	 * cuerda es aceptada o no por el Check
	 */
	public boolean accept(String string) {
		comparar = 0;
		for (int i = 0; i < string.length(); i++) {
			comparar = this.getTransition(comparar, string.charAt(i));
		}
		if (finales.indexOf(Integer.toString(comparar)) != -1) {
			return true;
		}
		return false;
	}

}