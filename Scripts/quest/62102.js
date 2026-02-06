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
            qm.sendNext("사실은.. 이 곳을 떠나고 싶지 않아요.. 이 곳에서 좋아하는 사람이 생겼기 때문이죠.");
            break;
        }
        case 1: {
            qm.sendNextPrevS("하지만 타타모 촌장님과 리프레의 주민들이 당신을 걱정하고 있어요!", 2);
            break;
        }
        case 2: {
            qm.sendNextPrev("하긴, 너무 오랜 시간 동안 그 곳으로 돌아가지 못하고 있네요... 이를 어쩐다...");
            break;
        }
        case 3: {
            qm.sendNextPrevS("그럼 이렇게 하죠. 제가 당신을 리프레까지 데려다 줄게요, 리프레의 주민들에게 안전하다는 소식을 전한 후 예원 정원으로 다시 돌아오는건 어떨까요?", 2);
            break;
        }
        case 4: {
            qm.askAcceptDecline("정말 좋은 생각이에요...! 떠나기 전에 한 가지만 더 부탁드려도 될까요?");
            break;
        }
        case 5: {
            qm.dispose();
            qm.forceCompleteQuest();
            break;
        }
    }
}