var status = -1;
var skillID = 92020000;
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
            var say = "그래. 장비 제작의 달인, 이 #Cyellow#에이센#k님에게 원하는게 뭐지?#Cgreen#";
            if (skillLevel < 1) {
                say += "\r\n#L0##e장비 제작#n에 대한 설명을 듣는다.";
                say += "\r\n#L1##e장비 제작#n을 배운다.";
            } else {
                say += "\r\n#L2##e장비 제작#n의 레벨을 올린다.";
                say += "\r\n#L3#장비 제작의 레벨을 초기화한다.";
            }
            cm.sendSimple(say);
            break;
        }
        case 1: {
            switch (selection) {
                case 0: {
                    cm.dispose();
                    cm.sendNext("\r\n장비 제작은 채광으로 제련 한 광물과 보석을 이 거대한 용광로에 녹여 너에게 필요한 유용한 방어구와 무기를 만드는 기술이야. 지금까지 볼 수 없었던 무기와 방어구도 이 에이센님께 배우면 만들 수 있지.");
                    break;
                }
                case 1: {
                    if (cm.getPlayer().getProfessionLevel(92010000) < 1) {
                        cm.dispose();
                        cm.sendNext("\r\n채광을 배우지 않은 사람에게 장비 제작을 가르쳐주지 않고 있어. 재료가 없다고 끈기 있게 하지 못할테니... 이 옆에 있는 채광의 마스터 #Cyellow#노붐#k님께 먼저 채광을 배워 오게.");
                        return;
                    }
                    cm.sendYesNo("#Cgreen#장비 제작#k을 배우고 싶다고? 배우고 싶다면 수강료를 내야지. #Cgreen#5,000메소#k인데... 정말 배울 건가?\r\n\r\n#Cyellow#(현재 습득 하신 전문 기술 갯수 : " + professionCount() + "개)#k");
                    break;
                }
                case 2: {
                    if (skillLevel > 9) {
                        cm.dispose();
                        cm.sendNext("\r\n농담이지? 장비 제작의 명장이 나에게 배우려고 하다니. 그건 그렇고 명장을 내 눈으로 보게 될 줄이야. 오래 살고 볼 일 이군.");
                        return;
                    }
                    status = 3;
                    if (cm.getPlayer().getProfessionExp(skillID) < professionProficiency(skillLevel)) {
                        cm.sendNext("\r\n장비 제작의 레벨을 올리기 위한 숙련도가 충분하지 않습니다. #Cyellow#" + (skillLevel + 1) + " 레벨#k로 올리기 위해서는 #Cyellow#" + cm.getPlayer().getNum(professionProficiency(skillLevel) - cm.getPlayer().getProfessionExp(skillID)) + "의 숙련도#k가 필요로 합니다.");
                        cm.dispose();
                        return;
                    }
                    cm.sendYesNo("장비 제작 숙련도를 모두 채웠군. 어때? 장비 제작의 재미를 좀 알겠나? 다음 레벨이 되기 위해서는 #Cgreen#" + cm.getPlayer().getNum(professionReqMeso(skillLevel)) + " 메소#k의 수강료가 필요하지. 배우겠어?");
                    break;
                }
                case 3: {
                    status = 5;
                    cm.sendYesNo("장비 제작을 배우지 않은 상태로 초기화 하는가? 지금까지 쌓은 레벨과 숙련도가 모두 초기화 된다네. 정말 초기화 하고 싶나?");
                    break;
                }
            }
            break;
        }
        case 2: {
            cm.dispose();
            cm.teachSkill(skillID, 0x1000000, 0);
            cm.sendNext("\r\n좋아. 장비 제작을 성공적으로 익혔어. 숙련도가 다 차게 되면 장비 제작의 레벨을 올릴 수 있으니 다시 찾아와 레벨을 올리는 것 잊지 말고.");
            break;
        }
        case 3: {
            cm.dispose();
            break;
        }
        case 4: {
            cm.dispose();
            cm.getPlayer().changeProfessionLevelExp(skillID, skillLevel + 1, 0);
            cm.getPlayer().getTrait(Packages.client.MapleTrait.MapleTraitType.craft).addExp(5);
            cm.sendNext("\r\n장비 제작의 레벨을 " + skillLevel + "로 올려 주었어. 덤으로 이 에이센님이 손재주를 5만큼 올려주었으니 확인해 보는 게 어때?");
            break;
        }
        case 5: {
            cm.dispose();
            break;
        }
        case 6: {
            cm.dispose();
            cm.teachSkill(skillID, 0, 0);
            cm.sendNext("\r\n장비 제작 기술을 초기화했다네. 다시 배우고 싶다면 언제든지 찾아오게.");
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