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
            qm.sendNextS("#b(전에 봤던 편지가 다시 내 손에 들어왔다.)#k\r\n\r\n새로운 임무가 주어진 걸까?", 16);
            break;
        }
        case 1: {
            qm.sendNextPrev("#r위험한 동굴#k, #b로라#k에게.");
            break;
        }
        case 2: {
            qm.sendAcceptDeclineS("지난 번 보다도 더 급한 임무가 발생 한 것 인게 분명해. 어떻게 하지?", 2);
            break;
        }
        case 3: {
            qm.sendNextS("#i2030027# 다행히 티켓이 들어있군. 이걸 사용해서 어서 로라에게 가보자.", 16);
            break;
        }
        case 4: {
            qm.dispose();
            qm.gainItem(2030027, 1);
            qm.forceStartQuest();
            break;
        }
    }
}