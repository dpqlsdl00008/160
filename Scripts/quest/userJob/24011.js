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
            qm.sendNext("... 이상한 일이로군. 그대에게 왕의 힘이 느껴지지 않아... 수 백 년의 세월이 지났다고 해서 사라지는 힘이 아닌데... 이게 어찌 된 일이오? 당신에게 느껴지는 건 이상 할 정도로 불길한 검은 기운에 눌린 힘 뿐이니...");
            break;
        }
        case 1: {
            qm.sendNextPrevS("설명하자면 긴 이야기가 될 겁니다.", 2);
            break;
        }
        case 2: {
            qm.sendNextPrev("이미 축제는 바스라져 먼지처럼 사라지고 남은 것은 수 백 수 천 년의 시간을 물처럼 보내는 정신 뿐인 내게 긴 이야기란 즐거움이지요. 그 동안 그대의 세상에 어떤 일이 있었는지 들려주시오.");
            break;
        }
        case 3: {
            qm.sendNextPrevS("(검은 마법사의 존재와 그를 물리치기 위한 싸움, 봉인은 했지만 결국 엘프들 전체를 위험에 빠뜨려 버린 저주에 대해 천천히 설명했다... 하나도 빠짐 없이. 그 어떤 부끄러운 일이라도 숨기지 않고.)", 2);
            break;
        }
        case 4: {
            qm.sendNextPrevS("어쩌면 저는 엘프 역사에 가장 어리석은 왕으로 기록 될 지도 모르겠습니다. 저의 실력이 아니었더라면... 제가 검은 마법사와 싸우지 않았더라면 우리 엘프들이 비참한 꼴로 전략하는 일은 없었겠지요... 위대한 정신이여, 벌은 달게 받겠습니다.", 2);
            break;
        }
        case 5: {
            qm.sendNextPrev("정말 많은 일이 있었군요... 놀라울 따름입니다. 메이플 월드 전체를 뒤덮을 정도로 강력한 어둠의 힘이 존재했다니... 그리고 그 영향이 우리 엘프들에게도 미쳤다니, 슬픈 일이군요... 허나, 그대가 책임져야 하는 일은 아닙니다.");
            break;
        }
        case 6: {
            qm.sendNextPrev("왕의 선택은 엘프의 선택. 그대가 한 그 어떤 선택도 엘프 전체가 원한 것과 다르지 않습니다. 아직 싸움은 끝나지 않았고, 결정 된 것은 아무 것도 없습니다. 어리석은 선택이 아닌 최상의 선택이 되도록 만들면 되는 것...");
            break;
        }
        case 7: {
            qm.sendAcceptDecline("그대가 어째서 이런 모습으로 나를 찾아왔는지 알겠군요. 지식은 있지만 왕의 힘을 잃은 자여... 그대에게 또 다시 <왕의 시련> 을 내 줄 필요는 없겠지요. 당신은 이미 자격을 갖춘 자, 당신에게 왕의 힘을 허락합니다.");
            break;
        }
        case 8: {
            qm.dispose();
            qm.forceStartQuest();
            break;
        }
    }
}

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
            qm.sendNext("긴 엘프의 역사에 이와 같이 큰 사건은 없었습니다. 지금은 전시. 엘프들은 조용한 전쟁을 치르는 중 입니다. 엘프의 젊은 왕이여, 부디 그대의 백성들을 승리로 이끄십시오.");
            break;
        }
        case 1: {
            qm.sendNextPrev("... 바쁜 전시에 또 다시 여기까지 찾아오게 할 필요는 없겠지요. 원래라면 새로운 스킬을 위해 두 번째, 세 번째 시련을 받으러 와야겠지만 그것도 생략하도록 하지요.");
            break;
        }
        case 2: {
            qm.sendAcceptDecline("내가 직접 그대를 찾아 가겠습니다. 그대가 왕의 힘을 견딜 수 있을 정도로 원래의 능력을 되 찾으면, 그대의 능력을 깨우러 가도록 하지요. 조금... 기대되는 일이기도 하군요.");
            break;
        }
        case 3: {
            qm.dispose();
            qm.changeJob(2310);
            qm.forceCompleteQuest();
            break;
        }
    }
}