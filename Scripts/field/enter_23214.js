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
            if (cm.getMap().getMonsterById(9001038) == null) {
                cm.topMessage("다른 차원의 마스테마와 싸워서 이기십시오.");
                cm.spawnMonster(9001038, 597, -14);
            }
            break;
        }
    }
}