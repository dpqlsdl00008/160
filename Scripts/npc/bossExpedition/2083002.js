var status = -1;

var value = 20230002;

function start() {
    status = -1;
    if (cm.getMapId() == 240050400) {
        status = 1;
    }
    action(1, 0, 0);
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
            cm.sendYesNo("전투를 그만두고 밖으로 나가시겠습니까? 하루 3회 입장 할 수 있으며, 퇴장 시 입장 횟수가 " + (3 - parseInt(cm.getPlayer().getOneInfoQuest(value, "horntail_enter"))) + "회 남습니다.");
            break;
        }
        case 1: {
            cm.dispose();
            cm.warp(240050400, 0);
            break;
        }
        case 2: {
            var say = "나무 뿌리에 얽혀 있는 수정의 표면에 글귀가 떠올랐다.\r\n#b";
            say += "\r\n#L0#수정의 표면에 떠오른 글귀를 좀 더 자세히 읽어본다.";
            say += "\r\n#L1#포기하고 밖으로 나가겠습니까?";
            cm.sendSimple(say);
            break;
        }
        case 3: {
            cm.dispose();
            if (selection == 1) {
                cm.warp(240050000, "out00");
            }
            break;
        }
    }
}