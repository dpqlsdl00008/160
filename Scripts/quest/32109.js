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
            if (qm.isQuestActive(32109) == false) {
                qm.dispose();
                qm.forceStartQuest();
                return;
            }
            qm.sendNextS("아이들이 어디로 사라졌을지 유추해보자는 건가요? 나쁘지 않은 방안인 것 같군요.", 8, 1500001, 1500001);
            break;
        }
        case 1: {
            qm.sendNextPrevS("그러면서 물건이나 훔칠 속셈이지? 교장 선생님, 이방인들의 말은 들을 필요가 없습니다.", 8, 1500002, 1500002);
            break;
        }
        case 2: {
            qm.sendNextPrevS("저도 완전히 믿음이 가는 것은 아니에요. 하지만 어디까지나 아이들의 안전이 최우선이니 지금으로선 답이 없군요.", 8, 1500001, 1500001);
            break;
        }
        case 3: {
            qm.sendNextPrevS("이름이 #b#h ##k 님이라고 했던가요? 당신이 요정 학원 엘리넬의 건물 내부를 수색할 수 있도록 허가하겠어요. 2층에는 남학생들의 기숙사가, 3층에는 여학생들의 기숙사가 있어요. 하지만 이 건물은 처음부터 외분인들에게 적대적으로 설계되어 있지요. 엘리넬의 선생이나 학생이 아닌 다른 자가 출입하면 자동으로 공격하게 되어있죠. 이 점은 알아서 조심하도록 해요.", 8, 1500001, 1500001);
            break;
        }
        case 4: {
            qm.sendNextPrevS("흥, 허튼 수작을 부리는지 똑똑히 지켜보고 있을 테다!", 8, 1500002, 1500002);
            break;
        }
        case 5: {
            qm.sendNextPrevS("아이들을 꼭 찾아내고 말 테니 걱정 마세요. #h #, 나는 위층에 먼저 올라가서 기다리고 있을게.\r\n#b(엘리넬 2층으로 올라가 쿠디를 만나자.)#k", 8, 1500000, 1500000);
            break;
        }
        case 6: {
            qm.dispose();
            qm.forceCompleteQuest();
            break;
        }
    }
}