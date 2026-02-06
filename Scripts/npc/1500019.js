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
            if (cm.getMonsterCount(cm.getMapId()) != 0) {
                cm.sendNextS("\r\n히익... 이 몬스터들 좀 어떻게 해 주세요!\r\n#b(몬스터들을 모두 처리한 뒤 다시 말을 걸어보자.)#k", 4);
                cm.dispose();
                return;
            }
            cm.sendNextS("\r\n만세! 살았다... 몬스터들에게 둘러싸여서 옴짝달싹 못했지 뭐예요.", 4, 1500019, 1500019);
            break;
        }
        case 1: {
            cm.sendNextPrevS("\r\n당신은 누구죠? 설마 우리를 구해주러 온 영웅?", 4, 1500020, 1500020);
            break;
        }
        case 2: {
            cm.sendNextPrevS("\r\n#b(없어진 아이는 모두 다섯 명이라고 했는데, 남은 아이들은 어디에 있을까?)#k", 2);
            break;
        }
        case 3: {
            cm.dispose();
            cm.forceStartQuest(32135, "2");
            break;
        }
    }
}