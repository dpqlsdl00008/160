function enter(pi) {
    
pi.saveReturnLocation("MULUNG_TC");

pi.playPortalSE();
    

pi.warp(300030100, 0);
 

                if (!pi.getPlayer().getMap().containsNPC(2133000)) {
                    pi.spawnNpc(2133000, 330, 150);
                }   
return true;

}