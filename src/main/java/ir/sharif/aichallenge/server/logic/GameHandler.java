//package ir.sharif.aichallenge.server.logic;
//
//import ir.sharif.aichallenge.server.common.network.data.ClientMessageInfo;
//import ir.sharif.aichallenge.server.common.network.data.Message;
//import ir.sharif.aichallenge.server.engine.core.GameLogic;
//import ir.sharif.aichallenge.server.logic.model.Game;
//
//import java.util.List;
//import java.util.Map;
//
//public class GameHandler implements GameLogic {
//
//    private Game game = Game();
//
//    @Override
//    public int getClientsNum() {
//        return 0;
//    }
//
//    @Override
//    public boolean[] getActiveClients() {
//        return new boolean[0];
//    }
//
//    @Override
//    public long getClientResponseTimeout() {
//        return 0;
//    }
//
//    @Override
//    public long getTurnTimeout() {
//        return 0;
//    }
//
//    @Override
//    public void init() {
//
//    }
//
//    @Override
//    public Message getUIInitialMessage() {
//        return null;
//    }
//
//    @Override
//    public Message[] getClientInitialMessages() {
//        return new Message[0];
//    }
//
//    @Override
//    public void simulateEvents(Map<String, List<ClientMessageInfo>> events) {
//
//    }
//
//    @Override
//    public void generateOutputs() {
//
//    }
//
//    @Override
//    public Message getUIMessage() {
//        return null;
//    }
//
//    @Override
//    public Message getStatusMessage() {
//        return null;
//    }
//
//    @Override
//    public Message[] getClientMessages() {
//        return new Message[0];
//    }
//
//    @Override
//    public Message[] getClientEndMessages() {
//        return new Message[0];
//    }
//
//    @Override
//    public boolean isGameFinished() {
//        return false;
//    }
//
//    @Override
//    public void terminate() {
//
//    }
//}
