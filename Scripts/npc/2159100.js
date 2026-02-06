function start() {
    cm.sendNext("뭐, 뭐지? 이 시간에는 아무도 공항에 오지 못하도록 하라고 했는데... 설마! 넌 레지스탕스인가?");
}

function action(mode, type, selection) {
    if (mode == 1) {
        cm.removeNpc(cm.getMapId(), cm.getNpc());
        cm.spawnMonster(9001031, 1);
    }
    cm.dispose();
}