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
package handling.world.family;

import client.CharacterTemporaryStat;
import client.MapleCharacter;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.concurrent.ScheduledFuture;
import server.MapleItemInformationProvider;
import server.MapleStatEffect;
import server.MapleStatEffect.CancelEffectAction;
import server.Timer.BuffTimer;
import tools.Pair;
import tools.packet.CWvsContext.BuffPacket;

public enum MapleFamilyBuff {
    Teleport("패밀리원에게 바로 이동", "[대상] 자기 자신\n[효과] 원하는 패밀리원이 있는 장소로 바로 이동한다.", 0, 0, 0, 300, 190000),
    Summon("패밀리원 바로 소환", "[대상] 패밀리원 1명\n[효과] 원하는 패밀리원을 자신이 있는 맵으로 바로 소환한다.", 1, 0, 0, 500, 190001),
    Drop_12_15("나만의 드롭률 1.2배 (15분)", "[대상] 자기 자신\n[지속 시간] 15분.\n[효과] 몬스터 사냥 시 드롭률이#c1.2#배로 향상.\n※ 나만의 드롭률 효과나 경험치 이벤트와 겹칠 경우 효력이 무시된다.", 3, 15, 120, 700, 190002),
    EXP_12_15("나만의 경험치 1.2배 (15분)", "[대상] 자기 자신\n[지속 시간] 15분.\n[효과] 몬스터 사냥 시 얻는 경험치가#c1.2#배로 향상.\n※ 나만의 경험치 효과나 드롭률 이벤트와 겹칠 경우 효력이 무시된다.", 2, 15, 120, 800, 190003),
    Bonding("패밀리원의 단결 (30분)", "[발동 조건] 가계도에 보이는 하위 패밀리원이 6명 이상 로그인\n[지속 시간] 30분.\n[효과] 드롭률과 경험치를 1.5배로 향상. ※ 나만의 드롭률, 나만의 경험치 효과나 다른 경험치, 드롭률 이벤트와 겹칠 경우 효력이 무시된다.", 4, 30, 150, 3000, 190006);
    public String name, desc;
    public int rep, type, questID, duration, effect;
    public EnumMap<CharacterTemporaryStat, Integer> effects;

    private MapleFamilyBuff(String name, String desc, int type, int duration, int effect, int rep, int questID) {
        this.name = name;
        this.desc = desc;
        this.rep = rep;
        this.type = type;
        this.questID = questID;
        this.duration = duration;
        this.effect = effect;
        setEffects();
    }

    public int getEffectId() {
        switch (type) {
            case 2: //drop
                return 2450018;
            case 3: //exp
                return 2022694;
        }
        return 2022332; //custom
    }

    public final void setEffects() {
        this.effects = new EnumMap<>(CharacterTemporaryStat.class);
        switch (type) {
            case 2:
                effects.put(CharacterTemporaryStat.DropUpByItem, effect);
                effects.put(CharacterTemporaryStat.MesoUpByItem, effect);
                break;
            case 3:
                effects.put(CharacterTemporaryStat.ExpBuffRate, effect);
                break;
            case 4: //both
                effects.put(CharacterTemporaryStat.ExpBuffRate, effect);
                effects.put(CharacterTemporaryStat.DropUpByItem, effect);
                effects.put(CharacterTemporaryStat.MesoUpByItem, effect);
                break;
        }
    }

    public void applyTo(MapleCharacter chr) {
        chr.getClient().getSession().write(BuffPacket.giveBuff(-getEffectId(), duration * 60000, effects, null));
        final MapleStatEffect eff = MapleItemInformationProvider.getInstance().getItemEffect(getEffectId());
        chr.cancelEffect(eff, -1);
        final long starttime = System.currentTimeMillis();
        final CancelEffectAction cancelAction = new CancelEffectAction(chr, eff, starttime, effects);
        final ScheduledFuture<?> schedule = BuffTimer.getInstance().schedule(cancelAction, duration * 60000);
        chr.registerEffect(eff, starttime, schedule, chr.getId());
    }
}
