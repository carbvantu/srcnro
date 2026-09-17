package nro.server;

import Data.DataGame;
import Utils.Logger;
import Utils.Util;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import jbcd.ConnectDB;
import jbcd.dao.PlayerDAO;
import jbcd.data.GodGK;
import models.Item.Item;
import models.Item.ItemOption;
import models.Item.ItemService;
import nro.inventory.Inventory;
import nro.inventory.InventoryService;
import nro.player.Player;
import nro.player.Detu;
import nro.services.DetuService;
import nro.services.PlayerService;
import nro.services.Service;
import nro.services.TaskService;
import nro.power.PowerLimitManager;
import nro.player.NPoint;
import nro.badges.BadgesData;
import nro.badges.BadgesService;
import nro.badges.BagesTemplate;
import event.EventManager;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.stream.Collectors;
import nro.task.TaskMain;
import nro.template.ItemTemplate;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@SuppressWarnings("serial")
public class PlayerBuffManagerPanel extends JPanel {

    private final Color COL_PRIMARY = new Color(0, 120, 215);
    private final Color COL_SUCCESS = new Color(40, 167, 69);
    private final Color COL_DANGER = new Color(220, 53, 69);
    private final Color COL_WARNING = new Color(230, 126, 34);
    private final Color COL_PURPLE = new Color(142, 68, 173);
    private final Color COL_HEADER = new Color(240, 242, 245);
    private final Color COL_BORDER = new Color(220, 220, 220);
    private final Font FONT_UI = new Font("Segoe UI", Font.PLAIN, 13);
    private final Font FONT_BOLD = new Font("Segoe UI", Font.BOLD, 13);

    // Left table
    private JTable playerTable;
    private DefaultTableModel playerModel;
    private TableRowSorter<DefaultTableModel> sorter;
    private JTextField txtSearchPlayer;
    private JLabel lblSelectedPlayerInfo;
    private JCheckBox chkOnlyOnline;

    // Currently selected target
    private int currentSelectedPlayerId = -1;
    private String currentSelectedPlayerName = "";
    private Player currentSelectedPlayer = null;

    // Right tabs
    private JTabbedPane mainTabbedPane;

    // Item Data Model
    public static class ItemData {
        public int id;
        public String name;
        public int type;
        public int gender;
        public int iconId;
        public int part;
        public int level;
        public String description;

        public ItemData(int id, String name, int type, int gender, int iconId, int part, int level, String description) {
            this.id = id;
            this.name = name != null ? name : "";
            this.type = type;
            this.gender = gender;
            this.iconId = iconId;
            this.part = part;
            this.level = level;
            this.description = description != null ? description : "";
        }

        @Override
        public String toString() {
            return "[" + id + "] " + name;
        }
    }

    // Cached item lists
    private final List<ItemData> allItemList = new ArrayList<>();
    private final List<ItemData> quanAoList = new ArrayList<>();
    private final List<ItemData> caiTrangList = new ArrayList<>();
    private final List<ItemData> susanoList = new ArrayList<>();
    private final List<ItemData> canhList = new ArrayList<>();
    private final List<ItemData> petList = new ArrayList<>();
    private final List<ItemData> skillBookList = new ArrayList<>();
    private final Map<Integer, ItemData> itemMap = new HashMap<>();
    private final Map<Integer, ImageIcon> iconCache = new ConcurrentHashMap<>();

    // Selected items for each tab
    private ItemData currentSelectedEquip = null;
    private JLabel lblEquipIconPreview;
    private JLabel lblEquipTitle;
    private JLabel lblEquipDesc;
    private JTextField txtEquipId;
    private JComboBox<String> cbEquipDestination;
    private JComboBox<String> cbEquipUpgrade;
    private JSpinner spEquipStar;
    private JCheckBox chkEquipFullStar;
    private JCheckBox chkEquipLock;
    private JSpinner spEquipHsd;
    private JTextField txtEquipCustomOptions;

    private ItemData currentSelectedCaiTrang = null;
    private JLabel lblCtIconPreview;
    private JLabel lblCtTitle;
    private JLabel lblCtDesc;
    private JComboBox<ItemData> cbCaiTrang;
    private JTextField txtCtId;
    private JComboBox<String> cbCtDestination;
    private JComboBox<String> cbCtOptionPreset;
    private JTextField txtCtCustomOptions;
    private JCheckBox chkCtLock;
    private JSpinner spCtHsd;
    private boolean isUpdatingCbCaiTrang = false;

    // Pet UI components
    private ItemData currentSelectedPet = null;
    private JLabel lblPetIconPreview;
    private JLabel lblPetTitle;
    private JLabel lblPetDesc;
    private JComboBox<ItemData> cbPet;
    private JTextField txtPetId;
    private JComboBox<String> cbPetDestination;
    private JComboBox<String> cbPetOptionPreset;
    private JTextField txtPetCustomOptions;
    private JCheckBox chkPetLock;
    private JSpinner spPetHsd;
    private boolean isUpdatingCbPet = false;

    private ItemData currentSelectedSusano = null;
    private JLabel lblSusanoIconPreview;
    private JLabel lblSusanoTitle;
    private JLabel lblSusanoDesc;
    private JTextField txtSusanoId;
    private JComboBox<String> cbSusanoDestination;
    private JCheckBox chkSusanoGodBuff;
    private JTextField txtSusanoCustomOptions;
    private JCheckBox chkSusanoLock;
    private JSpinner spSusanoHsd;

    private ItemData currentSelectedItem = null;
    private JLabel lblItemIconPreview;
    private JLabel lblItemTitle;
    private JLabel lblItemDesc;
    private JTextField txtItemId;
    private JSpinner spItemQuantity;
    private JComboBox<String> cbItemDestination;
    private JTextField txtItemCustomOptions;
    private JCheckBox chkItemLock;
    private JSpinner spItemHsd;

    // Skill Book UI components
    private ItemData currentSelectedSkillBook = null;
    private JLabel lblBookIconPreview;
    private JLabel lblBookTitle;
    private JLabel lblBookDesc;
    private JTextField txtBookId;
    private JSpinner spBookQuantity;
    private JComboBox<String> cbBookDestination;
    private JComboBox<ItemData> cbSkillBooks;
    private boolean isUpdatingCbSkillBooks = false;

    // Badges Data Model & Cache
    public static class BadgeInfo {
        public int id;
        public int idEffect;
        public int idItem;
        public int iconId;
        public String name;
        public List<ItemOption> options = new ArrayList<>();
        public String optionSummary = "";

        public BadgeInfo(int id, int idEffect, int idItem, int iconId, String name, List<ItemOption> options) {
            this.id = id;
            this.idEffect = idEffect;
            this.idItem = idItem;
            this.iconId = iconId;
            this.name = name != null ? name : "";
            if (options != null) {
                this.options.addAll(options);
            }
            buildOptionSummary();
        }

        public BadgeInfo(int id, int idEffect, int idItem, String name, List<ItemOption> options) {
            this(id, idEffect, idItem, -1, name, options);
        }

        private void buildOptionSummary() {
            if (options == null || options.isEmpty()) {
                optionSummary = "Danh hieu danh du (Khong co chi so)";
                return;
            }
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < options.size(); i++) {
                ItemOption opt = options.get(i);
                if (i > 0) sb.append(" | ");
                if (opt != null && opt.optionTemplate != null) {
                    sb.append(formatBadgeOption(opt.optionTemplate.id, opt.param));
                } else if (opt != null) {
                    sb.append("Option: +" + opt.param);
                }
            }
            optionSummary = sb.toString();
        }

