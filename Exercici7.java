package Activitat1;
import java.io.File;
import java.util.Scanner;

public class Exercici7 {

	public static void main(String[] args) {
		
		Exercici7 programa = new Exercici7();
		programa.inici();
		
	}
		
		public void inici() {
			try {
					Scanner lector = new Scanner(System.in);
					
					System.out.println("Digam la ruta, de la carpeta que vols llistar: ");
					String ruta = lector.nextLine();
					
					File r = new File(ruta);
					
					if(r.isDirectory()) {
							carpeta(r);
					}
			
			
			}catch(Exception e){
			
			}
		}
		public void carpeta(File carpeta) {
			
			File[] array = carpeta.listFiles();
			
			for(int i = 0; i<array.length; i++) {
				System.out.println(array[i]);
			}
			
		}

}
