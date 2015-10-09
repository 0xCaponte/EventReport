/* Autor: Carlos Aponte -- 09-10041
 * 
 * Clase: MostrarNoticias
 * 
 * Descripcion: esta clase extiende a Activity, y se encarga de mostrar una vista que contiene la lista
 * de noticias de interes que tiene actualmente la aplicacion. En caso de solicitarlo, el usuario puede
 * actualizar la lista con las que tiene el servidor.
 * 
 */
package caracas.ciudadania.segura;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MostrarNoticias extends Activity {

	private Context con = MostrarNoticias.this; // Contexto de la actividad.
	private ArrayList<Button> botones = new ArrayList<Button>(4); // Lista de
																	// botones
																	// de
																	// noticias.
	private String[] links = new String[4]; // Arreglo de los links a las
											// noticias.

	// Se llama cuando se crea la actividad por primera vez
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.noticias);

		// Cargamos los botones a la lista
		botones.add((Button) findViewById(R.id.noticia1));
		botones.add((Button) findViewById(R.id.noticia2));
		botones.add((Button) findViewById(R.id.noticia3));
		botones.add((Button) findViewById(R.id.noticia4));

		//Inicialmente carga las noticias que estan guardadas en el dispositivo.
		cargarNoticias(con);

		// On Click Actualizar -- Solicitamos al servidor la lista actual de
		// noticias
		Button btnActualizar = (Button) findViewById(R.id.btnActualizar);
		btnActualizar.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				actualizarNoticias();
			}

		});

		// On Click Noticia1 -- cargamos la noticia asociada a ese titulo/boton.
		Button noticia1 = (Button) findViewById(R.id.noticia1);
		noticia1.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				if (links[0] != null) {
					// Si hay noticia asociada
					Intent intent = new Intent(Intent.ACTION_VIEW).setData(Uri
							.parse(links[0]));
					startActivity(intent);
				} else {
					// Si no hay noticia asociada se notifica
					Toast toast = Toast.makeText(con, "No Hay Notica",
							Toast.LENGTH_LONG);
					toast.setGravity(Gravity.CENTER, 0, 0);
					toast.show();
				}

			}

		});

		// On Click Noticia2 -- cargamos la noticia asociada a ese titulo/boton.
		Button noticia2 = (Button) findViewById(R.id.noticia2);
		noticia2.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				if (links[1] != null) {
					Intent intent = new Intent(Intent.ACTION_VIEW).setData(Uri
							.parse(links[1]));
					startActivity(intent);
				} else {
					//si no hay noticia asociada se notifica
					Toast toast = Toast.makeText(con, "No Hay Notica",
							Toast.LENGTH_LONG);
					toast.setGravity(Gravity.CENTER, 0, 0);
					toast.show();
				}

			}

		});

		// On Click Noticia3 -- cargamos la noticia asociada a ese titulo/boton.

		Button noticia3 = (Button) findViewById(R.id.noticia3);
		noticia3.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				if (links[2] != null) {
					Intent intent = new Intent(Intent.ACTION_VIEW).setData(Uri
							.parse(links[2]));
					startActivity(intent);
				} else {
					//si no hay noticia asociada se notifica
					Toast toast = Toast.makeText(con, "No Hay Notica",
							Toast.LENGTH_LONG);
					toast.setGravity(Gravity.CENTER, 0, 0);
					toast.show();
				}

			}

		});

		// On Click Noticia4 -- cargamos la noticia asociada a ese titulo/boton.
		Button noticia4 = (Button) findViewById(R.id.noticia4);
		noticia4.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				if (links[3] != null) {
					Intent intent = new Intent(Intent.ACTION_VIEW).setData(Uri
							.parse(links[3]));
					startActivity(intent);
				} else {
					//si no hay noticia asociada se notifica
					Toast toast = Toast.makeText(con, "No Hay Notica",
							Toast.LENGTH_LONG);
					toast.setGravity(Gravity.CENTER, 0, 0);
					toast.show();
				}

			}

		});

		// On Click Emergencia! -- Va a la vista de Boton de Panico
		Button btnPanico = (Button) findViewById(R.id.Panico);
		btnPanico.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				startActivity(new Intent("caracas.ciudadania.segura.Emergencia"));
			}
		});

	}

	// Funcion que se encarga de solicitar las noticias y cargarlas a la vista
	public void actualizarNoticias() {

		// Un PopUp nos solicita la direccion IP del servidor usaremos.
		AlertDialog.Builder popup = new AlertDialog.Builder(con);

		popup.setTitle("Especifique IP del Servidor");

		final EditText input = new EditText(con);
		popup.setView(input);

		popup.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {

				GetNoticias task = new GetNoticias();
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
						return;
					} else {
						//Solicitamos las noticias de forma asincrona
						url = "http://" + url + ":8080/Server-CCS/Noticias";
						task.execute(new String[] { url });
					}

				} else {
					//Si la direccion IP es invalida, se notifica
					Toast toast = Toast.makeText(con, "Direccion IP invalida",
							Toast.LENGTH_LONG);
					toast.setGravity(Gravity.CENTER, 0, 0);
					toast.show();
					return;
				}
			}
		});

		popup.setNegativeButton("Cancelar",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
					}
				});

		popup.show();

	}

	//Funcion asincrona que se encarga de solicitar las noticias al servidor.
	private class GetNoticias extends AsyncTask<String, Void, String> {
		@Override
		protected String doInBackground(String... urls) {
			String output = "false";
			for (String url : urls) {
				try {

					Cliente_Servidor c = new Cliente_Servidor();
					ArrayList<Noticia> l = c.getNoticiasFromUrl(url);

					// GUARDAR las noticias en las preferencias del usuario
					Preferencias p = new Preferencias();

					Boolean resultado = p.setNoticias(con, l);

					output = resultado.toString();

					// Actualizamos la lista recargando la actividad
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

	//Funcion que revisa las noticias almacenadas en el dispositivo y las carga a la vista
	public void cargarNoticias(Context c) {

		Preferencias p = new Preferencias();
		ArrayList<Noticia> l = p.getNoticias(c);
		l.removeAll(Collections.singleton(null));

		Iterator<Noticia> it = l.iterator(); // iterator
		int i = 0;
		while (it.hasNext()) {

			Noticia n = ((Noticia) it.next());

			if ((n != null) & (n.link != null) & (n.titulo != null)) {

				botones.get(i).setText(n.getTitulo());
				links[i] = n.getLink();
			} else {
				links[i] = null;
			}

			i++;
		}
	}
}
