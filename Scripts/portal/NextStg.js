function enter(pi) {
    if (pi.getMap().getAllMonstersThreadsafe().size() != 0) {
        var nMob = "Mikagami";
        switch (pi.getMapId()) {
            case 811000200: {
                nMob = "Renka";
                break;
            }
            case 811000300: {
                nMob = "Oura Shogun";
                break;
            }
            case 811000400: {
                nMob = "Miroku";
                break;
            }
        }
        pi.getPlayer().dropMessage(-1, "You muse defeat '" + nMob + "' to proceed to the next floor.");
        return;
    }
    pi.getPlayer().dropMessage(5, "Going to the next floor.");
    pi.warp(pi.getMapId() + 100);
}