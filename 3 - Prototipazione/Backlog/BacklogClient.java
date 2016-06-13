/*
* Codice prototipo scritto per testare la funzionalita' backlog dei ServerSocket.
* La classe presente implmenta un Socket che si connette a un ServerSocket in ascolto sulla 9090 con Backlog 7.
*/

import java.io.*;
import java.net.*;

public class BacklogClient {
	public static void main(String[] args) {
		for (int n = 1; n < 5000; n++) {
			try (
				Socket s = new Socket("localhost", 9090);
				ObjectOutputStream oout = new ObjectOutputStream (s.getOutputStream());
			    )
			{
				oout.writeObject(254);
				System.out.println("Connessione n. " + n + " inviato: " + 254);
			}
			catch (Exception e)
			{
				System.out.println("Errore");
				e.printStackTrace();
				System.exit(1);
			}
		}
	}
}
