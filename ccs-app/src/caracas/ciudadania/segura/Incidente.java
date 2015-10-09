/* Autor: Carlos Aponte -- 09-10041
 * 
 * Clase: Incidente
 * 
 * Descripcion: esta clase define el tipo incidente que es usado en la aplicacion. De la
 * misma forma se encarga de manejar todo lo relacionado a los incidentes, por ejemplo el
 * reporte, la obtencion de la lista de incidentes, modificaicones, etc.
 *  */

package caracas.ciudadania.segura;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import android.content.Context;
import android.content.res.AssetManager;

import com.google.android.gms.maps.model.LatLng;

public class Incidente {

	LatLng ubicacion; // Ubicacion donde ocurrio el incidente.
	Date hora; // Fecha y hora del incidente.
	String tipo; // Tipo del incidente.
	String denunciante; // Quien es el usuario que lo reporta.

	/** ------- Constructores ------- */
	public Incidente() {
	}

	public Incidente(LatLng l, Date h, String t, String d) {
		ubicacion = l;
		hora = h;
		tipo = t;
		denunciante = d;
	}

	/** ------- Getters y Setters------- */
	public LatLng getUbicacion() {
		return ubicacion;
	}

	public void setUbicacion(LatLng ubicacion) {
		this.ubicacion = ubicacion;
	}

	public Date getHora() {
		return hora;
	}

	public void setHora(Date hora) {
		this.hora = hora;
	}

	public String getTipo() {
		return tipo;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
	}

	public String getDenunciante() {
		return denunciante;
	}

	public void setDenunciante(String denunciante) {
		this.denunciante = denunciante;
	}

	/** ------- Funciones Especificas ------- */

	// Funcion que elimina todos los incidentes guardados previamente
	public boolean limpiarLista(Context c) throws IOException {

		String archivo = App.getArchivoIncidentes();
		// Lista sin incidentes
		String data = "<lista-incidentes> \n </lista-incidentes>";

		// Se abre el archivo y se elimina el contenido
		try {
			FileOutputStream fos = c.openFileOutput(archivo,
					Context.MODE_PRIVATE);
			fos.write(data.getBytes());
			fos.close();
			return true;

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
		}

		return false;
	}

	// Funcion que retorna todos los incidentes que se tienen almacenados
	public ArrayList<Incidente> getListaIncidentes(Context c, String caso) {

		ArrayList<Incidente> lista = new ArrayList<Incidente>();

		try {

			String archivo = App.getArchivoIncidentes();
			InputStream in;

			if (caso == "Ejemplo") {
				AssetManager asset = c.getAssets();
				in = asset.open("incidentes_ejemplo.xml");

			} else {

				// Cargamos el contenido actual
				in = c.openFileInput(archivo);
			}

			// Se parsea el documento XML
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document documento = db.parse(in);
			documento.getDocumentElement().normalize();

			// Se desgloza el XML en funcion del tag incidente
			NodeList nodos = documento.getElementsByTagName("incidente");

			for (int i = 0; i < nodos.getLength(); i++) {

				Node nodo = nodos.item(i);

				// Para cada incidente se le obtienen y almacenan sus datos
				if (nodo.getNodeType() == Node.ELEMENT_NODE) {

					double lat = Double.parseDouble(obtenerValor("latitud",
							(Element) nodo));
					double lon = Double.parseDouble(obtenerValor("longitud",
							(Element) nodo));
					String t = obtenerValor("tipo", (Element) nodo);
					String hora = obtenerValor("hora", (Element) nodo);
					String d = obtenerValor("denunciante", (Element) nodo);

					LatLng l = new LatLng(lat, lon);

					// ----> cambiar por la hora parseada....
					DateFormat formato = new SimpleDateFormat(
							"dd-MM-yyyy' -- 'HH:mm");
					Date h = formato.parse(hora);
					Incidente inc = new Incidente(l, h, t, d);
					lista.add(inc);
				}
			}

			in.close();

		} catch (Exception e) {
			e.printStackTrace();
		}

		return lista;
	}

	// Funcion que devuelve el valor asociado a una etiqueta dada
	private static String obtenerValor(String etiqueta, Element elemento) {
		return elemento.getElementsByTagName(etiqueta).item(0).getChildNodes()
				.item(0).getNodeValue();
	}

