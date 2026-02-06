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
            qm.dispose();
            qm.getPlayer().dropMessage(-1, "결혼식장 열쇠 획득 1 / 1");
            qm.sendNextS("이것은 #b결혼식장 열쇠#k! 이걸로 #b비올레타 공주#k가 갇혀 있는 결혼식장 안으로 들어 갈 수 있게 되었군.", 2);
            qm.forceStartQuest();
            break;
        }
    }
}