/* Autor: Carlos Aponte -- 09-10041
 * 
 * Clase: Preferencias
 * 
 * Descripcion: Esta clase se encarga de manejar todo lo referente a las SharedPreferences 
 * de la aplicacion. Estas preferencias son una estrategia para efectuar el almacenamiento de
 * pares de datos, los cuales en nuestro caso sirven para mantener en la memoria del dispositivo
 * la configuracion de la aplicacion.
 */

package caracas.ciudadania.segura;

import java.util.ArrayList;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class Preferencias {

	Boolean preparado;

	public Preferencias() {
		super();
		this.preparado = false;
	}

	// Funcion que obtiene la lista de contactos de emergencia
	public ArrayList<String> getContactos(Context c) {

		ArrayList<String> l = new ArrayList<String>();
		SharedPreferences prefs = c.getSharedPreferences(
				"caracas.ciudadania.segura", Context.MODE_PRIVATE);

		// De cada contacto se tiene nombre y tlf
		for (int i = 1; i <= 3; i++) {

			String tmp = "nombreContacto" + i;
			String tmp2 = "contacto" + i;

			if (prefs.contains(tmp) & prefs.contains(tmp2)) {

				l.add(prefs.getString(tmp, null));
				l.add(prefs.getString(tmp2, null));
			}
		}

		return l;
	}
	
	

	// Funcion que guarda una nueva lista de contactos en la memoria del tlf
	public boolean setContactos(Context c, ArrayList<String> l) {

		// Obtiene las preferencias del app
		SharedPreferences prefs = c.getSharedPreferences(
				"caracas.ciudadania.segura", Context.MODE_PRIVATE);

		// Permite editar las preferencias
		Editor editor = prefs.edit();

		for (int i = 1; i <= 3; i++) {

			int pos = (i - 1) * 2;
			String tmp = "contacto" + i;
			String tmp2 = "nombreContacto" + i;
			String nombre = l.get(pos);
			String tlf = l.get(pos + 1);
			editor.putString(tmp, tlf);
			editor.putString(tmp2, nombre);
		}

		return editor.commit(); // Guarda los cambios

	}

	// Funcion que obtiene la lista de noticias guardada
	public ArrayList<Noticia> getNoticias(Context c) {

		ArrayList<Noticia> l = new ArrayList<Noticia>();
		SharedPreferences prefs = c.getSharedPreferences(
				"caracas.ciudadania.segura", Context.MODE_PRIVATE);

		// De cada contacto se tiene nombre y tlf
		for (int i = 0; i <= 3; i++) {

			String tmp = "noticia" + i;
			String tmp2 = "link" + i;

			Noticia noti = new Noticia(prefs.getString(tmp, null),
					prefs.getString(tmp2, null));
			l.add(noti);
		}

		return l;
	}

	// Funcion que guarda una nueva lista de noticias en la memoria del tlf
	public boolean setNoticias(Context c, ArrayList<Noticia> l) {

		// Obtiene las preferencias del app
		SharedPreferences prefs = c.getSharedPreferences(
				"caracas.ciudadania.segura", Context.MODE_PRIVATE);

		// Permite editar las preferencias
		Editor editor = prefs.edit();

		for (int i = 0; i < l.size(); i++) {

			String tmp = "noticia" + i;
			String tmp2 = "link" + i;
			Noticia noti = l.get(i);

			editor.putString(tmp, null);
			
			editor.putString(tmp, noti.getTitulo());
			editor.putString(tmp2, noti.getLink());
		}

		return editor.commit(); // Guarda los cambios

	}

	// Obtiene el mensaje de emergencias almacenado
	public String getMensaje(Context c) {

		String s = "Emergencia!!!  " + App.getNroTlf();
		SharedPreferences prefs = c.getSharedPreferences(
				"caracas.ciudadania.segura", Context.MODE_PRIVATE);

		if (prefs.contains("mensaje")) {
			// Si esta vacio, se tomara el mensaje de Emergencia + tlf
			s = prefs.getString("mensaje",
					"Emergencia!!!\n Tlf:  " + App.getNroTlf());
		}

		return s;
	}

	// Verifica si el mensaje esta vacio
	public boolean mensajeEmpty(Context c) {

		SharedPreferences prefs = c.getSharedPreferences(
				"caracas.ciudadania.segura", Context.MODE_PRIVATE);

		if (prefs.contains("mensaje")) {
			return false;
		}

		return true;
	}

	// Establece el mensaje de emergencia en las preferencias
	public boolean setMensaje(Context c, String m) {

		SharedPreferences prefs = c.getSharedPreferences(
				"caracas.ciudadania.segura", Context.MODE_PRIVATE);
		Editor editor = prefs.edit();

		editor.putString("mensaje", m);

		return editor.commit();

	}

	// Establece el servidor de la app
	public boolean setServer(Context c, String m) {

		SharedPreferences prefs = c.getSharedPreferences(
				"caracas.ciudadania.segura", Context.MODE_PRIVATE);
		Editor editor = prefs.edit();

		editor.putString("server", m);

		return editor.commit();

	}

	// Obtiene el servidor de la app
	public String getServer(Context c) {

		SharedPreferences prefs = c.getSharedPreferences(
				"caracas.ciudadania.segura", Context.MODE_PRIVATE);

		String s = prefs.getString("server", null);

		return s;
	}

	// Obtiene la cantidad de contactos almacenados
	public int sizeContactos(Context c) {

		SharedPreferences prefs = c.getSharedPreferences(
				"caracas.ciudadania.segura", Context.MODE_PRIVATE);

		int t = 0;
		for (int i = 1; i <= 3; i++) {

			String tmp = "contacto" + i;
			if (prefs.contains(tmp)) {

				// Si el contacto no es vacio
				if (prefs.getString(tmp, "") != "") {
					t++;
				}
			}
		}

		return t;
	}

	// Limpia (elimina) todas las preferencias de la aplicacion.
	// Sirve para "reiniciar" la app
	public boolean limpiar(Context c) {

		SharedPreferences prefs = c.getSharedPreferences(
				"caracas.ciudadania.segura", Context.MODE_PRIVATE);

		Editor e = prefs.edit();
		e.clear();
		return e.commit();

	}
}
