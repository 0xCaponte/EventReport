/* Autor: Carlos Aponte -- 09-10041
 * 
 * Clase: MapaIncidentes
 * 
 * Descripcion: esta clase extiende a Activity, y se encarga de manejar la visualizacion de
 * los incidentes en el mapa. El mapa es obtenido mediante la libreria de Google Play Services,
 * y su uso basico se puede ver en https://developers.google.com/maps/documentation/android/start
 * */
package caracas.ciudadania.segura;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapaIncidentes extends Activity {

	private GoogleMap map; // Mapa

	private Context con = MapaIncidentes.this;

	// Lista de incidentes reportados
	private ArrayList<Incidente> lista = new ArrayList<Incidente>();;

	// Lista de incidentes ejemplo
	private ArrayList<Incidente> lista2 = new ArrayList<Incidente>();;

	// Se llama cuando se crea la actividad por primera vez
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.mapa_incidentes);

		// Se obtienen los datos de la ubicacion del usuario.
		Context c = getApplicationContext();
		Ubicacion u = new Ubicacion(c);
		u.setUbicacion();
		LatLng actual = new LatLng(App.getLatitud(), App.getLongitud());

		// Se obtiene el fragmento de mapa
		map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map))
				.getMap();

		// Centra el mapa en la posicion actual del usuario.
		map.setMyLocationEnabled(true);
		map.moveCamera(CameraUpdateFactory.newLatLngZoom(actual, 14));

		// Carga los marcadores de incidentes reportados
		Incidente i = new Incidente();
		lista = i.getListaIncidentes(c, "archivo");
		addMarcadores(map, lista);

		// On Click Emergencia! -- Va a la vista del boton de Panico
		Button btnPanico = (Button) findViewById(R.id.Panico);
		btnPanico.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				startActivity(new Intent("caracas.ciudadania.segura.Emergencia"));
			}
		});

		// On Click Limpiar -- Borra todos los incidentes almacenados.
		Button btnLimpiar = (Button) findViewById(R.id.btnLimpiar);
		btnLimpiar.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				final Context c = MapaIncidentes.this;

				// Solicitar confirmacion
				AlertDialog.Builder builder = new AlertDialog.Builder(c);
				builder.setTitle("Borrar Lista de Incidentes?")
						.setPositiveButton("Si",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int which) {

										Incidente i = new Incidente();
										Boolean resultado = false;

										try {
											resultado = i.limpiarLista(c);
											lista.clear();
										} catch (IOException e) {
											e.printStackTrace();
										}

										String mostrar = "Limpieza";

										if (resultado) {

											mostrar = mostrar + " Exitosa";
											map.clear();
										} else {

											mostrar = mostrar + " Fallida";
										}

										AlertDialog.Builder builder2 = new AlertDialog.Builder(
												c);
										builder2.setTitle(mostrar)
												.setNegativeButton(
														"Continuar",
														new DialogInterface.OnClickListener() {
															public void onClick(
																	DialogInterface dialog,
																	int which) {
															}
														})
												.setIcon(
														android.R.drawable.ic_dialog_alert)
												.show();

									}
								})
						.setNegativeButton(android.R.string.no,
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int which) {
										// volver al Mapa
									}
								}).setIcon(android.R.drawable.ic_dialog_alert)
						.show();

			}
		});

		// On Toggle Ejemplos -- Muestra u Oculta los incidentes de ejemplo
		final ToggleButton btnEjemplos = (ToggleButton) findViewById(R.id.toggleEjemplos);
		btnEjemplos.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				if (btnEjemplos.isChecked()) {
					Incidente i = new Incidente();
					lista2 = i.getListaIncidentes(MapaIncidentes.this,
							"Ejemplo");
					addMarcadores(map, lista2);
				} else {
					map.clear();
					addMarcadores(map, lista);
				}
			}
		});

		// On Click Actualizar -- Solicitamos al servidor la lista actual de incidentes
		final Button btnActualizar = (Button) findViewById(R.id.btnActualizar);
		btnActualizar.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				// Un PopUp nos solicita que servidor usaremos.
				AlertDialog.Builder popup = new AlertDialog.Builder(con);

				popup.setTitle("Especifique Servidor:");
				popup.setMessage("IP del Servidor:");

				final EditText input = new EditText(con);
				popup.setView(input);

				popup.setPositiveButton("Ok",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int whichButton) {

								GetIncidentes task = new GetIncidentes();
								String url = input.getText().toString();
								Cliente_Servidor cs = new Cliente_Servidor();

								if (cs.validIP(url)) {

									// Si no hay conexion, se notifica
									if (!cs.isNetworkAvailable(con)) {
										Toast toast = Toast.makeText(con,
												"No Hay Conexion a Internet ",
												Toast.LENGTH_LONG);
										toast.setGravity(Gravity.CENTER, 0, 0);
										toast.show();
									} else {

										//Ejecutamos la peticion en forma asincrona
										url = "http://" + url
												+ ":8080/Server-CCS/Incidentes";
										task.execute(new String[] { url });
									}

								} else {
									
									//Si la IP no es valida se notifica
									Toast toast = Toast.makeText(con,
											"Direccion IP invalida",
											Toast.LENGTH_LONG);
									toast.setGravity(Gravity.CENTER, 0, 0);
									toast.show();
								}
							}
						});

				popup.setNegativeButton("Cancelar",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int whichButton) {
								// Volver al mapa.
							}
						});

				popup.show();

			}
		});
	}

	// Funcion asincrona que solicita la lista de incidentes al servidor.
	private class GetIncidentes extends AsyncTask<String, Void, String> {
		@Override
		protected String doInBackground(String... urls) {
			String output = "false";
			for (String url : urls) {
				try {

					Cliente_Servidor c = new Cliente_Servidor();
					ArrayList<Incidente> l = c.getOutputFromUrl(url);
					output = "true";

					// Guardo en el archivo los nuevos incidentes, siempre se toma la
					// version del servidor como la mas actual.
					Incidente tmp = new Incidente();
					tmp.actualizarIncidentes(l, con);

					// Actualizamos el mapa recargando la actividad
					finish();
					startActivity(getIntent());

				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			return output;
		}

	}

	// Funcion que agrega marcadores de una lista de incidentes al mapa
	public void addMarcadores(GoogleMap m, ArrayList<Incidente> l) {

		Iterator<Incidente> it = l.iterator(); // iterator
		while (it.hasNext()) {

			Incidente tmp = ((Incidente) it.next());
			LatLng lugar = tmp.getUbicacion();
			String tipo = tmp.getTipo();
			String titulo = "";
			Date fecha = tmp.getHora();
			DateFormat formato = new SimpleDateFormat("dd-MM-yyyy' -- 'HH:mm");

			if (titulo == "") {
				titulo = tipo + "\n" + formato.format(fecha);
			}

			// Color del marcador depende del tipo del incidente
			float color;

			if (tipo.equals("Robo")) {
				color = BitmapDescriptorFactory.HUE_ORANGE;
			} else if (tipo.equals("Secuestro")) {
				color = BitmapDescriptorFactory.HUE_BLUE;
			} else if (tipo.equals("Pelea")) {
				color = BitmapDescriptorFactory.HUE_RED;
			} else if (tipo.equals("Tiroteo")) {
				color = BitmapDescriptorFactory.HUE_GREEN;
			} else if (tipo.equals("Vandalismo")) {
				color = BitmapDescriptorFactory.HUE_VIOLET;
			} else if (tipo.equals("Arresto")) {
				color = BitmapDescriptorFactory.HUE_AZURE;
			} else if (tipo.equals("Asesinato")) {
				color = BitmapDescriptorFactory.HUE_MAGENTA;
			} else if (tipo.equals("Apuñalamiento")) {
				color = BitmapDescriptorFactory.HUE_YELLOW;
			} else if (tipo.equals("Ejemplo")) {
				color = BitmapDescriptorFactory.HUE_CYAN;
				titulo = "Incidente de Ejemplo";
			} else {
				color = BitmapDescriptorFactory.HUE_ROSE;
			}

			// Agrego el marcador
			m.addMarker(new MarkerOptions().position(lugar).title(titulo)
					.icon(BitmapDescriptorFactory.defaultMarker(color)));

		}
	}
}
