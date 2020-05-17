package services.threads;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import control.ServerController;
import model.entities.Usuario;

public class ChatThread extends Thread {

	private Usuario client;

	private BufferedReader in;

	public ChatThread(Usuario client) {
		this.client = client;
		try {
			in = new BufferedReader(new InputStreamReader(client.getSocket().getInputStream()));
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	@Override
	public void run() {

		System.out.println(client.getNome() + " entrou.");

		try {

			while (client.isRunning()) {

				String request = in.readLine();

				if (request.startsWith("SEND")) {
					SEND(request);
				}

			}

		} catch (IOException e) {
			System.out.println(e.getMessage() + " cod: 01");
			run();
		} catch (NullPointerException ne) {

			MainThread.usuarios.remove(client);
			
			ServerController.onlineUsers -= 2;
			ServerController.update();

			try {
				client.getSocket().close();
			} catch (IOException e) {
				System.out.println(e.getMessage() + " problemas para fechar a conexao");
			}
			client.setRunning(false);
		}
		System.out.println("Finalizou a seguinte conexão: " + client);
	}

	public void SEND(String request) {

		System.out.println(request);

		String[] elements = request.split(":");

		String nome = elements[1];

		for (Usuario user : MainThread.usuarios) {
			if (!user.getNome().equals(nome)) {
				user.getOut().println(request);
			}
		}

	}

	public Usuario getClient() {
		return client;
	}

	public void setClient(Usuario client) {
		this.client = client;
	}

}
