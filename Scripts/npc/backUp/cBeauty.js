var status = -1;

var count = 100;
var hair = new Array();
var face = new Array();
var skin = new Array(15, 16, 18, 19, 24, 25, 26, 27);
var hairColor = new Array();
var faceColor = new Array();

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
    loadHair();
    loadFace();
    switch (status) {
        case 0: {
            var say = "#b메이플 스토리#k에 오신 것을 진심으로 환영합니다.";
            say += "\r\n#L0##r헤어 변경#b을 하고 싶습니다.#k";
            say += "\r\n#L5##r헤어 염색#b을 하고 싶습니다.#k";
            say += "\r\n#L1##r얼굴 변경#b을 하고 싶습니다.#k";
            say += "\r\n#L3##r렌즈 변경#b을 하고 싶습니다.#k";
            say += "\r\n#L2##r피부 변경#b을 하고 싶습니다.#k";
            say += "\r\n\r\n#L4##r성별 변경#b을 하고 싶습니다#k";
            cm.sendSimple(say);
            break;
        }
        case 1: {
            var say = "#b메이플 스토리#k에 오신 것을 진심으로 환영합니다.";
            switch (selection) {
                case 0: {
                    for (i = 0; i < hair.length; i++) {
                        say += "\r\n#L" + i + "##r" + (i + 1) + "번 목록#b을 확인하고 싶습니다.#k";
                    }
                    cm.sendSimple(say);
                    break;
                }
                case 1: {
                    status = 5;
                    for (i = 0; i < face.length; i++) {
                        say += "\r\n#L" + i + "##r" + (i + 1) + "번 목록#b을 확인하고 싶습니다.#k";
                    }
                    cm.sendSimple(say);
                    break;
                }
                case 2: {
                    status = 8;
                    cm.askAvatar("#b메이플 스토리#k에 오신 것을 진심으로 환영합니다.", skin);
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
                    cm.askAvatar("#b메이플 스토리#k에 오신 것을 진심으로 환영합니다.", faceColor);
                    break;
                }
                case 4: {
                    status = 12;
                    var say = "#b메이플 스토리#k에 오신 것을 진심으로 환영합니다.";
                    say += "\r\n#L0##r남성#b으로 변경하고 싶습니다.";
                    say += "\r\n#L1##r여성#b으로 변경하고 싶습니다.";
                    say += "\r\n#L2##r중성#b으로 변경하고 싶습니다.";
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
                    cm.askAvatar("#b메이플 스토리#k에 오신 것을 진심으로 환영합니다.", hairColor);
                    break;
                }
            }
            break;
        }
        case 2: {
            jgys = selection;
            cm.askAvatar("#b메이플 스토리#k에 오신 것을 진심으로 환영합니다.", hair[selection]);
            break;
        }
        case 3: {
            cm.dispose();
            selection = selection & 0xFF;
            cm.setAvatar(hair[jgys][selection]);
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
            cm.askAvatar("#b메이플 스토리#k에 오신 것을 진심으로 환영합니다.", face[selection]);
            break;
        }
        case 7: {
            cm.dispose();
            selection = selection & 0xFF;
            cm.setAvatar(face[jgys][selection]);
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
            break;
        }
    }
}

function loadHair() {
    var ii = Packages.server.MapleItemInformationProvider.getInstance();
    var findHair = ii.getFindHair(cm.getPlayer().getHair() % 10, cm.getPlayer().getGender());
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
    var findFace = ii.getFindFace((cm.getPlayer().getFace() % 1000) - (cm.getPlayer().getFace() % 100));
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