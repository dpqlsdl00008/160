var status = -1;

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
            if (qm.isQuestActive(20031) == false) {
                qm.dispose();
                qm.forceStartQuest();
            } else {
                qm.sendSimple("왜 이렇게 오래 걸렸어? 내가 안 보는 사이 농땡이친거 아니야? 가져오라는 물건은 가져왔어?#b\r\n#L0#네...여기...그리고 다락에서 이 편지를 줏었는데요. 아직 못 보신것 같아서요...크롬이라는 분이 보내신 것 같던데...");
            }
            break;
        }
        case 1: {
            qm.sendNext("뭐라고!! 이리내놔! 왜 남의 물건에 마음대로 손을 대는거야?");
            break;
        }
        case 2: {
            qm.lockInGameUI(true);
            qm.gainExp(12);
            qm.gainItem(4033194, -1);
            qm.gainItem(4033195, -1);
            qm.forceCompleteQuest();
            qm.sendDelay(1000);
            break;
        }
        case 3: {
            qm.sendNextS("힝... 오늘도 혼나버렸어...", 3);
            break;
        }
        case 4: {
            qm.sendNextS("어? 그런데 저건 뭐지?", 3);
            break;
        }
        case 5: {
            qm.showDirectionEffect("Effect/Direction7.img/effect/tuto/soul/0", 3000, 0, -130, 0, 0);
            qm.sendDelay(3000);
            break;
        }
        case 6: {
            qm.sendNextS("우왓!! 방금 그건 뭐였지? 작은... 밝은 빛이었는데...?", 3);
            break;
        }
        case 7: {
            qm.dispose();
            qm.lockInGameUI(false);
            qm.warp(913070002, 0);
            break;
        }
    }
}