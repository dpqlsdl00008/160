var status = -1;

function start() {
    action (1, 0, 0);
}

function action(mode, type, selection) {
    if (mode == 1) {
        status++;
    } else {
        cm.dispose();
        return;
    }
    switch (status) {
        case 0: {
            if (cm.getMonsterCount(cm.getMapId()) != 0) {
                cm.sendNextS("\r\n살려주세요... 몬스터들 때문에 꼼짝도 못 하겠어요!\r\n#b(몬스터들을 모두 처리한 뒤 다시 말을 걸어보자.)#k", 4);
                cm.dispose();
                return;
            }
            cm.sendNextS("\r\n훌쩍, 훌쩍... 너무 무서웠어요.", 4, 1500017, 1500017);
            break;
        }
        case 1: {
            cm.sendNextPrevS("\r\n형들이랑 누나들이랑 연극을 연습하고 있었는데 만드라고라가 습격했어요. 너무 무서워서 눈을 꼭 감고 있었는데, 정신을 차려보니 이런 곳에 떨어져 있었어요... 훌쩍, 훌쩍.", 4, 1500017, 1500017);
            break;
        }
        case 2: {
            cm.sendNextPrevS("\r\n#b(한 명의 아이라도 찾아서 다행이다. 이아이를 엘리넬로 돌려보내자.)#k", 2);
            break;
        }
        case 3: {
            cm.dispose();
            cm.forceStartQuest(32135, "1");
            break;
        }
    }
}