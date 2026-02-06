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
                case 914090010: {
                    cm.lockInGameUI(true);
                    cm.reservedEffect("Effect/Direction1.img/aranTutorial/Scene0");
                    break;
                }
                case 914090011: {
                    cm.reservedEffect("Effect/Direction1.img/aranTutorial/Scene1" + gender);
                    break;
                }
                case 914090012: {
                    cm.reservedEffect("Effect/Direction1.img/aranTutorial/Scene2" + gender);
                    break;
                }
                case 914090013: {
                    cm.reservedEffect("Effect/Direction1.img/aranTutorial/Scene3");
                    break;
                }
                case 914090100: {
                    cm.lockInGameUI(true);
                    cm.reservedEffect("Effect/Direction1.img/aranTutorial/HandedPoleArm" + gender);
                    cm.getPlayer().changeJob(2100);
                    cm.resetStats(4, 4, 4, 4);
                    cm.forceCompleteQuest();
                    break;
                }
                case 914090200: {
                    cm.lockInGameUI(true);
                    cm.reservedEffect("Effect/Direction1.img/aranTutorial/Maha");
                    break;
                }
                case 914090201: {
                    cm.lockInGameUI(true);
                    cm.reservedEffect("Effect/Direction1.img/aranTutorial/PoleArm");
                    break;
                }
            }
        }
    }
}