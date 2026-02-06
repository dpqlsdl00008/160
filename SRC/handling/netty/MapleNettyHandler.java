/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package handling.netty;

import client.MapleClient;
import client.InHeader;
import constants.GameConstants;
import constants.ServerConstants;
import handling.RecvPacketOpcode;
import static handling.ServerType.*;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleStateEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import server.Randomizer;
import tools.MapleKMSEncryption;
import tools.Pair;
import tools.data.LittleEndianAccessor;
import tools.packet.CLogin;

/**
 *
 * @author csproj
 */
public class MapleNettyHandler extends SimpleChannelInboundHandler<LittleEndianAccessor> {

    private final int serverType;
    private final int channel;
    private final List<String> BlockedIP;
    private final Map<String, Pair<Long, Byte>> tracker;
    // private boolean norxoriv = !ServerConstants.Use_Fixed_IV;

    public MapleNettyHandler(int serverType, int channel) {
        this.BlockedIP = new ArrayList<String>();
        this.tracker = new ConcurrentHashMap<String, Pair<Long, Byte>>();
        this.serverType = serverType;
        this.channel = channel;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        final String address = ctx.channel().remoteAddress().toString().split(":")[0];
        if (this.BlockedIP.contains(address)) {
            ctx.close();
            return;
        }
        final Pair<Long, Byte> track = this.tracker.get(address);
        byte count;
        if (track == null) {
            count = 1;
        } else {
            count = track.right;
            final long difference = System.currentTimeMillis() - track.left;
            if (difference < 2000L) {
                ++count;
            } else if (difference > 20000L) {
                count = 1;
            }
            if (count >= 10) {
                this.BlockedIP.add(address);
                this.tracker.remove(address);
                ctx.close();
                return;
            }
        }
        // IV used to decrypt packets from client.
        switch (serverType) {
            case LOGIN:
                //System.out.println("[알림] " + address + " 에서 로그인 서버로 연결을 시도했습니다.");
                break;
            case CHANNEL:
                //System.out.println("[알림] " + address + " 에서 채널 서버로 연결을 시도했습니다.");
                break;
            case CASHSHOP:
                //System.out.println("[알림] " + address + " 에서 캐시샵 서버로 연결을 시도했습니다.");
                break;

            default:
        }
        final byte serverRecv[] = {(byte) 0x22, (byte) 0x3F, (byte) 0x37, (byte) Randomizer.nextInt(255)};
        final byte serverSend[] = {(byte) 0xC9, (byte) 0x3A, (byte) 0x27, (byte) Randomizer.nextInt(255)};
        final byte ivRecv[] = serverRecv;
        final byte ivSend[] = serverSend;
        final MapleClient client = new MapleClient(
                ctx.channel(),
                channel,
                new MapleKMSEncryption(ivSend, (short) (0xFFFF - ServerConstants.MAPLE_VERSION)), // Sent Cypher
                new MapleKMSEncryption(ivRecv, ServerConstants.MAPLE_VERSION));
        client.setChannel(channel);

        ctx.writeAndFlush(CLogin.getHello(ivSend, ivRecv));
        ctx.channel().attr(MapleClient.CLIENTKEY).set(client);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {

        MapleClient client = ctx.channel().attr(MapleClient.CLIENTKEY).get();

            /*if (client != null) {
            System.out.println(client.getIp() + " disconnected.");
            client.disconnect(true, false);
           // ctx.channel().eventLoop().shutdown(); // 아들 스레드 초기화
           // ctx.channel().eventLoop().parent().shutdown(); // 모친 스레드 초기화
        }*/
        /*    ctx.channel().attr(MapleClient.CLIENTKEY).set(null);
        if (ctx.channel().eventLoop() != null) {
            ctx.channel().eventLoop().shutdownNow(); // 아들 스레드 초기화
        }*/
        if (client != null) {
            try {
                //System.out.println(client.getIp() + " disconnected.");
                client.disconnect(true, false);
            } catch (Throwable t) {
                //    Logger.log(LogType.ERROR, LogFile.ACCOUNT_STUCK, t);
            } finally {
                ctx.close();
                ctx.channel().attr(MapleClient.CLIENTKEY).set(null);
                // client.empty();
                /*if (ctx.channel().eventLoop() != null) {
                    ctx.channel().eventLoop().shutdownNow(); // 아들 스레드 초기화
                }*/
            }
        }
        super.channelUnregistered(ctx);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        //MapleClient client = ctx.channel().attr(MapleClient.CLIENTKEY).get();
        if (cause instanceof IOException) { // 이게 강제로 팅겼을때 부분인데 여기에도 처리한번해보삼 
            System.out.print("Client forcibly closed the game."); // 맞징??
            ///if (ctx.channel().eventLoop() != null) {
            //   ctx.channel().eventLoop().shutdown(); // 아들 스레드 초기화
            // }
        } else {
            cause.printStackTrace();
        }
        /*client.disconnect(true, false);
        ctx.close();
        ctx.channel().attr(MapleClient.CLIENTKEY).set(null);
        if (ctx.channel().eventLoop() != null) {
            ctx.channel().eventLoop().shutdownNow(); // 아들 스레드 초기화
        }
        super.exceptionCaught(ctx, cause);// ?*/
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent e = (IdleStateEvent) evt;
        }

    }