        @Override
        public String toString() {
            return "[" + idEffect + "] " + name;
        }
    }

    private final List<BadgeInfo> badgeList = new ArrayList<>();
    private final Map<Integer, BadgeInfo> badgeMap = new ConcurrentHashMap<>();
    private final Map<Integer, ImageIcon> badgeImageCache = new ConcurrentHashMap<>();

    // Badge UI components
    private BadgeInfo currentSelectedBadge = null;
    private JLabel lblBadgeBannerPreview;
    private JLabel lblBadgeIconPreview;
    private JLabel lblBadgeTitle;
    private JLabel lblBadgeDesc;
    private JTable badgeTable;
    private DefaultTableModel badgeModel;
    private TableRowSorter<DefaultTableModel> badgeSorter;
    private JTextField txtSearchBadge;
    private JComboBox<String> cbBadgeDuration;
    private JComboBox<String> cbBadgeDestination;
    private JCheckBox chkBadgeActiveNow;
    private JCheckBox chkBadgeGiveItem;

    // Event UI components
    private final List<JCheckBox> eventCheckBoxes = new ArrayList<>();
    private final List<ItemData> eventItemList = new ArrayList<>();
    private JComboBox<ItemData> cbEventItems;
    private JLabel lblEventItemPreview;
    private JLabel lblEventItemTitle;
    private JLabel lblEventItemDesc;
    private JSpinner spEventItemQuantity;
    private JComboBox<String> cbEventItemDest;
    private JCheckBox chkEventItemLock;
    private boolean isUpdatingEventCombo = false;

    public PlayerBuffManagerPanel() {
        setLayout(new BorderLayout(10, 10));
        setBackground(Color.WHITE);
        setBorder(new EmptyBorder(10, 10, 10, 10));

        initHeader();
        initCenterSplit();

        loadItemTemplatesFromDB();
        loadPlayers("");
    }

    private ImageIcon getItemIcon(int iconId, int size) {
        if (iconId <= 0) return null;
        int key = iconId * 1000 + size;
        ImageIcon cached = iconCache.get(key);
        if (cached != null) return cached;

        try {
            File f = DataGame.getIconFile(iconId);
            if (f != null && f.exists()) {
                Image img = ImageIO.read(f);
                if (img != null) {
                    Image scaled = img.getScaledInstance(size, size, Image.SCALE_SMOOTH);
                    ImageIcon ico = new ImageIcon(scaled);
                    iconCache.put(key, ico);
                    return ico;
                }
            }
        } catch (Exception ignored) {}
        return null;
    }

    @SuppressWarnings("serial")
    private class ItemListCellRenderer extends DefaultListCellRenderer {
        private final int iconSize;

        public ItemListCellRenderer(int iconSize) {
            this.iconSize = iconSize;
        }

        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            JLabel lbl = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            if (value instanceof ItemData) {
                ItemData item = (ItemData) value;
                lbl.setText("[" + item.id + "] " + item.name);
                lbl.setIcon(getItemIcon(item.iconId, iconSize));
                lbl.setIconTextGap(8);
            }
            return lbl;
        }
    }

    private void loadItemTemplatesFromDB() {
        new Thread(() -> {
            List<ItemData> items = new ArrayList<>();
            List<ItemData> equips = new ArrayList<>();
            List<ItemData> cts = new ArrayList<>();
            List<ItemData> susanos = new ArrayList<>();
            List<ItemData> canhs = new ArrayList<>();
            List<ItemData> pets = new ArrayList<>();
            List<ItemData> books = new ArrayList<>();
            Map<Integer, ItemData> map = new HashMap<>();

            // 1. Load from Manager.ITEM_TEMPLATES first if available
            try {
                if (Manager.ITEM_TEMPLATES != null && !Manager.ITEM_TEMPLATES.isEmpty()) {
                    for (ItemTemplate temp : Manager.ITEM_TEMPLATES) {
                        if (temp != null) {
                            ItemData it = new ItemData(temp.id, temp.name, temp.type, temp.gender, temp.iconID, temp.part, temp.level, temp.description);
                            items.add(it);
                            map.put(it.id, it);
                            classifyItem(it, equips, cts, susanos, canhs, pets, books);
                        }
                    }
                }
            } catch (Exception ignored) {}

            // 2. Load from MySQL database to ensure 100% complete items (2000+ items)
            try (Connection conn = ConnectDB.getConnection(); Statement stmt = conn.createStatement()) {
                String sql = "SELECT id, name, type, gender, icon_id, part, level, description FROM item_template ORDER BY id ASC";
                try (ResultSet rs = stmt.executeQuery(sql)) {
                    while (rs.next()) {
                        int id = rs.getInt("id");
                        if (!map.containsKey(id)) {
                            String name = rs.getString("name");
                            int type = rs.getInt("type");
                            int gender = rs.getInt("gender");
                            int iconId = rs.getInt("icon_id");
                            int part = rs.getInt("part");
                            int level = rs.getInt("level");
                            String desc = rs.getString("description");

                            ItemData it = new ItemData(id, name, type, gender, iconId, part, level, desc);
                            items.add(it);
                            map.put(id, it);
                            classifyItem(it, equips, cts, susanos, canhs, pets, books);
                        }
                    }
                }
            } catch (Exception e) {
                Logger.logException(PlayerBuffManagerPanel.class, e, "Loi loadItemTemplatesFromDB");
            }

            // 3. Load Badges from Manager.BAGES_TEMPLATES and data_badges
            List<BadgeInfo> bList = new ArrayList<>();
            Map<Integer, BadgeInfo> bMap = new HashMap<>();
            try {
                if (Manager.BAGES_TEMPLATES != null && !Manager.BAGES_TEMPLATES.isEmpty()) {
                    for (BagesTemplate bt : Manager.BAGES_TEMPLATES) {
                        if (bt != null) {
                            int iconId = map.containsKey(bt.idItem) ? map.get(bt.idItem).iconId : -1;
                            BadgeInfo bi = new BadgeInfo(bt.id, bt.idEffect, bt.idItem, iconId, bt.NAME, bt.options);
                            bList.add(bi);
                            bMap.put(bi.idEffect, bi);
                        }
                    }
                }
            } catch (Exception ignored) {}

            try (Connection conn = ConnectDB.getConnection(); Statement stmt = conn.createStatement()) {
                String sql = "SELECT b.id, b.idEffect, b.idItem, b.NAME, b.Options, i.icon_id FROM data_badges b LEFT JOIN item_template i ON b.idItem = i.id ORDER BY b.id ASC";
                try (ResultSet rs = stmt.executeQuery(sql)) {
                    while (rs.next()) {
                        int effId = rs.getInt("idEffect");
                        int iconId = rs.getInt("icon_id");
                        if (!bMap.containsKey(effId)) {
                            int id = rs.getInt("id");
                            int idItem = rs.getInt("idItem");
                            String name = rs.getString("NAME");
                            List<ItemOption> opts = new ArrayList<>();
                            try {
                                JSONArray option = (JSONArray) JSONValue.parse(rs.getString("Options"));
                                if (option != null) {
                                    for (int u = 0; u < option.size(); u++) {
                                        JSONObject jsonobject = (JSONObject) option.get(u);
                                        int optionId = Integer.parseInt(jsonobject.get("id").toString());
                                        int param = Integer.parseInt(jsonobject.get("param").toString());
                                        opts.add(new ItemOption(optionId, param));
                                    }
                                }
                            } catch (Exception ignored) {}
                            BadgeInfo bi = new BadgeInfo(id, effId, idItem, iconId, name, opts);
                            bList.add(bi);
                            bMap.put(bi.idEffect, bi);
                        } else {
                            BadgeInfo bi = bMap.get(effId);
                            if (bi.iconId <= 0 && iconId > 0) {
                                bi.iconId = iconId;
                            }
                        }
                    }
                }
            } catch (Exception e) {
                Logger.logException(PlayerBuffManagerPanel.class, e, "Loi load data_badges");
            }

            SwingUtilities.invokeLater(() -> {
                allItemList.clear();
                allItemList.addAll(items);
                quanAoList.clear();
                quanAoList.addAll(equips);
                caiTrangList.clear();
                caiTrangList.addAll(cts);
                susanoList.clear();
                susanoList.addAll(susanos);
                canhList.clear();
                canhList.addAll(canhs);
                petList.clear();
                petList.addAll(pets);
                skillBookList.clear();
                skillBookList.addAll(books);
                itemMap.clear();
                itemMap.putAll(map);

                // Populate Disguise ComboBox safely
                if (cbCaiTrang != null) {
                    isUpdatingCbCaiTrang = true;
                    DefaultComboBoxModel<ItemData> ctModel = new DefaultComboBoxModel<>();
                    for (ItemData ct : caiTrangList) {
                        ctModel.addElement(ct);
                    }
                    cbCaiTrang.setModel(ctModel);
                    isUpdatingCbCaiTrang = false;

                    if (!caiTrangList.isEmpty()) {
                        setSelectedCaiTrang(caiTrangList.get(0));
                    }
                }

                // Populate Default Quan Ao (Default: Ao Than Linh 555)
                if (itemMap.containsKey(555)) {
                    setSelectedEquip(itemMap.get(555));
                } else if (!quanAoList.isEmpty()) {
                    setSelectedEquip(quanAoList.get(0));
                }

                // Populate Default Susano (Default: Hoa Than The 2137)
                if (itemMap.containsKey(2137)) {
                    setSelectedSusano(itemMap.get(2137));
                } else if (!susanoList.isEmpty()) {
                    setSelectedSusano(susanoList.get(0));
                }

                // Populate Default Item (Default: Thoi Vang 457)
                if (itemMap.containsKey(457)) {
                    setSelectedItem(itemMap.get(457));
                } else if (!allItemList.isEmpty()) {
                    setSelectedItem(allItemList.get(0));
                }

                // Populate Skill Books ComboBox
                if (cbSkillBooks != null) {
                    isUpdatingCbSkillBooks = true;
                    DefaultComboBoxModel<ItemData> bookModel = new DefaultComboBoxModel<>();
                    for (ItemData b : skillBookList) {
                        bookModel.addElement(b);
                    }
                    cbSkillBooks.setModel(bookModel);
                    isUpdatingCbSkillBooks = false;
                    if (itemMap.containsKey(1044)) {
                        setSelectedSkillBook(itemMap.get(1044));
                    } else if (!skillBookList.isEmpty()) {
                        setSelectedSkillBook(skillBookList.get(0));
                    }
                }

                // Populate Pet & Thu Cuoi ComboBox
                if (cbPet != null) {
                    isUpdatingCbPet = true;
                    DefaultComboBoxModel<ItemData> petModel = new DefaultComboBoxModel<>();
                    for (ItemData p : petList) {
                        petModel.addElement(p);
                    }
                    cbPet.setModel(petModel);
                    isUpdatingCbPet = false;
                    if (itemMap.containsKey(2468)) {
                        setSelectedPet(itemMap.get(2468));
                    } else if (!petList.isEmpty()) {
                        setSelectedPet(petList.get(0));
                    }
                }

                // Populate Badges List & Table
                badgeList.clear();
                badgeList.addAll(bList);
                badgeMap.clear();
                badgeMap.putAll(bMap);
                if (badgeModel != null) {
                    badgeModel.setRowCount(0);
                    for (BadgeInfo b : badgeList) {
                        ImageIcon badgeIco = getBadgeDisplayIcon(b, 130, 34);
                        badgeModel.addRow(new Object[]{badgeIco, b.idEffect, b.name, b.idItem, b.optionSummary});
                    }
                    if (!badgeList.isEmpty()) {
                        badgeTable.setRowSelectionInterval(0, 0);
                        setSelectedBadge(badgeList.get(0));
                    }
                }

                // Initialize Event Items in Event Tab
                initEventItems();
            });
        }).start();
    }

    private boolean isSkillBook(ItemData it) {
        if (it == null) return false;
        if (it.type == 7 || it.type == 35 || it.type == 37) return true;
        String lower = it.name.toLowerCase();
        return lower.contains("sách") || lower.contains("sach") || lower.contains("bí kíp") || lower.contains("bi kiep") || lower.contains("tuyệt kỹ") || lower.contains("tuyet ky") || lower.contains("kaioken") || lower.contains("makankosappo") || lower.contains("kamejoko") || lower.contains("masenko") || lower.contains("antomic") || lower.contains("thái dương") || lower.contains("trị thương") || lower.contains("tái tạo") || lower.contains("bom hi sinh") || lower.contains("đẻ trứng") || lower.contains("khiên năng lượng");
    }

    private void classifyItem(ItemData it, List<ItemData> equips, List<ItemData> cts, List<ItemData> susanos, List<ItemData> canhs, List<ItemData> pets, List<ItemData> books) {
        if ((it.type >= 0 && it.type <= 4) || it.type == 32 || it.type == 35 || it.type == 39) {
            equips.add(it);
        }
        if (it.type == 5 || isCaiTrangName(it.name)) {
            cts.add(it);
        }
        if (isSusanoItem(it)) {
            susanos.add(it);
        }
        if (isCanhItem(it)) {
            canhs.add(it);
        }
        if (it.type == 21 || it.type == 23 || it.type == 24 || it.type == 70 || it.type == 71 || isPetName(it.name) || isMountName(it.name)) {
            pets.add(it);
        }
        if (isSkillBook(it)) {
            books.add(it);
        }
    }

    private boolean isCaiTrangName(String name) {
        if (name == null) return false;
        String lower = name.toLowerCase();
        return lower.contains("cai trang") || lower.contains("cải trang") || lower.contains("avatar");
    }

    private boolean isSusanoItem(ItemData it) {
        if (it == null) return false;
        if (it.type == 11 && ((it.part >= 176 && it.part <= 185) || it.part == 211 || it.part == 205)) return true;
        String lower = it.name.toLowerCase();
        return lower.contains("thần thể") || lower.contains("than the") || lower.contains("susano") || lower.contains("hỏa thể") || lower.contains("ma thể") || lower.contains("viêm thể") || lower.contains("hộ thể");
    }

    private boolean isCanhItem(ItemData it) {
        if (it == null) return false;
        String lower = it.name.toLowerCase();
        return lower.contains("cánh") || lower.contains("canh") || lower.contains("dực") || lower.contains("duc");
    }

    private boolean isPetName(String name) {
        if (name == null) return false;
        String lower = name.toLowerCase();
        return lower.contains("pet") || lower.contains("linh thú") || lower.contains("linh thu") || lower.contains("thú cưng");
    }

    private boolean isMountName(String name) {
        if (name == null) return false;
        String lower = name.toLowerCase();
        return lower.contains("thú cưỡi") || lower.contains("thu cuoi") || lower.contains("cân đẩu vân") || lower.contains("phi long") || lower.contains("ván bay") || lower.contains("long vương") || lower.contains("kỳ lân");
    }

    private void initHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Color.WHITE);
        header.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(225, 225, 225)),
                new EmptyBorder(5, 5, 12, 5)
        ));

        JPanel titlePanel = new JPanel(new GridLayout(2, 1, 2, 2));
        titlePanel.setOpaque(false);

        JLabel lblTitle = new JLabel("Quan Tri & Buff Nguoi Choi (GM Tools)");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitle.setForeground(COL_PRIMARY);

        JLabel lblSub = new JLabel("Tang Quan Ao | Cai Trang | Susano & Canh | Vat Pham Game | Danh Hieu (Badges) | He Thong Su Kien & Qua SK | Buff Tien Te & Nhiem Vu");
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblSub.setForeground(new Color(110, 110, 110));

        titlePanel.add(lblTitle);
        titlePanel.add(lblSub);

        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        actionPanel.setOpaque(false);

        JButton btnRefresh = new JButton("Lam Moi DS");
        btnRefresh.setFont(FONT_BOLD);
        btnRefresh.setBackground(COL_HEADER);
        btnRefresh.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnRefresh.addActionListener(e -> loadPlayers(txtSearchPlayer.getText().trim()));

        actionPanel.add(btnRefresh);

        header.add(titlePanel, BorderLayout.WEST);
        header.add(actionPanel, BorderLayout.EAST);
        add(header, BorderLayout.NORTH);
    }

    private void initCenterSplit() {
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(380);
        splitPane.setContinuousLayout(true);
        splitPane.setBorder(null);

        // --- LEFT: Player list ---
        JPanel leftPanel = new JPanel(new BorderLayout(5, 5));
        leftPanel.setBackground(Color.WHITE);
        leftPanel.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(230, 230, 230), 1, true),
                new EmptyBorder(8, 8, 8, 8)
        ));

        JPanel searchContainer = new JPanel(new BorderLayout(5, 5));
        searchContainer.setBackground(Color.WHITE);

        txtSearchPlayer = new JTextField();
        txtSearchPlayer.setFont(FONT_UI);
        txtSearchPlayer.putClientProperty("JTextField.placeholderText", "Tim ten hoac ID nguoi choi...");
        txtSearchPlayer.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                String kw = txtSearchPlayer.getText().trim();
                if (sorter != null) {
                    if (kw.isEmpty()) {
                        sorter.setRowFilter(null);
                    } else {
                        sorter.setRowFilter(RowFilter.regexFilter("(?i)" + kw, 0, 1));
                    }
                }
            }
        });

        chkOnlyOnline = new JCheckBox("Online", true);
        chkOnlyOnline.setFont(FONT_UI);
        chkOnlyOnline.setBackground(Color.WHITE);
        chkOnlyOnline.addActionListener(e -> loadPlayers(txtSearchPlayer.getText().trim()));

        searchContainer.add(txtSearchPlayer, BorderLayout.CENTER);
        searchContainer.add(chkOnlyOnline, BorderLayout.EAST);

        String[] cols = {"ID", "Ten Nhan Vat", "Trang Thai", "Suc Manh"};
        playerModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        playerTable = new JTable(playerModel);
        playerTable.setFont(FONT_UI);
        playerTable.setRowHeight(28);
        playerTable.getTableHeader().setFont(FONT_BOLD);
        playerTable.getTableHeader().setBackground(COL_HEADER);
        playerTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        sorter = new TableRowSorter<>(playerModel);
        playerTable.setRowSorter(sorter);

        playerTable.getColumnModel().getColumn(0).setMaxWidth(60);
        playerTable.getColumnModel().getColumn(2).setMaxWidth(85);

        DefaultTableCellRenderer statusRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int col) {
                JLabel c = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);
                c.setHorizontalAlignment(CENTER);
                String val = String.valueOf(value);
                if ("Online".equalsIgnoreCase(val)) {
                    c.setForeground(new Color(40, 167, 69));
                    c.setText(" Online");
                } else {
                    c.setForeground(Color.GRAY);
                    c.setText(" Offline");
                }
                return c;
            }
        };
        playerTable.getColumnModel().getColumn(2).setCellRenderer(statusRenderer);

        playerTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int row = playerTable.getSelectedRow();
                if (row != -1) {
                    int modelRow = playerTable.convertRowIndexToModel(row);
                    int pId = (int) playerModel.getValueAt(modelRow, 0);
                    String pName = (String) playerModel.getValueAt(modelRow, 1);
                    selectPlayer(pId, pName);
                }
            }
        });

        JScrollPane scrollTable = new JScrollPane(playerTable);
        scrollTable.setBorder(new LineBorder(COL_BORDER));

        // Bottom info box
        lblSelectedPlayerInfo = new JLabel("<html><b>Chua chon nguoi choi nao</b></html>");
        lblSelectedPlayerInfo.setFont(FONT_UI);
        lblSelectedPlayerInfo.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(COL_BORDER, 1, true),
                new EmptyBorder(8, 8, 8, 8)
        ));

        leftPanel.add(searchContainer, BorderLayout.NORTH);
        leftPanel.add(scrollTable, BorderLayout.CENTER);
        leftPanel.add(lblSelectedPlayerInfo, BorderLayout.SOUTH);

        // --- RIGHT: 10 Modern Professional Tabs ---
        mainTabbedPane = new JTabbedPane();
        mainTabbedPane.setFont(new Font("Segoe UI", Font.BOLD, 13));
        mainTabbedPane.setBackground(new Color(248, 249, 250));
        mainTabbedPane.addTab("🐾 Tặng Pet & Thú Cưỡi", createGivePetTab());
        mainTabbedPane.addTab("🥋 Tặng Cải Trang (VIP)", createGiveCaiTrangTab());
        mainTabbedPane.addTab("🛡️ Tặng Trang Bị (VIP)", createGiveEquipTab());
        mainTabbedPane.addTab("🔥 Tặng Susanoo & Cánh", createGiveSusanoTab());
        mainTabbedPane.addTab("👑 Tặng Danh Hiệu", createGiveBadgesTab());
        mainTabbedPane.addTab("📜 Tặng Sách Kỹ Năng", createGiveSkillBookTab());
        mainTabbedPane.addTab("🎁 Tặng Vật Phẩm (Full)", createGiveItemTab());
        mainTabbedPane.addTab("💎 Buff Tiền Tệ & Chỉ Số", createBuffTab());
        mainTabbedPane.addTab("🎉 Hệ Thống Sự Kiện", createEventSystemTab());
        mainTabbedPane.addTab("🎯 Quản Lý Nhiệm Vụ", createTaskTab());

        splitPane.setLeftComponent(leftPanel);
        splitPane.setRightComponent(mainTabbedPane);
        add(splitPane, BorderLayout.CENTER);
    }

    private void loadPlayers(String keyword) {
        new Thread(() -> {
            List<Object[]> rows = new ArrayList<>();
            Set<Integer> loadedIds = new HashSet<>();

            // 1. Online players
            try {
                if (Client.gI() != null && Client.gI().getPlayers() != null) {
                    for (Player p : Client.gI().getPlayers()) {
                        if (p != null) {
                            if (!keyword.isEmpty() && !p.name.toLowerCase().contains(keyword.toLowerCase()) && !String.valueOf(p.id).equals(keyword)) {
                                continue;
                            }
                            rows.add(new Object[]{
                                    (int) p.id,
                                    p.name,
                                    "Online",
                                    p.nPoint != null ? Util.format(p.nPoint.power) : "0",
                                    p
                            });
                            loadedIds.add((int) p.id);
                        }
                    }
                }
            } catch (Exception ignored) {}

            // 2. Database players
            if (!chkOnlyOnline.isSelected()) {
                try (Connection conn = ConnectDB.getConnection(); Statement stmt = conn.createStatement()) {
                    String sql = "SELECT id, name, data_point FROM player ORDER BY id DESC LIMIT 200";
                    if (!keyword.isEmpty()) {
                        sql = "SELECT id, name, data_point FROM player WHERE name LIKE '%" + keyword + "%' OR id = '" + keyword + "' ORDER BY id DESC LIMIT 200";
                    }
                    try (ResultSet rs = stmt.executeQuery(sql)) {
                        while (rs.next()) {
                            int id = rs.getInt("id");
                            if (loadedIds.contains(id)) continue;
                            String name = rs.getString("name");
                            String dp = rs.getString("data_point");
                            long power = 0;
                            try {
                                if (dp != null && dp.startsWith("[")) {
                                    JsonArray arr = new JsonParser().parse(dp).getAsJsonArray();
                                    if (arr.size() > 1) power = arr.get(1).getAsLong();
                                }
                            } catch (Exception ignored) {}

                            rows.add(new Object[]{
                                    id,
                                    name,
                                    "Offline",
                                    Util.format(power),
                                    null
                            });
                        }
                    }
                } catch (Exception e) {
                    Logger.logException(PlayerBuffManagerPanel.class, e, "Loi loadPlayers tu DB");
                }
            }

            SwingUtilities.invokeLater(() -> {
                playerModel.setRowCount(0);
                for (Object[] r : rows) {
                    playerModel.addRow(new Object[]{r[0], r[1], r[2], r[3]});
                }
                if (currentSelectedPlayerId != -1) {
                    for (int i = 0; i < playerModel.getRowCount(); i++) {
                        if ((int) playerModel.getValueAt(i, 0) == currentSelectedPlayerId) {
                            playerTable.setRowSelectionInterval(i, i);
                            break;
                        }
                    }
                }
            });
        }).start();
    }

    private void selectPlayer(int pId, String pName) {
        this.currentSelectedPlayerId = pId;
        this.currentSelectedPlayerName = pName;

        Player plOnline = Client.gI() != null ? Client.gI().getPlayerByName(pName) : null;
        if (plOnline != null) {
            this.currentSelectedPlayer = plOnline;
            updatePlayerSummary(plOnline);
        } else {
            new Thread(() -> {
                Player pl = GodGK.loadById(pId);
                if (pl == null) {
                    pl = GodGK.loadPlayerByName(pName);
                }
                final Player finalPl = pl;
                SwingUtilities.invokeLater(() -> {
                    this.currentSelectedPlayer = finalPl;
                    updatePlayerSummary(finalPl);
                });
            }).start();
        }
    }

    private void updatePlayerSummary(Player p) {
        if (p == null) {
            lblSelectedPlayerInfo.setText("<html><b>Khong tim thay du lieu nguoi choi!</b></html>");
            return;
        }
        String status = (p.getSession() != null) ? "<font color='green'><b>[ONLINE]</b></font>" : "<font color='gray'><b>[OFFLINE]</b></font>";
        long gold = p.inventory != null ? p.inventory.gold : 0;
        int gem = p.inventory != null ? p.inventory.gem : 0;
        int ruby = p.inventory != null ? p.inventory.ruby : 0;
        long power = p.nPoint != null ? p.nPoint.power : 0;
        long tiemNang = p.nPoint != null ? p.nPoint.tiemNang : 0;
        String genderStr = p.gender == 0 ? "Trai Dat" : (p.gender == 1 ? "Namec" : "Xayda");
        String petStr = (p.Detu != null) ? "Co (SM: " + Util.format(p.Detu.nPoint != null ? p.Detu.nPoint.power : 0) + ")" : "Chua co";
        String taskName = "Chua co";
        if (p.playerTask != null && p.playerTask.taskMain != null) {
            taskName = "[" + p.playerTask.taskMain.id + "] " + p.playerTask.taskMain.name;
        }

        String html = "<html>"
                + "<b>Nhan vat:</b> <font color='#0078D7'>" + p.name + "</font> (ID: " + p.id + ") " + status + "<br>"
                + "<b>Hanh tinh:</b> " + genderStr + " | <b>De tu:</b> " + petStr + "<br>"
                + "<b>Suc manh:</b> " + Util.format(power) + " | <b>TN:</b> " + Util.format(tiemNang) + "<br>"
                + "<b>Vang:</b> " + Util.format(gold) + " | <b>Ngoc:</b> " + Util.format(gem) + " | <b>Ruby (KC):</b> " + Util.format(ruby) + "<br>"
                + "<b>Nhiem vu:</b> " + taskName
                + "</html>";
        lblSelectedPlayerInfo.setText(html);
    }

    private Player checkAndGetTargetPlayer() {
        if (currentSelectedPlayer != null) {
            if (currentSelectedPlayer.getSession() != null && Client.gI() != null) {
                Player live = Client.gI().getPlayerByName(currentSelectedPlayer.name);
                if (live != null) currentSelectedPlayer = live;
            }
            return currentSelectedPlayer;
        }
        if (currentSelectedPlayerId != -1) {
            Player p = Client.gI() != null ? Client.gI().getPlayerByName(currentSelectedPlayerName) : null;
            if (p != null) {
                currentSelectedPlayer = p;
                return p;
            }
            p = GodGK.loadById(currentSelectedPlayerId);
            if (p != null) {
                currentSelectedPlayer = p;
                return p;
            }
        }
        String input = JOptionPane.showInputDialog(this, "Chua chon nguoi choi ben danh sach!\nVui long nhap Ten hoac ID nguoi choi can thao tac:", "Chon muc tieu", JOptionPane.QUESTION_MESSAGE);
        if (input != null && !input.trim().isEmpty()) {
            input = input.trim();
            Player p = Client.gI() != null ? Client.gI().getPlayerByName(input) : null;
            if (p == null) {
                try {
                    int pid = Integer.parseInt(input);
                    if (Client.gI() != null) p = Client.gI().getPlayerByID(pid);
                    if (p == null) p = GodGK.loadById(pid);
                } catch (Exception ignored) {
                    p = GodGK.loadPlayerByName(input);
                }
            }
            if (p != null) {
                selectPlayer((int) p.id, p.name);
                return p;
            } else {
                JOptionPane.showMessageDialog(this, "Khong tim thay nguoi choi: " + input, "Loi", JOptionPane.ERROR_MESSAGE);
            }
        }
        return null;
    }

    private void savePlayer(Player player) {
        if (player == null) return;
        try {
            PlayerDAO.updatePlayer(player);
        } catch (Exception e) {
            Logger.logException(PlayerBuffManagerPanel.class, e, "Loi luu PlayerDAO");
        }
    }

    private boolean deliverItemToPlayer(Player p, Item item, int destinationIndex) {
        return deliverItemToPlayer(p, item, destinationIndex, true);
    }

    private boolean deliverItemToPlayer(Player p, Item item, int destinationIndex, boolean autoSave) {
        if (p == null || item == null) return false;
        if (item.createTime <= 0) {
            item.createTime = System.currentTimeMillis();
        }
        boolean ok = false;
        if (destinationIndex == 1) {
            // Rương đồ (Box)
            ok = InventoryService.gI().addItemBox(p, item);
            if (ok && p.getSession() != null) {
                InventoryService.gI().sendItemBox(p);
            }
        } else if (destinationIndex == 2) {
            // Hòm thư
            if (p.inventory.itemsMailBox == null) {
                p.inventory.itemsMailBox = new ArrayList<>();
            }
            p.inventory.itemsMailBox.add(item);
            ok = true;
        } else {
            // Mặc định: Hành trang (Bag)
            ok = InventoryService.gI().addItemBag(p, item);
            if (ok && p.getSession() != null) {
                InventoryService.gI().sendItemBag(p);
            }
        }

        if (ok && autoSave) {
            savePlayer(p);
        }
        return ok;
    }

    // ==========================================
    // TAB 1: TANG QUAN AO & SET DO (VIP, FULL SAO)
    // ==========================================
    private void setSelectedEquip(ItemData item) {
        this.currentSelectedEquip = item;
        if (item == null) {
            lblEquipTitle.setText("Chua chon trang bi");
            lblEquipDesc.setText("");
            lblEquipIconPreview.setIcon(null);
            txtEquipId.setText("");
            return;
        }

        txtEquipId.setText(String.valueOf(item.id));
        lblEquipTitle.setText(item.name + " (ID: " + item.id + ")");
        String typeName = getTypeName(item.type);
        String genderName = item.gender == 0 ? "Trai Dat" : (item.gender == 1 ? "Namec" : (item.gender == 2 ? "Xayda" : "Tat ca"));
        lblEquipDesc.setText("<html><b>Loai:</b> " + typeName + " | <b>Gioi tinh:</b> " + genderName + " | <b>Cap do:</b> " + item.level + "<br><i>" + (item.description.isEmpty() ? "Trang bi ngoc rong" : item.description) + "</i></html>");

        ImageIcon icon = getItemIcon(item.iconId, 48);
        lblEquipIconPreview.setIcon(icon);
    }

    private JPanel createGiveEquipTab() {
        JPanel panel = new JPanel(new BorderLayout(12, 12));
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(12, 12, 12, 12));

        // --- TOP: Combo Tặng Trọn Bộ Set (Thường, Thần Linh, Hủy Diệt, Thiên Sứ) ---
        JPanel topComboPanel = new JPanel(new BorderLayout(8, 8));
        topComboPanel.setBackground(new Color(245, 248, 255));
        topComboPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(new LineBorder(COL_PRIMARY, 1, true), "1-Click Tang Tron Bo Set 5 Mon (Ao, Quan, Gang, Giay, Nhan/Rada)"),
                new EmptyBorder(6, 8, 6, 8)
        ));

        JPanel pnlGenderChoice = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        pnlGenderChoice.setOpaque(false);
        JComboBox<String> cbPlanetChoice = new JComboBox<>(new String[]{
                "Tu dong theo Hanh tinh cua Nguoi choi",
                "Hanh tinh Trai Dat",
                "Hanh tinh Namec",
                "Hanh tinh Xayda"
        });
        cbPlanetChoice.setFont(FONT_BOLD);
        pnlGenderChoice.add(new JLabel("Hanh tinh nhan:"));
        pnlGenderChoice.add(cbPlanetChoice);

        JPanel pnlSetButtons = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        pnlSetButtons.setOpaque(false);

        JButton btnGiveSetThan = new JButton("Tang Set Than Linh (Cap 13)");
        btnGiveSetThan.setFont(FONT_BOLD);
        btnGiveSetThan.setBackground(COL_PRIMARY);
        btnGiveSetThan.setForeground(Color.WHITE);
        btnGiveSetThan.addActionListener(e -> giveEquipmentSet(0, cbPlanetChoice.getSelectedIndex()));

        JButton btnGiveSetHuyDiet = new JButton("Tang Set Huy Diet (Cap 14 - VIP)");
        btnGiveSetHuyDiet.setFont(FONT_BOLD);
        btnGiveSetHuyDiet.setBackground(COL_WARNING);
        btnGiveSetHuyDiet.setForeground(Color.WHITE);
        btnGiveSetHuyDiet.addActionListener(e -> giveEquipmentSet(1, cbPlanetChoice.getSelectedIndex()));

        JButton btnGiveSetThienSu = new JButton("Tang Set Thien Than / Thien Su (Cap 15 - Sieu VIP)");
        btnGiveSetThienSu.setFont(FONT_BOLD);
        btnGiveSetThienSu.setBackground(COL_PURPLE);
        btnGiveSetThienSu.setForeground(Color.WHITE);
        btnGiveSetThienSu.addActionListener(e -> giveEquipmentSet(2, cbPlanetChoice.getSelectedIndex()));

        JButton btnGiveSetThienTu = new JButton("Tang Tron Bo Chan Thien Tu (2002 - 2010)");
        btnGiveSetThienTu.setFont(FONT_BOLD);
        btnGiveSetThienTu.setBackground(new Color(218, 165, 32));
        btnGiveSetThienTu.setForeground(Color.WHITE);
        btnGiveSetThienTu.addActionListener(e -> giveEquipmentSet(4, cbPlanetChoice.getSelectedIndex()));

        JButton btnGiveSetThuong = new JButton("Tang Set Thuong C7");
        btnGiveSetThuong.setFont(FONT_BOLD);
        btnGiveSetThuong.setBackground(new Color(100, 110, 120));
        btnGiveSetThuong.setForeground(Color.WHITE);
        btnGiveSetThuong.addActionListener(e -> giveEquipmentSet(3, cbPlanetChoice.getSelectedIndex()));

        pnlSetButtons.add(btnGiveSetThan);
        pnlSetButtons.add(btnGiveSetHuyDiet);
        pnlSetButtons.add(btnGiveSetThienSu);
        pnlSetButtons.add(btnGiveSetThienTu);
        pnlSetButtons.add(btnGiveSetThuong);

        topComboPanel.add(pnlGenderChoice, BorderLayout.NORTH);
        topComboPanel.add(pnlSetButtons, BorderLayout.CENTER);

        // --- CENTER: Visual Card & Single Equip Form ---
        JPanel centerContainer = new JPanel(new BorderLayout(10, 10));
        centerContainer.setBackground(Color.WHITE);

        // Visual Preview Card
        JPanel card = new JPanel(new BorderLayout(12, 12));
        card.setBackground(new Color(250, 252, 255));
        card.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(COL_BORDER, 1, true),
                new EmptyBorder(10, 12, 10, 12)
        ));

        lblEquipIconPreview = new JLabel();
        lblEquipIconPreview.setPreferredSize(new Dimension(56, 56));
        lblEquipIconPreview.setHorizontalAlignment(JLabel.CENTER);
        lblEquipIconPreview.setBorder(new LineBorder(COL_BORDER, 1, true));
        lblEquipIconPreview.setBackground(Color.WHITE);
        lblEquipIconPreview.setOpaque(true);

        JPanel cardInfo = new JPanel(new GridLayout(2, 1, 3, 3));
        cardInfo.setOpaque(false);
        lblEquipTitle = new JLabel("Chua chon trang bi");
        lblEquipTitle.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblEquipTitle.setForeground(COL_PRIMARY);

        lblEquipDesc = new JLabel("Vui long chon mon do hoac click Duyet Tat Ca...");
        lblEquipDesc.setFont(FONT_UI);
        cardInfo.add(lblEquipTitle);
        cardInfo.add(lblEquipDesc);

        JButton btnBrowseEquip = new JButton("Duyet Thu Vien Quan Ao (Kem Anh)");
        btnBrowseEquip.setFont(FONT_BOLD);
        btnBrowseEquip.setBackground(COL_PRIMARY);
        btnBrowseEquip.setForeground(Color.WHITE);
        btnBrowseEquip.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnBrowseEquip.addActionListener(e -> openItemPickerDialog(2));

        card.add(lblEquipIconPreview, BorderLayout.WEST);
        card.add(cardInfo, BorderLayout.CENTER);
        card.add(btnBrowseEquip, BorderLayout.EAST);

        // Quick Pick Single Equipment
        JPanel quickPickPnl = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 4));
        quickPickPnl.setBackground(Color.WHITE);
        quickPickPnl.setBorder(new TitledBorder(new LineBorder(COL_BORDER), "Chon nhanh mon do mau:"));

        int[] quickEquipIds = {
                555, 556, 562, 563, 561, // Set Thần TĐ
                650, 651, 657, 658, 656, // Set Hủy diệt TĐ
                1048, 1051, 1054, 1057, 1060, // Set Thiên sứ / Thiên thần TĐ
                2002, 2007, 2008, 2009, 2010 // Chân Thiên Tử các cấp
        };
        for (int qId : quickEquipIds) {
            JButton b = new JButton();
            b.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            b.setMargin(new Insets(2, 6, 2, 6));
            if (itemMap.containsKey(qId)) {
                ItemData it = itemMap.get(qId);
                b.setText(it.name);
                b.setIcon(getItemIcon(it.iconId, 18));
            } else {
                b.setText("ID " + qId);
            }
            b.addActionListener(e -> {
                if (itemMap.containsKey(qId)) setSelectedEquip(itemMap.get(qId));
                else setSelectedEquip(new ItemData(qId, "Item " + qId, 0, 0, 0, 0, 0, ""));
            });
            quickPickPnl.add(b);
        }

        // Form settings
        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(Color.WHITE);
        form.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(new LineBorder(COL_BORDER), "Cau Hinh Chi So, Cap Sao & Tang Mon Nay"),
                new EmptyBorder(8, 10, 8, 10)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 6, 5, 6);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        txtEquipId = new JTextField(8);
        txtEquipId.setFont(FONT_BOLD);
        txtEquipId.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                try {
                    int id = Integer.parseInt(txtEquipId.getText().trim());
                    if (itemMap.containsKey(id)) setSelectedEquip(itemMap.get(id));
                } catch (Exception ignored) {}
            }
        });

        cbEquipDestination = new JComboBox<>(new String[]{
                "Hanh trang (Tui do)",
                "Ruong do (Ruong chua / Box)",
                "Hom thu (Hop qua)"
        });
        cbEquipDestination.setFont(FONT_BOLD);

        cbEquipUpgrade = new JComboBox<>(new String[]{
                "Khong nang cap (+0)",
                "Nang cap +1",
                "Nang cap +2",
                "Nang cap +3",
                "Nang cap +4",
                "Nang cap +5",
                "Nang cap +6",
                "Nang cap +7",
                "Nang cap +8 (Max Cap)"
        });
        cbEquipUpgrade.setFont(FONT_BOLD);

        spEquipStar = new JSpinner(new SpinnerNumberModel(8, 0, 8, 1));
        spEquipStar.setFont(FONT_BOLD);

        chkEquipFullStar = new JCheckBox("Ep San Full 8 Sao Pha Le VIP (+30% SD, HP, KI, Hut Mau, Ne Don)", true);
        chkEquipFullStar.setFont(FONT_BOLD);
        chkEquipFullStar.setForeground(COL_PRIMARY);
        chkEquipFullStar.setBackground(Color.WHITE);

        txtEquipCustomOptions = new JTextField("50-30,77-30", 25);
        txtEquipCustomOptions.setFont(FONT_UI);

        chkEquipLock = new JCheckBox("Khoa giao dich", false);
        chkEquipLock.setBackground(Color.WHITE);

        spEquipHsd = new JSpinner(new SpinnerNumberModel(0, 0, 9999, 1));
        spEquipHsd.setPreferredSize(new Dimension(80, 26));

        JButton btnGiveSingleEquip = new JButton("TANG TRANG BI NAY CHO NGUOI CHOI");
        btnGiveSingleEquip.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnGiveSingleEquip.setBackground(COL_SUCCESS);
        btnGiveSingleEquip.setForeground(Color.WHITE);
        btnGiveSingleEquip.setPreferredSize(new Dimension(360, 42));
        btnGiveSingleEquip.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnGiveSingleEquip.addActionListener(e -> giveSingleEquipment());

        // Row 0: ID & Dest
        gbc.gridx = 0; gbc.gridy = 0; form.add(new JLabel("ID Trang bi:"), gbc);
        gbc.gridx = 1; gbc.gridy = 0; form.add(txtEquipId, gbc);
        gbc.gridx = 2; gbc.gridy = 0; form.add(new JLabel("Noi nhan:"), gbc);
        gbc.gridx = 3; gbc.gridy = 0; form.add(cbEquipDestination, gbc);

        // Row 1: Cấp nâng cấp & Số sao
        gbc.gridx = 0; gbc.gridy = 1; form.add(new JLabel("Cap nang cap:"), gbc);
        gbc.gridx = 1; gbc.gridy = 1; form.add(cbEquipUpgrade, gbc);
        gbc.gridx = 2; gbc.gridy = 1; form.add(new JLabel("So sao pha le (0-8):"), gbc);
        gbc.gridx = 3; gbc.gridy = 1; form.add(spEquipStar, gbc);

        // Row 2: Full Sao VIP Checkbox
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 4;
        form.add(chkEquipFullStar, gbc);

        // Row 3: Custom Options
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 1; form.add(new JLabel("Option tuy chinh:"), gbc);
        gbc.gridx = 1; gbc.gridy = 3; gbc.gridwidth = 3; form.add(txtEquipCustomOptions, gbc);

        // Row 4: Khóa & HSD
        JPanel pnlExtra = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        pnlExtra.setOpaque(false);
        pnlExtra.add(chkEquipLock);
        pnlExtra.add(new JLabel("HSD (ngay, 0 = VV):"));
        pnlExtra.add(spEquipHsd);
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 4; form.add(pnlExtra, gbc);

        // Row 5: Submit Button
        gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 4; gbc.insets = new Insets(12, 6, 6, 6);
        form.add(btnGiveSingleEquip, gbc);

        JPanel centerWrap = new JPanel(new BorderLayout(8, 8));
        centerWrap.setBackground(Color.WHITE);
        centerWrap.add(card, BorderLayout.NORTH);
        centerWrap.add(quickPickPnl, BorderLayout.CENTER);
        centerWrap.add(form, BorderLayout.SOUTH);

        panel.add(topComboPanel, BorderLayout.NORTH);
        panel.add(new JScrollPane(centerWrap), BorderLayout.CENTER);
        return panel;
    }

    private void giveEquipmentSet(int setType, int planetChoice) {
        Player p = checkAndGetTargetPlayer();
        if (p == null) return;

        // Determine planet (0: Trái Đất, 1: Namec, 2: Xayda)
        int gender = p.gender;
        if (planetChoice == 1) gender = 0;
        else if (planetChoice == 2) gender = 1;
        else if (planetChoice == 3) gender = 2;

        int[] itemIds;
        String setName;

        if (setType == 0) {
            // Set Thần Linh
            setName = "Set Than Linh";
            if (gender == 0) itemIds = new int[]{555, 556, 562, 563, 561};
            else if (gender == 1) itemIds = new int[]{557, 558, 564, 565, 561};
            else itemIds = new int[]{559, 560, 566, 567, 561};
        } else if (setType == 1) {
            // Set Hủy Diệt (VIP)
            setName = "Set Huy Diet (VIP)";
            if (gender == 0) itemIds = new int[]{650, 651, 657, 658, 656};
            else if (gender == 1) itemIds = new int[]{652, 653, 659, 660, 656};
            else itemIds = new int[]{654, 655, 661, 662, 656};
        } else if (setType == 2) {
            // Set Thiên Sứ / Thiên Thần (Siêu VIP)
            setName = "Set Thien Than / Thien Su (Sieu VIP)";
            if (gender == 0) itemIds = new int[]{1048, 1051, 1054, 1057, 1060};
            else if (gender == 1) itemIds = new int[]{1049, 1052, 1055, 1058, 1061};
            else itemIds = new int[]{1050, 1053, 1056, 1059, 1062};
        } else if (setType == 4) {
            // Set Chân Thiên Tử (2002 - 2010)
            setName = "Set Chan Thien Tu (2002 - 2010)";
            itemIds = new int[]{2002, 2003, 2004, 2005, 2006, 2007, 2008, 2009, 2010};
        } else {
            // Set Thường C7
            setName = "Set Thuong C7";
            if (gender == 0) itemIds = new int[]{48, 51, 54, 57, 68};
            else if (gender == 1) itemIds = new int[]{49, 52, 55, 58, 68};
            else itemIds = new int[]{50, 53, 56, 59, 68};
        }

        int dest = cbEquipDestination.getSelectedIndex();
        int upgradeLevel = cbEquipUpgrade.getSelectedIndex();
        int starCount = (int) spEquipStar.getValue();
        boolean fullStar = chkEquipFullStar.isSelected();

        int successCount = 0;
        for (int id : itemIds) {
            Item item = ItemService.gI().createNewItem((short) id, 1);
            List<ItemOption> baseOps = ItemService.gI().getListOptionItemShop((short) id);
            if (baseOps != null && !baseOps.isEmpty()) {
                item.itemOptions.addAll(baseOps);
            }
            applyEquipOptions(item, upgradeLevel, starCount, fullStar, txtEquipCustomOptions.getText().trim(), chkEquipLock.isSelected(), (int) spEquipHsd.getValue());
            if (deliverItemToPlayer(p, item, dest, false)) {
                successCount++;
            }
        }
        if (successCount > 0) {
            savePlayer(p);
        }

        String destName = dest == 1 ? "Ruong do (Box)" : (dest == 2 ? "Hom thu" : "Hanh trang");
        if (p.getSession() != null) {
            Service.gI().sendThongBao(p, "Ban vua nhan duoc Tron bo " + setName + " tu Admin!");
        }
        JOptionPane.showMessageDialog(this, "Da gui tron bo " + setName + " vao [" + destName + "] cua [" + p.name + "] thanh cong!\n(Thanh cong: " + successCount + "/" + itemIds.length + ")", "Thanh cong", JOptionPane.INFORMATION_MESSAGE);
        updatePlayerSummary(p);
    }

    private void giveSingleEquipment() {
        Player p = checkAndGetTargetPlayer();
        if (p == null) return;

        if (currentSelectedEquip == null) {
            JOptionPane.showMessageDialog(this, "Vui long chon mot mon trang bi truoc!", "Thong bao", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int dest = cbEquipDestination.getSelectedIndex();
        int upgradeLevel = cbEquipUpgrade.getSelectedIndex();
        int starCount = (int) spEquipStar.getValue();
        boolean fullStar = chkEquipFullStar.isSelected();

        Item item = ItemService.gI().createNewItem((short) currentSelectedEquip.id, 1);
        List<ItemOption> baseOps = ItemService.gI().getListOptionItemShop((short) currentSelectedEquip.id);
        if (baseOps != null && !baseOps.isEmpty()) {
            item.itemOptions.addAll(baseOps);
        }
        applyEquipOptions(item, upgradeLevel, starCount, fullStar, txtEquipCustomOptions.getText().trim(), chkEquipLock.isSelected(), (int) spEquipHsd.getValue());

        boolean ok = deliverItemToPlayer(p, item, dest);
        String destName = dest == 1 ? "Ruong do (Box)" : (dest == 2 ? "Hom thu" : "Hanh trang");
        if (ok) {
            if (p.getSession() != null) Service.gI().sendThongBao(p, "Ban nhan duoc trang bi " + currentSelectedEquip.name + " tu Admin!");
            JOptionPane.showMessageDialog(this, "Da gui [" + currentSelectedEquip.name + "] vao [" + destName + "] cua [" + p.name + "] thanh cong!");
            updatePlayerSummary(p);
        } else {
            JOptionPane.showMessageDialog(this, destName + " cua nguoi choi da day!", "Loi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void applyEquipOptions(Item item, int upgradeLevel, int starCount, boolean fullStar, String customOptStr, boolean isLock, int hsd) {
        if (upgradeLevel > 0) {
            item.itemOptions.add(new ItemOption(72, upgradeLevel)); // Cấp +1 đến +8
        }
        if (starCount > 0) {
            item.itemOptions.add(new ItemOption(107, starCount)); // Số sao pha lê chưa ép
        }
        if (fullStar) {
            item.itemOptions.add(new ItemOption(102, 8)); // 8 Sao Pha Lê đã ép
            item.itemOptions.add(new ItemOption(50, 30)); // +30% Sức đánh
            item.itemOptions.add(new ItemOption(77, 30)); // +30% HP
            item.itemOptions.add(new ItemOption(103, 30)); // +30% KI
            item.itemOptions.add(new ItemOption(14, 10)); // +10% Chí mạng
            item.itemOptions.add(new ItemOption(95, 10)); // +10% Hút máu
            item.itemOptions.add(new ItemOption(96, 10)); // +10% Hút KI
            item.itemOptions.add(new ItemOption(108, 10)); // +10% Né đòn
        }
        if (customOptStr != null && !customOptStr.isEmpty()) {
            String[] parts = customOptStr.split("[,;]");
            for (String part : parts) {
                String[] p2 = part.trim().split("[-:]");
                if (p2.length == 2) {
                    try {
                        item.itemOptions.add(new ItemOption(Integer.parseInt(p2[0].trim()), Integer.parseInt(p2[1].trim())));
                    } catch (Exception ignored) {}
                }
            }
        }
        if (isLock) item.itemOptions.add(new ItemOption(30, 0));
        if (hsd > 0) item.itemOptions.add(new ItemOption(93, hsd));
    }

    // ==========================================
    // TAB 2: TANG CAI TRANG (VIP)
    // ==========================================
    private void setSelectedCaiTrang(ItemData ct) {
        this.currentSelectedCaiTrang = ct;
        if (ct == null) {
            lblCtTitle.setText("Chua chon cai trang");
            lblCtDesc.setText("");
            lblCtIconPreview.setIcon(null);
            txtCtId.setText("");
            return;
        }

        txtCtId.setText(String.valueOf(ct.id));
        lblCtTitle.setText(ct.name + " (ID: " + ct.id + ")");
        lblCtDesc.setText("<html><b>Loai:</b> Cai Trang (Avatar) | <b>Gioi tinh:</b> " + (ct.gender == 0 ? "Trai Dat" : (ct.gender == 1 ? "Namec" : (ct.gender == 2 ? "Xayda" : "Tat ca"))) + "<br><i>" + (ct.description.isEmpty() ? "Cai trang VIP" : ct.description) + "</i></html>");

        ImageIcon icon = getItemIcon(ct.iconId, 48);
        lblCtIconPreview.setIcon(icon);

        if (cbCaiTrang != null && !isUpdatingCbCaiTrang) {
            isUpdatingCbCaiTrang = true;
            for (int i = 0; i < cbCaiTrang.getItemCount(); i++) {
                if (cbCaiTrang.getItemAt(i).id == ct.id) {
                    cbCaiTrang.setSelectedIndex(i);
                    break;
                }
            }
            isUpdatingCbCaiTrang = false;
        }
    }

    private JPanel createGiveCaiTrangTab() {
        JPanel panel = new JPanel(new BorderLayout(12, 12));
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(12, 12, 12, 12));

        // Top Visual Card
        JPanel card = new JPanel(new BorderLayout(12, 12));
        card.setBackground(new Color(253, 248, 255));
        card.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(COL_BORDER, 1, true),
                new EmptyBorder(12, 15, 12, 15)
        ));

        lblCtIconPreview = new JLabel();
        lblCtIconPreview.setPreferredSize(new Dimension(56, 56));
        lblCtIconPreview.setHorizontalAlignment(JLabel.CENTER);
        lblCtIconPreview.setBorder(new LineBorder(COL_BORDER, 1, true));
        lblCtIconPreview.setBackground(Color.WHITE);
        lblCtIconPreview.setOpaque(true);

        JPanel cardInfo = new JPanel(new GridLayout(2, 1, 4, 4));
        cardInfo.setOpaque(false);
        lblCtTitle = new JLabel("Chua chon cai trang");
        lblCtTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblCtTitle.setForeground(COL_PURPLE);

        lblCtDesc = new JLabel("Vui long chon cai trang tu danh sach hoac click Duyet Tat Ca...");
        lblCtDesc.setFont(FONT_UI);
        cardInfo.add(lblCtTitle);
        cardInfo.add(lblCtDesc);

        JButton btnOpenPicker = new JButton("Duyet Thu Vien Cai Trang (Kem Anh)");
        btnOpenPicker.setFont(FONT_BOLD);
        btnOpenPicker.setBackground(COL_PURPLE);
        btnOpenPicker.setForeground(Color.WHITE);
        btnOpenPicker.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnOpenPicker.addActionListener(e -> openItemPickerDialog(1));

        card.add(lblCtIconPreview, BorderLayout.WEST);
        card.add(cardInfo, BorderLayout.CENTER);
        card.add(btnOpenPicker, BorderLayout.EAST);

        // Center Form
        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(Color.WHITE);
        form.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(new LineBorder(COL_BORDER), "Thiet Lap Goi Chi So & Tang Cai Trang"),
                new EmptyBorder(10, 15, 10, 15)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 8, 6, 8);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        cbCaiTrang = new JComboBox<>();
        cbCaiTrang.setRenderer(new ItemListCellRenderer(24));
        cbCaiTrang.setFont(FONT_UI);
        cbCaiTrang.setPreferredSize(new Dimension(300, 32));
        cbCaiTrang.addActionListener(e -> {
            if (!isUpdatingCbCaiTrang) {
                ItemData sel = (ItemData) cbCaiTrang.getSelectedItem();
                if (sel != null) setSelectedCaiTrang(sel);
            }
        });

        txtCtId = new JTextField(8);
        txtCtId.setFont(FONT_BOLD);
        txtCtId.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                try {
                    int id = Integer.parseInt(txtCtId.getText().trim());
                    if (itemMap.containsKey(id)) {
                        setSelectedCaiTrang(itemMap.get(id));
                    }
                } catch (Exception ignored) {}
            }
        });

        cbCtDestination = new JComboBox<>(new String[]{
                "Hanh trang (Tui do)",
                "Ruong do (Ruong chua / Box)",
                "Hom thu (Hop qua)"
        });
        cbCtDestination.setFont(FONT_BOLD);

        cbCtOptionPreset = new JComboBox<>(new String[]{
                "Mac dinh cua Cai Trang (Goc)",
                "Goi Tan Thu (+30% SD, +30% HP, +30% KI, +10% Crit)",
                "Goi VIP 1 (+50% SD, +50% HP, +50% KI, +15% Crit, +10% Hut Mau/KI)",
                "Goi VIP Pro (+80% SD, +80% HP, +80% KI, +20% Crit, +15% Hut Mau/KI)",
                "Goi Sieu VIP (+150% SD, +150% HP, +150% KI, +30% Crit, +20% Hut Mau/KI)",
                "Chi so Custom tuy chinh ben duoi"
        });
        cbCtOptionPreset.setFont(FONT_BOLD);

        txtCtCustomOptions = new JTextField("50-50,77-50,103-50,14-15", 25);
        txtCtCustomOptions.setFont(FONT_UI);

        chkCtLock = new JCheckBox("Khoa giao dich", false);
        chkCtLock.setBackground(Color.WHITE);

        spCtHsd = new JSpinner(new SpinnerNumberModel(0, 0, 9999, 1));
        spCtHsd.setPreferredSize(new Dimension(80, 26));

        JButton btnGiveCt = new JButton("TANG CAI TRANG NAY CHO NGUOI CHOI");
        btnGiveCt.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnGiveCt.setBackground(COL_SUCCESS);
        btnGiveCt.setForeground(Color.WHITE);
        btnGiveCt.setPreferredSize(new Dimension(360, 42));
        btnGiveCt.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnGiveCt.addActionListener(e -> {
            Player p = checkAndGetTargetPlayer();
            if (p == null) return;
            if (currentSelectedCaiTrang == null) {
                JOptionPane.showMessageDialog(this, "Vui long chon mot cai trang!", "Thong bao", JOptionPane.WARNING_MESSAGE);
                return;
            }

            int dest = cbCtDestination.getSelectedIndex();
            int preset = cbCtOptionPreset.getSelectedIndex();
            int hsd = (int) spCtHsd.getValue();

            Item ctItem = ItemService.gI().createNewItem((short) currentSelectedCaiTrang.id, 1);
            if (preset == 1) {
                ctItem.itemOptions.add(new ItemOption(50, 30));
                ctItem.itemOptions.add(new ItemOption(77, 30));
                ctItem.itemOptions.add(new ItemOption(103, 30));
                ctItem.itemOptions.add(new ItemOption(14, 10));
            } else if (preset == 2) {
                ctItem.itemOptions.add(new ItemOption(50, 50));
                ctItem.itemOptions.add(new ItemOption(77, 50));
                ctItem.itemOptions.add(new ItemOption(103, 50));
                ctItem.itemOptions.add(new ItemOption(14, 15));
                ctItem.itemOptions.add(new ItemOption(95, 10));
                ctItem.itemOptions.add(new ItemOption(96, 10));
            } else if (preset == 3) {
                ctItem.itemOptions.add(new ItemOption(50, 80));
                ctItem.itemOptions.add(new ItemOption(77, 80));
                ctItem.itemOptions.add(new ItemOption(103, 80));
                ctItem.itemOptions.add(new ItemOption(14, 20));
                ctItem.itemOptions.add(new ItemOption(95, 15));
                ctItem.itemOptions.add(new ItemOption(96, 15));
            } else if (preset == 4) {
                ctItem.itemOptions.add(new ItemOption(50, 150));
                ctItem.itemOptions.add(new ItemOption(77, 150));
                ctItem.itemOptions.add(new ItemOption(103, 150));
                ctItem.itemOptions.add(new ItemOption(14, 30));
                ctItem.itemOptions.add(new ItemOption(95, 20));
                ctItem.itemOptions.add(new ItemOption(96, 20));
            } else if (preset == 5) {
                String optStr = txtCtCustomOptions.getText().trim();
                if (!optStr.isEmpty()) {
                    for (String part : optStr.split("[,;]")) {
                        String[] p2 = part.trim().split("[-:]");
                        if (p2.length == 2) {
                            try {
                                ctItem.itemOptions.add(new ItemOption(Integer.parseInt(p2[0].trim()), Integer.parseInt(p2[1].trim())));
                            } catch (Exception ignored) {}
                        }
                    }
                }
            }

            if (chkCtLock.isSelected()) ctItem.itemOptions.add(new ItemOption(30, 0));
            if (hsd > 0) ctItem.itemOptions.add(new ItemOption(93, hsd));

            boolean ok = deliverItemToPlayer(p, ctItem, dest);
            String destName = dest == 1 ? "Ruong do (Box)" : (dest == 2 ? "Hom thu" : "Hanh trang");
            if (ok) {
                if (p.getSession() != null) Service.gI().sendThongBao(p, "Ban nhan duoc cai trang " + currentSelectedCaiTrang.name + " tu Admin!");
                JOptionPane.showMessageDialog(this, "Da tang [" + currentSelectedCaiTrang.name + "] vao [" + destName + "] cua [" + p.name + "] thanh cong!");
                updatePlayerSummary(p);
            } else {
                JOptionPane.showMessageDialog(this, destName + " cua nguoi choi da day!", "Loi", JOptionPane.ERROR_MESSAGE);
            }
        });

        gbc.gridx = 0; gbc.gridy = 0; form.add(new JLabel("Chon nhanh:"), gbc);
        gbc.gridx = 1; gbc.gridy = 0; form.add(cbCaiTrang, gbc);
        gbc.gridx = 2; gbc.gridy = 0; form.add(new JLabel("ID CT:"), gbc);
        gbc.gridx = 3; gbc.gridy = 0; form.add(txtCtId, gbc);

        gbc.gridx = 0; gbc.gridy = 1; form.add(new JLabel("Noi nhan:"), gbc);
        gbc.gridx = 1; gbc.gridy = 1; gbc.gridwidth = 3; form.add(cbCtDestination, gbc);

        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 1; form.add(new JLabel("Goi chi so VIP:"), gbc);
        gbc.gridx = 1; gbc.gridy = 2; gbc.gridwidth = 3; form.add(cbCtOptionPreset, gbc);

        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 1; form.add(new JLabel("Custom Options:"), gbc);
        gbc.gridx = 1; gbc.gridy = 3; gbc.gridwidth = 3; form.add(txtCtCustomOptions, gbc);

        JPanel pnlCtExtra = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        pnlCtExtra.setOpaque(false);
        pnlCtExtra.add(chkCtLock);
        pnlCtExtra.add(new JLabel("HSD (ngay, 0 = VV):"));
        pnlCtExtra.add(spCtHsd);
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 4; form.add(pnlCtExtra, gbc);

        gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 4; gbc.insets = new Insets(12, 6, 6, 6);
        form.add(btnGiveCt, gbc);

        panel.add(card, BorderLayout.NORTH);
        panel.add(new JScrollPane(form), BorderLayout.CENTER);
        return panel;
    }

    // ==========================================
    // TAB: TANG PET, LINH THU & THU CUOI VIP
    // ==========================================
    private void setSelectedPet(ItemData item) {
        this.currentSelectedPet = item;
        if (item == null) {
            lblPetTitle.setText("Chưa chọn Pet / Linh Thú");
            lblPetDesc.setText("");
            lblPetIconPreview.setIcon(null);
            txtPetId.setText("");
            return;
        }

        txtPetId.setText(String.valueOf(item.id));
        lblPetTitle.setText(item.name + " (ID: " + item.id + ")");
        String typeStr = (item.type == 21 ? "Pet / Thú Cưng Mini" : (item.type == 70 ? "Linh Thú Bay" : (item.type == 23 || item.type == 24 ? "Thú Cưỡi VIP" : "Đặc Biệt")));
        lblPetDesc.setText("<html><b>Phân loại:</b> " + typeStr + " | <b>Icon:</b> " + item.iconId + "<br><i>" + (item.description.isEmpty() ? "Vật phẩm thú cưng cao cấp" : item.description) + "</i></html>");

        ImageIcon icon = getItemIcon(item.iconId, 48);
        lblPetIconPreview.setIcon(icon);

        if (!isUpdatingCbPet && cbPet != null && cbPet.getSelectedItem() != item) {
            isUpdatingCbPet = true;
            cbPet.setSelectedItem(item);
            isUpdatingCbPet = false;
        }
    }

    private JPanel createGivePetTab() {
        JPanel panel = new JPanel(new BorderLayout(12, 12));
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(12, 12, 12, 12));

        // Top Visual Card
        JPanel card = new JPanel(new BorderLayout(12, 12));
        card.setBackground(new Color(245, 255, 250));
        card.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(46, 204, 113), 1, true),
                new EmptyBorder(12, 15, 12, 15)
        ));

        lblPetIconPreview = new JLabel();
        lblPetIconPreview.setPreferredSize(new Dimension(56, 56));
        lblPetIconPreview.setHorizontalAlignment(JLabel.CENTER);
        lblPetIconPreview.setBorder(new LineBorder(COL_BORDER, 1, true));
        lblPetIconPreview.setBackground(Color.WHITE);
        lblPetIconPreview.setOpaque(true);

        JPanel cardInfo = new JPanel(new GridLayout(2, 1, 4, 4));
        cardInfo.setOpaque(false);
        lblPetTitle = new JLabel("Chưa chọn Pet / Linh Thú");
        lblPetTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblPetTitle.setForeground(new Color(39, 174, 96));

        lblPetDesc = new JLabel("Chọn nhanh từ danh sách hoặc click 'Duyệt Thư Viện Pet' để xem toàn bộ ảnh...");
        lblPetDesc.setFont(FONT_UI);
        cardInfo.add(lblPetTitle);
        cardInfo.add(lblPetDesc);

        JButton btnOpenPicker = new JButton("🐾 Duyệt Thư Viện Pet & Thú Cưỡi (Kèm Ảnh)");
        btnOpenPicker.setFont(FONT_BOLD);
        btnOpenPicker.setBackground(new Color(39, 174, 96));
        btnOpenPicker.setForeground(Color.WHITE);
        btnOpenPicker.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnOpenPicker.addActionListener(e -> openItemPickerDialog(5));

        card.add(lblPetIconPreview, BorderLayout.WEST);
        card.add(cardInfo, BorderLayout.CENTER);
        card.add(btnOpenPicker, BorderLayout.EAST);

        // Center Form
        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(Color.WHITE);
        form.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(new LineBorder(COL_BORDER), "Thiết Lập Chỉ Số & Tặng Pet / Linh Thú / Thú Cưỡi"),
                new EmptyBorder(10, 15, 10, 15)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 8, 6, 8);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Sub Filter Toolbar
        JPanel pnlFilter = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        pnlFilter.setOpaque(false);
        JButton btnAll = new JButton("Tất Cả Thú");
        JButton btnMiniPet = new JButton("Pet Mini (Type 21)");
        JButton btnLinhThu = new JButton("Linh Thú Bay (Type 70)");
        JButton btnMount = new JButton("Thú Cưỡi VIP (Type 23)");
        for (JButton b : new JButton[]{btnAll, btnMiniPet, btnLinhThu, btnMount}) {
            b.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            b.setBackground(new Color(240, 243, 246));
            b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        }
        btnAll.addActionListener(e -> filterPetCombo(0));
        btnMiniPet.addActionListener(e -> filterPetCombo(21));
        btnLinhThu.addActionListener(e -> filterPetCombo(70));
        btnMount.addActionListener(e -> filterPetCombo(23));
        pnlFilter.add(new JLabel("Bộ lọc:"));
        pnlFilter.add(btnAll);
        pnlFilter.add(btnMiniPet);
        pnlFilter.add(btnLinhThu);
        pnlFilter.add(btnMount);

        cbPet = new JComboBox<>();
        cbPet.setRenderer(new ItemListCellRenderer(24));
        cbPet.setFont(FONT_UI);
        cbPet.setPreferredSize(new Dimension(320, 32));
        cbPet.addActionListener(e -> {
            if (!isUpdatingCbPet) {
                ItemData sel = (ItemData) cbPet.getSelectedItem();
                if (sel != null) setSelectedPet(sel);
            }
        });

        txtPetId = new JTextField(8);
        txtPetId.setFont(FONT_BOLD);
        txtPetId.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                try {
                    int id = Integer.parseInt(txtPetId.getText().trim());
                    if (itemMap.containsKey(id)) {
                        setSelectedPet(itemMap.get(id));
                    }
                } catch (Exception ignored) {}
            }
        });

        cbPetDestination = new JComboBox<>(new String[]{
                "Hành trang (Mặc định)",
                "Rương đồ (Box)",
                "Hòm thư"
        });
        cbPetDestination.setFont(FONT_UI);
        cbPetDestination.setPreferredSize(new Dimension(320, 30));

        cbPetOptionPreset = new JComboBox<>(new String[]{
                "Chỉ số chuẩn của Pet (Mặc định)",
                "Gói VIP 1: Tăng 50% Sức Đánh, 50% HP/KI (50-50,77-50,103-50)",
                "Gói VIP 2: Thần Linh Tối Thượng (100% SĐ, 100% HP/KI, 30% Chí Mạng, 20% Né)",
                "Gói VIP 3: Hỗ Trợ Đỉnh Cao (Hút HP/KI 20%, Phục hồi 10% mỗi 30s)",
                "Tuỳ chỉnh Options..."
        });
        cbPetOptionPreset.setFont(FONT_UI);
        cbPetOptionPreset.setPreferredSize(new Dimension(320, 30));

        txtPetCustomOptions = new JTextField();
        txtPetCustomOptions.setFont(FONT_UI);
        txtPetCustomOptions.putClientProperty("JTextField.placeholderText", "Ví dụ: 50-50,77-50,103-50,14-30 (id-param,...)");

        cbPetOptionPreset.addActionListener(e -> {
            int idx = cbPetOptionPreset.getSelectedIndex();
            if (idx == 1) txtPetCustomOptions.setText("50-50,77-50,103-50");
            else if (idx == 2) txtPetCustomOptions.setText("50-100,77-100,103-100,14-30,108-20");
            else if (idx == 3) txtPetCustomOptions.setText("95-20,96-20,77-50,103-50");
            else if (idx == 0) txtPetCustomOptions.setText("");
        });

        chkPetLock = new JCheckBox("Khoá giao dịch (Không thể trao đổi)", false);
        chkPetLock.setFont(FONT_UI);
        chkPetLock.setBackground(Color.WHITE);

        spPetHsd = new JSpinner(new SpinnerNumberModel(0, 0, 3650, 1));
        spPetHsd.setFont(FONT_UI);
        spPetHsd.setPreferredSize(new Dimension(80, 26));

        JButton btnGivePet = new JButton("🐾 TẶNG PET / THÚ CƯỠI CHO NGƯỜI CHƠI");
        btnGivePet.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnGivePet.setBackground(new Color(39, 174, 96));
        btnGivePet.setForeground(Color.WHITE);
        btnGivePet.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnGivePet.setPreferredSize(new Dimension(340, 40));
        btnGivePet.addActionListener(e -> {
            Player p = checkAndGetTargetPlayer();
            if (p == null) return;
            if (currentSelectedPet == null) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn Pet / Thú cưỡi cần tặng!", "Chưa chọn Pet", JOptionPane.WARNING_MESSAGE);
                return;
            }

            int dest = cbPetDestination.getSelectedIndex();
            Item it = ItemService.gI().createNewItem((short) currentSelectedPet.id, 1);
            if (it == null) {
                JOptionPane.showMessageDialog(this, "Không thể tạo vật phẩm ID " + currentSelectedPet.id, "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String custom = txtPetCustomOptions.getText().trim();
            if (!custom.isEmpty()) {
                for (String part : custom.split("[,;]")) {
                    String[] kv = part.trim().split("[-=:]");
                    if (kv.length == 2) {
                        try {
                            int optId = Integer.parseInt(kv[0].trim());
                            int param = Integer.parseInt(kv[1].trim());
                            it.itemOptions.add(new ItemOption(optId, param));
                        } catch (Exception ignored) {}
                    }
                }
            }
            if (chkPetLock.isSelected()) {
                it.itemOptions.add(new ItemOption(30, 0));
            }
            int hsd = (Integer) spPetHsd.getValue();
            if (hsd > 0) {
                it.itemOptions.add(new ItemOption(93, hsd));
            }

            boolean ok = deliverItemToPlayer(p, it, dest);
            String destName = dest == 1 ? "Rương đồ (Box)" : (dest == 2 ? "Hòm thư" : "Hành trang");
            if (ok) {
                if (p.getSession() != null) Service.gI().sendThongBao(p, "Bạn nhận được [" + currentSelectedPet.name + "] từ Admin!");
                JOptionPane.showMessageDialog(this, "Đã tặng [" + currentSelectedPet.name + "] vào [" + destName + "] của [" + p.name + "] thành công!");
                updatePlayerSummary(p);
            } else {
                JOptionPane.showMessageDialog(this, destName + " của người chơi đã đầy!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        });

        // Combo Gift Buttons Toolbar
        JPanel pnlCombo = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 6));
        pnlCombo.setOpaque(false);
        pnlCombo.setBorder(BorderFactory.createTitledBorder(new LineBorder(COL_BORDER), "Tặng Nhanh Bộ Sưu Tập Pet & Linh Thú"));

        JButton btnComboPets = new JButton("🎁 Tặng 9 Pet Mới");
        btnComboPets.setFont(FONT_BOLD);
        btnComboPets.setBackground(new Color(41, 128, 185));
        btnComboPets.setForeground(Color.WHITE);
        btnComboPets.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnComboPets.addActionListener(e -> giveItemBatch(new int[]{2446, 2447, 2448, 2449, 2450, 2468, 2469, 2470, 2471}, "Bộ 9 Pet Mới"));

        JButton btnComboLinhThu = new JButton("🐉 Tặng 5 Linh Thú Mới");
        btnComboLinhThu.setFont(FONT_BOLD);
        btnComboLinhThu.setBackground(new Color(142, 68, 173));
        btnComboLinhThu.setForeground(Color.WHITE);
        btnComboLinhThu.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnComboLinhThu.addActionListener(e -> giveItemBatch(new int[]{2437, 2438, 2439, 2472, 2473}, "Bộ 5 Linh Thú Mới"));

        JButton btnComboMounts = new JButton("⚡ Tặng 6 Thú Cưỡi Mới");
        btnComboMounts.setFont(FONT_BOLD);
        btnComboMounts.setBackground(new Color(230, 126, 34));
        btnComboMounts.setForeground(Color.WHITE);
        btnComboMounts.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnComboMounts.addActionListener(e -> giveItemBatch(new int[]{2451, 2452, 2453, 2454, 2455, 2474}, "Bộ 6 Thú Cưỡi Mới"));

        JButton btnGiveAdminCt = new JButton("👑 Tặng Cải Trang Admin");
        btnGiveAdminCt.setFont(FONT_BOLD);
        btnGiveAdminCt.setBackground(new Color(192, 57, 43));
        btnGiveAdminCt.setForeground(Color.WHITE);
        btnGiveAdminCt.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnGiveAdminCt.addActionListener(e -> giveItemBatch(new int[]{2434}, "Cải Trang Admin (Đồ Thần Linh Tự Động)"));

        pnlCombo.add(btnComboPets);
        pnlCombo.add(btnComboLinhThu);
        pnlCombo.add(btnComboMounts);
        pnlCombo.add(btnGiveAdminCt);

        // Layout GridBag
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 4; form.add(pnlFilter, gbc);
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 1; form.add(new JLabel("Chọn nhanh:"), gbc);
        gbc.gridx = 1; gbc.gridy = 1; form.add(cbPet, gbc);
        gbc.gridx = 2; gbc.gridy = 1; form.add(new JLabel("ID Pet:"), gbc);
        gbc.gridx = 3; gbc.gridy = 1; form.add(txtPetId, gbc);

        gbc.gridx = 0; gbc.gridy = 2; form.add(new JLabel("Nơi nhận:"), gbc);
        gbc.gridx = 1; gbc.gridy = 2; gbc.gridwidth = 3; form.add(cbPetDestination, gbc);

        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 1; form.add(new JLabel("Gói chỉ số:"), gbc);
        gbc.gridx = 1; gbc.gridy = 3; gbc.gridwidth = 3; form.add(cbPetOptionPreset, gbc);

        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 1; form.add(new JLabel("Custom Options:"), gbc);
        gbc.gridx = 1; gbc.gridy = 4; gbc.gridwidth = 3; form.add(txtPetCustomOptions, gbc);

        JPanel pnlPetExtra = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        pnlPetExtra.setOpaque(false);
        pnlPetExtra.add(chkPetLock);
        pnlPetExtra.add(new JLabel("HSD (ngày, 0 = Vĩnh viễn):"));
        pnlPetExtra.add(spPetHsd);
        gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 4; form.add(pnlPetExtra, gbc);

        gbc.gridx = 0; gbc.gridy = 6; gbc.gridwidth = 4; gbc.insets = new Insets(12, 6, 6, 6);
        form.add(btnGivePet, gbc);

        gbc.gridx = 0; gbc.gridy = 7; gbc.gridwidth = 4; gbc.insets = new Insets(10, 6, 6, 6);
        form.add(pnlCombo, gbc);

        panel.add(card, BorderLayout.NORTH);
        panel.add(new JScrollPane(form), BorderLayout.CENTER);
        return panel;
    }

    private void filterPetCombo(int filterType) {
        if (cbPet == null) return;
        isUpdatingCbPet = true;
        DefaultComboBoxModel<ItemData> model = new DefaultComboBoxModel<>();
        for (ItemData p : petList) {
            if (filterType == 0) {
                model.addElement(p);
            } else if (filterType == 21 && p.type == 21) {
                model.addElement(p);
            } else if (filterType == 70 && (p.type == 70 || p.type == 71)) {
                model.addElement(p);
            } else if (filterType == 23 && (p.type == 23 || p.type == 24)) {
                model.addElement(p);
            }
        }
        cbPet.setModel(model);
        isUpdatingCbPet = false;
        if (model.getSize() > 0) {
            setSelectedPet(model.getElementAt(0));
        }
    }

    private void giveItemBatch(int[] itemIds, String batchName) {
        Player p = checkAndGetTargetPlayer();
        if (p == null) return;
        int successCount = 0;
        for (int id : itemIds) {
            Item it = ItemService.gI().createNewItem((short) id, 1);
            if (it != null) {
                if (deliverItemToPlayer(p, it, 0, false)) {
                    successCount++;
                }
            }
        }
        PlayerDAO.updatePlayer(p);
        if (p.getSession() != null) {
            Service.gI().sendThongBao(p, "Bạn nhận được " + batchName + " từ Admin!");
        }
        JOptionPane.showMessageDialog(this, "Đã tặng thành công " + successCount + "/" + itemIds.length + " vật phẩm trong " + batchName + " cho [" + p.name + "]!");
        updatePlayerSummary(p);
    }

    // ==========================================
    // TAB 3: TANG SUSANO, CANH & LINH THU VIP
    // ==========================================
    private void setSelectedSusano(ItemData item) {
        this.currentSelectedSusano = item;
        if (item == null) {
            lblSusanoTitle.setText("Chua chon Susano / Canh");
            lblSusanoDesc.setText("");
            lblSusanoIconPreview.setIcon(null);
            txtSusanoId.setText("");
            return;
        }

        txtSusanoId.setText(String.valueOf(item.id));
        lblSusanoTitle.setText(item.name + " (ID: " + item.id + ")");
        lblSusanoDesc.setText("<html><b>Loai:</b> Susano / Canh / Linh Thu | <b>Part FlagBag:</b> " + item.part + "<br><i>" + (item.description.isEmpty() ? "Vat pham dac biet" : item.description) + "</i></html>");

        ImageIcon icon = getItemIcon(item.iconId, 48);
        lblSusanoIconPreview.setIcon(icon);
    }

    private JPanel createGiveSusanoTab() {
        JPanel panel = new JPanel(new BorderLayout(12, 12));
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(12, 12, 12, 12));

        // Top Visual Card
        JPanel card = new JPanel(new BorderLayout(12, 12));
        card.setBackground(new Color(248, 252, 250));
        card.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(COL_BORDER, 1, true),
                new EmptyBorder(10, 12, 10, 12)
        ));

        lblSusanoIconPreview = new JLabel();
        lblSusanoIconPreview.setPreferredSize(new Dimension(56, 56));
        lblSusanoIconPreview.setHorizontalAlignment(JLabel.CENTER);
        lblSusanoIconPreview.setBorder(new LineBorder(COL_BORDER, 1, true));
        lblSusanoIconPreview.setBackground(Color.WHITE);
        lblSusanoIconPreview.setOpaque(true);

        JPanel cardInfo = new JPanel(new GridLayout(2, 1, 3, 3));
        cardInfo.setOpaque(false);
        lblSusanoTitle = new JLabel("Chua chon Susano / Canh");
        lblSusanoTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblSusanoTitle.setForeground(new Color(22, 160, 133));

        lblSusanoDesc = new JLabel("Chon nhanh ben duoi hoac click Duyet Tat Ca...");
        lblSusanoDesc.setFont(FONT_UI);
        cardInfo.add(lblSusanoTitle);
        cardInfo.add(lblSusanoDesc);

        JButton btnBrowseSusano = new JButton("Duyet Thu Vien Susano & Canh");
        btnBrowseSusano.setFont(FONT_BOLD);
        btnBrowseSusano.setBackground(new Color(22, 160, 133));
        btnBrowseSusano.setForeground(Color.WHITE);
        btnBrowseSusano.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnBrowseSusano.addActionListener(e -> openItemPickerDialog(3));

        card.add(lblSusanoIconPreview, BorderLayout.WEST);
        card.add(cardInfo, BorderLayout.CENTER);
        card.add(btnBrowseSusano, BorderLayout.EAST);

        // Center Container
        JPanel centerPnl = new JPanel();
        centerPnl.setLayout(new BoxLayout(centerPnl, BoxLayout.Y_AXIS));
        centerPnl.setBackground(Color.WHITE);

        // Preset 1: Susano / Thần Thể
        JPanel pnlSusanoFast = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 4));
        pnlSusanoFast.setBackground(Color.WHITE);
        pnlSusanoFast.setBorder(new TitledBorder(new LineBorder(COL_BORDER), "Chon nhanh Susano / Than The (11 Bo Than The Hot Nhat):"));

        int[] susanoIds = {
                2137, // Hỏa Thần Thể
                2140, // Huyết Ma Thể
                2145, // Hắc Viêm Thể
                2144, // U Minh Lam Hỏa Thể
                2141, // Thái Cổ Sinh Mệnh Thể
                2211, // Cửu Thiên Thần Lôi Thể
                2136, // Bất Diệt Hỏa Thể
                2138, // Hỏa Hoàng Thể
                2139, // Lôi Âm Minh Hải Thể
                2142, // Vạn Độc Thể
                2143, // Thái Dương Thiên Tiên Thể
                2153  // Thần Luân Hộ Thể
        };
        for (int sid : susanoIds) {
            JButton b = new JButton();
            b.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            b.setMargin(new Insets(2, 6, 2, 6));
            if (itemMap.containsKey(sid)) {
                ItemData it = itemMap.get(sid);
                b.setText(it.name);
                b.setIcon(getItemIcon(it.iconId, 18));
            } else {
                b.setText("Susano ID " + sid);
            }
            b.addActionListener(e -> {
                if (itemMap.containsKey(sid)) setSelectedSusano(itemMap.get(sid));
                else setSelectedSusano(new ItemData(sid, "Susano " + sid, 11, 3, 0, 0, 0, ""));
            });
            pnlSusanoFast.add(b);
        }

        // Preset 2: Cánh Thần / Thần Dực
        JPanel pnlCanhFast = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 4));
        pnlCanhFast.setBackground(Color.WHITE);
        pnlCanhFast.setBorder(new TitledBorder(new LineBorder(COL_BORDER), "Chon nhanh Canh Than / Than Dyc VIP:"));

        int[] canhIds = {
                2166, // Kim Quang Thần Dực
                2167, // Lôi Đánh Thần Dực
                2165, // Phượng Hoàng Huyết Dực
                2164, // Vong Linh Minh Dực
                1995, // Cánh Huyết Hỏa
                1996, // Cánh Băng Hỏa
                1971, // Cánh Thiên Thần Sa Ngã
                2154, // Cánh Lông Vũ Trắng
                2157, // Cánh Hắc Ám
                2158, // Cánh Rồng
                2159  // Cánh Kiếm
        };
        for (int cid : canhIds) {
            JButton b = new JButton();
            b.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            b.setMargin(new Insets(2, 6, 2, 6));
            if (itemMap.containsKey(cid)) {
                ItemData it = itemMap.get(cid);
                b.setText(it.name);
                b.setIcon(getItemIcon(it.iconId, 18));
            } else {
                b.setText("Canh ID " + cid);
            }
            b.addActionListener(e -> {
                if (itemMap.containsKey(cid)) setSelectedSusano(itemMap.get(cid));
                else setSelectedSusano(new ItemData(cid, "Canh " + cid, 11, 3, 0, 0, 0, ""));
            });
            pnlCanhFast.add(b);
        }

        // Preset 3: Linh Thú & Pet VIP
        JPanel pnlPetFast = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 4));
        pnlPetFast.setBackground(Color.WHITE);
        pnlPetFast.setBorder(new TitledBorder(new LineBorder(COL_BORDER), "Chon nhanh Linh Thu & Pet VIP:"));

        int[] petIds = {
                2171, 2175, 2180, 2185, 2190, // Linh Thú
                1668, // Capybara hồng
                1550, // Godzilla
                1551, // Kong
                1793, // Rồng xương
                1597, // Albart Cup
                1207  // Minion
        };
        for (int pid : petIds) {
            JButton b = new JButton();
            b.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            b.setMargin(new Insets(2, 6, 2, 6));
            if (itemMap.containsKey(pid)) {
                ItemData it = itemMap.get(pid);
                b.setText(it.name);
                b.setIcon(getItemIcon(it.iconId, 18));
            } else {
                b.setText("Pet/Linh Thu " + pid);
            }
            b.addActionListener(e -> {
                if (itemMap.containsKey(pid)) setSelectedSusano(itemMap.get(pid));
                else setSelectedSusano(new ItemData(pid, "Pet/Linh Thu " + pid, 21, 3, 0, 0, 0, ""));
            });
            pnlPetFast.add(b);
        }

        // Settings Form
        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(Color.WHITE);
        form.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(new LineBorder(COL_BORDER), "Cau Hinh & Tang Susano / Canh / Linh Thu"),
                new EmptyBorder(8, 10, 8, 10)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 6, 5, 6);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        txtSusanoId = new JTextField(8);
        txtSusanoId.setFont(FONT_BOLD);
        txtSusanoId.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                try {
                    int id = Integer.parseInt(txtSusanoId.getText().trim());
                    if (itemMap.containsKey(id)) setSelectedSusano(itemMap.get(id));
                } catch (Exception ignored) {}
            }
        });

        cbSusanoDestination = new JComboBox<>(new String[]{
                "Hanh trang (Tui do)",
                "Ruong do (Ruong chua / Box)",
                "Hom thu (Hop qua)"
        });
        cbSusanoDestination.setFont(FONT_BOLD);

        chkSusanoGodBuff = new JCheckBox("Tu Dong Kich Hoat Chi So Than Thanh (+50% SD, +50% HP/KI, +20% Crit, +15% Hut Mau/KI, +15% Ne Don)", true);
        chkSusanoGodBuff.setFont(FONT_BOLD);
        chkSusanoGodBuff.setForeground(new Color(22, 160, 133));
        chkSusanoGodBuff.setBackground(Color.WHITE);

        txtSusanoCustomOptions = new JTextField("", 25);
        txtSusanoCustomOptions.setFont(FONT_UI);

        chkSusanoLock = new JCheckBox("Khoa giao dich", false);
        chkSusanoLock.setBackground(Color.WHITE);

        spSusanoHsd = new JSpinner(new SpinnerNumberModel(0, 0, 9999, 1));
        spSusanoHsd.setPreferredSize(new Dimension(80, 26));

        JButton btnGiveSusano = new JButton("TANG SUSANO / CANH / LINH THU NAY");
        btnGiveSusano.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnGiveSusano.setBackground(COL_SUCCESS);
        btnGiveSusano.setForeground(Color.WHITE);
        btnGiveSusano.setPreferredSize(new Dimension(360, 42));
        btnGiveSusano.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnGiveSusano.addActionListener(e -> {
            Player p = checkAndGetTargetPlayer();
            if (p == null) return;
            if (currentSelectedSusano == null) {
                JOptionPane.showMessageDialog(this, "Vui long chon mot Susano / Canh / Linh thu!", "Thong bao", JOptionPane.WARNING_MESSAGE);
                return;
            }

            int dest = cbSusanoDestination.getSelectedIndex();
            Item it = ItemService.gI().createNewItem((short) currentSelectedSusano.id, 1);

            if (chkSusanoGodBuff.isSelected()) {
                it.itemOptions.add(new ItemOption(50, 50));  // +50% Sức đánh
                it.itemOptions.add(new ItemOption(77, 50));  // +50% HP
                it.itemOptions.add(new ItemOption(103, 50)); // +50% KI
                it.itemOptions.add(new ItemOption(14, 20));  // +20% Chí mạng
                it.itemOptions.add(new ItemOption(95, 15));  // +15% Hút máu
                it.itemOptions.add(new ItemOption(96, 15));  // +15% Hút KI
                it.itemOptions.add(new ItemOption(108, 15)); // +15% Né đòn
            }

            String custom = txtSusanoCustomOptions.getText().trim();
            if (!custom.isEmpty()) {
                for (String part : custom.split("[,;]")) {
                    String[] p2 = part.trim().split("[-:]");
                    if (p2.length == 2) {
                        try {
                            it.itemOptions.add(new ItemOption(Integer.parseInt(p2[0].trim()), Integer.parseInt(p2[1].trim())));
                        } catch (Exception ignored) {}
                    }
                }
            }

            if (chkSusanoLock.isSelected()) it.itemOptions.add(new ItemOption(30, 0));
            int hsd = (int) spSusanoHsd.getValue();
            if (hsd > 0) it.itemOptions.add(new ItemOption(93, hsd));

            boolean ok = deliverItemToPlayer(p, it, dest);
            String destName = dest == 1 ? "Ruong do (Box)" : (dest == 2 ? "Hom thu" : "Hanh trang");
            if (ok) {
                if (p.getSession() != null) Service.gI().sendThongBao(p, "Ban nhan duoc " + currentSelectedSusano.name + " tu Admin!");
                JOptionPane.showMessageDialog(this, "Da tang [" + currentSelectedSusano.name + "] vao [" + destName + "] cua [" + p.name + "] thanh cong!");
                updatePlayerSummary(p);
            } else {
                JOptionPane.showMessageDialog(this, destName + " cua nguoi choi da day!", "Loi", JOptionPane.ERROR_MESSAGE);
            }
        });

        gbc.gridx = 0; gbc.gridy = 0; form.add(new JLabel("ID Vat pham:"), gbc);
        gbc.gridx = 1; gbc.gridy = 0; form.add(txtSusanoId, gbc);
        gbc.gridx = 2; gbc.gridy = 0; form.add(new JLabel("Noi nhan:"), gbc);
        gbc.gridx = 3; gbc.gridy = 0; form.add(cbSusanoDestination, gbc);

        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 4;
        form.add(chkSusanoGodBuff, gbc);

        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 1; form.add(new JLabel("Custom Options:"), gbc);
        gbc.gridx = 1; gbc.gridy = 2; gbc.gridwidth = 3; form.add(txtSusanoCustomOptions, gbc);

        JPanel pnlSusanoExtra = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        pnlSusanoExtra.setOpaque(false);
        pnlSusanoExtra.add(chkSusanoLock);
        pnlSusanoExtra.add(new JLabel("HSD (ngay, 0 = VV):"));
        pnlSusanoExtra.add(spSusanoHsd);
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 4; form.add(pnlSusanoExtra, gbc);

        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 4; gbc.insets = new Insets(12, 6, 6, 6);
        form.add(btnGiveSusano, gbc);

        centerPnl.add(pnlSusanoFast);
        centerPnl.add(Box.createVerticalStrut(6));
        centerPnl.add(pnlCanhFast);
        centerPnl.add(Box.createVerticalStrut(6));
        centerPnl.add(pnlPetFast);
        centerPnl.add(Box.createVerticalStrut(6));
        centerPnl.add(form);

        panel.add(card, BorderLayout.NORTH);
        panel.add(new JScrollPane(centerPnl), BorderLayout.CENTER);
        return panel;
    }

    // ==========================================
    // TAB: TANG SACH KI NANG CAO CAP & BI KIP
    // ==========================================
    private void setSelectedSkillBook(ItemData item) {
        this.currentSelectedSkillBook = item;
        if (item == null) {
            lblBookTitle.setText("Chua chon sach ki nang");
            lblBookDesc.setText("");
            lblBookIconPreview.setIcon(null);
            txtBookId.setText("");
            return;
        }
        txtBookId.setText(String.valueOf(item.id));
        lblBookTitle.setText(item.name + " (ID: " + item.id + ")");
        String genderName = item.gender == 0 ? "Trai Dat" : (item.gender == 1 ? "Namec" : (item.gender == 2 ? "Xayda" : "Dung chung"));
        lblBookDesc.setText("<html><b>Loai:</b> Sach ki nang / Bi kip | <b>Hanh tinh:</b> " + genderName + " | <b>Cap do:</b> " + item.level + "<br><i>" + (item.description.isEmpty() ? "Sach vo hoc thuong thua" : item.description) + "</i></html>");
        lblBookIconPreview.setIcon(getItemIcon(item.iconId, 48));

        if (cbSkillBooks != null && !isUpdatingCbSkillBooks) {
            isUpdatingCbSkillBooks = true;
            for (int i = 0; i < cbSkillBooks.getItemCount(); i++) {
                if (cbSkillBooks.getItemAt(i).id == item.id) {
                    cbSkillBooks.setSelectedIndex(i);
                    break;
                }
            }
            isUpdatingCbSkillBooks = false;
        }
    }

    private void giveSkillBookBatch(int[] ids, String name) {
        Player p = checkAndGetTargetPlayer();
        if (p == null) return;
        int dest = cbBookDestination != null ? cbBookDestination.getSelectedIndex() : 0;
        int successCount = 0;
        for (int id : ids) {
            Item item = ItemService.gI().createNewItem((short) id, 1);
            if (deliverItemToPlayer(p, item, dest)) {
                successCount++;
            }
        }
        String destName = dest == 1 ? "Ruong do (Box)" : (dest == 2 ? "Hom thu" : "Hanh trang");
        if (p.getSession() != null) {
            Service.gI().sendThongBao(p, "Ban nhan duoc " + name + " tu Admin!");
        }
        JOptionPane.showMessageDialog(this, "Da gui " + name + " vao [" + destName + "] cua [" + p.name + "] thanh cong! (" + successCount + "/" + ids.length + ")", "Thanh cong", JOptionPane.INFORMATION_MESSAGE);
        updatePlayerSummary(p);
    }

    private void giveSingleSkillBook() {
        Player p = checkAndGetTargetPlayer();
        if (p == null) return;
        if (currentSelectedSkillBook == null) {
            JOptionPane.showMessageDialog(this, "Vui long chon mot cuon sach ki nang truoc!", "Thong bao", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int dest = cbBookDestination != null ? cbBookDestination.getSelectedIndex() : 0;
        int qty = (int) spBookQuantity.getValue();
        Item item = ItemService.gI().createNewItem((short) currentSelectedSkillBook.id, qty);
        boolean ok = deliverItemToPlayer(p, item, dest);
        String destName = dest == 1 ? "Ruong do (Box)" : (dest == 2 ? "Hom thu" : "Hanh trang");
        if (ok) {
            if (p.getSession() != null) Service.gI().sendThongBao(p, "Ban nhan duoc x" + qty + " " + currentSelectedSkillBook.name + " tu Admin!");
            JOptionPane.showMessageDialog(this, "Da gui x" + qty + " [" + currentSelectedSkillBook.name + "] vao [" + destName + "] cua [" + p.name + "] thanh cong!");
            updatePlayerSummary(p);
        } else {
            JOptionPane.showMessageDialog(this, destName + " cua nguoi choi da day!", "Loi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private JPanel createGiveSkillBookTab() {
        JPanel panel = new JPanel(new BorderLayout(12, 12));
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(12, 12, 12, 12));

        // Top 1-Click Fast Gifts
        JPanel topFastPnl = new JPanel(new BorderLayout(8, 8));
        topFastPnl.setBackground(new Color(248, 252, 248));
        topFastPnl.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(new LineBorder(COL_SUCCESS, 1, true), "1-Click Tang Tron Bo Sach Ki Nang / Tuyet Ky / Bi Kip:"),
                new EmptyBorder(6, 8, 6, 8)
        ));

        JPanel pnlFastButtons = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        pnlFastButtons.setOpaque(false);

        JButton btnAllTuyetKy = new JButton("Tang Tron Bo Sach Tuyet Ky 1 & 2");
        btnAllTuyetKy.setFont(FONT_BOLD);
        btnAllTuyetKy.setBackground(COL_PRIMARY);
        btnAllTuyetKy.setForeground(Color.WHITE);
        btnAllTuyetKy.addActionListener(e -> giveSkillBookBatch(new int[]{1044, 1211, 1212, 1278, 1279, 1280}, "Tron bo Sach Tuyet Ky 1 & 2"));

        JButton btnBiKiep = new JButton("Tang Bi Kip & Ruong Bi Kip (590, 1229, 1403)");
        btnBiKiep.setFont(FONT_BOLD);
        btnBiKiep.setBackground(COL_PURPLE);
        btnBiKiep.setForeground(Color.WHITE);
        btnBiKiep.addActionListener(e -> giveSkillBookBatch(new int[]{590, 1229, 1403}, "Bi Kip & Ruong Bi Kip"));

        JButton btnSkillLv7TraiDat = new JButton("Full Skill C7 Trai Dat (10 mon)");
        btnSkillLv7TraiDat.setFont(FONT_BOLD);
        btnSkillLv7TraiDat.setBackground(new Color(46, 139, 87));
        btnSkillLv7TraiDat.setForeground(Color.WHITE);
        btnSkillLv7TraiDat.addActionListener(e -> giveSkillBookBatch(new int[]{72, 100, 121, 306, 313, 440, 488, 494, 495, 501}, "Full Sach Ki Nang C7 Trai Dat"));

        JButton btnSkillLv7Namec = new JButton("Full Skill C7 Namec (13 mon)");
        btnSkillLv7Namec.setFont(FONT_BOLD);
        btnSkillLv7Namec.setBackground(new Color(30, 144, 255));
        btnSkillLv7Namec.setForeground(Color.WHITE);
        btnSkillLv7Namec.addActionListener(e -> giveSkillBookBatch(new int[]{79, 86, 101, 107, 128, 328, 334, 335, 341, 474, 480, 481, 487}, "Full Sach Ki Nang C7 Namec"));

        JButton btnSkillLv7Xayda = new JButton("Full Skill C7 Xayda (14 mon)");
        btnSkillLv7Xayda.setFont(FONT_BOLD);
        btnSkillLv7Xayda.setBackground(COL_WARNING);
        btnSkillLv7Xayda.setForeground(Color.WHITE);
        btnSkillLv7Xayda.addActionListener(e -> giveSkillBookBatch(new int[]{87, 93, 108, 114, 129, 135, 314, 320, 321, 327, 502, 508, 509, 515}, "Full Sach Ki Nang C7 Xayda"));

        pnlFastButtons.add(btnAllTuyetKy);
        pnlFastButtons.add(btnBiKiep);
        pnlFastButtons.add(btnSkillLv7TraiDat);
        pnlFastButtons.add(btnSkillLv7Namec);
        pnlFastButtons.add(btnSkillLv7Xayda);
        topFastPnl.add(pnlFastButtons, BorderLayout.CENTER);

        // Center card & form
        JPanel centerWrap = new JPanel(new BorderLayout(10, 10));
        centerWrap.setBackground(Color.WHITE);

        // Visual Preview Card
        JPanel card = new JPanel(new BorderLayout(12, 12));
        card.setBackground(new Color(250, 255, 250));
        card.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(COL_BORDER, 1, true),
                new EmptyBorder(10, 12, 10, 12)
        ));

        lblBookIconPreview = new JLabel();
        lblBookIconPreview.setPreferredSize(new Dimension(56, 56));
        lblBookIconPreview.setHorizontalAlignment(JLabel.CENTER);
        lblBookIconPreview.setBorder(new LineBorder(COL_BORDER, 1, true));
        lblBookIconPreview.setBackground(Color.WHITE);
        lblBookIconPreview.setOpaque(true);

        JPanel cardInfo = new JPanel(new GridLayout(2, 1, 3, 3));
        cardInfo.setOpaque(false);
        lblBookTitle = new JLabel("Chua chon sach ki nang");
        lblBookTitle.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblBookTitle.setForeground(COL_SUCCESS);

        lblBookDesc = new JLabel("Vui long chon sach ki nang hoac click Duyet Thu Vien...");
        lblBookDesc.setFont(FONT_UI);
        cardInfo.add(lblBookTitle);
        cardInfo.add(lblBookDesc);

        JButton btnBrowseBook = new JButton("Duyet Thu Vien Sach Ki Nang (Kem Anh)");
        btnBrowseBook.setFont(FONT_BOLD);
        btnBrowseBook.setBackground(COL_SUCCESS);
        btnBrowseBook.setForeground(Color.WHITE);
        btnBrowseBook.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnBrowseBook.addActionListener(e -> openItemPickerDialog(4));

        card.add(lblBookIconPreview, BorderLayout.WEST);
        card.add(cardInfo, BorderLayout.CENTER);
        card.add(btnBrowseBook, BorderLayout.EAST);

        // Quick Pick Single Books
        JPanel quickPickPnl = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 4));
        quickPickPnl.setBackground(Color.WHITE);
        quickPickPnl.setBorder(new TitledBorder(new LineBorder(COL_BORDER), "Chon nhanh cac loai Sach Tuyet Ky & Bi Kip mau:"));

        int[] quickBookIds = {
                1044, 1211, 1212, // Tuyệt kỹ 1 (TĐ, NM, XD)
                1278, 1279, 1280, // Tuyệt kỹ 2 (TĐ, NM, XD)
                590, 1229, 1403, 1343, // Bí kíp, Bí kíp tuyệt kỹ, Rương, Cadic LH chưởng
                72, 86, 93, 100, 107, 114, 121, 128, 135, 306, 313, 320, 327, 334, 341, 440 // Sách lv7 tiêu biểu
        };
        for (int qId : quickBookIds) {
            JButton b = new JButton();
            b.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            b.setMargin(new Insets(2, 6, 2, 6));
            if (itemMap.containsKey(qId)) {
                ItemData it = itemMap.get(qId);
                b.setText(it.name);
                b.setIcon(getItemIcon(it.iconId, 18));
            } else {
                b.setText("ID " + qId);
            }
            b.addActionListener(e -> {
                if (itemMap.containsKey(qId)) setSelectedSkillBook(itemMap.get(qId));
                else setSelectedSkillBook(new ItemData(qId, "Item " + qId, 7, 0, 0, 0, 0, ""));
            });
            quickPickPnl.add(b);
        }

        // Form settings
        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(Color.WHITE);
        form.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(new LineBorder(COL_BORDER), "Cau Hinh Tang Sach Ki Nang Nguoi Choi"),
                new EmptyBorder(8, 10, 8, 10)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        txtBookId = new JTextField(8);
        txtBookId.setFont(FONT_BOLD);
        txtBookId.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                try {
                    int id = Integer.parseInt(txtBookId.getText().trim());
                    if (itemMap.containsKey(id)) setSelectedSkillBook(itemMap.get(id));
                } catch (Exception ignored) {}
            }
        });

        cbSkillBooks = new JComboBox<>();
        cbSkillBooks.setFont(FONT_UI);
        cbSkillBooks.setRenderer(new ItemListCellRenderer(24));
        cbSkillBooks.addActionListener(e -> {
            if (!isUpdatingCbSkillBooks) {
                ItemData selected = (ItemData) cbSkillBooks.getSelectedItem();
                if (selected != null) setSelectedSkillBook(selected);
            }
        });

        spBookQuantity = new JSpinner(new SpinnerNumberModel(1, 1, 9999, 1));
        spBookQuantity.setFont(FONT_BOLD);
        spBookQuantity.setPreferredSize(new Dimension(80, 26));

        cbBookDestination = new JComboBox<>(new String[]{
                "Hanh trang (Tui do)",
                "Ruong do (Ruong chua / Box)",
                "Hom thu (Hop qua)"
        });
        cbBookDestination.setFont(FONT_BOLD);

        JButton btnGiveSingleBook = new JButton("TANG SACH KI NANG NAY CHO NGUOI CHOI");
        btnGiveSingleBook.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnGiveSingleBook.setBackground(COL_SUCCESS);
        btnGiveSingleBook.setForeground(Color.WHITE);
        btnGiveSingleBook.setPreferredSize(new Dimension(360, 42));
        btnGiveSingleBook.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnGiveSingleBook.addActionListener(e -> giveSingleSkillBook());

        // Row 0: ID & Danh sách
        gbc.gridx = 0; gbc.gridy = 0; form.add(new JLabel("ID Sach:"), gbc);
        gbc.gridx = 1; gbc.gridy = 0; form.add(txtBookId, gbc);
        gbc.gridx = 2; gbc.gridy = 0; form.add(new JLabel("Danh sach sach:"), gbc);
        gbc.gridx = 3; gbc.gridy = 0; form.add(cbSkillBooks, gbc);

        // Row 1: Số lượng & Nơi nhận
        gbc.gridx = 0; gbc.gridy = 1; form.add(new JLabel("So luong:"), gbc);
        gbc.gridx = 1; gbc.gridy = 1; form.add(spBookQuantity, gbc);
        gbc.gridx = 2; gbc.gridy = 1; form.add(new JLabel("Noi nhan:"), gbc);
        gbc.gridx = 3; gbc.gridy = 1; form.add(cbBookDestination, gbc);

        // Row 2: Submit button
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 4; gbc.insets = new Insets(12, 6, 6, 6);
        form.add(btnGiveSingleBook, gbc);

        centerWrap.add(card, BorderLayout.NORTH);
        centerWrap.add(quickPickPnl, BorderLayout.CENTER);
        centerWrap.add(form, BorderLayout.SOUTH);

        panel.add(topFastPnl, BorderLayout.NORTH);
        panel.add(new JScrollPane(centerWrap), BorderLayout.CENTER);
        return panel;
    }

    // ==========================================
    // TAB 4: TANG VAT PHAM (FULL 2000+ ITEMS)
    // ==========================================
    private void setSelectedItem(ItemData item) {
        this.currentSelectedItem = item;
        if (item == null) {
            lblItemTitle.setText("Chua chon vat pham");
            lblItemDesc.setText("");
            lblItemIconPreview.setIcon(null);
            txtItemId.setText("");
            return;
        }

        txtItemId.setText(String.valueOf(item.id));
        lblItemTitle.setText(item.name + " (ID: " + item.id + ")");
        String typeName = getTypeName(item.type);
        String genderName = item.gender == 0 ? "Trai Dat" : (item.gender == 1 ? "Namec" : (item.gender == 2 ? "Xayda" : "Tat ca"));
        lblItemDesc.setText("<html><b>Loai:</b> " + typeName + " | <b>Gioi tinh:</b> " + genderName + "<br><i>" + (item.description.isEmpty() ? "Vat pham game" : item.description) + "</i></html>");

        ImageIcon icon = getItemIcon(item.iconId, 48);
        lblItemIconPreview.setIcon(icon);
    }

    private String getTypeName(int type) {
        switch (type) {
            case 0: return "Ao";
            case 1: return "Quan";
            case 2: return "Gang";
            case 3: return "Giay";
            case 4: return "Rada / Nhan";
            case 5: return "Cai Trang (Avatar)";
            case 6: return "Dau Than";
            case 11: return "Phu kien / Susano / Canh";
            case 12: return "Ngoc Rong";
            case 14: return "Da Nang Cap";
            case 21: return "Pet Thu Cung";
            case 27: return "Vat Pham Su Kien";
            case 29: return "The Cao / Ve / Item";
            case 30: return "Bua";
            case 32: return "Sao Pha Le";
            case 70: return "Linh Thu";
            case 80: return "Danh Hieu / Quan Ham";
            default: return "Loai " + type;
        }
    }

    private JPanel createGiveItemTab() {
        JPanel panel = new JPanel(new BorderLayout(12, 12));
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(12, 12, 12, 12));

        // Quick Items Bar
        JPanel quickBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 6));
        quickBar.setBackground(new Color(245, 248, 253));
        quickBar.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(220, 230, 245), 1, true),
                new EmptyBorder(4, 6, 4, 6)
        ));

        JLabel lblQuick = new JLabel("Chon nhanh:");
        lblQuick.setFont(FONT_BOLD);
        lblQuick.setForeground(COL_PRIMARY);
        quickBar.add(lblQuick);

        int[] quickIds = {
                457,  // Thỏi vàng
                572,  // Rương vàng
                573,  // Rương bạc
                574,  // Rương đồng
                14, 15, 16, 17, 18, 19, 20, // Ngọc Rồng 1s -> 7s
                220, 221, 222, 223, 224, // Đá nâng cấp
                441, 442, 443, 444, 445, 446, 447 // Sao pha lê
        };

        for (int qId : quickIds) {
            JButton b = new JButton();
            b.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            b.setMargin(new Insets(2, 6, 2, 6));
            if (itemMap.containsKey(qId)) {
                ItemData it = itemMap.get(qId);
                b.setText(it.name);
                b.setIcon(getItemIcon(it.iconId, 18));
            } else {
                b.setText("ID " + qId);
            }
            b.addActionListener(e -> {
                if (itemMap.containsKey(qId)) {
                    setSelectedItem(itemMap.get(qId));
                } else {
                    setSelectedItem(new ItemData(qId, "Item " + qId, 0, 3, 0, 0, 0, ""));
                }
            });
            quickBar.add(b);
        }

        // Top Visual Card
        JPanel card = new JPanel(new BorderLayout(12, 12));
        card.setBackground(new Color(250, 252, 255));
        card.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(COL_BORDER, 1, true),
                new EmptyBorder(12, 15, 12, 15)
        ));

        lblItemIconPreview = new JLabel();
        lblItemIconPreview.setPreferredSize(new Dimension(56, 56));
        lblItemIconPreview.setHorizontalAlignment(JLabel.CENTER);
        lblItemIconPreview.setBorder(new LineBorder(COL_BORDER, 1, true));
        lblItemIconPreview.setBackground(Color.WHITE);
        lblItemIconPreview.setOpaque(true);

        JPanel cardInfo = new JPanel(new GridLayout(2, 1, 4, 4));
        cardInfo.setOpaque(false);
        lblItemTitle = new JLabel("Chua chon vat pham");
        lblItemTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblItemTitle.setForeground(COL_PRIMARY);

        lblItemDesc = new JLabel("Vui long chon vat pham tu thanh chon nhanh hoac click Duyet Tat Ca...");
        lblItemDesc.setFont(FONT_UI);
        cardInfo.add(lblItemTitle);
        cardInfo.add(lblItemDesc);

        JButton btnOpenPicker = new JButton("Duyet Tat Ca Thu Vien (2000+ Item)");
        btnOpenPicker.setFont(FONT_BOLD);
        btnOpenPicker.setBackground(COL_PRIMARY);
        btnOpenPicker.setForeground(Color.WHITE);
        btnOpenPicker.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnOpenPicker.addActionListener(e -> openItemPickerDialog(0));

        card.add(lblItemIconPreview, BorderLayout.WEST);
        card.add(cardInfo, BorderLayout.CENTER);
        card.add(btnOpenPicker, BorderLayout.EAST);

        // Center Form
        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(Color.WHITE);
        form.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(new LineBorder(COL_BORDER), "Cau Hinh & Tang Vat Pham"),
                new EmptyBorder(10, 15, 10, 15)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 8, 6, 8);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        txtItemId = new JTextField(8);
        txtItemId.setFont(FONT_BOLD);
        txtItemId.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                try {
                    int id = Integer.parseInt(txtItemId.getText().trim());
                    if (itemMap.containsKey(id)) {
                        setSelectedItem(itemMap.get(id));
                    }
                } catch (Exception ignored) {}
            }
        });

        cbItemDestination = new JComboBox<>(new String[]{
                "Hanh trang (Tui do)",
                "Ruong do (Ruong chua / Box)",
                "Hom thu (Hop qua)"
        });
        cbItemDestination.setFont(FONT_BOLD);

        spItemQuantity = new JSpinner(new SpinnerNumberModel(1, 1, 99999, 1));
        spItemQuantity.setFont(FONT_BOLD);

        txtItemCustomOptions = new JTextField("", 25);
        txtItemCustomOptions.setFont(FONT_UI);

        chkItemLock = new JCheckBox("Khoa giao dich", false);
        chkItemLock.setBackground(Color.WHITE);

        spItemHsd = new JSpinner(new SpinnerNumberModel(0, 0, 9999, 1));
        spItemHsd.setPreferredSize(new Dimension(80, 26));

        JButton btnSubmit = new JButton("TANG VAT PHAM NAY CHO NGUOI CHOI");
        btnSubmit.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnSubmit.setBackground(COL_SUCCESS);
        btnSubmit.setForeground(Color.WHITE);
        btnSubmit.setPreferredSize(new Dimension(360, 42));
        btnSubmit.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnSubmit.addActionListener(e -> {
            Player p = checkAndGetTargetPlayer();
            if (p == null) return;
            if (currentSelectedItem == null) {
                JOptionPane.showMessageDialog(this, "Vui long chon mot vat pham!", "Thong bao", JOptionPane.WARNING_MESSAGE);
                return;
            }

            int count = (int) spItemQuantity.getValue();
            int dest = cbItemDestination.getSelectedIndex();
            int hsd = (int) spItemHsd.getValue();

            Item item = ItemService.gI().createNewItem((short) currentSelectedItem.id, count);

            String optStr = txtItemCustomOptions.getText().trim();
            if (!optStr.isEmpty()) {
                for (String part : optStr.split("[,;]")) {
                    String[] p2 = part.trim().split("[-:]");
                    if (p2.length == 2) {
                        try {
                            item.itemOptions.add(new ItemOption(Integer.parseInt(p2[0].trim()), Integer.parseInt(p2[1].trim())));
                        } catch (Exception ignored) {}
                    }
                }
            }

            if (chkItemLock.isSelected()) item.itemOptions.add(new ItemOption(30, 0));
            if (hsd > 0) item.itemOptions.add(new ItemOption(93, hsd));

            boolean ok = deliverItemToPlayer(p, item, dest);
            String destName = dest == 1 ? "Ruong do (Box)" : (dest == 2 ? "Hom thu" : "Hanh trang");
            if (ok) {
                if (p.getSession() != null) Service.gI().sendThongBao(p, "Ban nhan duoc " + count + " " + currentSelectedItem.name + " tu Admin!");
                JOptionPane.showMessageDialog(this, "Da tang " + count + " [" + currentSelectedItem.name + "] vao [" + destName + "] cua [" + p.name + "] thanh cong!");
                updatePlayerSummary(p);
            } else {
                JOptionPane.showMessageDialog(this, destName + " cua nguoi choi da day!", "Loi", JOptionPane.ERROR_MESSAGE);
            }
        });

        gbc.gridx = 0; gbc.gridy = 0; form.add(new JLabel("ID Vat pham:"), gbc);
        gbc.gridx = 1; gbc.gridy = 0; form.add(txtItemId, gbc);
        gbc.gridx = 2; gbc.gridy = 0; form.add(new JLabel("Noi nhan:"), gbc);
        gbc.gridx = 3; gbc.gridy = 0; form.add(cbItemDestination, gbc);

        gbc.gridx = 0; gbc.gridy = 1; form.add(new JLabel("So luong:"), gbc);
        gbc.gridx = 1; gbc.gridy = 1; form.add(spItemQuantity, gbc);

        gbc.gridx = 0; gbc.gridy = 2; form.add(new JLabel("Custom Options:"), gbc);
        gbc.gridx = 1; gbc.gridy = 2; gbc.gridwidth = 3; form.add(txtItemCustomOptions, gbc);

        JPanel pnlExtra = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        pnlExtra.setOpaque(false);
        pnlExtra.add(chkItemLock);
        pnlExtra.add(new JLabel("HSD (ngay, 0 = VV):"));
        pnlExtra.add(spItemHsd);
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 4; form.add(pnlExtra, gbc);

        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 4; gbc.insets = new Insets(12, 6, 6, 6);
        form.add(btnSubmit, gbc);

        JPanel centerWrap = new JPanel(new BorderLayout(8, 8));
        centerWrap.setBackground(Color.WHITE);
        centerWrap.add(card, BorderLayout.NORTH);
        centerWrap.add(form, BorderLayout.CENTER);

        panel.add(quickBar, BorderLayout.NORTH);
        panel.add(centerWrap, BorderLayout.CENTER);
        return panel;
    }

    // ==========================================
    // VISUAL PICKER DIALOG (FULL 2000+ ITEMS)
    // ==========================================
    private void openItemPickerDialog(int targetType) {
        // targetType: 0=All, 1=CaiTrang, 2=QuanAo, 3=Susano, 4=SkillBooks, 5=Pet
        String title = "Thu Vien Chon Vat Pham Game Truc Quan";
        if (targetType == 1) title = "Thu Vien Chon Cai Trang (Avatar VIP)";
        else if (targetType == 2) title = "Thu Vien Chon Quan Ao & Trang Bi";
        else if (targetType == 3) title = "Thu Vien Chon Susano & Canh VIP";
        else if (targetType == 4) title = "Thu Vien Chon Sach Ki Nang & Bi Kip";
        else if (targetType == 5) title = "Thu Vien Chon Pet, Linh Thu & Thu Cuoi (Kem Anh)";

        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), title, true);
        dialog.setSize(950, 680);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout(10, 10));

        JPanel topPnl = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        topPnl.setBackground(Color.WHITE);

        JTextField txtSearch = new JTextField(20);
        txtSearch.setFont(FONT_UI);
        txtSearch.putClientProperty("JTextField.placeholderText", "Tim theo ten hoac ID...");

        JComboBox<String> cbCategory = new JComboBox<>(new String[]{
                "Tat Ca Vat Pham (Full 2000+ Items)",
                "Quan Ao / Trang Bi (Ao, Quan, Gang, Giay, Rada, Chan Thien Tu)",
                "Cai Trang (Avatar VIP)",
                "Susano / Than The (11 Bo Than The)",
                "Canh Than & Than Dyc",
                "Linh Thu & Pet VIP",
                "Sach Ki Nang & Bi Kip",
                "Ruong & Hop Qua",
                "Thoi Vang & Tien Te",
                "Ngoc Rong & Da Nang Cap",
                "Dau Than & Bo Tro"
        });
        cbCategory.setFont(FONT_BOLD);

        if (targetType == 1) cbCategory.setSelectedIndex(2);
        else if (targetType == 2) cbCategory.setSelectedIndex(1);
        else if (targetType == 3) cbCategory.setSelectedIndex(3);
        else if (targetType == 4) cbCategory.setSelectedIndex(6);
        else if (targetType == 5) cbCategory.setSelectedIndex(5);

        topPnl.add(new JLabel("Tim kiem:"));
        topPnl.add(txtSearch);
        topPnl.add(new JLabel("Phan loai:"));
        topPnl.add(cbCategory);

        JLabel lblCount = new JLabel("0 vat pham");
        lblCount.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        lblCount.setForeground(Color.GRAY);
        topPnl.add(lblCount);

        String[] cols = {"Anh", "ID", "Ten Vat Pham", "Loai", "Gioi Tinh", "Mo Ta"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 0) return ImageIcon.class;
                return Object.class;
            }
        };

        JTable table = new JTable(model);
        table.setRowHeight(38);
        table.setFont(FONT_UI);
        table.getTableHeader().setFont(FONT_BOLD);
        table.getTableHeader().setBackground(COL_HEADER);

        table.getColumnModel().getColumn(0).setMaxWidth(48);
        table.getColumnModel().getColumn(1).setMaxWidth(65);
        table.getColumnModel().getColumn(3).setMaxWidth(130);
        table.getColumnModel().getColumn(4).setMaxWidth(80);

        List<ItemData> filteredList = new ArrayList<>();

        Runnable filterData = () -> {
            String kw = txtSearch.getText().trim().toLowerCase();
            int catIdx = cbCategory.getSelectedIndex();

            filteredList.clear();
            for (ItemData it : allItemList) {
                boolean matchCat = true;
                if (catIdx == 1) matchCat = ((it.type >= 0 && it.type <= 4) || it.type == 32 || it.type == 35 || it.type == 39);
                else if (catIdx == 2) matchCat = (it.type == 5 || isCaiTrangName(it.name));
                else if (catIdx == 3) matchCat = isSusanoItem(it);
                else if (catIdx == 4) matchCat = isCanhItem(it);
                else if (catIdx == 5) matchCat = (it.type == 21 || it.type == 23 || it.type == 24 || it.type == 70 || it.type == 71 || isPetName(it.name) || isMountName(it.name));
                else if (catIdx == 6) matchCat = isSkillBook(it);
                else if (catIdx == 7) matchCat = isRuongHopName(it.name);
                else if (catIdx == 8) matchCat = (it.id == 457 || it.name.toLowerCase().contains("thoi vang") || it.name.toLowerCase().contains("thỏi vàng"));
                else if (catIdx == 9) matchCat = (it.type == 12 || it.type == 14 || it.type == 32);
                else if (catIdx == 10) matchCat = (it.type == 6 || it.type == 29 || it.type == 30);

                if (!matchCat) continue;

                if (!kw.isEmpty()) {
                    boolean matchKw = it.name.toLowerCase().contains(kw) || String.valueOf(it.id).equals(kw);
                    if (!matchKw) continue;
                }

                filteredList.add(it);
            }

            model.setRowCount(0);
            for (ItemData it : filteredList) {
                ImageIcon icon = getItemIcon(it.iconId, 32);
                String genderStr = it.gender == 0 ? "Trai Dat" : (it.gender == 1 ? "Namec" : (it.gender == 2 ? "Xayda" : "Tat ca"));
                model.addRow(new Object[]{
                        icon,
                        it.id,
                        it.name,
                        getTypeName(it.type),
                        genderStr,
                        it.description
                });
            }
            lblCount.setText(filteredList.size() + " vat pham");
        };

        txtSearch.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { filterData.run(); }
            public void removeUpdate(DocumentEvent e) { filterData.run(); }
            public void changedUpdate(DocumentEvent e) { filterData.run(); }
        });
        cbCategory.addActionListener(e -> filterData.run());

        filterData.run();

        // On selection
        Runnable onSelect = () -> {
            int row = table.getSelectedRow();
            if (row != -1 && row < filteredList.size()) {
                ItemData selected = filteredList.get(row);
                if (targetType == 1) {
                    setSelectedCaiTrang(selected);
                } else if (targetType == 2) {
                    setSelectedEquip(selected);
                } else if (targetType == 3) {
                    setSelectedSusano(selected);
                } else if (targetType == 4) {
                    setSelectedSkillBook(selected);
                } else if (targetType == 5) {
                    setSelectedPet(selected);
                } else {
                    setSelectedItem(selected);
                }
                dialog.dispose();
            }
        };

        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    onSelect.run();
                }
            }
        });

        JButton btnChoose = new JButton("Chon Vat Pham Nay");
        btnChoose.setFont(FONT_BOLD);
        btnChoose.setBackground(COL_PRIMARY);
        btnChoose.setForeground(Color.WHITE);
        btnChoose.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnChoose.addActionListener(e -> onSelect.run());

        JPanel botPnl = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        botPnl.setBackground(Color.WHITE);
        botPnl.add(btnChoose);

        dialog.add(topPnl, BorderLayout.NORTH);
        dialog.add(new JScrollPane(table), BorderLayout.CENTER);
        dialog.add(botPnl, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private boolean isRuongHopName(String name) {
        if (name == null) return false;
        String lower = name.toLowerCase();
        return lower.contains("ruong") || lower.contains("rương") || lower.contains("hop") || lower.contains("hộp");
    }

    // ==========================================
    // TAB 5: BUFF TIEN TE, EXP & CHI SO TONG HOP
    // ==========================================
    private JPanel createBuffTab() {
        JPanel container = new JPanel();
        container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
        container.setBackground(Color.WHITE);
        container.setBorder(new EmptyBorder(12, 15, 12, 15));

        // Part 1: Money & Gems / KC
        JPanel pnlMoney = new JPanel(new GridBagLayout());
        pnlMoney.setBackground(Color.WHITE);
        pnlMoney.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(new LineBorder(COL_BORDER), "1. Tai San & Tien Te (Vang, Ngoc Xanh, Hong Ngoc / KC)"),
                new EmptyBorder(8, 10, 8, 10)
        ));

        GridBagConstraints gbcM = new GridBagConstraints();
        gbcM.insets = new Insets(4, 6, 4, 6);
        gbcM.anchor = GridBagConstraints.WEST;
        gbcM.fill = GridBagConstraints.HORIZONTAL;

        JTextField txtGold = new JTextField("500000000", 10);
        JTextField txtGem = new JTextField("100000", 8);
        JTextField txtRuby = new JTextField("100000", 8);

        JPanel pnlGoldFast = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
        pnlGoldFast.setOpaque(false);
        JButton bG1 = new JButton("+100M"); bG1.addActionListener(e -> txtGold.setText("100000000"));
        JButton bG2 = new JButton("+500M"); bG2.addActionListener(e -> txtGold.setText("500000000"));
        JButton bG3 = new JButton("+1 Ty"); bG3.addActionListener(e -> txtGold.setText("1000000000"));
        JButton bG4 = new JButton("+2 Ty (Max)"); bG4.addActionListener(e -> txtGold.setText("2000000000"));
        pnlGoldFast.add(bG1); pnlGoldFast.add(bG2); pnlGoldFast.add(bG3); pnlGoldFast.add(bG4);

        JPanel pnlGemFast = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
        pnlGemFast.setOpaque(false);
        JButton bK1 = new JButton("+50K"); bK1.addActionListener(e -> { txtGem.setText("50000"); txtRuby.setText("50000"); });
        JButton bK2 = new JButton("+100K"); bK2.addActionListener(e -> { txtGem.setText("100000"); txtRuby.setText("100000"); });
        JButton bK3 = new JButton("+500K"); bK3.addActionListener(e -> { txtGem.setText("500000"); txtRuby.setText("500000"); });
        JButton bK4 = new JButton("+1M"); bK4.addActionListener(e -> { txtGem.setText("1000000"); txtRuby.setText("1000000"); });
        pnlGemFast.add(bK1); pnlGemFast.add(bK2); pnlGemFast.add(bK3); pnlGemFast.add(bK4);

        JButton btnAddMoney = new JButton("Cong Tien Te / KC");
        btnAddMoney.setBackground(COL_SUCCESS);
        btnAddMoney.setForeground(Color.WHITE);
        btnAddMoney.setFont(FONT_BOLD);
        btnAddMoney.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnAddMoney.addActionListener(e -> {
            Player p = checkAndGetTargetPlayer();
            if (p == null) return;
            try {
                long goldAdd = Long.parseLong(txtGold.getText().trim());
                int gemAdd = Integer.parseInt(txtGem.getText().trim());
                int rubyAdd = Integer.parseInt(txtRuby.getText().trim());

                if (p.inventory == null) p.inventory = new Inventory();
                if (goldAdd > 0) p.inventory.gold = Math.min(Inventory.LIMIT_GOLD, p.inventory.gold + goldAdd);
                if (gemAdd > 0) p.inventory.gem = (int) Math.min(2_000_000_000L, (long) p.inventory.gem + gemAdd);
                if (rubyAdd > 0) p.inventory.ruby = (int) Math.min(2_000_000_000L, (long) p.inventory.ruby + rubyAdd);

                if (p.getSession() != null) {
                    Service.gI().sendMoney(p);
                    Service.gI().sendThongBao(p, "Ban duoc cong tien te / KC tu Admin!");
                }
                savePlayer(p);
                String info = (p.getSession() != null) ? "(Nguoi choi Online - Da cap nhat vao game)" : "(Nguoi choi Offline - Da luu vao Database)";
                JOptionPane.showMessageDialog(this, "Da cong tien te / KC cho [" + p.name + "] thanh cong!\n" + info, "Thanh cong", JOptionPane.INFORMATION_MESSAGE);
                updatePlayerSummary(p);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Loi gia tri tien te: " + ex.getMessage());
            }
        });

        gbcM.gridx = 0; gbcM.gridy = 0; pnlMoney.add(new JLabel("Vang:"), gbcM);
        gbcM.gridx = 1; gbcM.gridy = 0; pnlMoney.add(txtGold, gbcM);
        gbcM.gridx = 2; gbcM.gridy = 0; pnlMoney.add(pnlGoldFast, gbcM);

        gbcM.gridx = 0; gbcM.gridy = 1; pnlMoney.add(new JLabel("Ngoc Xanh:"), gbcM);
        gbcM.gridx = 1; gbcM.gridy = 1; pnlMoney.add(txtGem, gbcM);
        gbcM.gridx = 2; gbcM.gridy = 1; pnlMoney.add(new JLabel("(Kim cuong xanh)"), gbcM);

        gbcM.gridx = 0; gbcM.gridy = 2; pnlMoney.add(new JLabel("Hong Ngoc / KC:"), gbcM);
        gbcM.gridx = 1; gbcM.gridy = 2; pnlMoney.add(txtRuby, gbcM);
        gbcM.gridx = 2; gbcM.gridy = 2; pnlMoney.add(pnlGemFast, gbcM);

        gbcM.gridx = 1; gbcM.gridy = 3; gbcM.gridwidth = 2; gbcM.insets = new Insets(8, 6, 4, 6);
        pnlMoney.add(btnAddMoney, gbcM);

        // Part 2: Thỏi Vàng, Rương Vàng & Combo
        JPanel pnlGoldBarAndChest = new JPanel(new GridBagLayout());
        pnlGoldBarAndChest.setBackground(Color.WHITE);
        pnlGoldBarAndChest.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(new LineBorder(COL_BORDER), "2. Thoi Vang, Ruong Vang & Combo VIP"),
                new EmptyBorder(8, 10, 8, 10)
        ));

        GridBagConstraints gbcC = new GridBagConstraints();
        gbcC.insets = new Insets(4, 6, 4, 6);
        gbcC.anchor = GridBagConstraints.WEST;
        gbcC.fill = GridBagConstraints.HORIZONTAL;

        JComboBox<String> cbDestSpecial = new JComboBox<>(new String[]{
                "Hanh trang (Tui do)",
                "Ruong do (Ruong chua / Box)"
        });
        cbDestSpecial.setFont(FONT_BOLD);

        JSpinner spGoldBars = new JSpinner(new SpinnerNumberModel(100, 1, 99999, 10));
        JButton btnAddGoldBar = new JButton("Tang Thoi Vang (457)");
        btnAddGoldBar.setFont(FONT_BOLD);
        btnAddGoldBar.setBackground(COL_PRIMARY);
        btnAddGoldBar.setForeground(Color.WHITE);
        btnAddGoldBar.addActionListener(e -> {
            Player p = checkAndGetTargetPlayer();
            if (p == null) return;
            int count = (int) spGoldBars.getValue();
            int dest = cbDestSpecial.getSelectedIndex();
            Item thoiVang = ItemService.gI().createNewItem((short) 457, count);
            boolean ok = deliverItemToPlayer(p, thoiVang, dest);
            String destName = dest == 1 ? "Ruong do (Box)" : "Hanh trang";
            if (ok) {
                if (p.getSession() != null) Service.gI().sendThongBao(p, "Ban nhan duoc " + count + " Thoi vang tu Admin!");
                JOptionPane.showMessageDialog(this, "Da gui " + count + " Thoi vang vao [" + destName + "] cua [" + p.name + "] thanh cong!");
                updatePlayerSummary(p);
            } else {
                JOptionPane.showMessageDialog(this, destName + " cua nguoi choi da day!", "Loi", JOptionPane.ERROR_MESSAGE);
            }
        });

        JPanel pnlGbFast = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
        pnlGbFast.setOpaque(false);
        JButton bGb1 = new JButton("+50"); bGb1.addActionListener(e -> spGoldBars.setValue(50));
        JButton bGb2 = new JButton("+100"); bGb2.addActionListener(e -> spGoldBars.setValue(100));
        JButton bGb3 = new JButton("+500"); bGb3.addActionListener(e -> spGoldBars.setValue(500));
        JButton bGb4 = new JButton("+1.000"); bGb4.addActionListener(e -> spGoldBars.setValue(1000));
        pnlGbFast.add(bGb1); pnlGbFast.add(bGb2); pnlGbFast.add(bGb3); pnlGbFast.add(bGb4);

        JSpinner spGoldChest = new JSpinner(new SpinnerNumberModel(20, 1, 9999, 5));
        JButton btnAddGoldChest = new JButton("Tang Ruong Vang (572)");
        btnAddGoldChest.setFont(FONT_BOLD);
        btnAddGoldChest.setBackground(COL_WARNING);
        btnAddGoldChest.setForeground(Color.WHITE);
        btnAddGoldChest.addActionListener(e -> {
            Player p = checkAndGetTargetPlayer();
            if (p == null) return;
            int count = (int) spGoldChest.getValue();
            int dest = cbDestSpecial.getSelectedIndex();
            Item ruongVang = ItemService.gI().createNewItem((short) 572, count);
            boolean ok = deliverItemToPlayer(p, ruongVang, dest);
            String destName = dest == 1 ? "Ruong do (Box)" : "Hanh trang";
            if (ok) {
                if (p.getSession() != null) Service.gI().sendThongBao(p, "Ban nhan duoc " + count + " Ruong Vang tu Admin!");
                JOptionPane.showMessageDialog(this, "Da gui " + count + " Ruong Vang vao [" + destName + "] cua [" + p.name + "] thanh cong!");
                updatePlayerSummary(p);
            } else {
                JOptionPane.showMessageDialog(this, destName + " cua nguoi choi da day!", "Loi", JOptionPane.ERROR_MESSAGE);
            }
        });

        JPanel pnlGcFast = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
        pnlGcFast.setOpaque(false);
        JButton bGc1 = new JButton("+10"); bGc1.addActionListener(e -> spGoldChest.setValue(10));
        JButton bGc2 = new JButton("+50"); bGc2.addActionListener(e -> spGoldChest.setValue(50));
        JButton bGc3 = new JButton("+100"); bGc3.addActionListener(e -> spGoldChest.setValue(100));
        pnlGcFast.add(bGc1); pnlGcFast.add(bGc2); pnlGcFast.add(bGc3);

        // Combo gói VIP
        JPanel pnlCombos = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        pnlCombos.setOpaque(false);

        JButton btnComboDragonBalls = new JButton("Tang 1 Bo 7 Ngoc Rong (1s-7s)");
        btnComboDragonBalls.setFont(FONT_BOLD);
        btnComboDragonBalls.setBackground(new Color(142, 68, 173));
        btnComboDragonBalls.setForeground(Color.WHITE);
        btnComboDragonBalls.addActionListener(e -> {
            Player p = checkAndGetTargetPlayer();
            if (p == null) return;
            int dest = cbDestSpecial.getSelectedIndex();
            for (short id = 14; id <= 20; id++) {
                Item db = ItemService.gI().createNewItem(id, 1);
                deliverItemToPlayer(p, db, dest);
            }
            if (p.getSession() != null) Service.gI().sendThongBao(p, "Ban nhan duoc Bo 7 Vien Ngoc Rong tu Admin!");
            JOptionPane.showMessageDialog(this, "Da tang tron bo 7 Vien Ngoc Rong cho [" + p.name + "]!");
            updatePlayerSummary(p);
        });

        JButton btnComboUpgradeStones = new JButton("Tang Bo Da Nang Cap (99 moi loai)");
        btnComboUpgradeStones.setFont(FONT_BOLD);
        btnComboUpgradeStones.setBackground(new Color(39, 174, 96));
        btnComboUpgradeStones.setForeground(Color.WHITE);
        btnComboUpgradeStones.addActionListener(e -> {
            Player p = checkAndGetTargetPlayer();
            if (p == null) return;
            int dest = cbDestSpecial.getSelectedIndex();
            short[] stoneIds = {220, 221, 222, 223, 224};
            for (short id : stoneIds) {
                Item st = ItemService.gI().createNewItem(id, 99);
                deliverItemToPlayer(p, st, dest);
            }
            if (p.getSession() != null) Service.gI().sendThongBao(p, "Ban nhan duoc Bo Da Nang Cap tu Admin!");
            JOptionPane.showMessageDialog(this, "Da tang bo Da Nang Cap cho [" + p.name + "]!");
            updatePlayerSummary(p);
        });

        pnlCombos.add(btnComboDragonBalls);
        pnlCombos.add(btnComboUpgradeStones);

        gbcC.gridx = 0; gbcC.gridy = 0; pnlGoldBarAndChest.add(new JLabel("Noi nhan:"), gbcC);
        gbcC.gridx = 1; gbcC.gridy = 0; pnlGoldBarAndChest.add(cbDestSpecial, gbcC);

        gbcC.gridx = 0; gbcC.gridy = 1; pnlGoldBarAndChest.add(new JLabel("Thoi Vang:"), gbcC);
        gbcC.gridx = 1; gbcC.gridy = 1; pnlGoldBarAndChest.add(spGoldBars, gbcC);
        gbcC.gridx = 2; gbcC.gridy = 1; pnlGoldBarAndChest.add(pnlGbFast, gbcC);
        gbcC.gridx = 3; gbcC.gridy = 1; pnlGoldBarAndChest.add(btnAddGoldBar, gbcC);

        gbcC.gridx = 0; gbcC.gridy = 2; pnlGoldBarAndChest.add(new JLabel("Ruong Vang:"), gbcC);
        gbcC.gridx = 1; gbcC.gridy = 2; pnlGoldBarAndChest.add(spGoldChest, gbcC);
        gbcC.gridx = 2; gbcC.gridy = 2; pnlGoldBarAndChest.add(pnlGcFast, gbcC);
        gbcC.gridx = 3; gbcC.gridy = 2; pnlGoldBarAndChest.add(btnAddGoldChest, gbcC);

        gbcC.gridx = 0; gbcC.gridy = 3; gbcC.gridwidth = 4; gbcC.insets = new Insets(8, 6, 4, 6);
        pnlGoldBarAndChest.add(pnlCombos, gbcC);

        // Part 3: EXP & Suc Manh / Tiem Nang (SMTN)
        JPanel pnlExp = new JPanel(new GridBagLayout());
        pnlExp.setBackground(Color.WHITE);
        pnlExp.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(new LineBorder(COL_BORDER), "3. Tang EXP, Suc Manh & Tiem Nang (SMTN)"),
                new EmptyBorder(8, 10, 8, 10)
        ));

        GridBagConstraints gbcE = new GridBagConstraints();
        gbcE.insets = new Insets(4, 6, 4, 6);
        gbcE.anchor = GridBagConstraints.WEST;
        gbcE.fill = GridBagConstraints.HORIZONTAL;

        JComboBox<String> cbExpTarget = new JComboBox<>(new String[]{"Su Phu (Nhan vat chinh)", "De Tu (Pet)"});
        cbExpTarget.setFont(FONT_BOLD);
        JTextField txtExpAmount = new JTextField("1000000000", 12);
        txtExpAmount.setFont(FONT_BOLD);

        JPanel pnlExpFast = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
        pnlExpFast.setOpaque(false);
        JButton bE1 = new JButton("+100 Tr"); bE1.addActionListener(e -> txtExpAmount.setText("100000000"));
        JButton bE2 = new JButton("+1 Ty"); bE2.addActionListener(e -> txtExpAmount.setText("1000000000"));
        JButton bE3 = new JButton("+10 Ty"); bE3.addActionListener(e -> txtExpAmount.setText("10000000000"));
        JButton bE4 = new JButton("+50 Ty"); bE4.addActionListener(e -> txtExpAmount.setText("50000000000"));
        JButton bE5 = new JButton("Max 80 Ty"); bE5.addActionListener(e -> txtExpAmount.setText("80000000000"));
        JButton bE6 = new JButton("Max 120 Ty"); bE6.addActionListener(e -> txtExpAmount.setText("120000000000"));
        pnlExpFast.add(bE1); pnlExpFast.add(bE2); pnlExpFast.add(bE3); pnlExpFast.add(bE4); pnlExpFast.add(bE5); pnlExpFast.add(bE6);

        JButton btnAddExp = new JButton("Cong Suc Manh / Tiem Nang");
        btnAddExp.setFont(FONT_BOLD);
        btnAddExp.setBackground(COL_SUCCESS);
        btnAddExp.setForeground(Color.WHITE);
        btnAddExp.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnAddExp.addActionListener(e -> {
            Player p = checkAndGetTargetPlayer();
            if (p == null) return;
            try {
                long amount = Long.parseLong(txtExpAmount.getText().trim());
                if (amount <= 0) {
                    JOptionPane.showMessageDialog(this, "So luong SMTN phai > 0!");
                    return;
                }
                boolean isMaster = (cbExpTarget.getSelectedIndex() == 0);
                Player target = isMaster ? p : p.Detu;

                if (target == null) {
                    JOptionPane.showMessageDialog(this, "Nguoi choi chua co de tu!", "Thong bao", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                if (target.nPoint == null) {
                    JOptionPane.showMessageDialog(this, "Khong tim thay du lieu chi so!");
                    return;
                }

                // Auto-upgrade limitPower if needed
                while (target.nPoint.limitPower < NPoint.MAX_LIMIT && target.nPoint.getPowerLimit() < target.nPoint.power + amount) {
                    target.nPoint.limitPower++;
                    target.nPoint.powerLimit = PowerLimitManager.getInstance().get(target.nPoint.limitPower);
                }
                target.nPoint.power += amount;
                target.nPoint.tiemNang += amount;
                target.nPoint.calPoint();

                if (target.getSession() != null || (isMaster && p.getSession() != null)) {
                    PlayerService.gI().sendTNSM(target, (byte) 2, amount);
                    Service.gI().point(target);
                    Service.gI().Send_Info_NV(target);
                    if (isMaster) {
                        Service.gI().sendThongBao(p, "Ban duoc cong " + Util.format(amount) + " Suc manh & Tiem nang!");
                    } else {
                        Service.gI().sendThongBao(p, "De tu cua ban duoc cong " + Util.format(amount) + " SMTN!");
                    }
                }
                savePlayer(p);
                String info = (p.getSession() != null) ? "(Nguoi choi Online - Da cap nhat)" : "(Nguoi choi Offline - Da luu vao DB)";
                JOptionPane.showMessageDialog(this, "Da cong " + Util.format(amount) + " SMTN cho [" + target.name + "] thanh cong!\n" + info, "Thanh cong", JOptionPane.INFORMATION_MESSAGE);
                updatePlayerSummary(p);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Loi dinh dang so SMTN: " + ex.getMessage());
            }
        });

        JButton btnMaxLimitPower = new JButton("🔓 Mo Max Gioi Han SM (Cap 13 / 120 Ty)");
        btnMaxLimitPower.setFont(FONT_BOLD);
        btnMaxLimitPower.setBackground(COL_PRIMARY);
        btnMaxLimitPower.setForeground(Color.WHITE);
        btnMaxLimitPower.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnMaxLimitPower.addActionListener(e -> {
            Player p = checkAndGetTargetPlayer();
            if (p == null) return;
            boolean isMaster = (cbExpTarget.getSelectedIndex() == 0);
            Player target = isMaster ? p : p.Detu;
            if (target == null) {
                JOptionPane.showMessageDialog(this, "Nguoi choi chua co de tu!", "Thong bao", JOptionPane.WARNING_MESSAGE);
                return;
            }
            target.nPoint.limitPower = NPoint.MAX_LIMIT;
            target.nPoint.powerLimit = PowerLimitManager.getInstance().get(NPoint.MAX_LIMIT);
            savePlayer(p);
            if (target.getSession() != null || (isMaster && p.getSession() != null)) {
                Service.gI().point(target);
                Service.gI().Send_Info_NV(target);
                Service.gI().sendThongBao(p, (isMaster ? "Ban" : "De tu") + " da duoc mo Max Gioi han Suc manh (Cap 13)!");
            }
            JOptionPane.showMessageDialog(this, "Da mo Max Gioi han Suc manh (Cap 13 / 120 Ty) cho [" + target.name + "] thanh cong!", "Thanh cong", JOptionPane.INFORMATION_MESSAGE);
            updatePlayerSummary(p);
        });

        gbcE.gridx = 0; gbcE.gridy = 0; pnlExp.add(new JLabel("Muc tieu:"), gbcE);
        gbcE.gridx = 1; gbcE.gridy = 0; pnlExp.add(cbExpTarget, gbcE);

        gbcE.gridx = 0; gbcE.gridy = 1; pnlExp.add(new JLabel("So SMTN:"), gbcE);
        gbcE.gridx = 1; gbcE.gridy = 1; pnlExp.add(txtExpAmount, gbcE);
        gbcE.gridx = 2; gbcE.gridy = 1; pnlExp.add(pnlExpFast, gbcE);

        JPanel pnlExpBtns = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        pnlExpBtns.setOpaque(false);
        pnlExpBtns.add(btnAddExp);
        pnlExpBtns.add(btnMaxLimitPower);

        gbcE.gridx = 1; gbcE.gridy = 2; gbcE.gridwidth = 2; gbcE.insets = new Insets(8, 6, 4, 6);
        pnlExp.add(pnlExpBtns, gbcE);

        // Part 4: Stats
        JPanel pnlStats = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 5));
        pnlStats.setBackground(Color.WHITE);
        pnlStats.setBorder(new TitledBorder(new LineBorder(COL_BORDER), "4. Chi So Goc (Vinh Vien)"));

        JTextField txtHpg = new JTextField("100000", 7);
        JTextField txtMpg = new JTextField("100000", 7);
        JTextField txtDameg = new JTextField("10000", 7);
        JTextField txtDefg = new JTextField("500", 5);
        JTextField txtCritg = new JTextField("10", 4);

        JButton btnSaveStats = new JButton("Ap Dung Chi So Goc");
        btnSaveStats.setBackground(COL_SUCCESS);
        btnSaveStats.setForeground(Color.WHITE);
        btnSaveStats.setFont(FONT_BOLD);
        btnSaveStats.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnSaveStats.addActionListener(e -> {
            Player p = checkAndGetTargetPlayer();
            if (p == null) return;
            try {
                p.nPoint.hpg = Long.parseLong(txtHpg.getText().trim());
                p.nPoint.mpg = Long.parseLong(txtMpg.getText().trim());
                p.nPoint.dameg = Long.parseLong(txtDameg.getText().trim());
                p.nPoint.defg = Integer.parseInt(txtDefg.getText().trim());
                p.nPoint.critg = Integer.parseInt(txtCritg.getText().trim());
                p.nPoint.calPoint();

                if (p.getSession() != null) {
                    Service.gI().point(p);
                    Service.gI().sendThongBao(p, "Chi so goc cua ban da duoc Admin cap nhat!");
                }
                savePlayer(p);
                String info = (p.getSession() != null) ? "(Nguoi choi Online - Da cap nhat truc tiep)" : "(Nguoi choi Offline - Da luu vao Database)";
                JOptionPane.showMessageDialog(this, "Cap nhat chi so goc thanh cong cho [" + p.name + "]!\n" + info, "Thanh cong", JOptionPane.INFORMATION_MESSAGE);
                updatePlayerSummary(p);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Dinh dang chi so khong hop le: " + ex.getMessage());
            }
        });

        JButton btnPreset1 = new JButton("Tan Thu");
        btnPreset1.addActionListener(e -> { txtHpg.setText("100000"); txtMpg.setText("100000"); txtDameg.setText("10000"); txtDefg.setText("500"); txtCritg.setText("10"); });
        JButton btnPreset2 = new JButton("Pro VIP");
        btnPreset2.addActionListener(e -> { txtHpg.setText("500000"); txtMpg.setText("500000"); txtDameg.setText("50000"); txtDefg.setText("2000"); txtCritg.setText("30"); });
        JButton btnPreset3 = new JButton("God Mode");
        btnPreset3.addActionListener(e -> { txtHpg.setText("2000000"); txtMpg.setText("2000000"); txtDameg.setText("200000"); txtDefg.setText("5000"); txtCritg.setText("50"); });

        pnlStats.add(new JLabel("HP:")); pnlStats.add(txtHpg);
        pnlStats.add(new JLabel("KI:")); pnlStats.add(txtMpg);
        pnlStats.add(new JLabel("SD:")); pnlStats.add(txtDameg);
        pnlStats.add(new JLabel("Giap:")); pnlStats.add(txtDefg);
        pnlStats.add(new JLabel("Crit(%):")); pnlStats.add(txtCritg);
        pnlStats.add(btnPreset1); pnlStats.add(btnPreset2); pnlStats.add(btnPreset3);
        pnlStats.add(btnSaveStats);

        // Part 5: Heal & Pet
        JPanel pnlExtra = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 8));
        pnlExtra.setBackground(Color.WHITE);
        pnlExtra.setBorder(new TitledBorder(new LineBorder(COL_BORDER), "5. Hoi Phuc & Quan Ly De Tu"));

        JButton btnFullHpMaster = new JButton("Hoi Full 100% HP/KI Su Phu");
        btnFullHpMaster.setBackground(COL_HEADER);
        btnFullHpMaster.setFont(FONT_BOLD);
        btnFullHpMaster.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnFullHpMaster.addActionListener(e -> {
            Player p = checkAndGetTargetPlayer();
            if (p == null) return;
            p.nPoint.hp = p.nPoint.hpMax;
            p.nPoint.mp = p.nPoint.mpMax;
            if (p.getSession() != null) {
                Service.gI().Send_Info_NV(p);
                Service.gI().sendThongBao(p, "Admin da hoi phuc day du HP & KI cho ban!");
            }
            savePlayer(p);
            JOptionPane.showMessageDialog(this, "Da hoi phuc 100% HP & KI cho Su phu!", "Hoan tat", JOptionPane.INFORMATION_MESSAGE);
        });

        JButton btnFullHpPet = new JButton("Hoi Full 100% HP/KI De Tu");
        btnFullHpPet.setBackground(COL_HEADER);
        btnFullHpPet.setFont(FONT_BOLD);
        btnFullHpPet.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnFullHpPet.addActionListener(e -> {
            Player p = checkAndGetTargetPlayer();
            if (p == null) return;
            if (p.Detu == null) {
                JOptionPane.showMessageDialog(this, "Nguoi choi nay chua co de tu!", "Thong bao", JOptionPane.WARNING_MESSAGE);
                return;
            }
            p.Detu.nPoint.hp = p.Detu.nPoint.hpMax;
            p.Detu.nPoint.mp = p.Detu.nPoint.mpMax;
            if (p.getSession() != null) {
                Service.gI().sendThongBao(p, "De tu cua ban da duoc hoi phuc day du HP & KI!");
            }
            savePlayer(p);
            JOptionPane.showMessageDialog(this, "Da hoi phuc de tu!", "Hoan tat", JOptionPane.INFORMATION_MESSAGE);
        });

        JButton btnCreatePet = new JButton("Tao De Tu Moi");
        btnCreatePet.setBackground(COL_PRIMARY);
        btnCreatePet.setForeground(Color.WHITE);
        btnCreatePet.setFont(FONT_BOLD);
        btnCreatePet.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnCreatePet.addActionListener(e -> {
            Player p = checkAndGetTargetPlayer();
            if (p == null) return;
            if (p.Detu != null) {
                JOptionPane.showMessageDialog(this, "Nguoi choi da co de tu roi!", "Thong bao", JOptionPane.WARNING_MESSAGE);
                return;
            }
            String[] genders = {"Trai Dat", "Namec", "Xayda"};
            int choice = JOptionPane.showOptionDialog(this, "Chon hanh tinh cho de tu:", "Tao De Tu",
                    JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, genders, genders[0]);
            if (choice >= 0) {
                DetuService.gI().createNormalPetByGender(p, choice, p.nPoint.limitPower);
                if (p.getSession() != null) {
                    Service.gI().sendThongBao(p, "Ban vua nhan duoc mot de tu moi tu Admin!");
                }
                savePlayer(p);
                JOptionPane.showMessageDialog(this, "Da tao de tu thanh cong cho [" + p.name + "]!", "Thanh cong", JOptionPane.INFORMATION_MESSAGE);
                updatePlayerSummary(p);
            }
        });

        pnlExtra.add(btnFullHpMaster);
        pnlExtra.add(btnFullHpPet);
        pnlExtra.add(btnCreatePet);

        container.add(pnlMoney);
        container.add(Box.createVerticalStrut(8));
        container.add(pnlGoldBarAndChest);
        container.add(Box.createVerticalStrut(8));
        container.add(pnlExp);
        container.add(Box.createVerticalStrut(8));
        container.add(pnlStats);
        container.add(Box.createVerticalStrut(8));
        container.add(pnlExtra);

        JScrollPane scroll = new JScrollPane(container);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(16);

        JPanel wrap = new JPanel(new BorderLayout());
        wrap.setBackground(Color.WHITE);
        wrap.add(scroll, BorderLayout.CENTER);
        return wrap;
    }

    // ==========================================
    // TAB 6: BO QUA & QUAN LY NHIEM VU
    // ==========================================
    private JPanel createTaskTab() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel lblTaskInfo = new JLabel("Chon nguoi choi ben trai de xem thong tin nhiem vu");
        lblTaskInfo.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblTaskInfo.setForeground(COL_PRIMARY);

        JButton btnSkipTask = new JButton("BO QUA NHIEM VU NAY (Nhan Qua & Sang NV Ke)");
        btnSkipTask.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnSkipTask.setBackground(COL_WARNING);
        btnSkipTask.setForeground(Color.WHITE);
        btnSkipTask.setPreferredSize(new Dimension(380, 42));
        btnSkipTask.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnSkipTask.addActionListener(e -> {
            Player p = checkAndGetTargetPlayer();
            if (p == null) return;
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Xac nhan hoan thanh ngay nhiem vu hien tai va chuyen sang nhiem vu ke tiep cho [" + p.name + "]?",
                    "Xac nhan Bo Qua NV", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                TaskService.gI().sendNextTaskMain(p);
                savePlayer(p);
                JOptionPane.showMessageDialog(this, "Da chuyen sang nhiem vu tiep theo thanh cong!", "Hoan tat", JOptionPane.INFORMATION_MESSAGE);
                updatePlayerSummary(p);
            }
        });

        JButton btnCompleteSub = new JButton("Hoan Thanh Buoc Hien Tai (Max Tien Do Sub-Task)");
        btnCompleteSub.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnCompleteSub.setBackground(COL_SUCCESS);
        btnCompleteSub.setForeground(Color.WHITE);
        btnCompleteSub.setPreferredSize(new Dimension(380, 42));
        btnCompleteSub.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnCompleteSub.addActionListener(e -> {
            Player p = checkAndGetTargetPlayer();
            if (p == null) return;
            if (p.playerTask != null && p.playerTask.taskMain != null) {
                int subIdx = p.playerTask.taskMain.index;
                if (subIdx >= 0 && subIdx < p.playerTask.taskMain.subTasks.size()) {
                    var st = p.playerTask.taskMain.subTasks.get(subIdx);
                    st.count = st.maxCount;
                    TaskService.gI().sendUpdateCountSubTask(p);
                    if (p.getSession() != null) {
                        Service.gI().sendThongBao(p, "Nhiem vu da dat du tien do! Hay den gap NPC de tra nhiem vu.");
                    }
                    savePlayer(p);
                    JOptionPane.showMessageDialog(this, "Da hoan thanh tien do buoc hien tai (" + st.maxCount + "/" + st.maxCount + ")!", "Hoan tat", JOptionPane.INFORMATION_MESSAGE);
                    updatePlayerSummary(p);
                }
            }
        });

        JPanel jumpPnl = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
        jumpPnl.setBackground(Color.WHITE);
        jumpPnl.setBorder(new TitledBorder(new LineBorder(COL_BORDER), "Hoac Nhay Den Nhiem Vu Tuy Chon"));

        JComboBox<String> cbAllTasks = new JComboBox<>();
        if (Manager.TASKS != null) {
            for (TaskMain tm : Manager.TASKS) {
                if (tm != null) cbAllTasks.addItem("[" + tm.id + "] " + tm.name);
            }
        }
        JSpinner spSubIndex = new JSpinner(new SpinnerNumberModel(0, 0, 10, 1));
        JButton btnJump = new JButton("Nhay Den NV Nay");
        btnJump.setFont(FONT_BOLD);
        btnJump.setBackground(COL_PRIMARY);
        btnJump.setForeground(Color.WHITE);
        btnJump.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnJump.addActionListener(e -> {
            Player p = checkAndGetTargetPlayer();
            if (p == null) return;
            int selIdx = cbAllTasks.getSelectedIndex();
            if (Manager.TASKS != null && selIdx >= 0 && selIdx < Manager.TASKS.size()) {
                TaskMain selTask = Manager.TASKS.get(selIdx);
                int subIndex = (int) spSubIndex.getValue();
                setPlayerTask(p, selTask.id, subIndex);
                updatePlayerSummary(p);
            }
        });

        jumpPnl.add(new JLabel("Chon NV:"));
        jumpPnl.add(cbAllTasks);
        jumpPnl.add(new JLabel("Buoc (Index):"));
        jumpPnl.add(spSubIndex);
        jumpPnl.add(btnJump);

        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2; panel.add(lblTaskInfo, gbc);
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 2; panel.add(btnSkipTask, gbc);
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2; panel.add(btnCompleteSub, gbc);
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2; panel.add(jumpPnl, gbc);

        JPanel wrap = new JPanel(new BorderLayout());
        wrap.setBackground(Color.WHITE);
        wrap.add(panel, BorderLayout.NORTH);
        return wrap;
    }

    private void setPlayerTask(Player player, int taskId, int subTaskIndex) {
        try {
            TaskMain newTask = TaskService.gI().getTaskMainById(player, taskId);
            if (newTask.id != taskId) {
                JOptionPane.showMessageDialog(this, "ID Nhiem vu chinh khong ton tai: " + taskId, "Loi", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (subTaskIndex < 0 || subTaskIndex >= newTask.subTasks.size()) {
                JOptionPane.showMessageDialog(this, "Index nhiem vu con khong hop le. Phai tu 0 den " + (newTask.subTasks.size() - 1), "Loi", JOptionPane.ERROR_MESSAGE);
                return;
            }
            newTask.index = (byte) subTaskIndex;
            newTask.subTasks.get(newTask.index).count = 0;
            player.playerTask.taskMain = newTask;
            TaskService.gI().sendTaskMain(player);
            if (player.getSession() != null) {
                Service.gI().sendThongBao(player, "Nhiem vu hien tai cua ban la: " + player.playerTask.taskMain.subTasks.get(player.playerTask.taskMain.index).name);
            }
            savePlayer(player);
            JOptionPane.showMessageDialog(this, "Chuyen nhiem vu thanh cong cho [" + player.name + "]!", "Hoan tat", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Co loi xay ra: " + e.getMessage(), "Loi", JOptionPane.ERROR_MESSAGE);
        }
    }

    // ==========================================
    // TAB 7: TANG DANH HIEU (BADGES)
    // ==========================================
    public static File getBadgeOriginalImageFile(int idEffect) {
        if (idEffect <= 0) return null;
        String[] zoomLevels = {"x4", "x3", "x2", "x1"};
        for (String zoom : zoomLevels) {
            File f = new File("data/effect/" + zoom + "/ImgEffect_" + idEffect + ".png");
            if (f.exists()) return f;
        }
        File f = new File("data/effect/ImgEffect_" + idEffect + ".png");
        if (f.exists()) return f;
        return null;
    }

    private ImageIcon getBadgeDisplayIcon(BadgeInfo b, int targetWidth, int targetHeight) {
        if (b == null) return null;
        int cacheKey = b.idEffect * 10000 + targetWidth;
        ImageIcon cached = badgeImageCache.get(cacheKey);
        if (cached != null) return cached;

        File f = getBadgeOriginalImageFile(b.idEffect);
        if (f != null && f.exists()) {
            try {
                BufferedImage original = ImageIO.read(f);
                if (original != null) {
                    int origW = original.getWidth();
                    int origH = original.getHeight();
                    double ratioW = (double) targetWidth / origW;
                    double ratioH = (double) targetHeight / origH;
                    double ratio = Math.min(ratioW, ratioH);
                    int newW = Math.max(1, (int) (origW * ratio));
                    int newH = Math.max(1, (int) (origH * ratio));

                    Image scaled = original.getScaledInstance(newW, newH, Image.SCALE_SMOOTH);
                    ImageIcon ico = new ImageIcon(scaled);
                    badgeImageCache.put(cacheKey, ico);
                    return ico;
                }
            } catch (Exception ignored) {}
        }

        int iconId = b.iconId;
        if (iconId <= 0 && b.idItem > 0 && itemMap.containsKey(b.idItem)) {
            iconId = itemMap.get(b.idItem).iconId;
        }
        if (iconId > 0) {
            ImageIcon itemIco = getItemIcon(iconId, Math.min(targetWidth, targetHeight));
            if (itemIco != null) {
                badgeImageCache.put(cacheKey, itemIco);
                return itemIco;
            }
        }

        return null;
    }

    private void showBadgeImageDialog(BadgeInfo badge) {
        if (badge == null) {
            JOptionPane.showMessageDialog(this, "Vui long chon mot danh hieu truoc!", "Thong bao", JOptionPane.WARNING_MESSAGE);
            return;
        }

        JDialog dlg = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Hinh Anh Danh Hieu: " + badge.name + " (Effect " + badge.idEffect + ")", true);
        dlg.setSize(540, 370);
        dlg.setLocationRelativeTo(this);
        dlg.setLayout(new BorderLayout(12, 12));
        dlg.getContentPane().setBackground(Color.WHITE);

        JPanel top = new JPanel(new GridLayout(3, 1, 3, 3));
        top.setOpaque(false);
        top.setBorder(new EmptyBorder(12, 15, 6, 15));

        JLabel lblT = new JLabel(badge.name);
        lblT.setFont(new Font("Segoe UI", Font.BOLD, 17));
        lblT.setForeground(COL_PRIMARY);

        JLabel lblEff = new JLabel("ID Effect: " + badge.idEffect + " | ID Item: " + badge.idItem + " (Icon ID: " + badge.iconId + ")");
        lblEff.setFont(FONT_BOLD);

        JLabel lblOpt = new JLabel("<html><b>Quyen nang:</b> <font color='#27ae60'>" + badge.optionSummary + "</font></html>");
        lblOpt.setFont(FONT_UI);

        top.add(lblT);
        top.add(lblEff);
        top.add(lblOpt);

        JPanel center = new JPanel(new BorderLayout(8, 8));
        center.setOpaque(false);
        center.setBorder(new EmptyBorder(6, 15, 6, 15));

        File imgFile = getBadgeOriginalImageFile(badge.idEffect);
        JLabel lblImage = new JLabel();
        lblImage.setHorizontalAlignment(SwingConstants.CENTER);
        lblImage.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(200, 215, 235), 2, true),
                new EmptyBorder(10, 10, 10, 10)
        ));
        lblImage.setBackground(new Color(245, 248, 252));
        lblImage.setOpaque(true);

        String fileInfoText = "Chua co tap tin anh ImgEffect_" + badge.idEffect + ".png trong data/effect/x4";
        if (imgFile != null && imgFile.exists()) {
            try {
                BufferedImage bimg = ImageIO.read(imgFile);
                if (bimg != null) {
                    fileInfoText = "Kich thuoc goc: " + bimg.getWidth() + " x " + bimg.getHeight() + " px | File: " + imgFile.getPath().replace("\\", "/");
                    int targetW = Math.min(480, Math.max(bimg.getWidth() * 2, 240));
                    int targetH = Math.min(120, Math.max(bimg.getHeight() * 2, 50));
                    Image sc = bimg.getScaledInstance(targetW, targetH, Image.SCALE_SMOOTH);
                    lblImage.setIcon(new ImageIcon(sc));
                }
            } catch (Exception ignored) {}
        } else {
            lblImage.setText("<html><center><font color='red'>Khong tim thay file anh ImgEffect_" + badge.idEffect + ".png</font><br>Hien thi Icon Vat Pham thay the:</center></html>");
            if (badge.iconId > 0) {
                lblImage.setIcon(getItemIcon(badge.iconId, 64));
            }
        }

        JLabel lblPath = new JLabel("<html><center><i>" + fileInfoText + "</i></center></html>", SwingConstants.CENTER);
        lblPath.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblPath.setForeground(Color.GRAY);

        center.add(lblImage, BorderLayout.CENTER);
        center.add(lblPath, BorderLayout.SOUTH);

        JPanel bot = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        bot.setOpaque(false);

        JButton btnOpenExplorer = new JButton("📁 Mo File Trong Thu Muc (Explorer)");
        btnOpenExplorer.setFont(FONT_UI);
        btnOpenExplorer.setBackground(COL_PRIMARY);
        btnOpenExplorer.setForeground(Color.WHITE);
        btnOpenExplorer.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnOpenExplorer.addActionListener(e -> {
            try {
                if (imgFile != null && imgFile.exists()) {
                    Runtime.getRuntime().exec("explorer.exe /select,\"" + imgFile.getAbsolutePath() + "\"");
                } else {
                    File dir = new File("data/effect/x4");
                    if (!dir.exists()) dir = new File("data/effect");
                    if (Desktop.isDesktopSupported()) {
                        Desktop.getDesktop().open(dir);
                    } else {
                        Runtime.getRuntime().exec("explorer.exe \"" + dir.getAbsolutePath() + "\"");
                    }
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dlg, "Khong the mo thu muc: " + ex.getMessage());
            }
        });

        JButton btnClose = new JButton("Dong");
        btnClose.setFont(FONT_UI);
        btnClose.addActionListener(e -> dlg.dispose());

        bot.add(btnOpenExplorer);
        bot.add(btnClose);

        dlg.add(top, BorderLayout.NORTH);
        dlg.add(center, BorderLayout.CENTER);
        dlg.add(bot, BorderLayout.SOUTH);
        dlg.setVisible(true);
    }

    public static String formatBadgeOption(int optionId, int param) {
        switch (optionId) {
            case 50: return "Suc danh: +" + param + "%";
            case 77: return "HP toi da: +" + param + "%";
            case 103: return "KI toi da: +" + param + "%";
            case 5: return "Suc danh cong them: +" + param;
            case 14: return "Chi mang: +" + param + "%";
            case 147: return "Suc danh, HP, KI: +" + param + "%";
            case 80: return "Hoi phuc HP: +" + param + "%";
            case 81: return "Hoi phuc KI: +" + param + "%";
            case 94: return "Giap: +" + param + "%";
            case 108: return "Ne don: +" + param + "%";
            case 106: return "Khong bi quai chu dong danh";
            case 30: return "Khong the giao dich";
            case 73: return "Vinh vien";
            case 93: return "HSD: " + param + " ngay";
            default:
                try {
                    var optTemp = ItemService.gI().getItemOptionTemplate(optionId);
                    if (optTemp != null && optTemp.name != null) {
                        return optTemp.name.replaceAll("%d", String.valueOf(param));
                    }
                } catch (Exception ignored) {}
                return "Option [" + optionId + "]: +" + param;
        }
    }

    private void setSelectedBadge(BadgeInfo badge) {
        this.currentSelectedBadge = badge;
        if (badge == null) {
            lblBadgeTitle.setText("Chua chon danh hieu");
            lblBadgeDesc.setText("<html><i>Chon danh hieu ben danh sach de xem chi tiet</i></html>");
            if (lblBadgeIconPreview != null) lblBadgeIconPreview.setIcon(null);
            if (lblBadgeBannerPreview != null) {
                lblBadgeBannerPreview.setIcon(null);
                lblBadgeBannerPreview.setText("(Chon danh hieu de xem anh)");
            }
            return;
        }
        lblBadgeTitle.setText(badge.name + " (Effect ID: " + badge.idEffect + ")");
        lblBadgeDesc.setText("<html><b>ID Effect:</b> " + badge.idEffect + " | <b>ID Item:</b> " + badge.idItem + "<br><b>Quyen nang / Chi so:</b> <font color='#0078D7'>" + badge.optionSummary + "</font></html>");

        if (lblBadgeBannerPreview != null) {
            ImageIcon banner = getBadgeDisplayIcon(badge, 240, 48);
            lblBadgeBannerPreview.setIcon(banner);
            lblBadgeBannerPreview.setText(banner == null ? "(Chua co anh banner)" : "");
        }
        if (lblBadgeIconPreview != null) {
            int iId = badge.iconId > 0 ? badge.iconId : (itemMap.containsKey(badge.idItem) ? itemMap.get(badge.idItem).iconId : -1);
            if (iId > 0) {
                lblBadgeIconPreview.setIcon(getItemIcon(iId, 44));
            } else {
                lblBadgeIconPreview.setIcon(null);
            }
        }
    }

    private JPanel createGiveBadgesTab() {
        JPanel root = new JPanel(new BorderLayout(10, 10));
        root.setBackground(Color.WHITE);
        root.setBorder(new EmptyBorder(10, 10, 10, 10));

        // LEFT: Table of badges + Search + Quick Pick
        JPanel left = new JPanel(new BorderLayout(8, 8));
        left.setBackground(Color.WHITE);
        left.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(COL_BORDER, 1, true),
                new EmptyBorder(8, 8, 8, 8)
        ));

        JPanel topSearch = new JPanel(new BorderLayout(5, 5));
        topSearch.setBackground(Color.WHITE);

        txtSearchBadge = new JTextField();
        txtSearchBadge.setFont(FONT_UI);
        txtSearchBadge.putClientProperty("JTextField.placeholderText", "Tim ten danh hieu hoac ID Effect...");
        txtSearchBadge.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                String kw = txtSearchBadge.getText().trim();
                if (badgeSorter != null) {
                    if (kw.isEmpty()) badgeSorter.setRowFilter(null);
                    else badgeSorter.setRowFilter(RowFilter.regexFilter("(?i)" + kw, 1, 2));
                }
            }
        });
        topSearch.add(new JLabel("Tim kiem:"), BorderLayout.WEST);
        topSearch.add(txtSearchBadge, BorderLayout.CENTER);

        // Quick buttons row
        JPanel quickPnl = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 4));
        quickPnl.setBackground(Color.WHITE);
        String[] quickBadges = {"Admin", "Chien Than", "Dai Gia", "Canh Sat", "Trum San Boss", "De Nhat Bang", "Tu Tien", "Tu La", "Bat Bai", "Fan Cung", "Em Xinh"};
        for (String q : quickBadges) {
            JButton btn = new JButton(q);
            btn.setFont(new Font("Segoe UI", Font.PLAIN, 11));
            btn.setMargin(new Insets(2, 6, 2, 6));
            btn.setBackground(COL_HEADER);
            btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
            btn.addActionListener(e -> {
                txtSearchBadge.setText(q);
                if (badgeSorter != null) {
                    badgeSorter.setRowFilter(RowFilter.regexFilter("(?i)" + q, 1, 2));
                }
                for (int i = 0; i < badgeModel.getRowCount(); i++) {
                    String bName = (String) badgeModel.getValueAt(i, 2);
                    if (bName != null && bName.toLowerCase().contains(q.toLowerCase())) {
                        int viewIdx = badgeTable.convertRowIndexToView(i);
                        if (viewIdx != -1) {
                            badgeTable.setRowSelectionInterval(viewIdx, viewIdx);
                            badgeTable.scrollRectToVisible(badgeTable.getCellRect(viewIdx, 0, true));
                            int effId = (int) badgeModel.getValueAt(i, 1);
                            setSelectedBadge(badgeMap.get(effId));
                        }
                        break;
                    }
                }
            });
            quickPnl.add(btn);
        }

        JPanel topContainer = new JPanel(new BorderLayout(5, 5));
        topContainer.setBackground(Color.WHITE);
        topContainer.add(topSearch, BorderLayout.NORTH);
        topContainer.add(quickPnl, BorderLayout.SOUTH);

        String[] cols = {"Anh Danh Hieu", "ID Effect", "Ten Danh Hieu", "ID Item", "Chi So / Quyen Nang"};
        badgeModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 0) return ImageIcon.class;
                return Object.class;
            }
        };
        badgeTable = new JTable(badgeModel);
        badgeTable.setFont(FONT_UI);
        badgeTable.setRowHeight(40);
        badgeTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        badgeSorter = new TableRowSorter<>(badgeModel);
        badgeTable.setRowSorter(badgeSorter);

        badgeTable.getColumnModel().getColumn(0).setPreferredWidth(140);
        badgeTable.getColumnModel().getColumn(0).setMaxWidth(160);
        badgeTable.getColumnModel().getColumn(1).setPreferredWidth(70);
        badgeTable.getColumnModel().getColumn(1).setMaxWidth(80);
        badgeTable.getColumnModel().getColumn(2).setPreferredWidth(160);
        badgeTable.getColumnModel().getColumn(3).setPreferredWidth(70);
        badgeTable.getColumnModel().getColumn(3).setMaxWidth(80);
        badgeTable.getColumnModel().getColumn(4).setPreferredWidth(230);

        badgeTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int row = badgeTable.getSelectedRow();
                if (row != -1) {
                    int modelRow = badgeTable.convertRowIndexToModel(row);
                    int effId = (int) badgeModel.getValueAt(modelRow, 1);
                    BadgeInfo bi = badgeMap.get(effId);
                    if (bi != null) setSelectedBadge(bi);
                }
            }
        });

        badgeTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    if (currentSelectedBadge != null) {
                        showBadgeImageDialog(currentSelectedBadge);
                    }
                }
            }
        });

        JScrollPane scrollTable = new JScrollPane(badgeTable);
        scrollTable.setBorder(new LineBorder(COL_BORDER));

        left.add(topContainer, BorderLayout.NORTH);
        left.add(scrollTable, BorderLayout.CENTER);

        // RIGHT: Details Preview Card & Action Panel
        JPanel right = new JPanel(new BorderLayout(10, 10));
        right.setBackground(Color.WHITE);
        right.setPreferredSize(new Dimension(400, 0));
        right.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(COL_BORDER, 1, true),
                new EmptyBorder(12, 12, 12, 12)
        ));

        // Preview Card
        JPanel card = new JPanel(new BorderLayout(8, 8));
        card.setBackground(new Color(248, 250, 252));
        card.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(210, 225, 245), 1, true),
                new EmptyBorder(10, 10, 10, 10)
        ));

        lblBadgeBannerPreview = new JLabel();
        lblBadgeBannerPreview.setPreferredSize(new Dimension(250, 52));
        lblBadgeBannerPreview.setHorizontalAlignment(SwingConstants.CENTER);
        lblBadgeBannerPreview.setBorder(new LineBorder(new Color(215, 225, 240), 1, true));
        lblBadgeBannerPreview.setBackground(Color.WHITE);
        lblBadgeBannerPreview.setOpaque(true);

        lblBadgeIconPreview = new JLabel();
        lblBadgeIconPreview.setPreferredSize(new Dimension(52, 52));
        lblBadgeIconPreview.setHorizontalAlignment(SwingConstants.CENTER);
        lblBadgeIconPreview.setBorder(new LineBorder(new Color(215, 225, 240), 1, true));
        lblBadgeIconPreview.setBackground(Color.WHITE);
        lblBadgeIconPreview.setOpaque(true);

        JPanel imageBox = new JPanel(new BorderLayout(6, 0));
        imageBox.setOpaque(false);
        imageBox.add(lblBadgeBannerPreview, BorderLayout.CENTER);
        imageBox.add(lblBadgeIconPreview, BorderLayout.EAST);

        JPanel titleBox = new JPanel(new GridLayout(2, 1, 3, 3));
        titleBox.setOpaque(false);
        lblBadgeTitle = new JLabel("Chua chon danh hieu");
        lblBadgeTitle.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblBadgeTitle.setForeground(COL_PRIMARY);

        lblBadgeDesc = new JLabel("<html><i>Chon danh hieu de xem chi tiet</i></html>");
        lblBadgeDesc.setFont(FONT_UI);
        titleBox.add(lblBadgeTitle);
        titleBox.add(lblBadgeDesc);

        // Buttons for image actions
        JPanel btnImageBox = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 2));
        btnImageBox.setOpaque(false);

        JButton btnZoomBadge = new JButton("🔍 Xem Anh Goc");
        btnZoomBadge.setFont(new Font("Segoe UI", Font.BOLD, 11));
        btnZoomBadge.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnZoomBadge.addActionListener(e -> showBadgeImageDialog(currentSelectedBadge));

        JButton btnOpenFolder = new JButton("📁 Thu Muc Anh");
        btnOpenFolder.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        btnOpenFolder.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnOpenFolder.addActionListener(e -> {
            try {
                File dir = new File("data/effect/x4");
                if (!dir.exists()) dir = new File("data/effect");
                if (Desktop.isDesktopSupported()) {
                    Desktop.getDesktop().open(dir);
                } else {
                    Runtime.getRuntime().exec("explorer.exe \"" + dir.getAbsolutePath() + "\"");
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Khong the mo thu muc: " + ex.getMessage());
            }
        });

        btnImageBox.add(btnZoomBadge);
        btnImageBox.add(btnOpenFolder);

        JPanel topCardPanel = new JPanel(new BorderLayout(4, 4));
        topCardPanel.setOpaque(false);
        topCardPanel.add(titleBox, BorderLayout.CENTER);
        topCardPanel.add(btnImageBox, BorderLayout.SOUTH);

        card.add(imageBox, BorderLayout.NORTH);
        card.add(topCardPanel, BorderLayout.CENTER);

        // Controls
        JPanel ctrl = new JPanel(new GridBagLayout());
        ctrl.setBackground(Color.WHITE);
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(5, 5, 5, 5);
        g.fill = GridBagConstraints.HORIZONTAL;
        g.gridx = 0; g.gridy = 0;

        ctrl.add(new JLabel("Thoi han:"), g);
        g.gridx = 1;
        cbBadgeDuration = new JComboBox<>(new String[]{
                "Vinh vien (Vo han)",
                "30 Ngay",
                "15 Ngay",
                "7 Ngay",
                "3 Ngay",
                "1 Ngay"
        });
        cbBadgeDuration.setFont(FONT_UI);
        ctrl.add(cbBadgeDuration, g);

        g.gridx = 0; g.gridy = 1;
        ctrl.add(new JLabel("Noi nhan item:"), g);
        g.gridx = 1;
        cbBadgeDestination = new JComboBox<>(new String[]{"Hanh trang (Bag)", "Ruong do (Box)", "Hom thu (Mail)"});
        cbBadgeDestination.setFont(FONT_UI);
        ctrl.add(cbBadgeDestination, g);

        g.gridx = 0; g.gridy = 2; g.gridwidth = 2;
        chkBadgeActiveNow = new JCheckBox("Kich hoat hien thi tren dau nhan vat ngay", true);
        chkBadgeActiveNow.setFont(FONT_UI);
        chkBadgeActiveNow.setBackground(Color.WHITE);
        ctrl.add(chkBadgeActiveNow, g);

        g.gridx = 0; g.gridy = 3; g.gridwidth = 2;
        chkBadgeGiveItem = new JCheckBox("Tang kem Item Danh Hieu vao tui/ruong (neu co)", true);
        chkBadgeGiveItem.setFont(FONT_UI);
        chkBadgeGiveItem.setBackground(Color.WHITE);
        ctrl.add(chkBadgeGiveItem, g);

        // Action Buttons
        JButton btnGiveBadge = new JButton("TANG DANH HIEU CHO NGUOI CHOI");
        btnGiveBadge.setFont(FONT_BOLD);
        btnGiveBadge.setBackground(COL_SUCCESS);
        btnGiveBadge.setForeground(Color.WHITE);
        btnGiveBadge.setPreferredSize(new Dimension(0, 42));
        btnGiveBadge.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnGiveBadge.addActionListener(e -> {
            Player p = checkAndGetTargetPlayer();
            if (p == null) return;
            if (currentSelectedBadge == null) {
                JOptionPane.showMessageDialog(this, "Vui long chon 1 danh hieu trong danh sach!", "Thong bao", JOptionPane.WARNING_MESSAGE);
                return;
            }

            int durIdx = cbBadgeDuration.getSelectedIndex();
            long time;
            if (durIdx == 0) {
                time = -1L;
            } else {
                int[] days = {0, 30, 15, 7, 3, 1};
                time = System.currentTimeMillis() + (long) days[durIdx] * 24L * 60L * 60L * 1000L;
            }

            if (p.dataBadges == null) {
                p.dataBadges = new ArrayList<>();
            }

            boolean active = chkBadgeActiveNow.isSelected();
            boolean found = false;
            for (BadgesData bg : p.dataBadges) {
                if (bg.idBadGes == currentSelectedBadge.idEffect) {
                    bg.timeofUseBadges = time;
                    if (active) bg.isUse = true;
                    found = true;
                    break;
                }
            }
            if (!found) {
                BadgesData newBg = new BadgesData(currentSelectedBadge.idEffect, time, active);
                p.dataBadges.add(newBg);
            }

            if (active) {
                for (BadgesData bg : p.dataBadges) {
                    bg.isUse = (bg.idBadGes == currentSelectedBadge.idEffect);
                }
                BadgesService.turnOnBadges(p, currentSelectedBadge.idEffect);
                if (p.getSession() != null) {
                    Service.gI().sendBadgesPlayer(p, 0, currentSelectedBadge.idEffect);
                    Service.gI().Send_Info_NV(p);
                    Service.gI().point(p);
                    Service.gI().sendThongBao(p, "Ban da duoc trao danh hieu: " + currentSelectedBadge.name);
                }
            }

            int badgeItemId = currentSelectedBadge.idItem;
            if (badgeItemId <= 0 && (currentSelectedBadge.idEffect == 257 || currentSelectedBadge.name.toUpperCase().contains("ADMIN"))) {
                badgeItemId = 1288;
            }
            if (chkBadgeGiveItem.isSelected() && badgeItemId > 0) {
                Item it = ItemService.gI().createNewItem((short) badgeItemId);
                if (it != null) {
                    it.createTime = System.currentTimeMillis();
                    for (ItemOption opt : currentSelectedBadge.options) {
                        if (opt != null && opt.optionTemplate != null) {
                            it.addOptionParam(opt.optionTemplate.id, opt.param);
                        }
                    }
                    if (time > 0) {
                        int d = (int) Math.ceil((time - System.currentTimeMillis()) / (24.0 * 60.0 * 60.0 * 1000.0));
                        it.addOptionParam(93, d);
                    } else {
                        it.addOptionParam(73, 1);
                    }
                    deliverItemToPlayer(p, it, cbBadgeDestination.getSelectedIndex());
                }
            }

            savePlayer(p);
            updatePlayerSummary(p);
            JOptionPane.showMessageDialog(this, "Da tang danh hieu [" + currentSelectedBadge.name + "] cho " + p.name + " thanh cong!", "Hoan tat", JOptionPane.INFORMATION_MESSAGE);
        });

        JButton btnRemoveCurrent = new JButton("Go Danh Hieu Dang Dung (Tat Hieu Ung)");
        btnRemoveCurrent.setFont(FONT_UI);
        btnRemoveCurrent.setBackground(COL_WARNING);
        btnRemoveCurrent.setForeground(Color.WHITE);
        btnRemoveCurrent.setPreferredSize(new Dimension(0, 34));
        btnRemoveCurrent.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnRemoveCurrent.addActionListener(e -> {
            Player p = checkAndGetTargetPlayer();
            if (p == null) return;
            if (p.dataBadges != null) {
                for (BadgesData bg : p.dataBadges) {
                    bg.isUse = false;
                }
            }
            if (p.getSession() != null) {
                Service.gI().sendBadgesPlayer(p, 0, -1);
                Service.gI().Send_Info_NV(p);
                Service.gI().point(p);
                Service.gI().sendThongBao(p, "Danh hieu cua ban da duoc go xuong.");
            }
            savePlayer(p);
            updatePlayerSummary(p);
            JOptionPane.showMessageDialog(this, "Da go hieu ung danh hieu cho " + p.name + "!", "Hoan tat", JOptionPane.INFORMATION_MESSAGE);
        });

        JButton btnClearAllBadges = new JButton("Xoa Tat Ca Danh Hieu Cua Nhan Vat");
        btnClearAllBadges.setFont(FONT_UI);
        btnClearAllBadges.setBackground(COL_DANGER);
        btnClearAllBadges.setForeground(Color.WHITE);
        btnClearAllBadges.setPreferredSize(new Dimension(0, 34));
        btnClearAllBadges.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnClearAllBadges.addActionListener(e -> {
            Player p = checkAndGetTargetPlayer();
            if (p == null) return;
            int confirm = JOptionPane.showConfirmDialog(this, "Ban chac chan muon xoa TOAN BO danh hieu cua [" + p.name + "]?", "Xac nhan", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (confirm == JOptionPane.YES_OPTION) {
                if (p.dataBadges != null) {
                    p.dataBadges.clear();
                }
                if (p.getSession() != null) {
                    Service.gI().sendBadgesPlayer(p, 0, -1);
                    Service.gI().Send_Info_NV(p);
                    Service.gI().point(p);
                    Service.gI().sendThongBao(p, "Toan bo danh hieu da duoc xoa bo boi Admin.");
                }
                savePlayer(p);
                updatePlayerSummary(p);
                JOptionPane.showMessageDialog(this, "Da xoa tat ca danh hieu cua [" + p.name + "]!", "Hoan tat", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        JPanel btnPanel = new JPanel(new GridLayout(3, 1, 6, 6));
        btnPanel.setOpaque(false);
        btnPanel.add(btnGiveBadge);
        btnPanel.add(btnRemoveCurrent);
        btnPanel.add(btnClearAllBadges);

        JPanel rightCenter = new JPanel(new BorderLayout(10, 10));
        rightCenter.setOpaque(false);
        rightCenter.add(ctrl, BorderLayout.NORTH);
        rightCenter.add(btnPanel, BorderLayout.SOUTH);

        right.add(card, BorderLayout.NORTH);
        right.add(rightCenter, BorderLayout.CENTER);

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, left, right);
        split.setDividerLocation(520);
        split.setResizeWeight(0.6);
        split.setBorder(null);

        root.add(split, BorderLayout.CENTER);
        return root;
    }

    // ==========================================
    // TAB 8: HE THONG SU KIEN & QUA SK
    // ==========================================
    private static final String[] EVENT_LIST_NAMES = {
            "1. Tet Nguyen Dan",
            "2. Trung Thu",
            "3. Halloween",
            "4. Giang Sinh",
            "5. Vu Lan Bao Hieu",
            "6. 8/3 Quoc Te Phu Nu",
            "7. Gio To Hung Vuong",
            "8. Black Friday",
            "9. Valentine",
            "10. 20/10",
            "11. Top Up"
    };

    private JPanel createEventSystemTab() {
        JPanel container = new JPanel();
        container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
        container.setBackground(Color.WHITE);
        container.setBorder(new EmptyBorder(12, 12, 12, 12));

        // --- SECTION 1: BAT/TAT SU KIEN SERVER RUNTIME ---
        JPanel pnlServerEvents = new JPanel(new BorderLayout(10, 10));
        pnlServerEvents.setBackground(Color.WHITE);
        pnlServerEvents.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(new LineBorder(COL_PRIMARY, 1, true), "1. Quan Ly Trang Thai Su Kien Server Runtime (Ap dung ngay khong can restart)"),
                new EmptyBorder(10, 10, 10, 10)
        ));

        JPanel chkGrid = new JPanel(new GridLayout(0, 4, 10, 6));
        chkGrid.setOpaque(false);
        eventCheckBoxes.clear();
        List<Integer> savedEventIds = loadActiveEventsFromFile();

        for (int i = 0; i < EVENT_LIST_NAMES.length; i++) {
            JCheckBox chk = new JCheckBox(EVENT_LIST_NAMES[i]);
            chk.setFont(FONT_UI);
            chk.setBackground(Color.WHITE);
            chk.setCursor(new Cursor(Cursor.HAND_CURSOR));
            int eventId = i + 1;
            chk.putClientProperty("eventId", eventId);
            if (savedEventIds.contains(eventId)) chk.setSelected(true);
            eventCheckBoxes.add(chk);
            chkGrid.add(chk);
        }

        JPanel actServer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        actServer.setOpaque(false);
        JButton btnApplyEvents = new JButton("Luu & Kich Hoat Su Kien Toan Server (Runtime)");
        btnApplyEvents.setFont(FONT_BOLD);
        btnApplyEvents.setBackground(COL_PRIMARY);
        btnApplyEvents.setForeground(Color.WHITE);
        btnApplyEvents.setPreferredSize(new Dimension(360, 38));
        btnApplyEvents.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnApplyEvents.addActionListener(e -> {
            List<Integer> selected = new ArrayList<>();
            for (JCheckBox chk : eventCheckBoxes) {
                if (chk.isSelected()) {
                    selected.add((int) chk.getClientProperty("eventId"));
                }
            }
            if (selected.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui long chon it nhat 1 su kien!", "Thong bao", JOptionPane.WARNING_MESSAGE);
                return;
            }

            saveActiveEventsToFile(selected);

            // Update static flags in EventManager
            EventManager.LUNNAR_NEW_YEAR = selected.contains(1);
            EventManager.TRUNG_THU = selected.contains(2);
            EventManager.HALLOWEEN = selected.contains(3);
            EventManager.CHRISTMAS = selected.contains(4);
            EventManager.VU_LAN_FESTIVAL = selected.contains(5);
            EventManager.INTERNATIONAL_WOMANS_DAY = selected.contains(6);
            EventManager.HUNG_VUONG = selected.contains(7);
            EventManager.BLACK_FRIDAY = selected.contains(8);
            EventManager.VALENTINE_DAY = selected.contains(9);
            EventManager.DAY_20_10 = selected.contains(10);
            EventManager.TOP_UP = selected.contains(11);

            try {
                EventManager.gI().setCurrentEvent(selected.get(0));
                EventManager.gI().init();
                Service.gI().sendThongBaoAllPlayer("Su kien may chu da duoc cap nhat runtime!");
            } catch (Exception ex) {
                Logger.logException(PlayerBuffManagerPanel.class, ex, "Loi kich hoat event runtime");
            }

            JOptionPane.showMessageDialog(this, "Da luu va kich hoat su kien toan server thanh cong!", "Hoan tat", JOptionPane.INFORMATION_MESSAGE);
        });

        actServer.add(btnApplyEvents);
        pnlServerEvents.add(chkGrid, BorderLayout.CENTER);
        pnlServerEvents.add(actServer, BorderLayout.SOUTH);

        // --- SECTION 2: TANG GOI QUA SU KIEN (1-CLICK BUNDLES) ---
        JPanel pnlBundles = new JPanel(new BorderLayout(8, 8));
        pnlBundles.setBackground(Color.WHITE);
        pnlBundles.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(new LineBorder(COL_SUCCESS, 1, true), "2. Tang Goi Qua Su Kien Tron Goi (1-Click Cho Nguoi Choi Dang Chon)"),
                new EmptyBorder(10, 10, 10, 10)
        ));

        JPanel destPnl = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        destPnl.setOpaque(false);
        JComboBox<String> cbBundleDest = new JComboBox<>(new String[]{"Hanh trang (Bag)", "Ruong do (Box)", "Hom thu (Mail)"});
        cbBundleDest.setFont(FONT_UI);
        destPnl.add(new JLabel("Noi nhan qua su kien:"));
        destPnl.add(cbBundleDest);

        JPanel bundleGrid = new JPanel(new GridLayout(2, 3, 10, 10));
        bundleGrid.setOpaque(false);

        // 1. Goi Tet
        JButton btnTet = new JButton("Goi Qua Tet Nguyen Dan");
        btnTet.setFont(FONT_BOLD);
        btnTet.setBackground(new Color(235, 245, 255));
        btnTet.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnTet.setToolTipText("Banh Chung x50, Banh Tet x50, Thit Heo x99, Thung Nep x99, Trung Muoi x99, La Dong x99, Dau Xanh x99, Bao Li Xi x99");
        btnTet.addActionListener(e -> {
            Player p = checkAndGetTargetPlayer();
            if (p == null) return;
            int[][] items = {{752, 50}, {753, 50}, {748, 99}, {749, 99}, {750, 99}, {751, 99}, {886, 99}, {401, 99}};
            giveEventBundle(p, "Goi Qua Tet Nguyen Dan", items, cbBundleDest.getSelectedIndex());
        });

        // 2. Goi Trung Thu
        JButton btnTT = new JButton("Goi Qua Trung Thu");
        btnTT.setFont(FONT_BOLD);
        btnTT.setBackground(new Color(255, 248, 230));
        btnTT.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnTT.setToolTipText("Banh TT Ga Quay x50, Banh TT Thap Cam x50, Bot Mi x99, Dau Xanh x99, Trung Muoi x99, Ga Quay x50, Long Den x10");
        btnTT.addActionListener(e -> {
            Player p = checkAndGetTargetPlayer();
            if (p == null) return;
            int[][] items = {{890, 50}, {891, 50}, {888, 99}, {889, 99}, {886, 99}, {887, 50}, {465, 10}};
            giveEventBundle(p, "Goi Qua Trung Thu", items, cbBundleDest.getSelectedIndex());
        });

        // 3. Goi Noel
        JButton btnNoel = new JButton("Goi Qua Giang Sinh (Noel)");
        btnNoel.setFont(FONT_BOLD);
        btnNoel.setBackground(new Color(235, 250, 235));
        btnNoel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnNoel.setToolTipText("Keo Giang Sinh x99, Qua Cau Tuyet x99, Hop Qua Noel x50, Non Noel x10, Cay Thong x20, Vo Giang Sinh x99");
        btnNoel.addActionListener(e -> {
            Player p = checkAndGetTargetPlayer();
            if (p == null) return;
            int[][] items = {{649, 99}, {650, 99}, {651, 20}, {652, 50}, {653, 10}, {654, 99}};
            giveEventBundle(p, "Goi Qua Giang Sinh", items, cbBundleDest.getSelectedIndex());
        });

        // 4. Goi Halloween
        JButton btnHal = new JButton("Goi Qua Halloween");
        btnHal.setFont(FONT_BOLD);
        btnHal.setBackground(new Color(255, 240, 245));
        btnHal.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnHal.setToolTipText("Bi Ngo Ma Quai x99, Keo Phu Thuy x99, Mat Na Halloween x10, Long Den Bi Ngo x10");
        btnHal.addActionListener(e -> {
            Player p = checkAndGetTargetPlayer();
            if (p == null) return;
            int[][] items = {{585, 99}, {586, 99}, {587, 10}, {588, 10}};
            giveEventBundle(p, "Goi Qua Halloween", items, cbBundleDest.getSelectedIndex());
        });

        // 5. Goi Gio To Hung Vuong
        JButton btnHV = new JButton("Goi Qua Gio To Hung Vuong");
        btnHV.setFont(FONT_BOLD);
        btnHV.setBackground(new Color(245, 240, 255));
        btnHV.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnHV.setToolTipText("Banh Day x50, Banh Chung Lang Lieu x50, Com Lam x99");
        btnHV.addActionListener(e -> {
            Player p = checkAndGetTargetPlayer();
            if (p == null) return;
            int[][] items = {{1555, 50}, {1556, 50}, {1557, 99}};
            giveEventBundle(p, "Goi Qua Gio To Hung Vuong", items, cbBundleDest.getSelectedIndex());
        });

        // 6. Goi 8/3 & 20/10
        JButton btnWoman = new JButton("Goi Qua 8/3 & 20/10");
        btnWoman.setFont(FONT_BOLD);
        btnWoman.setBackground(new Color(255, 245, 238));
        btnWoman.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnWoman.setToolTipText("Bo Hoa Hong x99, Hop Socola Tinh Yeu x99");
        btnWoman.addActionListener(e -> {
            Player p = checkAndGetTargetPlayer();
            if (p == null) return;
            int[][] items = {{590, 99}, {591, 99}};
            giveEventBundle(p, "Goi Qua Ngay Phu Nu (8/3 & 20/10)", items, cbBundleDest.getSelectedIndex());
        });

        bundleGrid.add(btnTet);
        bundleGrid.add(btnTT);
        bundleGrid.add(btnNoel);
        bundleGrid.add(btnHal);
        bundleGrid.add(btnHV);
        bundleGrid.add(btnWoman);

        pnlBundles.add(destPnl, BorderLayout.NORTH);
        pnlBundles.add(bundleGrid, BorderLayout.CENTER);

        // --- SECTION 3: TANG VAT PHAM SU KIEN TUY CHON ---
        JPanel pnlSingle = new JPanel(new BorderLayout(10, 10));
        pnlSingle.setBackground(Color.WHITE);
        pnlSingle.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(new LineBorder(COL_PURPLE, 1, true), "3. Tang Vat Pham Su Kien Tuy Chon (Chon Theo Tung Loai SK)"),
                new EmptyBorder(10, 10, 10, 10)
        ));

        JPanel filterRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        filterRow.setOpaque(false);
        filterRow.add(new JLabel("Loc theo SK:"));
        JComboBox<String> cbEventCategory = new JComboBox<>(new String[]{
                "Tat ca su kien",
                "Tet Nguyen Dan",
                "Trung Thu",
                "Giang Sinh (Noel)",
                "Halloween",
                "Gio To Hung Vuong",
                "8/3 & 20/10"
        });
        cbEventCategory.setFont(FONT_UI);
        cbEventCategory.addActionListener(e -> filterEventItems(cbEventCategory.getSelectedIndex()));
        filterRow.add(cbEventCategory);

        filterRow.add(new JLabel("Vat pham:"));
        cbEventItems = new JComboBox<>();
        cbEventItems.setFont(FONT_UI);
        cbEventItems.setPreferredSize(new Dimension(300, 26));
        cbEventItems.setRenderer(new ItemListCellRenderer(24));
        cbEventItems.addActionListener(e -> {
            if (!isUpdatingEventCombo) {
                ItemData sel = (ItemData) cbEventItems.getSelectedItem();
                updateEventItemPreview(sel);
            }
        });
        filterRow.add(cbEventItems);

        // Item preview & action
        JPanel previewBox = new JPanel(new BorderLayout(10, 10));
        previewBox.setBackground(new Color(250, 250, 250));
        previewBox.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(COL_BORDER, 1, true),
                new EmptyBorder(8, 8, 8, 8)
        ));

        lblEventItemPreview = new JLabel();
        lblEventItemPreview.setPreferredSize(new Dimension(48, 48));
        lblEventItemPreview.setHorizontalAlignment(SwingConstants.CENTER);
        lblEventItemPreview.setBorder(new LineBorder(COL_BORDER, 1, true));

        JPanel infoBox = new JPanel(new GridLayout(2, 1, 2, 2));
        infoBox.setOpaque(false);
        lblEventItemTitle = new JLabel("Chua chon vat pham");
        lblEventItemTitle.setFont(FONT_BOLD);
        lblEventItemTitle.setForeground(COL_PURPLE);
        lblEventItemDesc = new JLabel("Chon vat pham su kien de xem chi tiet");
        lblEventItemDesc.setFont(FONT_UI);
        infoBox.add(lblEventItemTitle);
        infoBox.add(lblEventItemDesc);

        previewBox.add(lblEventItemPreview, BorderLayout.WEST);
        previewBox.add(infoBox, BorderLayout.CENTER);

        JPanel actionRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 4));
        actionRow.setOpaque(false);
        actionRow.add(new JLabel("So luong:"));
        spEventItemQuantity = new JSpinner(new SpinnerNumberModel(1, 1, 9999, 1));
        spEventItemQuantity.setPreferredSize(new Dimension(80, 26));
        actionRow.add(spEventItemQuantity);

        actionRow.add(new JLabel("Noi nhan:"));
        cbEventItemDest = new JComboBox<>(new String[]{"Hanh trang (Bag)", "Ruong do (Box)", "Hom thu (Mail)"});
        cbEventItemDest.setFont(FONT_UI);
        actionRow.add(cbEventItemDest);

        chkEventItemLock = new JCheckBox("Khoa giao dich (Option 30)", true);
        chkEventItemLock.setFont(FONT_UI);
        chkEventItemLock.setBackground(Color.WHITE);
        actionRow.add(chkEventItemLock);

        JButton btnGiveSingleEventItem = new JButton("Tang Vat Pham Su Kien Nay");
        btnGiveSingleEventItem.setFont(FONT_BOLD);
        btnGiveSingleEventItem.setBackground(COL_PURPLE);
        btnGiveSingleEventItem.setForeground(Color.WHITE);
        btnGiveSingleEventItem.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnGiveSingleEventItem.addActionListener(e -> {
            Player p = checkAndGetTargetPlayer();
            if (p == null) return;
            ItemData sel = (ItemData) cbEventItems.getSelectedItem();
            if (sel == null) {
                JOptionPane.showMessageDialog(this, "Vui long chon vat pham su kien!", "Thong bao", JOptionPane.WARNING_MESSAGE);
                return;
            }
            int qty = (int) spEventItemQuantity.getValue();
            Item it = ItemService.gI().createNewItem((short) sel.id, qty);
            if (it != null) {
                if (chkEventItemLock.isSelected()) {
                    it.addOptionParam(30, 0);
                }
                boolean ok = deliverItemToPlayer(p, it, cbEventItemDest.getSelectedIndex());
                if (ok) {
                    if (p.getSession() != null) {
                        Service.gI().sendThongBao(p, "Ban da nhan duoc " + qty + "x " + sel.name);
                    }
                    updatePlayerSummary(p);
                    JOptionPane.showMessageDialog(this, "Da tang " + qty + "x [" + sel.name + "] cho " + p.name + "!", "Hoan tat", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, "Khong the them item vao noi nhan (co the da day)!", "Loi", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        actionRow.add(btnGiveSingleEventItem);

        JPanel singleCenter = new JPanel(new BorderLayout(5, 5));
        singleCenter.setOpaque(false);
        singleCenter.add(previewBox, BorderLayout.NORTH);
        singleCenter.add(actionRow, BorderLayout.CENTER);

        pnlSingle.add(filterRow, BorderLayout.NORTH);
        pnlSingle.add(singleCenter, BorderLayout.CENTER);

        container.add(pnlServerEvents);
        container.add(Box.createVerticalStrut(10));
        container.add(pnlBundles);
        container.add(Box.createVerticalStrut(10));
        container.add(pnlSingle);

        JScrollPane scroll = new JScrollPane(container);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(Color.WHITE);
        root.add(scroll, BorderLayout.CENTER);
        return root;
    }

    private void updateEventItemPreview(ItemData sel) {
        if (sel == null) {
            lblEventItemTitle.setText("Chua chon vat pham");
            lblEventItemDesc.setText("");
            lblEventItemPreview.setIcon(null);
            return;
        }
        lblEventItemTitle.setText(sel.name + " (ID: " + sel.id + ")");
        lblEventItemDesc.setText("<html><b>Loai:</b> " + getTypeName(sel.type) + " | <b>Mo ta:</b> " + (sel.description.isEmpty() ? "Vat pham su kien" : sel.description) + "</html>");
        lblEventItemPreview.setIcon(getItemIcon(sel.iconId, 48));
    }

    private void giveEventBundle(Player p, String bundleName, int[][] items, int destIdx) {
        int addedCount = 0;
        for (int[] pair : items) {
            int itemId = pair[0];
            int qty = pair[1];
            Item it = ItemService.gI().createNewItem((short) itemId, qty);
            if (it != null) {
                if (deliverItemToPlayer(p, it, destIdx, false)) {
                    addedCount++;
                }
            }
        }
        if (addedCount > 0) {
            savePlayer(p);
        }
        if (p.getSession() != null) {
            Service.gI().sendThongBao(p, "Ban da nhan duoc " + bundleName + " tu Admin!");
        }
        updatePlayerSummary(p);
        JOptionPane.showMessageDialog(this, "Da tang " + bundleName + " (" + addedCount + " vat pham) cho " + p.name + " thanh cong!", "Hoan tat", JOptionPane.INFORMATION_MESSAGE);
    }

    private void initEventItems() {
        filterEventItems(0);
    }

    private void filterEventItems(int categoryIndex) {
        if (cbEventItems == null) return;
        isUpdatingEventCombo = true;
        DefaultComboBoxModel<ItemData> model = new DefaultComboBoxModel<>();

        int[][] catItemIds = {
                {}, // 0: All
                {748, 749, 750, 751, 752, 753, 886, 401}, // 1: Tet
                {886, 887, 888, 889, 890, 891, 462, 463, 464, 465, 466}, // 2: Trung Thu
                {649, 650, 651, 652, 653, 654}, // 3: Giang Sinh
                {585, 586, 587, 588}, // 4: Halloween
                {1555, 1556, 1557}, // 5: Hung Vuong
                {590, 591} // 6: 8/3 & 20/10
        };

        Set<Integer> targetIds = new HashSet<>();
        if (categoryIndex > 0 && categoryIndex < catItemIds.length) {
            for (int id : catItemIds[categoryIndex]) targetIds.add(id);
        }

        for (ItemData it : allItemList) {
            if (it == null) continue;
            boolean match = false;
            if (categoryIndex == 0) {
                String n = it.name.toLowerCase();
                if (n.contains("tết") || n.contains("tet") || n.contains("trung thu") || n.contains("bánh") || n.contains("banh") || n.contains("giáng sinh") || n.contains("noel") || n.contains("halloween") || n.contains("hùng vương") || n.contains("lì xì") || n.contains("hộp quà")) {
                    match = true;
                }
                for (int[] arr : catItemIds) {
                    for (int id : arr) {
                        if (it.id == id) { match = true; break; }
                    }
                }
            } else {
                match = targetIds.contains(it.id);
            }

            if (match) {
                model.addElement(it);
            }
        }

        cbEventItems.setModel(model);
        isUpdatingEventCombo = false;
        if (model.getSize() > 0) {
            cbEventItems.setSelectedIndex(0);
            updateEventItemPreview(model.getElementAt(0));
        } else {
            updateEventItemPreview(null);
        }
    }

    private List<Integer> loadActiveEventsFromFile() {
        List<Integer> ids = new ArrayList<>();
        File f = new File("active_event.txt");
        if (!f.exists()) {
            ids.add(11);
            return ids;
        }
        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String line = br.readLine();
            if (line != null && !line.isEmpty()) {
                for (String part : line.split("-")) {
                    try {
                        ids.add(Integer.parseInt(part.trim()));
                    } catch (NumberFormatException ignored) {}
                }
            }
        } catch (Exception ignored) {}
        if (ids.isEmpty()) ids.add(7);
        return ids;
    }

    private void saveActiveEventsToFile(List<Integer> ids) {
        try (PrintWriter pw = new PrintWriter(new FileWriter("active_event.txt"))) {
            String line = ids.stream().map(String::valueOf).collect(Collectors.joining("-"));
            pw.println(line);
            pw.flush();
        } catch (Exception e) {
            Logger.logException(PlayerBuffManagerPanel.class, e, "Loi luu active_event.txt");
        }
    }
}
