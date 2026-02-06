var status = -1;

var eqp_number = 100001;
var use_number = 100002;
var etc_number = 100003;
var ins_number = 100004;

var eqp_list = [
[1442039, 1], // 냉동 참치
[1322003, 1], // 막대 사탕
[1322027, 1], // 프라이팬
[1332020, 1], // 태극 부채
[1302063, 1], // 화염의 카타나
[1302049, 1], // 광선 채찍
[1003419, 1], // 머리 위에 떡 두개
[1072533, 1], // 말랑말랑한 신발
[1022073, 1], // 금이 간 안경
[1082232, 1], // 여신의 팔찌
[1122118, 1], // 영원한 사랑의 증표
[1003267, 1], // 폼나는 데비존의 모자
[1012541, 1], // 체리맛 아이스바
[1442016, 1], // 다크 스노우 보드
[1442057, 1], // 보라색 서핑 보드
[1112742, 1], // 메이플 이올렛 링
];

var use_list = [
[2070006, 1], // 일비 표창
[2070007, 1], // 화비 표창
[2070023, 1], // 플레임 표창
[2070024, 1], // 무한의 수리검
[2330005, 1], // 이터널 불릿
[2330008, 1], // 자이언트 불릿
[2049100, 1], // 혼돈의 주문서 60%
[2049005, 1], // 백의 주문서 20%
[2040002, 1], // 투구 방어력 주문서 10%
[2040705, 1], // 신발 점프력 주문서 10%
[2041041, 1], // 망토 행운 주문서 30%
[2450000, 1], // 사냥꾼의 행운
[2213009, 1], // 틱톡 변신 포션
[2213018, 1], // 삼미호 변신 포션
[2213036, 1], // 이루워터 변신 포션
];

var etc_list = [
[4000042, 999], // 스티지의 날개
[4000022, 999], // 스톤 골렘의 돌 조각
[4000041, 999], // 멜러디의 실험용 개구리
[4000058, 999], // 네펜데스의 씨앗
[4000052, 999], // 화이트 팽의 꼬리
[4000170, 999], // 호랑이 발 도장
[4000040, 5], // 머쉬맘의 포자
[4000235, 5], // 마뇽의 꼬리
[4000243, 5], // 그리프의 뿔
[4000460, 5], // 고래의 투구
[4000461, 5], // 나이트의 가면
[4000462, 5], // 수호수의 뿔
];

var ins_list = [
[3010756, 1], // Fat Lucky Cat
[3010762, 1], // Peppermint Throne
[3010763, 1], // Christmas Phantom
[3010765, 1], // Ice Fishing
[3010768, 1], // Furnace Chair
[3010817, 1], // Rabbit Sofa
[3010819, 1], // Bunny Tornado Chair
[3010821, 1], // My Childhood Memories
[3010833, 1], // I Love MapleStory Chair
[3010816, 1], // Forest Sanctuary Chair
[3010970, 1], // Arboren Chair
[3010934, 1], // Fun Cloud Chair
[3010834, 1], // Maple Crystal Chair
[3010966, 1], // Ghost Ship Chair
];

