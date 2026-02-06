package connector.netty;

public enum sendOpcode {
    PING(0x00),
    LOGIN_RESULT(0x01),
    REGISTER_RESULT(0x02);

    private int code = -2;

    public final int getValue() {
        return code;
    }

    private sendOpcode(int code) {
        this.code = code;
    }
}
