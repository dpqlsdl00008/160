package connector;

import connector.netty.packetCreator;
import connector.netty.recvOpcode;
import tools.data.LittleEndianAccessor;

public class connectorHandler {

    public static final void HandlePacket(final recvOpcode header, final LittleEndianAccessor slea, final connectorClient c) throws InterruptedException {
        switch (header) {
            case Pong: {
                c.Ping();
                break;
            }

            case Login: {
                String id = slea.readMapleAsciiString();
                String pw = slea.readMapleAsciiString();
                System.out.println("id: " + id + " pw: " + pw + "");
                int result = connectorWalker.Login(id, pw);
                if (result == 1) {
                    c.setId(id);
                    connectorWalker.setAlive(c.getId(), true);
                    connectorWalker.setIP(c.getId(), c.getIP());
                } else if (result == 3) {
                    c.closeSession(); //밴유저 내보내기
                }
                c.sendPacket(packetCreator.Login(result));
                break;
            }

            case Register: {
                String id = slea.readMapleAsciiString();
                String pw = slea.readMapleAsciiString();
                System.out.println("id : " + id + " pw : " + pw + "");
                int result = connectorWalker.AutoRegister(id, pw);
                if (result == 1) {
                    c.sendPacket(packetCreator.Register(1));
                } else if (result == 0) {
                    c.sendPacket(packetCreator.Register(0));
                }
                break;
            }

            default: {
                System.out.println("[UNHANDLED] Recv [" + header + "] found");
                break;
            }
        }
    }
}
