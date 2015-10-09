package ccs.server;

/* Autor: Carlos Aponte -- 09-10041
 * 
 * Clase: Servlet Noticias
 * 
 * Descripcion: este servlet se encarga de enviar la lista de las noticias que tiene almacenadas 
 * el servidor a los clientes que las soliciten. 
 * */

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

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
import com.google.gson.GsonBuilder;

/**
 * Servlet implementation class Noticias
 */
@WebServlet("/Noticias")
public class Noticias extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Noticias() {
        super();
        // TODO Auto-generated constructor stub
    }

    //Clase noticia compatible con la clase noticia del cliente
	private class Noticia {

		String titulo;
		String link;
		
		public Noticia(String titulo, String link) {
			super();
			this.titulo = titulo;
			this.link = link;
		}
	}

	
	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 * 
	 *		Metodo que se ejecuta cada vez que hay una peticion de tipo GET. En este metodo
	 *      se carga la lista de NOTICIAS del servidor, y se les transforma en un objeso JSON,
	 *      el cual es enviado a los clientes.	 *      
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		
		//Preparamos el archivo de noticias.
		PrintWriter out = response.getWriter();
		String home = System.getProperty("user.home");
		String path = home + "/server-ccs/";
		String archivo = path + "lista_noticias.xml";
		File a = new File(archivo);
		FileInputStream in = new FileInputStream(a);

		try {
			
			// Se parsea el documento XML
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document documento = db.parse(in);
			documento.getDocumentElement().normalize();

			// Se desgloza el XML en funcion del tag Noticia
			NodeList nodos = documento.getElementsByTagName("noticia");
			
			ArrayList<Noticia> l = new ArrayList<Noticia>();
			
			for (int i = 0; i < nodos.getLength(); i++) {

				Node nodo = nodos.item(i);

				// Para cada Noticia se le obtienen y almacenan sus datos
				if (nodo.getNodeType() == Node.ELEMENT_NODE) {

					String titulo = obtenerValor("titulo", (Element) nodo);
					String link = obtenerValor("link", (Element) nodo);
					Noticia tmp = new Noticia(titulo, link);
					l.add(tmp);

				}
			}
			
			//Se crea un GSON mediante la transformacion de la lista antes creada, usando
			//la libreria GSON de google.
			Gson gson = new GsonBuilder().disableHtmlEscaping().create();
			String json = gson.toJson(l);
			
			//Se envia el contenido como string
			out.print(json);
			in.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}
	
	/** Funcion que devuelve el valor asociado a una etiqueta dada */
		private static String obtenerValor(String etiqueta, Element elemento) {
			return elemento.getElementsByTagName(etiqueta).item(0).getChildNodes()
					.item(0).getNodeValue();
		}

}
