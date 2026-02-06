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
            qm.sendAcceptDecline("수련은 잘 하고 계신가요? 바쁘신데 미안하지만 어서 #b리엔#k으로 돌아와 주세요. #b마하#k가 또다시 이상한 반응을 보이고 있어요... 이상해요. 예전 같은 반응이 아니에요. 좀 더 깊고 어두운... 그런 느낌이에요.");
            break;
        }
        case 1: {
            qm.dispose();
            qm.forceStartQuest();
            break;
        }
    }
}