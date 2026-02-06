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
package server.maps;

import java.awt.Point;

import client.MapleCharacter;
import handling.channel.ChannelServer;
import handling.world.World;
import server.MapleItemInformationProvider;
import server.Randomizer;
import server.Timer.EventTimer;
import server.life.MapleLifeFactory;
import tools.packet.CField;
import tools.packet.CWvsContext;

public class AramiaFireWorks { 

    //KEG_ID, MAX_KEGS 플로넥스꺼로 변경
    public final static int KEG_ID = 4021036, SUN_ID = 4001246, DEC_ID = 4001473;
    public final static int MAX_KEGS = 1000, MAX_SUN = 3000, MAX_DEC = 3600;
    private short kegs = MAX_KEGS / 6;
    private short sunshines = 0; //start at 1/6 then go from that
    private short decorations = MAX_DEC / 6;
    public static final int[] arrayMob = {9500168, 9500169, 9500170, 9500171, 9500173,
        9500174, 9500175, 9500176, 9500170, 9500171, 9500172, 9500173, 9500174, 9500175/*,
        9400569*/};
    private static final int[] arrayX = {2100, 2605, 1800, 2600, 3120, 2700, 2320, 2062,
        2800, 3100, 2300, 2840, 2700, 2320, 1950};
    private static final int[] arrayY = {574, 364, 574, 316, 574, 574, 403, 364, 574, 574,
        403, 574, 574, 403, 574};
    private static final int[] array_X = {-1006,-956,-906,-856,-806,-756,-706,-656,-606,-556,-506,-456,-406,-356,-306,};
    
    private static final int[] array_Y = {34,34,34,34,34,34,34,34,34,34,34,34,34,34,34};
    
 /*   private static final int[] array_X = {720, 180, 630, 270, 360, 540, 450, 142,
        142, 218, 772, 810, 848, 232, 308, 142};
    맵이 어쩌피 운지함
    private static final int[] array_Y = {1234, 1234, 1174, 1234, 1174, 1174, 1174, 1260,
        1234, 1234, 1234, 1234, 1234, 1114, 1114, 1140};*/
    
    private static final int flake_Y = 149;
    
    // 베아트리스 팩에서는 아르미의 폭죽이 사용되지 않으므로, 플로넥스꺼로 바꿔도 무관함.

    public final void giveKegs(final MapleCharacter c, final int kegs) {
        this.kegs += kegs;
        if (this.kegs >= MAX_KEGS) {
            this.kegs = 0;
            broadcastEvent(c);
        }
    }

    private final void broadcastServer(final MapleCharacter c, final int itemid) {
        World.Broadcast.broadcastMessage(CWvsContext.serverNotice(6, itemid, "<Channel " + c.getClient().getChannel() + "> " + c.getMap().getMapName() + " : The amount of {" + MapleItemInformationProvider.getInstance().getName(itemid) + "} has reached the limit!"));
    }

    public final short getKegsPercentage() {
        return (short) ((kegs / MAX_KEGS) * 10000);
    }
    
    private final void broadcastEvent(final MapleCharacter c) {
        broadcastServer(c, KEG_ID);
        // Henesys Park
        EventTimer.getInstance().schedule(new Runnable() {

            @Override
            public final void run() {
                startEvent(c.getClient().getChannelServer().getMapFactory().getMap(123456789)); //서버 광장 코드
            }
        }, 10000);
    }

    private final void startEvent(final MapleMap map) {
        map.floatNotice("??? :  광장을 습격해보자구~!", 5121010, false);

        EventTimer.getInstance().schedule(new Runnable() {

            @Override
            public final void run() {
                spawnMonster(map);
            }
        }, 5000);
    }

    private final void spawnMonster(final MapleMap map) {
        Point pos;

        for (int i = 0; i < arrayMob.length; i++) {
            pos = new Point(arrayX[i], arrayY[i]);
            map.spawnMonsterOnGroundBelow(MapleLifeFactory.getMonster(arrayMob[i]), pos);
        }
    }
    
