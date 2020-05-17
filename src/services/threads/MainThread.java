package services.threads;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import control.ServerController;
import javafx.application.Platform;
import javafx.scene.control.Alert.AlertType;
import model.entities.Usuario;
import services.alerts.Alerts;

public class MainThread extends Thread {

	public static boolean isOpen = false;
	public static List<Usuario> usuarios = new ArrayList<>();
	public static List<ChatThread> chats = new ArrayList<>();
	
	private BufferedReader in;
	private PrintWriter out;
	
	public static int flag = 0;
	
	public void run() {
		
		try {
			ServerController.server = new ServerSocket(Integer.parseInt(ServerController.porta.getText()));
			System.out.println("Servidor instanciado");
			ServerController.update();
			flag = 1;
			
			while(isOpen) {
				System.out.println("Esperando conexões...");
				Socket client = ServerController.server.accept();
				
				System.out.println(client);
			
				in = new BufferedReader(new InputStreamReader(client.getInputStream()));
				out = new PrintWriter(client.getOutputStream(), true);
				
				System.out.println("Esperando requisição.");
				String nome = in.readLine();
				Usuario user = new Usuario(nome, client);
				
				if(!usuarios.contains(user)) {
					usuarios.add(user);
					System.out.println("OK");
					
					ChatThread chat = new ChatThread(usuarios.get(usuarios.size() -1));
					chats.add(chat);
					chat.start();
					
					ServerController.update();
					
					out.println("OK");
				}else {
					System.out.println("TRYAGAIN");
					out.println("TRYAGAIN");
				}
			}
			
		} catch (IOException e) {
			System.out.println(e.getMessage());
		} catch(NumberFormatException e1) {
			Platform.runLater(() -> {
				Alerts.doAlert("Server", "Digite uma porta valida.", AlertType.WARNING);
				ServerController.porta.setEditable(true);
				flag = 1;
			});
		} catch(IllegalArgumentException e2) {
			Platform.runLater(() -> {
				e2.printStackTrace();
				Alerts.doAlert("Server", "Porta inexistente.", AlertType.WARNING);
				ServerController.porta.setEditable(true);
				flag = 1;
			});
		}
		
		System.out.println("Finalizando todas as conexões.");
				
		for(ChatThread chat : chats) {
			try {
				chat.getClient().getSocket().close();
				chat.getClient().setRunning(false);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		isOpen = false;
		
		Platform.runLater(() -> {
			ServerController.status.setText("Desconectado.");
			ServerController.porta.setEditable(true);
			ServerController.abrirFechar.setText("Instanciar");
			ServerController.onlineUsers = -1;
		
			if(flag == 0) {
				Alerts.doAlert("server", "A porta não está disponivel.", AlertType.WARNING);
			}else {
				flag = 0;
			}
			
		});
		 
		System.out.println("Conexões finalizadas.");
		
	}
	
}
