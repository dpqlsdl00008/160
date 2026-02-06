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
            cm.lockInGameUI(true);
            cm.sendNextS("\r\n...진실만을 말해라, 진. 이 기록이... 트리스탄의 기록이 사실인가? 정말로 전대 다크로드님과 함께 적을 쫓고 있었어?", 1, 0, 1057001);
            break;
        }
        case 1:{
            cm.sendNextPrevS("\r\n...이제는 상관 없는 일이지 않나?", 1, 0, 1052001);
            break;
        }
        case 2:{
            cm.sendNextPrevS("\r\n상관 없을 리가 없잖아! 말 돌리지 마! 이미 엘리니아에서 제피라는 자에게도 확인했어...! 정말 네가 전대 다크로드님을 죽이지 않았다는 거야?", 1, 0, 1057001);
            break;
        }
        case 3:{
            cm.sendNextPrevS("\r\n...", 1, 0, 1052001);
            break;
        }
        case 4:{
            cm.sendNextPrevS("\r\n왜? 왜 진실을 숨겼지? 설희님이 비화원에서 복수의 칼날을 갈고 있다는 걸 알고 있었잖아? 왜 그대로 둔 거야? 대체 왜?", 1, 0, 1057001);
            break;
        }
        case 5:{
            cm.sendNextPrevS("\r\n설희님이 널 죽이러 올 때 까지 입 다물고 모르는 척 할 생각이었나? 그게 대체 너에게 무슨 이익이 있는데? 겨우 이런 진실을 맞이 하게 위해 설희님께서 애쓰신 게 아니야! 왜 사람을 바보로 만들어?!", 1, 0, 1057001);
            break;
        }
        case 6:{
            cm.sendNextPrevS("\r\n알려주면...", 1, 0, 1052001);
            break;
        }
        case 7:{
            cm.sendNextPrevS("\r\n말해!", 1, 0, 1057001);
            break;
        }
        case 8:{
            cm.sendNextPrevS("\r\n알려주면 설희가 지금처럼 성장했을까?", 1, 0, 1052001);
            break;
        }
        case 9:{
            cm.sendNextPrevS("\r\n무슨 말을 하고 있는 거야?", 1, 0, 1057001);
            break;
        }
        case 10:{
            cm.sendNextPrevS("\r\n설희에게 모든 진실을 알려주면, 그 다음은 어떻게 되는데? 그래, 다크로드님은 네가 인질로 잡힌 바람에 어쩔 수 없이 적에게 목숨을 내주셨단다... 그렇게 말해주면, 그 다음에 설희는?", 1, 0, 1052001);
            break;
        }
        case 11:{
            cm.sendNextPrevS("\r\n...!", 1, 0, 1057001);
            break;
        }
        case 12:{
            cm.sendNextPrevS("\r\n지금의 설희는 비화원의 수장이 될 정도로 냉철하지만, 그 당시는 아니었어. 그 애가 사랑하던 사람을 잃고 그 자책감과 죄악감을 견딜 수 있었을 거라고 생각 해? 그렇게 두느니...", 1, 0, 1052001);
            break;
        }
        case 13:{
            cm.sendNextPrevS("\r\n그냥 모르는 게 나을 거라고 생각했어. ...증오로라도 살아가길 바랬으니까.", 1, 0, 1052001);
            break;
        }
        case 14:{
            cm.sendNextPrevS("\r\n...그래서... 지금까지 사실을 숨겼던 거냐?", 1, 0, 1057001);
            break;
        }
        case 15:{
            cm.sendNextPrevS("\r\n미안하다...", 1, 0, 1052001);
            break;
        }
        case 16:{
            cm.sendNextPrevS("\r\n네 그 사과만큼 설희님을 상처 입히는 말은 없을 거다.", 1, 0, 1057001);
            break;
        }
        case 17:{
            cm.sendNextPrevS("\r\n...", 1, 0, 1052001);
            break;
        }
        case 18:{
            cm.dispose();
            cm.lockInGameUI(false);
            cm.forceStartQuest(2644, "1");
            break;
        }
    }
}