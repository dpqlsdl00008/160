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
package handling.cashshop;

import handling.ServerType;

import handling.channel.PlayerStorage;
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
import java.io.IOException;

import server.ServerProperties;

public class CashShopServer {

    private static String ip;
    private static String onlyip;    
    private static short PORT = 8600;
    private static PlayerStorage players;
    private static boolean finishedShutdown = false;
    private static ServerBootstrap bootstrap;

    public static final void run_startup_configurations() throws IOException {
        PORT = Short.parseShort(ServerProperties.getProperty("cashshopserver.port"));
        ip = ServerProperties.getProperty("cashshopserver.interface") + ":" + PORT;
        onlyip = ServerProperties.getProperty("cashshopserver.interface");
        players = new PlayerStorage(-10);
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
                            ch.pipeline().addLast("handler", new MapleNettyHandler(ServerType.CASHSHOP, -10));
                        }
                    })
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .childOption(ChannelOption.SO_KEEPALIVE, true);
            ChannelFuture f = bootstrap.bind(PORT).sync();

          //  Start.out.println("Port " + PORT + " Opened.");

        } catch (InterruptedException e) {
          //  e.printStackTrace(Start.out);
        }   
    }

    public static final String getIP() {
        return ip;
    }
    public static final String getonlyIP() {
        return onlyip;
    }
    public static final PlayerStorage getPlayerStorage() {
        return players;
    }

    public static final void shutdown() {
        if (finishedShutdown) {
            return;
        }
        System.out.println("Saving all connected clients (CS)...");
        players.disconnectAll();
        System.out.println("Shutting down CS...");
	//acceptor.unbindAll();
        finishedShutdown = true;
    }

    public static boolean isShutdown() {
	return finishedShutdown;
    }
}
