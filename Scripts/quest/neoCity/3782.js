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
            qm.sendNext("그건 참 신비로운 이야기였지. 할머님께서 아직은 어렸던 시기에 어떤 모험가를 만났는데. 그 모험가는 시간의 신전으로 떠나면서 검은 마법사의 봉인에 대해 말을 해주었다고 하네.");
            break;
        }
        case 1: {
            qm.sendNextPrevS("검은 마법사...?! 그 봉인이 시간의 힘과 어떤 과련이 있죠?", 2);
            break;
        }
        case 2: {
            qm.sendNextPrev("그런 셈이지. 봉인된 시점을 중심으로 시간이 뒤틀려 만에 하나 시간을 뛰어넘는 힘을 가진 사람이 봉인 이전의 순간으로 돌아가려고 해도 불가능하게 했다는데, 듣는 것만으로도 엄청난 일이 아닐 수 없지.");
            break;
        }
        case 3: {
            qm.sendNextPrevS("#b(어쩌면 엔디 역시 검은 마법사의 봉인 때문에 사고가 났을 지도 몰라. 이제 돌아가서 앤디에게 말해줘야... 또 다시 타임 다이버에서 빛이...!)#k", 2);
            break;
        }
        case 4: {
            qm.dispose();
            qm.warp(240070000, "west00");
            qm.forceStartQuest();
            break;
        }
    }
}