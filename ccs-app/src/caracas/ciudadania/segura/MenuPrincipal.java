/* Autor: Carlos Aponte -- 09-10041
 * 
 * Clase: MenuPrincipal
 * 
 * Descripcion: esta clase extiende a Activity, y se encarga de manejar la pantalla principal de la actividad.
 * En esta vista estan contenidos los botones que nos envian a las diversas actividades de la aplicacion.
 * */

package caracas.ciudadania.segura;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MenuPrincipal extends Activity {

	// Se llama cuando se crea la actividad por primera vez.
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.menu_principal);

		// On Click Emergencia! -- Va a la vista de boton de Panico
		Button btnPanico = (Button) findViewById(R.id.Panico);
		btnPanico.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				startActivity(new Intent("caracas.ciudadania.segura.Emergencia"));
			}
		});

		// On Click Mapa -- Va al mapa de visualizacion de incidentes
		Button btnMapa = (Button) findViewById(R.id.MapaIncidentes);
		btnMapa.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				startActivity(new Intent(
						"caracas.ciudadania.segura.MostrarMapa"));
			}
		});

		// On Click Reportar -- Va al formulario de reporte
		Button btnReportar = (Button) findViewById(R.id.Reporte);
		btnReportar.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				startActivity(new Intent(
						"caracas.ciudadania.segura.FormularioReporte"));
			}
		});

		// On click Telefonos -- Va a la lista de Telefonos de Emergencia
		Button btnTlfs = (Button) findViewById(R.id.Tlfs);
		btnTlfs.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				startActivity(new Intent(
						"caracas.ciudadania.segura.MostrarTelefonos"));
			}
		});

		// On Click Configurar -- Va a la vista de configuracion del boton de
		// panico
		Button btnConf = (Button) findViewById(R.id.configuracion);
		btnConf.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				startActivity(new Intent(
						"caracas.ciudadania.segura.Configuracion"));
			}
		});

		// On Click Noticias -- Va a la lista de noticias
		Button btnNoticias = (Button) findViewById(R.id.noticias);
		btnNoticias.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				startActivity(new Intent("caracas.ciudadania.segura.Noticias"));
			}
		});

	}
}
