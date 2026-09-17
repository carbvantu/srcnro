//package nro.npc.ListNpc;
//
///**
// * @author Anwin
// */
//
//import nro.inventory.InventoryService;
//import nro.services.Fun.ChangeMapService;
//import nro.services.NpcService;
//import nro.services.PlayerService;
//import nro.services.Service;
//import Utils.FormatStyle;
//import Utils.Util;
//import consts.ConstNpc;
//import models.Item.Item;
//import models.Item.ItemService;
//import nro.map.The23rdMartialArtCongress.The23rdMartialArtCongressService;
//import nro.map.WorldMartialArtsTournament.WorldMartialArtsTournamentService;
//import nro.npc.Npc;
//import nro.player.Player;
//import nro.top.TopService;
//
//public class GhiDanh extends Npc {
//
//    String[] menuselect = new String[]{};
//
//    public GhiDanh(int mapId, int status, int cx, int cy, int tempId, int avartar) {
//        super(mapId, status, cx, cy, tempId, avartar);
//    }
//
//    @Override
//    public void openBaseMenu(Player pl) {
//        if (canOpenNpc(pl)) {
//            if (this.map.mapId == 52) {
//                WorldMartialArtsTournamentService.menu(this, pl);
//            } else if (this.mapId == 129) {
//                if (Util.isAfterMidnight(pl.lastTimePKDHVT23)) {
//                    pl.goldChallenge = 50_000_000;
//                    pl.rubyChallenge = 20;
//                    pl.levelWoodChest = 0;
//                }
//                long goldchallenge = pl.goldChallenge;
//                long rubychallenge = pl.rubyChallenge;
//                if (pl.levelWoodChest == 0) {
//                    menuselect = new String[]{"HÆ°á»›ng\ndáº«n\nthÃªm", "Thi Ä‘áº¥u\n" + Util.formatNumber(rubychallenge, FormatStyle.VIETNAMESE) + " há»“ng ngá»c", "Thi Ä‘áº¥u\n" + Util.formatNumber(goldchallenge, FormatStyle.VIETNAMESE) + " vÃ ng", "Vá»\nÄáº¡i Há»™i\nVÃµ Thuáº­t"};
//                } else {
//                    menuselect = new String[]{"HÆ°á»›ng\ndáº«n\nthÃªm", "Thi Ä‘áº¥u\n" + Util.formatNumber(rubychallenge, FormatStyle.VIETNAMESE) + " há»“ng ngá»c", "Thi Ä‘áº¥u\n" + Util.formatNumber(goldchallenge, FormatStyle.VIETNAMESE) + " vÃ ng", "Nháº­n\nthÆ°á»Ÿng\nRÆ°Æ¡ng Cáº¥p\n" + pl.levelWoodChest, "Vá»\nÄáº¡i Há»™i\nVÃµ Thuáº­t"};
//                }
//                this.createOtherMenu(pl, ConstNpc.BASE_MENU, "Äáº¡i há»™i vÃµ thuáº­t láº§n thá»© 23\nDiá»…n ra báº¥t ká»ƒ ngÃ y Ä‘Ãªm, ngÃ y nghá»‰, ngÃ y lá»…\nPháº§n thÆ°á»Ÿng vÃ´ cÃ¹ng quÃ½ giÃ¡\nNhanh chÃ³ng tham gia nÃ o", menuselect, "Tá»« chá»‘i");
//            } else if (this.mapId == 42 || this.mapId == 43 || this.mapId == 44) {
//                this.createOtherMenu(pl, ConstNpc.BASE_MENU, 
//                        "TÃ­nh Ä‘iá»ƒm mÃ¡y Ä‘áº¥m nÃ o cÃ¡c thÃ­ sinh\n"
//                        + "MÃ¡y Ä‘áº¥m Ä‘áº±ng kia khÃ´ng pháº£i tÃ´i.", 
//                        "Top 100\nTrÃ¡i Ä‘áº¥t", "Top 100\nNamáº¿c", "Top 100\nXayda", "Xem Ä‘iá»ƒm", "ÄÃ³ng");
//            } else {
//                super.openBaseMenu(pl);
//            }
//        }
//    }
//    
//    private long TopMayDam(Player player) {
//        if (player == null) {
//            return 0;
//        }
//        switch (player.gender) {
//            case 0:
//                return player.TopMayDamTraiDat;
//            case 1:
//                return player.TopMayDamNamec;
//            case 2:
//                return player.TopMayDamXayda;
//            default:
//                break;
//        }
//        return 0;
//    }
//
//    @Override
//    public void confirmMenu(Player player, int select) {
//        if (canOpenNpc(player)) {
//            if (this.map.mapId == 52) {
//                WorldMartialArtsTournamentService.confirm(this, player, select);
//            } else if (this.map.mapId == 42 || this.map.mapId == 43 || this.map.mapId == 44) {
//                if (player.iDMark.isBaseMenu()) {
//                    switch (select) {
//                        case 0: {
//                            TopService.showListTopMayDamTraiDat(player);
//                            break;
//                        }
//                        case 1: {
//                            TopService.showListTopMayDamNamec(player);
//                            break;
//                        }
//                        case 2: {
//                            TopService.showListTopMayDamXayda(player);
//                            break;
//                        }
//                        case 3: {
//                            NpcService.gI().createTutorial(player, tempId, this.avartar, "Äiá»ƒm mÃ¡y Ä‘áº¥m cá»§a báº¡n lÃ  " + Util.format(TopMayDam(player)));
//                            break;
//                        }
//                    }
//                }
//            } else if (this.mapId == 129) {
//                switch (player.iDMark.getIndexMenu()) {
//                case ConstNpc.BASE_MENU: {
//                long goldchallenge = player.goldChallenge;
//                long rubychallenge = player.rubyChallenge;
//                if (player.levelWoodChest == 0) {
//                    switch (select) {
//                        case 0:
//                            NpcService.gI().createTutorial(player, tempId, this.avartar, ConstNpc.NPC_DHVT23);
//                            break;
//                        case 1:
//                        case 2: {
//                            if (player.levelWoodChest != 12) {
//                                if (InventoryService.gI().finditemWoodChest(player)) {
//                                    if (select == 1) {
//                                        if (player.inventory.ruby >= rubychallenge) {
//                                            The23rdMartialArtCongressService.gI().startChallenge(player);
//                                            player.inventory.ruby -= (rubychallenge);
//                                            PlayerService.gI().sendInfoHpMpMoney(player);
//                                            player.goldChallenge += 50000000;
//                                            player.rubyChallenge += 20;
//                                        } else {
//                                            Service.gI().sendThongBao(player, "Báº¡n khÃ´ng Ä‘á»§ há»“ng ngá»c, cÃ²n thiáº¿u " + Util.formatNumber(rubychallenge - player.inventory.ruby, FormatStyle.VIETNAMESE) + " há»“ng ngá»c ná»¯a");
//                                        }
//                                    } else {
//                                        if (player.inventory.gold >= goldchallenge) {
//                                            The23rdMartialArtCongressService.gI().startChallenge(player);
//                                            player.inventory.gold -= (goldchallenge);
//                                            PlayerService.gI().sendInfoHpMpMoney(player);
//                                            player.goldChallenge += 50000000;
//                                            player.rubyChallenge += 20;
//                                        } else {
//                                            Service.gI().sendThongBao(player, "Báº¡n khÃ´ng Ä‘á»§ vÃ ng, cÃ²n thiáº¿u " + Util.formatNumber(goldchallenge - player.inventory.gold, FormatStyle.VIETNAMESE) + " vÃ ng ná»¯a");
//                                        }
//                                    }
//                                } else {
//                                    Service.gI().sendThongBao(player, "HÃ£y má»Ÿ rÆ°Æ¡ng bÃ¡u váº­t trÆ°á»›c");
//                                }
//                            } else {
//                                Service.gI().sendThongBao(player, "Báº¡n Ä‘Ã£ vÃ´ Ä‘á»‹ch giáº£i. Vui lÃ²ng chá» Ä‘áº¿n ngÃ y mai");
//                            }
//                            break;
//                        }
//                        case 3:
//                            ChangeMapService.gI().changeMapNonSpaceship(player, 52, player.location.x, 336);
//                            break;
//                    }
//                } else {
//                    switch (select) {
//                        case 0:
//                            NpcService.gI().createTutorial(player, tempId, this.avartar, ConstNpc.NPC_DHVT23);
//                            break;
//                        case 1:
//                        case 2: {
//                            if (player.levelWoodChest != 12) {
//                                if (InventoryService.gI().finditemWoodChest(player)) {
//                                    if (select == 1) {
//                                        if (player.inventory.ruby >= rubychallenge) {
//                                            The23rdMartialArtCongressService.gI().startChallenge(player);
//                                            player.inventory.ruby -= (rubychallenge);
//                                            PlayerService.gI().sendInfoHpMpMoney(player);
//                                            player.goldChallenge += 50000000;
//                                            player.rubyChallenge += 20;
//                                        } else {
//                                            Service.gI().sendThongBao(player, "Báº¡n khÃ´ng Ä‘á»§ há»“ng ngá»c, cÃ²n thiáº¿u " + Util.formatNumber(rubychallenge - player.inventory.ruby, FormatStyle.VIETNAMESE) + " há»“ng ngá»c ná»¯a");
//                                        }
//                                    } else {
//                                        if (player.inventory.gold >= goldchallenge) {
//                                            The23rdMartialArtCongressService.gI().startChallenge(player);
//                                            player.inventory.gold -= (goldchallenge);
//                                            PlayerService.gI().sendInfoHpMpMoney(player);
//                                            player.goldChallenge += 50000000;
//                                            player.rubyChallenge += 20;
//                                        } else {
//                                            Service.gI().sendThongBao(player, "Báº¡n khÃ´ng Ä‘á»§ vÃ ng, cÃ²n thiáº¿u " + Util.formatNumber(goldchallenge - player.inventory.gold, FormatStyle.VIETNAMESE) + " vÃ ng ná»¯a");
//                                        }
//                                    }
//                                } else {
//                                    Service.gI().sendThongBao(player, "HÃ£y má»Ÿ rÆ°Æ¡ng bÃ¡u váº­t trÆ°á»›c");
//                                }
//                            } else {
//                                Service.gI().sendThongBao(player, "Báº¡n Ä‘Ã£ vÃ´ Ä‘á»‹ch giáº£i. Vui lÃ²ng chá» Ä‘áº¿n ngÃ y mai");
//                            }
//                            break;
//                        }
//                        case 3:
//                            this.createOtherMenu(player, 1, "Pháº§n thÆ°á»Ÿng cá»§a báº¡n Ä‘ang á»Ÿ cáº¥p " + player.levelWoodChest + " / 12\n"
//                                    + "Má»—i ngÃ y chá»‰ Ä‘Æ°á»£c nháº­n Ä‘Æ°á»£c nháº­n thÆ°á»Ÿng 1 láº§n\n"
//                                    + "báº¡n cÃ³ cháº¯c sáº½ nháº­n pháº§n thÆ°á»Ÿng ngay bÃ¢y giá»?", "OK", "Tá»« chá»‘i");
//                            break;
//                        case 4:
//                            ChangeMapService.gI().changeMapNonSpaceship(player, 52, player.location.x, 336);
//                            break;
//                    }
//                }
//            }
//            break;
//            case 1: {
//                if (select == 0) {
//                    if (InventoryService.gI().finditemWoodChest(player)) {
//                        if (InventoryService.gI().getCountEmptyBag(player) > 0) {
//                            Item it = ItemService.gI().createNewItem((short) 570);
//                            it.addOptionParam(72, player.levelWoodChest);
//                            it.addOptionParam(30, 0);
//                            it.createTime = System.currentTimeMillis();
//                            InventoryService.gI().addItemBag(player, it);
//                            InventoryService.gI().sendItemBag(player);
//                            player.levelWoodChest = 0;
//                            player.lastTimeRewardWoodChest = System.currentTimeMillis();
//                            NpcService.gI().createMenuConMeo(player, -1, -1, "Báº¡n nháº­n Ä‘Æ°á»£c\n|1|RÆ°Æ¡ng Gá»—\n|2|Giáº¥u bÃªn trong nhiá»u váº­t pháº©m quÃ½ giÃ¡", "OK");
//                        } else {
//                            this.npcChat(player, "HÃ nh trang Ä‘Ã£ Ä‘áº§y, cáº§n má»™t Ã´ trá»‘ng trong hÃ nh trang Ä‘á»ƒ nháº­n váº­t pháº©m");
//                        }
//                    } else {
//                        Service.gI().sendThongBao(player, "HÃ£y má»Ÿ rÆ°Æ¡ng bÃ¡u váº­t trÆ°á»›c");
//                    }
//                }
//                break;
//            }
//                }
//            }
//        }
//    }
//}
package nro.npc.ListNpc;


