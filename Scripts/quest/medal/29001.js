?/*
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
/*
 * Quest ID : 29002
 * Quest Name : 칭호 도전 - 인기인!
 * Quest Progress Info : #b#e칭호 도전 - 인기인#n\n#k - 남은시간 : #Qdaylimit#일 #Qhourlimit#시간 #Qminlimit#분\n - 인기도 증가량 : #b#jpopgap##k / #jpopG#\n               #jgaugePop# #jperPop# %
 * Quest End Info : #b#e칭호 도전 - 인기인#n\n#k - 남은시간 : #Qdaylimit#일 #Qhourlimit#시간 #Qminlimit#분\n - 인기도 증가량 : #b#jpopgap##k / #jpopG#\n               #jgaugePop# #jperPop# %\n\n30일 동안 인기도 1000을 올렸다! 달리어도 이런 나의 인기에 놀란 눈치였는데, 결국 명예의 신관인 그에게서 '#b인기인#k'이라는 칭호를 받아내고 말았다. 훗, 이제 '#b메이플 아이돌스타#k'에 도전해 볼까?
 * Start NPC : 9000040
 * End NPC : 9000040
 * 
 * @author T-Sun
 *
 */

var status = -1;
function start(mode, type, selection) {
    if (mode == 1 && type != 1 && type != 11) {
        status++;
    } else {
        if ((type == 1 || type == 11) && mode == 1) {
            status++;
            selection = 1;
        } else if ((type == 1 || type == 11) && mode == 0) {
            status++;
            selection = 0;
        } else {
            qm.dispose();
            return;
        }
    }
    if (status == 0) {
        qm.sendNext("#v1112980# #e#b#t1112980##k\r\n\r\n - 퀘스트 1000개 완료\r\n\r\n#n이 훈장의 주인이 될 자격이 있는지 시험해 보시겠소?");
    } else if (status == 1) {
        qm.sendNext("자, 퀘스트 스페셜 리스트가 될 자격이 있으려면, 1000개의 퀘스트를 완료해야만 하네. 내 인생에 그런 모험가는 본적이 없지만. 그대라면 가능할지도 모른다고 생각하오.");
    } else if (status == 2) {
        qm.forceStartQuest();
        qm.dispose();
    }
}
function end(mode, type, selection) {
	if (qm.getPlayer().getNumQuest() >= 1000) {
        if (qm.getInvSlots(1) >= 1) {
                qm.gainItem(1112980 ,1 , false);
		qm.forceCompleteQuest();         
                qm.dispose();
        } else {
        qm.sendOk("장비창을 비워주세요.");
	qm.dispose();
        }
        } else {
        qm.sendOk("아직 그대는 #b"+qm.getPlayer().getNumQuest()+"개#k의 퀘스트를 완료하였군. 아직 퀘스트 스페셜 리스트가 될 자격을 충족하지 못한것같소. 퀘스트 스페셜리스트가 되기위한 자격은 1000개의 퀘스트 완료요.");
	qm.dispose();
        }
}
