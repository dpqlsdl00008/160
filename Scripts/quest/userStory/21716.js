var status = -1;

function start(mode, type, selection) {
    if (mode == 1) {
        status++;
    } else {
        if (status == 0) {
            qm.dispose();
            return;
        }
        if (status == 2) {
            qm.sendNext("네? 그 아이 외에 다른 용의자는 없는 것 같아요. 다시 생각 해보세요.");
            qm.dispose();
            return;
        }
        status--;
    }
    switch (status) {
        case 0: {
            qm.sendNext("#p1032112#는 뭐라고 하던가요?");
            break;
        }
        case 1: {
            qm.sendNextPrevS("#b(#p1032112#에게 들은 이야기를 전해 주었다.)#k", 2);
            break;
        }
        case 2: {
            qm.sendAcceptDecline("인형을 든 꼬마? 의심스럽기 짝이 없네요. 역시 그가 초록버섯들을 포악하게 만든 원인이 틀림없어요.");
            break;
        }
        case 3: {
            qm.sendNextS("남쪽 숲을 어지럽히다니, 정말이지 용서할 수가 없네요. 흐트러진 남쪽 숲의 생태계를 되돌릴려면 시간이 얼마나 걸릴지... 한동안은 남쪽 숲 관리에 신경 써야겠네요.", 2);
            break;
        }
        case 4: {
            qm.sendNextPrevS("#b(#p1002104#이 포악해진 원인을 알아내었다. 이제 트루에게 수집한 정보를 전해 주자.)#k", 2);
            break;
        }
        case 5: {
            qm.dispose();
            qm.forceStartQuest();
            break;
        }
    }
}