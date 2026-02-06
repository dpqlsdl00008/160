var status = -1;

function start() {
    status = -1;
    action (1, 0, 0);
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
            cm.sendSimpleS("판타스틱 테마 파크를 즐길 준비가 됐어? 퍼니 스테이션, 익스트림 스테이션, 판타지 스테이션을 순서대로 이용 하는 것이 좋을거야~#b\r\n#L0#퍼니 스테이션 <1>\r\n#L1#익스트림 스테이션 <1>\r\n#L2#판타지 스테이션 <1>", 4);
            break;
        }
        case 1: {
            cm.dispose();
            cm.warp(selection == 0 ? 223010000 : selection == 1 ? 223020100 : 223030000, "west00");
            break;
        }
    }
}