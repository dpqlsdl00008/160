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
            var say = "냠냠. 정말 맛있군. 종종 나를 찾아 와 #Cyellow#월묘의 떡#k을 구해주게.#d";
            if (cm.isLeader() == true) {
                if (cm.haveItem(4001101, 10) == true) {
                    say += "\r\n#L0#1. 토리에게도 월묘의 떡을 좀 주고 싶어요.";
                }
            }
            var v1 = ((cm.isLeader() == true && cm.haveItem(4001101, 10) == true) ? "2" : "1");
            say += "\r\n#L1#" + v1 + ". 이곳에서 나가고 싶습니다.";
            cm.sendSimple(say);
            break;
        }
        case 1: {
           if (selection == 1) {
               cm.dispose();
               cm.warp(910010500, "out00");
               return;
           }
           cm.sendNext("\r\n그럼 다음에도 나를 찾아 와 #b월묘의 떡#k을 구해 주게. 그럼 잘 가게.");
           break;
        }
        case 2: {
            cm.dispose();
            cm.warp(910010400, 0);
            break;
        }
    }
}