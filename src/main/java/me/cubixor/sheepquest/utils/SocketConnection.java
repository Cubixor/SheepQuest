package me.cubixor.sheepquest.utils;

import java.io.ObjectOutputStream;
import java.net.Socket;

public class SocketConnection {

    private final Socket socket;
    private final ObjectOutputStream outputStream;

    public SocketConnection(Socket socket, ObjectOutputStream outputStream) {
        this.socket = socket;
        this.outputStream = outputStream;
    }

    public Socket getSocket() {
        return socket;
    }

    public ObjectOutputStream getOutputStream() {
        return outputStream;
    }

    @Override
    public String toString() {
        return "SocketConnection{" +
                "socket=" + socket +
                ", outputStream=" + outputStream +
                '}';
    }
}
