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
            cm.sendYesNo("#b(정신을 잃은 어린 아이가 보인다... 빨리 데리고 나가서 의사에게 보이자!)#k");
            break;
        }
        case 1: {
            cm.dispose();
            cm.forceStartQuest(24096, "1");
            break;
        }
    }
}