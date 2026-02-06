var status = -1;

function end(mode, type, selection) {
    if (mode == 1) {
        status++;
    } else {
        if (status == 0) {
            qm.sendNext("신중하게 선택해라.");
            qm.dispose();
            return;
        }
        status--;
    }
    switch (status) {
        case 0: {
            qm.sendYesNo("선택을 한 것인가? 되돌릴 수는 없다. 신중하게 결정하도록. 정말 소울 마스터의 길을 가겠는가?");
            break;
        }
        case 1: {
            qm.forceStartQuest();
            if (qm.isQuestActive(20101) == true) {
                qm.changeJob(1100);
                qm.getPlayer().resetStats(4, 4, 4, 4);
                qm.gainItem(1302077, 1);
		qm.gainItem(2431876, 1);
                qm.gainItem(2431877, 1);
                qm.forceCompleteQuest();
            }
            qm.sendNext("그대의 몸을 소울 마스터에 적합하도록 만들었다. 이후에 더 강해지고 싶다면 스탯창(S키)에서 적절한 스탯을 올려라. 어렵다면 #b자동 배분#k을 사용해도 좋다.");
            break;
        }
        case 2: {
            qm.sendNextPrev("그대의 장비, 기타 아이템의 보관함 갯수를 늘려 주었다. 기사로서 필요한 것들로 채우도록.");
            break;
        }
        case 3: {
            qm.sendNextPrev("또한 그대에게 약간의 #bSP#k를 지급했으니 #bSkill 메뉴#k를 열어 스킬을 배우도록. 물론, 처음부터 전부 올릴 수 있는 건 아니다. 다른 스킬을 익히지 않고는 배울 수 없는 스킬도 있다.");
            break;
        }
        case 4: {
            qm.sendNextPrev("노블레스 때와 달리 소울 마스터가 된 이상 죽게 되면 그동안 쌓았던 경험치의 일부를 잃을 수 있다는 것을 명심하도록.");
            break;
        }
        case 5: {
            qm.sendNextPrev("그럼... 시그너스 기사단의 기사로서 부끄럽지 않은 모습을 보여다오.");
            break;
        }
        case 6: {
            qm.dispose();
            break;
        }
    }
}