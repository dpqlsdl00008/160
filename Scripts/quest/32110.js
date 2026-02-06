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
            qm.sendNext("왔구나, #h #. 그러면 수색을 시작해볼까?");
            break;
        }
        case 1: {
            qm.sendNextPrevS("무엇부터 시작하면 될까?", 2);
            break;
        }
        case 2: {
            qm.sendNextPrev("아이들이 가장 좋아하는 것이 뭔 줄 알아? 그건 바로 자기들 끼리의 비밀이야. 나도 스승님 몰래 같은 제작들끼리 쪽지를 주고 받으면서 킥킥대곤 했지. 비밀의 장소에 무언가를 숨겨 놓기도 하고 말이야. 그런 게 그렇게 재밌더라구.");
            break;
        }
        case 3: {
            qm.sendSimple("나의 직감에 의하면, 아이들은 서로가 공유하던 비밀이 있었어. 그 비밀이 힌트가 될 수 있을 거야. 여기에서 문제. 아이들의 비밀을 찾으려면 어떻게 해야 할까?#b\r\n#L0#먼저 아이들을 찾고 나서 그 다음에 비밀을 물어보자.\r\n#L1#비밀을 적어놓은 쪽지라도 찾아보는 건 어떨까?\r\n#L2#잘 모르겠는데. 네 생각은 어때?");
            break;
        }
        case 4: {
            qm.askAcceptDecline("분명 이 #r하급 마법책#k들 사이에 쪽지가 숨어있을 거야. 아까 보니까 너 싸움 좀 하는 모양이던데, #r하급 마법책#k들을 사냥해서 #b남학생들의 쪽지#k를 찾아 줄 수 있겠나?");
            break;
        }
        case 5: {
            qm.dispose();
            qm.forceStartQuest();
            break;
        }
    }
}

function end(mode, type, selection) {
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
            qm.sendNext("쪽지에서 유용한 내용을 찾은 거야?");
            break;
        }
        case 1: {
            qm.sendNextPrev("비밀스러운 물건이라... 그게 과연 뭘까? 우리가 직접 찾아보는 편이 좋겠어.");
            break;
        }
        case 2: {
            qm.dispose();
            qm.forceCompleteQuest();
            break;
        }
    }
}