package ir.sharif.aichallenge.server.common.network.data;

public class MessageTypes {
    //Server messages
    public static final String INIT = "init";
    public static final String TURN = "turn";
    public static final String SHUTDOWN = "shutdown";


    //Client messages
    public static final String TOKEN = "token";

    public static final String PICK = "pick";

    public static final String PUT_UNIT = "putUnit";
    public static final String CAST_SPELL = "castSpell";
    public static final String UPGRADE_RANGE = "rangeUpgrade";
    public static final String UPGRADE_DAMAGE = "damageUpgrade";

    public static final String END_TURN = "endTurn";
}
