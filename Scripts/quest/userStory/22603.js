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
            qm.sendNext("마스터~ 이것 봐. 이번 성장으로 거의 힘이 완성되어 가는 것 같아.");
            break;
        }
        case 1: {
            qm.sendNextPrevS("#b응. 보기만 해도 굉장한 위압감이 느껴질 정도인걸. 이게 오닉스 드래곤의 진정한 힘인가?#k", 2);
            break;
        }
        case 2: {
            qm.sendNextPrev("오닉스 드래곤의 힘이며 동시에 마스터의 힘이야. 오닉스 드래곤은 계약자가 강해져야만 성장하니까. 마스터의 마음이 그 만큼 많이 성장한 거겠지.");
            break;
        }
        case 3: {
            qm.sendNextPrevS("#b과연... 예전보다 어른스러운 말도 잘 하는구나, 미르.#k", 2);
            break;
        }
        case 4: {
            qm.sendAcceptDecline("흠흠. 당연하지. 이 우아한 모습으로 어리광 부리는 건 창피하니까 말이야. 그 것 보다 마스터, 전 처럼 이번에도 탈피 후에 남은 비늘이 하나 있어. 전 보다 더 강력한 힘이 담겨 있으니 아마 마스터에게 많은 도움이 될 거야. 자, 받아.");
            break;
        }
        case 5: {
            qm.dispose();
            qm.gainItem(1142157, 1);
            qm.forceCompleteQuest();
            qm.sendOk("마스터는 인간이라 몬스터들에게 타격을 받지만, 그 비늘을 이용해 좋은 아이템을 만들면 피해를 줄일 수 있을 거야. 그렇게 마스터가 강해지면 나 역시 또 다시 강해지고... 서로 함께 더 강해지자, 마스터.");
            break;
        }
    }
}