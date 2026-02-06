var status = -1;

var nApple = [
[1, 1012029], 
[2, 1032039], 
[3, 1132014], 
[4, 1152002], 
[5, 1162012], 
[6, 1112008],
[0, 1142249], 
];

var nRing = [
2290096, // 메이플용사20
2290125, // 메이플용사30
2046685, // 프리미엄 방어구 공격력 주문서 100%
2046686, // 프리미엄 방어구 마력 주문서 100%
1112585, // 엔젤릭 블레스
1112586, // 다크 엔젤릭 블레스
1112663, // 화이트 엔젤릭 블레스
1672040, // 티타늄 하트
1113069, // 어드벤쳐 다크 크리티컬 링
2049370, // 12성 강화권
2049372, // 15성 강화권
4310031, // 보스 원정대 코인
4310031, // 보스 원정대 코인
];

var nScroll = [
2046991, // 매지컬 한손 무기 공격력 주문서
2046992, // 매지컬 한손 무기 마력 주문서
2047814, // 매지컬 두손 무기 공격력 주문서
2046829, // 프리미엄 악세서리 공격력 스크롤 100%
2046830, // 프리미엄 악세서리 마력 스크롤 100%
2048094, // 프리미엄 펫 장비 공격력 스크롤 100%
2048095, // 프리미엄 펫 장비 마력 스크롤 100%

1003552, // 99 이올렛 모자
1052461, // 99 이올렛 슈트
1082433, // 99 이올렛 장갑
1072666, // 99 이올렛 슈즈
1102441, // 99 이올렛 망토
1152089, // 99 이올렛 견장
1132154, // 99 이올렛 벨트
1113068, // 어드벤쳐 크리티컬 링

2049373, // 10성 강화권

4310031, // 보스 원정대 코인
4310031, // 보스 원정대 코인
];

var nEtc = [

5121060, // MVP의 기상 효과
2000054, // MVP의 물약
//5133000, // 버프 프리져

1672003, // 골드 하트
1662002, // 고급형 안드로이드 남
1662003, // 고급형 안드로이드 여

1112428, // 크리티컬 링
4310031, // 보스 원정대 코인
4310031, // 보스 원정대 코인
4310031, // 보스 원정대 코인
4310031, // 보스 원정대 코인
4310031, // 보스 원정대 코인
4310031, // 보스 원정대 코인
4310031, // 보스 원정대 코인
4310031, // 보스 원정대 코인
4310031, // 보스 원정대 코인
4310031, // 보스 원정대 코인
];

