var status = -1;

function start() {
    status = -1;
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
            if (cm.getInfoQuest(23999).contains("exp1=1") == true &&
                cm.getInfoQuest(23999).contains("exp2=1") == true &&
                cm.getInfoQuest(23999).contains("exp3=1") == true &&
                cm.getInfoQuest(23999).contains("exp4=1") == true) {
                cm.sendYesNo("#b(의심스러워 보이는 구멍이다. 혹시 반은 이 안으로 들어가 버린걸지도 모르겟다. 한 번 들어가 볼까?) #k");
            } else {
                cm.dispose();
                cm.sendNext("#b(의심스러워 보이는 구멍이다. 혹시 반은 이 안으로 들어가 버린걸지도 모르겟다. 한 번 들어가 볼까?) #k");
            }
            break;
        }
        case 1: {
            cm.dispose();
            cm.gainExp(35);
            cm.warp(931000010, 0);
            break;
        }
    }
}