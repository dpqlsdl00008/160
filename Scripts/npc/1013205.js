var status = -1;

function start() {
    status = -1;
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
    switch (status) {
        case 0: {
            cm.sendNext("몸은 괜찮은가, 나의 마스터? 피곤해 보이는군...");
            break;
        }
        case 1: {
            cm.sendNextPrevS("#b난 괜찮아. 제일 앞에서 싸우던 아란만 좀 다쳤고 나머지는 다들 무사해. 그보다 너야말로 괜찮은 거야?#k", 2);
            break;
        }
        case 2: {
            cm.sendNextPrev("문제 없다. 전투에는 전혀 지장 없을 거다.");
            break;
        }
        case 3: {
            cm.sendNextPrevS("#b내가 걱정하는 건 네 몸이 아니야. 네 마음이야. 네 동족들은 이미 전부...#k", 2);   
            break;
        }
        case 4: {
            cm.sendNextPrev("......");
            break;
        }
        case 5: {
            cm.sendNextPrevS("#b미안. 내가 널 이런 싸움에 끌어들였어. 널 검은 마법사에게 가게 뒀어야 했는데, 그랬다면... 오닉스 드래곤은 무사 할 수 있었을 텐데...#k", 2);
            break;
        }
        case 6: {
            cm.sendNextPrev("어리석은 말은 그만 둬라, 마스터. 우리는 우리가 원해서 싸운 거다. 네 잘못이 아니야.");
            break;
        }
        case 7: {
            cm.sendNextPrevS("#b하지만...!#k", 2);   
            break;
        }
        case 8: {
            cm.sendNextPrev("검은 마법사가 아무리 우리의 힘을 탐내도... 우리는 결코 검은 마법사의 편이 되지 않았을 거야. 우리 오닉스 드래곤은 강한 영훈을 가진 인간에게 끌리는 종족. 검은 마법사와 같이 사악한 자와는 함께 할 수 없어.");
            break;
        }
        case 9: {
            cm.sendNextPrev("그러니 사과하지 마라, 마스터. 아니, 프리드. 설령 싸우다 전멸한다 하더라도 그 것은 우리 오닉스 드래곤의 선택 일 뿐이야. 우리의 선택을 존중해 주길 바란다.");
            break;
        }
        case 10: {
            cm.sendNextPrevS("#b아프리엔...#k", 2);
            break;
        }
        case 11: {
            cm.sendNextPrev("다만 부탁이 있다. 검은 마법사와의 마지막 전투... 이 전투에서 내가 죽는다면 나의 아이를 보호해 다오. 아직 깨어나려면 멀었지만... 너라면 믿고 맡길 수 있다.");
            break;
        }
        case 12: {
            cm.sendNextPrevS("#b그런 말 하지마, 아프리엔. 살아남아서 네가 아이를 돌보면 되잖아!#k", 2);
            break;
        }
        case 13: {
            cm.sendNextPrev("우리들 중 누가 살아남을 지는 아무도 모른다. 그러니 미리 부탁하는 것 뿐이야. 약속해 주지 않을 건가, 마스터? ");
            break;
        }
        case 14: {
            cm.sendNextPrevS("#b알았어. 약속... 할게. 너도 약속해. 너도 살아남기 위해 최선을 다해줘.#k", 2);
            break;
        }
        case 15: {
            cm.sendNextPrev("물론이다, 마스터.");   
            break;
        }
        case 16: {
            cm.sendNextPrevS("#b절대로 날 위해 희생하지 마...#k", 2);
            break;
        }
        case 17: {
            cm.dispose();
            cm.forceStartQuest(22601, "1");
            cm.warp(914100021, 1);
            break;
        }
    }
}