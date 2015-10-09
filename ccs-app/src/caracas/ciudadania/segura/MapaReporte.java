/* Autor: Carlos Aponte -- 09-10041
 * 
 * Clase: MapaReporte
 * 
 * Descripcion: esta clase extiende a Activity, y se encarga de manejar la seleccion de la
 * ubicacion de un incidente a la hora de realizar un reporte. El mapa es obtenido mediante la libreria de Google Play Services,
 * y su uso basico se puede ver en https://developers.google.com/maps/documentation/android/start
 * */

package caracas.ciudadania.segura;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapaReporte extends FragmentActivity implements OnMapClickListener {

	private GoogleMap map;
	private Location ubicacion;
	private LatLng ubi = null;
	private String h, d, t;
	private Date hora;
	private Marker marker; // Marcador del lugar deseado.
	private ArrayList<Incidente> lista = new ArrayList<Incidente>();
	
	/**
	 * Se llama cuando se crea la actividad por primera vez. En esta actividad
	 * se pasa informacion desde el formulario de reporte.
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		setContentView(R.layout.mapa_reporte);

		// Se guarda el Intent con el que se paso la data
		Intent intent = getIntent();

		// Se obtiene la data que se paso
		h = intent.getStringExtra("hora");
		d = intent.getStringExtra("denunciante");
		t = intent.getStringExtra("tipo");

		DateFormat formato = new SimpleDateFormat("dd-MM-yyyy' -- 'HH:mm");

		
		try {
			hora = formato.parse(h);
		} catch (ParseException e) {

			e.printStackTrace();
		}

		// Creamos el mapa
		Context c = getApplicationContext();
		Ubicacion u = new Ubicacion(c);
		u.setUbicacion();

		map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map))
				.getMap();

		LatLng actual = new LatLng(App.getLatitud(), App.getLongitud());

		// Centramo el mapa en la ubicacion actual
		map.setMyLocationEnabled(true);
		map.moveCamera(CameraUpdateFactory.newLatLngZoom(actual, 16));
		map.setOnMapClickListener(this);

		// On Click Emergencia! -- Vamos a la vista de Boton de Panico
		Button btnPanico = (Button) findViewById(R.id.Panico);
		btnPanico.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				startActivity(new Intent("caracas.ciudadania.segura.Emergencia"));
			}
		});

		// On Click Cancelar -- Se cancela el reporte
		Button btnCancelar = (Button) findViewById(R.id.btnCancelar);
		btnCancelar.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				MapaReporte.this.finish();
			}
		});

		// On Click Siguiente -- Se envia el reporte
		Button btnSiguiente = (Button) findViewById(R.id.btnSiguiente);
		btnSiguiente.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				// Verificamos la seleccion del marcador
				if (ubi == null) {
					Toast toast = Toast.makeText(MapaReporte.this,
							"Debe Seleccionar Una Ubicacion en el Mapa",
							Toast.LENGTH_LONG);
					toast.setGravity(Gravity.CENTER, 0, 0);
					toast.show();
				} else {

					// Solicitar confirmacion
					final Incidente in = new Incidente(ubi, hora, t, d);
					final Context c = MapaReporte.this;

					AlertDialog.Builder builder = new AlertDialog.Builder(
							MapaReporte.this);
					builder.setTitle("Enviar Reporte");
					builder.setMessage(
									" Tipo: " + t + "\n  Fecha: " + h
											+ "\n Ubicacion: Marcador"
											+ "\n\n Direccion Ip del Servidor:");
					
					final EditText input = new EditText(c);
					builder.setView(input);
					
					builder.setPositiveButton("Enviar",
									new DialogInterface.OnClickListener() {
										public void onClick(
												DialogInterface dialog,
												int which) {

											lista.add(in);

											sendIncidentes task = new sendIncidentes();
											String url = input.getText()
													.toString();
											Cliente_Servidor cs = new Cliente_Servidor();

											if (cs.validIP(url)) {

												// Si no hay conexion, se
												// notifica
												if (!cs.isNetworkAvailable(c)) {
													Toast toast = Toast
															.makeText(
																	c,
																	"No Hay Conexion a Internet ",
																	Toast.LENGTH_LONG);
													toast.setGravity(
															Gravity.CENTER,
															0, 0);
													toast.show();
													return;
												} else {
													//Se ejecuta la peticion de POST de 
													// forma asincrona
													url = "http://"
															+ url
															+ ":8080/Server-CCS/Incidentes";
													task.execute(new String[] { url });

													// resultado =
												}

											} else {
												//Si la IP es invalida, notifica
												Toast toast = Toast
														.makeText(
																c,
																"Direccion IP invalida",
																Toast.LENGTH_LONG);
												toast.setGravity(
														Gravity.CENTER, 0,
														0);
												toast.show();
												return;
											}

											String mostrar = "Envio Exitoso";

											AlertDialog.Builder builder2 = new AlertDialog.Builder(
													MapaReporte.this);
											builder2.setTitle(mostrar)
													.setNegativeButton(
															"Volver",
															new DialogInterface.OnClickListener() {
																public void onClick(
																		DialogInterface dialog,
																		int which) {
																	MapaReporte.this
																			.finish();
																}
															})
													.setIcon(
															android.R.drawable.ic_dialog_alert)
													.show();

										}
									})
							.setNegativeButton(android.R.string.no,
									new DialogInterface.OnClickListener() {
										public void onClick(
												DialogInterface dialog,
												int which) {

											marker.remove();

											// //volver al formulario
										}
									})
							.setIcon(android.R.drawable.ic_dialog_alert).show();

				}
			}
		});

	}

	// Maneja lo que ocurre cuando se hace click en el mapa (selecciona el
	// marcador)
	@Override
	public void onMapClick(LatLng point) {

		ubi = point;

		// Si existia otro marcador anterior, se sustituye por el nuevo
		if (marker != null) {
			marker.remove();
		}

		marker = map.addMarker(new MarkerOptions().position(point).icon(
				BitmapDescriptorFactory
						.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
	}

	// Funcion que se ejecuta de forma asincrona y que se encarga de enviar la solicitud
	// de POST al servidor.
	private class sendIncidentes extends AsyncTask<String, Void, String> {
		@Override
		protected String doInBackground(String... urls) {
			String output = null;
			for (String url : urls) {
				try {
					Cliente_Servidor cs = new Cliente_Servidor();
					Boolean resultado = cs.enviarIncidentes(url, lista);
					output = resultado.toString();
					lista.clear();
					
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			return output;
		}
	}

}
