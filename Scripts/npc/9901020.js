var status = -1;

var v1 = [
1302050, // 글라디우스
1302051, // 쥬얼 쿠아다라
1302052, // 네오코라
1302053, // 레드 카타나
1302054, // 아츠
1312026, // 벅
1312027, // 호크헤드
1312028, // 미하일
1312029, // 리프 엑스
1322046, // 워해머
1322047, // 너클메이스
1322048, // 타무스
1322049, // 스튬
1322050, // 골든해머
1402030, // 왕푸
1402031, // 호검
1402032, // 그리스
1402033, // 그륜힐
1402034, // 그레이트 로헨
1412022, // 라이징
1412023, // 샤이닝
1412024, // 크로노
1412025, // 헬리오스
1422023, // 골든 모울
1422024, // 플루튬
1422025, // 호프만
1422026, // 크롬
1432031, // 삼지창
1432032, // 장팔사모
1432033, // 십자창
1432034, // 스페판
1432035, // 호진공창
1442040, // 구룡도
1442041, // 방천극
1442042, // 황룡도
1442043, // 월아산
];
var v2 = [
1372027, // 위저드 완드
1372028, // 크로미
1372029, // 이블테일러
1372030, // 엔젤윙즈
1382030, // 아크 스태프
1382031, // 쏜즈
1382032, // 이블윙즈
1382033, // 레이든 스태프
1382034, // 케이그
];
var v3 = [
1452038, // 라이덴
1452039, // 올림푸스
1452040, // 봉황위궁
1452041, // 골든 힌켈
1452042, // 다크 아룬드
1462033, // 산양 석궁
1462034, // 로우어
1462035, // 골든 크로우
1462036, // 그로스야거
1462037, // 다크 샬리트
1522039, // 더블 윙
1522040, // 페이탈
1522041, // 문 글로리
1522042, // 에오로
1522043, // 실피드 스카이아
];
var v4 = [
1332043, // 리프 크로
1332044, // 신기타
1332045, // 게타
1332046, // 칸디네
1332047, // 용천권
1342013, // 의천도
1342014, // 패왕도
1342015, // 아슈켈론
1342016, // 천무도
1342017, // 용화도
1472045, // 아다만티움 가즈
1472046, // 다크 슬레인
1472047, // 다크 기간틱
1472048, // 흑갑충
1472049, // 코브라스티어
1362032, // 루주 웨이
1362033, // 오리엔탈 로얄 케인
1362034, // 비쥬 칸느
1362035, // 란 에이
1362036, // 마제스티 케인
];
var v5 = [
1482015, // 프라임 핸즈
1482016, // 데빌클로
1482017, // 세라핌즈
1482018, // 베이아 크래쉬
1482019, // 스틸르노
1492015, // 콜드마인드
1492016, // 라스펠트건
1492017, // 버닝헬
1492018, // 어비스 슈터
1492019, // 인피티니
1532024, // 아트럴리
1532025, // 드레드넛
1532026, // 플라멘 베르퍼
1532027, // 헬크래쉬
1532028, // 라이노
];
var v6 = [
1542021, // 호철
1542022, // 호철진타
1542023, // 사진
1542024, // 자전
1542025, // 열풍환
];
var v7 = [
1552021, // 삼문선
1552022, // 자문등선
1552023, // 상창선
1552024, // 주묘선
1552025, // 귀선
];

