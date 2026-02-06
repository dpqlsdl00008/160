var status = -1;

var cQuest = [
[1431, 100],
[1432, 100],
[1433, 100],
[1435, 200],
[1436, 200],
[1437, 200],
[1439, 300],
[1440, 300],
[1442, 400],
[1443, 400],
[1445, 500],
[1446, 500],
[1447, 500],
[1448, 500],
];

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
            v1 = false;
            v2 = 0;
            for (i = 0; i < cQuest.length; i++) {
                if (cm.isQuestActive(cQuest[i][0]) == true) {
                    v1 = true;
                    v2 = cQuest[i][1];
                }
            }
            if (!v1) {
                cm.dispose();
                return;
            }
            cm.sendYesNo("#b다른 차원의 세계로 이동하시겠습니까?#k");
            break;
        }
        case 1: {
            cm.dispose();
            cm.resetMap((910540000 + v2));
            cm.warp((910540000 + v2), 0);
            break;
        }
    }
}