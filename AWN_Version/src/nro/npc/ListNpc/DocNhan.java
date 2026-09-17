//package nro.npc.ListNpc;
//
///**
// * @author Anwin
// */
//
//import consts.ConstNpc;
//import nro.npc.Npc;
//import nro.player.Player;
//
//public class DocNhan extends Npc {
//
//    public DocNhan(int mapId, int status, int cx, int cy, int tempId, int avartar) {
//        super(mapId, status, cx, cy, tempId, avartar);
//    }
//
//    @Override
//    public void openBaseMenu(Player player) {
//        if (canOpenNpc(player)) {
//            if (mapId == 57) {
//                if (!player.clan.doanhTrai.winDT) {
//                    NpcService.gI().createTutorial(player, tempId, this.avartar, "Bá»n mi Ä‘á»«ng hÃ²ng thoÃ¡t khá»i nÆ¡i Ä‘Ã¢y");
//                } else {
//                    NpcService.gI().createTutorial(player, tempId, this.avartar, "Ta chá»‹u thua, nhÆ°ng cÃ¡c ngÆ°Æ¡i Ä‘á»«ng cÃ³ mong láº¥y Ä‘Æ°á»£c ngá»c cá»§a ra\nta Ä‘Ã£ giáº¥u ngá»c 4 sao vÃ  1 Ä‘á»‘ng ngá»c 7 sao trong doanh tráº¡i nÃ y...\nCÃ¡c ngÆ°Æ¡i chá»‰ cÃ³ 5 phÃºt Ä‘i tÃ¬m, Ä‘á»‘ cÃ¡c ngÆ°Æ¡i tÃ¬m ra hahaha");
//                    if (!player.clan.doanhTrai.isTimePicking) {
//                        Service.gI().sendThongBao(player, "Tráº¡i Äá»™c NhÃ£n Ä‘Ã£ bá»‹ tiÃªu diá»‡t, báº¡n cÃ³ 5 phÃºt Ä‘á»ƒ tÃ¬m kiáº¿m viÃªn ngá»c 4 sao trÆ°á»›c khi phi thuyá»n Ä‘áº¿n Ä‘Ã³n");
//                        player.clan.doanhTrai.isTimePicking = true;
//                        player.clan.doanhTrai.lastTimePick = System.currentTimeMillis();
//                        player.clan.doanhTrai.randomNR();
//                        player.clan.doanhTrai.sendTextTimePickDoanhTrai();
//                    }
//                }
//
//            }
//        }
//    }
//
//    @Override
//    public void confirmMenu(Player player, int select) {
//    }
//}

