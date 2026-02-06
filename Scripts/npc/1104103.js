function start() {
    var v1 = parseInt(cm.getJob() / 100);
    if (v1 != 14) {
        cm.dispose();
        cm.sendNext("\r\n수색은 잘 되고 있나요? 여긴 특별히 수상한 것은 보이지 않아요... 계속 살펴 볼 테니, 당신은 다른 곳을 찾아봐 주세요.");
    }
    cm.sendNext("\r\n헙... 들킨 건가?! 그렇다면 어쩔 수 없지! 싸움이다! #r블랙 윙#k답게!");
}

function action(mode, type, selection) {
    if (mode == 1) {
        cm.resetMap(cm.getMapId());
        cm.spawnMonster(9001009, 1);
    }
    cm.dispose();
}