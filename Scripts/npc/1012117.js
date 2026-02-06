var status = -1;

var count = 100;
var hair = new Array();
var face = new Array();
var skin = new Array(0, 1, 2, 3, 4, 9, 10, 11, 12, 13);
var hairColor = new Array();
var faceColor = new Array();
var sHair = new Array();
var sFace = new Array();

function start() {
    status = -1;
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (mode == 1) {
        status++;
    } else {
        if (status == 0) {
            cm.dispose();
            return;
        }
        if (status == 2) {
            selection = 0;
        }
        status--;
    }
    switch (status) {
        case 0: {
            var say = "안녕. 난 #d빅 헤드워드#k라고 해.";
            say += "\r\n#L0##Cgreen#머리를 변경#d하고 싶습니다.#k";
            say += "\r\n#L5##Cgreen#머리를 염색#d하고 싶습니다.#k";
            say += "\r\n#L1##Cgreen#얼굴을 변경#d하고 싶습니다.#k";
            say += "\r\n#L3##Cgreen#렌즈를 변경#d하고 싶습니다.#k";
            say += "\r\n#L2##Cgreen#피부를 변경#d하고 싶습니다.#k";
            say += "\r\n#L4##Cgreen#성별을 변경#d하고 싶습니다.#k";
            cm.sendSimple(say);
            break;
        }
        case 1: {
            var say = "안녕. 난 #d빅 헤드워드#k라고 해.";
            switch (selection) {
                case 0: {
                    loadHair();
                    for (i = 0; i < hair.length; i++) {
                        say += "\r\n#L" + i + "##d목록 <" + (i + 1) + ">을 확인하고 싶습니다.#k";
                    }
                    say += "\r\n#L100##Cgreen#머리 검색을 이용하고 싶습니다.#k";
                    cm.sendSimple(say);
                    break;
                }
                case 1: {
                    loadFace();
                    status = 5;
                    for (i = 0; i < face.length; i++) {
                        say += "\r\n#L" + i + "##d목록 <" + (i + 1) + ">을 확인하고 싶습니다.#k";
                    }
                    say += "\r\n#L100##Cgreen#얼굴 검색을 이용하고 싶습니다.#k";
                    cm.sendSimple(say);
                    break;
                }
                case 2: {
                    status = 8;
                    cm.askAvatar("안녕. 난 #d빅 헤드워드#k라고 해.", skin);
                    break;
                }
                case 3: {
                    status = 10;
                    var normalColor = ((cm.getPlayer().getFace() % 1000) - (cm.getPlayer().getFace() % 100));
                    var currentFace = cm.getPlayer().getFace() - normalColor;
                    for (i = 100; i < 701; i += 100) {
                        if (currentFace + i == cm.getPlayer().getFace()) {
                            continue;
                        }
                        faceColor.push(currentFace + i);
                    }
                    cm.askAvatar("안녕. 난 #d빅 헤드워드#k라고 해.", faceColor);
                    break;
                }
                case 4: {
                    status = 12;
                    var say = "#r* 성별 변경 후 아이템 장착에 문제가 있을 경우\r\n   재접속 해보시고 그래도 해결이 안될 시 연락 주세요.#d";
                    say += "\r\n#L0#남성으로 변경하고 싶습니다.";
                    say += "\r\n#L1#여성으로 변경하고 싶습니다.";
                    cm.sendSimple(say);
                    break;
                }
                case 5: {
                    status = 14;
                    var normalColor = (cm.getPlayer().getHair() % 10);
                    var currentHair = cm.getPlayer().getHair() - normalColor;
                    for (i = 0; i < 8; i++) {
                        if (currentHair + i == cm.getPlayer().getHair()) {
                            continue;
                        }
                        hairColor.push(currentHair + i);
                    }
                    cm.askAvatar("안녕. 난 #d빅 헤드워드#k라고 해.", hairColor);
                    break;
                }
            }
            break;
        }
        case 2: {
            jgys = selection;
            if (selection == 100) {
                status = 16;
                cm.sendGetText("　\r\n음... 따로 원하는 스타일이 있는 거야? 원하는 스타일의 #Cgreen#머리#k를 검색해 보라고.　\r\n　");
                return;
            }
            cm.askAvatar("안녕. 난 #d빅 헤드워드#k라고 해.", hair[selection]);
            break;
        }
        case 3: {
            cm.dispose();
            selection = selection & 0xFF;
            cm.setAvatar(hair[jgys][selection]);
            cm.sendNext("\r\n어때? 이게 최신 유행 스타일인 #Cgreen##z" + hair[jgys][selection] + "##k라고. 와우~ 정말 전설에 남을 스타일인데? 하하하. 하긴. 내 솜씨가 어디 가겠어~ 또 필요하다면 언제든지 찾아 오라고. 후후.");
            break;
        }
        case 4: {
            status = -1;
            action(1, 0, 0);
            break;
        }
        case 5: {
            status = 0;
            action(1, 0, 1);
            break;
        }
        case 6: {
            jgys = selection;
            if (selection == 100) {
                status = 19;
                cm.sendGetText("　\r\n음... 따로 원하는 스타일이 있는 거야? 원하는 스타일의 #Cgreen#얼굴#k을 검색해 보라고.　\r\n　");
                return;
            }
            cm.askAvatar("안녕. 난 #d빅 헤드워드#k라고 해.", face[selection]);
            break;
        }
        case 7: {
            cm.dispose();
            selection = selection & 0xFF;
            cm.setAvatar(face[jgys][selection]);
            cm.sendNext("\r\n어때? 이게 최신 유행 스타일인 #Cgreen##z" + face[jgys][selection] + "##k라고. 와우~ 정말 전설에 남을 스타일인데? 하하하. 하긴. 내 솜씨가 어디 가겠어~ 또 필요하다면 언제든지 찾아 오라고. 후후.");
            break;
        }
        case 8: {
            status = -1;
            action(1, 0, 0);
            break;
        }
        case 9: {
            cm.dispose();
            selection = selection & 0xFF;
            cm.setAvatar(skin[selection]);
            break;
        }
        case 10: {
            status = -1;
            action(1, 0, 0);
            break;
        }
        case 11: {
            cm.dispose();
            selection = selection & 0xFF;
            cm.setAvatar(faceColor[selection]);
            cm.sendNext("\r\n어때? 이게 최신 유행 스타일인 #Cgreen##z" + faceColor[jgys][selection] + "##k라고. 와우~ 정말 전설에 남을 스타일인데? 하하하. 하긴. 내 솜씨가 어디 가겠어~ 또 필요하다면 언제든지 찾아 오라고. 후후.");
            break;
        }
        case 12: {
            status = -1;
            action(1, 0, 0);
            break;
        }
        case 13: {
            cm.dispose();
            cm.getPlayer().setGender(selection);
            cm.getPlayer().getClient().setGender(selection);
            break;
        }
        case 14: {
            status = -1;
            action(1, 0, 0);
            break;
        }
        case 15: {
            cm.dispose();
            selection = selection & 0xFF;
            cm.setAvatar(hairColor[selection]);
            cm.sendNext("\r\n어때? 이게 최신 유행 스타일인 #Cgreen##z" + face[jgys][selection] + "##k라고. 와우~ 정말 전설에 남을 스타일인데? 하하하. 하긴. 내 솜씨가 어디 가겠어~ 또 필요하다면 언제든지 찾아 오라고. 후후.");
            break;
        }
        case 16: {
            cm.dispose();
            break;
        }
        case 17: {
            searchHair(cm.getText());
            cm.askAvatar("안녕. 난 #d빅 헤드워드#k라고 해. #Cgreen#" + cm.getText() + "#k에 관련 된 머리가 #r총 " + sHair.length+ "개#k가 검색됬어. 자, 이제 원하는 스타일을 선택해 보라고.", sHair);
            break;
        }
        case 18: {
            cm.dispose();
            selection = selection & 0xFF;
            cm.setAvatar(sHair[selection]);
            cm.sendNext("\r\n어때? 이게 최신 유행 스타일인 #Cgreen##z" + sHair[selection] + "##k라고. 와우~ 정말 전설에 남을 스타일인데? 하하하. 하긴. 내 솜씨가 어디 가겠어~ 또 필요하다면 언제든지 찾아 오라고. 후후.");
            break;
        }
        case 19: {
            cm.dispose();
            break;
        }
        case 20: {
            searchFace(cm.getText());
            cm.askAvatar("안녕. 난 #d빅 헤드워드#k라고 해. #Cgreen#" + cm.getText() + "#k에 관련 된 얼굴이 #r총 " + sFace.length+ "개#k가 검색됬어. 자, 이제 원하는 스타일을 선택해 보라고.", sFace);
            break;
        }
        case 21: {
            cm.dispose();
            selection = selection & 0xFF;
            cm.setAvatar(sFace[selection]);
            cm.sendNext("\r\n어때? 이게 최신 유행 스타일인 #Cgreen##z" + sFace[selection] + "##k라고. 와우~ 정말 전설에 남을 스타일인데? 하하하. 하긴. 내 솜씨가 어디 가겠어~ 또 필요하다면 언제든지 찾아 오라고. 후후.");
            break;
        }
    }
}

