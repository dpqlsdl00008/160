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
            qm.sendNextS("이런 우연이...여기서 또 뵙게 됬네요?", 1);
            break;
        }
        case 1: {
            qm.sendNextPrevS("#b(...이런 데서 또 만나다니. 그 때 커닝시티 지하철에서 도와줬던 이상한 여자자나...)#k\r\n\r\n네. 또 만났네요...", 3);
            break;
        }
        case 2: {
            qm.sendNextPrevS("저도 유적 발굴 현장을 도우러 왔다가, 제 이상한 게이트가 발견되었다고 들었어요. 혹시 직접 보셨나요?", 1);
            break;
        }
        case 3: {
            qm.sendNextPrevS("네. 미접근 지역에서 스켈레톤 지휘관을 처치하다가 봤어요. 묘한 기운이 뿜어져 나오는 것 같았어요.", 3);
            break;
        }
        case 4: {
            qm.sendYesNo("너무 궁금하군요! 저를 그곳으로 안내해 주실 수 있을까요? 제가 길 눈이 어두워서...");
            break;
        }
        case 5: {
            qm.sendYesNo("그럼 미접근 지역으로 바로 출발해볼까요?");
            break;
        }
        case 6: {
            qm.sendNext("그럼 지금 바로 가요.");
            break;
        }
        case 7: {
            qm.dispose();
            qm.warp(102040600, 3);
            qm.forceStartQuest();
            break;
        }
    }
}