function start(mode, type, selection) {
    if (mode == 1) {
        status++;
    } else {
        qm.dispose();
        return;
    }
    switch (status) {
        case 0: {
            var say = "#Cgreen#컬렉션#k을 등록하고 #Cyellow#추가 능력치#k를 획득해 보세요.\r\n";
            say += "\r\n#Cgray#    데미지 증가 : +" + qm.getPlayer().getTotalSkillLevel(8000001) + "%";
            say += "\r\n#Cgray#    보스 공격 시 데미지 증가 : +" + qm.getPlayer().getTotalSkillLevel(8000002) + "%";
            say += "\r\n#Cgray#    크리티컬 최소 및 최대 데미지 증가 : +" + qm.getPlayer().getTotalSkillLevel(8000003) + "%";
            say += "\r\n#Cgray#    몬스터의 방어율 일정 수치 무시 : +" + qm.getPlayer().getTotalSkillLevel(8000004) + "%";
            say += "\r\n#L0##Cgreen#장비 아이템#d을 등록한다.#k";
            say += "\r\n#L1##Cgreen#소비 아이템#d을 등록한다.#k";
            say += "\r\n#L2##Cgreen#기타 아이템#d을 등록한다.#k";
            say += "\r\n#L3##Cgreen#설치 아이템#d을 등록한다.#k";
            qm.sendSimple(say);
            break;
        }
        case 1: {
            s1 = selection;
            switch (selection) {
                case 0: {
                    var eqp_count = 0;
                    for (i = 0; i < eqp_list.length; i++) {
                        var quest_info = qm.getPlayer().getOneInfoQuest(eqp_number, "collection_eqp");
                        if (quest_info.contains("" + eqp_list[i][0])) {
                            eqp_count++;
                        }
                    }
                    var say = "#Cgreen#장비 아이템#k의 효과는 #Cyellow#데미지 증가#k이며 #d#h ##k님의 현재 등록 된 장비 아이템의 갯수는 #r" + eqp_count + "개#k입니다.\r\n";
                    say += "\r\n#Cgray#- 데미지 증가 : +" + parseInt(eqp_count / 2) + "%#k\r\n";
                    say += "\r\n#fUI/UIWindow.img/QuestIcon/3/0#\r\n";
                    for (i = 0; i < eqp_list.length; i++) {
                        say += " #L" + i + "##i" + eqp_list[i][0] + "##l";
                    }
                    qm.sendNext(say);
                    break;
                }
                case 1: {
                    var use_count = 0;
                    for (i = 0; i < use_list.length; i++) {
                        var quest_info = qm.getPlayer().getOneInfoQuest(use_number, "collection_use");
                        if (quest_info.contains("" + use_list[i][0])) {
                            use_count++;
                        }
                    }
                    var say = "#Cgreen#소비 아이템#k의 효과는 #Cyellow#보스 공격 시 데미지 증가#k이며 #d#h ##k님의 현재 등록 된 소비 아이템의 갯수는 #r" + use_count + "개#k입니다.\r\n";
                    say += "\r\n#Cgray#- 보스 공격 시 데미지 증가 : +" + parseInt(use_count / 2) + "%#k\r\n";
                    say += "\r\n#fUI/UIWindow.img/QuestIcon/3/0#\r\n";
                    for (i = 0; i < use_list.length; i++) {
                        say += " #L" + i + "##i" + use_list[i][0] + "##l";
                    }
                    qm.sendNext(say);
                    break;
                }
                case 2: {
                    var etc_count = 0;
                    for (i = 0; i < etc_list.length; i++) {
                        var quest_info = qm.getPlayer().getOneInfoQuest(etc_number, "collection_etc");
                        if (quest_info.contains("" + etc_list[i][0])) {
                            etc_count++;
                        }
                    }
	            var say = "#Cgreen#설치 아이템#k의 효과는 #Cyellow#크리티컬 최소 및 최대 데미지 증가#k이며 #d#h ##k님의 현재 등록 된 설치 아이템의 갯수는 #r" + ins_count + "개#k입니다.\r\n";
                    say += "\r\n#Cgray#- 몬스터의 방어율 일정 수준 무시 : +" + parseInt(etc_count / 2) + "%#k\r\n";
                    say += "\r\n#fUI/UIWindow.img/QuestIcon/3/0#\r\n";
                    for (i = 0; i < etc_list.length; i++) {
                        say += " #L" + i + "##i" + etc_list[i][0] + "##l";
                    }
                    qm.sendNext(say);
                    break;
                }
                case 3: {
                    var ins_count = 0;
                    for (i = 0; i < ins_list.length; i++) {
                        var quest_info = qm.getPlayer().getOneInfoQuest(ins_number, "collection_ins");
                        if (quest_info.contains("" + ins_list[i][0])) {
                            ins_count++;
                        }
                    }
                    var say = "#Cgreen#기타 아이템#k의 효과는 #Cyellow#몬스터의 방어율 일정 수준 무시#k이며 #d#h ##k님의 현재 등록 된 기타 아이템의 갯수는 #r" + etc_count + "개#k입니다.\r\n";
                    say += "\r\n#Cgray#- 크리티컬 최소 및 최대 데미지 증가 : +" + parseInt(ins_count / 2) + "%#k\r\n";
                    say += "\r\n#fUI/UIWindow.img/QuestIcon/3/0#\r\n";
                    for (i = 0; i < ins_list.length; i++) {
                        say += " #L" + i + "##i" + ins_list[i][0] + "##l";
                    }
                    qm.sendNext(say);
                    break;
                }
            }
            break;
        }
        case 2: {
            s2 = s1;
            s3 = selection;
            switch (s1) {
                case 0: {
                    for (i = 0; i < eqp_list.length; i++) {
                        var quest_info = qm.getPlayer().getOneInfoQuest(eqp_number, "collection_eqp");
                        if (quest_info.contains("" + eqp_list[selection][0])) {
                            qm.dispose();
                            qm.sendNext("\r\n#i" + eqp_list[selection][0] + "# #Cgreen##z" + eqp_list[selection][0] + "##k(은)는 #d컬렉션#k에 이미 등록되어 있는 #Cyellow#장비 아이템#k입니다.");
                            return;
                        }
                    }
                    if (!qm.haveItem(eqp_list[selection][0], 1)) {
                        qm.dispose();
                        qm.sendNext("\r\n#i" + eqp_list[selection][0] + "# #Cgreen##z" + eqp_list[selection][0] + "##k(을)를 소지 중에만 #d컬렉션#k의 #Cyellow#장비 아이템 등록#k을 진행 할 수 있습니다.");
                        return;
                    }
                    var say = "#i" + eqp_list[selection][0] + "# #Cgreen##z" + eqp_list[selection][0] + "##k의 #d컬렉션#k #Cyellow#장비 아이템 등록#k을 진행하겠습니다.";
                    qm.sendYesNoS(say, 2);
                    break;
                }
                case 1: {
                    for (i = 0; i < use_list.length; i++) {
                        var quest_info = qm.getPlayer().getOneInfoQuest(use_number, "collection_use");
                        if (quest_info.contains("" + use_list[selection][0])) {
                            qm.dispose();
                            qm.sendNext("\r\n#i" + use_list[selection][0] + "# #Cgreen##z" + use_list[selection][0] + "##k(은)는 #d컬렉션#k에 이미 등록되어 있는 #Cyellow#소비 아이템#k입니다.");
                            return;
                        }
                    }
                    if (!qm.haveItem(use_list[selection][0], 1)) {
                        qm.dispose();
                        qm.sendNext("\r\n#i" + use_list[selection][0] + "# #Cgreen##z" + use_list[selection][0] + "##k(을)를 소지 중에만 #d컬렉션#k의 #Cyellow#소비 아이템 등록#k을 진행 할 수 있습니다.");
                        return;
                    }
                    var say = "#i" + use_list[selection][0] + "# #Cgreen##z" + use_list[selection][0] + "##k의 #d컬렉션#k #Cyellow#소비 아이템 등록#k을 진행하겠습니다.";
                    qm.sendYesNoS(say, 2);
                    break;
                }
                case 2: {
                    for (i = 0; i < etc_list.length; i++) {
                        var quest_info = qm.getPlayer().getOneInfoQuest(etc_number, "collection_etc");
                        if (quest_info.contains("" + etc_list[selection][0])) {
                            qm.dispose();
                            qm.sendNext("\r\n#i" + etc_list[selection][0] + "# #Cgreen##z" + etc_list[selection][0] + "##k(은)는 #d컬렉션#k에 이미 등록되어 있는 #Cyellow#기타 아이템#k입니다.");
                            return;
                        }
                    }
                    if (!qm.haveItem(etc_list[selection][0], etc_list[selection][1])) {
                        qm.dispose();
                        qm.sendNext("\r\n#i" + etc_list[selection][0] + "# #Cgreen##z" + etc_list[selection][0] + "# " + etc_list[selection][1] + "개#k를 소지 중에만 #d컬렉션#k의 #Cyellow#기타 아이템 등록#k을 진행 할 수 있습니다.");
                        return;
                    }
                    var say = "#i" + etc_list[selection][0] + "# #Cgreen##z" + etc_list[selection][0] + "##k의 #d컬렉션#k #Cyellow#기타 아이템 등록#k을 진행하겠습니다.";
                    qm.sendYesNoS(say, 2);
                    break;
                }
                case 3: {
                    for (i = 0; i < ins_list.length; i++) {
                        var quest_info = qm.getPlayer().getOneInfoQuest(ins_number, "collection_ins");
                        if (quest_info.contains("" + ins_list[selection][0])) {
                            qm.dispose();
                            qm.sendNext("\r\n#i" + ins_list[selection][0] + "# #Cgreen##z" + ins_list[selection][0] + "##k(은)는 #d컬렉션#k에 이미 등록되어 있는 #Cyellow#설치 아이템#k입니다.");
                            return;
                        }
                    }
                    if (!qm.haveItem(ins_list[selection][0], 1)) {
                        qm.dispose();
                        qm.sendNext("\r\n#i" + ins_list[selection][0] + "# #Cgreen##z" + ins_list[selection][0] + "##k(을)를 소지 중에만 #d컬렉션#k의 #Cyellow#설치 아이템 등록#k을 진행 할 수 있습니다.");
                        return;
                    }
                    var say = "#i" + ins_list[selection][0] + "# #Cgreen##z" + ins_list[selection][0] + "##k의 #d컬렉션#k #Cyellow#설치 아이템 등록#k을 진행하겠습니다.";
                    qm.sendYesNoS(say, 2);
                    break;
                }
            }
            break;
        }
        case 3: {
            qm.dispose();
            var skill_id = 0;
            var list_id = [];
            var list_name = "";
            var list_number = 0;
            var collection_name = "";
            switch (s2) {
                case 0: {
                    skill_id = 8000001;
                    list_id = eqp_list;
                    list_name = "장비 아이템";
                    list_number = 100001;
                    collection_name = "collection_eqp";
                    break;
                }
                case 1: {
                    skill_id = 8000002;
                    list_id = use_list;
                    list_name = "소비 아이템";
                    list_number = 100002;
                    collection_name = "collection_use";
                    break;
                }
                case 2: {
                    skill_id = 8000003;
                    list_id = etc_list;
                    list_name = "기타 아이템";
                    list_number = 100003;
                    collection_name = "collection_etc";
                    break;
                }
                case 3: {
                    skill_id = 8000004;
                    list_id = ins_list;
                    list_name = "설치 아이템";
                    list_number = 100004;
                    collection_name = "collection_ins";
                    break;
                }
            }
            var quest_info = qm.getPlayer().getOneInfoQuest(list_number, collection_name);
            if (quest_info.equals("")) {
               qm.getPlayer().updateOneInfoQuest(list_number, collection_name, list_id[s3][0]);
            } else {
               qm.getPlayer().updateOneInfoQuest(list_number, collection_name, quest_info + "," + list_id[s3][0]);
            }
            qm.gainItem(list_id[s3][0], -(s2 == 2 ? list_id[s3][1] : 1));
            var list_info = qm.getPlayer().getOneInfoQuest(list_number, collection_name);
            var list_count = 0;
            for (i = 0; i < list_id.length; i++) {
                if (list_info.contains("" + list_id[i][0])) {
                    list_count++;
                }
            }
            var skill_level = parseInt(list_count / 2);
            qm.teachSkill(skill_id, skill_level, skill_level);
            var say = "\r\n#i" + list_id[s3][0] + "# #Cgreen##z" + list_id[s3][0] + "##k의 #Cyellow#" + list_name + " 등록#k을 성공적으로 완료하였습니다.";
            qm.sendNext(say);
            qm.getPlayer().getClient().sendPacket(Packages.tools.packet.CUserLocal.chatMsg(Packages.handling.ChatType.GameDesc, "[" + Packages.server.MapleItemInformationProvider.getInstance().getName(list_id[s3][0]) + "]의 " + list_name + " 등록을 성공적으로 완료하였습니다."));
            break;
        }
    }
}