function start() {
    action (1, 0, 0);
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
            if (cm.itemQuantity(2435457) < 1) {
                cm.dispose();
                return;
            }
            if (cm.getPlayer().getOneInfoQuest(12500001, "gold_apple") == null) {
                cm.getPlayer().updateOneInfoQuest(12500001, "gold_apple", "0");
            }
            if (cm.getPlayer().getOneInfoQuest(12500001, "gold_apple").equals("")) {
                cm.getPlayer().updateOneInfoQuest(12500001, "gold_apple", "0");
            }
            var say = "　 #Cgray#신의 열매, #i2435457# #Cyellow##z2435457##Cgray#이 신비의 힘을 발휘합니다.";
            say += "\r\n\r\n　 #eGauge : " + parseInt(cm.getPlayer().getOneInfoQuest(12500001, "gold_apple")) + " / 100#n";
            var nGauge = parseInt(cm.getPlayer().getOneInfoQuest(12500001, "gold_apple"));
            if (nGauge > 100) {
                nGauge = 100;
            }
	     
            say += "\r\n　 #B" + nGauge + "#";
	    say += "\r\r   \n#Cblue#(게이지를 모두 채우면 해당 요일 희귀아이템 확정 지급)";
            say += "\r\n#L0##Cgreen#골드 애플을 사용한다.";
            say += "\r\n#L1##Cgray#획득 가능 한 아이템 목록을 확인한다.";
            say += "\r\n#L2##Cgray#엔젤릭 블레스 링을 교환한다.";
            cm.sendSimple(say);
            break;
        }
        case 1: {
            switch (selection) {
                case 0: {
                    for (i = 1; i < 6; i++) {
                        if (cm.getInventory(i).getNumFreeSlot() < 2) {
                            cm.dispose();
                            cm.getPlayer().getClient().sendPacket(Packages.tools.packet.CWvsContext.crossHunterQuestResult(2, 0));
                            return;
                        }
                    }
                    var nNotice = 0;
                    var nRand = Packages.server.Randomizer.nextInt(1000);
                    var tRand = Packages.server.Randomizer.nextInt(nEtc.length - 1);
                    var qRand = Packages.server.Randomizer.rand(0, 2);
                    var nItem = nEtc[tRand];
                    if (nRand < 400) {
                        nNotice = 1;
                        tRand = Packages.server.Randomizer.nextInt(nScroll.length - 1);
                        nItem = nScroll[tRand];
                    }
                    if (nRand < 40) {
                        if (qRand != 0) {
                            nNotice = 1;
                            tRand = Packages.server.Randomizer.nextInt(nRing.length - 1);
                            nItem = nRing[tRand];
                        } else {
                            nNotice = 1;
                            var nDate = new java.util.Date();
                            for (i = 0; i < nApple.length; i++) {
                                if (nDate.getDay() == nApple[i][0]) {
                                    nItem = nApple[i][1];
                                }
                            }
                        }
                    }
                    cm.getPlayer().updateOneInfoQuest(12500001, "gold_apple", "" + (parseInt(cm.getPlayer().getOneInfoQuest(12500001, "gold_apple")) + 1));
                    if (parseInt(cm.getPlayer().getOneInfoQuest(12500001, "gold_apple")) > 100) {
                        cm.getPlayer().updateOneInfoQuest(12500001, "gold_apple", "0");
                        nNotice = 1;
                        var tDate = new java.util.Date();
                        for (i = 0; i < nApple.length; i++) {
                            if (tDate.getDay() == nApple[i][0]) {
                                nItem = nApple[i][1];
                            }
                        }
                    }
                    cm.gainItem(2435457, -1);
                    cm.gainItem(2435458, 1);
                    cm.gainItem(nItem, 1);
                    var say = "\r\n#Cgray#신의 열매, #i2435457# #Cyellow##z2435457##Cgray#이 신비의 힘을 발휘합니다.#Cgreen#";
                    say += "\r\n";
                    say += "\r\n#fUI/UIWindow.img/QuestIcon/4/0#";
                    say += "\r\n#i" + nItem + "# #z" + nItem + "# 1개";
                    say += "\r\n#i2435458# #z2435458# 1개";
                    cm.sendNext(say);
                    if (nNotice > 0) {
                        Packages.handling.world.World.Broadcast.broadcastMessage(Packages.tools.packet.CWvsContext.serverNotice(6, nItem, cm.getPlayer().getName() + "님께서 골드 애플에서 " + "[{" + Packages.server.MapleItemInformationProvider.getInstance().getItemInformation(nItem).name + "}]" + " 1개를 획득하였습니다.", false));
                    }
                    break;
                }
                case 1: {
                    cm.dispose();
                    var say = "";
                    say += "\r\n#i2435457##Cyellow##z2435457##Cgray#의 아이템 확률 정보입니다.\r #Cyellow#1%#Cgray#의 확률로 #Cyellow#요일별 희귀 아이템#Cgray#을 획득 하실 수 있습니다. #Cgray#(요일은 매일 #Cyellow#오전 00:00시#Cgray#에 리셋됩니다.)\r\n\r\n#Cgreen#↓ 아이템 리스트 및 획득 확률 ↓";
                    say += "\r\n";
                    for (i = 0; i < nApple.length; i++) {
                        say += "\r\n#Cgreen#(01%) #Cgray##z" + nApple[i][1] + "#";
                    }
                    say += "\r\n";
                    for (i = 0; i < nRing.length; i++) {
                        say += "\r\n#Cgreen#(04%) #Cgray##z" + nRing[i] + "# ";
                    }
                    say += "\r\n";
                    for (i = 0; i < nScroll.length; i++) {
                        say += "\r\n#Cgreen#(40%) #Cgray##z" + nScroll[i] + "# ";
                    }
                    say += "\r\n";
                    for (i = 0; i < nEtc.length; i++) {
                        say += "\r\n#Cgreen#(55%) #Cgray##z" + nEtc[i] + "# ";
                    }
                    cm.sendNext(say);
                    break;
                }
                case 2: {
                    status = 3;
                    var say = "";
                    say += "#Cgray#엔젤릭 블레스 링 #Cyellow#2개#Cgray#를 조합하여 \r\n#Cyellow#더 높은 등급#Cgray#의 엔젤릭 블레스 링으로 교환 할 수 있습니다.";
                    say += "\r\n#L0##Cgreen#엔젤릭 블레스 2개  > 다크 엔젤릭 블레스";
                    say += "\r\n#L1##Cgreen#다크 엔젤릭 블레스 2개 > 화이트 엔젤릭 블레스";
                    cm.sendSimple(say);
                    break;
                }
            }
            break;
        }
        case 2: {
            status = -1;
            action(1, 0, 0);
            break;
        }
        case 3: {
            cm.dispose();
            break;
        }
        case 4: {
            cm.dispose();
            if (!cm.haveItem(selection == 0 ? 1112585 : 1112586, 1)) {
                cm.sendNext("\r\n#Cyellow##z" + (selection == 0 ? 1112586 : 1112663) + "# 1개#k로 교환을 진행하기 위한 #Cgreen##z" + (selection == 0 ? 1112585 : 1112586) + "# 2개#k를 소지 중에 있지 않습니다.");
                return;
            }
            cm.gainItem((selection == 0 ? 1112585 : 1112586), -2);
            cm.gainItem((selection == 0 ? 1112586 : 1112663), 1);
            var say = "\r\n#Cgray#신의 열매, #i2435457# #Cyellow##z2435457##Cgray#이 신비의 힘을 발휘합니다.#Cgreen#";
            say += "\r\n";
            say += "\r\n#fUI/UIWindow.img/QuestIcon/4/0#";
            say += "\r\n#i" + (selection == 0 ? 1112586 : 1112663) + "# #z" + (selection == 0 ? 1112586 : 1112663) + "# 1개";
            cm.sendNext(say);
            break;
        }
    }
}