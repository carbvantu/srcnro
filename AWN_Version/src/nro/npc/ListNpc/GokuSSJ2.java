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
//public class GokuSSJ2 extends Npc {
//
//    public GokuSSJ2(int mapId, int status, int cx, int cy, int tempId, int avartar) {
//        super(mapId, status, cx, cy, tempId, avartar);
//    }
//
//    @Override
//    public void openBaseMenu(Player player) {
//        if (canOpenNpc(player)) {
//            this.createOtherMenu(player, ConstNpc.BASE_MENU, "HÃ£y cá»‘ gáº¯ng luyá»‡n táº­p\nThu tháº­p 9.999 bÃ­ kiáº¿p Ä‘á»ƒ Ä‘á»•i trang phá»¥c Yardrat nhÃ©!",
//                    "Nháº­n\nthÆ°á»Ÿng", "OK");
//        }
//    }
//
//    @Override
//    public void confirmMenu(Player player, int select) {
//        if (canOpenNpc(player)) {
//            if (select == 0) {
//                int soluong = InventoryService.gI().getParam(player, 31, 590);
//                if (soluong >= 9999) {
//                    InventoryService.gI().subParamItemsBag(player, 590, 31, 9999);
//                    Item yardart = ItemService.gI().createNewItem((short) (player.gender + 592));
//                    yardart.itemOptions.add(new Item.ItemOption(47, 400));
//                    yardart.itemOptions.add(new Item.ItemOption(97, 10));
//                    yardart.itemOptions.add(new Item.ItemOption(14, 15));
//                    yardart.itemOptions.add(new Item.ItemOption(147, 30));
//                    yardart.itemOptions.add(new Item.ItemOption(108, 10));
//                    InventoryService.gI().addItemBag(player, yardart);
//                    InventoryService.gI().sendItemBag(player);
//                    Service.gI().sendThongBao(player, "Báº¡n nháº­n Ä‘Æ°á»£c vÃµ phá»¥c cá»§a ngÆ°á»i Yardrat");
//                }
//            }
//        }
//    }
//}

