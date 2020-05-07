import java.io.BufferedReader;
import java.io.*;
import java.io.FileInputStream; 
import java.util.Arrays;
import java.io.IOException;
import java.util.Scanner.*;
import java.util.*;
import java.util.ArrayList;

public class Check {
    public int comparar;
	public LinkedList<String> estados = new LinkedList<String>();
	public LinkedList<String> total = new LinkedList<String>();
	public LinkedList<String> finales = new LinkedList<String>();
	public LinkedList<String> conjutoEstados = new LinkedList<String>();//conjunto-transiciones
	public LinkedList<String> almacen = new LinkedList<String>();
    public BufferedReader br;
    
    // public Check(String[] args) {
    //     System.out.println("----- Check -----");
    //     for (String value : args) {
    //         System.out.println(value);
    //     }
    // }

	public Check(String[] args) {
		System.out.println("----- Check -----");
		// for (String value : args) {
		// System.out.println(value);
		// }
		try {
			FileInputStream fstreamEstados = new FileInputStream(args);// lee archivo con estados
			br = new BufferedReader(new InputStreamReader(fstreamEstados));
			String lineaCheck;
			int linea = 0;

		while ((lineaCheck = br.readLine()) != null)   {
			if (linea == 0) { // conjunto de estados
				String[] estadosa=lineaCheck.split(",");
				estados = new LinkedList<String>(Arrays.asList(estadosa)); 
			}else if (linea == 1) { // numero total de estados incluido el absorvente(total)
				String[] totala=lineaCheck.split(",");	
				total = new LinkedList<String>(Arrays.asList(totala));
			}else if (linea == 2) { //conjunto de estados finales
				String[] finalesa=lineaCheck.split(",");
				finales = new LinkedList<String>(Arrays.asList(finalesa));
			}else { //transiciones de estados
				
						conjutoEstados.add(lineaCheck);
			}
			linea++;
		}
	br.close();
		} catch(IOException e){ 
				System.out.println("\n");
				System.out.println("Asegurese de haber ingresado el nombre de archivo correctamente");
			}
	}
	/*
		Implemente el metodo transition, que recibe de argumento
		un entero que representa el estado actual del Check y un
		caracter que representa el simbolo a consumir, y devuelve 
		un entero que representa el siguiente estado
	*/
	public int getTransition(int currentState, char symbol){
		almacen.clear();
		int int_estado = estados.indexOf(Character.toString(symbol));
		if(estados.indexOf(Character.toString(symbol)) == -1) return 0;
		String [] leido = conjutoEstados.get(int_estado).split(",");
		for(int i = 0; i < leido.length; i++){
			almacen.add(leido[i]);
		} 
		return Integer.parseInt(almacen.get(currentState));
	}

	/*
		Implemente el metodo accept, que recibe como argumento
		un String que representa la cuerda a evaluar, y devuelve
		un boolean dependiendo de si la cuerda es aceptada o no 
		por el Check
	*/
	public boolean accept(String string){
		comparar = 1;
		for(int i = 0;i < string.length(); i++){
			comparar = this.getTransition(comparar, string.charAt(i));
		}
		if(finales.indexOf(Integer.toString(comparar)) != -1){
			return true;
		}
		return false;
	}

	/*
		El metodo main debe recibir como primer argumento el args
		donde se encuentra el archivo ".Check", como segundo argumento 
		una bandera ("-f" o "-i"). Si la bandera es "-f", debe recibir
		como tercer argumento el args del archivo con las cuerdas a 
		evaluar, y si es "-i", debe empezar a evaluar cuerdas ingresadas
		por el usuario una a una hasta leer una cuerda vacia (""), en cuyo
		caso debe terminar. Tiene la libertad de implementar este metodo
		de la forma que desee. 
	*/
	public static void main(String[] args) throws Exception{
		String comp = "-i";
		String comp2 = "-f";
		boolean salir = false;
		Check Check = new Check(args[0]);
		if (args.length == 2 && args[1].equals(comp)){
			System.out.println("Ejecutando interactivo:\n");
			Scanner reader = new Scanner(System.in);
			System.out.print("Ingrese un cadena: ");
			String cuerdaLeida = reader.nextLine();
			while(salir != true){
				if(cuerdaLeida.length() == 0){
					salir = true;
				}
				//System.out.println(cuerdaLeida.length());
				System.out.println(Check.accept(cuerdaLeida));
				System.out.print("Ingrese un cadena: ");
				cuerdaLeida = reader.nextLine();
			}

		}else if (args.length == 3 && args[1].equals(comp2)){
			String cuerdaLeida;
			System.out.println("Ejecutando Batch:\n");
			FileInputStream fstreamCuerdas = new FileInputStream(args[2]);
			BufferedReader br2 = new BufferedReader(new InputStreamReader(fstreamCuerdas));
			while((cuerdaLeida = br2.readLine()) != null){
				System.out.println(Check.accept(cuerdaLeida)); 
			}
		}
	}
}