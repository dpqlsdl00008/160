var status = -1;
var skillID = 92010000;
var skillLevel = 0;

function start() {
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (mode == 1) {
        status++;
    } else {
        cm.dispose();
        return;
    }
    skillLevel = cm.getPlayer().getProfessionLevel(skillID);
    switch (status) {
        case 0: {
            var say = "그래, 이 채광의 달인인 이 #Cyellow#노붐#k님에게 원하는 것이 무엇인가?#Cgreen#";
            if (skillLevel < 1) {
                say += "\r\n#L0##e채광#n에 대한 설명을 듣는다.";
                say += "\r\n#L1##e채광#n을 배운다.";
            } else {
                if (skillLevel < 10) {
                    say += "\r\n#L2##e채광#n의 레벨을 올린다.";
                }
                say += "\r\n#L3#원석의 파편을 교환한다.";
            }
            cm.sendSimple(say);
            break;
        }
        case 1: {
            switch (selection) {
                case 0: {
                    cm.dispose();
                    cm.sendNext("\r\n채광은 필드 곳곳에 있는 광석을 채집하는 스킬이야. 이렇게 채집 한 원석을 라피니르트가 판매하는 거푸집에 담아 제련하게 되면 장비, 장신구, 연금술 등에 필요한 재료가 되지.");
                    break;
                }
                case 1: {
                    cm.sendYesNo("#Cgreen#채광#k을 배우게 된다. 정말 이대로 배울거지? 약간의 비용이 드는데 #Cgreen#5,000메소#k야. 그 정도 돈은 있는거지?\r\n\r\n#Cyellow#(현재 습득 하신 전문 기술 갯수 : " + professionCount() + "개)#k");
                    break;
                }
                case 2: {
                    status = 3;
                    if (cm.getPlayer().getProfessionExp(skillID) < professionProficiency(skillLevel)) {
                        cm.sendNext("\r\n채광의 레벨을 올리기 위한 숙련도가 충분하지 않습니다. #Cyellow#" + (skillLevel + 1) + " 레벨#k로 올리기 위해서는 #Cyellow#" + cm.getPlayer().getNum(professionProficiency(skillLevel) - cm.getPlayer().getProfessionExp(skillID)) + "의 숙련도#k가 필요로 합니다.");
                        cm.dispose();
                        return;
                    }
                    cm.sendYesNo("채광의 숙련도를 모두 채웠군. 다음 레벨이 되기 위해서는 #Cgreen#" + cm.getPlayer().getNum(professionReqMeso(skillLevel)) + " 메소#k의 수강료가 필요하지. 배우겠어?");
                    break;
                }
                case 3: {
                    if (cm.haveItem(4011010, 100) == false) {
                        cm.dispose();
                        cm.sendNext("\r\n#Cgreen#원석의 파편 100개#k는 #i2028067# #Cgreen##z2028067# 1개#k로 교환해 드립니다. 원석의 파편을 더 가져 오세요.");
                        return;
                    }
                    status = 5;
                    cm.sendGetNumber("노붐의 광물 주머니를 몇 개 원하시나요?\r\n#Cgreen#원석의 파편 100개#k는 #i2028067# #Cgreen##z2028067# 1개#k로 교환해 드립니다.", 1, 1, (cm.itemQuantity(4011010) / 100));
                    break;
                }
            }
            break;
        }
        case 2: {
            cm.dispose();
            cm.teachSkill(skillID, 0x1000000, 0);
            cm.sendNext("\r\n좋아. 채광의 기초 지식을 너에게 전수했어. 숙련도를 채우면 다음 레벨이 되어 새로운 걸 배울 수 있으니 숙련도를 채우면 다시 찾아와 줘.");
            break;
        }
        case 3: {
            cm.dispose();
            break;
        }
        case 4: {
            cm.dispose();
            cm.getPlayer().changeProfessionLevelExp(skillID, skillLevel + 1, 0);
            cm.getPlayer().getTrait(Packages.client.MapleTrait.MapleTraitType.will).addExp(5);
            cm.sendNext("\r\n채광의 레벨이 " + skillLevel + "가 되었어. 선물로 의지를 5만큼 올려 주었으니 확인해 보는 게 어때?");
            break;
        }
        case 5: {
            cm.dispose();
            break;
        }
        case 6: {
            cm.dispose();
            if (selection < 1) {
                return;
            }
            cm.gainItem(4011010, -(100 * selection));
            cm.gainItem(2028067, selection);
            cm.sendNext("\r\n원석의 파편은 언제든지 교환해 드립니다.");
            break;
        }
    }
}

function professionCount() {
    var count = 0;
    var totalSkillID = [92000000, 92010000, 92020000, 92030000, 92040000];
    for (i = 0; i < totalSkillID.length; i++) {
        count += (cm.getPlayer().getProfessionLevel(totalSkillID[i]) > 0 ? 1 : 0);
    }
    return count;
}

function professionProficiency(cLevel) {
    var aExp = 0;
    switch (cLevel) {
        case 2: {
            aExp = 100;
            break;
        }
        case 3: {
            aExp = 300;
            break;
        }
        case 4: {
            aExp = 600;
            break;
        }
        case 5: {
            aExp = 1000;
            break;
        }
        case 6: {
            aExp = 1500;
            break;
        }
        case 7: {
            aExp = 2100;
            break;
        }
        case 8: {
            aExp = 2800;
            break;
        }
        case 9: {
            aExp = 3600;
            break;
        }
    }
    return (cLevel * 250) + aExp;
}

function professionReqMeso(cLevel) {
    var rMeso = 0;
    switch (cLevel) {
        case 1: {
            rMeso = 15000;
            break;
        }
        case 2: {
            rMeso = 25000;
            break;
        }
        case 3: {
            rMeso = 30000;
            break;
        }
        case 4: {
            rMeso = 40000;
            break;
        }
        case 5: {
            rMeso = 60000;
            break;
        }
        case 6: {
            rMeso = 85000;
            break;
        }
        case 7: {
            rMeso = 115000;
            break;
        }
        case 8: {
            rMeso = 150000;
            break;
        }
        case 9: {
            rMeso = 190000;
            break;
        }
    }
    return rMeso;
}