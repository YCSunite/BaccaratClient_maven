package controller;

import javafx.application.Application;

import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;

import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.BaccaratInfo;
import util.Util;
import java.io.*;
import java.net.Socket;

public class Main extends Application implements EventHandler, Serializable {
	Stage primaryStage;
	VBox mainRoot;
	GameSceneController gameSceneController;
	public TextField ipAddress;
	public TextField clientName;
	public TextField portNum;
	public Button connectBtn;
	private BaccaratInfo baccaratInfo;
	private Socket socket;


	@Override
	public void start(Stage primaryStage) throws Exception {
		this.primaryStage = primaryStage;  // initialze the primary stage

		VBox parent = new VBox();
		parent.setPadding(new Insets(100,120,0,120));
		clientName =  new TextField();
		clientName.setPromptText("Enter name:");

		ipAddress =  new TextField();
		ipAddress.setPromptText("Enter IP Address:");

		portNum = new TextField();
		portNum.setPromptText("Enter Port Number:");

		connectBtn = new Button("Connect");
		clientName.setAlignment(Pos.CENTER);
		ipAddress.setAlignment(Pos.CENTER);
		portNum.setAlignment(Pos.CENTER);
		connectBtn.setAlignment(Pos.CENTER);
		connectBtn.setPrefWidth(100);
		connectBtn.setPrefHeight(40);

		connectBtn.setOnAction(this);

		parent.getChildren().addAll(clientName,ipAddress,portNum, connectBtn);
		Scene scene = new Scene(parent, 600, 600);
		primaryStage.setTitle("Baccarrat Game");
		primaryStage.setScene(scene);
		primaryStage.show();

	}

	public void showGameScene(ObjectOutputStream out) throws IOException {
		System.out.println("showing game scene");
		mainRoot = new VBox();
		gameSceneController = new GameSceneController(mainRoot, socket, baccaratInfo, out);
		primaryStage.setScene(new Scene(mainRoot,600,600));
		primaryStage.show();

	}

	@Override
	public void handle(Event event) {
		if (event.getSource() == connectBtn){
			try {
				// connect to server, then show game scene
				System.out.println("Connecting to server...");
				ObjectOutputStream out = connectToServer(Integer.valueOf(portNum.getText()));
				showGameScene(out);
			} catch (IOException | ClassNotFoundException e) {    //
				e.printStackTrace();
			}

		}
	}


	public ObjectOutputStream connectToServer(int portNumber) throws IOException, ClassNotFoundException {

		socket = new Socket(ipAddress.getText(), portNumber);
		ObjectOutputStream outStream = new ObjectOutputStream(socket.getOutputStream());


		baccaratInfo = new BaccaratInfo(socket.getLocalSocketAddress().toString(),portNumber, clientName.getText());

		baccaratInfo.actionRequest = Util.ACTION_REQUEST_CONNECT;
		outStream.writeObject(baccaratInfo);
		outStream.reset();
		outStream.reset();
		return outStream;
	}
}
