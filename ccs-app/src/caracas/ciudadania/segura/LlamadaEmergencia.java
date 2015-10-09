/* Autor: Carlos Aponte -- 09-10041
 * 
 * Clase: LlamadaEmergencia
 * 
 * Descripcion: esta clase extiende a Activity, por lo tanto es usada para manejar lo que se ve y 
 * lo que ocurre en la vista que activa el boton de panico.
 *  */

package caracas.ciudadania.segura;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class LlamadaEmergencia extends Activity {

	private CountDownTimer countDownTimer; // Timmer antes de solicitar ayuda
	private Button btnCancelar; // Boton que cancela la solicitud
	private TextView text; // Lo que muestra el timmer
	private final long startTime = 15000; // Tiempo de inicio (15 seg)
	private final long interval = 1000; // Intervalos de 1 seg


	//Se llama cuando se crea la actividad por primera vez
	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.boton_panico);

		// Inicializacion del timer
		text = (TextView) this.findViewById(R.id.timer);
		countDownTimer = new MyCountDownTimer(startTime, interval);
		text.setText(text.getText() + String.valueOf(startTime / 1000));

		countDownTimer.start();

		// On Click Cancelar
		btnCancelar = (Button) findViewById(R.id.btnCancelar);
		btnCancelar.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				// Se finaliza la solicitud de ayuda y vuelve al menu
				LlamadaEmergencia.this.finish();
				countDownTimer.cancel();
			}
		});

	}

	// Funcion que maneja el timer.
	public class MyCountDownTimer extends CountDownTimer {

		public MyCountDownTimer(long startTime, long interval) {
			super(startTime, interval);
		}

		// Controla lo que ocurre al finalizar el tiempo
		@Override
		public void onFinish() {

			// Enviar mensaje de ayuda + ubicacion a los contactos.
			Preferencias p = new Preferencias();
			ArrayList<String> l = p.getContactos(LlamadaEmergencia.this);
			String m = p.getMensaje(LlamadaEmergencia.this);

			String lat = Double.toString(App.getLatitud());
			String lng = Double.toString(App.getLongitud());
			m = m + "\nTlf: " + App.getNroTlf() + "\nCoordenadas:"
					+ lat.substring(0, 10) + " ; " + lng.substring(0, 10);

			//Se envia el SMS
			envioMensaje(l, m);

			//Se cambia el valor de algunos botones de la vista
			text.setText("Enviado");
			Button b = (Button) findViewById(R.id.btnCancelar);
			b.setText("Volver");

			// Volvemos la menu en 5 segundos
			Timer timer = new Timer();
			timer.schedule(new TimerTask() {

				public void run() {

					LlamadaEmergencia.this.finish();
				}

			}, 5000);
		}

		//Controla que pasa con cada tick del reloj
		@Override
		public void onTick(long millisUntilFinished) {
			text.setText("" + millisUntilFinished / 1000);
		}
	}

	//Funcion que envia el SMS a la lista de contactos
	private void envioMensaje(ArrayList<String> l, String m) {

		SmsManager smsManager = SmsManager.getDefault();

		int fin = (l.size() / 2) - 1;
		int i = 0;
		
		//A cada contacto se le envia el sms
		while (i < fin) {

			int pos = i * 2;
			i++;

			if (l.get(pos + 1) != null) {
				String nro = l.get(pos + 1);
				nro = nro.replace("-", "");
				smsManager.sendTextMessage(nro, null, m, null, null);
			}
		}
	}

}
