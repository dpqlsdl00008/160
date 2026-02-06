function start() {
    cm.sendYesNo("에델슈타인으로 가고 싶어? 요금은 800메소인데... 가겠다면 어서 타.");
}

function action(mode,type,selection) {
    cm.dispose();
    if (mode == 1) {
        if (cm.getMeso() < 800) {
            cm.sendNext("\r\n메소가 충분하지 않은 걸?");
            return;
        }
        cm.gainMeso(-800);
        cm.warp(310000010,0);
    }
}