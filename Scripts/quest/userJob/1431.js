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
            if (qm.isQuestActive(1431) == false) {
                qm.dispose();
                qm.forceStartQuest();
            } else {
                qm.sendNext("시험을 무사히 통과했군... 후후, #b다른 차원의 주먹펴고 일어서#k와 싸우는 것은 어땟는가. 그것은 #b성스러운 돌#k이 가진 놀라운 힘이 보여준 신비한 환상... 주먹펴고 일어서가 자네를 더 강한 전사의 길로 이끌기 위해 허락했기에 나타난 것이지.");
            }
            break;
        }
        case 1: {
            qm.sendYesNo("진정한 전사인 #b주먹펴고 일어서#k와 싸움으로서 자네 또한 진정한 전사의 자격을 얻었네. 이제 남은 것은 진정한 전사, #b크루세이더#k로 전직하는 것 뿐... 자, 새로운 힘을 받아들일 준비가 되었나?");
            break;
        }
        case 2: {
            qm.dispose();
            qm.gainItem(4031059, -1);
            qm.changeJob(111);
            qm.forceCompleteQuest();
            qm.sendNext("좋아! 자네는 이제부터 #b크루세이더#k라네. #b검과 도끼#k의 마스터, 진정한 전사로서 자네가 가진 힘을 마음껏 펼쳐보게.");
            break;
        }
    }
}