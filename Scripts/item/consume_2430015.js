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
            cm.dispose();
            var v1 = false;
            if (cm.getMapId() == 106020500) {
                v1 = true;
            }
            if (v1 == false) {
                cm.dispose();
                cm.getPlayer().dropMessage(5, "너무 멀리 떨어져 있어 아이템을 사용 할 수 없습니다. 조금 더 덩굴의 가시 쪽으로 다가가서 사용하세요.");
                return;
            }
            cm.openNpc(1300011);
            break;
        }
    }
}