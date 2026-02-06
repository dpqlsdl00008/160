var status = -1;

var itemID = 2439827;
var skillID = 33121009;
var skillLevel = 50;
var reqJob = 3312;
var reqSkillLevel = 35;
var cRand = 5;

function action(mode, type, selection) {
    cm.dispose();
    var v1 = true;
    if (cm.getPlayer().getJob() != reqJob) {
        v1 = false;
    }
    if (cm.getPlayer().getTotalSkillLevel(skillID) < reqSkillLevel) {
        v1 = false;
    }
    if (!v1) {
        cm.getPlayer().getMap().broadcastMessage(Packages.tools.packet.CWvsContext.useSkillBook(cm.getPlayer(), skillID, skillLevel, false, false));
        return;
    }
    cm.gainItem(itemID, -1);
    var v2 = Packages.server.Randomizer.nextInt(100);
    if (v2 > cRand) {
        cm.getPlayer().getMap().broadcastMessage(Packages.tools.packet.CWvsContext.useSkillBook(cm.getPlayer(), skillID, skillLevel, true, false));
        return;
    }
    cm.teachSkill(skillID, cm.getPlayer().getTotalSkillLevel(skillID), skillLevel);
    cm.getPlayer().getMap().broadcastMessage(Packages.tools.packet.CWvsContext.useSkillBook(cm.getPlayer(), skillID, skillLevel, true, true));
    Packages.handling.world.World.Broadcast.broadcastMessage(Packages.tools.packet.CWvsContext.serverNotice(6, itemID, "축하드립니다. " + cm.getPlayer().getName() + "님께서 " + "[{" + Packages.server.MapleItemInformationProvider.getInstance().getItemInformation(itemID).name + "}]" + " 사용에 성공하였습니다.", false));
}