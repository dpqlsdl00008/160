var status = -1;

var what = [[1, "캐쉬"], [2, "메이플 포인트"]];

var expedition = [
["Balog", "마왕 발록", 20230000, "balog", 3, 105100100],
["Zakum", "자쿰", 20230001, "zakum", 2, 211042300],
["Hontale", "혼테일", 20230002, "horntail", 2, 240050400],
["PinkBean", "핑크빈", 20230003, "pinkbean", 1, 270050000],
["VanLeon\r\n", "반 레온", 20230004, "vonleon", 1, 211070000],
["Aswan", "힐라", 20230006, "hillah", 1, 262000000],
["Akayrum", "아카이럼", 20230005, "akayrum", 1, 272020110],
];

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
    switch (status) {
        case 0: {
            var say = "#Cyellow#응 아니야#k";
            say += "\r\n#L0##Cgreen#캐쉬 충전#d을 하고 싶습니다.";
            say += "\r\n#L2##Cgreen#MaxHP & MaxMP 증가#d를 하고 싶습니다.";
            cm.sendSimple(say);
            break;
        }
        case 1: {
            switch (selection) {
                case 0: {
                    cm.sendGetNumber("\r\n#d10,000,000 메소#k로 #d10,000 캐쉬#k로 교환을 할 수 있다네. #Cgreen#몇 회 교환을 할 텐가?#k\r\n　\r\n", 1, 1, 999);
                    break;
                }
                case 1: {
                    status = 17;
                    var say = "참여를 원하는 #Cgreen#원정대#k를 선택해 주게나.";
                    for (i = 0; i < expedition.length; i++) {
                        say += "\r\n#L" + i + "##d#m" + expedition[i][5] + "# #Cyellow#(" + expedition[i][1] + ")";
                    }
                    cm.sendSimple(say);
                    break;
                }
                case 2: {
                    status = 22;
                    if (cm.getPlayer().getOneInfoQuest(1000000, "max_hp_mp").equals("")) {
                        cm.getPlayer().updateOneInfoQuest(1000000, "max_hp_mp", "0");
                    }
                    if (cm.getPlayer().getOneInfoQuest(1000000, "max_hp_mp").equals("")) {
                        cm.getPlayer().updateOneInfoQuest(1000000, "max_hp_mp", "0");
                    }
                    var z1 = parseInt(cm.getPlayer().getOneInfoQuest(1000000, "max_hp_mp"));
                    if (z1 > 29) {
                        cm.dispose();
                        cm.sendNext("\r\n#bMaxHP & MaxMP 증가#k는 최대 #r30회#k까지만 가능하다네.");
                        return;
                    }
                    var say = "자네의 #dMaxHP & MaxMP 증가 잔여 횟수#k는 #Cgreen#" + (30 - z1) + "회#k라네. 한 번 선택하면 되돌릴 수 없으니 신중하게 선택해 주게나.";
                    say += "\r\n#L0##Cgreen#MaxHP 증가#d를 하고 싶습니다.";
                    say += "\r\n#L1##Cgreen#MaxMP 증가#d를 하고 싶습니다.";
                    cm.sendSimple(say);
                    break;
                }
            }
            break;
        }
        case 2: {
            if (selection < 1) {
                cm.dispose();
                return;
            }
            var cash = "자네가 입력 한 횟수는 #Cgreen#" + selection + "회#k로 #d" + cm.getPlayer().getNum(selection * 10000000) + " 메소#k를 사용하여 #d" + cm.getPlayer().getNum(selection * 10000) + "#k #d캐쉬#k로 교환을 할 수 있다네.";
            for (i = 0; i < what.length; i++) {
                cash += "\r\n#L" + i + "##Cgreen#" + what[i][1] + "#d로 교환을 하겠습니다.";
            }
            cm.sendSimple(cash);
            cash_selection = selection;
            break;
        }
        case 3: {
            cm.dispose();
            if (cm.getMeso() > (10000000 * cash_selection)) {
                cm.gainMeso(-(10000000 * cash_selection));
                var v10000 = 0;
                for (var i = 0; i < cash_selection; ++i) {
                    v10000++;
                }
                cm.getPlayer().modifyCSPoints(what[selection][0], 10000 * v10000, true);
                cm.sendNext("\r\n#d" + cm.getPlayer().getNum((10000000 * cash_selection)) + "메소#k로 #Cgreen#" + cm.getPlayer().getNum((10000 * v10000)) + " " + what[selection][1] + "#k 교환을 완료 했다네.");
            } else {
                cm.sendNext("\r\n음... #d캐쉬#k로 교환을 하기 위한 #Cgreen#메소#k가 충분하지 않은 것 같구만.");
            }
            break;
        }
        case 4: {
            cm.dispose();
            break;
        }
        case 17: {
            cm.dispose();
            break;
        }
        case 18: {
            v5 = selection;
            cm.sendAcceptDeclineS("#Cgreen##m" + expedition[selection][5] + "##k(으)로 이동하겠습니다.", 2);
            break;
        }
        case 19: {
            cm.dispose();
            cm.warp(expedition[v5][5], 0);
            break;
        }
        case 20: {
            cm.dispose();
            break;
        }
        case 22: {
            cm.dispose();
            break;
        }
        case 23: {
            var z2 = parseInt(cm.getPlayer().getOneInfoQuest(1000000, "max_hp_mp"));
            var z3 = (30 - z2);
            reqMeso = 100000000;
            if (z3 < 26) {
                reqMeso = 300000000;
            }
            if (z3 < 21) {
                reqMeso = 500000000;
            }
            if (z3 < 11) {
                reqMeso = 1000000000;
            }
            cm.askAcceptDecline("\r\n#bMax" + (selection == 0 ? "HP" : "MP") + " 증가#k를 선택하였군. 수락 할 시에 #b500이 증가#k한다네. 그리고 수수료는 #b" + cm.getPlayer().getNum(reqMeso) + " 메소#k라네. 자네의 남은 잔여 횟수는 #r" + z3 + "회#k니 신중하게 선택해 주게나.");
            z4 = selection;
            break;
        }
        case 24: {
            cm.dispose();
            if (cm.getMeso() < reqMeso) {
                cm.sendNext("\r\n자네, 수수료는 가지고 있는 건가? #b" + cm.getPlayer().getNum(reqMeso) + " 메소#k라네.");
                return;
            }
            cm.gainMeso(-reqMeso);
            var z5 = parseInt(cm.getPlayer().getOneInfoQuest(1000000, "max_hp_mp"));
            cm.getPlayer().updateOneInfoQuest(1000000, "max_hp_mp", (z5 + 1) + "");
            if (z4 == 0) {
                cm.getPlayer().getStat().setMaxHp(cm.getPlayer().getStat().getMaxHp() + 500, cm.getPlayer());
            } else {
                cm.getPlayer().getStat().setMaxMp(cm.getPlayer().getStat().getMaxMp() + 500, cm.getPlayer());
            }
            cm.getPlayer().saveToDB(false, false);
            cm.getPlayer().fakeRelog();
            cm.getPlayer().getClient().sendPacket(Packages.tools.packet.CUserLocal.chatMsg(Packages.handling.ChatType.GameDesc, "Max" + (z4 == 0 ? "HP" : "MP") + " 증가를 성공적으로 완료하였습니다. (잔여 횟수 : " + cm.getPlayer().getOneInfoQuest(1000000, "max_hp_mp") + "/30회)"));
            break;
        }
    }
}