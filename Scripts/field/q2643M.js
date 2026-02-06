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
            if (cm.getMap().getMonsterById(9300528) != null) {
                cm.dispose();
                return;
            }
            if (cm.isQuestFinished(2643) == true) {
                cm.dispose();
                return;
            }
            cm.lockInGameUI(true);
            cm.sendNextS("\r\n너는... 익숙한 얼굴이군. 설희는 괜찮은가?", 1, 0, 1052001);
            break;
        }
        case 1:{
            cm.sendNextPrevS("\r\n(설희의 이야기를 전해 주었다.)", 17);
            break;
        }
        case 2:{
            cm.sendNextPrevS("\r\n그런가... 용서는 아닐 지 라도... 힘을 합하자는 건가. 비화원의 수장다운 선택... 설희도 더 이상 예전의 여린 아가씨는 아니군.", 1, 0, 1052001);
            break;
        }
        case 3:{
            cm.sendNextPrevS("\r\n좋아. 덤벼라. 커닝시티 도적의 힘을 보여 주지.", 1, 0, 1052001);
            break;
        }
        case 4:{
            cm.dispose();
            cm.spawnMonster(9300528, -745, 142);
            cm.lockInGameUI(false);
            break;
        }
    }
}