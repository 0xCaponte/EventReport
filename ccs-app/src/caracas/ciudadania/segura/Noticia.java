/* Autor: Carlos Aponte -- 09-10041
 * 
 * Clase: Noticia
 * 
 * Descripcion: esta clase define el tipo Noticia que es usado en la aplicacion. De la
 * misma forma se encarga de manejar el manejo de este tipo mediante gettes y setters.
 *  */
package caracas.ciudadania.segura;

public class Noticia {

	String titulo; //Titulo de la noticia
	String link;	// Link a la pagina donde esta la noticia

	/* ---------- Constructor ------------ */
	public Noticia(String titulo, String link) {
		super();
		this.titulo = titulo;
		this.link = link;
	}

	public Noticia() {

	}

	/** ------- Getters y Setters------- */

	public String getTitulo() {
		return titulo;
	}

	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}
}
