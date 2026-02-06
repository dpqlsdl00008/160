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
            qm.sendNext("거기 너, 그래 너 말이야. 못 보던 얼굴인데?");
            break;
        }
        case 1: {
            qm.askAcceptDecline("꽤 강해보이는데 예원 정원 코인이 필요하지 않아? 코인을 얻을 수 있는 일일 퀘스트에 대해 알려줄까?");
            break;
        }
        case 2: {
            qm.sendNext("이 마을의 사람들은 유령과 강시들로 항상 불편을 겪고 있지. 그렇기 때문에 매일 매일 크고 작은 일들을 해결해줄 누군가가 필요해. 그래서 내가 일일 퀘스트 게시판을 만들었지!");
            break;
        }
        case 3: {
            qm.sendNextPrev("대신 퀘스트는 매일 변경되고 하루에 일정량만 할 수 있다는 것을 명심해야해!");
            break;
        }
        case 4: {
            qm.sendNextPrev("퀘스트를 완료하면 #i4310177# #d#z4310177##k을 얻을 수 있어. 그 코인은 예원 정원에 있는 특별한 상점에서만 사용이 가능해. 그럼, 힘내서 마을 사람들을 도와주길 바래!");
            break;
        }
        case 5: {
            qm.dispose();
            qm.forceCompleteQuest();
            break;
        }
    }
}