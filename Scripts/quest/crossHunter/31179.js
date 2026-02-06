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
            qm.sendNext("#h #님을 기다리고 있었습니다. 그 동안 저는 저 이외에 다른 요원들에게 아카이럼의 영향이 미쳤는지 조사하러 다녔죠. 다행히 다른 요원들은 괜찮더군요. 과거의 상황은 어떻게 되었습니까?");
            break;
        }
        case 1: {
            qm.sendNextPrevS("다행히도 늦지 않게 아카이럼의 분신을 해치웠습니다.", 2);
            break;
        }
        case 2: {
            qm.sendNextPrev("최근 들은 소식 중 가장 좋은 소리군요. 그럼 이제 아카이럼의 음모는 모두 막아 낸 셈인가요?");
            break;
        }
        case 3: {
            qm.sendNextPrevS("아직 아카이럼의 본신이 남아 있다고 합니다. 륀느님께서 시간의 균열을 통해 그가 있는 차원의 틈으로 접근 할 수 있다고 하셨습니다.", 2);
            break;
        }
        case 4: {
            qm.sendNextPrev("이번에야 말로 그를 확실하게 물리쳐야 합니다. 마음 같아서는 #h #님에게 휴식을 주고 싶지만 때가 때인지라 그럴 수 없는 것이 미안합니다만.");
            break;
        }
        case 5: {
            qm.sendNextPrev("그럼 정비가 끝나는 데로 제게 다시 말을 걸어 주세요.");
            break;
        }
        case 6: {
            qm.dispose();
            qm.forceCompleteQuest();
            break;
        }
    }
}