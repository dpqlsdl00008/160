var status = -1;
var skillID = 92040000;
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
            var say = "안녕하세요. 혹시 연금술에 관심있으세요?#Cgreen#";
            if (skillLevel < 1) {
                say += "\r\n#L0##e연금술#n에 대한 설명을 듣는다.";
                say += "\r\n#L1##e연금술#n을 배운다.";
            } else {
                say += "\r\n#L2##e연금술#n의 레벨을 올린다.";
                say += "\r\n#L3#연금술의 레벨을 초기화한다.";
            }
            cm.sendSimple(say);
            break;
        }
        case 1: {
            switch (selection) {
                case 0: {
                    cm.dispose();
                    cm.sendNext("\r\n연금술은 허브의 오일을 이용하여 다양한 물약을 만드는 기술이랍니다. HP와 MP를 회복하는 물약부터 당신을 강하게 할 수 있는 다양한 물약도 만들 수 있어요. 지금까지 체험하지 못했던 신기한 물약도 당연히 만들 수 있구요.");
                    break;
                }
                case 1: {
                    if (cm.getPlayer().getProfessionLevel(92000000) < 1) {
                        cm.dispose();
                        cm.sendNext("\r\n연금술은 약초 채집을 배운 후에 가르쳐 드릴 수 있답니다. 약초 채집은 오른쪽으로 가시면 솥에 열심히 약초를 넣고 있는 약초 채집의 마스터 #Cyellow#스타첼#k에게 배울 수 있어요.");
                        return;
                    }
                    cm.sendYesNo("#Cgreen#연금술#k을 배워 보시고 싶으세요? 비용은 #Cgreen#5,000메소#k입니다. 정말 배우시겠어요?\r\n\r\n#Cyellow#(현재 습득 하신 전문 기술 갯수 : " + professionCount() + "개)#k");
                    break;
                }
                case 2: {
                    if (skillLevel > 9) {
                        cm.dispose();
                        cm.sendNext("\r\n넌 이미 나 이상의 전문가가 되었어... 흑흑... 더 이상 나에게 배울 것이 없어. #Cgreen#명장#k이 되려면 스스로를 연마해야 해. 곧 날 뛰어 넘을 지도 몰라! 이런... 훌륭한 제자...");
                        return;
                    }
                    status = 3;
                    if (cm.getPlayer().getProfessionExp(skillID) < professionProficiency(skillLevel)) {
                        cm.sendNext("\r\n연금술의 레벨을 올리기 위한 숙련도가 충분하지 않습니다. #Cyellow#" + (skillLevel + 1) + " 레벨#k로 올리기 위해서는 #Cyellow#" + cm.getPlayer().getNum(professionProficiency(skillLevel) - cm.getPlayer().getProfessionExp(skillID)) + "의 숙련도#k가 필요로 합니다.");
                        cm.dispose();
                        return;
                    }
                    cm.sendYesNo("연금술 숙련도를 모두 채우셨군요. 열심히 수련하셨나 봐요. 다음 레벨이 되기 위해서는 #Cgreen#" + cm.getPlayer().getNum(professionReqMeso(skillLevel)) + " 메소#k의 수강료가 필요하답니다. 배우시겠어요?");
                    break;
                }
                case 3: {
                    status = 5;
                    cm.sendYesNo("연금술을 배우지 않은 상태로 초기화합니다. 지금까지 쌓으신 레벨과 숙련도가 모두 초기화 됩니다. 정말 초기화 하시고 싶으세요?");
                    break;
                }
            }
            break;
        }
        case 2: {
            cm.dispose();
            cm.teachSkill(skillID, 0x1000000, 0);
            cm.sendNext("\r\n자아~ 연금술에 대한 기본적인 지식을 알려드렸어요. 그리고 숙련도가 다 차게 되면 연금술의 레벨을 올릴 수 있으니 꼭 저에게 다시 찾아와주세요. 새로운 지식을 알려 드릴게요.");
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
            if (skillLevel > 9) {
                cm.sendNext("\r\n연금술의 장인이 되셨군요! 굉장하네요! 덤으로 손재주도 5만큼 올려드렸어요. 제가 올려드린 손재주가 큰 도움이 되면 좋겠네요.");
            } else {
                cm.sendNext("\r\n연금술의 레벨을 " + skillLevel + "로 올렸으니까 확인해 봐. 그리고 이 멜츠님의 아름다운 힘으로 손재주를 5만큼 올려주... 어머~ 그렇게까지 감동 받은 표정을 지을 것 까진 없잖아!");
            }
            break;
        }
        case 5: {
            cm.dispose();
            break;
        }
        case 6: {
            cm.dispose();
            cm.teachSkill(skillID, 0, 0);
            cm.sendNext("\r\n연금술 기술을 초기화 해드렸어요. 다시 배우시고 싶으시다면 언제든지 찾아주세요!");
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