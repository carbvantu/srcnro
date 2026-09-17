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
//public class GokuSSJ extends Npc {
//
//    public GokuSSJ(int mapId, int status, int cx, int cy, int tempId, int avartar) {
//        super(mapId, status, cx, cy, tempId, avartar);
//    }
//
//    @Override
//    public void openBaseMenu(Player player) {
//        if (canOpenNpc(player)) {
//            switch (this.mapId) {
//                case 80 ->
//                    this.createOtherMenu(player, ConstNpc.BASE_MENU, "Ta má»›i háº¡ Fide, nhÆ°ng nÃ³ Ä‘Ã£ ká»‹p Ä‘Ã o 1 cÃ¡i lá»—\nHÃ nh tinh nÃ y sáº¯p ná»• tung rá»“i\nMau lÆ°á»£n thÃ´i",
//                            "Chuáº©n");
//                case 131 ->
//                    this.createOtherMenu(player, ConstNpc.BASE_MENU, "ÄÃ¢y lÃ  Ä‘Ã¢u? Xong cmnr", "BÃ³ tay", "Vá» chá»— cÅ©");
//                default ->
//                    super.openBaseMenu(player);
//            }
//        }
//    }
//
//    @Override
//    public void confirmMenu(Player player, int select) {
//        if (canOpenNpc(player)) {
//            switch (player.iDMark.getIndexMenu()) {
//                case ConstNpc.BASE_MENU -> {
//                    if (this.mapId == 131) {
//                        if (select == 1) {
//                            ChangeMapService.gI().changeMapBySpaceShip(player, 80, -1, 870);
//                        }
//                    } else if (this.mapId == 80) {
//                        if (select == 0) {
//                            ChangeMapService.gI().changeMapBySpaceShip(player, 131, -1, 870);
//                        }
//                    }
//                }
//            }
//        }
//    }
//}

