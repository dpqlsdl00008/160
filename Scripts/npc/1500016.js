var status = -1;

function start() {
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
            if (cm.getMonsterCount(cm.getMapId()) != 0) {
                cm.sendNextS("\r\n저 음흉한 두더지를 처치해주세요!\r\n#b(몬스터들을 모두 처리한 뒤 다시 말을 걸어보자.)#k", 4);
                cm.dispose();
                return;
            }
            cm.sendNextS("\r\n저희를 구해주시다니... 정말 고맙습니다.", 4, 1500016, 1500016);
            break;
        }
        case 1: {
            cm.sendNextPrevS("\r\n이 은혜는 평생 잊지 않을게요!", 4, 1500016, 1500016);
            break;
        }
        case 2: {
            cm.dispose();
            cm.forceStartQuest(32135, "3");
            break;
        }
    }
}