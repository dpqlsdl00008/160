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
            qm.sendNext("이런거 말고 더 맛있는 거 없어? 풀은 나에게 안 맞아, 더 영양가 높은 게 필요해, 마스터!");
            break;
        }
        case 1: {
            qm.sendNextPrevS("#b음... 채식은 싫은 거야? 드래곤이니까 역시 육식을 좋아하는 걸지도 모르겠네. 그럼 돼지고기 같은 건 어때?#k", 2);
            break;
        }
        case 2: {
            qm.sendAcceptDecline("돼지고기가 뭔지 모르겠어~ 하지만 맛있는 거라면 뭐든 좋아. 얼른 아무거나 구해줘~ 풀은 빼고!");
            break;
        }
        case 3: {
            qm.dispose();
            qm.forceStartQuest();
            qm.sendOkS("#b(그럼 미르에게 돼지고기를 주도록 하자. 농장에 있는 돼지들을 몇 마리만 잡으면 된다. 한 10개만 구하면 되겠지?)#k", 2);
            break;
        }
    }
}