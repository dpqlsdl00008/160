package connector.netty;

public enum recvOpcode {
    Pong(0x00),
    Login(0x01),
    Register(0x02);

    private int code = -2;

    public final int getValue() {
        return code;
    }

    private recvOpcode(int code) {
        this.code = code;
    }
}
