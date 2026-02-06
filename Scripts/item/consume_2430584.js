var status = -1;

var nSkill = [
[8000080, "공격력 및 마력"],
[8000081, "최소 및 최대 크리티컬 데미지"],
[8000082, "몬스터의 방어율 일정 수치 무시"],
[8000083, "보스 몬스터 공격 시 데미지 증가"], 
];

function start() {
    action (1, 0, 0);
}

function GetTLV() {
    var nTS = 0;
    nTS += cm.getPlayer().getTotalSkillLevel(8000080);
    nTS += cm.getPlayer().getTotalSkillLevel(8000081);
    nTS += cm.getPlayer().getTotalSkillLevel(8000082);
    nTS += cm.getPlayer().getTotalSkillLevel(8000083);
    return nTS;
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
            if (GetTLV() > 29) {
                cm.dispose();
                cm.sendNext("\r\n#Cgray#HEXA 능력치 강화의 최대 강화 횟수는 #Cyellow#30회#Cgray#입니다. #Cgreen#HEXA 능력치 초기화#Cgray#를 진행 후에 #z2430584#을 사용 할 수 있습니다.");
                return;
            }
            var say = "\r\n　 #Cgray#강화를 진행 할 HEXA 능력치를 선택해 주세요.#Cgreen#";
            for (i = 0; i < nSkill.length; i++) {
                say += "\r\n#L" + i + "#" + nSkill[i][1] + "";
            }
            cm.sendSimple(say);
            break;
        }
        case 1: {
            s = selection;
            nAdd = (selection == 0 ? "+2" : "+1%");
            say = "#Cgray#강화를 진행 할 HEXA 능력치는 아래와 같습니다.\r\n";
            say += "\r\n#Cgreen#" + nSkill[selection][1] + " : " + nAdd;
            cm.sendAcceptDecline(say);
            break;
        }
        case 2: {
            cm.dispose();
            cm.gainItem(2430584, -1);
            cm.teachSkill(nSkill[s][0], cm.getPlayer().getTotalSkillLevel(nSkill[s][0]) + 1, cm.getPlayer().getTotalSkillLevel(nSkill[s][0]) + 1);
            var say = "\r\n#Cgray#HEXA 능력치 강화를 성공적으로 완료하였습니다.\r\n";
            say += "\r\n#e적용 된 HEXA 능력치 : #n";
            say += "\r\n- " + nSkill[s][1] + " : " + nAdd;
            cm.sendNext(say);
            break;
        }
    }
}