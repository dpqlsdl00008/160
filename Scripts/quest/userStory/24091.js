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
            qm.sendNext("딩동댕! 검은 마법사님의 군대를 이끌던 #r군단장 오르카#k입니다! 정답이니까 박수~ 짝짝짝~");
            break;
        }
        case 1: {
            qm.sendNextPrevS("살아 있었나!", 2);
            break;
        }
        case 2: {
            qm.sendNextPrev("응. 당연히 살아 있었지. 너희들도 살아 있는데, 내가 죽을 리 없잖아? 다시 만나고 싶었어~ 그래서 이렇게 저렇게 계략을 써봤는데 어때?");
            break;
        }
        case 3: {
            qm.sendNextPrevS("나에 대해 모르는 것이 당연한 블랙윙이 이상하게 나를 노린다고 생각했는데... 이유가 있었군. 네가 날 노린 거였어.", 2);
            break;
        }
        case 4: {
            qm.sendNextPrev("응. 우리 애들이 너한테도 인사하고 싶어서 소개를 좀 시켜줬지. 안 시켜도 달려간 멍청이도 하나 있었지만~");
            break;
        }
        case 5: {
            qm.sendNextPrevS("우리... 애들?", 2);
            break;
        }
        case 6: {
            qm.sendNextPrev("응. 오면서 못 봤어? 꽤 자주 봤을 텐데. 방금까지 너한테 길 안내도 해줬잖아?");
            break;
        }
        case 7: {
            qm.sendNextPrevS("설마... 네가 블랙윙을...!", 2);
            break;
        }
        case 8: {
            qm.sendNextPrev("내가 만들었어. 혼자 있으려니 너무 심심해서 말이지.");
            break;
        }
        case 9: {
            qm.sendNextPrevS("검은 마법사는 이미 봉인됐다! 그런데 블랙윙이라는 집단까지 만들어서 활동하다니. 아직도 메이플 월드를 노리는 거냐!?", 2);
            break;
        }
        case 10: {
            qm.sendNextPrev("맞아... 그 분은 봉인되셨지. 하지만 그것도 곧... 후후훗. 재밌는 건 비밀로 해두는 게 좋겠지?");
            break;
        }
        case 11: {
            qm.sendNextPrev("자, 잡담은 여기까지! 이제 우리 놀자! 나 말야, 옛날부터 너랑 놀고 싶었어.");
            break;
        }
        case 12: {
            qm.dispose();
            qm.resetMap(qm.getMapId());
            qm.spawnMonster(9300435, 382, 18);
            qm.forceStartQuest();
            break;
        }
    }
}