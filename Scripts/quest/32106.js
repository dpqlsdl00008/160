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
            qm.sendNextS("아직도 돌아가지 않았군요. 무슨 할 말이 남았나요?", 8, 1500001, 1500001);
            break;
        }
        case 1: {
            qm.sendNextPrevS("흥, 보나마나 뻔하지요. 분명 되도 않는 변명을 할 것이 뻔합니다. 이방인의 말은 들을 것도 없습니다, 교장 선생님.", 8, 1500002, 1500002);
            break;
        }
        case 2: {
            qm.sendNextPrevS("#b저도 오해를 풀어 달라는 부탁을 받았을 뿐입니다. 서로 정확한 사실 관계를 파악하는 것이 우선 아닐까요?#k", 2);
            break;
        }
        case 3: {
            qm.sendNextPrevS("흥, 허튼 소리! 무려 다섯 명이나 되는 우리 아이들이 한꺼번에 사라졌다고. 이게 납치가 아니라면 뭐겠어?", 8, 1500002, 1500002);
            break;
        }
        case 4: {
            qm.sendNextPrevS("#b마법사 쿠디가 범행을 저질렀다는 증거가 있나요?#k", 2);
            break;
        }
        case 5: {
            qm.sendNextPrevS("저 쿠디라는 마법사 녀석이 우리 숲 근처에 틀락거리던 것이 한 두 번이 아니야. 몇 번이나 쫓아냈지만 계속해서 몰래 무언가 수상한 짓을 하고 있었어.", 8, 1500002, 1500002);
            break;
        }
        case 6: {
            qm.sendNextPrevS("알고 보니 범행을 위해서 미리 현장을 답습하고 있었던 거지. 마침 방학이 시작되어 모든 선생들이 휴가를 간 틈을 타서 어린 아이들을 노렸던 거야. 하지만 범인은 현장에 다시 나타난다는 소리가 있지. 요 근처에서 어슬렁 거리기에 내가 냉큼 붙잡았던 거야.", 8, 1500002, 1500002);
            break;
        }
        case 7: {
            qm.sendNextPrevS("냉정히 생각해봐야 한다는 당신의 말은 이해합니다. 하지만 우리로서는 우선적으로 의심 가는 자를 문책할 수 밖에 없습니다.", 8, 1500001, 1500001);
            break;
        }
        case 8: {
            qm.sendAcceptDeclineS("#b(이들은 아이들에 대한 걱정 때문에 너무 흥분한 상태인 것 같다. 마법사 쿠디 본인의 말을 들어보는 것이 좋을 것 같다.)#k", 2);
            break;
        }
        case 9: {
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
            qm.sendNext("혹시 날 구해주러 온 거야?");
            break;
        }
        case 1: {
            qm.dispose();
            qm.forceCompleteQuest();
            break;
        }
    }
}