/*
This file is part of the ZeroFusion MapleStory Server
Copyright (C) 2008 Patrick Huy <patrick.huy@frz.cc> 
Matthias Butz <matze@odinms.de>
Jan Christian Meyer <vimes@odinms.de>
ZeroFusion organized by "RMZero213" <RMZero213@hotmail.com>

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
package server.events;

import client.MapleCharacter;
import java.awt.Point;
import server.Timer.EventTimer;
import server.life.MapleLifeFactory;
import tools.packet.CField;
import tools.packet.CWvsContext;

public class MapleBig extends MapleEvent {


    private boolean finished = false;

    public MapleBig(final int channel, final MapleEventType type) {
	super(channel,type);
    }

    @Override
    public void finished(MapleCharacter chr) { //do nothing.
    }
    

    @Override
    public void onMapLoad(MapleCharacter chr) {
	super.onMapLoad(chr);
    }

    @Override
    public void reset() {
        super.reset();
      //  getMap(0).getPortal("join00").setPortalState(false);
    }

    @Override
    public void unreset() {
        super.unreset();
     //   getMap(0).getPortal("join00").setPortalState(true);
    }
    //apparently npc says 10 questions

    @Override
    public void startEvent() {
        reset();
        for (MapleCharacter chr : getMap(0).getCharactersThreadsafe()) {
             BigIn(chr); // 입장 
        }
        getMap(1).broadcastMessage(CWvsContext.serverNotice(5, "머임 시작임"));
        getMap(1).broadcastMessage(CField.getClock(300));
        getMap(1).broadcastMessage(CField.floatNotice("대박이야! 우리 모두 힘을 합쳐 대박 몬스터를 공격하도록해요!", 5120006, true));
        getMap(1).spawnMonsterOnGroundBelow(MapleLifeFactory.getMonster(9500517), new Point(-602, 3));
        EventTimer.getInstance().schedule(new Runnable() {

            @Override
            public void run() {

     
                    for (MapleCharacter chr : getMap(0).getCharactersThreadsafe()) {
                   /*     if (chr.getTeam() == (getMapleScore() > getStoryScore() ? 0 : 1)) {
                            chr.getClient().getSession().write(CField.showEffect("event/coconut/victory"));
                            chr.getClient().getSession().write(CField.playSound("Coconut/Victory"));
                        } else {
                            chr.getClient().getSession().write(CField.showEffect("event/coconut/lose"));
                            chr.getClient().getSession().write(CField.playSound("Coconut/Failed"));
                        }*/
                    }
                    warpOut();
                
            }
        }, 300000);
    }
    
    public void warpOut() {
        EventTimer.getInstance().schedule(new Runnable() {

            @Override
            public void run() {
                for (MapleCharacter chr : getMap(1).getCharactersThreadsafe()) {
		    BigOut(chr);
                }
                unreset();
            }
        }, 10000);
    }
}
