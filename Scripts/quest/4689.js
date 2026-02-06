?/* ===========================================================
			Resonance
	NPC Name: 		Maple Administrator
	Description: 	Quest -  Kingdom of Mushroom in Danger
=============================================================
Version 1.0 - Script Done.(17/7/2010)
=============================================================
*/

var status = -1;

function start(mode, type, selection) {
    status++;
	if (mode != 1) {
	    if(type == 1 && mode == 0)
		    status -= 2;
		else{
			if(status == 0){
				qm.sendOk("정말로 머쉬킹 왕국에 도움을 줘볼 생각이 없나? 언제든지 생각이 바뀌면 나를 찾아오게나.");
				qm.dispose();
				return;
			} else if(status == 3){
				qm.sendNext("Okay. In that case, I'll just give you the routes to the Kingdom of Mushroom. #bNear the west entrance of Henesys,#k you'll find an #bempty house#k. Enter the house, and turn left to enter#b<Themed Dungeon : Mushroom Castle>#k. That's the entrance to the Kingdom of Mushroom. There's not much time!");
				qm.forceStartQuest();
				return;
			}
		}
	}
	if(status == 0) 
		qm.sendYesNo("더이상 여기로부터 도망갈 수 없을 것 같아... \r\n어떻하지? 여기서 싸운다면 좀 더 동료가 필요할지도 몰라... 어떻게 할 거야? 싸워?");
	if(status == 1){
		qm.dispose();
		
		
                qm.warp(802000309);
	}
}

function end(mode, type, selection) {
    status++;
	if (mode != 1) {
	    if(type == 1 && mode == 0)
		    status -= 2;
		else{
		    qm.dispose();
			return;
		}
	}
	if(status == 0)
		qm.sendNext("타란튤로스를 해치운건가?");
	if(status == 1)
		qm.sendYesNo("타란튤로스를 해치우자 암벽 거인의 떨림이 잦아들고 맑은 기운이 사방에서 샘솟기 시작한다. 암벽 거인들 위기로부터 구출해냈다. 이제 당분간은 오염되지 않을 것 같다.");
	if(status == 2){
                qm.warp(240092100);
		qm.dispose();
	}
}

