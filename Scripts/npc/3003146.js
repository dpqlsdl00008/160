var status = -1;

var value = 10000000;

var aSymbol = [
[2050011, 8000005], 
[2050012, 8000006], 
[2050013, 8000007], 
[2050014, 8000008], 
[2050015, 8000009], 
[2050016, 8000010], 
];

function start() {
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
            var say = "\r\n　 #Cgray#내가 자네에게 도움을 줄 수도 있을 것 같네만?\r\n\r\n";
            sType = (selection == 0 ? aSymbol : bSymbol);
            var tStat = 0;
            var pStat = 0;
            switch (selection) {
                case 0: {
                    for (i = 0; i < aSymbol.length; i++) {
                        tStat += (cm.getPlayer().getSkillLevel(aSymbol[i][1]) * 30);
                    }
                    say += "#Cgray# 　STR : +" + tStat + " | DEX : +" + tStat + " | INT : +" + tStat + " | LUK : +" + tStat + "\r\n";
                    break;
                }
                case 1: {
                    say += "#Cgray#　* 경험치 획득량 : +0% | 메소 획득량 : +0%\r\n";
                    say += "#Cgray#　* 보스 몬스터 공격 시 데미지 : +0%\r\n";
                    say += "#Cgray#　* 몬스터의 방어율 일정 수치 무시 : +0%\r\n";
                    say += "#Cgray#　* 크리티컬 확률 : +0% | 크리티컬 데미지 : +0%\r\n";
                    break;
                }
            }
            say += "\r\n　 #fUI/UIWindow.img/QuestIcon/3/0#\r\n";
            for (i = 0; i < sType.length; i++) {
                say += "#L" + i + "##i" + sType[i][0] + "#";
                if (cm.getPlayer().getOneInfoQuest(value, "item_level_" + sType[i][0]).equals("")) {
                    cm.getPlayer().updateOneInfoQuest(value, "item_level_" + sType[i][0], "0");
                }
                if (cm.getPlayer().getOneInfoQuest(value, "item_exp_" + sType[i][0]).equals("")) {
                    cm.getPlayer().updateOneInfoQuest(value, "item_exp_" + sType[i][0], "0");
                }
            }
	    
            cm.sendSimple(say);
            nSelection = selection;
            break;
        }
        case 1: {
	
	    var say = "#i" + sType[selection][0] + "# #d#z" + sType[selection][0] + "#\r\n";      
            var iLevel = parseInt(cm.getPlayer().getOneInfoQuest(value, "item_level_" + sType[selection][0]));
            var mLevel = 0;
            say += "\r\n#Cgray#* 성장 레벨 : " + iLevel;
            var iExp = "0 / 0 #d(0%)#k";
            var cExp = parseInt(cm.getPlayer().getOneInfoQuest(value, "item_exp_" + sType[selection][0]));
            var mExp = 0;
            switch (nSelection) {
                case 0: {
                    mLevel = 20;
                    mExp = Packages.constants.GameConstants.reqArcaneForceExp(iLevel);
                    break;
                }
                case 1: {
                    mLevel = 10;
                    mExp = Packages.constants.GameConstants.reqMapleForceExp(iLevel);
                    break;
                }
            }
            if (iLevel > 0) {
                iExp = cExp + " / " + mExp + " #d(" + parseInt((cExp / mExp) * 100) + "%)#k";
            }
            if (iLevel > (mLevel - 1)) {
                iExp = "#dMax#k";
            }
            say += "\r\n#Cgray#* 성장 수치 : " + iExp;
            var ySkill = 0;
            var rMeso = 0;
            switch (nSelection) {
                case 0: {
                    ySkill = cm.getPlayer().getSkillLevel(sType[selection][1]) * 30;
                    rMeso = Packages.constants.GameConstants.reqArcaneForceMeso(iLevel);
                    say += "\r\n";
                    say += "\r\n#Cgray#* STR : +" + ySkill + " | DEX : +" + ySkill + " | INT : +" + ySkill + " | LUK : +" + ySkill;
                    break;
                }
                case 1: {
                    rMeso = Packages.constants.GameConstants.reqMapleForceMeso(iLevel);
                    say += "\r\n";
                    switch (sType[selection][0]) {
                        case 2050025: {
                            say += "\r\n#Cgray#* 경험치 획득량 : +0%";
                            break;
                        }
                        case 2050026: {
                            say += "\r\n#Cgray#* 메소 획득량 : +0%";
                            break;
                        }
                        case 2050027: {
                            say += "\r\n#Cgray#* 보스 몬스터 공격 시 데미지 : +0%";
                            break;
                        }
                        case 2050028: {
                            say += "\r\n#Cgray#* 몬스터의 방어율 일정 수치 무시 : +0%";
                            break;
                        }
                        case 2050029: {
                            say += "\r\n#Cgray#* 크리티컬 확률 : +0%";
                            break;
                        }
                        case 2050030: {
                            say += "\r\n#Cgray#* 크리티컬 데미지 : +0%";
                            break;
                        }
                    }
                    break;
                }
            }
            if (iLevel != 0 && iLevel < mLevel && cExp == mExp) {
                say += "\r\n";
                say += "\r\n#Cgreen#* 요구 메소 : #i4310116# #z4310116# " + cm.getPlayer().getNum(rMeso) + "개#k";
                cm.sendYesNo(say);
            } else {
		
                cm.dispose();
                cm.sendNext(say);
            }
            fSymbol = sType[selection][0];
            fSkill = sType[selection][1];
            fLevel = iLevel;
            fMeso = rMeso;
            fSelection = nSelection;
            break;
        }
        case 2: {
            cm.dispose();
            if (!cm.haveItem(4310116, fMeso)) {
                cm.sendNext("\r\n#i" + fSymbol + "# #d#z" + fSymbol + "##k의 강화를 진행하기 위한 메소가 충분하지 않습니다.");
                return;
            }
            cm.gainItem(4310116, -fMeso);
            cm.teachSkill(fSkill, fLevel + 1, 20);
            cm.getPlayer().updateOneInfoQuest(value, "item_level_" + fSymbol, "" + (fLevel + 1));
            cm.getPlayer().updateOneInfoQuest(value, "item_exp_" + fSymbol, "0");
            var say = "#d#z" + fSymbol + "##k의 성장을 성공적으로 완료하였습니다.";
            say += "\r\n\r\n#fUI/UIWindow.img/QuestIcon/4/0#";
            say += "\r\n#i" + fSymbol + "# #d#z" + fSymbol + "# Lv." + (fLevel + 1) + "#k";
            cm.sendNext("\r\n" + say);
            break;
        }
    }
}