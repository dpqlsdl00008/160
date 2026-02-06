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
            qm.sendNext("마스터! 나 엄청 감동받았어! 마스터는 정말 착한 사람이구나! 나는 잘 모르지만, 마스터가 저 나이 많은 인간을 도와주는 걸 보니까 그런 생각이 들었어! 우리 앞으로도 어렵고 힘든 사람들을 도와주자! 그게 우리의 사명이야!");
            break;
        }
        case 1: {
            qm.sendNextPrevS("#b엥? 가, 갑자기 무슨 말이야? 사명이라니?#k", 2);
            break;
        }
        case 2: {
            qm.sendNextPrev("응! 마스터와 난 이제 강한 힘을 갖게 되었잖아? 우리가 그런 힘을 갖게 된 이유는 남을 돕기 위해서인 게 분명해! 그런 생각이 들어. 그게 드래곤 마스터로서 해야 할 일이야!");
            break;
        }
        case 3: {
            qm.sendNextPrevS("#b드래곤 마스터로서 해야 할 일?#k", 2);
            break;
        }
        case 4: {
            qm.sendAcceptDecline("그래! 그러니까 우리 고민 있는 사람을 도와주자! 어딘가에 분명 마스터의 도움을 간절히 바라는 사람이 있을 거야!");
            break;
        }
        case 5: {
            qm.dispose();
            qm.forceStartQuest();
            qm.sendOkS("#b(드래곤 마스터로써 남을 돕기로 약속했다. 흠... 왠지 멋진걸? 좋아! 헤네시스 마을에서 혼자 해결할 수 없는 일로 고민하고 있는 사람이 없는지 한 번 찾아보자. 나 정도면 충분히 다른 사람을 도와줄 수 있을 것이다.)#k", 2);
            break;
        }
    }
}