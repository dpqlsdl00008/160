var status = -1;

function start(mode, type, selection) {
    if (mode == 1) {
        status++;
    } else {
        if (status == 0) {
            qm.dispose();
            return;
        }
        if (status == 4) {
            qm.sendNext("두려워 할 것은 없다... 그 것은 이미 수백 년전의 과거, 그저 기억의 한 토막을 보여주는 것 뿐, 그 이상도 이하도 아니다...");
            qm.dispose();
            return;
        }
        status--;
    }
    switch (status) {
        case 0: {
            qm.sendNext("수백 년 전... 메이플 월드에는 아직 많은 오닉스 드래곤이 있었다... 그리고 오닉스 드래곤을 사랑하는 많은 인간이 있었지... 우리, 나와 나의 친구 프리드는 서로 다른 두 종족이 함께 살아 갈 수 있길 바랬다...");
            break;
        }
        case 1: {
            qm.sendNextPrev("강한 힘을 가졌지만 미 완성의 영혼을 가진 오닉스 드래곤... 힘은 약하지만 강한 의지를 가진 인간... 두 종족의 뜻이 결합되어 드래곤 마스터를 탄생시켰다... 우리는 그 힘으로 인간과 오닉스 드래곤이 공생하길 바랬다...");
            break;
        }
        case 2: {
            qm.sendNextPrev("하지만 그 바램은 #r검은 마법사#k로 인해 무너지고 말았다.");
            break;
        }
        case 3: {
            qm.sendNextPrevS("#b(검은 마법사?! 블랙윙이 메이플 월드의 평화를 위해 부활시키겠다는 사람이 검은 마법사가 아니었나!?)#k", 2);
            break;
        }
        case 4: {
            qm.sendAcceptDecline("말로 설명하는 것보다 직접 보여주는 것이 편할지도 모르겠군... #b그대를 나의 기억 속으로 보내겠다.#k 내 과거의 한 토막, 수백 년 전, 검은 마법사와 싸우기 직전, 프리드와 나의 대화가 이루어지던 그 시절의 기억 속으로...");
            break;
        }
        case 5: {
            qm.dispose();
            qm.warp(900030000, 0);
            qm.forceStartQuest();
            break;
        }
    }
}