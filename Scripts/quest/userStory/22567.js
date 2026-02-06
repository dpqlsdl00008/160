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
            if (qm.isQuestActive(22567) == false) {
                qm.dispose();
                qm.forceStartQuest();
            } else {
                qm.sendNext("#b(은밀한 벽돌을 꺼내 구해온 성장촉진제를 넣었다.)#k");
            }
            break;
        }
        case 1: {
            qm.sendNextPrev("#b(은밀한 벽돌을 구멍 안으로 밀어 넣어 원래 상태로 만들었다.)#k\r\n\r\n#fUI/UIWindow.img/QuestIcon/8/0# 25,000 exp\r\n#fUI/UIWindow.img/QuestIcon/10/0# 2 sp");
            break;
        }
        case 2: {
            if (qm.isQuestFinished(22567) == false) {
                qm.gainItem(4032468, -10);
                qm.gainExp(25000);
                qm.getPlayer().gainSP(2, Packages.constants.GameConstants.getSkillBook(qm.getJob()));
                qm.forceCompleteQuest();
            }
            qm.sendNextPrevS("#b휴... 임시 멤버라고 해서 쉬울 줄 알았는데, 이것도 생각보다 힘드네. 하지만 과연 비밀 단체 같은 느낌이라 좀 재밌기도 하다. 그렇지?#k", 3);
            break;
        }
        case 3: {
            qm.sendNextPrevS("나름 흥미 진진한 일이잖아, 마스터. 그런데 이 단체는 도대체 성장 촉진제를 어디에 쓰려는 걸까? 내가 먹어도 마구 자라날려나?", 5, 1013000);
            break;
        }
        case 4: {
            qm.sendNextPrevS("#b그, 글쎄. 허클씨가 동물이 먹었다가는 부작용이 일어 날 수도 있다고 했으니 안 먹는 게 좋을 것 같은데...#k", 3);
            break;
        }
        case 5: {
            qm.sendNextPrevS("응? 잠깐, 마스터. 그럼 내가 동물이란 말이야?", 5, 1013000);
            break;
        }
        case 6: {
            qm.sendNextPrevS("#b인간도 다 동물이라잖아. 아하하.#k", 3);
            break;
        }
        case 7: {
            qm.sendNextPrevS("그걸로 납득하기 어려운데... 흠. 좋아. 이번만은 넘어가지.", 5, 1013000);
            break;
        }
        case 8: {
            qm.sendNextPrevS("#b그것보다 성장 촉진제는 아마 작물을 잘 자라게 하려고 그러는 거 아닐까? 농장에서도 가끔 밖에 비료를 줬거든. 잘 자라서 식량을 많이 만들 생각인거지.#k", 3);
            break;
        }
        case 9: {
            qm.sendNextPrevS("오호, 그렇군. 과연 식량이 많으면 굶는 사람이 없을 테니 좋겠지? 좋은 일을 하는 단체잖아?", 5, 1013000);
            break;
        }
        case 10: {
            qm.sendNextPrevS("#b응. 그런 것 같지?#k", 3);
            break;
        }
        case 11: {
            qm.dispose();
            break;
        }
    }
}