    public final static void broadcastStarEvent() {
        // Henesys Park
        
        ChannelServer.getInstance(1).getMapFactory().getMap(910022000).floatNotice("메이플 라이징 스타 선데이 콘서트가 시작됩니다.", 5120008, false); 
        ChannelServer.getInstance(1).setEventstatchr(Randomizer.nextInt(2));
        World.Broadcast.broadcastMessage(CWvsContext.serverNotice(6, "채널 1에서 라이징스타 공연이 시작될려고 합니다! 현재 메인은 "+(ChannelServer.getInstance(1).Eventstatchr() == 0 ? "시그너스" : ChannelServer.getInstance(1).Eventstatchr() == 1 ? "카이린" : "리린") + " 입니다! ")); // 전체 공지
        EventTimer.getInstance().schedule(new Runnable() {
            @Override
            public final void run() {
                startStarEvent(ChannelServer.getInstance(1).getMapFactory().getMap(910022000)); // c.getClient().getChannelServer().getMapFactory().getMap(910022000)
                
            }
        }, 30000);
    }

    public final static void startStarEvent(final MapleMap map) {
        map.floatNotice("자신이 응원하는 라이징 스타에게 보상을 받아가세요! 현재 메인은 "+(ChannelServer.getInstance(1).Eventstatchr() == 0 ? "시그너스" : ChannelServer.getInstance(1).Eventstatchr() == 1 ? "카이린" : "리린") + " 입니다 ", 5120008, false); 
        map.broadcastMessage(CField.EffectPacket.ShowWZEffect("Effect/BasicEff.img/Flame/SquibEffect")); // 소리만 작동함
        ChannelServer.getInstance(1).setEventstat(true);
        EventTimer.getInstance().schedule(new Runnable() {

            @Override
            public final void run() {
            //    spawnMonster(map);
                   
                   map.floatNotice("메이플 라이징 스타의 공연이 끝났습니다! ", 5120008, false); 
                   ChannelServer.getInstance(1).setEventstat(false);
            }
        }, 5 * 60000);
    }
    
    
//    private final void spawnMonster(final MapleMap map) {
//        Point pos;
//
//        for (int i = 0; i < arrayMob.length; i++) {
//            pos = new Point(arrayX[i], arrayY[i]);
//            map.spawnMonsterOnGroundBelow(MapleLifeFactory.getMonster(arrayMob[i]), pos);
//        }
//    }

    public final void giveSuns(final MapleCharacter c, final int kegs) {
        this.sunshines += kegs;
        //have to broadcast a Reactor?
        final MapleMap map = c.getClient().getChannelServer().getMapFactory().getMap(910000000);
        //final MapleReactor reactor = map.getReactorByName("mapleTree");
        final MapleReactor reactor = c.getClient().getPlayer().getMap().getReactorById(9702000);
        int a = 0;
     //   System.out.print("노무 : " + this.sunshines);
        if (this.sunshines >= (short) 0 && this.sunshines < (short) 500) { // 0단계
            reactor.setState((byte) 0);
            reactor.setTimerActive(false);
            map.broadcastMessage(CField.triggerReactor(reactor, reactor.getState()));
        } else if (this.sunshines >= (short) 500 && this.sunshines < (short) 1000) { // 1단계
            reactor.setState((byte) 1);
            reactor.setTimerActive(false);
            map.broadcastMessage(CField.triggerReactor(reactor, reactor.getState()));
        } else if (this.sunshines >= (short) 1000 && this.sunshines < (short) 1500) { // 2단계
            reactor.setState((byte) 2);
            reactor.setTimerActive(false);
            map.broadcastMessage(CField.triggerReactor(reactor, reactor.getState()));
        } else if (this.sunshines >= (short) 1500 && this.sunshines < (short) 2000) { // 3단계
            reactor.setState((byte) 3);
            reactor.setTimerActive(false);
            map.broadcastMessage(CField.triggerReactor(reactor, reactor.getState()));
        } else if (this.sunshines >= (short) 2000 && this.sunshines < (short) 2500) { // 4단계
            reactor.setState((byte) 4);
            reactor.setTimerActive(false);
            map.broadcastMessage(CField.triggerReactor(reactor, reactor.getState()));
        } else if (this.sunshines > 2499) {
             //back to state 0
            this.sunshines = 0;
            broadcastSun(c);
        }
        
      //  c.getPlayer().getMap().setReactorState(Byte.parseByte(splitted[1]));
       /* for (int gogo = kegs + (MAX_SUN / 6); gogo > 0; gogo -= (MAX_SUN / 6)) {
            switch (reactor.getState()) {
                case 0: //first state
                case 1: //first state
                case 2: //first state
                case 3: //first state
                case 4: //first state
                    if (this.sunshines >= (MAX_SUN / 6) * (2 + reactor.getState())) {
                        reactor.setState((byte) (reactor.getState() + 1));
                        reactor.setTimerActive(false);
                        map.broadcastMessage(CField.triggerReactor(reactor, reactor.getState()));
                    }
                    break;
                default:
                    if (this.sunshines >= (MAX_SUN / 6)) {
                        map.resetReactors(); //back to state 0
                    }
                    break;
            }
        }*/
     /*   if (this.sunshines >= MAX_SUN) {
            this.sunshines = 0;
            broadcastSun(c);
        }*/
    }

