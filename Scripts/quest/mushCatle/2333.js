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
            qm.sendNext("아! #b#h ##k님 당신이 저를 구해주러 오신 용사님이시군요. 와주실 줄 알았어요. 흑흑흑...");
            break;
        }
        case 1: {
            qm.sendNextPrevS("공주님 무사하십니까?", 2);
            break;
        }
        case 2: {
            qm.sendNextPrev("네. 저는 괜찮아요. 그보다 저희 아버님... 아버님은 무사 하신가요?");
            break;
        }
        case 3: {
            qm.sendNextPrevS("네. #b머쉬킹#k님께서는 각료 대신들과 함께 성 외곽 안전한 곳에 계십니다. 걱정 마십시오.", 2);
            break;
        }
        case 4: {
            qm.sendNextPrevS("네 이놈. 잘도 여기까지 왔구나. 하지만 이대로 끝날 것이라 생각했다면 큰 오산이다!", 4, 1300001);
            break;
        }
        case 5: {
            qm.sendNextPrev("앗! 용사님 위험해요!!! 이 모든 사건의 배후... 그를 소환하려 해요!");
            break;
        }
        case 6: {
            qm.sendNextPrevS("배후? 아니 그럼 누군가 또 있단 말씀인가요?", 2);
            break;
        }
        case 7: {
            qm.sendNextPrevS("시끄럽다! 이제 곧 그 분이 오신다!", 4, 1300001);
            break;
        }
        case 8: {
            qm.sendNextPrev("#b총리 대신!#k 총리 대신을 없애 주세요!!");
            break;
        }
        case 9: {
            qm.dispose();
            qm.spawnMonster(3300008, 0, 142);
            qm.forceStartQuest();
            break;
        }
    }
}

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
            qm.sendNext("#b#h ##k님 해내셨군요. 정말 감사 드려요.");
            break;
        }
        case 1: {
            qm.sendNextPrevS("헉! 총리 대신 마저...", 4, 1300001);
            break;
        }
        case 2: {
            qm.sendNextPrevS("#b페페킹#k 네 이놈!!! 이제 네 녀석의 야망도 끝이다! 목숨만은 살려줄테니 너희 나라 #b아이스 랜드#k로 물러 가도록 해라.", 2);
            break;
        }
        case 3: {
            qm.sendNextPrevS("잠깐!!! 가기 전에 너를 무찔렀다는 증거를 확보해 두어야 겠다.", 2);
            break;
        }
        case 4: {
            qm.sendNextPrevS("제길 부... 분하다... 두고보자. 이 굴욕 잊지 않겠다.\r\n\r\n#fUI/UIWindow.img/QuestIcon/4/0#\r\n\r\n#fUI/UIWindow.img/QuestIcon/8/0# 10,000 exp\r\n#fUI/UIWindow.img/QuestIcon/11/0# 카리스마 50", 4, 1300001);
            break;
        }
        case 5: {
            qm.dispose();
            qm.gainExp(10000);
            qm.getPlayer().getTrait(Packages.client.MapleTrait.MapleTraitType.charisma).addExp(50);
            qm.forceCompleteQuest();
            break;
        }
    }
}