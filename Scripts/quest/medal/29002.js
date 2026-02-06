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
        qm.sendNext("#v1112981# #e#b#t1112981##k\r\n\r\n - 존재하는 모든 테마던전 클리어\r\n\r\n#n이 훈장의 주인이 될 자격이 있는지 시험해 보시겠소?");
    } else if (status == 1) {
        qm.sendNext("자, 테마던전 매니아가 될 자격이 있으려면, 존재하는 모든 테마던전을 완료해야 하오. 내 인생에 그런 모험가는 본적이 없지만. 그대라면 가능할지도 모른다고 생각하오.");
    } else if (status == 2) {
        qm.forceStartQuest();
        qm.dispose();
    }
}
function end(mode, type, selection) {
	if (qm.haveItem(1112960) && qm.haveItem(1112961) && qm.haveItem(1112963) && qm.haveItem(1112971) && qm.haveItem(1112972) && qm.haveItem(1112974) && qm.haveItem(1112975) && qm.haveItem(1112976) && qm.haveItem(1112979)) {
		qm.forceCompleteQuest();
                qm.dispose();
        } else {
        qm.sendOk("아직 그대는 모든 테마던전을 완료하지 않았구만. \r\n아래와 같은 테마던전 클리어를 증명할 훈장들이 필요하오.\r\n#v1112960# #e#b#t1112960##k\r\n#v1112961# #e#b#t1112961##k\r\n#v1112963# #e#b#t1112963##k\r\n#v1112971# #e#b#t1112971##k\r\n#v1112972# #e#b#t1112972##k\r\n#v1112974# #e#b#t1112974##k\r\n#v1112975# #e#b#t1112975##k\r\n#v1112976# #e#b#t1112976##k\r\n#v1112979# #e#b#t1112979##k");
	qm.dispose();
}
}
