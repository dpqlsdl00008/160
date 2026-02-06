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
            qm.sendNextS("아... 수원에 오는 것도 정말 오랜만이군. 그러고보니 헬레나가 여기로 가출했던 적이 있지 않던가? 검은 마법사의 존재를 알지 못했던 정말 메이플 월드가 평화롭던 시절...", 16);
            break;
        }
        case 1: {
            qm.sendNextPrevS("수련도 좋지만 잠깐 과거를 회상해 볼까?", 16);
            break;
        }
        case 2: {
            qm.dispose();
            qm.warp(910150210, 0);
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
            qm.sendNext("훌쩍... 메, 메르세데스님...");
            break;
        }
        case 1: {
            qm.sendNextPrevS("여기 숨어 있었구나, 헬레나. 아스틸라가 놀랬잖니. 왜 말도 없이 이런 곳에 와 있는 거니?", 2);
            break;
        }
        case 2: {
            qm.sendNextPrev("훌쩍... 훌쩍...");
            break;
        }
        case 3: {
            qm.sendNextPrevS("그만 울고, 착하지? 착한 엘프는 우는 거 아니예요.", 2);
            break;
        }
        case 4: {
            qm.sendNextPrev("히잉.");
            break;
        }
        case 5: {
            qm.sendNextPrevS("뚝.", 2);
            break;
        }
        case 6: {
            qm.sendNextPrev("뚜, 뚝...!");
            break;
        }
        case 7: {
            qm.sendNextPrevS("아하하, 뚝 그렸구나, 착하다. 왜 그렇게 운 건지 말해주면 더 착한 아이일텐데.", 2);
            break;
        }
        case 8: {
            qm.sendNextPrev("왜... 저는 듀얼 보우건을 못 쓰는 건가요?");
            break;
        }
        case 9: {
            qm.sendNextPrevS("응? 너도 듀얼 보우건을 쓰고 싶었구나?", 2);
            break;
        }
        case 10: {
            qm.sendNextPrev("넹! 멋있잖아요! 메르세데스님처럼 두 개 들고 싸우고 싶었는데... 저, 저는 왜 그냥 활만 배워야 하는 거예요?");
            break;
        }
        case 11: {
            qm.sendNextPrevS("듀얼 보우건은... 싸우는 운명을 타고 난 사람만 쓰게 되어 있으니까.", 2);
            break;
        }
        case 12: {
            qm.sendNextPrev("싸우는...?");
            break;
        }
        case 13: {
            qm.sendNextPrevS("응? 그냥 혼잣 말이야. 듀얼 보우건... 듀얼 보우건이라, 양 손으로 쓰려면 무지 헷갈리고 어려운데 헬레나는 그래도 배우고 싶니?", 2);
            break;
        }
        case 14: {
            qm.sendNextPrev("...우웅... 어려워요?");
            break;
        }
        case 15: {
            qm.sendNextPrevS("왼 손도 오른 손하고 똑같이 써야 하니까. 쉽지는 않지. 게다가 사실 위력은 그냥 활이 더 나아. 듀얼 보우건은 열심히 뛰어다니면서 써야 하거든.");
            break;
        }
        case 16: {
            qm.sendNextPrev("헤, 헬레나는 달리기는 자신 없는데...");
            break;
        }
        case 17: {
            qm.sendNextPrevS("그래? 하지만 헬레나는 활을 쏘는 건 자신 있지? 전에 연습하는 거 보니까 백발 백중이던데.", 2);
            break;
        }
        case 18: {
            qm.sendNextPrev("에헤헤...");
            break;
        }
        case 19: {
            qm.sendNextPrevS("헬레나라면 분명 활의 명수가 될 거야. 듀얼 보우건을 안 써도 엄~청 강해질 걸? 다른 사람을 가르칠 정도가 될 지도 모르지.", 2);
            break;
        }
        case 20: {
            qm.sendNextPrev("정말요?");
            break;
        }
        case 21: {
            qm.sendNextPrevS("그럼. 정말이고 말고...", 2);
            break;
        }
        case 22: {
            qm.sendNextPrevS("(이 때도 헬레나는 고지식하고 성실했지. 고집도 셌지만 말이야. 이렇게 어리던 헬레나가 그렇게 자라다니... 세월이란 놀랍구나.)", 2);
            break;
        }
        case 23: {
            qm.dispose();
            qm.warp(101050020, 0);
            qm.forceCompleteQuest();
            break;
        }
    }
}