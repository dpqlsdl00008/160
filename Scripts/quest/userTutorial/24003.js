var status = -1;

function start(mode, type, selection) {
    if (mode == 1) {
        status++;
    } else {
        qm.dispose();
        return;
    }
    switch (status) {
        case 0: {
            qm.sendYesNo("#b(오르골을 가동해 음악이 흘러나오게 하자.)#k");
            break;
        }
        case 1: {
            qm.dispose();
            qm.sendNext("#b(서늘한 마을의 분위기와 어울리지 않는 평화로운 음악이 흘러나온다. 아이들이 좋은 꿈 꾸길...)#k");
            qm.changeMusic("Bgm25/SoundOfElf");
            qm.forceCompleteQuest();
            break;
        }
    }
}