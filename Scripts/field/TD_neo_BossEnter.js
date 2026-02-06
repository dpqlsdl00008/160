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
            cm.dispose();
            var bNumber = parseInt((cm.getMapId() - 240070000) / 100);
            switch (bNumber) {
                case 2: {
                    cm.getPlayer().dropMessage(5, "삐걱거리는 기계음이 들려 온다. 근처에 무엇인가 있는 것 같다.");
                    break;
                }
                case 4: {
                    cm.getPlayer().dropMessage(5, "정적이 흐르더니 눈 앞에 눈부신 붉은 머리 칼의 천사가 나타났다.");
                    break;
                }
                case 5: {
                    cm.getPlayer().dropMessage(5, "정적이 흐르더니 눈 앞에 눈부신 검은 머리 칼의 천사가 나타났다.");
                    break;
                }
            }
            break;
        }
    }
}