import nro.inventory.InventoryService;
import nro.services.Fun.ChangeMapService;
import nro.services.NpcService;
import nro.services.Service;
import Utils.Util;
import consts.ConstNpc;
import models.Item.Item;
import models.Item.ItemService;
import nro.map.The23rdMartialArtCongress.The23rdMartialArtCongressService;
import nro.map.WorldMartialArtsTournament.WorldMartialArtsTournamentService;
import nro.npc.Npc;
import nro.player.Player;
import nro.top.TopService;

public class GhiDanh extends Npc {

    String[] menuselect = new String[]{};

    private static final short THOI_VANG_ID = 457; 
    private static final int SO_THOI_VANG_MOI_LAN = 1;

    public GhiDanh(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        super(mapId, status, cx, cy, tempId, avartar);
    }

    @Override
    public void openBaseMenu(Player pl) {
        if (canOpenNpc(pl)) {
            if (this.map.mapId == 52) {
                WorldMartialArtsTournamentService.menu(this, pl);

            } else if (this.mapId == 129) {

                // Bá» reset goldChallenge/rubyChallenge vÃ¬ khÃ´ng dÃ¹ng ná»¯a
                if (Util.isAfterMidnight(pl.lastTimePKDHVT23)) {
                    pl.levelWoodChest = 0;
                }

                if (pl.levelWoodChest == 0) {
                    menuselect = new String[]{
                            "HÆ°á»›ng\ndáº«n\nthÃªm",
                            "Thi Ä‘áº¥u\n1 Thá»i vÃ ng",
                            "Vá»\nÄáº¡i Há»™i\nVÃµ Thuáº­t"
                    };
                } else {
                    menuselect = new String[]{
                            "HÆ°á»›ng\ndáº«n\nthÃªm",
                            "Thi Ä‘áº¥u\n1 Thá»i vÃ ng",
                            "Nháº­n\nthÆ°á»Ÿng\nRÆ°Æ¡ng Cáº¥p\n" + pl.levelWoodChest,
                            "Vá»\nÄáº¡i Há»™i\nVÃµ Thuáº­t"
                    };
                }

                this.createOtherMenu(pl, ConstNpc.BASE_MENU,
                        "Äáº¡i há»™i vÃµ thuáº­t láº§n thá»© 23\n"
                                + "Diá»…n ra báº¥t ká»ƒ ngÃ y Ä‘Ãªm, ngÃ y nghá»‰, ngÃ y lá»…\n"
                                + "Pháº§n thÆ°á»Ÿng vÃ´ cÃ¹ng quÃ½ giÃ¡\n"
                                + "Nhanh chÃ³ng tham gia nÃ o",
                        menuselect, "Tá»« chá»‘i");

            } else if (this.mapId == 42 || this.mapId == 43 || this.mapId == 44) {
                this.createOtherMenu(pl, ConstNpc.BASE_MENU,
                        "TÃ­nh Ä‘iá»ƒm mÃ¡y Ä‘áº¥m nÃ o cÃ¡c thÃ­ sinh\n"
                                + "MÃ¡y Ä‘áº¥m Ä‘áº±ng kia khÃ´ng pháº£i tÃ´i.",
                        "Top 100\nTrÃ¡i Ä‘áº¥t", "Top 100\nNamáº¿c", "Top 100\nXayda", "Xem Ä‘iá»ƒm", "ÄÃ³ng");
            } else {
                super.openBaseMenu(pl);
            }
        }
    }

