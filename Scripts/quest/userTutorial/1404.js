var status = -1;
var beatrice = [
["전사", "주먹펴고 일어서", 102000003, 1401], 
["마법사", "하인즈", 101000003, 1402], 
["궁수", "헬레나", 100000201, 1403], 
["해적", "카이린", 120000101, 1405]
];

function start(mode, type, selection) {
    if (mode == 1) {
        status++;
    } else {
        if (status == 0 || status == 5) {
            qm.dispose();
            return;
        }
        if (status == 3) {
            status = 5;
        } else {
            status--;
        }
    }
    switch (status) {
        case 0: {
            qm.sendNext("마이가 말하던 사람이 너인가? #h #... 흠. 그녀 말대로 제법 자질이 있어 보이는 애송이로군... 이봐. 도적이 되고 싶다고? 도적에 대해 알고 있나?");
            break;
        }
        case 1: {
            qm.sendNextPrev("도적이라고 하면 좁쌀만한 좀도둑을 생각하는 사람도 있지만, 실은 달라. 메이플 월드의 도적은 어둠 속에서 날카로운 단도와 표창으로 싸우는 자들이지. 뭐 조금은 정정당당하지 못한 부분이 있을지도 몰라. 하지만 그 또한 도적의 본질. 부정 할 필요는 없겠지.");
            break;
        }
        case 2: {
            qm.sendNextPrev("직업으로서의 도적은 빠르고 강력한 스킬로 적을 공격하는 자들이지. 체력은 약한 편이지만 그만큼 빨리 움직이기 때문에 몬스터를 피하는 것도 어렵지 않아. 강력한 운으로 크리티컬한 공격도 잘 하고 말이야.");
            break;
        }
        case 3: {
            qm.sendAcceptDecline("어때? 도적의 길에 동참하겠나? 자네가 도적의 길을 가겠다고 결정하면 전직관의 특권으로 자네를 바로 이 #b커닝시티, 도둑의 아지트#k로 초대하지... 은밀한 곳이니 영광으로 생각하라고. #r하지만 다른 직업이 더 좋다면? 그렇다면 거절해. 도적이 아닌 다른 길을 추천해주지.#k");
            break;
        }
        case 4: {
            qm.dispose();
            qm.forceStartQuest();
            qm.warp(103000003, 0);
            break;
        }
        case 5: {
            var say = "흠... 아직 마음의 준비가 되지 않은 모양이군. 내가 다른 직업들을 소개해주도록 하지.#b";
            for (i = 0; i < beatrice.length; i++) {
                say += "\r\n#L" + i + "#" + beatrice[i][0];
            }
            qm.sendSimple(say);
            break;
        }
        case 6: {
            jgys = selection;
            qm.sendAcceptDecline(beatrice[selection][0] + (selection < 3 ? "를" : "을") + "" + beatrice[selection][1] + "");
            break;
        }
        case 7: {
            qm.dispose();
            qm.forceStartQuest(beatrice[jgys][3]);
            qm.warp(beatrice[jgys][2], 0);
            break;
        }
    }
}

function end(mode, type, selection) {
    if (mode == 1) {
        status++;
    } else {
        if (status == 0) {
            qm.sendNextS("", 2);
            qm.dispose();
            return;
        }
        status--;
    }
    switch (status) {
        case 0: {
            qm.sendAcceptDecline("도둑의 아지트에 온 것을 환영하네. 이런 방식이 아니면 입구조차 찾기 힘든 곳이지. 후후. 나갈 때 문을 잘 기억해두라고. 자, 그럼 도적이 될 준비가 되었나?");
            break;
        }
        case 1: {
            if (qm.isQuestFinished(1404) == false) {
                qm.changeJob(400);
                qm.getPlayer().resetStats(4, 4, 4, 4);
                qm.gainItem(2070015, 500);
		qm.gainItem(2431876, 1);
                qm.gainItem(2431877, 1);
                qm.gainItem(1472061, 1);
                qm.gainItem(1332063, 1);
                qm.gainItem(1142107, 1);
                qm.forceCompleteQuest();
            }
            qm.sendNext("이로서 자네는 도적일세. 도적의 스킬을 사용 할 수 있게 되었으니 스킬창을 열어 보라고. 레벨을 올리면 더 많은 스킬을 익힐 수 있게 될 거야.");
            break;
        }
        case 2: {
            qm.sendNextPrev("스킬만으로는 부족하지? 스탯또한 도적에 어울려야 진짜 도적이지. 도적은 LUK을 중심 스탯으로, DEX를 보조 스탯으로 여긴다네. 스탯 올리는 법을 모르면 #b자동 배분#k을 사용하면 돼.");
            break;
        }
        case 3: {
            qm.sendNextPrev("그리고 한 가지 선물. 네 장비, 기타 아이템 보관함의 개수를 늘려주었네. 인벤토리가 넓을수록 세상이 즐거운 법이지. 후후...");
            break;
        }
        case 4: {
            qm.sendNextPrev("아, 그리고 한 가지 주의해야 할 점을 알려주지. 초보자는 상관없지만, 초보자가 아닌 자들은 죽게 되면 그 동안 쌓았던 경험치가 깎일 수 있다네. 조심하라고. 애써 얻은 경험치가 깎이면 억울하잖아?");
            break;
        }
        case 5: {
            qm.sendNextPrev("자! 내가 자네에게 가르쳐 줄 수 있는 건 이게 다야. 자네 수준에 쓸만한 무기도 몇 개 줬으니 여행을 하면서 자신을 단련시켜봐.");
            break;
        }
        case 6: {
            qm.dispose();
            break;
        }
    }
}