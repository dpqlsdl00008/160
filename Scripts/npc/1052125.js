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
            var say = "잠깐만요! 여기부터는 리모델링 중이어서 관계자 외 출입이 제한 된 구역입니다. 조건에 맞는 분만 입장을 시켜 드릴 수 있어요.";
            say += "\r\n#L0##b난 #e혁이#n를 돕고 있는 중이야.#k";
            say += "\r\n#L1##b난 백화점의 #e#rVIP#k#n#b라고!!#k";
            cm.sendSimple(say);
            break;
        }
        case 1: {
            switch (selection) {
                case 1: {
                    cm.dispose();
                    cm.warp(103040440, "right01");
                    return;
                }
            }
            cm.sendNext("\r\n아아, 제 동기생인 #b혁이#k를 도와주고 계신 #b#h ##k님 이셨군요. 7, 8층 #b일반 존#k으로 입장 시켜 드릴게요. VIP존 이용은 30분에 한번 가능합니다. 자, 그럼 어서 들어가세요.");
            break;
        }
        case 2: {
            cm.dispose();
            cm.warp(103040410, 1);
            break;
        }
    }
}