    private long TopMayDam(Player player) {
        if (player == null) return 0;
        switch (player.gender) {
            case 0:
                return player.TopMayDamTraiDat;
            case 1:
                return player.TopMayDamNamec;
            case 2:
                return player.TopMayDamXayda;
        }
        return 0;
    }

    private boolean truThoiVang(Player player, int soLuong) {
        Item tv = InventoryService.gI().findItemBag(player, THOI_VANG_ID);
        if (tv != null && tv.isNotNullItem() && tv.quantity >= soLuong) {
            InventoryService.gI().subQuantityItemsBag(player, tv, soLuong);
            InventoryService.gI().sendItemBag(player);
            return true;
        }
        return false;
    }

    @Override
    public void confirmMenu(Player player, int select) {
        if (!canOpenNpc(player)) return;

        if (this.map.mapId == 52) {
            WorldMartialArtsTournamentService.confirm(this, player, select);
            return;
        }

        if (this.map.mapId == 42 || this.map.mapId == 43 || this.map.mapId == 44) {
            if (player.iDMark.isBaseMenu()) {
                switch (select) {
                    case 0:
                        TopService.showListTopMayDamTraiDat(player);
                        break;
                    case 1:
                        TopService.showListTopMayDamNamec(player);
                        break;
                    case 2:
                        TopService.showListTopMayDamXayda(player);
                        break;
                    case 3:
                        NpcService.gI().createTutorial(player, tempId, this.avartar,
                                "Äiá»ƒm mÃ¡y Ä‘áº¥m cá»§a báº¡n lÃ  " + Util.format(TopMayDam(player)));
                        break;
                }
            }
            return;
        }

        if (this.mapId == 129) {
            switch (player.iDMark.getIndexMenu()) {

                case ConstNpc.BASE_MENU: {
                    if (player.levelWoodChest == 0) {
                        switch (select) {
                            case 0: // HÆ°á»›ng dáº«n
                                NpcService.gI().createTutorial(player, tempId, this.avartar, ConstNpc.NPC_DHVT23);
                                break;

                            case 1: // Thi Ä‘áº¥u 1 thá»i vÃ ng
                                if (player.levelWoodChest != 12) {
                                    if (InventoryService.gI().finditemWoodChest(player)) {
                                        if (truThoiVang(player, SO_THOI_VANG_MOI_LAN)) {
                                            The23rdMartialArtCongressService.gI().startChallenge(player);
                                        } else {
                                            Service.gI().sendThongBao(player, "Báº¡n cáº§n " + SO_THOI_VANG_MOI_LAN + " Thá»i vÃ ng Ä‘á»ƒ thi Ä‘áº¥u");
                                        }
                                    } else {
                                        Service.gI().sendThongBao(player, "HÃ£y má»Ÿ rÆ°Æ¡ng bÃ¡u váº­t trÆ°á»›c");
                                    }
                                } else {
                                    Service.gI().sendThongBao(player, "Báº¡n Ä‘Ã£ vÃ´ Ä‘á»‹ch giáº£i. Vui lÃ²ng chá» Ä‘áº¿n ngÃ y mai");
                                }
                                break;

                            case 2: // Vá» Äáº¡i Há»™i VÃµ Thuáº­t
                                ChangeMapService.gI().changeMapNonSpaceship(player, 52, player.location.x, 336);
                                break;
                        }

                    } else {
                        switch (select) {
                            case 0: // HÆ°á»›ng dáº«n
                                NpcService.gI().createTutorial(player, tempId, this.avartar, ConstNpc.NPC_DHVT23);
                                break;

                            case 1: 
                                if (player.levelWoodChest != 12) {
                                    if (InventoryService.gI().finditemWoodChest(player)) {
                                        if (truThoiVang(player, SO_THOI_VANG_MOI_LAN)) {
                                            The23rdMartialArtCongressService.gI().startChallenge(player);
                                        } else {
                                            Service.gI().sendThongBao(player, "Báº¡n cáº§n " + SO_THOI_VANG_MOI_LAN + " Thá»i vÃ ng Ä‘á»ƒ thi Ä‘áº¥u");
                                        }
                                    } else {
                                        Service.gI().sendThongBao(player, "HÃ£y má»Ÿ rÆ°Æ¡ng bÃ¡u váº­t trÆ°á»›c");
                                    }
                                } else {
                                    Service.gI().sendThongBao(player, "Báº¡n Ä‘Ã£ vÃ´ Ä‘á»‹ch giáº£i. Vui lÃ²ng chá» Ä‘áº¿n ngÃ y mai");
                                }
                                break;

                            case 2: // Nháº­n thÆ°á»Ÿng
                                this.createOtherMenu(player, 1,
                                        "Pháº§n thÆ°á»Ÿng cá»§a báº¡n Ä‘ang á»Ÿ cáº¥p " + player.levelWoodChest + " / 12\n"
                                                + "Má»—i ngÃ y chá»‰ Ä‘Æ°á»£c nháº­n Ä‘Æ°á»£c nháº­n thÆ°á»Ÿng 1 láº§n\n"
                                                + "báº¡n cÃ³ cháº¯c sáº½ nháº­n pháº§n thÆ°á»Ÿng ngay bÃ¢y giá»?",
                                        "OK", "Tá»« chá»‘i");
                                break;

                            case 3: 
                                ChangeMapService.gI().changeMapNonSpaceship(player, 52, player.location.x, 336);
                                break;
                        }
                    }
                    break;
                }

                case 1: { 
                    if (select == 0) {
                        if (InventoryService.gI().finditemWoodChest(player)) {
                            if (InventoryService.gI().getCountEmptyBag(player) > 0) {
                                Item it = ItemService.gI().createNewItem((short) 570);
                                it.addOptionParam(72, player.levelWoodChest);
                                it.addOptionParam(30, 0);
                                it.createTime = System.currentTimeMillis();
                                InventoryService.gI().addItemBag(player, it);
                                InventoryService.gI().sendItemBag(player);

                                player.levelWoodChest = 0;
                                player.lastTimeRewardWoodChest = System.currentTimeMillis();

                                NpcService.gI().createMenuConMeo(player, -1, -1,
                                        "Báº¡n nháº­n Ä‘Æ°á»£c\n|1|RÆ°Æ¡ng Gá»—\n|2|Giáº¥u bÃªn trong nhiá»u váº­t pháº©m quÃ½ giÃ¡",
                                        "OK");
                            } else {
                                this.npcChat(player, "HÃ nh trang Ä‘Ã£ Ä‘áº§y, cáº§n má»™t Ã´ trá»‘ng trong hÃ nh trang Ä‘á»ƒ nháº­n váº­t pháº©m");
                            }
                        } else {
                            Service.gI().sendThongBao(player, "HÃ£y má»Ÿ rÆ°Æ¡ng bÃ¡u váº­t trÆ°á»›c");
                        }
                    }
                    break;
                }
            }
        }
    }
}
