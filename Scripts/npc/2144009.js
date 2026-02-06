var status = -1;

function start() {
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
            if (cm.isQuestFinished(31177) == false) {
                cm.dispose();
                return;
            }
            cm.sendSimple("약속은 약속이니까 원한다면 #r드래곤#k으로 변신 시켜 줄게. 어떻게 할래?\r\n\r\n#L0##d드래곤으로 변신 시켜줘.#k");
            break;
        }
        case 1: {
            cm.dispose();
            cm.warp(200090520, "minar00");
            break;
        }
    }
}