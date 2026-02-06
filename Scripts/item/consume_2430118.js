var status = -1;

var nAdv = [
[112, "히어로", 100, 110, 111], 
[122, "팔라딘", 100, 120, 121], 
[132, "다크 나이트", 100, 130, 131], 
[212, "아크 메이지 (불 & 독)", 200, 210, 211], 
[222, "아크 메이지 (얼음 & 번개)", 200, 220, 221], 
[232, "비숍", 200, 230, 231], 
[312, "보우 마스터", 300, 310, 311], 
[322, "신궁", 300, 320, 321], 
[412, "나이트로드", 400, 410, 411], 
[422, "섀도어", 400, 420, 421], 
[434, "듀얼 블레이드", 400, 430, 431], 
[512, "바이퍼", 500, 510, 511], 
[522, "캡틴", 500, 520, 521], 
];
var nCgn = [
[1112, "소울 마스터", 1100, 1110, 1111], 
[1212, "플레임 위자드", 1200, 1210, 1211], 
[1312, "윈드 브레이커", 1300, 1310, 1311], 
[1412, "나이트워커", 1400, 1410, 1411], 
[1512, "스트라이커", 1500, 1510, 1511], 
];
var nRes = [
[3212, "배틀 메이지", 3200, 3210, 3211], 
[3312, "와일드 헌터", 3300, 3310, 3311], 
[3512, "메카닉", 3500, 3510, 3511], 
];
var nHer = [
[2112, "아란", 2100, 2110, 2111], 
[2312, "메르세데스", 2300, 2310, 2311], 
];

function action(mode, type, selection) {
    if (mode == 1) {
        status++;
    } else {
        cm.dispose();
        return;
    }
    switch (status) {
        case 0: {
            if (cm.getPlayer().getLevel() < 120) {
                cm.dispose();
                cm.sendNext("\r\n이노시스의 전직서는 #e#r120 레벨 이상의 캐릭터#k#n 만 사용 할 수 있습니다.");
                return;
            }
            var say = "";
            say += "　 #Cgray#변경을 원하시는 직업 분류를 선택해 주세요.#Cgreen#";
            say += "\r\n#L0#1. 모험가";
            say += "\r\n#L1#2. 시그너스 기사단";
            say += "\r\n#L3#3. 영웅";
            say += "\r\n#L2#4. 레지스탕스";
            cm.sendSimple(say);
            break;
        }
        case 1: {
            var say = "";
            say += "　 #Cgray#변경을 원하시는 직업을 선택해 주세요.#Cgreen#";
            nCategory = (selection == 0 ? nAdv : selection == 1 ? nCgn : selection == 2 ? nRes : nHer);
            for (i = 0; i < nCategory.length; i++) {
                if (cm.getPlayer().getJob() == nCategory[i][0]) {
                    continue;
                }
                say += "\r\n#L" + i + "#" + nCategory[i][1];
            }
            cm.sendSimple(say);
            break;
        }
        case 2: {
            s = selection;
            var say = "#Cgray#이노시스 전직의 서를 사용하여 #Cyellow#" + nCategory[selection][1] + "#Cgray#(으)로 직업을 변경합니다.";
            cm.askAcceptDecline(say);
            break;
        }
        case 3: {
            cm.dispose();
            cm.gainItem(2430118, -1);
            cm.getPlayer().handleResetSkillByChangeJob();
            if (nCategory[s][0] == 434) {
                cm.getPlayer().handleSkillMasterByChangeJob(430);
                cm.getPlayer().handleSkillMasterByChangeJob(431);
                cm.getPlayer().handleSkillMasterByChangeJob(432);
                cm.getPlayer().handleSkillMasterByChangeJob(433);
            } else {
                cm.getPlayer().handleSkillMasterByChangeJob(nCategory[s][2]);
                cm.getPlayer().handleSkillMasterByChangeJob(nCategory[s][3]);
                cm.getPlayer().handleSkillMasterByChangeJob(nCategory[s][4]);
            }
            if (nCategory[s][0] == 2112) {
                cm.teachSkill(8001049, 30, 30);
                cm.teachSkill(8001050, 30, 30);
                cm.teachSkill(8001051, 30, 30);
                cm.teachSkill(8001052, 30, 30);
                cm.teachSkill(8001053, 30, 30);
            }
            cm.getPlayer().changeJob(nCategory[s][0]);
            cm.getPlayer().resetStats(4, 4, 4, 4);
            break;
        }
    }
}