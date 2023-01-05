package com.corundumstudio.socketio;

import com.corundumstudio.socketio.listener.ConnectListener;
import com.corundumstudio.socketio.listener.DataListener;
import com.corundumstudio.socketio.listener.DisconnectListener;
import com.corundumstudio.socketio.listener.PongListener;

public class SocketServerDemo {

    public static void main(String[] args) throws InterruptedException {
        Configuration config = new Configuration();
        config.setHostname("localhost");
        config.setPort(9998);
        config.setMaxFramePayloadLength(1024 * 1024);
        config.setMaxHttpContentLength(1024 * 1024);

        final SocketIOServer server = new SocketIOServer(config);
        server.addEventListener("conn", String.class, new DataListener<String>() {
            @Override
            public void onData(SocketIOClient client, String data, AckRequest ackRequest) throws Exception {
                System.out.println("conn");
//                data = "connection";
//                client.sendEvent("connection event", data);
            }
        });

        server.addEventListener("disconn", String.class, new DataListener<String>() {
            @Override
            public void onData(SocketIOClient client, String data, AckRequest ackRequest) throws Exception {
                System.out.println("disconn");
                client.disconnect();
            }
        });

        server.addConnectListener(new ConnectListener() {
            @Override
            public void onConnect(SocketIOClient client) {
                System.out.println(client.getSessionId() + " online...");
            }
        });

        server.addPongListener(new PongListener() {
            @Override
            public void onPong(SocketIOClient client) {
                System.out.println(client.getSessionId() + " pong...");
            }
        });

        server.addDisconnectListener(new DisconnectListener() {
            @Override
            public void onDisconnect(SocketIOClient client) {
                System.out.println(client.getSessionId()+" offline");
            }
        });

        server.addEventListener("ackevent1", ChatObject.class, new DataListener<ChatObject>() {
            @Override
            public void onData(SocketIOClient client, ChatObject data, AckRequest ackRequest) {
                System.out.println(data.getMessage());
//                server.getBroadcastOperations().sendEvent("ackevent2", data);
                server.getAllClients().forEach(c -> c.sendEvent("ackevent2",data));
            }
        });
//        server.addEventListener("ackevent2", ChatObject.class, new

        server.start();

        Thread.sleep(Integer.MAX_VALUE);

        server.stop();
    }
}
