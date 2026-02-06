var status = -1;

function start() {
    switch (cm.getMapId()) {
        case 930000400: {
            status = -1;
            break;
        }
        case 930000500: {
            status = 2;
            break;
        }
    }
    action (1, 0, 0);
}

function action(mode, type, selection) {
    if (mode == 1) {
        status++;
    } else {
        cm.dispose();
        return;
    }
    switch (status) {
        case 0: {
            cm.sendYesNo("구해주셔서 감사합니다용! 지금까지는 독 때문에 괴인의 수하로 지냈습니다용! 하지만 그 덕분에 괴인의 행동을 보아 왔으니, 여러분들을 안내 드릴 수 있습니다용! 자, 그럼 더 깊은 숲으로 보내드릴게용!");
            break;
        }
        case 1: {
            cm.dispose();
            if (cm.allMembersHere() == false) {
                return;
            }
            cm.warpParty(cm.getMapId() + 100);
            break;
        }
        case 2: {
            cm.dispose();
            break;
        }
        case 3: {
            if (cm.haveItem(4001163, 1) == true) {
                cm.sendNext("\r\n보라색 마력석을 가져오셨군용! 괴인은 이걸 제단 위에 올려 놓고 뭔가 연구하곤 했습니다용! 당신께서도 그렇게 하면 되지 않을까용? 그럼 괴인이 연구를 하던 #b독의 숲#k으로 보내드릴게용!");
                return;
            }
            cm.dispose();
            cm.sendNext("\r\n저 옆에 있는 독 나무에서 만들어지는 보라색 마력석으로 괴인은 뭔가 연구를 하곤 했습니다용! 아마 만들어진 마력석은 괴인의 책상에 있을 겁니다용! 보라색 마력석을 가져와 주세용!");
            break;
        }
        case 4: {
            cm.dispose();
            if (cm.allMembersHere() == false) {
                return;
            }
            cm.warpParty(cm.getMapId() + 100);
            break;
        }
    }
}