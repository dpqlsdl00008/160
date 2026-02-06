var status = -1;
var currentSkill = [1075, 1076, 1077, 1078, 1079];
var nextSkillLevel = 2;

function start(mode, type, selection) {
    if (mode == 1) {
        status++;
    } else {
        if (status == 0) {
            qm.dispose();
            return;
        }
        status--;
    }
    switch (status) {
        case 0: {
             qm.dispose();
	     for (i = 0; i < currentSkill.length; i++) {
                 if (qm.getPlayer().getTotalSkillLevel(currentSkill[i]) > 0) {
                     qm.teachSkill(currentSkill[i], nextSkillLevel, nextSkillLevel);
                     qm.showinfoMessage(Packages.client.SkillFactory.getSkill(currentSkill[i]).getName() + "의 레벨이 1 올랐습니다.");
                 }
             }
             qm.forceCompleteQuest();
             break;
        }
    }
}