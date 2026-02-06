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
            cm.sendYesNo("#Cgreen#칼라 버섯 포자를 사용 하시겠습니까?\r\n\r\n#r#e※주의 사항#n\r\n인체에 사용하지 마세요!\r\n먹었을 경우 가까운 병원으로 찾아가세요!");
            break;
        }
        case 1: {
            cm.dispose();
            cm.forceCustomDataQuest(2322, "1");
            cm.gainItem(2430014, -1);
            cm.sendNextS("\r\n성공이다!! 결계를 뚫었어!", 2);
            break;
        }
    }
}