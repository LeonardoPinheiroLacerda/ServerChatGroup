package application;
	
import java.io.IOException;

import control.ServerController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import services.threads.ChatThread;
import services.threads.MainThread;


public class ServerMain extends Application {
	
	private final String VIEW_PATH = "/view/ServerStart.fxml";
	
	@Override
	public void start(Stage primaryStage) {
		try {
			
			Parent parent = FXMLLoader.load(getClass().getResource(VIEW_PATH));
			
			primaryStage.setTitle("chat server");
			primaryStage.setScene(new Scene(parent));
			
			primaryStage.show();
			
			primaryStage.setOnCloseRequest(e ->{
				
				for(ChatThread chat : MainThread.chats) {
					try {
						chat.getClient().getSocket().close();
						chat.getClient().setRunning(false);
					} catch (IOException e2) {
						e2.printStackTrace();
					}
				}
				MainThread.isOpen = false;
				
				try {				
					ServerController.server.close();				
				} catch (Exception e1) {
					System.out.println(e1.getMessage());
				}
				
			});
			
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}