package ir.sharif.aichallenge.server.common.network.data;

public class MessageTypes {
    // Server messages
    public static final String INIT = "3"; // connection established, send game config
    public static final String GAME_STATUS = "4";
    public static final String YOUR_TURN = "5";

    // Client messages
    public static final String GET_GAME_STATUS = "0";
    public static final String ACTION = "1";
    public static final String SEND_MESSAGE = "2";
    public static final String END_TURN = "6";

    // old Types
    // public static final String TURN = "turn";
    public static final String SHUTDOWN = "shutdown";

    public static final String TOKEN = "token";
    public static final String PICK = "pick";
    public static final String PUT_UNIT = "putUnit";
    public static final String CAST_SPELL = "castSpell";
    public static final String UPGRADE_RANGE = "rangeUpgrade";
    public static final String UPGRADE_DAMAGE = "damageUpgrade";
}
