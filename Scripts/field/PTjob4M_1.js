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
            if (cm.getJob() != 2411) {
                cm.dispose();
                return;
            }
            cm.lockInGameUI(true);
            cm.sendNextS("\r\n(예전에 세워 둔 가디언이다. 만드는 데 돈이 엄청나게 들었다... 그만큼 강력한 가디언으로 침입자를 모두 물리 칠 만한 능력을 갖고 있다.)", 17);
            break;
        }
        case 1:{
            cm.sendNextPrevS("\r\n문 열어.", 17);
            break;
        }
        case 2:{
            cm.sendNextPrevS("\r\n음성... 확인...", 1, 0, 1403002);
            break;
        }
        case 3:{
            cm.sendNextPrevS("\r\n(예전보다 기동이 늦는 걸... 녹이라도 슬었나?)", 17);
            break;
        }
        case 4:{
            cm.sendNextPrevS("\r\n침입자! 침입자다! 전투 모드로 변경! 침입자를 물리친다!", 1, 0, 1403002);
            break;
        }
        case 5:{
            cm.sendNextPrevS("\r\n뭐? 야, 왜 이래?! 주인 목소리도 못 알아 듣냐?!", 17);
            break;
        }
        case 6:{
            cm.sendNextPrevS("\r\n침입자는 즉시 제거한다!", 1, 0, 1403002);
            break;
        }
        case 7:{
            cm.sendNextPrevS("\r\n어, 어어어...?", 17);
            break;
        }
        case 8:{
            cm.sendNextPrevS("\r\n제거한다!", 1, 0, 1403002);
            break;
        }
        case 9:{
            cm.dispose();
            cm.lockInGameUI(false);
            cm.spawnMonster(9001047, 255, 182);
            break;
        }
    }
}