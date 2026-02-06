var status = -1;

function start(mode, type, selection) {
    if (mode == 1) {
        status++;
    } else {
        if (status == 0) {
            qm.dispose();
            return;
        }
        if (status == 14) {
            qm.sendNext("우왓? 농담이겠지? 손이 미끄러진 거지? 자, 다시 한 번 수락하기를 눌러줘~");
            qm.dispose();
            return;
        }
        status--;
    }
    switch (status) {
        case 0: {
            qm.sendNext("역시 마스터와 난 이어져 있는 게 분명해! 마스터가 강해지면 나도 강해지고, 내가 강해지면 그 힘을 쓸 수 있는 마스터도 강해지는 거지! 그게 바로 우리의 계약이야! 역시 마스터와 계약하길 잘했다니까!");
            break;
        }
        case 1: {
            qm.sendNextPrevS("#b헤에, 그렇구나... 그런데 도대체 어떻게 우리가 계약하게 된 거야?#k", 2);
            break;
        }
        case 2: {
            qm.sendNextPrev("음... 그건 나도 잘 몰라. 알 상태였을 때는 잘 기억이 안 나거든. 꼭 꿈 속의 일처럼 어렴풋하게 기억나. 안개 낀 숲에서 마스터가 이쪽으로 걸어오고 있었어. 그리고 날 보고 깜짝 놀랐지. 난 마스터를 불렀고.");
            break;
        }
        case 3: {
            qm.sendNextPrevS("#b(어, 그건 내 꿈하고 비슷한데? 혹시 우리는 꿈에서 만날 걸까? 혹시 꿈 속에서 본 커다란 드래곤이 미르?)#k", 2);
            break;
        }
        case 4: {
            qm.sendNextPrev("마스터와 나는 영혼의 파장이 잘 맞았어. 난 마스터라면 나와 계약할 수 있을 거라는 걸 단번에 알아챘지. 그래서 마스터에게 계약하자고 말했어. 마스터는 계약의 대가를 치뤄주었고.");
            break;
        }
        case 5: {
            qm.sendNextPrevS("#b내가 뭘 했던가?#k", 2);
            break;
        }
        case 6: {
            qm.sendNextPrev("날 알아차리고 내게 손을 댔잖아? 잊어버린 거야? 그게 내가 정한 계약의 조건이었어. 알이었던 내게 손을 댄 순간 계약이 성립되면서 우리의 영혼이 연결된 거지.");
            break;
        }
        case 7: {
            qm.sendNextPrevS("#b영혼의... 연결?#k", 2);
            break;
        }
        case 8: {
            qm.sendNextPrev("응! 지금의 마스터와 나는 두 개의 몸을 가진 한 명이나 다름 없어. 그러니 내가 강해지면 마스터도 강해지고, 또 마스터가 강해지면 내가 강해지는 거지! 굉장하지?");
            break;
        }
        case 9: {
            qm.sendNextPrevS("#b뭔지 모르지만 엄청 대단한 이야기인 것 같은걸?#k", 2);
            break;
        }
        case 10: {
            qm.sendNextPrev("당연히 대단하지! 이제 몬스터 따위는 걱정할 거 없어. 마스터한테는 내가 있으니까 말이야. 내가 몬스터에게서 지켜줄게, 마스터! 자, 당장 시험해 봐도 좋아!");
            break;
        }
        case 11: {
            qm.sendNextPrevS("#b근데... 여기 평화로운 농장이라 위험한 몬스터는 없어.#k", 2);
            break;
        }
        case 12: {
            qm.sendNextPrev("에엑? 그렇단 말이야? 그거 재미 없는데... 마스터는 모험 같은 거 안 해? 사람들을 위해 몬스터와 싸우고 마왕을 물리치고, 이런 거 안 해?");
            break;
        }
        case 13: {
            qm.sendNextPrevS("#b당장은 예정이 없는데...#k", 2);
            break;
        }
        case 14: {
            qm.sendAcceptDecline("으으... 하지만 드래곤 마스터가 그렇게 평화롭게 살 수 있을리가 없어! 조만간에 내 실력을 보여줄 기회가 올 거야! 그 때 나하고 같이 모험가는 거다, 마스터?\r\n\r\n#fUI/UIWindow.img/QuestIcon/8/0# 810 exp");
            break;
        }
        case 15: {
            if (qm.isQuestFinished(22507) == false) {
                qm.gainExp(810);
                qm.forceCompleteQuest();
            }
            qm.sendNext("에헤헷, 그럼 앞으로도 계속 잘 부탁할게. 마스터.");
            break;
        }
        case 16: {
            qm.sendNextPrevS("#b(잘은 모르지만 드래곤 마스터가 되어 미르와 함께 지내게 되었다. 언젠가는 모험을 떠나게 될지도 모르겠다.)#k", 2);
            break;
        }
        case 17: {
            qm.sendNextPrevS("#b(하지만 일단 급한 건 심부름이다. 뭔가 하실 말씀이 있다고 하셨으니 아빠에게 가보자.)#k", 2);
            break;
        }
        case 18: {
            qm.dispose();
            break;
        }
    }
}