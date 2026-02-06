var status = -1;

function start() {
    status = -1;
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (mode == 1) {
        status++;
    } else {
        if (status == 0) {
            cm.dispose();
            return;
        }
        status--;
    }
    switch (status) {
        case 0: {
            var say = "#e<아스완 해방전>#n\r\n\r\n여전히 아스완 지역을 배회하고 있는 힐라의 잔당들을 소탕하시겠습니까?\r\n";
            say += "\r\n#L0##b힐라의 잔당을 소탕한다.#k"
            say += "\r\n#L1##b힐라를 직접 처치한다. #r(120 레벨 이상)#k#k";
            cm.sendSimple(say);
            break;
        }
        case 1: {
            if (selection == 0) {
                cm.dispose();
                cm.openNpc(2182001);
                return;
            }
            cm.sendNext("\r\n#b힐라의 탑 입구#k로 보내드리겠습니다. 힐라를 꼭 물리쳐주세요.");
            break;
        }
        case 2: {
            cm.dispose();
            cm.warp(262030000, "west00");
            break;
        }
    }
}