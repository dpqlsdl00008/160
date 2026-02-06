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
            qm.askAcceptDecline("엘리니아의 요정 아르웬과 요정 로웬을 알고 계신가요? 그들도 사실 이곳 엘리넬의 졸업생이랍니다. 그들에게 도움을 요청해보겠어요? 어쩌면 그들이라면 아이들의 심리에 대해서 더 잘 알지도 모르죠.\r\n\r\n#e#b(수락하면 엘리니아로 자동으로 이동합니다.)#k#n");
            break;
        }
        case 1: {
            qm.sendNext("부탁해요. 엘리니아로 돌아가서 요정 아르웬을 만나주세요.");
            break;
        }
        case 2: {
            qm.dispose();
            qm.forceStartQuest();
            qm.warp(101000000, 0);
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
            qm.sendNext("무슨 일이죠? 저는 그다지 한가하지 않은데...");
            break;
        }
        case 1: {
            qm.sendNextPrevS("(요정 아르웬에게 그동안의 이야기를 해주었다.)", 2);
            break;
        }
        case 2: {
            qm.sendNextPrev("엘리넬의 아이들이 실종되었다구요? 그런 일이 일어나다니...");
            break;
        }
        case 3: {
            qm.dispose();
            qm.forceCompleteQuest();
            break;
        }
    }
}