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
            cm.dispose();
            cm.sendNext("\r\n만용을 부리는구나. 어리석은 자들이여... 강한 자들과 함께 도전하라.");
            break;
        }
    }
}