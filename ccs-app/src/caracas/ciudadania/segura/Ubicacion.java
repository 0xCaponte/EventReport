/* Autor: Carlos Aponte -- 09-10041
 * 
 * Clase: Ubicacion
 * 
 * Descripcion: esta clase define el tipo ubicacion que es usado en la aplicacion. De la
 * misma forma se encarga de manejar todo lo relacionado con la localizacion, por ejemplo obtener
 * la posicion actual, mantenerla actualizada, e incluso usar la latitud y longitud
 * para obtener una direccion mediante reverse geocoding.
 */

package caracas.ciudadania.segura;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.view.View;

public class Ubicacion {

	Context appContext;
	LocationManager lm;
	Timer temporizador, temporizadorAct; // Timers de actualizaciones
	String proveedor; // Proveedor de la ubicacion (GPS, Red, Pasivo)
	Location ubicacionObtenida;
	View vista;

	/** -------- Constructor ----- */
	public Ubicacion(Context c) {
		appContext = c;

		// Ubicaicon por defecto en plaza Venezuela
		ubicacionObtenida = new Location(LocationManager.NETWORK_PROVIDER);
		ubicacionObtenida.setLatitude(10.496497);
		ubicacionObtenida.setLongitude(-66.885945);
		;
	}

	/** ------- Getters y Setters ------ */

	public void setUbicacion() {
		double lon, lat;
		ubicacionObtenida = getUbicacionActual();

		lon = ubicacionObtenida.getLongitude();
		lat = ubicacionObtenida.getLatitude();
		App.setLatitud(lat);
		App.setLongitud(lon);
	}

	// Actualiza la ubicacion del usuario cada cierto tiempo (25 seg)
	public void actualizarUbicacion(Context c) {

		double lon, lat;
		ubicacionObtenida = getUbicacionActual();

		lon = ubicacionObtenida.getLongitude();
		lat = ubicacionObtenida.getLatitude();
		App.setLatitud(lat);
		App.setLongitud(lon);

		temporizadorAct = new Timer();
		temporizadorAct.schedule(new actualizarUbicacion(), 25000);

	}

	// Listener que esta atento a los cambios en el proveedor de la ubicacion.
	private final LocationListener listener = new LocationListener() {

		@Override
		public void onLocationChanged(Location ubicacion) {
			// Si se le notifica que hubo cambios de ubicacion.
			temporizador.cancel();
			ubicacionObtenida = ubicacion;
			App.setLatitud(ubicacion.getLatitude());
			App.setLongitud(ubicacion.getLongitude());
			lm.removeUpdates(this);
			lm.removeUpdates(listener);
		}

		@Override
		public void onProviderDisabled(String provider) {
			// Si el proveedor esta apagado ejecuto esto
		}

		@Override
		public void onProviderEnabled(String provider) {
			// Si el proveedor esta encendido ejecuto esto
		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
			// Si hay cambios en el estado del proveedor ejecuto esto
		}
	};

	// Funcion que retorna la ubicacion actual del dispositivo, o en su defecto
	// la ultima ubicacion conocida.
	private Location getUbicacionActual() {

		// Criterio de decision -- Una buena precision
		Criteria crta = new Criteria();
		crta.setAccuracy(Criteria.ACCURACY_FINE);

		lm = (LocationManager) appContext
				.getSystemService(Context.LOCATION_SERVICE);

		// Retorna el mejor proveedor activo que concuerda con el criterio dado.
		proveedor = lm.getBestProvider(crta, true);

		lm.requestLocationUpdates(proveedor, 0, 0, listener);

		// Inicio la busqueda de la ubicacion
		temporizador = new Timer();
		temporizador.schedule(new getUltimaUbicacion(), 25000);

		// Mientras conseguimos una mejor ubicacion, usamos la ultima conocida
		ubicacionObtenida = lm.getLastKnownLocation(proveedor);

		// Si el mejor proveedor (gps) no tiene datos de ubicacion anteriores
		// usamos la red como nuevo proveedor
		if (ubicacionObtenida == null) {

			ubicacionObtenida = lm
					.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
			if (ubicacionObtenida == null) {

				// Se usa la forma pasiva como nuevo proveedor
				ubicacionObtenida = lm
						.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);

				if (ubicacionObtenida == null) {
					// Si todo falla, la ubicacion inicial sera en Plaza
					// Venezuela
					ubicacionObtenida = new Location(
							LocationManager.NETWORK_PROVIDER);
					ubicacionObtenida.setLatitude(10.496497);
					ubicacionObtenida.setLongitude(-66.885945);

				}
			}

		}

		return ubicacionObtenida;
	}

	// Funcion que se corre en caso de que se espere el time-out del proveedor.
	// En este caso se usa la ultima ubicacion conocida (Puede estar muy
	// desactualizada).
	private class getUltimaUbicacion extends TimerTask {

		@Override
		public void run() {

			// dejamos de escuchar a ese proveedor
			lm.removeUpdates(listener);
			Location ubicacion = lm.getLastKnownLocation(proveedor);
			ubicacionObtenida = ubicacion;
		}
	}

	private class actualizarUbicacion extends TimerTask {

		@Override
		public void run() {

			Looper.prepare();
			double lon, lat;

			ubicacionObtenida = getUbicacionActual();

			lon = ubicacionObtenida.getLongitude();
			lat = ubicacionObtenida.getLatitude();
			App.setLatitud(lat);
			App.setLongitud(lon);

			temporizadorAct = new Timer();
			temporizadorAct.schedule(new actualizarUbicacion(), 30000);

		}
	}

	// Fucnion que dada una latitud y una longitud es capaz de devolver una
	// direccion, Esto mediante la consulta web con la api de google maps.
	public String reverseGeoCoder(String lat, String lng) {

		// Pag que se va a consultar
		String url = "http://maps.google.com/maps/api/geocode/json?latlng="
				+ lat + "," + lng + "&sensor=true";
		URL u = null;
		try {
			u = new URL(url);
		} catch (MalformedURLException e) {
		
			e.printStackTrace();
		}

		if (u == null) {
			return "";
		}

		// Se hace la solicitud
		new GetDireccion().execute(u);

		return url;

	}

	// Funcion que realiza de forma asincrona la solicitud web, y que
	// posteriormente nos retorna la direccion dada por el api de google maps
	private class GetDireccion extends AsyncTask<URL, Integer, Long> {

		protected Long doInBackground(URL... urls) {

			// Datos de la solicitud
			HttpGet httpGet = new HttpGet(urls[0].toString());
			HttpClient client = new DefaultHttpClient();
			HttpResponse response;
			StringBuilder stringBuilder = new StringBuilder();

			try {
				//Se realiza la solicitud
				response = client.execute(httpGet);
				HttpEntity entity = response.getEntity();
				InputStream stream = entity.getContent();
				int b;
				//Se guarda la respuesta en un string
				while ((b = stream.read()) != -1) {
					stringBuilder.append((char) b);
				}
			} catch (ClientProtocolException e) {
			} catch (IOException e) {
			}

			// Se construye el JSONObject con los datos de la respuesta
			JSONObject jsonObject = new JSONObject();
			try {
				jsonObject = new JSONObject(stringBuilder.toString());
			} catch (JSONException e) {
				e.printStackTrace();
			}

			//Se busca la direccion en el JASONObject creado.
			JSONObject location;
			String location_string = "";
			
			try {
				location = jsonObject.getJSONArray("results").getJSONObject(0);
				location_string = location.getString("formatted_address");

			} catch (JSONException e1) {
				e1.printStackTrace();
			}

			return (long) 1;
		}
	}
}