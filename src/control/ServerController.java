package control;

import java.net.ServerSocket;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import services.threads.MainThread;

public class ServerController implements Initializable{

	@FXML
	public Label lbl_status;
	
	@FXML 
	public TextField txf_porta;
	
	@FXML
	public Button btn_abrirFechar;
	
	public static TextField porta;
	public static Button abrirFechar;
	public static Label status;
	
	public static ServerSocket server;
	public static MainThread mainThread;
	
	public static int onlineUsers = -1;
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		
		//Criando apontamentos staticos para os controls
		status = lbl_status;
		porta = txf_porta;
		abrirFechar = btn_abrirFechar;
		
		btn_abrirFechar.setOnAction(e -> openClose());
		txf_porta.setOnAction(e -> openClose());
		
	}
	
	public void openClose() {
					
		MainThread.isOpen = !MainThread.isOpen;
		
		if(MainThread.isOpen) {			
			mainThread = new MainThread();
			mainThread.start();
		}else {
				
			try {				
				server.close();				
			} catch (Exception e) {
				System.out.println(e.getMessage());
			}
			
			lbl_status.setText("Desconectado.");
			txf_porta.setEditable(true);
			btn_abrirFechar.setText("Instanciar");
			onlineUsers = -1;
			
			MainThread.flag = 1;
		}
		
		
	}
	
	public static void update() {
		Platform.runLater(() -> {
			onlineUsers += 1;
			status.setText("Conectado (" + onlineUsers + " usuários online).");
			abrirFechar.setText("Fechar");
			porta.setEditable(false);
		});
	}
	
}
