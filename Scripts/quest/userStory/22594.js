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
            qm.sendNext("무슨 일인가. 너에게는 나의 가르침이 필요치 않아 보이는데... 음? 좀비를 많이 퇴치하면 좋냐고? 그야 물론이다. 좀비들이 아니라면 이 엘나스도 좀 더 발전 할 수 있을 것이니. 네게 힘이 있다면 좀비 퇴치에 힘쓰도록.");
            break;
        }
        case 1: {
            qm.sendNextPrevS("#b(이번에는 좋은 일을 한 게 맞는 걸까?)#k", 2);
            break;
        }
        case 2: {
            qm.sendNextPrev("하지만 좀비를 퇴치 한 후에 이빨 처리는 조심해야 할 것이야. 좀비의 잃어버린 어금늬에는 검은 힘이 들어 있으니까. 함부로 다뤘다간 검은 힘에 물들어 버릴 수도 있지. 샤모스처럼 말이야. 죄를 뉘우치길 바랬지만 그러긴 커녕 점점 더 약해져만 가니...");
            break;
        }
        case 3: {
            qm.sendNextPrevS("#b샤모스가 무슨 잘못이라도 했나요?#k", 2);
            break;
        }
        case 4: {
            qm.sendNextPrev("얼마 전에 샤모스가 장로의 관저 지하실 열쇠를 복사해 가지고 있는 것을 확인했다. 일단 압수했지만, 한 두 개 복사 한 게 아니라 더 있을 게 분명해. 한 동안은 지하실의 단속을 더 철저히 해야겠지.");
            break;
        }
        case 5: {
            qm.sendNextPrevS("#b...! 지하실에는 뭐가 있나요?#k", 2);
            break;
        }
        case 6: {
            qm.sendAcceptDecline("오래 전부터 엘나스에서 보호하고 있던 보물이다. 정확한 것은 말해 줄 수 없다. 잃어버려서는 안 될 물건인 것은 확실하지. 더는 묻지 마라. 널 의심해야 할 수도 있으니...\r\n\r\n#fUI/UIWindow.img/QuestIcon/8/0# 23,000 exp");
            break;
        }
        case 7: {
            qm.sendNextPrev("그렇다고 그렇게까지 어두운 얼굴이 될 필요는 없다. 네가 보물을 훔친 것도 아니고, 보물 훔치는 것을 도운 것도 아닌데 뭐가 그리 걱정이냐. #r방어 체제가 약해진 것은 사실#k이지만 잃어버리지 않도록 더 주의하고 조심하면 돼.");
            break;
        }
        case 8: {
            qm.dispose();
            qm.gainExp(23000);
            qm.forceCompleteQuest();
            break;
        }
    }
}