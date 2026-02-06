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
            qm.sendNext("헤이, " + (qm.getPlayer().getGender() == 0 ? "맨" : "예쁜이") + "~ 나좀 잠깐 볼까? 하하! 나는 새로운 여행자들에게 여러가지를 알려주는 교관, 로저야.");
            break;
        }
        case 1: {
            qm.sendNextPrev("그런걸 누가 하라고 시켰냐구? 아하하! you는 호기심이 많군! 좋아 좋아~ 이건 내가 자진해서 하는 일이야.");
            break;
        }
        case 2: {
            qm.getPlayer().addHP(-25);
            qm.sendNextPrev("놀랐지? HP가 0이 되면 큰일난다구. 자, #r로저의 사과#k를 줄 테니 먹어봐. 힘이 날거야. 아이템 창을 열어서 더블클릭해봐. 아이템창은 #bI키#k를 누르면 간단히 열린다구.");
            break;
        }
        case 3: {
            qm.sendNextPrev("내가 준 로저의 사과, 전부 먹어야 돼. 먹으면 바로 HP가 회복되는 것이 보일 거야. HP를 전부 회복한 후 다시 말을 걸어줘.");
            break;
        }
        case 4: {
            qm.dispose();
            qm.gainItem(2010007, 1);
            qm.forceStartQuest();
            break;
        }
    }
}

function end(mode, type, selection) {
    if (mode == 1) {
        status++;
    } else {
        status--;
    }
    switch (status) {
        case 0: {
            qm.sendNext("아이템을 먹는 거... 어때? 간단하지? 오른쪽 하단 슬롯에 #b단축 아이콘#k으로 설정할 수도 있다구. 몰랐지? 하하~ 하지만 아이템이 없으면 가만히 있어도 알아서 회복되기도 해. 느리다는 단점이 있지만 초보 시절엔 이런 회복도 나쁘지 않은 방법이야.");
            break;
        }
        case 1: {
            qm.sendNextPrev("좋았어! 많은 걸 배웠으니 선물을 주지. 여행을 하려면 꼭 배워둬야 하는 거니까 나한테 고마워 하라구! 위급할 때 사용해.");
            break;
        }
        case 2: {
            qm.sendNextPrev("내가 가르쳐 줄 수 있는건 여기까지야. 아쉽지만 이제 헤어져야 할 시간이로군. 아무쪼록 몸조심 하라구. 그럼 잘가~!!!\r\n\r\n#fUI/UIWindow.img/QuestIcon/4/0#\r\n#i2010000# 사과 3개\r\n#i2010009# 초록 사과 3개\r\n\r\n#fUI/UIWindow.img/QuestIcon/8/0# 10 exp");
            break;
        }
        case 3: {
            qm.dispose();
            qm.gainExp(10);
            qm.gainItem(2010000, 3);
            qm.gainItem(2010009, 3);
            qm.forceCompleteQuest();
            break;
        }
    }
}