    public final short getSunsPercentage() {
        return (short) ((sunshines / 2500) * 10000);
    }

    private final void broadcastSun(final MapleCharacter c) {
        broadcastServer(c, SUN_ID);
        // Henesys Park
       EventTimer.getInstance().schedule(new Runnable() {

            @Override
            public final void run() {
                startSun(c.getClient().getChannelServer().getMapFactory().getMap(910000000));
            }
        }, 10000);
    }

    private final void startSun(final MapleMap map) {
        map.floatNotice("The tree is bursting with sunshine!", 5121010, false);
        for (int i = 0; i < 3; i++) {
            EventTimer.getInstance().schedule(new Runnable() {

                @Override
                public final void run() {
                    spawnItem(map);
                }
            }, 5000 + (i * 10000));
        }
    }

    private final void spawnItem(final MapleMap map) {
        Point pos;

        for (int i = 0; i < Randomizer.nextInt(5) + 10; i++) {
            pos = new Point(array_X[i], array_Y[i]);
	    int itemId = 4001246;
	    switch(Randomizer.nextInt(15)) {
		case 0:
		case 1:
		    itemId = 3010141;
		    break;
		case 2:
		    itemId = 3010146;
		    break;
		case 3:
		case 4:
		    itemId = 3010025;
		    break;
	    }
            map.spawnAutoDrop(itemId, pos);
        }
        map.resetReactors(); //나무 다시
      
    }

    public final void giveDecs(final MapleCharacter c, final int kegs) {
        this.decorations += kegs;
        //have to broadcast a Reactor?
        final MapleMap map = c.getClient().getChannelServer().getMapFactory().getMap(555000000);
        final MapleReactor reactor = map.getReactorByName("XmasTree");
        for (int gogo = kegs + (MAX_DEC / 6); gogo > 0; gogo -= (MAX_DEC / 6)) {
            switch (reactor.getState()) {
                case 0: //first state
                case 1: //first state
                case 2: //first state
                case 3: //first state
                case 4: //first state
                    if (this.decorations >= (MAX_DEC / 6) * (2 + reactor.getState())) {
                        reactor.setState((byte) (reactor.getState() + 1));
                        reactor.setTimerActive(false);
                        map.broadcastMessage(CField.triggerReactor(reactor, reactor.getState()));
                    }
                    break;
                default:
                    if (this.decorations >= MAX_DEC / 6) {
                        map.resetReactors(); //back to state 0
                    }
                    break;
            }
        }
        /*if (this.decorations >= MAX_DEC) {
            this.decorations = 0;
            broadcastDec(c);
        }*/
    }

    public final short getDecsPercentage() {
        return (short) ((decorations / MAX_DEC) * 10000);
    }

    private final void broadcastDec(final MapleCharacter c) {
        broadcastServer(c, DEC_ID);
        EventTimer.getInstance().schedule(new Runnable() {

            @Override
            public final void run() {
                startDec(c.getClient().getChannelServer().getMapFactory().getMap(555000000));
            }
        }, 10000); //no msg
    }

    private final void startDec(final MapleMap map) {
        map.floatNotice("The tree is bursting with snow!", 5120000, false);
        for (int i = 0; i < 3; i++) {
            EventTimer.getInstance().schedule(new Runnable() {

                @Override
                public final void run() {
                    spawnDec(map);
                }
            }, 5000 + (i * 10000));
        }
    }

    private final void spawnDec(final MapleMap map) {
        Point pos;

        for (int i = 0; i < Randomizer.nextInt(10) + 40; i++) {
            pos = new Point(Randomizer.nextInt(800) - 400, flake_Y);
            map.spawnAutoDrop(Randomizer.nextInt(15) == 1 ? 4310012 : 4310011, pos);
        }
    }
}
