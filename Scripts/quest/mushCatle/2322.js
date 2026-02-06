var status = -1;

function start(mode, type, selection) {
    if (mode == 1) {
        status++;
    } else {
        if (status == 0) {
            qm.sendNext("정말 성에 침입 할 수있는 다른 방법이 있습니까? 모르시면 저를 찾아와주세요.");
            qm.dispose();
            return;
        }
        status--;
    }
    switch (status) {
        case 0: {
            qm.sendYesNo("말씀 드린대로 결계를 뚫었다고 해도 안심 할 수 없어요. 저희 머쉬킹 왕국의 성은 외부에서 침입이 절대 불가능 하게끔 되어 있어서 침투가 쉽지 않을겁니다. 음.. 일단 한번 성벽 외부 쪽을 조사해 주시지 않겠습니까?");
            break;
        }
        case 1: {
            qm.dispose();
            qm.forceStartQuest();
            break;
        }
    }
}