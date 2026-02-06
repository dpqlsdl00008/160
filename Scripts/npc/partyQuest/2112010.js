var status = -1;

function start() {
    switch (cm.getMapId()) {
        case 926100203:
        case 926110203: {
            cm.dispose();
            break;
        }
        default: {
            status = -1;
        }
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
            cm.sendNext("\r\n... 오랜 기다림 끝에... 내 연구를 인정하지 않는 마가티아의 멍청한 놈들에게 진정한 고확이 뭔지 보여줄 수 있는 때가 왔도다!! 응? 너희는!? 제뉴미스트와 알카드노의 개인가?");
            break;
        }
        case 1: {
            var say = "큭큭큭. 이것도 괜찮지. 내 연구의 희생물이 되기에 딱 좋아. 영광으로 생각하게나. 자네들이야 말로 최고의 기계 공학과 연금술이 결합되는 것을 보는 거니까 말이야!!#d";
            say += "\r\n#L0#멈춰요! 당신 때문에 마가티아는 전쟁 일보 직전이에요!!";
            if (cm.getPlayer().getIntNoRecord(7923) > 4) {
                say += "\r\n#L1#멈춰요! 당신이 인정 받도록 도와주겠어요.";
            }
            cm.sendSimple(say);
            break;
        }
        case 2: {
            cm.sendNext("\r\n#Cgray#(유레테는 조금 흔들리는 듯 하다.)#k\r\n\r\n시... 시끄럽다!! 이제와서 그런 소리를 누가 믿을 것 같으냐!!");
            break;
        }
        case 3: {
            cm.sendNextPrev("\r\n#Cgray#(유레테의 중얼거림이 들린다.)#k\r\n\r\n크크... 제뉴미스트와 알카느도 놈들. 내 연구성과를 알면 깜짝 놀랄테지. 그 멍청한 놈들에게 연금술과 기계 공학으로 만들어낸 내 최고의 창조물을 보여주면, 놈들도 나를 존경하게 되겠지. 크... 크크");
            break;
        }
        case 4: {
            cm.dispose();
            cm.removeNpc(cm.getMapId(), cm.getNpc());
            var mobID = (cm.getPlayer().getIntNoRecord(7923) > 4 ? 9300140 : 9300139);
            if (cm.getMap().getMonsterById(mobID) == null) {
                cm.spawnMonster(mobID, 250, 150);
            }
            cm.mapMessage(6, "유레테가 기계 장치를 조작하자 거대한 괴물이 나타났다.");
            cm.floatMessage("프랑켄로이드를 퇴치하고 " + (cm.getMapId() == 926100401 ? "줄리엣을" : "로미오를") + " 지켜주세요!", cm.getMapId() == 926100401 ? 5120021 : 5120022);
            break;
        }
    }
}