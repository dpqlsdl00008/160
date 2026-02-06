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
            qm.sendNext("앗! 저를 도와주셨던 #h #님 이시군요? 덕분에 저는 리프레에 잘 다녀왔답니다!");
            break;
        }
        case 1: {
            qm.sendNextPrev("앞으로 제 도움이 필요한 일이라면 언제든 도와드릴게요!");
            break;
        }
        case 2: {
            qm.dispose();
            qm.forceCompleteQuest();
            break;
        }
    }
}