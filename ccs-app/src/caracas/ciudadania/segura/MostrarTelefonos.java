/* Autor: Carlos Aponte -- 09-10041
 * 
 * Clase: MostrarTelefonos
 * 
 * Descripcion: esta clase extiende a Activity, y se encarga de mostrar una vista que contiene un acceso
 * rapido a los telefonos de emergencia como lo son el 171, la policia, los bomberos y los numeros de
 *  emergencia de la operadora.
 */

package caracas.ciudadania.segura;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.Button;

public class MostrarTelefonos extends Activity {

	// Se llama cuando se crea la actividad por primera vez.
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.lista_telefonos);

		// On Click Emergencia! -- Va a la vista de Boton de Panico
		Button btnPanico = (Button) findViewById(R.id.Panico);
		btnPanico.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				startActivity(new Intent("caracas.ciudadania.segura.Emergencia"));
			}
		});

		// On Click Policia Nacional
		// Llamar a la Policia Nacional en Caracas
		Button pnb = (Button) findViewById(R.id.policiaNacional);
		pnb.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent callIntent = new Intent(Intent.ACTION_CALL);
				callIntent.setData(Uri.parse("tel:08007654622"));
				startActivity(callIntent);
			}
		});

		// On Click Bomberos
		// Llamar a los Bomberos Metropolitanos
		Button bomberos = (Button) findViewById(R.id.bomberos);
		bomberos.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent callIntent = new Intent(Intent.ACTION_CALL);
				callIntent.setData(Uri.parse("tel:02125422512"));
				startActivity(callIntent);
			}
		});

		// On Click 171 Emergencia -- Llamar al 171
		Button e171 = (Button) findViewById(R.id.emergencias171);
		e171.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent callIntent = new Intent(Intent.ACTION_CALL);
				callIntent.setData(Uri.parse("tel:171"));
				startActivity(callIntent);
			}
		});

		// On Click EmergenciaOperadora
		// Llamar al numero de emergencia de la operadora del usuario
		Button operadora = (Button) findViewById(R.id.emergenciaOperadora);
		operadora.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				//Se busca cual es la operadora del usuario.
				Context c = MostrarTelefonos.this;
				TelephonyManager manager = (TelephonyManager) c
						.getSystemService(Context.TELEPHONY_SERVICE);
				String operadora = manager.getNetworkOperatorName()
						.toLowerCase();
				String tlf;

				if (operadora.contains("movistar")) {
					tlf = "*911";
				} else if (operadora.contains("digitel")) {
					tlf = "112";
				} else {
					tlf = "*1";
				}

				Intent callIntent = new Intent(Intent.ACTION_CALL);
				callIntent.setData(Uri.parse("tel:" + tlf));
				startActivity(callIntent);
			}
		});

	}
}
