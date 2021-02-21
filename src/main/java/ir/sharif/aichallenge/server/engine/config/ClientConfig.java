package ir.sharif.aichallenge.server.engine.config;

/**
 * Copyright (C) 2016 Hadi
 */
public class ClientConfig {
    private static int clientCount = 0;

    private int id;
    private final StringParam name;
    private final StringParam token;

    public ClientConfig() {
        int num = clientCount++;
        name = new StringParam("Client" + num + "Name", "Client" + num);
        token = new StringParam("Client" + num + "Token", "00000000000000000000000000000000");
    }

    public int getID() {
        return id;
    }

    public void setID(int id) {
        this.id = id;
    }

    public String getName() {
        return name.getValue();
    }

    public String getToken() {
        return token.getValue();
    }
}
