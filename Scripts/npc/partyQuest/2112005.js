var status = -1;

function start() {
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
            if (cm.getPlayer().getIntNoRecord(7923) > 3) {
                cm.dispose();
                return;
            }
            if (cm.haveItem(4001130, 1) == true) {
                cm.sendSimple("아니!! 편지에 찍힌 그 인장은... 설마 그건 #b로미오의 편지#k가 아닌가요? 어째서 당신이 그걸 가지고 있죠?#d\r\n#L0#1. 당신에게 쓴 편지를 흘렸더군요. 당신의 것 입니다.");
                return;
            }
            if (cm.haveItem(4001134, 1) == true) {
                cm.sendSimple("앗, 그것은 #b알카드노의 실험 자료#k 아닌가요? 이것만 있다면 알카드노의 에너지원을 훔친 자가 알카드노가 아니라는게 증명 되겠군요! 어서 그걸 제게 주세요!#d\r\n#L1#1. 알카드노의 실험 자료를 줄리엣에게 준다.");
                return;
            }
            if (cm.haveItem(4001135, 1) == true) {
                cm.sendSimple("앗, 그것은 #b제뉴미스트의 실험 자료#k 아닌가요? 이것만 있다면 제뉴미스트의 에너지원을 훔친 자가 제뉴미스트가 아니라는게 증명 되겠군요! 어서 그걸 제게 주세요!#d\r\n#L2#1. 제뉴미스트의 실험 자료를 줄리엣에게 준다.");
                return;
            }
            cm.dispose();
            cm.sendNext("\r\n제가 문을 열고 있을테니 증거 자료를 찾아다 주세요.");
            break;
        }
        case 1: {
            switch (selection) {
                case 0: {
                    cm.dispose();
                    cm.gainItem(4001130, -1);
                    cm.sendNext("\r\n이럴 수가.. 로미오도 저와 똑같은 생각으로.. 마가티아의 전쟁을 막기 위해 이 곳에 온 거군요! 그 사람 혼자라면 위험합니다. 어서 서둘러야 해요!!");
                    cm.mapMessage(6, "줄리엣은 로미오의 편지를 보고 크게 당황하는 모습이다.");
                    break;
                }
                case 1: {
                    cm.gainItem(4001134, -1);
                    if (cm.getPlayer().getIntNoRecord(7923) > 2) {
                        cm.sendNext("\r\n제뉴미스트와 알카드노의 에너지원을 각 각 상대편이 훔친 것이 아니란게 증명 되었으니, 이제 전쟁을 막을 수 있겠군요. 감사합니다. 다음 방으로 가는 문을 제가 열어두었으니, 누가 이 음모의 주인공인지 밝혀 주세요!");
                        return;
                    }
                    cm.dispose();
                    cm.sendNext("\r\n전쟁을 막으려면 아직 제뉴미스트를 설득 할 자료가 필요합니다. 제가 문을 열고 있을테니 증거 자료를 찾아다 주세요.");
                    cm.getPlayer().updateQuest(7923, (cm.getPlayer().getIntNoRecord(7923) + 1) + "");
                    break;
                }
                case 2: {
                    cm.gainItem(4001135, -1);
                    if (cm.getPlayer().getIntNoRecord(7923) > 2) {
                        cm.sendNext("\r\n제뉴미스트와 알카드노의 에너지원을 각 각 상대편이 훔친 것이 아니란게 증명 되었으니, 이제 전쟁을 막을 수 있겠군요. 감사합니다. 다음 방으로 가는 문을 제가 열어두었으니, 누가 이 음모의 주인공인지 밝혀 주세요!");
                        return;
                    }
                    cm.dispose();
                    cm.sendNext("\r\n전쟁을 막으려면 아직 알카드노를 설득 할 자료가 필요합니다. 제가 문을 열고 있을테니 증거 자료를 찾아다 주세요.");
                    cm.getPlayer().updateQuest(7923, (cm.getPlayer().getIntNoRecord(7923) + 1) + "");
                    break;
                }
            }
            break;
        }
        case 2: {
            cm.dispose();
            cm.showFieldEffect(true, "quest/party/clear");
            cm.playSound(true, "Party1/Clear");
            cm.getPlayer().updateQuest(7923, "4");
            cm.getMap().getReactorByName("jnr3_out3").forceHitReactor(1);
            break;
        }
    }
}