	// Funcion que reporta un incidente al servidor y actualiza el archivo local
	public boolean reportarIncidente(Incidente i, Context c) throws IOException {

		String archivo = App.getArchivoIncidentes();
		Date hora = i.getHora();
		DateFormat formato = new SimpleDateFormat("dd-MM-yyyy' -- 'HH:mm");
		String tiempo = formato.format(hora);

		// Se carga el incidente
		String data = "\n\t<incidente>" + "\n\t\t<latitud>"
				+ i.getUbicacion().latitude + "</latitud>" + "\n\t\t<longitud>"
				+ i.getUbicacion().longitude + "</longitud>" + "\n\t\t<hora>"
				+ tiempo + "</hora>" + "\n\t\t<tipo>" + i.getTipo() + "</tipo>"
				+ "\n\t\t<denunciante>" + i.getDenunciante() + "</denunciante>"
				+ "\n\t</incidente>" + "\n</lista-incidentes>";

		// Se lee todo el archivo, y se le anexa el nuevo incidente al final.
		try {

			// Cargamos el contenido actual
			FileInputStream fis = c.openFileInput(archivo);
			BufferedReader buffer = new BufferedReader(new InputStreamReader(
					fis));

			StringBuilder sb = new StringBuilder();
			String linea, info, new_info;
			new_info = "";

			while ((linea = buffer.readLine()) != null) {
				sb.append(linea);
			}

			fis.close();

			// Agregamos el nuevo incidente
			info = sb.toString();

			if (info == "") {
				new_info = "<lista-incidentes>" + data;
			} else {

				if (info.contains("</lista-incidentes>")) {
					new_info = info.replace("</lista-incidentes>", data);

				}
			}

			// Guardamos la nueva lista
			FileOutputStream fos = c.openFileOutput(archivo,
					Context.MODE_PRIVATE);
			fos.write(new_info.getBytes());
			fos.close();
			return true;

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
		}

		return false;
	}

	//Funcion que actualiza el archivo de incidentes con aquellos dontenidos en la lista l
	public boolean actualizarIncidentes(ArrayList<Incidente> l, Context c)
			throws IOException {

		String archivo = App.getArchivoIncidentes();
		String data = "<lista-incidentes>";

		Iterator<Incidente> it = l.iterator(); // iterator
		while (it.hasNext()) {

			Incidente i = ((Incidente) it.next());
			DateFormat formato = new SimpleDateFormat(
					"dd-MM-yyyy' -- 'HH:mm");
			String h = formato.format(i.getHora());
			
			// Se carga el incidente
			data = data + "\n\t<incidente>" + "\n\t\t<latitud>"
					+ i.getUbicacion().latitude + "</latitud>"
					+ "\n\t\t<longitud>" + i.getUbicacion().longitude
					+ "</longitud>" + "\n\t\t<hora>" + h + "</hora>"
					+ "\n\t\t<tipo>" + i.getTipo() + "</tipo>"
					+ "\n\t\t<denunciante>" + i.getDenunciante()
					+ "</denunciante>" + "\n\t</incidente>";
		}

		data = data + "\n</lista-incidentes>";

		try {

			// Guardamos la nueva lista
			FileOutputStream fos = c.openFileOutput(archivo,
					Context.MODE_PRIVATE);
			fos.write(data.getBytes());
			fos.close();

			return true;

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
		}

		return false;
	}

	/* Funcion que guarda un incidente en el archivo */
	public boolean almacenarIncidente(Incidente i, Context c)
			throws IOException {

		String var;
		FileOutputStream fOut = c.openFileOutput("lista_incidentes",
				Context.MODE_APPEND);

		var = "\t<incidente>\n";
		fOut.write(var.getBytes());
		var = "\t\t<latitud>" + String.valueOf(i.getUbicacion().latitude)
				+ "</latitud>\n";
		fOut.write(var.getBytes());
		var = "\t\t<longitud>" + String.valueOf(i.getUbicacion().longitude)
				+ "</longitud>\n";
		fOut.write(var.getBytes());
		var = "\t\t<hora>" + String.valueOf(i.getHora()) + "</hora>\n";
		fOut.write(var.getBytes());
		var = "\t\t<tipo>" + i.getTipo() + "</tipo>\n";
		fOut.write(var.getBytes());
		var = "\t\t<denunciante>" + i.getDenunciante() + "</denunciante>\n";
		fOut.write(var.getBytes());
		var = "\t</incidente>\n";
		fOut.write(var.getBytes());

		fOut.close();

		return true;
	}
}
