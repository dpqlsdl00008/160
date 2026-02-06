var status = -1;

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
            if (qm.isQuestActive(1447) == false) {
                qm.dispose();
                qm.forceStartQuest();
            } else {
                qm.sendNext("시험을 무사히 통과했군... 제법인데? 설마 분신에 불과한 #b다른 차원의 다크로드#k라지만 그를 이길 줄은 몰랐어. #b성스러운 돌#k을 통해 분신까지 소환해 달라길래 얼마나 재능이 있나 했더니, 다크로드의 말이 틀리지는 않았군.");
            }
            break;
        }
        case 1: {
            qm.sendYesNo("진정한 도적인 다크로드와의 전투는 자네를 진정한 도적으로 만들었다... 본인은 잘 못 느끼겠지만 말이야. 이제 남은 것은 전직 뿐. 더 강력한 도적, 슬래셔가 될 준비가 되었나?");
            break;
        }
        case 2: {
            qm.dispose();
            qm.gainItem(4031059, -1);
            qm.changeJob(433);
            qm.forceCompleteQuest();
            qm.sendNext("이제부터 자네는 #b슬래셔#k이다. 도적의 숨겨진 힘, 진정한 슬래셔로서 자네가 가진 힘을 마음껏 사용하게.");
            break;
        }
    }
}