/* Autor: Carlos Aponte -- 09-10041
 * 
 * Clase: ReporteIncidente
 * 
 * Descripcion: esta clase extiende a Activity, y se encarga de manejar la visualizacion del
 * formulario de reporte de incidentes. La vista puede llevarnos al mapa de reporte para la seleccion
 * de la ubicacion, si el usuario asi lo indica.
 */

package caracas.ciudadania.segura;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.Gravity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

public class ReporteIncidente extends Activity {

	ArrayList<Incidente> lista = new ArrayList<Incidente>();
	Context c = ReporteIncidente.this;

	// Se ejecuta la primeraz vez que se crea la vista
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.formulario_incidente);

		EditText editText = (EditText) findViewById(R.id.incidenteHora);

		DateFormat formato = new SimpleDateFormat("dd-MM-yyyy' -- 'HH:mm");
		Date date = new Date();
		editText.setText(formato.format(date), TextView.BufferType.EDITABLE);

		// Carga la configuracion del Spinner, los datos vienen de una lista
		// preestablecida
		Spinner spinner = (Spinner) findViewById(R.id.spinnerTipos);

		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
				this, R.array.incidente_tipos,
				android.R.layout.simple_spinner_dropdown_item);

		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(adapter);

		// On Click Emergencia -- Va a la vista del Boton de Panico
		Button btnPanico = (Button) findViewById(R.id.Panico);
		btnPanico.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				startActivity(new Intent("caracas.ciudadania.segura.Emergencia"));
			}
		});

		// On Click Siguiente -- Solicita confirmacion o nos lleva al mapa
		Button btnSiguiente = (Button) findViewById(R.id.btnSiguiente);
		btnSiguiente.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				// RadioGroup que solo permite un valor, actual o mapa.
				RadioButton ubicacion = (RadioButton) findViewById(R.id.incidenteUbicacionAct);

				EditText fecha = (EditText) findViewById(R.id.incidenteHora);

				// Lista de tipos incidentes
				Spinner spinner = (Spinner) findViewById(R.id.spinnerTipos);
				String tipo = spinner.getSelectedItem().toString();

				String ubi = (String) ubicacion.getText();
				String hora = fecha.getText().toString();

				DateFormat formato = new SimpleDateFormat(
						"dd-MM-yyyy' -- 'HH:mm");

				// Si la ubicacion a usar es la actual, reporta el incidente
				if (ubicacion.isChecked()) {

					String denunciante = App.getNroTlf();
					if (denunciante == "") {
						TelephonyManager tMgr = (TelephonyManager) c
								.getSystemService(Context.TELEPHONY_SERVICE);
						denunciante = tMgr.getLine1Number();
					}
					LatLng l = new LatLng(App.getLatitud(), App.getLongitud());
					Date h = new Date();

					try {

						h = formato.parse(hora);

					} catch (ParseException e) {
						e.printStackTrace();
					}

					final Incidente in = new Incidente(l, h, tipo, denunciante);
					final Context c = ReporteIncidente.this;
					final EditText input = new EditText(c);

					// Solicitar confirmacion
					AlertDialog.Builder builder = new AlertDialog.Builder(
							ReporteIncidente.this);
					builder.setTitle("Enviar Reporte");
					builder.setMessage("\n Tipo: " + tipo + "\n Fecha: " + hora
							+ "\n Ubicacion: " + ubi
							+ "\n\n Direccion Ip del Servidor:");

					builder.setView(input);

					builder.setPositiveButton("Enviar",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {

									boolean resultado = false;

									lista.add(in);

									sendIncidentes task = new sendIncidentes();
									String url = input.getText().toString();
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
											toast.setGravity(Gravity.CENTER, 0,
													0);
											toast.show();
											return;
										} else {
											// Realiza la solicitud de POST de
											// forma asincrona
											url = "http://"
													+ url
													+ ":8080/Server-CCS/Incidentes";
											task.execute(new String[] { url });

										}

									} else {
										// Si la direccion es invalida, lo
										// notifica
										Toast toast = Toast.makeText(c,
												"Direccion IP invalida",
												Toast.LENGTH_LONG);
										toast.setGravity(Gravity.CENTER, 0, 0);
										toast.show();
										return;

									}

									String mostrar = "Envio Exitoso";

									AlertDialog.Builder builder2 = new AlertDialog.Builder(
											ReporteIncidente.this);
									builder2.setTitle(mostrar)
											.setNegativeButton(
													"Continuar",
													new DialogInterface.OnClickListener() {
														public void onClick(
																DialogInterface dialog,
																int which) {
															// volver
															// al
															// formulario
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
											// //volver al formulario
										}
									})
							.setIcon(android.R.drawable.ic_dialog_alert).show();

				} else {
					// Vamos al mapa de reporte, al cual le pasamos mediante un
					// intent la
					// informacion de esta vista.
					String d = App.getNroTlf();
					if (d == "") {
						TelephonyManager tMgr = (TelephonyManager) c
								.getSystemService(Context.TELEPHONY_SERVICE);
						d = tMgr.getLine1Number();
					}

					// Creamos el intent al que vamos a ir
					Intent intent = new Intent(
							"caracas.ciudadania.segura.VerMapa");

					// Creamos un bundle de los datos a transmitir
					Bundle extras = new Bundle();
					extras.putString("denunciante", d);
					extras.putString("hora", hora);
					extras.putString("tipo", tipo);

					// Agregamos el bundle al intent
					intent.putExtras(extras);

					// Vamos a la actividad de seleccion en mapa
					startActivity(intent);
				}

			}
		});

		// On Click Cancelar -- Volvemos al menu principal
		Button btnCancelar = (Button) findViewById(R.id.btnCancelar);
		btnCancelar.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// Terminar reporte y volver al menu principal
				ReporteIncidente.this.finish();
			}
		});
	}

	// Funcion que se encarga de enviar la lista de incidentes al servidor
	// mediante un objeto JSOn y una solicitud de POST
	private class sendIncidentes extends AsyncTask<String, Void, String> {
		@Override
		protected String doInBackground(String... urls) {
			String output = null;
			for (String url : urls) {
				try {
					Cliente_Servidor cs = new Cliente_Servidor();
					Boolean resultado = cs.enviarIncidentes(url, lista);
					lista.clear();
					output = resultado.toString();

				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			return output;
		}
	}
}