function loadHair() {
    var ii = Packages.server.MapleItemInformationProvider.getInstance();
    var findHair = ii.getFindHair(cm.getPlayer().getHair() % 10, cm.getPlayer().getGender(), null, false);
    var hairArray = parseInt(findHair.size() / count);
    for (var i = 0; i < hairArray; i++) {
        hair[i] = new Array();
        for (var j = 0; j < count; j++) {
            var index = parseInt(j + (i * count));
            if (index >= findHair.size()) {
                continue;
            }
            hair[i].push(findHair.get(index));
        }
    }
}

function loadFace() {
    var ii = Packages.server.MapleItemInformationProvider.getInstance();
    var findFace = ii.getFindFace((cm.getPlayer().getFace() % 1000) - (cm.getPlayer().getFace() % 100), null, false);
    var faceArray = parseInt(findFace.size() / count);
    for (var i = 0; i < faceArray; i++) {
        face[i] = new Array();
        for (var j = 0; j < count; j++) {
            var index = parseInt(j + (i * count));
            if (index >= findFace.size()) {
                continue;
            }
            face[i].push(findFace.get(index));
        }
    }
}

function searchHair(text) {
    var ii = Packages.server.MapleItemInformationProvider.getInstance();
    var findHair = ii.getFindHair(cm.getPlayer().getHair() % 10, cm.getPlayer().getGender(), text, true);
    for (var i = 0; i < findHair.size(); i++) {
        sHair.push(findHair.get(i));
    }
    if (sHair.length == 0) {
        cm.dispose();
        cm.sendNext("\r\n음... 너가 찾는 스타일 중에 #d" + text + "#k이라는 머리는 없는 걸? 확인 후에 다시 검색해 보라고.");
        return;
    }
}

function searchFace(text) {
    var ii = Packages.server.MapleItemInformationProvider.getInstance();
    var findFace = ii.getFindFace((cm.getPlayer().getFace() % 1000) - (cm.getPlayer().getFace() % 100), text, true);
    for (var i = 0; i < findFace.size(); i++) {
        sFace.push(findFace.get(i));
    }
    if (sFace.length == 0) {
        cm.dispose();
        cm.sendNext("\r\n음... 너가 찾는 스타일 중에 #d" + text + "#k이라는 얼굴은 없는 걸? 확인 후에 다시 검색해 보라고.");
        return;
    }
}