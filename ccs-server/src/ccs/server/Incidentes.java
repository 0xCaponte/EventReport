/* Autor: Carlos Aponte -- 09-10041
 * 
 * Clase: Servlet Incidentes
 * 
 * Descripcion: este servlet se encarga de enviar la lista de los incidentes almacenados en el servidor,
 * SIN los datos del denunciante. Asi mismo, se encarga de recibir una lista de incidentes enviado por
 * un cliente y anexarla a la lista del servidor.
 * */

package ccs.server;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

/**
 * Servlet implementation class Incidentes
 */
@WebServlet("/Incidentes")
public class Incidentes extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public Incidentes() {
		super();
		// TODO Auto-generated constructor stub
	}

	//Clase incidente compatible con la clase incidente que usan los clientes.
	private class Incidente {

		Double lat; // Ubicacion donde ocurrio el incidente.
		Double lon;
		Date hora; // Fecha y hora del incidente.
		String tipo; // Tipo del incidente.
		String denunciante; // Quien es el usuario que lo reporta.

		public Incidente(Double l1, Double l2, Date h, String t, String d){
			lat = l1;
			lon = l2;
			hora = h;
			tipo = t;
			denunciante = d;			
		}
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 *      
	 *      Metodo que se ejecuta cada vez que hay una peticion de tipo GET. En este metodo
	 *      se carga la lista d eincidentes del servidor, y se les transforma en un objeso JSON,
	 *      el cual es enviado a los clientes.	 
	 *      
	 */
	
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		//Se carga el archivo de incidentes
		PrintWriter out = response.getWriter();
		String home = System.getProperty("user.home");
		String path = home + "/server-ccs/";
		String archivo = path + "lista_incidentes.xml";
		File a = new File(archivo);
		FileInputStream in = new FileInputStream(a);

		try {
			
			// Se parsea el documento XML
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document documento = db.parse(in);
			documento.getDocumentElement().normalize();

			// Se desgloza el XML en funcion del tag incidente
			NodeList nodos = documento.getElementsByTagName("incidente");
		
			ArrayList<Incidente> l = new ArrayList<Incidente>();
			
			for (int i = 0; i < nodos.getLength(); i++) {

				Node nodo = nodos.item(i);

				// Para cada incidente se le obtienen y almacenan sus datos
				if (nodo.getNodeType() == Node.ELEMENT_NODE) {

					double lat = Double.parseDouble(obtenerValor("latitud",
							(Element) nodo));
					double lon = Double.parseDouble(obtenerValor("longitud",
							(Element) nodo));
					String tipo = obtenerValor("tipo", (Element) nodo);
					String hora = obtenerValor("hora", (Element) nodo);
					String denunciante = obtenerValor("denunciante", (Element) nodo);
					denunciante = "100000";
					DateFormat formato = new SimpleDateFormat("dd-MM-yyyy' -- 'HH:mm");
					Date h = formato.parse(hora);
			
					Incidente tmp = new Incidente(lat,lon,h,tipo,denunciante);
					l.add(tmp);

				}
			}
			
			//Se crea un json transformando la lista de incidentes. Esto se hace 
			//usando la libreria Gson de google.
			String json = new Gson().toJson(l);
			out.print(json);
			in.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 *      
	 *      Metodo que se ejecuta cada vez que hay una peticion de tipo POST. En este metodo
	 *      se recibe una lista lista de incidentes desde un cliente, y se les anexa al servidor.
	 *    
	 */
	
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		//Procesamo el Json Mandado por el cliente
		processRequest(request, response);

	}

	// Funcion que devuelve el valor asociado a una etiqueta dada
	private static String obtenerValor(String etiqueta, Element elemento) {
		return elemento.getElementsByTagName(etiqueta).item(0).getChildNodes()
				.item(0).getNodeValue();
	}

	// Escribe los incidentes que han sido reportados por los clientesen el
	// archivo
	private boolean reportarIncidentes(ArrayList<Incidente> l)
			throws IOException {

		//Preparamos el archivo de incidentes
		String home = System.getProperty("user.home");
		String path = home + "/server-ccs/";
		String archivo = path + "lista_incidentes.xml";
		
		String data = "";
		for (int i = 0; i < l.size(); i++) {
			
			// Se carga cada incidente
			Incidente r = l.get(i);
			
			DateFormat formato = new SimpleDateFormat(
					"dd-MM-yyyy' -- 'HH:mm");
			String tiempo  = "";
			
			tiempo = formato.format(r.hora);
			
			data = data + "\t<incidente>" + "\n\t\t<latitud>" + r.lat
					+ "</latitud>" + "\n\t\t<longitud>" + r.lon + "</longitud>"
					+ "\n\t\t<hora>" + tiempo + "</hora>" + "\n\t\t<tipo>"
					+ r.tipo + "</tipo>" + "\n\t\t<denunciante>"
					+ r.denunciante + "</denunciante>" + "\n\t</incidente>"
					+ "\n";
		}

		data = data + "\n</lista-incidentes>";
		
		// Se lee todo el archivo, y se le anexa los nuevos incidente al
		// final.
		try {

			// Cargamos el contenido actual
			FileInputStream fis = new FileInputStream(archivo);
			BufferedReader buffer = new BufferedReader(new InputStreamReader(fis));

			StringBuilder sb = new StringBuilder();
			String linea, info, new_info;
			new_info = "";

			while ((linea = buffer.readLine()) != null) {
				sb.append(linea + "\n");
			}

			fis.close();

			// Agregamos el nuevo incidente
			info = sb.toString();

			if (info == "") {
				new_info = "<lista-incidentes>" + data;
			} else {

				if (info.contains("</lista-incidentes>")) {
					new_info = info.replace("</lista-incidentes>", data);

				}
			}

			// Guardamos el nuevo archivo.
			FileOutputStream fos = new FileOutputStream(new File(archivo));
			fos.write(new_info.getBytes());
			fos.close();
			return true;

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
		}

		return false;
	}

	// Funcuion que recibe el json de los incidentes y lo almacena en el archivo
	protected void processRequest(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("application/json");
		// Gson gson = new Gson();

		try {
			
			//Carga todo lo enviado por el cliente en un solo String
			StringBuilder sb = new StringBuilder();
			String s;
			while ((s = request.getReader().readLine()) != null) {
				sb.append(s);
			}
			
			//Parsea el string del cliente, en funcion del tipo de informacion
			//que contiene, en este caso en funcion de una lista de incidentes.
			Gson gson = new Gson();
			Type tipo = new TypeToken<List<Incidente>>(){}.getType();
			
			JsonElement json = new JsonParser().parse(sb.toString());

			List<Incidente> lista = gson.fromJson(json, tipo);
			
			
			//Volvemos la lista de incidentes un array list.
			ArrayList<Incidente> l =  new ArrayList<Incidente>();
			l.addAll(lista);

			//Escribimos en el archivo
			boolean result = reportarIncidentes(l);
			
			if (result){
				response.getOutputStream().print("ok");
			}else{
				response.getOutputStream().print("dail");
			}
	
			//Enviamos respuesta al cliente 
			response.getOutputStream().flush();

		} catch (Exception ex) {
			ex.printStackTrace();
			response.getOutputStream().print("fail");
			response.getOutputStream().flush();
		}
	}
}
