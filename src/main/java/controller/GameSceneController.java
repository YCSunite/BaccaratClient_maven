package controller;

import javafx.application.Platform;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.*;

import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import model.Card;
import model.CardVisual;
import model.BaccaratInfo;
import util.Util;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;


public class GameSceneController extends Thread implements EventHandler {

    VBox gameScene;
    HBox scoreRow;
    HBox playArea;
    HBox bidRow;
    HBox player1;
    HBox player2;
    HBox banker1;
    HBox banker2;
    Button makeDraw;
    Button play;
    Button quit;
    Button playerWins;
    Button bankerWins;
    Button tieGame;
    TextField displayDollars;
    Socket socket;
    BaccaratInfo baccaratInfo;
    ObjectOutputStream out;
    ObjectInputStream in;
    int countClicks;
    int drawBtnClicks;
    int playerCard;
    int bankerCard;
    Label playerScore;
    Label bankerScore;


    public GameSceneController(VBox gameScene, Socket socket, BaccaratInfo baccaratInfo, ObjectOutputStream out) throws IOException {
        this.socket = socket;
        this.gameScene = gameScene;
        this.baccaratInfo = baccaratInfo;
        this.out = out;

        gameScene.setPadding(new Insets(10,10,10,10));

        scoreRow =  new HBox();
        createScoreRow();
        playArea = new HBox();
        createPlayArea();
        bidRow = new HBox();
        bidRow.setSpacing(30);
        createControlsArea();
        initializeGame();

        gameScene.getChildren().addAll(scoreRow,playArea, bidRow);
        this.start();

    }


    public void createScoreRow(){

        playerScore = new Label("0");    // display the player's current score

        playerScore.setAlignment(Pos.CENTER);
        Label playerLabel = new Label("PLAYER");
        Label baccarat = new Label("BACCARAT");
        baccarat.setUnderline(true);
        Label bankerLabel = new Label("BANKER");
        bankerScore = new Label("0");

        bankerScore.setAlignment(Pos.CENTER);
        bankerLabel.setAlignment(Pos.CENTER);
        playerLabel.setAlignment(Pos.CENTER);
        baccarat.setAlignment(Pos.CENTER);

        playerScore.setPrefSize(50,50);
        playerScore.setBackground(new Background(new BackgroundFill(Color.INDIANRED, CornerRadii.EMPTY, Insets.EMPTY)));
        bankerScore.setPrefSize(50,50);
        bankerScore.setBackground(new Background(new BackgroundFill(Color.INDIANRED, CornerRadii.EMPTY, Insets.EMPTY)));

        playerLabel.setBackground(new Background(new BackgroundFill(Color.INDIANRED, CornerRadii.EMPTY, Insets.EMPTY)));
        bankerLabel.setBackground(new Background(new BackgroundFill(Color.INDIANRED, CornerRadii.EMPTY, Insets.EMPTY)));
        playerLabel.setPrefSize(140,50);
        bankerLabel.setPrefSize(140,50);
        baccarat.setPrefSize(120,50);

        scoreRow.setSpacing(600/16);
        scoreRow.getChildren().addAll(playerScore,playerLabel, baccarat,bankerLabel, bankerScore); // adding all the elements to the scoreRow

    }
    public void createPlayArea(){
        playArea.setMinHeight(380);
        Card sampleCard = new Card("test", 0);
        CardVisual cardVisual = new CardVisual(sampleCard);
        cardVisual.getVisual().setVisible(false);


        VBox playerArea = new VBox();
        playerArea.setMinWidth(600/2.5);
        playerArea.setSpacing(10);
        VBox drawArea = new VBox();
        drawArea.setMinWidth(600/5);
        VBox bankerArea = new VBox();
        bankerArea.setSpacing(10);
        bankerArea.setMinWidth(600/2.5);


        player1 = new HBox();
        player1.setAlignment(Pos.CENTER);
        player1.setSpacing(10);
        player2 = new HBox();
        player2.setAlignment(Pos.CENTER);
        playerArea.getChildren().addAll(player1, player2);


        makeDraw = new Button("DRAW");
        makeDraw.setPrefSize(70,50);
        makeDraw.setOnAction(this);
        drawArea.setAlignment(Pos.CENTER);
        drawArea.getChildren().add(makeDraw);


        banker1 = new HBox();
        banker1.setAlignment(Pos.CENTER);
        banker1.setSpacing(10);
        banker2 = new HBox();
        banker2.setAlignment(Pos.CENTER);
        bankerArea.getChildren().addAll(banker1,  banker2);

        playArea.setBackground(new Background(new BackgroundFill(Color.DARKGREEN, null, null)));
        playArea.getChildren().addAll(playerArea, drawArea, bankerArea);
        playArea.setPadding(new Insets(10,10,0,0));

    }

