package kk.socket.client.executions;

import kk.socket.emitter.Emitter;
import kk.socket.client.IO;
import kk.socket.client.Socket;

import java.net.URISyntaxException;

public class Connection {

    public static void main(String[] args) throws URISyntaxException {
        IO.Options options = new IO.Options();
        options.forceNew = true;
        final Socket socket = IO.socket("http://localhost:" + System.getenv("PORT"), options);
        socket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                System.out.println("connect");
                socket.close();
            }
        });
        socket.open();
    }
}
