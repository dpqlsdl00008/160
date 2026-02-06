var status = -1;
var beatrice = [
["마법사", "하인즈", 101000003, 1402], 
["궁수", "헬레나", 100000201, 1403], 
["도적", "다크로드", 103000003, 1404], 
["해적", "카이린", 120000101, 1405]
];

function start(mode, type, selection) {
    if (mode == 1) {
        status++;
    } else {
        if (status == 0 || status == 4) {
            qm.dispose();
            return;
        }
        if (status == 2) {
            status = 4;
        } else {
            status--;
        }
    }
    switch (status) {
        case 0: {
            qm.sendNext("자네가 바로 마이가 추천한 그 사람이로군. 전사가 되고 싶어한다고 들었네만... 맞는가? 내가 바로 전사 전직관 주먹펴고 일어서라네. 전사로서 모험하길 원하는 자들을 도와주고 있지.");
            break;
        }
        case 1: {
            qm.sendNextPrev("전사에 대해서는 어느 정도 알고 있는가? 전사는 강한 힘과 체력을 바탕으로 근거리 무기를 휘둘러 적을 쓰러뜨리는 직업이지. 적 가장 가까이에서 싸우는 두려움 없는 자들. 매력적이지 않는가?");
            break;
        }
        case 2: {
            qm.sendAcceptDecline("자네는 자질이 충분해 보이는군. 자네 같은 사람이 전사가 되겠다면 환영이지. 전사가 되겠는가? 수락한다면 전직관의 특권을 사용해 당장 자네를 #b페리온, 전사의 성전#k으로 초대하겠네 #r하지만 거절한다고 해서 길이 없는 건 아니야. 거절하면 다른 직업의 길을 안내하지.#k");
            break;
        }
        case 3: {
            qm.dispose();
            qm.forceStartQuest();
            qm.warp(102000003, 0);
            break;
        }
        case 4: {
            var say = "#b";
            for (i = 0; i < beatrice.length; i++) {
                say += "\r\n#L" + i + "#" + beatrice[i][0];
            }
            qm.sendSimple(say);
            break;
        }
        case 5: {
            jgys = selection;
            qm.sendAcceptDecline(beatrice[selection][0] + (selection < 3 ? "를" : "을") + "" + beatrice[selection][1] + "");
            break;
        }
        case 6: {
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
            qm.sendAcceptDecline("여기서 만나니 더욱 반갑군... 그럼 자네를 전사로 만들어 주지. 마음의 준비는 물론 되었겠지? 물러서는 자는 전사가 아니야.");
            break;
        }
        case 1: {
            if (qm.isQuestFinished(1401) == false) {
                qm.changeJob(100);
                qm.getPlayer().resetStats(4, 4, 4, 4);
                qm.gainItem(1302077, 1);
                qm.gainItem(1142107, 1);
		qm.gainItem(2431876, 1);
                qm.gainItem(2431877, 1);

                qm.forceCompleteQuest();
            }
            qm.sendNext("전사가 된 자네는 한층 강해졌다네. 전사로서 사용 할 수 있는 새로운 스킬이 생겼으니 어서 새로운 힘을 시험해보게나.");
            break;
        }
        case 2: {
            qm.sendNextPrev("또한, 자네의 능력치도 전사에 어울리도록 변경해야 하지. 전사라면 당연히 STR이 중심 스탯이고 DEX가 보조 스탯이라네. 잘 모르겠다면 #b자동 배분#k을 사용하는 것도 좋겠지.");
            break;
        }
        case 3: {
            qm.sendNextPrev("전사가 된 기념으로 자네의 인벤토리 슬롯도 조금 늘려 주었네. 많은 무기와 방어구를 구해서 자네의 힘을 더 강력하게 만들게나.");
            break;
        }
        case 4: {
            qm.sendNextPrev("아참, 한 가지 주의 사항을 알려주지. 초보자 때는 상관 없지만, 전사가 된 그 순간부터는 죽지 않도록 조심해야 하네... 만일 죽게 되면 그 동안 쌓았던 경험치가 깎일 수 있으니까 말야...");
            break;
        }
        case 5: {
            qm.sendNextPrev("내가 자네에게 가르쳐 줄 수 있는건 여기까지 일세... 자네에게 검을 한 개 주었으니 이제는 자네 혼자서 자기 자신을 더욱 단련시키게.");
            break;
        }
        case 6: {
            qm.dispose();
            break;
        }
    }
}