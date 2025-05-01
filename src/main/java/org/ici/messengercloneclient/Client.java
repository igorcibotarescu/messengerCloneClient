package org.ici.messengercloneclient;

import javafx.scene.layout.VBox;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.net.Socket;

public class Client implements Runnable {
    private static final Logger LOGGER = LogManager.getLogger(Client.class);
    private final Socket socket;
    private final BufferedReader bufferedReader;
    private final PrintWriter printWriter;
    private final String ipAddress;
    private final int port;
    private final VBox vbox;
    private final String name;

    public Client(Socket socket, VBox vbox, String name) {
        try {
            this.socket = socket;
            this.bufferedReader = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
            this.printWriter = new PrintWriter(this.socket.getOutputStream(), true);
            this.ipAddress = this.socket.getInetAddress().getHostAddress();
            this.port = this.socket.getPort();
            this.vbox = vbox;
            this.name = name;
            this.printWriter.println(name);
        } catch (IOException e) {
            LOGGER.error("Could not initialize client");
            this.closeResources();
            throw new RuntimeException();
        }
    }
    public String getIpAddress() {
        return this.ipAddress;
    }

    public int getPort() {
        return this.port;
    }
    public String getName() {
        return this.name;
    }

    public void closeResources() {
        try {
            if (this.socket != null) {
                this.socket.close();
            }
            if (this.printWriter != null) {
                this.printWriter.close();
            }
            if (this.bufferedReader != null) {
                this.bufferedReader.close();
            }
        } catch (IOException e) {
            LOGGER.error("Error when closing resources");
        }
    }

    public void sendMsg(String msg2Send) {
        if(this.printWriter != null) {
            this.printWriter.println(msg2Send);
        }
    }

    @Override
    public void run() {
        String receivedMessage;
        try {
            while ((receivedMessage = this.bufferedReader.readLine()) != null) {
                Controller.addLabel(receivedMessage, this.vbox);
            }
        } catch (IOException e) {
            closeResources();
        }
    }
}
