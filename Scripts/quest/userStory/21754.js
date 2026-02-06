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
            qm.sendNext("아란... 아란, 다시 만나게 될 줄 알았어요. 당신은 약속을 잘지키는 사람이니까요. 언제가 와줄 거라고 믿으며 기다렸어요...");
            break;
        }
        case 1: {
            qm.sendNextPrevS("#b(헬레나는 매우 행복하게 웃었다.)#k", 2);
            break;
        }
        case 2: {
            qm.sendAcceptDecline("드디어 그 때 주지 못했던 편지를 드릴 수 있게 되었네요. 시간이 오래 흘러서 그런지 많이 낡았지만... 하지만 내용은 아직 읽을 수 있을 거예요.");
            break;
        }
        case 3: {
            qm.sendNext("더 이야기하고 싶지만, 예전과 달리 전직관이라는 일을 맡아서 짬을 내기 어렵네요. 나중에 다시 찾아와줘요.");
            break;
        }
        case 4: {
            qm.sendNextPrev("당신에게 도움이 되어서 기뻐요. 나의 친구...");
            break;
        }
        case 5: {
            qm.sendNextPrev("#b(헬레나에게 편지를 받았다...편지 안에는 어떤 내용이 있을까? 트루와 함께 살펴보자.)#k");
            break;
        }
        case 6: {
            qm.dispose();
            if (qm.haveItem(4032328, 1) == false) {
                qm.gainItem(4032328, 1);
            }
            qm.forceStartQuest();
            break;
        }
    }
}