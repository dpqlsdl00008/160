function act() {
    rm.dropSingleItem(4033196);
    var value = 0;
    for (var i = 1; i < 11; i++) {
        if (rm.getMap().getReactorByName("egg" + i).getState() == 1) {
            value += 1;
        }
    }
    if (value > 9) {
        rm.getMap().getReactorByName("dog").hitReactor(rm.getClient());
        rm.spawnMonster(9001051, 229, 63);
    }
}