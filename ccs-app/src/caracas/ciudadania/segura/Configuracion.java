/* Autor: Carlos Aponte -- 09-10041
 * 
 * Clase: Configuracion
 * 
 * Descripcion: esta clase extiende a Activity, por lo tanto es usada para manejar lo que se ve y 
 * lo que ocurre en la vista de configuracion del boton de panico.
 */

package caracas.ciudadania.segura;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class Configuracion extends Activity {

	// Lista que contiene a los contactos del usuario.
	private ArrayList<String> l = new ArrayList<String>();

	// Arreglo para modificar contactos.
	private String[] nuevos = new String[6];

	// Cursor que permite acceder a los datos del nombre de los contactos del
	// dispositivo.
	private Cursor cur;

	// Cursor que permite acceder a los datos del nro de
	// los contactos del dispositivo.
	private Cursor cur2;

	//Lista de campos de texto de la vista.
	ArrayList<EditText> c = new ArrayList<EditText>();


	//Se llama cuando se crea la actividad por primera vez
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.configuracion);

		// Cargo el mensaje y los contactos de emergencia.
		Preferencias p = new Preferencias();
		String m = p.getMensaje(Configuracion.this);

		l = p.getContactos(Configuracion.this);
		l.removeAll(Collections.singleton(null));

		c.add((EditText) findViewById(R.id.textoEmergencia));
		c.add((EditText) findViewById(R.id.contacto1));
		c.add((EditText) findViewById(R.id.contacto2));
		c.add((EditText) findViewById(R.id.contacto3));

		if (m.isEmpty()) {
			c.get(0).setText("Emergencia!!", TextView.BufferType.EDITABLE);
		} else {
			c.get(0).setText(m);
		}

		c.get(1).setText("Agregar Contacto", TextView.BufferType.EDITABLE);
		c.get(2).setText("Agregar Contacto", TextView.BufferType.EDITABLE);
		c.get(3).setText("Agregar Contacto", TextView.BufferType.EDITABLE);

		/**
		 * Si la lista no esta vacia (es la primera vez), se cargan los datos a
		 * la vista
		 */
		if (!l.isEmpty()) {
			int j = 1;
			for (int i = 0; i < l.size(); i = i + 2) {

				if (l.get(i) == null) {
					continue;
				}

				String txt = l.get(i) + "\n" + l.get(i + 1);
				c.get(j).setText(txt, TextView.BufferType.EDITABLE);
				nuevos[i] = l.get(i);
				nuevos[i + 1] = l.get(i + 1);

				j++;

			}
		}

		// Agregar el Contacto 1
		Button add1 = (Button) findViewById(R.id.add1);
		add1.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				// Acceder a lista de contactos
				Intent intent = new Intent(Intent.ACTION_PICK,
						ContactsContract.Contacts.CONTENT_URI);
				startActivityForResult(intent, 1);

			}
		});

		// Agregar el Contacto 2
		Button add2 = (Button) findViewById(R.id.add2);
		add2.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				// Acceder a lista de contactos
				Intent intent = new Intent(Intent.ACTION_PICK,
						ContactsContract.Contacts.CONTENT_URI);
				startActivityForResult(intent, 2);

			}
		});

		// Agregar el Contacto 3
		Button add3 = (Button) findViewById(R.id.add3);
		add3.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// Acceder a lista de contactos
				Intent intent = new Intent(Intent.ACTION_PICK,
						ContactsContract.Contacts.CONTENT_URI);
				startActivityForResult(intent, 3);

			}
		});

		// Borrar el Contacto 1
		Button del1 = (Button) findViewById(R.id.del1);
		del1.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				// Sacar contacto de la lista y dejar el nombre en "Agregar
				// Contacto" 
				nuevos[0] = null;
				nuevos[1] = null;

				c.get(1).setText("Agregar Contacto",
						TextView.BufferType.EDITABLE);

			}
		});

		// Borrar el Contacto 2
		Button del2 = (Button) findViewById(R.id.del2);
		del2.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				// Sacar contacto de la lista y dejar el nombre en "Agregar
				// Contacto"
				nuevos[2] = null;
				nuevos[3] = null;

				c.get(2).setText("Agregar Contacto",
						TextView.BufferType.EDITABLE);

			}
		});

		// Borrar el Contacto 3
		Button del3 = (Button) findViewById(R.id.del3);
		del3.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				// Sacar contacto de la lista y dejar el nombre en "Agregar
				// Contacto"
				nuevos[4] = null;
				nuevos[5] = null;

				c.get(3).setText("Agregar Contacto",
						TextView.BufferType.EDITABLE);

			}
		});

		// On Click Cancelar
		Button cancelar = (Button) findViewById(R.id.btnCancelar2);
		cancelar.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				Preferencias p = new Preferencias();

				// Revisamos Datos de Emergencias no vacios
				if ((p.sizeContactos(Configuracion.this) == 0)
						| p.mensajeEmpty(Configuracion.this)) {

					Toast toast = Toast.makeText(Configuracion.this,
							"Debe tener al menos un contacto para emergencias",
							Toast.LENGTH_LONG);
					toast.setGravity(Gravity.CENTER, 0, 0);
					toast.show();

				} else {

					startActivity(new Intent(
							"caracas.ciudadania.segura.CLEARSCREEN"));
				}
			}
		});

		// On Click Guardar
		// Guardamos los cambios en la configuracion de la App
		Button btnGuardar = (Button) findViewById(R.id.btnLimpiar);
		btnGuardar.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				final Context c = Configuracion.this;

				if (vacio()) {
					Toast toast = Toast.makeText(Configuracion.this,
							"Debe tener al menos un contacto para emergencias",
							Toast.LENGTH_LONG);
					toast.setGravity(Gravity.CENTER, 0, 0);
					toast.show();

				} else {
					
					// Solicitar confirmacion
					AlertDialog.Builder builder = new AlertDialog.Builder(c);
					builder.setTitle("Guardar Cambios?")
							.setPositiveButton("Si",
									new DialogInterface.OnClickListener() {
										public void onClick(
												DialogInterface dialog,
												int which) {

											EditText texto = (EditText) findViewById(R.id.textoEmergencia);
											String m = texto.getText()
													.toString();

											if (m.isEmpty()) {
												texto.setText(
														"Emergencia!!\n Tlf: "
																+ App.getNroTlf(),
														TextView.BufferType.EDITABLE);
											}
											// Guardar cambios de Mensaje
											Preferencias p = new Preferencias();
											Boolean resultado = p.setMensaje(c,
													m);
											App.setMensaje(m);

											// Guardar cambios de Contactos
											ArrayList<String> n = new ArrayList<String>(
													Arrays.asList(nuevos));

											resultado = p.setContactos(c, n);
											String mostrar = "Guardado";

											if (resultado) {

												mostrar = mostrar + " Exitoso";
											} else {

												mostrar = mostrar + " Fallido";
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
										public void onClick(
												DialogInterface dialog,
												int which) {
											// //volver al formulario
										}
									})
							.setIcon(android.R.drawable.ic_dialog_alert).show();
				}
			}
		});

	}

	//Lo que se realiza al cerrar la actividad
	protected void onDestroy() {
		super.onDestroy();

		if (cur != null) {
			cur.close();
		}

		if (cur2 != null) {
			cur2.close();
		}
	}

	//Funcion para revisar si los contactos estan vacios
	public boolean vacio() {

		for (int i = 0; i < nuevos.length; i++) {

			if (nuevos[i] != null) {
				return false;
			}
		}

		return true;
	}
	
	/** Lo que se va a realizar cuando se termine de ejecutar la actividad de
		seleccionar contacto*/
	@SuppressWarnings("deprecation")
	@Override
	public void onActivityResult(int reqCode, int resultCode, Intent data) {
		super.onActivityResult(reqCode, resultCode, data);

		if (resultCode == Activity.RESULT_OK) {
			Uri contactData = data.getData();
			cur = managedQuery(contactData, null, null, null, null);

			if (cur.moveToFirst()) {

				String nombre = cur
						.getString(cur
								.getColumnIndexOrThrow(ContactsContract.Contacts.DISPLAY_NAME));
				String id = cur.getString(cur
						.getColumnIndexOrThrow(ContactsContract.Contacts._ID));

				String num = "";

				if (Integer
						.parseInt(cur.getString(cur
								.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {

					cur2 = getContentResolver().query(
							ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
							null,
							ContactsContract.CommonDataKinds.Phone.CONTACT_ID
									+ " = " + id, null, null);

					if (cur2.moveToFirst()) {

						num = cur2
								.getString(cur2
										.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
					}

					// Si hay cambios en los contactos, lo sustituyo
					if (!Arrays.asList(nuevos).contains(nombre)) {

						c.get(reqCode).setText(nombre + "\n" + num);
						int pos = (reqCode - 1) * 2;
						nuevos[pos] = nombre;
						nuevos[pos + 1] = num;

					} else {
						Toast toast = Toast.makeText(Configuracion.this,
								"El Contacto Ya Existe", Toast.LENGTH_LONG);
						toast.setGravity(Gravity.CENTER, 0, 0);
						toast.show();
					}

				} else {
					c.get(reqCode).setText(nombre + " Sin Numero");
				}

			}

		}

	}
	
	

}