    public void createControlsArea(){
        VBox bidAmount = new VBox();
        Label bidLabel = new Label();
        bidLabel.setText("Enter Your Bid Amount");
        bidLabel.setAlignment(Pos.CENTER);
        bidLabel.setPrefSize(180,50);

        displayDollars = new TextField();
        displayDollars.setPromptText("$");
        displayDollars.setPrefSize(180,30);
        displayDollars.setAlignment(Pos.CENTER);
        displayDollars.setOnKeyReleased(this);
        bidAmount.getChildren().addAll(bidLabel,displayDollars);


        VBox betChoices = new VBox();
        Label betsLabel = new Label();
        betsLabel.setText("What will you bet on?");
        betsLabel.setPrefSize(180,50);

        playerWins = new Button();
        playerWins.setText("Player");
        playerWins.setAlignment(Pos.CENTER);
        playerWins.setPrefSize(180,30);
        playerWins.setOnAction(this);

        bankerWins = new Button();
        bankerWins.setText("Banker");
        bankerWins.setAlignment(Pos.CENTER);
        bankerWins.setPrefSize(180,30);
        bankerWins.setOnAction(this);

        tieGame = new Button();
        tieGame.setText("tieGame");
        tieGame.setAlignment(Pos.CENTER);
        tieGame.setPrefSize(180,30);
        tieGame.setOnAction(this);
        betChoices.setSpacing(10);

        betChoices.getChildren().addAll(betsLabel,playerWins,bankerWins,tieGame);


        VBox controls = new VBox();
        play = new Button();
        play.setText("Play");
        play.setAlignment(Pos.CENTER);
        play.setPrefSize(80,50);
        play.setOnAction(this);


        quit
                = new Button();
        quit
                .setText("Quit");
        quit
                .setAlignment(Pos.CENTER);
        quit
                .setPrefSize(80,50);
        quit
                .setOnAction(this);
        controls.setSpacing(30);
        controls.setAlignment(Pos.TOP_RIGHT);

        controls.getChildren().addAll(play, quit
        );

        bidRow.setPadding(new Insets(20,10,10,10));
        bidRow.getChildren().addAll(bidAmount,betChoices, controls);

    }


    public void displayResults(){
        String result;
        if(baccaratInfo.getPlayerDetails().getBetChoice().equals(baccaratInfo.getWinnerMsg())){
            result = "won";
        }
        else{
            result = "lose";
        }
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeight(300);
        alert.setWidth(300);
        alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
        alert.setTitle("Game Over");
        alert.setHeaderText("--Game Results--");


        ArrayList<Card> playerHand = baccaratInfo.getPlayerDetails().getPlayerHand();
        ArrayList<Card> bankerHand = baccaratInfo.getPlayerDetails().getBankerHand();


        if(baccaratInfo.getWinnerMsg().equals("tieGame")){
            alert.setContentText("Player's hand Total : " + baccaratInfo.getPlayerDetails().getHandTotal(playerHand)+"\n"+
           "Banker's hand Total : " + baccaratInfo.getPlayerDetails().getHandTotal(bankerHand)+ "\n"+
            "It's a tieGame ! "+ "You bet on " + baccaratInfo.getPlayerDetails().getBetChoice() + ", you " + result);
        }
        else {
            alert.setContentText("Player's hand Total : " + baccaratInfo.getPlayerDetails().getHandTotal(playerHand)+"\n"+
            "Banker's hand Total : " + baccaratInfo.getPlayerDetails().getHandTotal(bankerHand)+ "\n"+
             baccaratInfo.getWinnerMsg() + " wins !"+ "You bet on " + baccaratInfo.getPlayerDetails().getBetChoice() + ", you " + result);
        }
        alert.showAndWait();
    }


