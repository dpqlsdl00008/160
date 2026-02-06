var status = -1;

function end(mode, type, selection) {
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
            qm.sendNext("오, 드디어 왔군. 베타 박사에게 듣자니 그 책을 찾아 빅토리아 아일랜드를 빙빙 돈 모양이더군. 무척 늦게 반납 받고 있지만 자네가 고생한 것도 있고 하니, 그 부분은 넘어가도록 하지. 그래, 책에서 원하는 지식은 얻었나? 뭘 알고 싶었던건가?");
            break;
        }
        case 1: {
            qm.sendNextPrevS("#b오닉스 드래곤에 대해 조금...#k", 2);
            break;
        }
        case 2: {
            qm.sendYesNo("오닉스 드래곤? 나도 그 책을 읽어 보아서 알고 있지만, 오닉스 드래곤이라면 이미 멸종한 용 아닌가? 자네는 그들에 대한 연구를 하는 겐가? 참 어려운 학문의 길을 가는 사람을 보게 되었군. 좋아, 내 최선을 다해 자네의 연구를 도와주지.\r\n\r\n#fUI/UIWindow.img/QuestIcon/8/0# 2000 exp");
            break;
        }
        case 3: {
            qm.sendNext("마법 도서관에 드래곤에 대한 책은 많지만 오닉스 드래곤에 대한 책은 더 이상 없다네. 혹시 마법 도서관에 드래곤에 대한 다른 책이 들어오거든 연락 주겠네.");
            break;
        }
        case 4: {
            qm.sendNextPrev("아, 그리고 내 친구 중에 리프레에서 용을 돌보는 종족, #b하플링의 촌장 타타모#k라는 자가 있다네. 그에게 오닉스 드래곤에 대해 아는 게 있는지 물어 보고 답이 오면 자네에게도 알려주지.");
            break;
        }
        case 5: {
            qm.sendNextPrev("오닉스 드래곤이라... 오닉스 드래곤은 검고 투명한 비늘에 금색 뿔을 가지고 있다던데 자네 도마뱀은 뿔은 금색이지만 비늘은 그리 검지 않군... 흠...");
            break;
        }
        case 6: {
            qm.sendNextPrevS("#b(미르가 드래곤이라는 게 밝혀지면 퇴치할지도 모른다!) 이 애는 드래곤이 아니라 도마뱀이에요!#k", 2);
            break;
        }
        case 7: {
            qm.sendNextPrev("어이쿠 귀청이야. 알았네. 누가 뭐랬나? 그렇겠지. 도마뱀이겠지... 흠. 오닉스 드래곤이라...");
            break;
        }
        case 8: {
            qm.dispose();
            qm.gainExp(2000);
            qm.forceCompleteQuest();
            break;
        }
    }
}