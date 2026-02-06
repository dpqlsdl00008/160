var status = -1;

function start() {
    action (1, 0, 0);
}

function action(mode, type, selection) {
    if (mode == 1) {
        status++;
    } else {
        if (status == 0 || status == 1) {
            if (status == 1) {
                cm.sendNext("\r\n흠... 지금은 그럴 여유가 없는 모양이지? 하지만 마음이 바뀌게 된다면 언제라도 날 다시 찾아오라구~!");
            }
            cm.dispose();
            return;
        }
        status--;
    }
    switch (status) {
        case 0: {
            cm.sendSimple("나한테 무슨 볼일인 건가?\r\n#b#L0#이곳에 대해 알려주세요.\r\n#L1##p1032102#에게 이야기를 듣고 찾아왔는데...");
            break;
        }
        case 1: {
            switch (selection) {
                case 1: {
                    cm.dispose();
                    cm.sendNext("\r\n이봐... 자네 정말 #b#p1032102##k를 만난적이 있는건가? 괜히 만난적도 없으면서 만났다고 거짓말 하는거 아냐? 그런 거짓말은 나에겐 안통한다고~!");
                    break;
                }
                default: {
                    if (cm.haveItem(4031035) == true) {
                        cm.dispose();
                        cm.sendNext("\r\n그 편지를 펫과 함께 장애물을 넘으면서 위로 올라가서 내 동생인 #p1012007#에게 가져다 주라고. 편지를 전해주면 분명 펫에게 좋은 일이 있을 거라고.");
                        return;
                    }
                    cm.sendYesNo("이곳은 펫과 함께할 수 있는 산책로이지. 산책만 하다가 가도 되지만 이곳 저곳에 있는 장애물을 통과하는 훈련을 시켜볼 수도 있어. 아직 펫과 친해지지 못했다면 말을 잘 안들을 수도 있지만 말야. 어때? 한 번 펫에게 훈련을 시켜 보겠나?");
                    break;
                }
            }
            break;
        }
        case 2: {
            cm.dispose();
            cm.gainItem(4031035, 1);
            cm.sendNext("\r\n좋았어! 자, 여기 편지를 받도록 해. 그냥 갔다가는 내가 보낸 사람인 줄 모를테니까... 펫과 같이 장애물을 넘으면서 꼭대기까지 올라가서 #p1012007#에게 말을 걸어 편지를 전해주도록 해. 펫에게 신경쓰면서 간다면 어렵지 않을거야. 그럼 열심히 해보라고!");
            break;
        }
    }
}