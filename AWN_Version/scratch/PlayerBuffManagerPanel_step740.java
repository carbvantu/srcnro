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
import nro.services.Service;
import nro.services.TaskService;
import nro.task.TaskMain;
import nro.template.ItemTemplate;

import javax.imageio.ImageIO;
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
            Map<Integer, ItemData> map = new HashMap<>();

            // 1. Load from Manager.ITEM_TEMPLATES first if available
            try {
                if (Manager.ITEM_TEMPLATES != null && !Manager.ITEM_TEMPLATES.isEmpty()) {
                    for (ItemTemplate temp : Manager.ITEM_TEMPLATES) {
                        if (temp != null) {
                            ItemData it = new ItemData(temp.id, temp.name, temp.type, temp.gender, temp.iconID, temp.part, temp.level, temp.description);
                            items.add(it);
                            map.put(it.id, it);
                            classifyItem(it, equips, cts, susanos, canhs, pets);
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
                            classifyItem(it, equips, cts, susanos, canhs, pets);
                        }
                    }
                }
            } catch (Exception e) {
                Logger.logException(PlayerBuffManagerPanel.class, e, "Loi loadItemTemplatesFromDB");
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

                // Populate Default Quần Áo (Default: Áo Thần Linh 555)
                if (itemMap.containsKey(555)) {
                    setSelectedEquip(itemMap.get(555));
                } else if (!quanAoList.isEmpty()) {
                    setSelectedEquip(quanAoList.get(0));
                }

                // Populate Default Susano (Default: Hỏa Thần Thể 2137)
                if (itemMap.containsKey(2137)) {
                    setSelectedSusano(itemMap.get(2137));
                } else if (!susanoList.isEmpty()) {
                    setSelectedSusano(susanoList.get(0));
                }

                // Populate Default Item (Default: Thỏi Vàng 457)
                if (itemMap.containsKey(457)) {
                    setSelectedItem(itemMap.get(457));
                } else if (!allItemList.isEmpty()) {
                    setSelectedItem(allItemList.get(0));
                }
            });
        }).start();
    }

    private void classifyItem(ItemData it, List<ItemData> equips, List<ItemData> cts, List<ItemData> susanos, List<ItemData> canhs, List<ItemData> pets) {
        if (it.type >= 0 && it.type <= 4) {
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
        if (it.type == 21 || it.type == 70 || isPetName(it.name)) {
            pets.add(it);
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

        JLabel lblSub = new JLabel("Tang Quan Ao (Set Than/HD/TS/Full Sao) | Cai Trang VIP | Susano, Canh & Linh Thu | Vat Pham Game | Buff Tien Te & Nhiem Vu");
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

        // --- RIGHT: 6 Professional Tabs ---
        mainTabbedPane = new JTabbedPane();
        mainTabbedPane.setFont(FONT_BOLD);
        mainTabbedPane.addTab("Tang Quan Ao (Set Do VIP)", createGiveEquipTab());
        mainTabbedPane.addTab("Tang Cai Trang (VIP)", createGiveCaiTrangTab());
        mainTabbedPane.addTab("Tang Susano & Canh VIP", createGiveSusanoTab());
        mainTabbedPane.addTab("Tang Vat Pham (Full Item)", createGiveItemTab());
        mainTabbedPane.addTab("Buff Tien Te & Chi So", createBuffTab());
        mainTabbedPane.addTab("Quan Ly & Bo Qua NV", createTaskTab());

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

        Player plOnline = Client.gI() != null ? Client.gI().getPlayer(pName) : null;
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
            if (currentSelectedPlayer.getSession() != null) {
                Player live = Client.gI().getPlayer(currentSelectedPlayer.name);
                if (live != null) currentSelectedPlayer = live;
            }
            return currentSelectedPlayer;
        }
        if (currentSelectedPlayerId != -1) {
            Player p = Client.gI().getPlayer(currentSelectedPlayerName);
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
            Player p = Client.gI().getPlayer(input);
            if (p == null) {
                try {
                    int pid = Integer.parseInt(input);
                    p = GodGK.loadById(pid);
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

        if (ok) {
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

        JButton btnGiveSetThienSu = new JButton("Tang Set Thien Su (Cap 15 - Sieu VIP)");
        btnGiveSetThienSu.setFont(FONT_BOLD);
        btnGiveSetThienSu.setBackground(COL_PURPLE);
        btnGiveSetThienSu.setForeground(Color.WHITE);
        btnGiveSetThienSu.addActionListener(e -> giveEquipmentSet(2, cbPlanetChoice.getSelectedIndex()));

        JButton btnGiveSetThuong = new JButton("Tang Set Thuong C7");
        btnGiveSetThuong.setFont(FONT_BOLD);
        btnGiveSetThuong.setBackground(new Color(100, 110, 120));
        btnGiveSetThuong.setForeground(Color.WHITE);
        btnGiveSetThuong.addActionListener(e -> giveEquipmentSet(3, cbPlanetChoice.getSelectedIndex()));

        pnlSetButtons.add(btnGiveSetThan);
        pnlSetButtons.add(btnGiveSetHuyDiet);
        pnlSetButtons.add(btnGiveSetThienSu);
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
                1048, 1051, 1054, 1057, 1060 // Set Thiên sứ TĐ
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
            // Set Thiên Sứ (Siêu VIP)
            setName = "Set Thien Su (Sieu VIP)";
            if (gender == 0) itemIds = new int[]{1048, 1051, 1054, 1057, 1060};
            else if (gender == 1) itemIds = new int[]{1049, 1052, 1055, 1058, 1061};
            else itemIds = new int[]{1050, 1053, 1056, 1059, 1062};
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
            applyEquipOptions(item, upgradeLevel, starCount, fullStar, txtEquipCustomOptions.getText().trim(), chkEquipLock.isSelected(), (int) spEquipHsd.getValue());
            if (deliverItemToPlayer(p, item, dest)) {
                successCount++;
            }
        }

        String destName = dest == 1 ? "Ruong do (Box)" : (dest == 2 ? "Hom thu" : "Hanh trang");
        if (p.getSession() != null) {
            Service.gI().sendThongBao(p, "Ban vua nhan duoc Tron bo " + setName + " tu Admin!");
        }
        JOptionPane.showMessageDialog(this, "Da gui tron bo " + setName + " (5 mon) vao [" + destName + "] cua [" + p.name + "] thanh cong!\n(Thanh cong: " + successCount + "/5)", "Thanh cong", JOptionPane.INFORMATION_MESSAGE);
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
        // targetType: 0=All, 1=CaiTrang, 2=QuanAo, 3=Susano
        String title = "Thu Vien Chon Vat Pham Game Truc Quan";
        if (targetType == 1) title = "Thu Vien Chon Cai Trang (Avatar VIP)";
        else if (targetType == 2) title = "Thu Vien Chon Quan Ao & Trang Bi";
        else if (targetType == 3) title = "Thu Vien Chon Susano, Canh & Linh Thu";

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
                "Quan Ao / Trang Bi (Ao, Quan, Gang, Giay, Rada)",
                "Cai Trang (Avatar VIP)",
                "Susano / Than The (11 Bo Than The)",
                "Canh Than & Than Dyc",
                "Linh Thu & Pet VIP",
                "Ruong & Hop Qua",
                "Thoi Vang & Tien Te",
                "Ngoc Rong & Da Nang Cap",
                "Dau Than & Bo Tro"
        });
        cbCategory.setFont(FONT_BOLD);

        if (targetType == 1) cbCategory.setSelectedIndex(2);
        else if (targetType == 2) cbCategory.setSelectedIndex(1);
        else if (targetType == 3) cbCategory.setSelectedIndex(3);

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
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getColumnModel().getColumn(0).setMaxWidth(60);
        table.getColumnModel().getColumn(0).setPreferredWidth(50);
        table.getColumnModel().getColumn(1).setMaxWidth(60);
        table.getColumnModel().getColumn(2).setPreferredWidth(230);
        table.getColumnModel().getColumn(3).setPreferredWidth(120);
        table.getColumnModel().getColumn(4).setPreferredWidth(80);

        List<ItemData> filteredList = new ArrayList<>();

        Runnable filterData = () -> {
            String kw = txtSearch.getText().trim().toLowerCase();
            int catIdx = cbCategory.getSelectedIndex();

            filteredList.clear();
            for (ItemData it : allItemList) {
                boolean matchCat = true;
                if (catIdx == 1) matchCat = (it.type >= 0 && it.type <= 4);
                else if (catIdx == 2) matchCat = (it.type == 5 || isCaiTrangName(it.name));
                else if (catIdx == 3) matchCat = isSusanoItem(it);
                else if (catIdx == 4) matchCat = isCanhItem(it);
                else if (catIdx == 5) matchCat = (it.type == 21 || it.type == 70 || isPetName(it.name));
                else if (catIdx == 6) matchCat = isRuongHopName(it.name);
                else if (catIdx == 7) matchCat = (it.id == 457 || it.name.toLowerCase().contains("thoi vang") || it.name.toLowerCase().contains("thỏi vàng"));
                else if (catIdx == 8) matchCat = (it.type == 12 || it.type == 14 || it.type == 32);
                else if (catIdx == 9) matchCat = (it.type == 6 || it.type == 29 || it.type == 30);

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

                target.nPoint.power += amount;
                target.nPoint.tiemNang += amount;

                if (target.getSession() != null || (isMaster && p.getSession() != null)) {
                    Service.gI().point(target);
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

        gbcE.gridx = 0; gbcE.gridy = 0; pnlExp.add(new JLabel("Muc tieu:"), gbcE);
        gbcE.gridx = 1; gbcE.gridy = 0; pnlExp.add(cbExpTarget, gbcE);

        gbcE.gridx = 0; gbcE.gridy = 1; pnlExp.add(new JLabel("So SMTN:"), gbcE);
        gbcE.gridx = 1; gbcE.gridy = 1; pnlExp.add(txtExpAmount, gbcE);
        gbcE.gridx = 2; gbcE.gridy = 1; pnlExp.add(pnlExpFast, gbcE);

        gbcE.gridx = 1; gbcE.gridy = 2; gbcE.gridwidth = 2; gbcE.insets = new Insets(8, 6, 4, 6);
        pnlExp.add(btnAddExp, gbcE);

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
}
