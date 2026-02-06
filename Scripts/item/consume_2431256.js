var status = -1;

var itemID = [
3010756, // Fat Lucky Cat
3010762, // Peppermint Throne
3010763, // Christmas Phantom
3010764, // Polar Bear Chair
3010765, // Ice Fishing
3010768, // Furnace Chair
3010784, // Critter Champs Chair
3010785, // Triple Volt Chair
3010786, // Stork Swing
3010807, // Shamrock Chair
3010808, // Reality TV Chair
3010809, // Recharger Chair
3010816, // Forest Sanctuary Chair
3010817, // Rabbit Sofa
3010819, // Bunny Tornado Chair
3010821, // My Childhood Memories
3010833, // I Love MapleStory Chair
3010834, // Maple Crystal Chair
3010840, // Super Giant Mushroom Chair
3010841, // Marry Me Chair
3010858, // Elluel Concerto
3010881, // Pearl Shell Chair
3010882, // Blue Beach Chair
3010884, // Black Bean Chair
3010886, // Black Wings Homebase
3010887, // Campfire Chair
3010891, // Von Leon Chair
3010892, // Xerxes Chair
3010893, // Hilla's Style Maker
3010925, // Ancient Tank Chair
3010931, // Fun Cloud Chair
3010933, // Royal Azalea Chair
3010934, // Mosquito Repellent Pig Chair
3010945, // Thorny Chair
3010953, // Bookshelf Chair
3010966, // Ghost Ship Chair
3010970, // Demonic Birthday
3010972, // Cheetos Chair
3010973, // Cadeira de Morceguinho
3010974, // Jawdestroyer Chair
3010981, // Forest Sanctuary Chair
3010982, // Arboren Chair
3010983, // Down Home Family Chair
3010985, // The Miwok Experience
3010986, // Friend of the Miwoks
3010987, // The Old and the Wise Chair
3013005, // Under the Sakura Tree
3013008, // 104th Cadet Corps [Group Chair]
];

function action(mode, type, selection) {
    if (mode == 1) {
        status++;
    } else {
        cm.dispose();
        return;
    }
    switch (status) {
        case 0: {
            cm.dispose();
            if (cm.getInventory(3).getNumFreeSlot() < 1) {
                cm.getPlayer().getClient().sendPacket(Packages.tools.packet.CWvsContext.crossHunterQuestResult(2, 0));
                return;
            }
            cm.gainItem(2431256, -1);
            var cRand = Math.floor(Math.random() * itemID.length);
            cm.gainItem(itemID[cRand], 1);
            break;
        }
    }
}