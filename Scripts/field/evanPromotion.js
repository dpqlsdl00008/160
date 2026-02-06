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
            var gender = (cm.getPlayer().getGender() == 0 ? "0" : "1");
            switch (cm.getMapId()) {
                case 900090000: {
                    cm.movieClipInGameUI(true);
                    cm.reservedEffect("Effect/Direction4.img/promotion/Scene0" + gender);
                    break;
                }
                case 900090001: {
                    cm.reservedEffect("Effect/Direction4.img/promotion/Scene1");
                    break;
                }
                case 900090002: {
                    cm.reservedEffect("Effect/Direction4.img/promotion/Scene2" + gender);
                    break;
                }
                case 900090003: {
                    cm.reservedEffect("Effect/Direction4.img/promotion/Scene3");
                    break;
                }
                case 900090004: {
                    cm.movieClipInGameUI(false);
                    cm.warp(900010000, 0);
                    break;
                }
            }
        }
    }
}