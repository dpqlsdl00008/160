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
            var say = "도와주셔서 감사합니다. 저 아래 쪽 동굴에 뭔가 있는 것 같은데, 혼자 조사하기에는 너무 무서워서... 저를 도와주시겠어요?";
            var exitMsg = "이곳에서 나가고 싶습니다.";
            if (cm.getMapId() == 923040400) {
                say = "도와주셔서 감사합니다. 덕분에 저 아래 동굴까지 조사를 끝냈어요. 혹시 #b피아누스의 비늘#k을 찾으시면 #r위험한 바다 갈림길 <대기실> 의 돌고래#k에게 가져다 주세요. 저의 연구에 도움이 될 것 같아요. 100개 이상 구해주시면, 답례로 #i1022175# #b#z1022175##k을 드릴께요. 돌고래가 걱정하겠네요. 저도 얼른 돌아가야겠어요.";
                exitMsg = "저도 이만 나가겠습니다";
            }
            say += "\r\n#L1##d1. " + exitMsg;
            cm.sendSimple(say);
            break;
        }
        case 1: {
            if (cm.getMapId() != 923040400) {
                cm.dispose();
                cm.warp(923040000, 0);
                return;
            }
            cm.sendNext("\r\n정말 감사했어요. 혹시 다음에도 제가 위기에 빠지면 도와주실꺼죠? 그럼 안녕히 가세요~");
            break;
        }
        case 2: {
            cm.dispose();
            var v1 = 1;
            if (cm.getParty() != null) {
                v1 = cm.getParty().getMembers().size();
            }
            //Packages.handling.channel.handler.DueyHandler.addNewItemToDb(4001535, 1, cm.getPlayer().getId(), "파티 퀘스트", "", true);
            Packages.handling.channel.handler.DueyHandler.addNewItemToDb(4310050, 1, cm.getPlayer().getId(), "파티 퀘스트", "", true);
            cm.getClient().sendPacket(Packages.tools.packet.CField.receiveParcel("위험에 빠진 켄타", true));
            cm.gainExp(2999999);
            cm.warp(923040000, 0);
            break;
        }
    }
}