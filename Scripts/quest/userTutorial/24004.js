var status = -1;

function start(mode, type, selection) {
    if (mode == 1) {
        status++;
    } else {
        qm.dispose();
        return;
    }
    switch (status) {
        case 0: {
            qm.sendAcceptDecline("#b(결계를 만들 마법의 언어가 보인다. 주문을 외우면 에우렐의 결계를 완성할 수 있다. 적어도 100년간 누구도 이 마을에 침입할 수 없게 하는 강력한 결계를... 결계를 칠까?)#k");
            break;
        }
        case 1: {
            qm.dispose();
            qm.sendOk("#b(결계가 만들어졌다. 이제 마을은 안전하겠지...)#k");
            qm.forceCompleteQuest();
            break;
        }
    }
}