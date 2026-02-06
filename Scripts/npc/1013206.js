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
            cm.sendNext("여기까지 찾아오실 줄은 몰랐습니다. 표정을 보아하니... 무척 화가 나신 모양이군요.");
            break;
        }
        case 1: {
            cm.sendNextPrevS("지금까지 날 속였지?! 용서하지 않겠다!", 2);
            break;
        }
        case 2: {
            cm.sendNextPrev("속였다? 아니, 당신 혼자 착각 한 거겠죠... 후후, 덕분에 많은 도움이 되었습니다. 하지만, 더 이상은 안 되겠군요. 이제 당신은 방해물일 뿐 입니다.");
            break;
        }
        case 3: {
            cm.sendNextPrev("여기서 없어져 주셔야 겠습니다.");   
            break;
        }
        case 4: {
            cm.dispose();
            cm.resetMap(cm.getMapId());
            cm.spawnMonster(9300393, 183, 31);
            break;
        }
    }
}