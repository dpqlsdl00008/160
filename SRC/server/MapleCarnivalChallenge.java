/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

import java.lang.ref.WeakReference;
import client.MapleCharacter;
import handling.world.MaplePartyCharacter;

/**
 * TODO : Make this a function for NPC instead.. cleaner
 *
 * @author Rob
 */
public class MapleCarnivalChallenge {

    WeakReference<MapleCharacter> challenger;
    String challengeinfo = "";

    public MapleCarnivalChallenge(MapleCharacter challenger) {
        this.challenger = new WeakReference<MapleCharacter>(challenger);
        challengeinfo += "#b";
        for (MaplePartyCharacter pc : challenger.getParty().getMembers()) {
            MapleCharacter c = challenger.getMap().getCharacterById(pc.getId());
            if (c != null) {
                challengeinfo += (c.getName() + " / 레벨 " + c.getLevel() + " / " + getJobNameById(c.getJob()) + "\r\n");
            }
        }
        challengeinfo += "#k\r\n";
    }

    public MapleCharacter getChallenger() {
        return challenger.get();
    }

    public String getChallengeInfo() {
        return challengeinfo;
    }

    public static final String getJobNameById(int job) {
        switch (job) {
            case 0:
                return "초보자";
            case 1000:
                return "노블레스";
            case 2000:
                return "레전드";
            case 2001:
                return "에반";
            case 3000:
                return "시티즌";
            case 100:
                return "검사";
            case 110:
                return "파이터";
            case 111:
                return "크루세이더";
            case 112:
            case 113:
            case 114:
                return "히어로";
            case 120:
                return "페이지";
            case 121:
                return "나이트";
            case 122:
                return "팔라딘";
            case 130:
                return "스피어맨";
            case 131:
                return "용기사";
            case 132:
                return "다크나이트";
            case 200:
                return "마법사";
            case 210:
                return "위자드(불,독)";
            case 211:
                return "메이지(불,독)";
            case 212:
                return "아크메이지(불,독)";
            case 220:
                return "위자드(얼음,번개)";
            case 221:
                return "메이지(얼음,번개)";
            case 222:
                return "아크메이지(얼음,번개)";
            case 230:
                return "클레릭";
            case 231:
                return "프리스트";
            case 232:
                return "비숍";
            case 300:
                return "아처";
            case 310:
                return "헌터";
            case 311:
                return "레인저";
            case 312:
                return "보우마스터";
            case 320:
                return "사수";
            case 321:
                return "저격수";
            case 322:
                return "신궁";
            case 400:
                return "로그";
            case 410:
                return "어쌔신";
            case 411:
                return "허밋";
            case 412:
                return "나이트로드";
            case 420:
                return "시프";
            case 421:
                return "시프마스터";
            case 422:
                return "섀도어";
            case 430:
                return "새미듀어러";
            case 431:
                return "듀어러";
            case 432:
                return "듀얼마스터";
            case 433:
                return "슬래셔";
            case 434:
                return "듀얼블레이더";
            case 500:
                return "해적";
            case 510:
                return "인파이터";
            case 511:
                return "버커니어";
            case 512:
                return "바이퍼";
            case 520:
                return "건슬링거";
            case 521:
                return "발키리";
            case 522:
                return "캡틴";
            case 501:
                return "해적";
            case 530:
                return "캐논슈터";
            case 531:
                return "캐논블래스터";
            case 532:
                return "캐논마스터";
            case 5000:
            case 5100:
            case 5110:
            case 5111:
            case 5112:
                return "미하일";
            case 1100:
            case 1110:
            case 1111:
            case 1112:
                return "소울마스터";
            case 1200:
            case 1210:
            case 1211:
            case 1212:
                return "플레임위자드";
            case 1300:
            case 1310:
            case 1311:
            case 1312:
                return "윈드브레이커";
            case 1400:
            case 1410:
            case 1411:
            case 1412:
                return "나이트워커";
            case 1500:
            case 1510:
            case 1511:
            case 1512:
                return "스트라이커";
            case 2100:
            case 2110:
            case 2111:
            case 2112:
                return "아란";
            case 2200:
            case 2210:
            case 2211:
            case 2212:
            case 2213:
            case 2214:
            case 2215:
            case 2216:
            case 2217:
            case 2218:
                return "에반";
            case 2002:
            case 2300:
            case 2310:
            case 2311:
            case 2312:
                return "메르세데스";
            case 3001:
            case 3100:
            case 3110:
            case 3111:
            case 3112:
                return "데몬슬레이어";
            case 3200:
            case 3210:
            case 3211:
            case 3212:
                return "배틀메이지";
            case 3300:
            case 3310:
            case 3311:
            case 3312:
                return "와일드헌터";
            case 3500:
            case 3510:
            case 3511:
            case 3512:
                return "메카닉";
            case 2003:
            case 2400:
            case 2410:
            case 2411:
            case 2412:
                return "팬텀";
            case 2004:
            case 2700:
            case 2710:
            case 2711:
            case 2712:
                return "루미너스";
            case 6000:
            case 6100:
            case 6110:
            case 6111:
            case 6112:
                return "하야토";
            case 6001:
            case 6200:
            case 6210:
            case 6211:
            case 6212:
                return "칸나";
            case 6002:
            case 6500:
            case 6510:
            case 6511:
            case 6512:
                return "묵현";
            case 4000:
            case 4200:
            case 4210:
            case 4211:
            case 4212:
                return "비스트테이머";
            case 5001:
            case 5200:
            case 5210:
            case 5211:
            case 5212:
                return "린";
            case 800:
            case 900:
            case 910:
                return "운영자";
            case 12000:
            case 12100:
                return "카마도 탄지로";
            default:
                return null;
        }
    }

