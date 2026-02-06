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
            qm.sendAcceptDecline("아, 전에 드래곤과 굉장히 비슷한 특이한 생물로 라이딩을 하시던 분이시군요. 그런데 어쩐 일로 여기까지... 아하, 혹시 안장을 잃어버리신 건가요?");
            break;
        }
        case 1: {
            qm.dispose();
            qm.warp(923030000, 0);
            qm.forceStartQuest();
            break;
        }
    }
}