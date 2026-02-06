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
            cm.dispose();
            var v1 = true;
            if (cm.isQuestActive(6230) == false) {
                v1 = false;
            }
            if (cm.haveItem(4031456, 1) == true) {
                v1 = false;
            }
            if (cm.haveItem(4031476, 1) == false) {
                v1 = false;
            }
            if (!v1) {
                cm.sendNext("\r\n투명한 구슬 속에 붉은 단풍잎이 있다.");
                return;
            }
            cm.gainItem(4031476, -1);
            cm.gainItem(4031456, 1);
            cm.sendNext("\r\n단풍잎이 빛나는 유리 구슬 속으로 빨려 들어 갔습니다.");
            break;
        }
    }
}