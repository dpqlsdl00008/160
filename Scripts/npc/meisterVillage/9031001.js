// 약초 채집을 배우지 않은 상태로 초기화 합니다. 지금 까지 쌓은 레벨과 숙련도가 모두 사라집니다. 정말 초기화를 하시겠습니까?

var status = -1;
var skillID = 92000000;
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
            var say = "안녕하세요. 무엇을 도와드릴까요?#Cgreen#";
            if (skillLevel < 1) {
                say += "\r\n#L0##e약초 채집#n에 대한 설명을 듣는다.";
                say += "\r\n#L1##e약초 채집#n을 배운다.";
            } else {
                if (skillLevel < 10) {
                    say += "\r\n#L2##e약초 채집#n의 레벨을 올린다.";
                }
                say += "\r\n#L3#약초 뿌리를 교환한다.";
            }
            cm.sendSimple(say);
            break;
        }
        case 1: {
            switch (selection) {
                case 0: {
                    cm.dispose();
                    cm.sendNext("\r\n약초 채집은 필드 곳곳에 있는 약초를 채집하는 스킬입니다. 이렇게 채집 한 약초를 맹스가 판매하는 오일 병에 담아 제련하게 되면 장비, 장신구, 연금술 등에 필요한 재료가 됩니다.");
                    break;
                }
                case 1: {
                    cm.sendYesNo("#Cgreen#약초 채집#k을 배웁니다. 비용은 #Cgreen#5,000메소#k입니다. 정말 배우시겠습니까?\r\n\r\n#Cyellow#(현재 습득 하신 전문 기술 갯수 : " + professionCount() + "개)#k");
                    break;
                }
                case 2: {
                    status = 3;
                    if (cm.getPlayer().getProfessionExp(skillID) < professionProficiency(skillLevel)) {
                        cm.sendNext("\r\n약초 채집의 레벨을 올리기 위한 숙련도가 충분하지 않습니다. #Cyellow#" + (skillLevel + 1) + " 레벨#k로 올리기 위해서는 #Cyellow#" + cm.getPlayer().getNum(professionProficiency(skillLevel) - cm.getPlayer().getProfessionExp(skillID)) + "의 숙련도#k가 필요로 합니다.");
                        cm.dispose();
                        return;
                    }
                    cm.sendYesNo("약초 채집의 숙련도를 모두 채웠네요. 다음 레벨이 되기 위해서는 #Cgreen#" + cm.getPlayer().getNum(professionReqMeso(skillLevel)) + " 메소#k의 수강료가 필요합니다. 배우시겠어요?");
                    break;
                }
                case 3: {
                    if (cm.haveItem(4022023, 100) == false) {
                        cm.dispose();
                        cm.sendNext("\r\n#Cgreen#약초 뿌리 100개#k는 #i2028066# #Cgreen##z2028066# 1개#k로 교환해 드립니다. 약초 뿌리를 더 모아 오세요.");
                        return;
                    }
                    status = 5;
                    cm.sendGetNumber("스타첼의 약초 주머니를 몇 개 원하시나요?\r\n#Cgreen#약초 뿌리 100개#k는 #i2028066# #Cgreen##z2028066# 1개#k로 교환해 드립니다.", 1, 1, (cm.itemQuantity(4022023) / 100));
                    break;
                }
            }
            break;
        }
        case 2: {
            cm.dispose();
            cm.teachSkill(skillID, 0x1000000, 0);
            cm.sendNext("\r\n좋아. 약초 채집을 성공적으로 익혔습니다. 숙련도를 다 채워야지만 레벨을 올릴 수 있으니 다시 찾아오세요.");
            break;
        }
        case 3: {
            cm.dispose();
            break;
        }
        case 4: {
            cm.dispose();
            cm.getPlayer().changeProfessionLevelExp(skillID, skillLevel + 1, 0);
            cm.getPlayer().getTrait(Packages.client.MapleTrait.MapleTraitType.sense).addExp(5);
            cm.sendNext("\r\n약초 채집의 레벨을 " + skillLevel + "로 올려 드렸습니다. 그리고 추가 보상으로 감성을 5만큼 올려드렸으니, 확인해 보세요.");
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
            cm.gainItem(4022023, -(100 * selection));
            cm.gainItem(2028066, selection);
            cm.sendNext("\r\n약초 뿌리는 언제든지 교환해 드립니다.");
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