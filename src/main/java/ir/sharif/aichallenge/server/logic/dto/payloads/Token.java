package ir.sharif.aichallenge.server.logic.dto.payloads;

import java.util.UUID;

public class Token {
    String access;

    public Token(String access) {
        this.access = access;
    }

    public Token() {
        // todo: no need to check token now!
        // just random
        this.access = UUID.randomUUID().toString();
    }

}
