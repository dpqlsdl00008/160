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
            if (qm.isQuestActive(1674) == false) {
                qm.dispose();
                qm.forceStartQuest();
                return;
            }
            qm.sendNext("오셨군요. 기다리고 있었습니다. 이번 임무에서 '전설'을 만나셨다고요? 어떤 분이시던가요?");
            break;
        }
        case 1: {
            qm.sendNextPrevS("저도 자세한 건 잘 모릅니다. 일부러 모습을 잘 드러내지 않으셨어요.", 16);
            break;
        }
        case 2: {
            qm.sendNextPrev("아쉽군요. 명색이 제가 리더지만 그 분에 대해서는 아는 것이 거의 없습니다. 저도 #h #님처럼 그 분을 멀리서나마 뵐 기회가 있다면 좋을 텐데 말이죠. 아무튼 그 분의 인정이 있었으니 이제부터 #h #님은 #bS급 헌터#k이십니다.");
            break;
        }
        case 3: {
            qm.sendNextPrev("지금까지 이 칭호를 받은 자는 제가 아는 한 단 세 명 뿐이었죠. 스스로를 자랑스럽게 생각해주시면 고맙겠습니다. 그리고 이건 제가 드리는 개인적인 보상입니다. 부디 유용하게 쓰시길.");
            break;
        }
        case 4: {
            qm.sendNextPrev("언젠가 중요한 임무가 발생하면 다시 뵐 날이 있겠죠. 그 때 까지 건강하길 빌겠습니다.");
            break;
        }
        case 5: {
            qm.dispose();
            qm.showFieldEffect(false, "crossHunter/chapter/end4");
            qm.forceCompleteQuest();
            break;
        }
    }
}