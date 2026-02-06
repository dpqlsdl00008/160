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
            qm.sendNext("메르세데스님...");
            break;
        }
        case 1: {
            qm.sendNextPrevS("아스틸라! 몸은 괜찮은 건가?!", 2);
            break;
        }
        case 2: {
            qm.sendNextPrev("네. 이 늙은 몸도 다행히 아픈 곳 없이 건강하답니다. 힘은 약해졌지만, 문제는 없습니다.");
            break;
        }
        case 3: {
            qm.sendNextPrevS("하아... 다행이군. 필리우스나 다니카는 튼튼하니 괜찮지만 아스틸라는 혹시나 걱정했어.", 2);
            break;
        }
        case 4: {
            qm.sendNextPrev("왕께서 걱정해 주신 덕분에 무사하답니다. 호호호... 왕께서는 혼자 먼저 깨어나 많은 일을 겪으셨다고요?");
            break;
        }
        case 5: {
            qm.sendNextPrevS("응. 하지만 어려운 일은 없었어.", 2);
            break;
        }
        case 6: {
            qm.sendAcceptDecline("어린 나이에 왕위에 올라 검은 마법사까지 만나 어려움이 많으셨던 당신이 이렇게 의젓한 얼굴을 하시다니... 시련이 사람을 성장시킨다는 말은 역시 틀리지 않는 모양이군요.\r\n\r\n#fUI/UIWindow.img/QuestIcon/8/0# 1,000 exp");
            break;
        }
        case 7: {
            qm.sendNext("이제 우리들... 엘프의 세 장로가 모두 모였습니다. 아직 다른 백성들은 검은 마법사의 저주에 의해 얼음 속에 갇혀 고통 받고 있지만... 왕이 계시고 우리가 있는데 무엇이 두렵겠습니까.");
            break;
        }
        case 8: {
            qm.dispose();
            qm.gainExp(1000);
            qm.forceCompleteQuest();
            break;
        }
    }
}