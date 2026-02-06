/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package client;

import handling.factory.PotentialStampHandler;
import client.inventory.Item;
import client.inventory.MaplePet;
import client.inventory.PetDataFactory;
import handling.RecvPacketOpcode;

import static handling.RecvPacketOpcode.*;
import handling.SendPacketOpcode;

import handling.cashshop.handler.CashShopOperation;
import handling.channel.handler.AllianceHandler;
import handling.channel.handler.BBSHandler;
import handling.channel.handler.BuddyListHandler;
import handling.channel.handler.ChatHandler;
import handling.channel.handler.DueyHandler;
import handling.channel.handler.FamilyHandler;
import handling.channel.handler.GuildHandler;
import handling.channel.handler.HiredMerchantHandler;
import handling.channel.handler.InterServerHandler;
import handling.channel.handler.InventoryHandler;
import handling.channel.handler.ItemMakerHandler;
import handling.channel.handler.MobHandler;
import handling.channel.handler.MCarnivalHandler;
import handling.channel.handler.NPCHandler;
import handling.channel.handler.PartyHandler;
import handling.channel.handler.PetHandler;
import handling.channel.handler.PlayerHandler;
import handling.channel.handler.PlayerInteractionHandler;
import handling.channel.handler.PlayersHandler;
import handling.channel.handler.StatsHandling;
import handling.channel.handler.SummonHandler;
import handling.channel.handler.UserInterfaceHandler;
import handling.login.handler.CharLoginHandler;
import server.CashItemFactory;
import server.CashItemInfo;
import tools.FileoutputUtil;
import tools.data.LittleEndianAccessor;
import tools.packet.CLogin;

public class InHeader {

