/*
* Codice prototipo scritto per testare la funzionalita' backlog dei ServerSocket.
* La classe presente implmenta un ServerSocket in ascolto sulla 9090 con Backlog 7.
*/

import java.net.*;
import java.io.*;

public class BacklogServer {
	private static int n = 0;

	public static void main(String[] args) {
		try ( ServerSocket servs = new ServerSocket(9090, 2) ){
			System.out.println("Server avviato correttamente. Utilizzare Ctrl + c per terminare.");
			while (true) {
				Socket s = servs.accept();
				Thread t = new Thread() {
					public void run() {
						try (
							ObjectInputStream oin = new ObjectInputStream(s.getInputStream())
						    )
						{
							System.out.println("Connessione n. " + ++n + ") ricevuto: " + (int) oin.readObject());
						}
						catch (Exception e)
						{
							e.printStackTrace();
						}
					}
				};
				t.start();

				Thread.sleep(3000);
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
}
