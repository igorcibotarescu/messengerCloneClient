package org.ici.messengercloneclient;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.Socket;
import java.net.URL;

import java.util.List;
import java.util.ResourceBundle;

public class Controller implements Initializable {

    @FXML
    private Button button_send;
    @FXML
    private TextField to_send_msg;
    @FXML
    private VBox vbox;
    @FXML
    private ScrollPane scroll_pane;

    private Client client;
    private static List<String>args;

    public void setStage(Stage stage) {
        // Add close request handler
        stage.setOnCloseRequest(event -> {
            // Perform cleanup or ask for confirmation
            handleCloseEvent();
        });
    }

    private void handleCloseEvent() {
        System.out.println("App is closing...");
        this.client.sendMsg(this.client.getIpAddress() + ":" + this.client.getPort() + ": " + this.client.getName() + "->quit");
        this.client.closeResources();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        try {
            this.client = new Client(new Socket(args.getFirst(), Integer.parseInt(args.get(1))), this.vbox, args.get(2));
            System.out.println("Connected to server");
        } catch (IOException e) {
            System.out.println(e.getMessage());
            throw new RuntimeException(e);
        }

        this.vbox.heightProperty().addListener((observableValue, oldValue, newValue) -> this.scroll_pane.setVvalue((Double) newValue));
        Thread thread = new Thread(this.client);
        thread.setDaemon(true);
        thread.start();

        this.to_send_msg.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                sendText();
            }
        });

        this.button_send.setOnAction(actionEvent -> sendText());
    }

    private void sendText() {

        String msg2Send = this.to_send_msg.getText();

        if (msg2Send.isBlank()) {
            return;
        }

        HBox hBox = new HBox();
        hBox.setAlignment(Pos.CENTER_RIGHT);
        hBox.setPadding(new Insets(5, 5, 5, 10));

        Text text = new Text(msg2Send);
        TextFlow textFlow = new TextFlow(text);
        textFlow.setStyle("-fx-color: rgb(239, 242, 255); -fx-background-color: rgb(15, 125, 242); -fx-background-radius: 20px;");

        textFlow.setPadding(new Insets(5, 10, 5, 10));
        text.setFill(Color.color(0.934, 0.945, 0.996));

        hBox.getChildren().add(textFlow);
        this.vbox.getChildren().add(hBox);
        this.client.sendMsg(msg2Send);
        this.to_send_msg.clear();
    }

    public static void addLabel(String msgFromServer, VBox vbox) {

        HBox hBox = new HBox();
        hBox.setAlignment(Pos.CENTER_LEFT);
        hBox.setPadding(new Insets(5, 5, 5, 10));

        Text text = new Text(msgFromServer);
        TextFlow textFlow = new TextFlow(text);

        textFlow.setStyle("-fx-color: rgb(239, 242, 255); -fx-background-color: rgb(15, 125, 242); -fx-background-radius: 20px;");
        textFlow.setPadding(new Insets(5, 10, 5, 10));
        text.setFill(Color.color(0, 0, 0));

        hBox.getChildren().add(textFlow);
        Platform.runLater(() -> vbox.getChildren().add(hBox));
    }

    public static void setArguments(List<String> rawArgs) {
        args = rawArgs;
    }
}