function act() {
    var jgys = java.util.Calendar.getInstance().get(java.util.Calendar.DAY_OF_WEEK);
    rm.changeMusic(jgys == 1 ? "Bgm08/ForTheGlory" : jgys == 2 ? "Bgm11/Aquarium" : jgys == 3 ? "Bgm06/WelcomeToTheHell" : jgys == 4 ? "Bgm06/FantasticThinking" : jgys == 5 ? "Bgm02/EvilEyes" : jgys == 6 ? "Bgm10/TheWayGrotesque" : "Bgm01/MoonlightShadow");
    if (rm.getEventManager("PQ_Orbis") != null) {
	rm.getEventManager("PQ_Orbis").setProperty("1stage", "1");
    }
    rm.getPlayer().getMap().broadcastMessage(Packages.tools.packet.CField.showEffect("quest/party/clear"));
    rm.getPlayer().getMap().broadcastMessage(Packages.tools.packet.CField.playSound("Party1/Clear"));
    rm.getReactor().forceHitReactor(1);
}