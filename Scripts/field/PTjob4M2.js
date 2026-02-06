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
            if (cm.getJob() != 2411) {
                cm.dispose();
                return;
            }
            cm.lockInGameUI(true);
            cm.sendNextS("\r\n...아리아의 초상화... 여기에 있었군.", 17);
            break;
        }
        case 1:{
            cm.sendNextPrevS("\r\n...그리운 얼굴이로군. 맹하게 생겨서는 바보처럼 웃기는. 그래, 이 뒤에 스킬 북을 숨겨 놨었지. 누가 훔쳐 가겠냐고 하면서. 하... 정말이지...", 17);
            break;
        }
        case 2:{
            cm.sendNextPrevS("\r\n아리아의 초상화... 이 뒤에 노트를 뒀구나... 맞아, 기억 나. 이 노트를 완성하고 나서 에레브에 예고장을 보냈어. 스승님을 넘어 섰으니 스승님도 훔치지 못한 보물을 훔쳐 보겠다면서...", 17);
            break;
        }
        case 3:{
            cm.sendNextPrevS("\r\n이 초상화... 누구한테 훔친 거 였더라. 초상화로 본 아리아는 너무 만만해 보여서, 에레브의 보물 정도는 정말 쉽게 훔쳐 낼 거라고 생각했는데... 하하하, 철이 없었지...", 17);
            break;
        }
        case 4:{
            cm.sendNextPrevS("\r\n그 때는 그저 메이플 월드를 놀라게 만들 괴도로서 살겠다고 생각했는데... 괴도가 영웅으로 불리게 될 줄 누가 알았겠어? 아리아, 너도 몰랐겠지? 후후...", 17);
            break;
        }
        case 5:{
            cm.dispose();
            cm.forceCompleteQuest(25122);
            cm.changeJob(2412);
            cm.lockInGameUI(false);
            break;
        }
    }
}