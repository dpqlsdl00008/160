var status = -1;

function end(mode, type, selection) {
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
            if (qm.isQuestActive(3759) == false) {
                qm.dispose();
                qm.forceStartQuest();
                return;
            }
            qm.sendNext("오, 드디어 구해 오셨군요. 잠시만 기다리세요. 곧 비약을 만들어 드리지요.");
            break;
        }
        case 1: {
            qm.sendNextPrev("자, 그럼 준비가 되셨나요? 준비가 되셨다면 바로 비약을 만들어 몸에 뿌려 드리도록 하지요. 그럼 하늘을 날 수 있습니다.");
            break;
        }
        case 2: {
            qm.sendNextPrev("자, 이제 플라잉 스킬을 사용 할 준비가 다 되었군요. 한 가지 주의 하실 점은 용족의 기운이 닿는 곳에서만 플라잉 스킬을 사용 하실 수 있다는 것 입니다. 아마도 이 곳 천공의 나루터 지역부터 해당이 될 것 입니다.");
            break;
        }
        case 3: {
            qm.sendNextPrev("또, 플라잉 스킬을 사용하고 있으면 #r지속적으로 MP가 소모#k 되므로 MP 관리에 유의하시기 바랍니다. 잘못해서 떨어 질 경우 #r평소보다 훨씬 큰 양의 낙하 데미지#k를 입게 되거든요. 잘못하면 목숨을 잃는다구요. 그럼 행운을 빌겠습니다.");
            break;
        }
        case 4: {
            qm.dispose();
            qm.teachSkill(Packages.client.PlayerStats.getSkillByJob(1026, qm.getPlayer().getJob()), 1, 1);
            qm.getPlayer().dropMessage(5, "<플라잉> 스킬을 획득 하였습니다.");
            qm.forceCompleteQuest();
            break;
        }
    }
}