var status = -1;

function start(mode, type, selection) {
    if (mode == 1) {
        status++;
    } else {
        if (status == 0) {
            qm.dispose();
            return;
        }
        status--;
    }
    switch (status) {
        case 0: {
            qm.sendNext("(상자 안에 정체모를 약이 들어있었다.)");
            break;
        }
        case 1: {
            qm.sendNextPrevS("#b어라? 상자 안에 무슨 약이 들어있네.. 이것은 대체 뭐지?! 안 되겠다. 이 약을 가져가서 존에게 직접 물어보는 수 밖에...#k", 2);
            break;
        }
        case 2: {
            qm.dispose();
            qm.gainItem(4032423,1);
            qm.forceStartQuest();
            break;
        }
    }
}