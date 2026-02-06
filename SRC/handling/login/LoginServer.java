/*
 This file is part of the OdinMS Maple Story Server
 Copyright (C) 2008 ~ 2010 Patrick Huy <patrick.huy@frz.cc> 
 Matthias Butz <matze@odinms.de>
 Jan Christian Meyer <vimes@odinms.de>

 This program is free software: you can redistribute it and/or modify
 it under the terms of the GNU Affero General Public License version 3
 as published by the Free Software Foundation. You may not use, modify
 or distribute this program under any other version of the
 GNU Affero General Public License.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU Affero General Public License for more details.

 You should have received a copy of the GNU Affero General Public License
 along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package handling.login;

import handling.ServerType;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.logging.Logger;
import handling.netty.MapleNettyDecoder;
import handling.netty.MapleNettyEncoder;
import handling.netty.MapleNettyHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import server.ServerProperties;
import tools.Triple;

public class LoginServer {

    private static ServerBootstrap bootstrap;
    public static int PORT = 5656;
    private static Logger _log = Logger.getLogger(LoginServer.class.getName());
    private static Map<Integer, Integer> load = new HashMap<Integer, Integer>();
    private static String serverName, eventMessage;
    private static byte flag;
    private static int maxCharacters, userLimit, usersOn = 0;
    private static boolean finishedShutdown = true, adminOnly = false;

    private static HashMap<Integer, Triple<String, String, Integer>> loginAuth = new HashMap<Integer, Triple<String, String, Integer>>();
    private static HashSet<String> loginIPAuth = new HashSet<String>();
    
     public static void putLoginAuth(int chrid, String ip, String tempIP, int channel) {
        loginAuth.put(chrid, new Triple<String, String, Integer>(ip, tempIP, channel));
        loginIPAuth.add(ip);
    }

    public static Triple<String, String, Integer> getLoginAuth(int chrid) {
        return loginAuth.remove(chrid);
    }

    public static boolean containsIPAuth(String ip) {
        return loginIPAuth.contains(ip);
    }

    public static void removeIPAuth(String ip) {
        loginIPAuth.remove(ip);
    }

    public static void addIPAuth(String ip) {
        loginIPAuth.add(ip);
    }


    public static final void addChannel(final int channel) {
        load.put(channel, 0);
    }

    public static final void removeChannel(final int channel) {
        load.remove(channel);
    }

    public static final void run_startup_configurations() throws Exception {
        //System.out.print("Login Server Initializing.. Retriving Login Server Properties..");
        userLimit = Integer.parseInt(ServerProperties.getProperty("gameserver.ratio.high_player_count.disabling"));
        serverName = ServerProperties.getProperty("gameserver.name");
        eventMessage = ServerProperties.getProperty("gameserver.eventmessage");
        flag = Byte.parseByte(ServerProperties.getProperty("gameserver.flag"));
        maxCharacters = Integer.parseInt(ServerProperties.getProperty("gameserver.maxcharacters"));
        
        PORT = 8484; // 11601 // 8484
        System.out.println("\r\n");
        System.out.println("<Login Server Status>");
        System.out.println("Max User Limit             : " + userLimit);
        System.out.println("ServerName                 : " + serverName);
        System.out.println("EventMessage               : " + eventMessage);
        System.out.println("Flag                 	   : "
                + (flag == 0 ? "Normal" : flag == 1 ? "Hot" : "New"));
       // thread = new LoginServerThread();
        // thread._serverSocket = new ServerSocket(PORT);
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {


            bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast("decoder", new MapleNettyDecoder());
                            ch.pipeline().addLast("encoder", new MapleNettyEncoder());
                            ch.pipeline().addLast("idleStateHandler", new IdleStateHandler(60, 30, 0));
                            ch.pipeline().addLast("handler", new MapleNettyHandler(ServerType.LOGIN, -1));
                        }
                    })
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .childOption(ChannelOption.SO_KEEPALIVE, true);
            ChannelFuture f = bootstrap.bind(PORT).sync(); // (7)
            /* 소켓 설정 종료 */
            System.out.println("[Success] Login Server Port : " + PORT);
        } catch (InterruptedException e) {
            System.err.println("[Fail] Login Server Port : " + PORT);
        }


        // ByteBuffer.setUseDirectBuffers(false);
        // ByteBuffer.setAllocator(new SimpleByteBufferAllocator());

    }

    public static final void shutdown() {
        if (finishedShutdown) {
            return;
        }
        System.out.println("Shutting down login...");
        finishedShutdown = true; // nothing. lol
    }

    public static final String getServerName() {
        return serverName;
    }

    public static final String getTrueServerName() {
        return getServerName();
    }

    public static final String getEventMessage() {
        return eventMessage;
    }

    public static final byte getFlag() {
        return flag;
    }

    public static final int getMaxCharacters() {
        return maxCharacters;
    }

    public static final Map<Integer, Integer> getLoad() {
        return load;
    }

    public static void setLoad(final Map<Integer, Integer> load_,
            final int usersOn_) {
        load = load_;
        usersOn = usersOn_;
    }

    public static final void setEventMessage(final String newMessage) {
        eventMessage = newMessage;
    }

    public static final void setFlag(final byte newflag) {
        flag = newflag;
    }

    public static final int getUserLimit() {
        return userLimit;
    }

    public static final int getUsersOn() {
        return usersOn;
    }

    public static final void setUserLimit(final int newLimit) {
        userLimit = newLimit;
    }

    public static final boolean isAdminOnly() {
        return adminOnly;
    }

    public static final boolean isShutdown() {
        return finishedShutdown;
    }

    public static final void setOn() {
        finishedShutdown = false;
    }
}
