var status = -1;

function start() {
    action (1, 0, 0);
}

function action(mode, type, selection) {
    if (mode == 1) {
        status++;
    } else {
        cm.dispose();
        return;
    }
    switch (status) {
        case 0: {
            if (cm.isQuestActive(23215) == false) {
                cm.dispose();
                return;
            }
            cm.sendYesNo("#h #님 오셨군요. 그럼 시간의 틈을 과거로 보내 드리도록 하겠습니다. 과거의 #h #님은 정말 강하니까 준비 단단히 하셔야 할 것 입니다. 준비 되셨습니까?");
            break;
        }
        case 1: {
            cm.sendNext("\r\n무운을 빕니다. #h #님.");
            break;
        }
        case 2: {
            cm.dispose();
            var v1 = true;
            if (cm.getPlayerCount(927000100) != 0) {
                v1 = false;
            }
            if (cm.getPlayerCount(927000101) != 0) {
                v1 = false;
            }
            if (!v1) {
                cm.getPlayer().dropMessage(5, "다른 캐릭터가 퀘스트를 진행 중이여서 들어 갈 수 없습니다.");
                return;
            }
            cm.resetMap(927000100);
            cm.resetMap(927000101);
            cm.warp(927000100, 0);
            break;
        }
    }
}