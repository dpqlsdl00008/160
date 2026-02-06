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
            qm.sendNext("장로들의 순백의 정화마저 통하지 않는 이유는 어떻게 생각해도 하나뿐입니다. 침착하게 들어주심시오, 메르세데스님. #r검은 마법사의 봉인이 약해지거나, 이미 봉인에서 풀려난 것#k 같습니다.");
            break;
        }
        case 1: {
            qm.sendNextPrev("생각해 보면 간단합니다. 검은 마법사의 저주에 균열이 생겼다는 건 왕께서 눈을 뜨신 것으로 확신 할 수 있습니다. 게다가 약해지셨던 왕께서는 지속적인 수련으로 힘을 되찾고 계시죠.");
            break;
        }
        case 2: {
            qm.sendNextPrev("그에 따라 우리 종족 전체의 힘도 분명 조금씩 강해지고 있습니다. 그럼에도 불구하고 정작 다른 종족들은 저주를 이겨내지 못하고 있지요. 무언가 가로막고 있는 것 처럼...");
            break;
        }
        case 3: {
            qm.sendNextPrev("결론은 하나입니다. #r검은 마법사가 내린 저주 자체가, 균열에도 불구하고 강해지는 것 입니다.#k");
            break;
        }
        case 4: {
            qm.sendNextPrev("검은 마법사의 저주가 강해진다는 것은, 다시 말해 검은 마법사의 봉인이 약해져 그가 가진 힘이 메이플 월드에 돌아오고 있다... 라고 해석 할 수 밖에 없습니다.");
            break;
        }
        case 5: {
            qm.sendNextPrev("결론적으로 말하자면, 지금의... 과거에서 수백 년이 흐른 메이플 월드는 위기에 빠져 있습니다. 바로, 검은 마법사에 의한.\r\n\r\n#fUI/UIWindow.img/QuestIcon/8/0# 10,000 exp");
            break;
        }
        case 6: {
            qm.dispose();
            qm.gainExp(10000);
            qm.forceCompleteQuest();
            break;
        }
    }
}