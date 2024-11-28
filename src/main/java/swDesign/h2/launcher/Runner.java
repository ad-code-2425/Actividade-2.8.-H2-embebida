package swDesign.h2.launcher;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

import org.h2.tools.Console;
import org.h2.tools.RunScript;

import swDesign.h2demo.db.DbConnectionHelper;

public class Runner {

	public static void main(String[] args)  {
		
		try {
			
			// Abre la consola de administración de H2
			Console.main();
						
			Connection conn = crearConexion();

			inicializarBasedeDatos(conn);
			
			listarPelicula(conn,0);
			
			// Paramos la consola para evitar que el programa termine
			// hasta que nosotros lo decidamos
			System.out.println("Pulse INTRO para salir.");
			Scanner scanner = new Scanner(System.in); 
			scanner.nextLine();
			scanner.close();
			
			// El programa no se cerrará hasta que la consola de administración 
			// de H2 no se cierre. Para forzar el cierre de la aplicación, utilizamos 
			// el método System.exit(0), que indica que la aplicación debe cerrase con
			// código de salida 0, es decir éxito.
			System.out.println("Cerrando el programa.");
			System.exit(0);
			
		} catch(SQLException e) {
			System.out.println("Algo fue mal con la base de datos.");
			e.printStackTrace();
		}
		
 	} // main
	
	private static Connection crearConexion() throws SQLException {
		
		System.out.println("Creando conexión a la base de datos.");
		
		// Configuramos y usamos la clase DbConnectionHelper
		DbConnectionHelper.setUsername("sa");
		DbConnectionHelper.setPassword("");
		DbConnectionHelper.setConnectionUrl("jdbc:h2:mem:h2Demo");
		
		return DbConnectionHelper.createConnection();

	} // crearConexion

	private static void inicializarBasedeDatos(Connection conn) throws SQLException {
	
		System.out.println("Creando las tablas en la base de datos.");
		
		try {
			// Ejecutamos el script para crear las tablas.
			RunScript.execute(conn, new FileReader("src/main/resources/sql/createDatabase.sql"));
		} catch(FileNotFoundException e) {
			System.out.println("El script para crear la base de datos no ha podido ser encontrado.");
		}
		
		System.out.println("Tablas creadas.");
		System.out.println("Alimentando con datos la base de datos.");
		
		try {
			// Ejecutamos el script para añadir películas a la base de datos
			RunScript.execute(conn, new FileReader("src/main/resources/sql/feedDatabase.sql"));
		} catch(FileNotFoundException e) {
			System.out.println("El script para cargar datos en la base de datos no ha podido ser encontrado.");
		}
	
	} // inicializarBasedeDatos

	private static void listarPelicula(Connection conn, int id) {
		
		// Creamos una consulta como una cadena con valores parametrizados. Los valores 
		// parametrizados se representan y numeran como ?1, ?2, ?3, ...
		String query = "SELECT titulo, director, nacionalidad FROM Pelicula WHERE "
				+ "id = ?1";

		// Creamos desde la conexión una consulta preparada/almacenada a partir de la cadena
		// definida anterioremente.
		try(PreparedStatement ps = conn.prepareStatement(query)) {
			
			// Sustituimos el valor del valor parametrizado ?1 por el valor del argumento id 
			ps.setInt(1, id);
			// Ejecutamos la consulta y obtenemos un result set, o tabla iterable de resultados.
			try(ResultSet rs = ps.executeQuery()) {
			
				// Intentamos ponernos en la primera y última fila del ResultSet
				if (rs.next()) {
					
					 // Recuperamos valores de las columnas correspondientes a la primera fila 	
					 String titulo       = rs.getString("titulo");
					 String director     = rs.getString("director");
					 String nacionalidad = rs.getString("nacionalidad"); 
					 
					 // Mostramos los valores recuperados por pantalla.
					 imprimirPelicula(titulo,director,nacionalidad);
				}
				
			} catch (SQLException e) {
				System.out.println("Falla en el executeQuery");
				e.printStackTrace();
			}
		} catch (SQLException e) {
			System.out.println("Falla en el prepareStatement");
			e.printStackTrace();
		} 
	}

	private static void imprimirPelicula(String titulo, String director, String nacionalidad) {

		System.out.println("");
		System.out.println(" Cartelera  ");
		System.out.println("============");
		
		System.out.println("`" + titulo + "` de " + director + " (" + nacionalidad + ")");
		System.out.println("");
	}
	
	

}
