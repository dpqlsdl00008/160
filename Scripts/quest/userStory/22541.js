var status = -1;

function start(mode, type, selection) {
    if (mode == 1) {
        status++;
    } else {
        if (status == 0) {
            qm.dispose();
            return;
        }
        status--;
    }
    switch (status) {
        case 0: {
            qm.sendNext("책을 빌리러 온 건가? 하긴. 강한 힘을 갖고 있다고 해서 안주하지 말고 더 노력하는 자세야 말로 가장 중요하니... 흠흠. 그래, 무슨 책을 보고 싶은 겐가?");
            break;
        }
        case 1: {
            qm.sendNextPrevS("드래곤의 종류와 특징(상)의 뒷권이 보고 싶어요.", 2);
            break;
        }
        case 2: {
            qm.sendNextPrev("아... 리프레에서 발행된 그 책 말이로군. 어디보자... (상)권은 분명 얼마 전에 헤네시스의 청년에게 빌려줬고 (하)권은...이런, 그 책도 누군가 빌려가 버렸군.");
            break;
        }
        case 3: {
            qm.sendNextPrevS("에엑~ 이미 누가 빌려 갔다고요? 누가 빌려 갔나요?", 2);
            break;
        }
        case 4: {
            qm.sendNextPrev("#b커닝시티#k의 #b이카루스#k라는 소년을 아는가? 하늘을 날고 싶어하는 그 소년이 빌려갔지. 가지고 간지 꽤 됐는데 아직도 반납을 하지 않았군... 흠");
            break;
        }
        case 5: {
            qm.sendNextPrevS("어, 언제쯤 반납할까요?", 2);
            break;
        }
        case 6: {
            qm.sendAcceptDecline("글쎄... 마법 도서관에는 대여 기한이 없어서... 아, 그렇지. 괜찮다면 자네가 커닝시티로 가서 이카루스 소년에게 드래곤의 종류와 특징(하)를 받아 읽고 나서 반납해 주겠나?");
            break;
        }
        case 7: {
            qm.sendNext("자네는 책을 봐서 좋고 나는 반납 받아서 좋고, 좋은 일이지. 그런데 나이도 어린 것 같은데 드래곤에 관심이 많다니 신기하군. 그러고 보니 옆에도 특이한 도마뱀을 데리고 다니고 있고... 흠... 정말 신기하게 생긴 도마뱀인데? 어떤 종류인지 물어봐도 되나?");
            break;
        }
        case 8: {
            qm.sendNextPrevS("#b(미르가 드래곤임을 들키면 안 된다! 거절하자!)#k", 2);
            break;
        }
        case 9: {
            qm.sendNextPrev("그, 그렇게까지 고개를 도리저을 필요는 없는데... 알았네. 그럼 책이나 받아다 주게. 부탁하네");
            break;
        }
        case 10: {
            qm.dispose();
            qm.forceStartQuest();
            break;
        }
    }
}