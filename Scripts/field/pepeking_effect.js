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
            cm.showFieldEffect(false, "pepeKing/chat/nugu");
            var v1 = 3300005;
            var v2 = "B";
            if (Packages.server.Randomizer.nextInt(100) > 50) {
                v1 = 3300006;
                v2 = "G";
            }
            if (Packages.server.Randomizer.nextInt(100) > 50) {
                v1 = 3300007;
                v2 = "W";
            }
            if (cm.getMap().getNumMonsters() < 1) {
                cm.showFieldEffect(false, "pepeKing/pepe/pepe" + v2);
                cm.spawnMonster(v1, -40, -68);
            }
            break;
        }
    }
}