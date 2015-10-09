/* Autor: Carlos Aponte -- 09-10041
 * 
 * Clase: Cliente-Servidor
 * 
 * Descripcion: esta clase  se encarga de realizar la comunicacion con el servidor de la palicacion mediante
 * eticiones GET y POST. Encontramos metodos para solicitar la lista de noticias y de incidentes, asi como para
 * enviar nuevos incidentes.
 * 
 *  Aqui mismo conseguimos una clase privada, la cual se una como intermediario entre la 
 * clase incidente del servidor (no maneja LatLon) y la del cliente.
 * 
 *  */
package caracas.ciudadania.segura;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

public class Cliente_Servidor {

	/* Constructor */
	public Cliente_Servidor() {

	}

	// Clase intermediaria entre los incidentes del cliente y el servidor

	private class IncidenteTmp {

		Double lat;
		Double lon;
		Date hora;
		String tipo;
		String denunciante;

		public IncidenteTmp(Double lat2, Double lon2, Date fecha, String t,
				String denunciante2) {

			lat = lat2;
			lon = lon2;
			hora = fecha;
			tipo = t;
			denunciante = denunciante2;

		}
	}

	/* Metodos de comunicacion Cliente-Servidor */

	/* Solicita la lista de incidentes del servidor */
	public ArrayList<Incidente> getOutputFromUrl(String url) throws IOException {

		StringBuffer output = new StringBuffer("");
		InputStream stream = null;

		URL u = new URL(url);
		URLConnection connection = u.openConnection();

		// De ser posible abre una conexion con el servidor
		try {
			HttpURLConnection httpConnection = (HttpURLConnection) connection;
			httpConnection.setRequestMethod("GET");
			httpConnection.connect();

			if (httpConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
				stream = httpConnection.getInputStream();
			} else {
				return null;
			}

		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {

			e.printStackTrace();
			return null;
		}

		// Lee el stream de datos que envio el servidor
		BufferedReader buffer = new BufferedReader(
				new InputStreamReader(stream));

		String s = "";
		while ((s = buffer.readLine()) != null)
			output.append(s);

		// Vuelve el Stream un ArrayList de Incidentes basado en el tipo del
		// objeto
		String data = output.toString();

		Gson gson = new Gson();
		Type tipo = new TypeToken<ArrayList<IncidenteTmp>>() {
		}.getType();

		JsonElement json = new JsonParser().parse(data);

		// Transformo del incidente del servidor al incidente del cliente
		ArrayList<IncidenteTmp> l1 = gson.fromJson(json, tipo);
		ArrayList<Incidente> l2 = new ArrayList<Incidente>();

		Iterator<IncidenteTmp> it = l1.iterator(); // iterator

		while (it.hasNext()) {
			IncidenteTmp tmp = ((IncidenteTmp) it.next());

			LatLng lugar = new LatLng(tmp.lat, tmp.lon);
			String t = tmp.tipo;
			Date fecha = tmp.hora;

			String denunciante = "100000";
			DateFormat formato = new SimpleDateFormat("dd-MM-yyyy' -- 'HH:mm");
			Incidente i = new Incidente(lugar, fecha, t, denunciante);
			l2.add(i);
		}

		return l2;
	}

	/* Envia una lista de incidentes al servidor */
	public Boolean enviarIncidentes(String url, ArrayList<Incidente> info)
			throws IOException {

		InputStream stream = null;
		String result = "";

		// Transformo del incidente del cliente al incidente del servidor.
		ArrayList<IncidenteTmp> l2 = new ArrayList<IncidenteTmp>();

		Iterator<Incidente> it = info.iterator(); // iterator

		while (it.hasNext()) {
			Incidente tmp = ((Incidente) it.next());

			Double lat = tmp.getUbicacion().latitude;
			Double lon = tmp.getUbicacion().longitude;
			String t = tmp.tipo;
			Date fecha = tmp.hora;
			String denunciante = tmp.denunciante;
			DateFormat formato = new SimpleDateFormat("dd-MM-yyyy' -- 'HH:mm");
			IncidenteTmp i = new IncidenteTmp(lat, lon, fecha, t, denunciante);
			l2.add(i);
		}

		// De ser posible abre una conexion con el servidor y envia
		try {

			// Crea el cliente http y la solicitud de POST,
			HttpClient httpclient = new DefaultHttpClient();
			HttpPost httpPost = new HttpPost(url);

			// Transforma la lista de incidentes enu n objeto JSON
			String json = "";
			json = new Gson().toJson(l2);

			// Establece la entity y los header del POST
			StringEntity se = new StringEntity(json);
			httpPost.setEntity(se);
			httpPost.setHeader("Accept", "application/json");
			httpPost.setHeader("Content-type", "application/json");

			// Ejecuta la solicitud de POST y espera la respuesta
			HttpResponse httpResponse = httpclient.execute(httpPost);
			stream = httpResponse.getEntity().getContent();

			// Transforma el stream a in string
			if (stream != null) {
				BufferedReader bufferedReader = new BufferedReader(
						new InputStreamReader(stream));
				String linea = "";
				result = "";
				while ((linea = bufferedReader.readLine()) != null)
					result += linea;
			} else
				result = "Fail";

		} catch (Exception ex) {
			ex.printStackTrace();
		}

		if (result == "Fail") {
			return false;
		} else {
			return true;
		}

	}

	/* Solicita la lista de noticias del servidor */
	public ArrayList<Noticia> getNoticiasFromUrl(String url) throws IOException {

		StringBuffer output = new StringBuffer("");
		InputStream stream = null;

		URL u = new URL(url);
		URLConnection connection = u.openConnection();

		// De ser posible abre una conexion con el servidor
		try {

			HttpURLConnection httpConnection = (HttpURLConnection) connection;
			httpConnection.setRequestMethod("GET");
			httpConnection.connect();

			if (httpConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
				stream = httpConnection.getInputStream();
			} else {
				return null;
			}

		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {

			e.printStackTrace();
			return null;
		}

		// Lee el stream de datos que envio el servidor
		BufferedReader buffer = new BufferedReader(
				new InputStreamReader(stream));

		String s = "";
		while ((s = buffer.readLine()) != null)
			output.append(s);

		// Vuelve el Stream un elemento JSON con el formato adecuado para el
		// tipo noticia
		String data = output.toString();

		Gson gson = new Gson();
		Type tipo = new TypeToken<ArrayList<Noticia>>() {
		}.getType();

		JsonElement json = new JsonParser().parse(data);

		// Transforma ese json en un arraylist de noticias
		ArrayList<Noticia> l = gson.fromJson(json, tipo);

		return l;
	}

	//Metodo para verificar si el dispositivo tiene acceso a internet.
	public boolean isNetworkAvailable(Context c) {
		ConnectivityManager cm = (ConnectivityManager) c
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = cm.getActiveNetworkInfo();

		if (networkInfo != null && networkInfo.isConnected()) {
			return true;
		}

		return false;
	}

	//Metodo que usa una expresion regular para validar una direccion IP.
	public boolean validIP(String u) {

		String patronIP = "^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\."
				+ "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\."
				+ "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\."
				+ "([01]?\\d\\d?|2[0-4]\\d|25[0-5])$";

		Pattern patron = Pattern.compile(patronIP);
		Matcher matcher = patron.matcher(u);

		return matcher.matches();
	}
}
