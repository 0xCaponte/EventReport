/* Autor: Carlos Aponte -- 09-10041
 * 
 * Clase: APP
 * 
 * Descripcion: esta clase extiende a APPLICATION, y se usa como metodo para crear, modificar y accedes
 * a diversas variables de categoria global.
 *  */

package caracas.ciudadania.segura;

import java.util.ArrayList;

import android.app.Application;

public class App extends Application {

	private static App singleton;
   
	public static App getInstance() {
        return singleton;
    }
	
	@Override
    public void onCreate() {
        super.onCreate();
        singleton = this;
    }
	
	private static String mensaje = ""; // Mensaje de Emergencia.
	private static String nroTlf = ""; //Nro de Tlf del Usuario.
	private static double longitud = 0.0; //Coordenada: latitud.
	private static double latitud = 0.0;  // Coordenada: longitud.
	private static String archivoIncidentes = "incidentes.xml"; //Archivo donde se almacenan los incidentes.
	
	//Cada contacto tendra dos posiciones, la primera con el nombre de contacto, seguida por el numero de tlf.
	private static ArrayList<String> contactos = new ArrayList<String>(); //Contactos a informar de una emergencia
	
	
	
	/** ------- Getters y Setters ------- */
	public static String getMensaje() {
		return mensaje;
	}

	public static void setMensaje(String mensaje) {
		App.mensaje = mensaje;
	}

	public static ArrayList<String> getContactos() {
		return contactos;
	}

	public static void setContactos(ArrayList<String> contactos) {
		App.contactos = contactos;
	}

	public static void addContacto(String c) {
		App.contactos.add(c);
	}
	
	public static void eliminarContacto(String c){
		int i = App.contactos.indexOf(c);
		App.contactos.remove(i);
		App.contactos.remove(i+1);
	}
	
	public static String getArchivoIncidentes() {
		return archivoIncidentes;
	}

	public static void setArchivoIncidentes(String a) {
		archivoIncidentes = a;
	}

	public static String getNroTlf() {
		return nroTlf;
	}

	public static  void setNroTlf(String n) {
		nroTlf = n;
	}

	public static  double getLongitud() {
		return longitud;
	}

	public static  void setLongitud(double l) {
		longitud = l;
	}

	public static  double getLatitud() {
		return latitud;
	}

	public static  void setLatitud(double l) {
		latitud = l;
	}

}

