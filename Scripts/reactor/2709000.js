/*
	Reactor: 		PinkBeenPower
	Map(s): 		Twilight of the gods
	Description:		Summons Pink Bean
*/

function act() {
    rm.killAllMob();
    rm.spawnMonster(8820008);
    if (!rm.getPlayer().isGM()) {
        var map = rm.getMap();
        if (typeof map.startSpeedRun === "function") {
            map.startSpeedRun();
        }
    }
}