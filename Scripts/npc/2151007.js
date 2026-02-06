function start() {
    var say = "원하는 수련장으로 갈 수 있는 엘리베이터이다. 원하는 층을 정확히 선택하자.#b";
    say += "\r\n#L0#지하 2층 트레이닝 룸 A";
    say += "\r\n#L1#지하 3층 트레이닝 룸 B";
    say += "\r\n#L2#지하 4층 트레이닝 룸 C";
    say += "\r\n#L3#지하 5층 트레이닝 룸 D";
    say += "\r\n#L4#지하 6층 트레이닝 룸 포스";
    cm.sendSimple(say);
}

function action(mode,type,selection) {
    cm.dispose();
    if (mode == 1) {
        switch (selection) {
            case 4: {
                if (cm.getPlayerCount(931000400) != 0) {
                    cm.sendNext("이미 다른 유저가 수령 중에 있이므로, 입장 할 수 없습니다.");
                    return;
                }
                cm.getMap(931000400).resetFully();
                cm.warp(931000400, 0);
                break;
            }
            default: {
                var v1 = ((selection + 1) * 100);
                cm.resetMap(310010000 + v1);
                cm.warp(310010000 + v1);
                break;
            }
        }
    }
}