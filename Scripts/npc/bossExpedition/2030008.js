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
            var say = "뭐... 좋소. 당신들은 충분한 자격이 되어 보이는군. 무엇을 하시겠소?#b";
            say += "\r\n#L0#폐광 동굴을 조사하러 떠난다.";
            say += "\r\n#L1#자쿰 던전을 탐사한다.";
            say += "\r\n#L2#자쿰에게 바칠 제물을 받는다.";
            say += "\r\n#L3#엘나스로 이동한다.";
            cm.sendSimple(say);
            break;
        }
        case 1: {
            if (selection == 0) {
                cm.dispose();
                cm.warp(280010000, 0);
                return;
            }
            if (selection == 1) {
                status = 5;
                var say = "잠깐!! 어느 자쿰의 제단으로 이동하시겠소?#b";
                say += "\r\n#L0#노멀 자쿰";
                say += "\r\n#L1#카오스 자쿰";
                cm.sendSimple(say);
                return;
            }
            if (selection == 2) {
                if (cm.haveItem(4001017, 1)) {
                    cm.sendNext("\r\n이미 자쿰의 제물인 #b#z4001017##k을 가지고 있군... 다 사용하면 다시 말하시오.");
                    cm.dispose();
                    return;
                }
                cm.gainItem(4001017, 3);
                cm.sendOk("좋소. 자쿰의 제물인 #b#z4001017##k을 드리겠소. 자쿰의 제단에 떨어뜨리면 된다오.");
                cm.dispose();
                return;
            }
            if (selection == 3) {
                cm.dispose();
                cm.warp(211000000, 0);
                return;
            }
            break;
        }
        case 6: {
            cm.dispose();
            cm.warp(selection == 0 ? 211042400 : 211042401, "west00");
            break;
        }
    }
}
