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
            qm.sendNext("뭐야, 내 팬인가? 드디어 이 라이더 카푸님의 명성이 메이플 월드 방방곳곳에 퍼지기 시작했군. 미리 말하지만 내 싸인은 비싸거든, 한 1억 메소 정도?");
            break;
        }
        case 1: {
            qm.sendNextPrev("도무지 유머를 모르는 친구로군. 나를 소개하지. 내 이름은 카푸! 발음에 주의하라고. '카프'나 '카뿌'로 발음한다면 엉덩이를 걷어차 줄 테니 말이야. 크하핫!");
            break;
        }
        case 2: {
            qm.sendNextPrev("이런, 서두는 잡아떼고 본론부터? 마음에 드는 친구로군. 역시 인생에서 가장 중요한 건 속도거든. 암벽 거인에게 가는 길은 멀고도 높고도 험하기 때문에 걸어서 이동하는 것은 불가능해. 하지만 이 카푸님의 라이딩이 있다면 이야기는 달라지지! 물론 네가 조수 역할을 제대로 할 수 있다면.");
            break;
        }
        case 3: {
            qm.askAcceptDecline("간단해. 앞으로 달리는 건 무조건 내가 한다! 넌 조수석에 앉아서 나머지 역할을 맡아주면 되는 거야. 백문이 불여일견. 어디 한 번 달려볼래, 친구?");
            break;
        }
        case 4: {
            qm.sendNext("마음에 들어, 친구! 그럼 달려보자고!");
            break;
        }
        case 5: {
            qm.sendNextPrev("잘 들어, 암벽 거인에게 가는 길에는 총 세 가지 장애물들이 있어!");
            break;
        }
        case 6: {
            qm.sendNextPrev("전방에 장애물이 있어? Alt 키를 눌러 점프하라고, 친구! 이 카푸님의 라이딩은 굉장한 점프력을 가지고 있으니까 말이야.");
            break;
        }
        case 7: {
            qm.sendNextPrev("전방에 플로라가 있어? Ctrl 키를 눌러 잘라내라고, 친구! 무엇이든 잘라내는 고성능 가위에 아마 놀라게 될 걸?");
            break;
        }
        case 8: {
            qm.sendNextPrev("전방에 호넷이 날아와? Shift 키를 눌러 불을 뿜으라고, 친구! 우리를 가로막는 것들은 불살라 버리는 거야!");
            break;
        }
        case 9: {
            qm.sendNextPrev("우리의 목표는 주어진 시간 동안 무사히 완주하는 것이야. 장애물에 맞아 체력이 다 닳아 없어지면 그대로 미션 실패. 어때, 간단하지?");
            break;
        }
        case 10: {
            qm.dispose();
            qm.forceStartQuest();
            break;
        }
    }
}