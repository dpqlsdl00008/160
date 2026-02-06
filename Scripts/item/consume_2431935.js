var status = -1;
var canSkill = new Array();

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
    var mData = Packages.provider.MapleDataProviderFactory.getDataProvider(Packages.provider.MapleDataProviderFactory.fileInWZPath("Skill.wz")).getData(Packages.tools.StringUtil.getLeftPaddedStr(cm.getJob(), '0', 3) + ".img");
    if (mData != null) {
        var it = mData.iterator();
        while (it.hasNext()) {
            var v1 = it.next();
            var v2 = v1.getChildren().iterator();
            while (v2.hasNext()) {
                var v3 = v2.next();
                var maxLv = Packages.provider.MapleDataTool.getIntConvert("maxLevel", v3.getChildByPath("common"), 0);
                if (maxLv > 20) {
                    if (cm.getPlayer().getMasterLevel(v3.getName()) < 20) {
                        canSkill.push(v3.getName());
                    }
                }
            }
        }
    }
    switch (status) {
        case 0: {
            var say = "자네가 올릴 수 있는 스킬의 목록은 다음과 같네.";
            if (canSkill.length < 1) {
                cm.dispose();
                cm.sendNextS("자네는 아직 어떤 4차 스킬도 배우지 않았거나 현재 이 마스터리 북이 적용 될 스킬이 없는 모양일세. 확인해 보고 다음에 다시 사용하게나.", 4);
                return;
            }
            for (i = 0; i < canSkill.length; i++) {
                say += "\r\n#L" + i + "##s" + canSkill[i] + "# #d" + Packages.client.SkillFactory.getSkill(canSkill[i]).getName() + "";
            }
            cm.sendSimpleS(say, 4);
            break;
        }
        case 1: {
            cm.dispose();
            if (cm.getPlayer().getTotalSkillLevel(canSkill[selection]) < 5) {
                cm.sendNextS("아직 마스터리 북을 사용하기엔 자네가 고른 스킬의 레벨이 낮네. 해당 스킬의 레벨을 5 이상 올려 주게.", 4);
                return;
            }
            cm.gainItem(2431935, -1);
            cm.teachSkill(canSkill[selection], cm.getPlayer().getTotalSkillLevel(canSkill[selection]), 20);
            cm.sendNextS("\r\n#s" + canSkill[selection] + "# #d" + Packages.client.SkillFactory.getSkill(canSkill[selection]).getName() + "#k의 마스터 레벨을 올렸습니다.", 4);
            break;
        }
    }
}