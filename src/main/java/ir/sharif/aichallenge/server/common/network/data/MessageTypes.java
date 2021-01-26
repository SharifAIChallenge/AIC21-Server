package ir.sharif.aichallenge.server.common.network.data;

public class MessageTypes {
    // Server messages
    public static final String INIT = "2";              // connection established
    public static final String GAME_STATUS = "4";
    public static final String YOUR_TURN = "6";
    private static final String ACTION_ERROR = "8";


//    public static final String TURN = "turn";
    public static final String SHUTDOWN = "shutdown";


    // Client messages
    public static final String GET_GAME_STATUS = "3";
    public static final String ACTION = "5";


//    public static final String TOKEN = "token";
//    public static final String PICK = "pick";
//    public static final String PUT_UNIT = "putUnit";
//    public static final String CAST_SPELL = "castSpell";
//    public static final String UPGRADE_RANGE = "rangeUpgrade";
//    public static final String UPGRADE_DAMAGE = "damageUpgrade";
//    public static final String END_TURN = "endTurn";
}
