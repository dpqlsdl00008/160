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
importPackage(Packages.handling.channel.handler);
importPackage(Packages.constants);
importPackage (java.lang);
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
        qm.sendNext("뀨웃....뀨르르르르...\r\n\r\n\#b(앗 귀엽다! 가까이 다가가 보았다.)#k");	
    } else if (status == 1) {
        qm.gainItem(4001094, -1);
        qm.gainItem(2041200, 1);
        qm.sendNext("아기용이 붉은색 돌을 뱉어냈습니다."); 
        qm.forceStartQuest();
        qm.forceCompleteQuest()
        qm.dispose();
    }
}
function end(mode, type, selection) {
        if (qm.getInvSlots(2) >= 1) {
                //qm.gainItem(4031119);
                qm.sendNext("아기용이 붉은색 돌을 뱉어냈습니다.");       
        } else {
        qm.sendOk("장비창을 비워주세요.");
	qm.dispose();
        }
}
