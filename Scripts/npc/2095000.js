var status = -1;

function start() {
    status = -1;
    if (cm.getMapId() != 925010200) {
        status = 4;
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
            cm.sendNext("\r\n당신은 누구시죠..? 네..? 슈린츠가 보내서 왔다구요?");
            break;
        }
        case 1: {
            cm.sendYesNo("다행이다.. 정말 무서웠어요. 그런데 아직도 저를 노리는 무서운 몬스터들이 많이 있어요. 그 몬스터들을 물리쳐 주시면 좋겠어요.");
            break;
        }
        case 2: {
            cm.sendNext("\r\n정말 감사해요, 6분 동안 몬스터들로부터 저를 보호해 주시면 된답니다.");
            break;
        }
        case 3: {
            cm.dispose();
            cm.warp(925010300, 0);
            break;
        }
        case 4: {
            cm.dispose();
            break;;
        }
        case 5: {
            if (cm.isQuestActive(6410) == false) {
                cm.dispose();
                cm.warp(120000104, 0);
                return;
            }
            cm.sendNext("\r\n저를 구해주셔서 정말 고마워요. 당신은 친절한 사람이군요.");
            break;
        }
        case 4: {
            cm.dispose();
            cm.forceStartQuest(6411, "p2");
            cm.warp(120000104, 0);
            break;
        }
    }
}