    @Override
    public void handle(Event event) {


        if(event.getSource() == displayDollars){
            System.out.println("enter displayDollars");
            baccaratInfo.getPlayerDetails().setBidAmount(Integer.valueOf(displayDollars.getText()));
            activateButtons();
        }

        if (event.getSource() == makeDraw) {     // send demo packet to server
            if (baccaratInfo.getPlayerDetails().getPlayerHand()==null){
                try {

                    baccaratInfo.actionRequest = Util.ACTION_REQUEST_DRAW;
                    out.reset();            // reset the ObjectOutputStream
                    out.writeObject(baccaratInfo);
                    System.out.println("sent packet to server");


                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            else {
                updateWithCards(baccaratInfo);
                drawBtnClicks++;
                if(drawBtnClicks % 2 != 0){
                    updateCurrentScores(baccaratInfo.getPlayerDetails().getPlayerHand(),playerScore,playerCard);
                    playerCard++;
                }
                else{
                    updateCurrentScores(baccaratInfo.getPlayerDetails().getBankerHand(),bankerScore,bankerCard);
                    bankerCard++;

                }
            }

        }

        if(event.getSource() == play){

            activateButtons();
            baccaratInfo.actionRequest = Util.ACTION_REQUEST_PLAY;
            try {
                out.reset();
                out.writeObject(baccaratInfo);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        if (event.getSource() == quit
        ) {
            try {

                baccaratInfo.setClientPlaying(-1);
                baccaratInfo.actionRequest = Util.ACTION_REQUEST_QUIT;
                out.reset();
                out.writeObject(baccaratInfo);
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println("quit the server");
        }

        if(event.getSource() == playerWins){
            countClicks++;
            if(countClicks % 2!= 0) {
                bankerWins.setDisable(true);
                tieGame.setDisable(true);
                playerWins.setBackground(new Background(new BackgroundFill(Color.INDIANRED, null, null)));
                baccaratInfo.getPlayerDetails().setBetChoice(playerWins.getText());
               activateButtons();

            }
            else{
                 bankerWins.setDisable(false);
                 tieGame.setDisable(false);
                 playerWins.setBackground(new Background(new BackgroundFill(Color.LIGHTGREY, null, null)));
            }


        }
        if(event.getSource() == bankerWins){
           countClicks++;
           if(countClicks % 2!= 0) {
                playerWins.setDisable(true);
                tieGame.setDisable(true);
                bankerWins.setBackground(new Background(new BackgroundFill(Color.INDIANRED, null, null)));
                baccaratInfo.getPlayerDetails().setBetChoice(bankerWins.getText());
                activateButtons();
           }
           else{
               playerWins.setDisable(false);
               tieGame.setDisable(false);
               bankerWins.setBackground(new Background(new BackgroundFill(Color.LIGHTGREY, null, null)));
           }
        }
        if(event.getSource() == tieGame){
            countClicks++;
            if(countClicks % 2!= 0) {
                bankerWins.setDisable(true);
                playerWins.setDisable(true);
                tieGame.setBackground(new Background(new BackgroundFill(Color.INDIANRED, null, null)));
                baccaratInfo.getPlayerDetails().setBetChoice(tieGame.getText());
                activateButtons();
            }
            else{
                playerWins.setDisable(false);
                bankerWins.setDisable(false);
                tieGame.setBackground(new Background(new BackgroundFill(Color.LIGHTGREY, null, null)));
            }

        }

    }

    @Override
    public void run() {
        try {
            in = new ObjectInputStream(this.socket.getInputStream());

            while (true){
                System.out.println("waiting on server");
                BaccaratInfo baccaratInfoFromServer = (BaccaratInfo) in.readObject();


                if (!baccaratInfoFromServer.getIpAddress().toString().equals(socket.getLocalSocketAddress().toString())){
                    System.out.println("incorrect packet");
                    continue;
                }
                else if (baccaratInfoFromServer.actionRequest.equals(Util.ACTION_REQUEST_QUIT)){

                    System.out.println("right packet.");

                    this.interrupt();
                    in.close();
                    out.close();
                    socket.close();
                    Platform.exit();
                    System.exit(0);
                }
                else{
                    System.out.println("right packet! updating");
                    baccaratInfo = baccaratInfoFromServer;
                    updateWithCards(baccaratInfo);

                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            drawBtnClicks++;
                            System.out.println("update scores");
                            updateCurrentScores(baccaratInfo.getPlayerDetails().getPlayerHand(),playerScore,playerCard);
                            playerCard++;

                        }
                    });
                }

            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }


    public void updateCurrentScores(ArrayList<Card> hand, Label scoreLbl,int index){
        ArrayList<Card> tempHand = new ArrayList<>();
        ArrayList<Integer> currScore = new ArrayList<>();

        for(Card card: hand){
            tempHand.add(card);
            currScore.add(baccaratInfo.getPlayerDetails().getHandTotal(tempHand));
        }
        System.out.println("score: " + currScore.get(0));
        if(index >= hand.size()){
            return;
        }
        scoreLbl.setText(String.valueOf(currScore.get(index)));
    }


    public void initializeGame(){

        play.setDisable(true);
        makeDraw.setDisable(true);
        playerWins.setDisable(false);
        bankerWins.setDisable(false);
        tieGame.setDisable(false);
        countClicks = 0;
        drawBtnClicks = 0;
        playerCard = 0;
        bankerCard = 0;

        player1.getChildren().clear();
        player2.getChildren().clear();
        banker1.getChildren().clear();
        banker2.getChildren().clear();
        baccaratInfo.getPlayerDetails().setBankerHand(null);
        baccaratInfo.getPlayerDetails().setPlayerHand(null);
        baccaratInfo.getPlayerDetails().setBidAmount(0);
        playerScore.setText("0");
        bankerScore.setText("0");
        playerWins.setBackground(new Background(new BackgroundFill(Color.LIGHTGREY, null, null)));
        bankerWins.setBackground(new Background(new BackgroundFill(Color.LIGHTGREY, null, null)));
        tieGame.setBackground(new Background(new BackgroundFill(Color.LIGHTGREY, null, null)));
        baccaratInfo.getPlayerDetails().setBetChoice(null);


    }

    // activate the play and draw buttons when needed
     public void activateButtons(){

         if(!play.isDisable()){
             makeDraw.setDisable(false);
             play.setDisable(true);
         }
         if(baccaratInfo.getPlayerDetails().getBetChoice() != null && baccaratInfo.getPlayerDetails().getBidAmount() != 0){

             play.setDisable(false);
         }

     }

    public void updateWithCards(BaccaratInfo baccaratInfo){
       Platform.runLater(new Runnable() {
           @Override
           public void run() {
               ArrayList<Card> playerHand = baccaratInfo.getPlayerDetails().getPlayerHand();
               ArrayList<Card> bankerHand = baccaratInfo.getPlayerDetails().getBankerHand();

               if (playerHand == null || bankerHand == null){
                   return;
               }
               else if (player1.getChildren().size() == 0){
                   CardVisual cardVisual = new CardVisual(playerHand.get(0));
                   player1.getChildren().add(cardVisual.getVisual());
               }
               else if (banker1.getChildren().size() == 0){
                   CardVisual cardVisual = new CardVisual(bankerHand.get(0));
                   banker1.getChildren().add(cardVisual.getVisual());
               }
               else if (player1.getChildren().size() ==1){
                   CardVisual cardVisual = new CardVisual(playerHand.get(1));
                   player1.getChildren().add(cardVisual.getVisual());
               }
               else if (banker1.getChildren().size() ==1){
                   CardVisual cardVisual = new CardVisual(bankerHand.get(1));
                   banker1.getChildren().add(cardVisual.getVisual());
               }
               else if (player2.getChildren().size() == 0 && playerHand.size()>2){
                   CardVisual cardVisual = new CardVisual(playerHand.get(2));
                   player2.getChildren().add(cardVisual.getVisual());
               }
               else if (banker2.getChildren().size() == 0 && bankerHand.size()>2){
                   CardVisual cardVisual = new CardVisual(bankerHand.get(2));
                   banker2.getChildren().add(cardVisual.getVisual());
               }

               else{
                   displayResults();
                   baccaratInfo.getPlayerDetails().setPlayerHand(null);
                   baccaratInfo.getPlayerDetails().setBankerHand(null);
                   try {
                       baccaratInfo.actionRequest = Util.ACTION_REQUEST_GAME_OVER;
                       out.reset();
                       out.writeObject(baccaratInfo);
                       initializeGame();
                   } catch (IOException e) {
                       e.printStackTrace();
                   }
               }

           }
       });

    }
}