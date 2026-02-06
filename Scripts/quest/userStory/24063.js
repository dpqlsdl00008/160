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
            qm.sendNext("메르세데스님! 메르세데스님! 보고 싶었어요! 우아앙~ 태연한 척 했지만 혹시 우리 다시 못 보게 되는 건 아닐까 엄~청 무서웠다고요!");
            break;
        }
        case 1: {
            qm.sendNextPrevS("몸은 괜찮은 거냐, 다니카?", 2);
            break;
        }
        case 2: {
            qm.sendNextPrev("네! 괜찮아요! 튼튼한 거 하나는 어느 엘프 못지 않은 게 자랑인걸요! 예전처럼 스킬을 쓸 자신은 없지만, 어쨌든 아픈 곳은 없어요!");
            break;
        }
        case 3: {
            qm.sendNextPrevS("다행이구나...", 2);
            break;
        }
        case 4: {
            qm.sendAcceptDecline("그럼요! 다행이지요! 수백 년이나 지난 세상에 혼자 계시다니! 얼마나 쓸쓸하셨어요! 우와앙!~ 이제 다니카가 메르세데스님과 함께 있어 드릴게요!\r\n\r\n#fUI/UIWindow.img/QuestIcon/8/0# 1,000 exp");
            break;
        }
        case 5: {
            qm.sendNext("훌쩍, 당장 같이 나가고 싶지만 다니카는 아직 힘을 많이 못 찾아서 같이 다니면 방해 밖에 안 될 거예요. 지금 다시 수련 중이시라면서요, 메르세데스님? 이런 상황에 메르세데스님을 방해했다가는 필리우스 오빠가 목을 조를 지도 몰라요.");
            break;
        }
        case 6: {
            qm.sendNextPrev("그러니 일단 전 마을에서 힘을 되 찾을 수 있도록 노력할게요. 메르세데스님의 수련에 방해되지 않도록 열심히요! 그러니 메르세데스님도 #b열심히 수련하셔서 꼭 다른 사람들도 깨어 날 수 있게 해주셔야 해요!#k");
            break;
        }
        case 7: {
            qm.dispose();
            qm.gainExp(1000);
            qm.forceCompleteQuest();
            break;
        }
    }
}