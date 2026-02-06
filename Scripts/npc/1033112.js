var status = -1;

function start() {
    if (cm.isQuestActive(24058) == true) {
        status = 2;
    }
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
            var say = "(더 이상 에우렐에 볼 일이 있다면 신비한 포탈을 통해 다른 마을로 갈 수 있다. 다만, 메소가 필요한 듯 하다... 어느 마을로 갈까? 다른 마을로 가는 데는 #d2,000 메소#k가 들어간다.)#d";
            if (Packages.constants.GameConstants.isMercedes(cm.getJob()) == true) {
                say = "(더 이상 에우렐에 볼 일이 없다면 신비한 포탈을 통해 다른 마을로 갈 수 있다... 어느 마을로 갈까?)";
            }
            say += "\r\n#L101000000#1. 엘리니아";
            say += "\r\n#L100000000#2. 헤네시스";
            say += "\r\n#L102000000#3. 페리온";
            say += "\r\n#L103000000#4. 커닝시티";
            cm.sendSimple(say);
            break;
        }
        case 1: {
            cm.dispose();
            if (Packages.constants.GameConstants.isMercedes(cm.getJob()) == false) {
                if (cm.getMeso() < 2000) {
                    return;
                }
                cm.gainMeso(-2000);
            }
            cm.warp(selection, 0);
            break;
        }
        case 2: {
            cm.dispose();
            break;
        }
        case 3: {
            cm.sendYesNo("(더 이상 에우렐에 볼 일이 없다면 신비한 포탈을 통해 다른 곳으로 갈 수 있다... 이번만은 윈스턴이 있는 거친 바위 지대로 갈까?)");
            break;
        }
        case 4: {
            cm.dispose();
            cm.warp(102020400, 0);
            break;
        }
    }
}