    @Override
    /*   protected void channelRead0(ChannelHandlerContext ctx, LittleEndianAccessor slea) throws Exception {
        final MapleClient c = (MapleClient) ctx.channel().attr(MapleClient.CLIENTKEY).get();



        final short header_num = slea.readShort();
        try {
          PacketHandler.handlePacket(c, channel == -10, header_num, slea);
          System.out.print("반응4444");
        } catch (Throwable ex) {
            FileoutputUtil.outputFileError(FileoutputUtil.PacketEx_Log, ex);
            FileoutputUtil.log(FileoutputUtil.PacketEx_Log, "Packet: " + header_num + "\n" + slea.toString(true));
        }
    }*/

    protected void channelRead0(ChannelHandlerContext ctx, LittleEndianAccessor slea) throws Exception {
        final MapleClient c = (MapleClient) ctx.channel().attr(MapleClient.CLIENTKEY).get();

        final short header_num = slea.readShort();
        if (header_num != RecvPacketOpcode.MOVE_PLAYER.getValue()
                && header_num != RecvPacketOpcode.MOVE_LIFE.getValue()
                && header_num != RecvPacketOpcode.QUEST_ACTION.getValue()
                && header_num != RecvPacketOpcode.NPC_ACTION.getValue()
                && header_num != RecvPacketOpcode.AUTO_AGGRO.getValue()
                && header_num != RecvPacketOpcode.TAKE_DAMAGE.getValue()
                && header_num != RecvPacketOpcode.CHANGE_MAP_SPECIAL.getValue()
                //&& header_num != RecvPacketOpcode.MOVE_PET.getValue()
                && header_num != RecvPacketOpcode.HEAL_OVER_TIME.getValue()) {
                //System.out.println("Recv " + header_num + " : " + RecvPacketOpcode.getOpcodeName(header_num) + " : " + slea.toString());
        }
        if (GameConstants.showPacket) {
            //System.out.println("[R] " + slea.toString());
            //System.out.println();
        }
        for (final RecvPacketOpcode recv : RecvPacketOpcode.values()) {
            if (recv.getValue() == header_num) {
                try {
                    //     MapleServerHandler.handlePacket(recv, slea, c, serverType.equals(ServerType.CASHSHOP));
                    InHeader.handlePacket(c, channel == -10, header_num, slea);
                    //     System.out.print("반응4444");
                } catch (Exception ex) {

                }
                return;
            }
        }
    }
}
