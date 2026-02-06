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
            qm.sendNextS("성의라고는 없는 대답이지만 말하는 나무의 말이 맞을 지도 모른다.", 16);
            break;
        }
        case 1: {
            qm.sendNextPrevS("나에게나 어제 있었던 일인 검은 마법사와의 치열했던 싸움도, 다른 자들에게는 이미 수백 년, 역사 책에서나 나올 일에 불과하다...", 16);
            break;
        }
        case 2: {
            qm.sendNextPrevS("메이플 월드를 지키기 위해 길러왔던 힘을 모두 잃었다. 하찮은 슬라임과 싸우면서도 허덕이는 나는, 이리도 약하다.", 16);
            break;
        }
        case 3: {
            qm.sendNextPrevS("예전과는 상황이 다르다. 믿을 수 있는 동료들과 함께하던 그 떄와 달리 지금 내 곁에는 아무도 없다... 아무도...", 16);
            break;
        }
        case 4: {
            qm.sendNextPrevS("하지만... 그렇지만! 그렇다고 해도 포기 할 수 없어!", 16);
            break;
        }
        case 5: {
            qm.sendNextPrevS("나는 일어서야만 해! 왜냐하면 난 왕이니까! 검은 마법사의 저주로 얼음 속에 갇혀버린 백성들을 가진 엘프의 왕...!", 16);
            break;
        }
        case 6: {
            qm.sendNextPrevS("#b왕에게 포기라는 것은 허락되지 않는다!#k", 16);
            break;
        }
        case 7: {
            qm.sendNextPrevS("포기 할 수 없다면 해야 할 일은 정해져 있다! 고통 받는 백성들을 저주에서 구해야지! 힘이 없다고? 그렇다면 힘을 키우면 되잖아! 힘을 키운다면 검은 마법사가 내린 저주도 완전히 깨뜨릴 수 있을 거야!", 16);
            break;
        }
        case 8: {
            qm.sendNextPrevS("너무나 약해져 버렸지만... 경험을 쌓다 보면 다시 한 번 예전의 힘을 되찾을 수 있을 거야. 조급해 하지 말고 차근 차근... 내가 해야 할 일, 할 수 있는 일을 하자.\r\n\r\n#fUI/UIWindow.img/QuestIcon/8/0# 2,000 exp", 16);
            break;
        }
        case 9: {
            qm.dispose();
            qm.gainExp(2000);
            qm.forceCompleteQuest();
            break;
        }
    }
}