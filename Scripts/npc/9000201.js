var status = -1;

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
    var ii = Packages.server.MapleItemInformationProvider.getInstance();
    setSkill();
    switch (status) {
        case 0: {
            var mbook = cm.getPlayer().getMonsterBook();
            var normal = mbook.getCardCount(cm.getPlayer(), false)
            var special = mbook.getCardCount(cm.getPlayer(), true);
            var level = 1;
            if (normal > 10) {
                level = 2;
            }
            if (normal > 30) {
                level = 3;
            }
            if (normal > 60) {
                level = 4;
            }
            if (normal > 100) {
                level = 5;
            }
            if (normal > 150) {
                level = 6;
            }
            if (normal > 210) {
                level = 7;
            }
            var say = "";
            say += "#Cgray#　* 북 마스터 레벨 : " + level;
            say += "\r\n#Cgray#　* 총 등록 카드 수 : " + (normal + special);
            say += "\r\n#Cgray#　* 일반 카드 수 : " + normal;
            say += "\r\n#Cgray#　* 특수 카드 수 : " + special;
            say += "\r\n#Cgray#　* 북 커버 : ";
            say += "\r\n#L0##Cgreen#Chapter. 1 #Cyellow#(" + getChapter(1)  + " / 020)#k#l　#L1##Cgreen#Chapter. 2 #Cyellow#(" + getChapter(2) + " / 047)#k#l";
            say += "\r\n#L2##Cgreen#Chapter. 3 #Cyellow#(" + getChapter(3) + " / 097)#k#l　#L3##Cgreen#Chapter. 4 #Cyellow#(" + getChapter(4) + " / 059)#k#l";
            say += "\r\n#L4##Cgreen#Chapter. 5 #Cyellow#(" + getChapter(5) + " / 048)#k#l　#L5##Cgreen#Chapter. 6 #Cyellow#(" + getChapter(6) + " / 032)#k#l";
            say += "\r\n#L6##Cgreen#Chapter. 7 #Cyellow#(" + getChapter(7) + " / 026)#k#l　#L7##Cgreen#Chapter. 8 #Cyellow#(" + getChapter(8) + " / 013)#k#l";
            say += "\r\n#L8##Cgreen#Chapter. 9 #Cyellow#(" + getChapter(9) + " / 067)#k#l";
            say += "\r\n\r\n#L9##d몬스터 북 추가 효과를 확인한다.#k#l";
            cm.sendSimple(say);
            break;
        }
        case 1: {
            s1 = selection;
            var size = 0;
            var min = 0;
            var max = 0;
            var say = "";
            switch (selection) {
                case 9: {
                    cm.dispose();
                    var say = "";
                    say += "\r\n#Cgray#    데미지 증가 : +" + cm.getPlayer().getSkillLevel(8000001) + "%";
                    say += "\r\n#Cgray#    보스 공격 시 데미지 증가 : +" + cm.getPlayer().getSkillLevel(8000002) + "%";
                    say += "\r\n#Cgray#    크리티컬 최소 및 최대 데미지 증가 : +" + cm.getPlayer().getSkillLevel(8000003) + "%";
                    say += "\r\n#Cgray#    몬스터의 방어율 일정 수치 무시 : +" + cm.getPlayer().getSkillLevel(8000004) + "%";
                    cm.sendNext(say);
                    break;
                }
                case 0: {
                    size = 20;
                    min = 2380000;
                    max = 2380020;
                    break;
                }
                case 1: {
                    size = 47;
                    min = 2381000;
                    max = 2381085;
                    break;
                }
                case 2: {
                    size = 97;
                    min = 2382000;
                    max = 2382112;
                    break;
                }
                case 3: {
                    size = 59;
                    min = 2383000;
                    max = 2383059;
                    break;
                }
                case 4: {
                    size = 48;
                    min = 2384000;
                    max = 2384062;
                    break;
                }
                case 5: {
                    size = 32;
                    min = 2385000;
                    max = 2385044;
                    break;
                }
                case 6: {
                    size = 26;
                    min = 2386000;
                    max = 2386034;
                    break;
                }
                case 7: {
                    size = 13;
                    min = 2387000;
                    max = 2387013;
                    break;
                }
                case 8: {
                    size = 67;
                    min = 2388000;
                    max = 2388084;
                    break;
                }
            }
            var succes = ((100 / size) * cm.getPlayer().getMonsterBook().getChapter(cm.getPlayer(), selection +1));
            if (succes < 10) {
                succes = 10;
            }
            say += "　  #B" + succes + "#\r\n\r\n";
            for (i = min; i < max; i++) {
                if (ii.getItemInformation(i) == null) {
                    continue;
                }
                var image = ("#fItem/Consume/0238.img/0" + i + "/info/iconRaw#");
                if (cm.getPlayer().getMonsterBook().getCard(cm.getPlayer().getAccountID(), i) == 5) {
                    image = ("#fItem/Consume/0238.img/02389999/info/iconRaw#");
                }
                say += "#L" + i + "#" + image + "#l";
            }
            cm.sendSimple(say);
            break;
        }
        case 2: {
            var cardMonster = ii.getCardMobId(selection);
            var getMonster = Packages.server.life.MapleLifeFactory.getMonster(cardMonster);
            var say = "";
            //var path = (cardMonster < 1000000 ? "0" : "");
            say += "#d" + getMonster.getName() + "#k ";
            say += "#Cgreen#(" + cm.getPlayer().getMonsterBook().getCard(cm.getPlayer().getAccountID(), selection) + " / 5)#k";
            say += "\r\n\r\n#Cyellow#에피소드\r\n#Cgray#" + ii.getBookEpisode(cardMonster) + "";
            say += "\r\n\r\n#Cgray#" + ii.getBookMap(cardMonster) + "";
            say += "\r\n\r\n#Cgray#" + ii.getBookReward(cardMonster) + "";
            cm.sendNext("" + say);
            break;
        }
        case 3: {
            status = 0;
            start(1, 0, s1);
            break;
        }
    }
}

function getChapter(i) {
    var mbook = cm.getPlayer().getMonsterBook();
    var chapter = i;
    var path = (mbook.getChapter(cm.getPlayer(), chapter) < 10 ? "00" : mbook.getChapter(cm.getPlayer(), chapter) < 100 ? "0" : "");
    var count = (path + mbook.getChapter(cm.getPlayer(), chapter));
    return count;
}

function setSkill() {
    var a1 = 0;
    for (i = 1; i < 10; i++) {
        a1 += cm.getPlayer().getMonsterBook().getChapter(cm.getPlayer(), i);
    }
    var a2 = parseInt(a1 / 13);
    if (a2 > 30) {
        a2 = 30;
    }
    for (j = 1; j < 5; j++) {
        var a3 = Packages.client.SkillFactory.getSkill(8000000 + j);
        if (a3 != null) {
            cm.getPlayer().changeSingleSkillLevel(a3, a2, a2);
        }
    }
}