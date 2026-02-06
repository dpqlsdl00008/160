var status = -1;
var beatrice = [
["전사", "주먹펴고 일어서", 102000003, 1401], 
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
            qm.sendNext("오오, 자네가 바로 마이가 말한 그 사람이로군. 안녕하신가? 마법사의 길에 관심이 있다고 들었네. 그렇다면 이 마법사 전직교관 하인즈가 자네를 돕도록 하지.");
            break;
        }
        case 1: {
            qm.sendNextPrev("마법사에 대해서는 자네도 이미 어느 정도 알고 있을 게야. 높은 지능을 바탕으로 마법을 익혀 사용하는 직업이지. 원거리와 근거리 모두에서 뛰어나지만 체력이 약한 게 좀 아쉽달까... 하지만 마법사들은 그것마저도 극복해낼 마법을 많이 만들었으니 걱정할 건 없다네.");
            break;
        }
        case 2: {
            qm.sendAcceptDecline("자네에게는 마법사가 될 자질이 충분히 보이는군... 마법사가 되겠는가? 수락하면 전직관의 특권으로 자네를 바로 이 #b엘리니아, 마법도서관#k으로 초대하지. 얼굴을 보고 전직을 시켜줄 게야. #r하지만 거절한다고 해서 길이 없는 건 아니야. 거절한다면 마법사 외의 직업을 소개해 주지.#k");
            break;
        }
        case 3: {
            qm.dispose();
            qm.forceStartQuest();
            qm.warp(101000003, 0);
            break;
        }
        case 4: {
            var say = "마법사의 길에 만족하지 못하는 건가? 아쉽군. 하지만 자네의 선택을 존중하지. 그럼 어떤 길을 걷겠나?#b";
            for (i = 0; i < beatrice.length; i++) {
                say += "\r\n#L" + i + "#" + beatrice[i][0];
            }
            qm.sendSimple(say);
            break;
        }
        case 5: {
            jgys = selection;
            qm.sendAcceptDecline(beatrice[selection][0] + (selection < 2 ? "를" : "을") + " 택하는 건가? 아쉽지만 어쩔 수 없지. " + beatrice[selection][1] + "에게 자네를 보내주겠네.");
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
            qm.sendNextS("두려운 겐가? 두려워하지 말게... 자네를 위해 준비된 길이네.", 2);
            qm.dispose();
            return;
        }
        status--;
    }
    switch (status) {
        case 0: {
            qm.sendAcceptDecline("오오, 잘 왔네. 이렇게 얼굴을 보니 더욱 반갑군... 자네라면 좋은 마법사가 될 수 있을 걸세. 그럼 바로 마법사로 만들어 주지.");
            break;
        }
        case 1: {
            if (qm.isQuestFinished(1402) == false) {
                qm.changeJob(200);
                if (qm.getLevel() > 8) {
                    qm.getPlayer().gainSP((qm.getLevel() - 8) * 3);
                }
                qm.getPlayer().resetStats(4, 4, 4, 4);
                qm.gainItem(1372043, 1);
                qm.gainItem(1142107, 1);
		qm.gainItem(2431876, 1);
                qm.gainItem(2431877, 1);
		qm.gainItem(1092003, 1);
                qm.forceCompleteQuest();
            }
            qm.sendNext("이로서 자네는 다양한 마법 스킬을 사용할 수 있게 되었다네. 자네에게 약간의 SP를 주었으니 Skii을 열어 원하는 스킬을 익혀 보게나. 되도록이면 공격 마법을 익히도록 하게나.");
            break;
        }
        case 2: {
            qm.sendNextPrev("스킬만으로는 부족하지. 자네의 스탯 또한 마법사에 어울리도록 해야 하지 않겠는가? 마법사는 INT를 중심 스탯으로, LUK을 보조 스탯으로 여기고 있다네. 스탯 올리는 게 어렵다면 #b자동 배분#k을 이용하게나.");
            break;
        }
        case 3: {
            qm.sendNextPrev("");
            break;
        }
        case 4: {
            qm.sendNextPrev("아참, 그리고 마법사가 되었다고 좋아서 사냥터로 달려갈 자네에게 주는 충고 한 마디. 마법사가 된 순간부터 만일 죽게되면 그동안 쌓였던 경험치를 잃을 수 있다네. 체력이 약하니 항상 조심해야 하네.");
            break;
        }
        case 5: {
            qm.sendNextPrev("내가 자네에게 가르쳐 줄 수 있는 것은 이게 전부일세. 자네의 수련을 위해 완드를 한 개 주었으니 잘 사용하게. 그럼 자네의 모험에 행운을 빌지.");
            break;
        }
        case 6: {
            qm.dispose();
            break;
        }
    }
}