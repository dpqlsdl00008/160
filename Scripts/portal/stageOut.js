/*
 * 퓨어온라인 소스 스크립트 입니다.
 * 
 * 포탈위치 : 
 * 포탈설명 : 
 * 
 * 제작 : 주크블랙
 * 
 */
importPackage(Packages.tools.packet);
importPackage(Packages.client);
importPackage(Packages.constants);
importPackage(Packages.handling.world);

function enter(pi) {
	if(pi.getPlayer().getMapId() == 992002000) {
	pi.getPlayer().getMap().broadcastMessage(CField.showEffect("Gstar/clearS"));
	pi.getPlayer().getMap().broadcastMessage(CWvsContext.getTopMsg("3초 뒤 로비로 이동됩니다."));
	pi.TheSeedClear();
        GameConstants.setTheSeed2(GameConstants.getTheSeedTower(pi.getPlayer().getMapId()), GameConstants.getTheSeedMap((GameConstants.getTheSeedTower(pi.getPlayer().getMapId()) + 1)), pi.getPlayer());
        World.Broadcast.broadcastGMMessage(CWvsContext.broadcastMsg(6, "[더 시드] " + pi.getPlayer().getParty().getLeader().getName() + "님의 파티가 더 시드 2층을 공략 했습니다."));
        }        
        if(pi.getPlayer().getMapId() == 992005000) {
	pi.getPlayer().getMap().broadcastMessage(CField.showEffect("Gstar/clearS"));
	pi.getPlayer().getMap().broadcastMessage(CWvsContext.getTopMsg("3초 뒤 로비로 이동됩니다."));
	pi.TheSeedClear();
        GameConstants.setTheSeed2(GameConstants.getTheSeedTower(pi.getPlayer().getMapId()), GameConstants.getTheSeedMap((GameConstants.getTheSeedTower(pi.getPlayer().getMapId()) + 1)), pi.getPlayer());
        World.Broadcast.broadcastGMMessage(CWvsContext.broadcastMsg(6, "[더 시드] " + pi.getPlayer().getParty().getLeader().getName() + "님의 파티가 더 시드 5층을 공략 했습니다."));
       }
}