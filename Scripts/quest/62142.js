var status = -1;

function start(mode, type, selection) {
    if (mode == 1) {
        status++;
    } else {
        if (status == 0) {
            qm.dispose();
            return;
        }
        status--;
    }
    switch (status) {
        case 0: {
            qm.sendNext("역시 당신은 진정한 영웅임이 틀림 없습니다! 우리 영웅의 적은 누구란 말인가!!");
            break;
        }
        case 1: {
            qm.sendNextPrev("수 백년 전 부터 있던 강시는 본래 전장에서 죽은 사람들을 고향으로 편히 옮기기 위해서 특수한 부적을 붙여 움직 일 수 있게 주술을 건 존재였습니다. 그러나! 어느 날 주술을 풀고 자유 의지로 움직이기 시작하면서 인간을 공격하게 된 것입니다!");
            break;
        }
        case 2: {
            qm.sendNextPrev("그것이 이 예원 정원에 나타나다니...");
            break;
        }
        case 3: {
            qm.sendNextPrev("놀라긴 일러요! 결정적인 이야기는 지금 부터라구요. 제가 강시를 물리칠 비기! 아주 극비 사항을 알려 드리겠습니다...");
            break;
        }
        case 4: {
            qm.askAcceptDecline("먼저 그들에게 팥죽을 뿌리면 그들은 부리나케 달아난답니다! 그러니 지금부터 우리는 재료를 모아야 합니다. 먼저 #d팥 10개#k와 #d찹쌀 10개#k를 가지고 와주시겠습니까?");
            break;
        }
        case 5: {
            qm.sendNext("그 재료들은 상해 외곽에 있는 팥과 벼를 공격해서 구하시면 된답니다!");
            break;
        }
        case 6: {
            qm.dispose();
            qm.forceStartQuest();
            break;
        }
    }
}

function end(mode, type, selection) {
    if (mode == 1) {
        status++;
    } else {
        if (status == 0) {
            qm.dispose();
            return;
        }
        status--;
    }
    switch (status) {
        case 0: {
            qm.sendNext("호오! 재료를 구해 왔군요! 하지만 아직 완성된 것은 아닙니다! 이 재료를 가지고 주방장 젠 황에게 가서 요리를 해달라고 요청해 보세요!\r\n\r\n#fUI/UIWindow.img/QuestIcon/4/0#\r\n\r\n#fUI/UIWindow.img/QuestIcon/8/0# 511,795 exp");
            break;
        }
        case 1: {
            qm.dispose();
            qm.forceCompleteQuest();
            qm.gainItem(4034641, -10);
            qm.gainItem(4034642, -10);
            qm.gainExp(511795);
            break;
        }
    }
}