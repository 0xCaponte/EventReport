/* Autor: Carlos Aponte -- 09-10041
 * 
 * Clase: Configuracion
 * 
 * Descripcion: esta clase extiende a ActionBarActivity, y se encarga de manejar lo que se ve y 
 * lo que ocurre en la primera vista de la aplicacion. Aqui se hace la carga de datos inicial 
 * (ubicacion, datos del usuario, preferencias, etc)
 */
package caracas.ciudadania.segura;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.telephony.TelephonyManager;

public class MainActivity extends ActionBarActivity {


	//Se llama cuando se crea la actividad por primera vez
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.splash_screen);

		// Buscamos la ubicacion del usuario.
		Ubicacion u = new Ubicacion(MainActivity.this);
		u.actualizarUbicacion(MainActivity.this);

		
		// Establecemos la "identidad" del usuario (nro tlf)
		TelephonyManager tMgr = (TelephonyManager) MainActivity.this
				.getSystemService(Context.TELEPHONY_SERVICE);
		App.setNroTlf(tMgr.getLine1Number());

		// Inicia un timer que nos enviara al menu principal
		Thread splashTimer = new Thread() {
			public void run() {
				try {
					sleep(1000);

					// Revisamos que el boton de panico este configurado.
					// De no estarlo, vamos a la configuracion
					if (!confInicial(MainActivity.this)) {
						startActivity(new Intent(
								"caracas.ciudadania.segura.Configuracion"));
					} else {

						startActivity(new Intent(
								"caracas.ciudadania.segura.CLEARSCREEN"));
					}
				}

				catch (InterruptedException e) {

					e.printStackTrace();
				}

				finally {
					finish();
				}
			}
		};

		splashTimer.start();
	}

	// Funcion que revisa que la configuracion inicial exista
	private boolean confInicial(Context c) {

		Preferencias p = new Preferencias();

		// Revisamos Datos de Emergencias
		if ((p.sizeContactos(c) == 0) | p.mensajeEmpty(c)) {

			return false;
		}

		return true;
	}
}
