var status = -1;

function start() {
    action(1, 0, 0);
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
            cm.sendYesNo("아직 배가 출발하려면 잠시 있어야 합니다만.. 지금 내려서 정거장으로 돌아가실 수도 있답니다. 어떠세요? 지금 돌아가 보시겠어요? 지금 내려도 표는 환불되지 않으니 신중하게 생각해주세요.");
            break;
        }
        case 1: {
            cm.dispose();
            cm.warp(cm.getPlayer().getMapId() - 1, 0);
            break;
        }
    }
}