    public static final void handlePacket(MapleClient c, boolean cs, final short header_num, LittleEndianAccessor slea) throws Exception {
        RecvPacketOpcode header = RecvPacketOpcode.RSA_KEY;
        for (RecvPacketOpcode rev : RecvPacketOpcode.values()) {
            if (rev.getValue() == header_num) {
                header = rev;
                break;
            }
        }
        switch (header) { //아 아니다 너 95팩 아무거나있나?65나 
            case PONG:
                c.pongReceived();
                break;
            case CLIENT_HELLO:
                //System.out.println(c.getSessionIPAddress() + " Connected!");
                break;
            case LOGIN_PASSWORD:
                CharLoginHandler.login(slea, c);
                break;
            case CHARLIST_REQUEST:
                CharLoginHandler.CharlistRequest(slea, c);
                break;
            case CHECK_CHAR_NAME:
                CharLoginHandler.CheckCharName(slea.readMapleAsciiString(), c);
                break;
            case CREATE_CHAR:
            case CREATE_SPECIAL_CHAR:
                CharLoginHandler.CreateChar(slea, c);
                break;
            case CREATE_ULTIMATE:
                CharLoginHandler.CreateUltimate(slea, c);
                break;
            case DELETE_CHAR:
                CharLoginHandler.DeleteChar(slea, c);
                break;
            case CHAR_RENAME:
                CharLoginHandler.CharnameChangeCheck(slea, c);
                break;
            case CHECK_SECOND_PASSWORD:
                CharLoginHandler.checkSecondPassword(slea, c);
                break;
            case CHANGE_SECOND_PASSWORD:
                CharLoginHandler.Change_SecondPassword(slea, c);
                break;
            case REGISTER_SECOND_PASSWORD:
                CharLoginHandler.Character_RegisterSecondPassword(slea, c);
                break;
            case CHAR_SELECT_WITH_PASSWORD:
                CharLoginHandler.Character_WithSecondPassword(slea, c, false);
                break;
            case PART_TIME_JOB:
                CharLoginHandler.PartJob(slea, c);
                break;
            case CHARACTER_CARDS:
                CharLoginHandler.updateCCards(slea, c);
                break;
            case ENABLE_SPECIAL_CREATION:
                c.getSession().write(CLogin.enableSpecialCreation(c.getAccID(), true));
                break;
            case CHANGE_CHANNEL:
            case CHANGE_ROOM_CHANNEL:
                InterServerHandler.ChangeChannel(slea, c, c.getPlayer(), header == RecvPacketOpcode.CHANGE_ROOM_CHANNEL);
                break;
            case PLAYER_LOGGEDIN:
                final int playerid = slea.readInt();
                if (cs) {
                    CashShopOperation.EnterCS(playerid, c);
                } else {
                    InterServerHandler.Loggedin(playerid, c);
                }
                break;
            case ENTER_PVP: //3D 00 B7 EF 5C 00
            case ENTER_PVP_PARTY:
                PlayersHandler.EnterPVP(slea, c);
                break;
            case PVP_RESPAWN:
                PlayersHandler.RespawnPVP(slea, c);
                break;
            case LEAVE_PVP: // 나가기
                PlayersHandler.LeavePVP(slea, c);
                break;
            case PVP_ATTACK:
                PlayersHandler.AttackPVP(slea, c);
                break;
            case PVP_SUMMON:
                SummonHandler.SummonPVP(slea, c);
                break;
            case ENTER_CASH_SHOP:
                InterServerHandler.EnterCS(c, c.getPlayer(), false);
                break;
            case MOVE_PLAYER:
                /*  
                c.getChannelServer().addConnectedMacs(c.getIp());
              //  if (!c.getMacString().equals("")) {
                    if (c.getChannelServer().containsConnectedMacs(c.getIp())) {
                        c.getSession().write(CWvsContext.serverNotice(1, "You are already connected to the server on this computer."));
                        return;
                    } else {
                        System.out.print("값이 추가전");
                        c.getChannelServer().addConnectedMacs(c.getIp());
                        System.out.print("값이 추가됨");
                    }
             //   }*/
                PlayerHandler.MovePlayer(slea, c, c.getPlayer());
                break;
            case CHAR_INFO_REQUEST:
                c.getPlayer().updateTick(slea.readInt());
                PlayerHandler.CharInfoRequest(slea.readInt(), c, c.getPlayer());
                break;
            case CLOSE_RANGE_ATTACK:
                PlayerHandler.tryDoingMeleeAttack(slea, c, c.getPlayer(), false);
                break;
            case RANGED_ATTACK:
                PlayerHandler.tryDoingShootAttack(slea, c, c.getPlayer());
                break;
            case MAGIC_ATTACK:
                PlayerHandler.tryDoingMagicAttack(slea, c, c.getPlayer());
                break;
            case SPECIAL_MOVE:
                PlayerHandler.SpecialMove(slea, c, c.getPlayer());
                break;
            case PASSIVE_ENERGY:
                PlayerHandler.tryDoingMeleeAttack(slea, c, c.getPlayer(), true);
                break;
            case GET_BOOK_INFO:
                PlayersHandler.MonsterBookInfoRequest(slea, c, c.getPlayer());
                break;
            case MONSTER_BOOK_DROPS:
                PlayersHandler.MonsterBookDropsRequest(slea, c, c.getPlayer());
                break;
            case CHANGE_SET:
                PlayersHandler.ChangeSet(slea, c, c.getPlayer());
                break;
            case PROFESSIONAL_INFO:
                ItemMakerHandler.handleUserRequestInstanceTable(slea, c);
                break;
            case CRAFT_DONE:
                ItemMakerHandler.CraftComplete(slea, c, c.getPlayer());
                break;
            case CRAFT_MAKE:
                ItemMakerHandler.CraftMake(slea, c, c.getPlayer());
                break;
            case handleDodgeSkillReady:
                PlayerHandler.handleDodgeSkillReady(slea, c, c.getPlayer());
                break;
            case CRAFT_EFFECT:
                ItemMakerHandler.CraftEffect(slea, c, c.getPlayer());
                break;
            case START_HARVEST:
                ItemMakerHandler.StartHarvest(slea, c, c.getPlayer());
                break;
            case STOP_HARVEST:
                ItemMakerHandler.StopHarvest(slea, c, c.getPlayer());
                break;
            case MAKE_EXTRACTOR:
                ItemMakerHandler.MakeExtractor(slea, c, c.getPlayer());
                break;
            case LEAVE_EXTRACTOR:
                ItemMakerHandler.LeaveExtractor(slea, c, c.getPlayer());
                break;
            case USE_BAG:
                ItemMakerHandler.UseBag(slea, c, c.getPlayer());
                break;
            case USE_RECIPE:
                ItemMakerHandler.UseRecipe(slea, c, c.getPlayer());
                break;
            case STAR_FALL:
                PlayerHandler.StarFall(slea, c, c.getPlayer());
                break;
            case MOVE_ANDROID:
                PlayerHandler.MoveAndroid(slea, c, c.getPlayer());
                break;
            case FACE_EXPRESSION:
                //PlayerHandler.ChangeEmotion(slea.readInt(), c.getPlayer());
                break;
            case FACE_ANDROID:
                PlayerHandler.ChangeAndroidEmotion(slea.readInt(), c.getPlayer());
                break;
            case TAKE_DAMAGE:
                PlayerHandler.TakeDamage(slea, c, c.getPlayer());
                break;
            case HEAL_OVER_TIME:
                PlayerHandler.Heal(slea, c.getPlayer());
                break;
            case CANCEL_BUFF:
                PlayerHandler.CancelBuffHandler(slea.readInt(), c.getPlayer());
                break;
            case MECH_CANCEL:
                PlayerHandler.CancelMech(slea, c.getPlayer());
                break;
            case CANCEL_ITEM_EFFECT:
                PlayerHandler.CancelItemEffect(slea.readInt(), c.getPlayer());
                break;
            case USE_TITLE:
                PlayerHandler.UseTitle(slea, slea.readInt(), c, c.getPlayer());//블링블링몽키
                break;
            case USE_CHAIR:
                PlayerHandler.UseChair(slea.readInt(), c, c.getPlayer());
                break;
            case CANCEL_CHAIR:
                PlayerHandler.CancelChair(slea.readShort(), c, c.getPlayer());
                break;
            case WHEEL_OF_FORTUNE:
                PlayerHandler.WheelOfFortuneEffect(slea.readInt(), c.getPlayer());
                break; //whatever
            case HandleShowItemEffect:
            case HandleBlingblingMonkey:
                PlayerHandler.HandleShowItemEffect(slea.readInt(), c, c.getPlayer());
                break;
            case SKILL_EFFECT:
                PlayerHandler.HandleSkillPrepare(slea, c.getPlayer());
                break;
            case QUICK_SLOT:
                PlayerHandler.QuickSlot(slea, c.getPlayer());
                break;
            case MESO_DROP:
                c.getPlayer().updateTick(slea.readInt());
                PlayerHandler.DropMeso(slea.readInt(), c.getPlayer());
                break;
            case CHANGE_KEYMAP:
                PlayerHandler.ChangeKeymap(slea, c.getPlayer());
                break;
            case UPDATE_ENV:
                // We handle this in MapleMap
                break;
            case CHANGE_MAP:
                if (cs) {
                    CashShopOperation.LeaveCS(slea, c, c.getPlayer());
                } else {
                    PlayerHandler.ChangeMap(slea, c, c.getPlayer());
                }
                break;
            case CHANGE_MAP_SPECIAL:
                slea.skip(1);
                PlayerHandler.ChangeMapSpecial(slea.readMapleAsciiString(), c, c.getPlayer());
                break;
            case USE_INNER_PORTAL:
                slea.skip(1);
                PlayerHandler.InnerPortal(slea, c, c.getPlayer());
                break;
            case TROCK_ADD_MAP:
                PlayerHandler.TrockAddMap(slea, c, c.getPlayer());
                break;
            case LIE_DETECTOR:
            case LIE_DETECTOR_SKILL:
                PlayersHandler.LieDetector(slea, c, c.getPlayer(), header == RecvPacketOpcode.LIE_DETECTOR);
                break;
            case LIE_DETECTOR_RESPONSE:
                PlayersHandler.LieDetectorResponse(slea, c, c.getPlayer());
                break;
            case LIE_DETECTOR_RESET:
                PlayersHandler.LieDetectorReset(c, c.getPlayer());
                break;
            case ARAN_COMBO:
                PlayerHandler.AranCombo(c, c.getPlayer(), 1);
                break;
            case BLESS_OF_DARKNES:
                PlayerHandler.BlessOfDarkness(c.getPlayer());
                break;
            case SKILL_MACRO:
                PlayerHandler.ChangeSkillMacro(slea, c.getPlayer());
                break;
            case GIVE_FAME:
                PlayersHandler.GiveFame(slea, c, c.getPlayer());
                break;
            case TRANSFORM_PLAYER:
                PlayersHandler.TransformPlayer(slea, c, c.getPlayer());
                break;
            case NOTE_ACTION:
                PlayersHandler.Note(slea, c.getPlayer());
                break;
            case USE_DOOR:
                PlayersHandler.UseDoor(slea, c.getPlayer());
                break;
            case USE_MECH_DOOR:
                PlayersHandler.UseMechDoor(slea, c.getPlayer());
                break;
            case DAMAGE_REACTOR:
                PlayersHandler.HitReactor(slea, c);
                break;
            case CLICK_REACTOR:
            case TOUCH_REACTOR:
                PlayersHandler.TouchReactor(slea, c);
                break;
            case HandleNettPyramidMob:
                PlayersHandler.HandleNettPyramidMob(slea, c);
                break;
            case HandleNettPyramidSkill:
                PlayersHandler.HandleNettPyramidSkill(slea, c);
                break;
            case CLOSE_CHALKBOARD:
                c.getPlayer().setChalkboard(null);
                break;
            case ITEM_SORT:
                InventoryHandler.ItemSort(slea, c);
                break;
            case ITEM_GATHER:
                InventoryHandler.ItemGather(slea, c);
                break;
            case ITEM_MOVE:
                InventoryHandler.ItemMove(slea, c);
                break;
            case MOVE_BAG:
                InventoryHandler.MoveBag(slea, c);
                break;
            case SWITCH_BAG:
                InventoryHandler.SwitchBag(slea, c);
                break;
            case ITEM_MAKER:
                ItemMakerHandler.ItemMaker(slea, c);
                break;
            case USER_SELECT_PQ_REWARD:
                InventoryHandler.handleSeletPQReward(slea, c);
                break;
            case USER_REQUEST_PQ_REWARD:
                InventoryHandler.handleRecivePQReward(c);
                break;
            case ITEM_PICKUP:
                InventoryHandler.Pickup_Player(slea, c, c.getPlayer());
                break;
            case USE_CASH_ITEM:
                InventoryHandler.UseCashItem(slea, c);
                break;
            case USE_ITEM:
                InventoryHandler.UseItem(slea, c, c.getPlayer());
                break;
            case USE_COSMETIC:
                InventoryHandler.UseCosmetic(slea, c, c.getPlayer());
                break;
            case USE_MAGNIFY_GLASS:
                InventoryHandler.UseMagnify(slea, c);
                break;
            case USE_SCRIPTED_NPC_ITEM:
                InventoryHandler.UseScriptedNPCItem(slea, c, c.getPlayer());
                break;
            case USE_RETURN_SCROLL:
                InventoryHandler.UseReturnScroll(slea, c, c.getPlayer());
                break;
            case GOLDEN_HAMMER:
                slea.skip(4);
                InventoryHandler.UseGoldHammer(slea, c);
                break;
            case HAMMER_EFFECT:
                InventoryHandler.HammerEffect(slea, c);
                break;
            case USE_UPGRADE_SCROLL:
            case USE_POTENTIAL_SCROLL:
            case USE_EQUIP_SCROLL:
                //c.getPlayer().updateTick(slea.readInt());
                slea.skip(4);
                InventoryHandler.UseUpgradeScroll(slea.readShort(), slea.readShort(), (short) 0, c, c.getPlayer(), slea.readByte() > 0);
                break;
            case UseSoulEnchant:
                InventoryHandler.handleUseSoulEnchant(slea, c);
                break;
            case USE_EDITIONAL_STAMP:
                PotentialStampHandler.handleUserCashPetPickUpOnOffRequest(slea, c);
                break;
            case USE_EDITIONAL_SCROLL:
                PotentialStampHandler.EditionalScroll(slea, c);
                break;
            case USE_POTENTIAL_STAMP:
                PotentialStampHandler.UsePotentialStamp(slea, c);
                break;
            case USE_SUMMON_BAG:
                InventoryHandler.UseSummonBag(slea, c, c.getPlayer());
                break;
            case USE_TREASURE_CHEST:
                InventoryHandler.UseTreasureChest(slea, c, c.getPlayer());
                break;
            case USE_SKILL_BOOK:
                c.getPlayer().updateTick(slea.readInt());
                InventoryHandler.UseSkillBook((byte) slea.readShort(), slea.readInt(), c, c.getPlayer());
                break;
            case USE_CATCH_ITEM:
                InventoryHandler.UseCatchItem(slea, c, c.getPlayer());
                break;
            case USE_MOUNT_FOOD:
                InventoryHandler.UseMountFood(slea, c, c.getPlayer());
                break;
            case REWARD_ITEM:
                InventoryHandler.UseRewardItem((byte) slea.readShort(), slea.readInt(), c, c.getPlayer());
                break;
            case START_ROULETTE:
                InventoryHandler.roulette_start(slea.readInt(), c);
                break;
            case STOP_ROULETTE:
                InventoryHandler.roulette_stop(c);
                break;
            case HYPNOTIZE_DMG:
                MobHandler.HypnotizeDmg(slea, c.getPlayer());
                break;
            case HandleLapidificationState:
                MobHandler.handleLapidificationState(slea, c.getPlayer());
                break;
            case HandleMobSkillDelay:
                MobHandler.handleMobSkillDelay(slea, c.getPlayer());
                break;
            case HandleCygnusMist:
                MobHandler.handleCygnusMist(slea, c.getPlayer());
                break;
            case MOB_NODE:
                MobHandler.handleMobEscortCollision(slea, c.getPlayer());
                break;
            case DISPLAY_NODE:
                MobHandler.DisplayNode(slea, c.getPlayer());
                break;
            case MOVE_LIFE:
                MobHandler.handleMobMove(slea, c, c.getPlayer());
                break;
            case AUTO_AGGRO:
                MobHandler.AutoAggro(slea.readInt(), c.getPlayer());
                break;
            case FRIENDLY_DAMAGE:
                MobHandler.FriendlyDamage(slea, c.getPlayer());
                break;
            case REISSUE_MEDAL:
                PlayerHandler.ReIssueMedal(slea, c, c.getPlayer());
                //System.out.println("Reissuemedal: (" + header.toString() + " . " + slea.toString());
                break;
            case MONSTER_BOMB:
                MobHandler.handleMobSelfDestruct(slea.readInt(), c.getPlayer());
                break;
            case MOB_BOMB:
                MobHandler.MobBomb(slea, c.getPlayer());
                break;
            case NPC_SHOP:
                NPCHandler.NPCShop(slea, c, c.getPlayer());
                break;
            case NPC_TALK:
                NPCHandler.NPCTalk(slea, c, c.getPlayer());
                break;
            case NPC_TALK_MORE:
                NPCHandler.NPCMoreTalk(slea, c);
                break;
            case HIRED_REMOTE:
                InventoryHandler.useRemoteHiredMerchant(slea, c);
                break;
            case NPC_ACTION:
                NPCHandler.NPCAnimation(slea, c);
                break;
            case QUEST_ACTION:
                NPCHandler.QuestAction(slea, c, c.getPlayer());
                break;
            case STORAGE:
                NPCHandler.Storage(slea, c, c.getPlayer());
                break;
            case GENERAL_CHAT:
                if (c.getPlayer() != null && c.getPlayer().getMap() != null) {
                    c.getPlayer().updateTick(slea.readInt());
                    ChatHandler.GeneralChat(slea.readMapleAsciiString(), slea.readByte(), c, c.getPlayer());
                }
                break;
            case PARTYCHAT:
                ChatHandler.Others(slea, c, c.getPlayer());
                break;
            case WHISPER:
                ChatHandler.Whisper_Find(slea, c);
                break;
            case MESSENGER:
                ChatHandler.Messenger(slea, c);
                break;
            case AUTO_ASSIGN_AP:
                StatsHandling.AutoAssignAP(slea, c, c.getPlayer());
                break;
            case DISTRIBUTE_AP:
                StatsHandling.DistributeAP(slea, c, c.getPlayer());
                break;
            case DISTRIBUTE_SP:
                c.getPlayer().updateTick(slea.readInt());
                StatsHandling.DistributeSP(slea.readInt(), c, c.getPlayer());
                break;
            case DISTRIBUTE_HYPER_SP:
                c.getPlayer().updateTick(slea.readInt());
                StatsHandling.DistributeHyperSP(slea.readInt(), c, c.getPlayer());
                break;
            case PLAYER_INTERACTION:
                PlayerInteractionHandler.PlayerInteraction(slea, c, c.getPlayer());
                break;
            case GUILD_OPERATION:
                GuildHandler.Guild(slea, c);
                break;
            case DENY_GUILD_REQUEST:
                slea.skip(1);
                GuildHandler.DenyGuildRequest(slea.readMapleAsciiString(), c);
                break;
            case ALLIANCE_OPERATION:
                AllianceHandler.HandleAlliance(slea, c, false);
                break;
            case DENY_ALLIANCE_REQUEST:
                AllianceHandler.HandleAlliance(slea, c, true);
                break;
            case PUBLIC_NPC:
                NPCHandler.handleMakeEnterFieldPacketForQuickMove(slea, c);
                break;
            case BBS_OPERATION:
                BBSHandler.BBSOperation(slea, c);
                break;
            case PARTY_OPERATION:
                PartyHandler.PartyOperation(slea, c);
                break;
            case DENY_PARTY_REQUEST:
                PartyHandler.DenyPartyRequest(slea, c);
                break;
            case ALLOW_PARTY_INVITE:
                PartyHandler.AllowPartyInvite(slea, c);
                break;
            case BUDDYLIST_MODIFY:
                BuddyListHandler.BuddyOperation(slea, c);
                break;
            case CYGNUS_SUMMON:
                UserInterfaceHandler.CygnusSummon_NPCRequest(c);
                break;
            case SHIP_OBJECT:
                UserInterfaceHandler.ShipObjectRequest(slea.readInt(), c);
                break;
            case BUY_CS_ITEM:
            case GIFT_CS_ITEM:
                CashShopOperation.BuyCashItem(slea, c, c.getPlayer());
                break;
            case COUPON_CODE:
                //FileoutputUtil.log(FileoutputUtil.PacketEx_Log, "Coupon : \n" + slea.toString(true));
                //System.out.println("Coupon Code: " + slea.toString());
                slea.skip(2);
                String nxCoupon = slea.readMapleAsciiString();
                CashShopOperation.CouponCode(nxCoupon, c);
                //c.getSession().write(MTSCSPacket.showOneADayInfo(true, 10003055));
                CashShopOperation.doCSPackets(c);
                break;
            case TWIN_DRAGON_EGG:
                //System.out.println("TWIN_DRAGON_EGG: " + slea.toString());
                final CashItemInfo item = CashItemFactory.getInstance().getItem(10003055);
                Item itemz = c.getPlayer().getCashInventory().toItem(item);
                //PAUL c.getSession().write(MTSCSPacket.sendTwinDragonEgg(true, true, 38, itemz, 1));
                break;
            case XMAS_SURPRISE:
                //System.out.println("XMAS_SURPRISE: " + slea.toString());
                break;
            case CS_UPDATE:
                CashShopOperation.CSUpdate(c);
                break;
            case ITEM_POT_USE:
                ItemMakerHandler.handleUseItemPot(slea, c);
                break;
            case ITEM_POT_CLEAR:
                ItemMakerHandler.handleClearItemPot(slea, c);
                break;
            case ITEM_POT_FEED:
                ItemMakerHandler.handleFeedItemPot(slea, c);
                break;
            case ITEM_POT_CURE:
                ItemMakerHandler.handleCureItemPot(slea, c);
                break;
            case ITEM_POT_REWARD:
                ItemMakerHandler.handleRewardItemPot(slea, c);
                break;
            case DAMAGE_SUMMON:
                slea.skip(4);
                SummonHandler.DamageSummon(slea, c.getPlayer());
                break;
            case MOVE_SUMMON:
                SummonHandler.MoveSummon(slea, c.getPlayer());
                break;
            case SUMMON_ATTACK:
                SummonHandler.SummonAttack(slea, c, c.getPlayer());
                break;
            case MOVE_DRAGON:
                SummonHandler.MoveDragon(slea, c.getPlayer());
                break;
            case SUB_SUMMON:
                SummonHandler.SubSummon(slea, c.getPlayer());
                break;
            case REMOVE_SUMMON:
                SummonHandler.RemoveSummon(slea, c);
                break;
            case SPAWN_PET:
                PetHandler.SpawnPet(slea, c.getPlayer());
                break;
            case PET_AUTO_BUFF:
                PetHandler.Pet_AutoBuff(slea, c, c.getPlayer());
                break;
            case MOVE_PET:
                PetHandler.MovePet(slea, c.getPlayer());
                break;
            case PET_CHAT:
                //System.out.println("Pet chat: " + slea.toString());
                if (slea.available() < 12) {
                    break;
                }
                final int petid = slea.readInt();
                c.getPlayer().updateTick(slea.readInt());
                PetHandler.PetChat(petid, slea.readShort(), slea.readMapleAsciiString(), c.getPlayer());
                break;
            case PET_COMMAND:
                MaplePet pet;
                pet = c.getPlayer().getPet(c.getPlayer().getPetIndex((int) slea.readLong()));
                slea.readByte(); // always 0?
                if (pet == null) {
                    return;
                }
                PetHandler.PetCommand(pet, PetDataFactory.getPetCommand(pet.getPetItemId(), slea.readByte()), c, c.getPlayer());
                break;
            case PET_FOOD:

                //System.out.println("PET FOOD: " + slea.toString());
                PetHandler.PetFood(slea, c, c.getPlayer());
                break;
            case PET_LOOT:
                //System.out.println("PET_LOOT ACCESSED");
                InventoryHandler.Pickup_Pet(slea, c, c.getPlayer());
                break;
            case PET_AUTO_POT:
                PetHandler.Pet_AutoPotion(slea, c, c.getPlayer());
                break;
            case USE_CP:
                MCarnivalHandler.useCP(slea, c);
                break;
            case DUEY_ACTION:
                DueyHandler.DueyOperation(slea, c);
                break;
            case USE_HIRED_MERCHANT:
                HiredMerchantHandler.UseHiredMerchant(c, true);
                break;
            case MERCH_ITEM_STORE:
                HiredMerchantHandler.MerchantItemStore(slea, c);
                break;
            case CANCEL_DEBUFF:
                // Ignore for now
                break;
            case MAPLETV:
                break;
            case LEFT_KNOCK_BACK:
                PlayerHandler.leftKnockBack(slea, c);
                break;
            case SNOWBALL:
                PlayerHandler.snowBall(slea, c);
                break;
            case COCONUT:
                PlayersHandler.hitCoconut(slea, c);
                break;
            case REPAIR:
                NPCHandler.repair(slea, c);
                break;
            case REPAIR_ALL:
                NPCHandler.repairAll(c);
                break;
            case GAME_POLL:
                UserInterfaceHandler.InGame_Poll(slea, c);
                break;
            case OWL:
                InventoryHandler.Owl(slea, c);
                break;
            case OWL_WARP:
                InventoryHandler.OwlWarp(slea, c);
                break;
            case USE_CHAR_NAME_CHANGE:
                InventoryHandler.CharnameCHANGE(slea, c);
                break;
            case USE_OWL_MINERVA:
                InventoryHandler.OwlMinerva(slea, c);
                break;
            case RPS_GAME:
                NPCHandler.RPSGame(slea, c);
                break;
            case UPDATE_QUEST:
                NPCHandler.UpdateQuest(slea, c);
                break;
            case USE_ITEM_QUEST:
                NPCHandler.UseItemQuest(slea, c);
                break;
            case FOLLOW_REQUEST:
                PlayersHandler.FollowRequest(slea, c);
                break;
            case AUTO_FOLLOW_REPLY:
            case FOLLOW_REPLY:
                PlayersHandler.FollowReply(slea, c);
                break;
            case RING_ACTION:
                PlayersHandler.RingAction(slea, c);
                break;
            case REQUEST_FAMILY:
                FamilyHandler.RequestFamily(slea, c);
                break;
            case OPEN_FAMILY:
                FamilyHandler.OpenFamily(slea, c);
                break;
            case FAMILY_OPERATION:
                FamilyHandler.FamilyOperation(slea, c);
                break;
            case DELETE_JUNIOR:
                FamilyHandler.DeleteJunior(slea, c);
                break;
            case DELETE_SENIOR:
                FamilyHandler.DeleteSenior(slea, c);
                break;
            case USE_FAMILY:
                FamilyHandler.UseFamily(slea, c);
                break;
            case FAMILY_PRECEPT:
                FamilyHandler.FamilyPrecept(slea, c);
                break;
            case FAMILY_SUMMON:
                FamilyHandler.FamilySummon(slea, c);
                break;
            case ACCEPT_FAMILY:
                FamilyHandler.AcceptFamily(slea, c);
                break;
            case SOLOMON:
                PlayersHandler.Solomon(slea, c);
                break;
            case PARTY_SEARCH_START:
                PartyHandler.MemberSearch(slea, c);
                break;
            case PARTY_SEARCH_STOP:
                //PartyHandler.PartySearch(slea, c);
                break;
            case EXPEDITION_LISTING:
                PartyHandler.PartyListing(slea, c);
                break;
            case EXPEDITION_OPERATION:
                PartyHandler.Expedition(slea, c);
                break;
            case USE_TELE_ROCK:
                InventoryHandler.TeleRock(slea, c);
                break;
            case EXP_ITEM:
                InventoryHandler.handleExpConsumeItemResult(slea, c);
                break;
            case INNER_CIRCULATOR:
                InventoryHandler.useInnerCirculator(slea, c);
                break;
            case RESET_CORE_AURA:
                InventoryHandler.ResetCoreAura(slea.readInt(), c, c.getPlayer());
                break;
            case PAM_SONG:
                InventoryHandler.PamSong(slea, c);
                break;
            case REPORT:
                PlayersHandler.OnClaimResult(slea, c);
                break;
            //working
            case CANCEL_OUT_SWIPE:
                slea.readInt();
                break;
            //working
            case VIEW_SKILLS:
                PlayersHandler.viewSkills(slea, c);
                break;
            //not yet
            case SKILL_SWIPE:
                PlayersHandler.StealSkill(slea, c);
                break;
            case CHOOSE_SKILL:
                PlayersHandler.ChooseSkill(slea, c);
                break;
            //??
            case MAGIC_WHEEL:
                /* System.out.println("[MAGIC_WHEEL] [" + slea.toString() + "]");
                This is impossible to code without some reference to what the client should expect to receive.
                No online references have been made to what the receive packet struct should even look like. 
                [63 04] Header 0x1CF
                [02] Mode
                [04 00 00 00] ?? Int
                [0D 00 00 00] Slot position ?Does it need to be an int?
                [82 23 43 00] Item ID Int

                final byte mode = slea.readByte(); // 0 = open, 2 = start.
                if (mode == 2) {
                    final int idk = slea.readInt(); // 4? o.o
                    final short toUseSlot = (short) slea.readInt();
                    final int tokenId = slea.readInt();
                } else if (mode == 4) {
                    final String randomstring = slea.readMapleAsciiString();
                    int[] items = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10}; //List supposed to contain the 10 items to show while spinning.
                    c.getSession().write(CField.UIPacket.getWheelNotice((byte) 3, items, randomstring));
                }*/
                break;
            case UPDATE_RED_LEAF:
                PlayersHandler.updateRedLeafHigh(slea, c);
                break;
            case ENTER_AZWAN:
                PlayersHandler.EnterAzwan(slea, c);
                break;
            case ENTER_AZWAN_EVENT:
                PlayersHandler.EnterAzwanEvent(slea, c);
                break;
            case LEAVE_AZWAN:
                PlayersHandler.LeaveAzwan(slea, c);
                break;
            case HandleBuffProtector:
                PlayersHandler.handleBuffProtector(slea, c);
                break;
            case LINKED_SKILLS_SELECT:
                PlayersHandler.link_skill(slea, c);
                break;
            case DOUBLE_DOWN_UNK:
                PlayersHandler.DoubleDownUnk(slea, c);
                break;
            case CROSS_CHAPTER:
                PlayerHandler.CrossChapter(slea, c.getPlayer());
                break;
            case ADMIN_SHOP_REQUEST:
                NPCHandler.AdminShop(slea, c);
                break;
            case USE_FLAG_SCROLL:
                c.getPlayer().updateTick(slea.readInt());
                InventoryHandler.UseSpecialScroll(slea, c.getPlayer());
                break;
            case UseCube:
                InventoryHandler.UseCube(slea, c);
                break;
            case MixerResult:
                InventoryHandler.대충헨들러(slea, c);
                break;
            case BoardGame:
                InventoryHandler.대충헨들러3(slea, c);
                break;
            case MesoExchange:
                InventoryHandler.대충헨들러4(slea, c);
                break;
            case CROSS_HUNTER_QUEST_RESULT:
                NPCHandler.handleCrossHunterQuestRequest(slea, c);
                break;
            case CROSS_HUNTER_SHOP_RESULT:
                NPCHandler.handleCrossHunterShopRequest(slea, c);
                break;
            case PET_EXCEPTION_LIST:
                PetHandler.PetExceptionPickup(slea, c.getPlayer());
                break;
            case QuestPotOpen:
                InventoryHandler.QuestPotOpen(slea, c);
                break;
            case QuestPotFeed:
                InventoryHandler.QuestPotFeed(slea, c);
                break;
            case CLIENT_ERROR: //이거없으니까 존나 섭섭하긴하드라 뭐하는거임?
            //그러니까 유저가 팅기잖아 ? 그럼 뭐때문에 팅겼는지 로그로 남겨줌
            // 아 근데 맞아 지금 나 사실 서버 지금 테섭중인데 그 뭐냐 로그인창에서 계속 팅기던데 애들
            //로그인창으로 계속 팅긴다는거아님? 로그인창에서 로그인 누르면 그냥 해당 서버 연결이 끊어졌다나 뭐라나 하면서 팅김
            {
                if (slea.available() >= 18) {
                    String str = slea.readMapleAsciiString();
                    if (str.startsWith("Invalid Decoding")) {
                        String invalidPacketData = str.substring(43);
                        String opcode = "";
                        int op = 0;
                        op += Integer.parseInt(invalidPacketData.substring(0, 2), 16);
                        op += (Integer.parseInt(invalidPacketData.substring(3, 5), 16) << 8);
                        for (SendPacketOpcode spo : SendPacketOpcode.values()) {
                            if (spo.getValue() == op) {
                                opcode = " (Opcode : " + spo.name() + " - 0x" + Integer.toHexString(op) + ")";
                                break;
                            }
                        }
                        FileoutputUtil.log("ClientErrorPacket.txt", "Invalid Packet Decoding" + opcode + "\r\n" + invalidPacketData.toUpperCase());
                    }
                }
                break;
            }
            default:
                //System.out.println(header_num + "헤더넘버임");
                break;
        }
    }

}
