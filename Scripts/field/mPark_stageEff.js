var status = -1;

function start() {
    status = -1;
    action (1, 0, 0);
}

function action(mode, type, selection) {
    if (mode == 1) {
        status++;
    } else {
        if (status == 0) {
            cm.dispose();
            return;
        }
        status--;
    }
    switch (status) {
        case 0: {
            cm.dispose();
            cm.getPlayer().dropMessage(-1, "필드 내의 모든 몬스터를 제거해야 다음 스테이지로 이동하실 수 있습니다.");
            var cMap = parseInt(cm.getMapId() % 10000);
            switch (cMap) {
                case 500: {
                    cm.showFieldEffect(false, "monsterPark/stageEff/final");
                    break;
                }
                default: {
                    cm.showFieldEffect(false, "monsterPark/stageEff/stage");
                    cm.showFieldEffect(false, "monsterPark/stageEff/number/" + ((cMap / 100) + 1));
                    break;
                }
            }
            break;
        }
    }
}
