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
            if (cm.getMapId() != 931000030) {
                cm.dispose();
            } else {
                cm.sendNext("휴... 아슬아슬하게 따돌린 것 같군. 쉴러 정도 되는 녀석에게 질 거라고 생각하지는 않지만 너희까지 보호 할 자신은 없었거든?");
            }
            break;
        }
        case 1: {
            cm.sendNextPrevS("죄, 죄송해요. #h0#에게는 아무런 잘못도 없어요? 오히려 절 구해준 걸요", 4, 2159007);
            break;
        }
        case 2: {
            cm.sendNextPrev("응? 그러고보니 넌... 마을 사람 같지 않군. 그 이상한 옷은 대체 뭐지? 넌 설마 블랙윙에게 잡혀 있던 거냐?");
            break;
        }
        case 3: {
            cm.sendNextPrevS("#b(벨비티는 방금 전에 있던 일에 대해 간단히 설명했다.)#k", 4, 2159007);
            break;
        }
        case 4: {
            cm.sendNextPrev(".. 휴.. 그래 블랙윙이 위험한 실험을 하고 있을지도 모른다는 추측은 있었지만 설마 사실일 줄이야.. 정말 무서운 일이군. 어서 가서 알리고 대책을 마련해야겟어.");
            break;
        }
        case 5: {
            cm.sendNextPrevS("저기.. 그런데 당신은 누구세요? 누구신데 거기 갑자기 나타나신거죠? 그리고 왜 저희를 구해주신 거예요?", 2);
            break;
        }
        case 6: {
            cm.sendNextPrev("..그건 너도 다 큰 것 같고, 이런 일까지 있었으니 숨기기 어려울것 같군.. 그래, 말해주마 너도 우리 마을 에델슈타인이 블랙윙의 지배를 받고 있다는 사실은 알겠지?");
            break;
        }
        case 7: {
            cm.sendNextPrev("빼앗긴 광산, 점령당한 의회, 감시자들의 존재, 우리 마을의 사람들은 노예처럼 숨 죽인 채 그들의 명령을 들을 수밖에 없지. 하지만 블랙윙이라 하더라도 마음까지 지배하는 것은 불가능하다.");
            break;
        }
        case 8: {
            cm.sendNextPrev("나는 레지스탕스 동료들과 함께 블랙윙에 대항하는 에델슈타인 레지스탕스의 일원이다. 이름은 알려줄 수 없지만 암호명은 J라고 하지.");
            break;
        }
        case 9: {
            cm.sendNextPrev("알겠으면 이만 마을로 돌아가렴. 위험하니 다시는 이쪽으로 오지말고, 실헴체였던 아이는 그냥 두면 도로 잡혀갈 수도 있으니 내가 동료들에게 데려가마. 나를 여기서 본 것은 비밀로 해다오.");
            break;
        }
        case 10: {
            cm.sendNextPrevS("한 가지만 더 말해 주세요. 저도 레지스탕스에 들어갈 수 있나요?", 2);
            break;
        }
        case 11: {
            cm.sendNextPrev("훗.. 너도 블랙윙과 싸우고 싶은 거냐? 그 마음이 있다면 레지스탕스에 못 들어올 것도 없지. 하지만 아직은 아냐. 레벨이 10이되면 레지스탕스 쪽에서 먼저 네게 접근할 거다. 동료가 되면 다시 볼수도 있겠지 그럼 이만.");
            break;
        }
        case 12: {
            cm.dispose();
            cm.forceCompleteQuest(23007);
            cm.gainItem(2000000, 3);
            cm.gainItem(2000003, 3);
            cm.gainExp(90);
            cm.warp(310000000, 8);
            break;
        }
    }
}