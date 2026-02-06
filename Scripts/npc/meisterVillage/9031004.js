var status = -1;
var skillID = 92030000;
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
            var say = "우아한 #Cyellow#멜츠#k님의 고상한 취미 생활은 보석 감상이지. 반짝 반짝 거리는 보석들을 보고 있노라면 시간 가는 줄 모르겠어. 으흠~ 너도 관심이 있는 거야? 그래 보이지 않는 걸?#Cgreen#";
            if (skillLevel < 1) {
                say += "\r\n#L0##e장신구 제작#n에 대한 설명을 듣는다.";
                say += "\r\n#L1##e장신구 제작#n을 배운다.";
            } else {
                say += "\r\n#L2##e장신구 제작#n의 레벨을 올린다.";
                say += "\r\n#L3#장신구 제작의 레벨을 초기화한다.";
            }
            cm.sendSimple(say);
            break;
        }
        case 1: {
            switch (selection) {
                case 0: {
                    cm.dispose();
                    cm.sendNext("\r\n장신구 제작에 대해서 알려줄려면 우선 보석의 아름다움에 대한 근본적인 것에서부터 출발해야 하지만 짧게 이야기하지. 밤새도록 이야기해도 모자르니... 장신구 제작은 간단해. 그냥 다듬어지지 않는 보석과 광물을 아름답게 다듬고 장신구로 만들어 원래의 빛을 발하게 해주는 거야. 그 과정에서 숨겨진 힘이 발휘되어 나를 더 아름답고 강하게 만들 수 있지.");
                    break;
                }
                case 1: {
                    if (cm.getPlayer().getProfessionLevel(92000000) < 1) {
                        cm.dispose();
                        cm.sendNext("\r\n약초 채집을 배우지 않은 사람에게 장신구 제작을 가르쳐주지 않고 있어. 재료가 없다고 끈기 있게 하지 못할테니... 이 옆에 있는 약초 채집의 마스터 #Cyellow#스타첼#k님께 먼저 약초 재집을 배워 오게.");
                        return;
                    }
                    cm.sendYesNo("정말 #Cgreen#장신구 제작#k을 배울 거지? 수강료는 #Cgreen#5,000메소#k야.\r\n\r\n#Cyellow#(현재 습득 하신 전문 기술 갯수 : " + professionCount() + "개)#k");
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
                        cm.sendNext("\r\n장신구 제작의 레벨을 올리기 위한 숙련도가 충분하지 않습니다. #Cyellow#" + (skillLevel + 1) + " 레벨#k로 올리기 위해서는 #Cyellow#" + cm.getPlayer().getNum(professionProficiency(skillLevel) - cm.getPlayer().getProfessionExp(skillID)) + "의 숙련도#k가 필요로 합니다.");
                        cm.dispose();
                        return;
                    }
                    cm.sendYesNo("장신구 제작 숙련도를 모두 채웠네. 만들면 만들수록 아름다움에 대한 눈이 높아지는 것 같지 않아? 다음 레벨이 되기 위해서는 #Cgreen#" + cm.getPlayer().getNum(professionReqMeso(skillLevel)) + " 메소#k의 수강료가 필요해. 오케이?");
                    break;
                }
                case 3: {
                    status = 5;
                    cm.sendYesNo("장신구 제작을 배우지 않은 상태로 초기화 하는가? 지금까지 쌓은 레벨과 숙련도가 모두 초기화 된다네. 정말 초기화 하고 싶나?");
                    break;
                }
            }
            break;
        }
        case 2: {
            cm.dispose();
            cm.teachSkill(skillID, 0x1000000, 0);
            cm.sendNext("\r\n좋아. 장신구 제작을 성공적으로 익혔어. 숙련도가 다 차게 되면 장신구 제작의 레벨을 올릴 수 있으니 다시 찾아와 레벨을 올리는 것 잊지 말고.");
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
                cm.sendNext("\r\n장신구 제작의 장인이 되었잖아! 반할 것 같아...");
            } else {
                cm.sendNext("\r\n장신구 제작의 레벨을 " + skillLevel + "로 올렸으니까 확인해 봐. 그리고 이 멜츠님의 아름다운 힘으로 손재주를 5만큼 올려주... 어머~ 그렇게까지 감동 받은 표정을 지을 것 까진 없잖아!");
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
            cm.sendNext("\r\n장신구 제작 기술을 초기화했다네. 다시 배우고 싶다면 언제든지 찾아오게.");
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