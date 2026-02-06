var baseid = 910510001;
var dungeonid = 910510000;

var status = -1;

function start() {
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
            if (cm.isQuestActive(21731) == false) {
                cm.dispose();
                return;
            }
            cm.sendNextS("\r\n이 자식... 여긴 어떻게 들어온 거지? 그리고 그렇게 경고 했는데도 계속 내 일을 방해 해?", 8);
            break;
        }
        case 1: {
            cm.sendNextPrevS("\r\n너야말로 도대체 무슨 짓을 하는 거지? 몬스터를 조종해서 무슨 짓을 하려는 거냐?! 블랙윙의 목적을 말해라!", 2);
            break;
        }
        case 2: {
            cm.sendNextPrevS("흥! 네 녀석에게 말해줄 것은 없다! 바로 없애주마!", 8);
            break;
        }
        case 3: {
            cm.dispose();
            cm.resetMap(910510000);
            cm.spawnMonster(9300344, 220, 220);
            break;
        }
    }
}