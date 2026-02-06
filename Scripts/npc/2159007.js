var status = -1;

function start() {
    status = 9;
    if (cm.getInfoQuest(23007).indexOf("vel01=2") == -1) {
        status = -1;
    }
    if (cm.getInfoQuest(23007).indexOf("vel01=2") != -1) {
        status = 6;
    }
    if (cm.getMapId() == 931000021 || cm.getMapId() == 931000030) {
        status = 9;
    }
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
            cm.sendNext("..어 어라? 어떻게 된 거지? 아까 그 진동 때문에 유리가 약해진건가..? 깨졌잖아?");
            break;
        }
        case 1: {
            cm.sendNextPrevS("자, 이제 널 막는 건 없는 거지? 그럼 같이 가자!", 2);
            break;
        }
        case 2: {
            cm.sendNextPrev("하, 하지만...");
            break;
        }
        case 3: {
            cm.sendNextPrevS("혹시 여기에 계속 있고 싶은 거야?", 2);
            break;
        }
        case 4: {
            cm.sendNextPrev("그럴 리가 없잖아! 실험체로 살고 싶지는 않아!");
            break;
        }
        case 5: {
            cm.sendNextPrevS("그럼 같이 가자! 어서!", 2);
            break;
        }
        case 6: {
            cm.dispose();
            cm.updateInfoQuest(23007, "vel00=2;vel01=2");
            cm.warp(931000020, 1);
            break;
        }
        case 7: {
            cm.sendNext("시, 실험실 밖으로 나온 건 정말 오랜만이야.. 여긴 어디야?");
            break;
        }
        case 8: {
            cm.sendNextPrevS("우리 마을, 에델슈타인으로 가는 길이야! 아까 그 이상한 블랙윙들이 따라오기 전에 얼른 도망치자.", 2);
            break;
        }
        case 9: {
            cm.dispose();
            cm.updateInfoQuest(23007, "vel00=2;vel01=3");
            cm.delayEffect("Effect/OnUserEff.img/guideEffect/aranTutorial/tutorialArrow1", 0);
            break;
        }
        case 10: {
            cm.dispose();
            break;
        }
    }
}