function start() {
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
        status--;
    }
    var cJob = (parseInt(cm.getJob() / 100) + "");
    var a1 = v1;
    if (cJob.endsWith("2")) {
        a1 = v2;
    }
    if (cJob.endsWith("3")) {
        a1 = v3;
    }
    if (cJob.endsWith("4")) {
        a1 = v4;
    }
    if (cJob.endsWith("5")) {
        a1 = v5;
    }
    if (cm.getJob() > 6099 && cm.getJob() < 6113) {
        a1 = v6;
    }
    if (cm.getJob() > 6199 && cm.getJob() < 6213) {
        a1 = v7;
    }
    switch (status) {
        case 0: {
            cm.sendYesNo("#b리첸시아 무기 대여 서비스#k를 이용 하시려면 #r0 메소#k가 필요하며, 게임 종료 시에 아이템은 사라지니 주의 하시길 바랍니다.");
            break;
        }
        case 1: {
            var say = "대여를 원하시는 아이템을 선택해 주세요.\r\n\r\n#fUI/UIWindow.img/QuestIcon/3/0#";
            for (i = 0; i < a1.length; i++) {
                if (cm.getJob() > 429 && cm.getJob() < 435) {
                    if (parseInt(a1[i] / 10000) == 147) {
                        continue;
                    }
                }
                if (cm.getJob() > 509 && cm.getJob() < 513) {
                    if (parseInt(a1[i] / 10000) != 148) {
                        continue;
                    }
                }
                if (cm.getJob() > 519 && cm.getJob() < 523) {
                    if (parseInt(a1[i] / 10000) != 149) {
                        continue;
                    }
                }
                if (cm.getJob() > 1299 && cm.getJob() < 1313) {
                    if (parseInt(a1[i] / 10000) != 145) {
                        continue;
                    }
                }
                if (cm.getJob() > 1399 && cm.getJob() < 1413) {
                    if (parseInt(a1[i] / 10000) != 147) {
                        continue;
                    }
                }
                if (cm.getJob() > 1499 && cm.getJob() < 1513) {
                    if (parseInt(a1[i] / 10000) != 148) {
                        continue;
                    }
                }
                if (cm.getJob() > 2099 && cm.getJob() < 2113) {
                    if (parseInt(a1[i] / 10000) != 144) {
                        continue;
                    }
                }
                if (cm.getJob() > 2199 && cm.getJob() < 2219) {
                    if (parseInt(a1[i] / 10000) != 138) {
                        continue;
                    }
                }
                if (cm.getJob() > 2299 && cm.getJob() < 2313) {
                    if (parseInt(a1[i] / 10000) != 152) {
                        continue;
                    }
                }
                if (cm.getJob() > 2399 && cm.getJob() < 2413) {
                    if (parseInt(a1[i] / 10000) != 136) {
                        continue;
                    }
                }
                if (cm.getJob() > 3099 && cm.getJob() < 3113) {
                    if (parseInt(a1[i] / 10000) != 132) {
                        continue;
                    }
                }
                if (cm.getJob() > 3199 && cm.getJob() < 3213) {
                    if (parseInt(a1[i] / 10000) != 138) {
                        continue;
                    }
                }
                if (cm.getJob() > 3299 && cm.getJob() < 3313) {
                    if (parseInt(a1[i] / 10000) != 146) {
                        continue;
                    }
                }
                if (cm.getJob() > 3499 && cm.getJob() < 3513) {
                    if (parseInt(a1[i] / 10000) != 149) {
                        continue;
                    }
                }
                if (cm.getJob() > 5099 && cm.getJob() < 5113) {
                    if (parseInt(a1[i] / 10000) != 130) {
                        continue;
                    }
                }
                say += "\r\n#L" + i + "##i" + a1[i] + "# #b(Lv." + Packages.server.MapleItemInformationProvider.getInstance().getEquipStats(a1[i]).get("reqLevel") + ") #z" + a1[i] + "##k";
            }
            cm.sendSimple(say);
            break;
        }
        case 2: {
            jgys = selection;
            cm.sendYesNo("#b#h ##k님께서 대여를 원하시는 아이템은 아래와 같으며, 해당 아이템은 게임 종료 시에 사라집니다.\r\n\r\n#i" + a1[selection] + "# #b#z" + a1[selection] + "# 1개#k");
            break;
        }
        case 3: {
            cm.dispose();
            cm.gainItem(a1[jgys], 1);
            break;
        }
    }
}