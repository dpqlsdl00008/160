var status = -1;

function start() {
    status = -1;
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
            cm.sendYesNoS("전투를 마치고 밖으로 나갈까?", 2);
            break;
        }
        case 1: {
            cm.dispose();
            var portalID = 0;
            switch (cm.getMapId()) {
                case 105200100:
                case 105200110: {
                    portalID = 3;
                    break;
                }
                case 105200200:
                case 105200210: {
                    portalID = 2;
                    break;
                }
                case 105200300:
                case 105200310: {
                    portalID = 4;
                    break;
                }
                case 105200400:
                case 105200410: {
                    portalID = 1;
                    break;
                }
            }
            cm.warp(105200000, portalID);
            break;
        }
    }
}