    public static final String getJobBasicNameById(int job) {
        switch (job) {
            case 0:
            case 1000:
            case 2000:
            case 2001:
            case 2002:
            case 2003:
            case 2004:
            case 3000:
            case 3001:
            case 4001:
            case 4002:
            case 6000:
            case 6001:
                return "Beginner";
            case 100:
            case 110:
            case 111:
            case 112:
            case 120:
            case 121:
            case 122:
            case 130:
            case 131:
            case 132:
            case 1100:
            case 1110:
            case 1111:
            case 1112:
            case 2100:
            case 2110:
            case 2111:
            case 2112:
            case 3100:
            case 3110:
            case 3111:
            case 3112:
            case 4100:
            case 4110:
            case 4111:
            case 4112:
            case 6100:
            case 6110:
            case 6111:
            case 6112:
                return "Warrior";
            case 200:
            case 210:
            case 211:
            case 212:
            case 220:
            case 221:
            case 222:
            case 230:
            case 231:
            case 232:
            case 1200:
            case 1210:
            case 1211:
            case 1212:
            case 2200:
            case 2210:
            case 2211:
            case 2212:
            case 2213:
            case 2214:
            case 2215:
            case 2216:
            case 2217:
            case 2218:
            case 2700:
            case 2710:
            case 2711:
            case 2712:
            case 3200:
            case 3210:
            case 3211:
            case 3212:
            case 4200:
            case 4210:
            case 4211:
            case 4212:
                return "Magician";
            case 300:
            case 310:
            case 311:
            case 312:
            case 320:
            case 321:
            case 322:
            case 1300:
            case 1310:
            case 1311:
            case 1312:
            case 2300:
            case 2310:
            case 2311:
            case 2312:
            case 3300:
            case 3310:
            case 3311:
            case 3312:
            case 6500:
            case 6510:
            case 6511:
            case 6512:
                return "Bowman";
            case 400:
            case 410:
            case 411:
            case 412:
            case 420:
            case 421:
            case 422:
            case 430:
            case 431:
            case 432:
            case 433:
            case 434:
            case 1400:
            case 1410:
            case 1411:
            case 1412:
            case 2400:
            case 2410:
            case 2411:
            case 2412:
                return "Thief";
            case 500:
            case 501:
            case 508:
            case 510:
            case 511:
            case 512:
            case 520:
            case 521:
            case 522:
            case 530:
            case 531:
            case 532:
            case 570:
            case 571:
            case 572:
            case 1500:
            case 1510:
            case 1511:
            case 1512:
            case 3500:
            case 3510:
            case 3511:
            case 3512:
                return "Pirate";
            default:
                return null;
        }
    }
}
