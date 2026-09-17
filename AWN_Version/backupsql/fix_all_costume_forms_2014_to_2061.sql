-- Fix character form for costumes 2014 to 2061
-- Generated on: Thu Sep 17 22:16:45 ICT 2026

UPDATE part SET DATA = '[[31213,5,4],[31214,-3,-5],[31215,1,-5],[31216,0,-3],[31217,0,-4],[31218,-1,-2],[31219,2,-3],[31220,0,-1],[31221,1,-1],[31222,-3,-7],[31223,0,0],[31224,-1,3],[31225,2,-5],[2955,0,0]]' WHERE id = 1928;
INSERT INTO head_avatar (head_id, avatar_id) VALUES (1643, 14187) ON DUPLICATE KEY UPDATE avatar_id = 14187;
UPDATE item_template SET head = 1643, body = 1646, leg = 1647 WHERE id = 2014; -- Cải Trang Kami
INSERT INTO head_avatar (head_id, avatar_id) VALUES (1648, 14195) ON DUPLICATE KEY UPDATE avatar_id = 14195;
UPDATE item_template SET head = 1648, body = 1654, leg = 1655 WHERE id = 2015; -- Cải Trang Oren
INSERT INTO head_avatar (head_id, avatar_id) VALUES (1656, 14333) ON DUPLICATE KEY UPDATE avatar_id = 14333;
UPDATE item_template SET head = 1656, body = 1660, leg = 1661 WHERE id = 2016; -- Captain America
INSERT INTO head_avatar (head_id, avatar_id) VALUES (1662, 14897) ON DUPLICATE KEY UPDATE avatar_id = 14897;
UPDATE item_template SET head = 1662, body = 1663, leg = 1664 WHERE id = 2017; -- Spider Man
INSERT INTO head_avatar (head_id, avatar_id) VALUES (1665, 14953) ON DUPLICATE KEY UPDATE avatar_id = 14953;
UPDATE item_template SET head = 1665, body = 1668, leg = 1669 WHERE id = 2018; -- hulk
INSERT INTO part (id, TYPE, DATA) VALUES (1959, 0, '[[31131,-2,-15],[31132,-1,-12],[2955,0,0]]');
INSERT INTO part (id, TYPE, DATA) VALUES (1960, 1, '[[31133,0,-7],[31134,-2,-11],[31135,-3,-11],[31136,0,-8],[31137,1,-9],[31138,1,-7],[31139,1,-9],[31140,-1,-11],[31141,1,-11],[31142,-5,-21],[31143,-2,-9],[31144,-5,-15],[31145,-1,-7],[31146,-1,-9],[31147,-2,-11],[31148,-1,-8],[2955,0,0]]');
INSERT INTO part (id, TYPE, DATA) VALUES (1961, 2, '[[31149,5,4],[31150,-3,-5],[31151,1,-5],[31152,0,-3],[31153,0,-4],[31154,-1,-2],[31155,2,-3],[31156,0,-1],[31157,1,-1],[31158,-3,-7],[31159,0,0],[31160,-1,3],[31161,2,-5],[2955,0,0]]');
INSERT INTO head_avatar (head_id, avatar_id) VALUES (1959, 31162) ON DUPLICATE KEY UPDATE avatar_id = 31162;
UPDATE item_template SET head = 1959, body = 1960, leg = 1961 WHERE id = 2019; -- iron Man
INSERT INTO part (id, TYPE, DATA) VALUES (1962, 0, '[[31163,-2,-15],[31164,-1,-12],[2955,0,0]]');
INSERT INTO part (id, TYPE, DATA) VALUES (1963, 1, '[[31165,0,-7],[31166,-2,-11],[31167,-3,-11],[31168,0,-8],[31169,1,-9],[31170,1,-7],[31171,1,-9],[31172,-1,-11],[31173,1,-11],[31174,-5,-21],[31175,-2,-9],[31176,-5,-15],[31177,-1,-7],[31178,-1,-9],[31179,-2,-11],[31180,-1,-8],[2955,0,0]]');
INSERT INTO part (id, TYPE, DATA) VALUES (1964, 2, '[[31181,5,4],[31182,-3,-5],[31183,1,-5],[31184,0,-3],[31185,0,-4],[31186,-1,-2],[31187,2,-3],[31188,0,-1],[31189,1,-1],[31190,-3,-7],[31191,0,0],[31192,-1,3],[31193,2,-5],[2955,0,0]]');
INSERT INTO head_avatar (head_id, avatar_id) VALUES (1962, 31194) ON DUPLICATE KEY UPDATE avatar_id = 31194;
UPDATE item_template SET head = 1962, body = 1963, leg = 1964 WHERE id = 2020; -- Black Panther
INSERT INTO head_avatar (head_id, avatar_id) VALUES (1926, 31226) ON DUPLICATE KEY UPDATE avatar_id = 31226;
UPDATE item_template SET head = 1926, body = 1927, leg = 1928 WHERE id = 2021; -- Cải Trang Thor
INSERT INTO part (id, TYPE, DATA) VALUES (1965, 0, '[[31227,-2,-15],[31228,-1,-12],[2955,0,0]]');
INSERT INTO part (id, TYPE, DATA) VALUES (1966, 1, '[[31229,0,-7],[31230,-2,-11],[31231,-3,-11],[31232,0,-8],[31233,1,-9],[31234,1,-7],[31235,1,-9],[31236,-1,-11],[31237,1,-11],[31238,-5,-21],[31239,-2,-9],[31240,-5,-15],[31241,-1,-7],[31242,-1,-9],[31243,-2,-11],[31244,-1,-8],[2955,0,0]]');
INSERT INTO part (id, TYPE, DATA) VALUES (1967, 2, '[[31245,5,4],[31246,-3,-5],[31247,1,-5],[31248,0,-3],[31249,0,-4],[31250,-1,-2],[31251,2,-3],[31252,0,-1],[31253,1,-1],[31254,-3,-7],[31255,0,0],[31256,-1,3],[31257,2,-5],[2955,0,0]]');
INSERT INTO head_avatar (head_id, avatar_id) VALUES (1965, 31258) ON DUPLICATE KEY UPDATE avatar_id = 31258;
UPDATE item_template SET head = 1965, body = 1966, leg = 1967 WHERE id = 2022; -- Dr.Strange
INSERT INTO part (id, TYPE, DATA) VALUES (1968, 0, '[[31259,-2,-15],[31260,-1,-12],[2955,0,0]]');
INSERT INTO part (id, TYPE, DATA) VALUES (1969, 1, '[[31261,0,-7],[31262,-2,-11],[31263,-3,-11],[31264,0,-8],[31265,1,-9],[31266,1,-7],[31267,1,-9],[31268,-1,-11],[31269,1,-11],[31270,-5,-21],[31271,-2,-9],[31272,-5,-15],[31273,-1,-7],[31274,-1,-9],[31275,-2,-11],[31276,-1,-8],[2955,0,0]]');
INSERT INTO part (id, TYPE, DATA) VALUES (1970, 2, '[[31277,5,4],[31278,-3,-5],[31279,1,-5],[31280,0,-3],[31281,0,-4],[31282,-1,-2],[31283,2,-3],[31284,0,-1],[31285,1,-1],[31286,-3,-7],[31287,0,0],[31288,-1,3],[31289,2,-5],[2955,0,0]]');
INSERT INTO head_avatar (head_id, avatar_id) VALUES (1968, 31290) ON DUPLICATE KEY UPDATE avatar_id = 31290;
UPDATE item_template SET head = 1968, body = 1969, leg = 1970 WHERE id = 2023; -- Thanos
INSERT INTO part (id, TYPE, DATA) VALUES (1971, 0, '[[31291,-2,-15],[31292,-1,-12],[2955,0,0]]');
INSERT INTO part (id, TYPE, DATA) VALUES (1972, 1, '[[31293,0,-7],[31294,-2,-11],[31295,-3,-11],[31296,0,-8],[31297,1,-9],[31298,1,-7],[31299,1,-9],[31300,-1,-11],[31301,1,-11],[31302,-5,-21],[31303,-2,-9],[31304,-5,-15],[31305,-1,-7],[31306,-1,-9],[31307,-2,-11],[31308,-1,-8],[2955,0,0]]');
INSERT INTO part (id, TYPE, DATA) VALUES (1973, 2, '[[31309,5,4],[31310,-3,-5],[31311,1,-5],[31312,0,-3],[31313,0,-4],[31314,-1,-2],[31315,2,-3],[31316,0,-1],[31317,1,-1],[31318,-3,-7],[31319,0,0],[31320,-1,3],[31321,2,-5],[2955,0,0]]');
INSERT INTO head_avatar (head_id, avatar_id) VALUES (1971, 31322) ON DUPLICATE KEY UPDATE avatar_id = 31322;
UPDATE item_template SET head = 1971, body = 1972, leg = 1973 WHERE id = 2024; -- Zamasu Chột
INSERT INTO part (id, TYPE, DATA) VALUES (1974, 0, '[[31323,-2,-15],[31324,-1,-12],[2955,0,0]]');
INSERT INTO part (id, TYPE, DATA) VALUES (1975, 1, '[[31325,0,-7],[31326,-2,-11],[31327,-3,-11],[31328,0,-8],[31329,1,-9],[31330,1,-7],[31331,1,-9],[31332,-1,-11],[31333,1,-11],[31334,-5,-21],[31335,-2,-9],[31336,-5,-15],[31337,-1,-7],[31338,-1,-9],[31339,-2,-11],[31340,-1,-8],[2955,0,0]]');
INSERT INTO part (id, TYPE, DATA) VALUES (1976, 2, '[[31341,5,4],[31342,-3,-5],[31343,1,-5],[31344,0,-3],[31345,0,-4],[31346,-1,-2],[31347,2,-3],[31348,0,-1],[31349,1,-1],[31350,-3,-7],[31351,0,0],[31352,-1,3],[31353,2,-5],[2955,0,0]]');
INSERT INTO head_avatar (head_id, avatar_id) VALUES (1974, 31354) ON DUPLICATE KEY UPDATE avatar_id = 31354;
UPDATE item_template SET head = 1974, body = 1975, leg = 1976 WHERE id = 2025; -- Goku Best
INSERT INTO part (id, TYPE, DATA) VALUES (1977, 0, '[[31355,-2,-15],[31356,-1,-12],[2955,0,0]]');
INSERT INTO part (id, TYPE, DATA) VALUES (1978, 1, '[[31357,0,-7],[31358,-2,-11],[31359,-3,-11],[31360,0,-8],[31361,1,-9],[31362,1,-7],[31363,1,-9],[31364,-1,-11],[31365,1,-11],[31366,-5,-21],[31367,-2,-9],[31368,-5,-15],[31369,-1,-7],[31370,-1,-9],[31371,-2,-11],[31372,-1,-8],[2955,0,0]]');
INSERT INTO part (id, TYPE, DATA) VALUES (1979, 2, '[[31373,5,4],[31374,-3,-5],[31375,1,-5],[31376,0,-3],[31377,0,-4],[31378,-1,-2],[31379,2,-3],[31380,0,-1],[31381,1,-1],[31382,-3,-7],[31383,0,0],[31384,-1,3],[31385,2,-5],[2955,0,0]]');
INSERT INTO head_avatar (head_id, avatar_id) VALUES (1977, 31386) ON DUPLICATE KEY UPDATE avatar_id = 31386;
UPDATE item_template SET head = 1977, body = 1978, leg = 1979 WHERE id = 2026; -- Yajiro Katana
INSERT INTO part (id, TYPE, DATA) VALUES (1980, 0, '[[31387,-2,-15],[31388,-1,-12],[2955,0,0]]');
INSERT INTO part (id, TYPE, DATA) VALUES (1981, 1, '[[31389,0,-7],[31390,-2,-11],[31391,-3,-11],[31392,0,-8],[31393,1,-9],[31394,1,-7],[31395,1,-9],[31396,-1,-11],[31397,1,-11],[31398,-5,-21],[31399,-2,-9],[31400,-5,-15],[31401,-1,-7],[31402,-1,-9],[31403,-2,-11],[31404,-1,-8],[2955,0,0]]');
INSERT INTO part (id, TYPE, DATA) VALUES (1982, 2, '[[31405,5,4],[31406,-3,-5],[31407,1,-5],[31408,0,-3],[31409,0,-4],[31410,-1,-2],[31411,2,-3],[31412,0,-1],[31413,1,-1],[31414,-3,-7],[31415,0,0],[31416,-1,3],[31417,2,-5],[2955,0,0]]');
INSERT INTO head_avatar (head_id, avatar_id) VALUES (1980, 31418) ON DUPLICATE KEY UPDATE avatar_id = 31418;
UPDATE item_template SET head = 1980, body = 1981, leg = 1982 WHERE id = 2027; -- Cải Trang Hearts
INSERT INTO part (id, TYPE, DATA) VALUES (1983, 0, '[[31451,-2,-15],[31452,-1,-12],[2955,0,0]]');
INSERT INTO part (id, TYPE, DATA) VALUES (1984, 1, '[[31453,0,-7],[31454,-2,-11],[31455,-3,-11],[31456,0,-8],[31457,1,-9],[31458,1,-7],[31459,1,-9],[31460,-1,-11],[31461,1,-11],[31462,-5,-21],[31463,-2,-9],[31464,-5,-15],[31465,-1,-7],[31466,-1,-9],[31467,-2,-11],[31468,-1,-8],[2955,0,0]]');
INSERT INTO part (id, TYPE, DATA) VALUES (1985, 2, '[[31469,5,4],[31470,-3,-5],[31471,1,-5],[31472,0,-3],[31473,0,-4],[31474,-1,-2],[31475,2,-3],[31476,0,-1],[31477,1,-1],[31478,-3,-7],[31479,0,0],[31480,-1,3],[31481,2,-5],[2955,0,0]]');
INSERT INTO head_avatar (head_id, avatar_id) VALUES (1983, 31482) ON DUPLICATE KEY UPDATE avatar_id = 31482;
UPDATE item_template SET head = 1983, body = 1984, leg = 1985 WHERE id = 2028; -- Cải Trang Kefla
INSERT INTO part (id, TYPE, DATA) VALUES (1986, 0, '[[31483,-2,-15],[31484,-1,-12],[2955,0,0]]');
INSERT INTO part (id, TYPE, DATA) VALUES (1987, 1, '[[31485,0,-7],[31486,-2,-11],[31487,-3,-11],[31488,0,-8],[31489,1,-9],[31490,1,-7],[31491,1,-9],[31492,-1,-11],[31493,1,-11],[31494,-5,-21],[31495,-2,-9],[31496,-5,-15],[31497,-1,-7],[31498,-1,-9],[31499,-2,-11],[31500,-1,-8],[2955,0,0]]');
INSERT INTO part (id, TYPE, DATA) VALUES (1988, 2, '[[31501,5,4],[31502,-3,-5],[31503,1,-5],[31504,0,-3],[31505,0,-4],[31506,-1,-2],[31507,2,-3],[31508,0,-1],[31509,1,-1],[31510,-3,-7],[31511,0,0],[31512,-1,3],[31513,2,-5],[2955,0,0]]');
INSERT INTO head_avatar (head_id, avatar_id) VALUES (1986, 31514) ON DUPLICATE KEY UPDATE avatar_id = 31514;
UPDATE item_template SET head = 1986, body = 1987, leg = 1988 WHERE id = 2029; -- Cải Trang Gojo
INSERT INTO part (id, TYPE, DATA) VALUES (1989, 0, '[[31515,-2,-15],[31516,-1,-12],[2955,0,0]]');
INSERT INTO part (id, TYPE, DATA) VALUES (1990, 1, '[[31517,0,-7],[31518,-2,-11],[31519,-3,-11],[31520,0,-8],[31521,1,-9],[31522,1,-7],[31523,1,-9],[31524,-1,-11],[31525,1,-11],[31526,-5,-21],[31527,-2,-9],[31528,-5,-15],[31529,-1,-7],[31530,-1,-9],[31531,-2,-11],[31532,-1,-8],[2955,0,0]]');
INSERT INTO part (id, TYPE, DATA) VALUES (1991, 2, '[[31533,5,4],[31534,-3,-5],[31535,1,-5],[31536,0,-3],[31537,0,-4],[31538,-1,-2],[31539,2,-3],[31540,0,-1],[31541,1,-1],[31542,-3,-7],[31543,0,0],[31544,-1,3],[31545,2,-5],[2955,0,0]]');
INSERT INTO head_avatar (head_id, avatar_id) VALUES (1989, 31546) ON DUPLICATE KEY UPDATE avatar_id = 31546;
UPDATE item_template SET head = 1989, body = 1990, leg = 1991 WHERE id = 2030; -- Fide Robot
INSERT INTO part (id, TYPE, DATA) VALUES (1992, 0, '[[31547,-2,-15],[31548,-1,-12],[2955,0,0]]');
INSERT INTO part (id, TYPE, DATA) VALUES (1993, 1, '[[31549,0,-7],[31550,-2,-11],[31551,-3,-11],[31552,0,-8],[31553,1,-9],[31554,1,-7],[31555,1,-9],[31556,-1,-11],[31557,1,-11],[31558,-5,-21],[31559,-2,-9],[31560,-5,-15],[31561,-1,-7],[31562,-1,-9],[31563,-2,-11],[31564,-1,-8],[2955,0,0]]');
INSERT INTO part (id, TYPE, DATA) VALUES (1994, 2, '[[31565,5,4],[31566,-3,-5],[31567,1,-5],[31568,0,-3],[31569,0,-4],[31570,-1,-2],[31571,2,-3],[31572,0,-1],[31573,1,-1],[31574,-3,-7],[31575,0,0],[31576,-1,3],[31577,2,-5],[2955,0,0]]');
INSERT INTO head_avatar (head_id, avatar_id) VALUES (1992, 31578) ON DUPLICATE KEY UPDATE avatar_id = 31578;
UPDATE item_template SET head = 1992, body = 1993, leg = 1994 WHERE id = 2031; -- Majinbuu Ác Quỷ
INSERT INTO part (id, TYPE, DATA) VALUES (1995, 0, '[[31579,-2,-15],[31580,-1,-12],[2955,0,0]]');
INSERT INTO part (id, TYPE, DATA) VALUES (1996, 1, '[[31581,0,-7],[31582,-2,-11],[31583,-3,-11],[31584,0,-8],[31585,1,-9],[31586,1,-7],[31587,1,-9],[31588,-1,-11],[31589,1,-11],[31590,-5,-21],[31591,-2,-9],[31592,-5,-15],[31593,-1,-7],[31594,-1,-9],[31595,-2,-11],[31596,-1,-8],[2955,0,0]]');
INSERT INTO part (id, TYPE, DATA) VALUES (1997, 2, '[[31597,5,4],[31598,-3,-5],[31599,1,-5],[31600,0,-3],[31601,0,-4],[31602,-1,-2],[31603,2,-3],[31604,0,-1],[31605,1,-1],[31606,-3,-7],[31607,0,0],[31608,-1,3],[31609,2,-5],[2955,0,0]]');
INSERT INTO head_avatar (head_id, avatar_id) VALUES (1995, 31610) ON DUPLICATE KEY UPDATE avatar_id = 31610;
UPDATE item_template SET head = 1995, body = 1996, leg = 1997 WHERE id = 2032; -- Cải Trang Xên Đỏ
INSERT INTO part (id, TYPE, DATA) VALUES (1998, 0, '[[31611,-2,-15],[31612,-1,-12],[2955,0,0]]');
INSERT INTO part (id, TYPE, DATA) VALUES (1999, 1, '[[31613,0,-7],[31614,-2,-11],[31615,-3,-11],[31616,0,-8],[31617,1,-9],[31618,1,-7],[31619,1,-9],[31620,-1,-11],[31621,1,-11],[31622,-5,-21],[31623,-2,-9],[31624,-5,-15],[31625,-1,-7],[31626,-1,-9],[31627,-2,-11],[31628,-1,-8],[2955,0,0]]');
INSERT INTO part (id, TYPE, DATA) VALUES (2000, 2, '[[31629,5,4],[31630,-3,-5],[31631,1,-5],[31632,0,-3],[31633,0,-4],[31634,-1,-2],[31635,2,-3],[31636,0,-1],[31637,1,-1],[31638,-3,-7],[31639,0,0],[31640,-1,3],[31641,2,-5],[2955,0,0]]');
INSERT INTO head_avatar (head_id, avatar_id) VALUES (1998, 31642) ON DUPLICATE KEY UPDATE avatar_id = 31642;
UPDATE item_template SET head = 1998, body = 1999, leg = 2000 WHERE id = 2033; -- Cải Trang Anubis
INSERT INTO part (id, TYPE, DATA) VALUES (2001, 0, '[[31643,-2,-15],[31644,-1,-12],[2955,0,0]]');
INSERT INTO part (id, TYPE, DATA) VALUES (2002, 1, '[[31645,0,-7],[31646,-2,-11],[31647,-3,-11],[31648,0,-8],[31649,1,-9],[31650,1,-7],[31651,1,-9],[31652,-1,-11],[31653,1,-11],[31654,-5,-21],[31655,-2,-9],[31656,-5,-15],[31657,-1,-7],[31658,-1,-9],[31659,-2,-11],[31660,-1,-8],[2955,0,0]]');
INSERT INTO part (id, TYPE, DATA) VALUES (2003, 2, '[[31661,5,4],[31662,-3,-5],[31663,1,-5],[31664,0,-3],[31665,0,-4],[31666,-1,-2],[31667,2,-3],[31668,0,-1],[31669,1,-1],[31670,-3,-7],[31671,0,0],[31672,-1,3],[31673,2,-5],[2955,0,0]]');
INSERT INTO head_avatar (head_id, avatar_id) VALUES (2001, 31674) ON DUPLICATE KEY UPDATE avatar_id = 31674;
UPDATE item_template SET head = 2001, body = 2002, leg = 2003 WHERE id = 2034; -- Cải Trang Gaara
INSERT INTO part (id, TYPE, DATA) VALUES (2004, 0, '[[31675,-2,-15],[31676,-1,-12],[2955,0,0]]');
INSERT INTO part (id, TYPE, DATA) VALUES (2005, 1, '[[31677,0,-7],[31678,-2,-11],[31679,-3,-11],[31680,0,-8],[31681,1,-9],[31682,1,-7],[31683,1,-9],[31684,-1,-11],[31685,1,-11],[31686,-5,-21],[31687,-2,-9],[31688,-5,-15],[31689,-1,-7],[31690,-1,-9],[31691,-2,-11],[31692,-1,-8],[2955,0,0]]');
INSERT INTO part (id, TYPE, DATA) VALUES (2006, 2, '[[31693,5,4],[31694,-3,-5],[31695,1,-5],[31696,0,-3],[31697,0,-4],[31698,-1,-2],[31699,2,-3],[31700,0,-1],[31701,1,-1],[31702,-3,-7],[31703,0,0],[31704,-1,3],[31705,2,-5],[2955,0,0]]');
INSERT INTO head_avatar (head_id, avatar_id) VALUES (2004, 31706) ON DUPLICATE KEY UPDATE avatar_id = 31706;
UPDATE item_template SET head = 2004, body = 2005, leg = 2006 WHERE id = 2035; -- ChiChi Tóc Đỏ
INSERT INTO part (id, TYPE, DATA) VALUES (2007, 0, '[[31739,-2,-15],[31740,-1,-12],[2955,0,0]]');
INSERT INTO part (id, TYPE, DATA) VALUES (2008, 1, '[[31741,0,-7],[31742,-2,-11],[31743,-3,-11],[31744,0,-8],[31745,1,-9],[31746,1,-7],[31747,1,-9],[31748,-1,-11],[31749,1,-11],[31750,-5,-21],[31751,-2,-9],[31752,-5,-15],[31753,-1,-7],[31754,-1,-9],[31755,-2,-11],[31756,-1,-8],[2955,0,0]]');
INSERT INTO part (id, TYPE, DATA) VALUES (2009, 2, '[[31757,5,4],[31758,-3,-5],[31759,1,-5],[31760,0,-3],[31761,0,-4],[31762,-1,-2],[31763,2,-3],[31764,0,-1],[31765,1,-1],[31766,-3,-7],[31767,0,0],[31768,-1,3],[31769,2,-5],[2955,0,0]]');
INSERT INTO head_avatar (head_id, avatar_id) VALUES (2007, 31770) ON DUPLICATE KEY UPDATE avatar_id = 31770;
UPDATE item_template SET head = 2007, body = 2008, leg = 2009 WHERE id = 2036; -- Hakai Toppo
INSERT INTO part (id, TYPE, DATA) VALUES (2010, 0, '[[31772,-2,-15],[31772,-1,-12],[2955,0,0]]');
INSERT INTO head_avatar (head_id, avatar_id) VALUES (2010, 31772) ON DUPLICATE KEY UPDATE avatar_id = 31772;
UPDATE item_template SET head = 2010, body = 1886, leg = 1887 WHERE id = 2037; -- Trunks Ssj God
INSERT INTO part (id, TYPE, DATA) VALUES (2011, 0, '[[31773,-2,-15],[31774,-1,-12],[2955,0,0]]');
INSERT INTO part (id, TYPE, DATA) VALUES (2012, 1, '[[31775,0,-7],[31776,-2,-11],[31777,-3,-11],[31778,0,-8],[31779,1,-9],[31780,1,-7],[31781,1,-9],[31782,-1,-11],[31783,1,-11],[31784,-5,-21],[31785,-2,-9],[31786,-5,-15],[31787,-1,-7],[31788,-1,-9],[31789,-2,-11],[31790,-1,-8],[2955,0,0]]');
INSERT INTO part (id, TYPE, DATA) VALUES (2013, 2, '[[31791,5,4],[31792,-3,-5],[31793,1,-5],[31794,0,-3],[31795,0,-4],[31796,-1,-2],[31797,2,-3],[31798,0,-1],[31799,1,-1],[31800,-3,-7],[31801,0,0],[31802,-1,3],[31803,2,-5],[2955,0,0]]');
INSERT INTO head_avatar (head_id, avatar_id) VALUES (2011, 31804) ON DUPLICATE KEY UPDATE avatar_id = 31804;
UPDATE item_template SET head = 2011, body = 2012, leg = 2013 WHERE id = 2038; -- Vegeta Ultra Ego
INSERT INTO part (id, TYPE, DATA) VALUES (2014, 0, '[[31805,-2,-15],[31806,-1,-12],[2955,0,0]]');
INSERT INTO part (id, TYPE, DATA) VALUES (2015, 1, '[[31807,0,-7],[31808,-2,-11],[31809,-3,-11],[31810,0,-8],[31811,1,-9],[31812,1,-7],[31813,1,-9],[31814,-1,-11],[31815,1,-11],[31816,-5,-21],[31817,-2,-9],[31818,-5,-15],[31819,-1,-7],[31820,-1,-9],[31821,-2,-11],[31822,-1,-8],[2955,0,0]]');
INSERT INTO part (id, TYPE, DATA) VALUES (2016, 2, '[[31823,5,4],[31824,-3,-5],[31825,1,-5],[31826,0,-3],[31827,0,-4],[31828,-1,-2],[31829,2,-3],[31830,0,-1],[31831,1,-1],[31832,-3,-7],[31833,0,0],[31834,-1,3],[31835,2,-5],[2955,0,0]]');
INSERT INTO head_avatar (head_id, avatar_id) VALUES (2014, 31836) ON DUPLICATE KEY UPDATE avatar_id = 31836;
UPDATE item_template SET head = 2014, body = 2015, leg = 2016 WHERE id = 2039; -- Zeno Kid
INSERT INTO part (id, TYPE, DATA) VALUES (2017, 0, '[[31837,-2,-15],[31838,-1,-12],[2955,0,0]]');
INSERT INTO part (id, TYPE, DATA) VALUES (2018, 1, '[[31839,0,-7],[31840,-2,-11],[31841,-3,-11],[31842,0,-8],[31843,1,-9],[31844,1,-7],[31845,1,-9],[31846,-1,-11],[31847,1,-11],[31848,-5,-21],[31849,-2,-9],[31850,-5,-15],[31851,-1,-7],[31852,-1,-9],[31853,-2,-11],[31854,-1,-8],[2955,0,0]]');
INSERT INTO part (id, TYPE, DATA) VALUES (2019, 2, '[[31855,5,4],[31856,-3,-5],[31857,1,-5],[31858,0,-3],[31859,0,-4],[31860,-1,-2],[31861,2,-3],[31862,0,-1],[31863,1,-1],[31864,-3,-7],[31865,0,0],[31866,-1,3],[31867,2,-5],[2955,0,0]]');
INSERT INTO head_avatar (head_id, avatar_id) VALUES (2017, 31868) ON DUPLICATE KEY UPDATE avatar_id = 31868;
UPDATE item_template SET head = 2017, body = 2018, leg = 2019 WHERE id = 2040; -- Cải Trang 74
INSERT INTO part (id, TYPE, DATA) VALUES (2020, 0, '[[31869,-2,-15],[31870,-1,-12],[2955,0,0]]');
INSERT INTO part (id, TYPE, DATA) VALUES (2021, 1, '[[31871,0,-7],[31872,-2,-11],[31873,-3,-11],[31874,0,-8],[31875,1,-9],[31876,1,-7],[31877,1,-9],[31878,-1,-11],[31879,1,-11],[31880,-5,-21],[31881,-2,-9],[31882,-5,-15],[31883,-1,-7],[31884,-1,-9],[31885,-2,-11],[31886,-1,-8],[2955,0,0]]');
INSERT INTO part (id, TYPE, DATA) VALUES (2022, 2, '[[31887,5,4],[31888,-3,-5],[31889,1,-5],[31890,0,-3],[31891,0,-4],[31892,-1,-2],[31893,2,-3],[31894,0,-1],[31895,1,-1],[31896,-3,-7],[31897,0,0],[31898,-1,3],[31899,2,-5],[2955,0,0]]');
INSERT INTO head_avatar (head_id, avatar_id) VALUES (2020, 31900) ON DUPLICATE KEY UPDATE avatar_id = 31900;
UPDATE item_template SET head = 2020, body = 2021, leg = 2022 WHERE id = 2041; -- Orange Piccolo
INSERT INTO part (id, TYPE, DATA) VALUES (2023, 0, '[[31901,-2,-15],[31902,-1,-12],[2955,0,0]]');
INSERT INTO part (id, TYPE, DATA) VALUES (2024, 1, '[[31903,0,-7],[31904,-2,-11],[31905,-3,-11],[31906,0,-8],[31907,1,-9],[31908,1,-7],[31909,1,-9],[31910,-1,-11],[31911,1,-11],[31912,-5,-21],[31913,-2,-9],[31914,-5,-15],[31915,-1,-7],[31916,-1,-9],[31917,-2,-11],[31918,-1,-8],[2955,0,0]]');
INSERT INTO part (id, TYPE, DATA) VALUES (2025, 2, '[[31919,5,4],[31920,-3,-5],[31921,1,-5],[31922,0,-3],[31923,0,-4],[31924,-1,-2],[31925,2,-3],[31926,0,-1],[31927,1,-1],[31928,-3,-7],[31929,0,0],[31930,-1,3],[31931,2,-5],[2955,0,0]]');
INSERT INTO head_avatar (head_id, avatar_id) VALUES (2023, 31932) ON DUPLICATE KEY UPDATE avatar_id = 31932;
UPDATE item_template SET head = 2023, body = 2024, leg = 2025 WHERE id = 2042; -- Vegito Điên Cuồng
INSERT INTO part (id, TYPE, DATA) VALUES (2026, 0, '[[31933,-2,-15],[31934,-1,-12],[2955,0,0]]');
INSERT INTO part (id, TYPE, DATA) VALUES (2027, 1, '[[31935,0,-7],[31936,-2,-11],[31937,-3,-11],[31938,0,-8],[31939,1,-9],[31940,1,-7],[31941,1,-9],[31942,-1,-11],[31943,1,-11],[31944,-5,-21],[31945,-2,-9],[31946,-5,-15],[31947,-1,-7],[31948,-1,-9],[31949,-2,-11],[31950,-1,-8],[2955,0,0]]');
INSERT INTO part (id, TYPE, DATA) VALUES (2028, 2, '[[31951,5,4],[31952,-3,-5],[31953,1,-5],[31954,0,-3],[31955,0,-4],[31956,-1,-2],[31957,2,-3],[31958,0,-1],[31959,1,-1],[31960,-3,-7],[31961,0,0],[31962,-1,3],[31963,2,-5],[2955,0,0]]');
INSERT INTO head_avatar (head_id, avatar_id) VALUES (2026, 31964) ON DUPLICATE KEY UPDATE avatar_id = 31964;
UPDATE item_template SET head = 2026, body = 2027, leg = 2028 WHERE id = 2043; -- Saiyan God
INSERT INTO part (id, TYPE, DATA) VALUES (2029, 0, '[[31965,-2,-15],[31966,-1,-12],[2955,0,0]]');
INSERT INTO part (id, TYPE, DATA) VALUES (2030, 1, '[[31967,0,-7],[31968,-2,-11],[31969,-3,-11],[31970,0,-8],[31971,1,-9],[31972,1,-7],[31973,1,-9],[31974,-1,-11],[31975,1,-11],[31976,-5,-21],[31977,-2,-9],[31978,-5,-15],[31979,-1,-7],[31980,-1,-9],[31981,-2,-11],[31982,-1,-8],[2955,0,0]]');
INSERT INTO part (id, TYPE, DATA) VALUES (2031, 2, '[[31983,5,4],[31984,-3,-5],[31985,1,-5],[31986,0,-3],[31987,0,-4],[31988,-1,-2],[31989,2,-3],[31990,0,-1],[31991,1,-1],[31992,-3,-7],[31993,0,0],[31994,-1,3],[31995,2,-5],[2955,0,0]]');
INSERT INTO head_avatar (head_id, avatar_id) VALUES (2029, 31996) ON DUPLICATE KEY UPDATE avatar_id = 31996;
UPDATE item_template SET head = 2029, body = 2030, leg = 2031 WHERE id = 2044; -- Cải Trang Fu
INSERT INTO part (id, TYPE, DATA) VALUES (2032, 0, '[[31997,-2,-15],[31998,-1,-12],[2955,0,0]]');
INSERT INTO part (id, TYPE, DATA) VALUES (2033, 1, '[[31999,0,-7],[32000,-2,-11],[32001,-3,-11],[32002,0,-8],[32003,1,-9],[32004,1,-7],[32005,1,-9],[32006,-1,-11],[32007,1,-11],[32008,-5,-21],[32009,-2,-9],[32010,-5,-15],[32011,-1,-7],[32012,-1,-9],[32013,-2,-11],[32014,-1,-8],[2955,0,0]]');
INSERT INTO part (id, TYPE, DATA) VALUES (2034, 2, '[[32015,5,4],[32016,-3,-5],[32017,1,-5],[32018,0,-3],[32019,0,-4],[32020,-1,-2],[32021,2,-3],[32022,0,-1],[32023,1,-1],[32024,-3,-7],[32025,0,0],[32026,-1,3],[32027,2,-5],[2955,0,0]]');
INSERT INTO head_avatar (head_id, avatar_id) VALUES (2032, 32028) ON DUPLICATE KEY UPDATE avatar_id = 32028;
UPDATE item_template SET head = 2032, body = 2033, leg = 2034 WHERE id = 2045; -- Vegeta Ego
INSERT INTO part (id, TYPE, DATA) VALUES (2035, 0, '[[32060,-2,-15],[32061,-1,-12],[2955,0,0]]');
INSERT INTO part (id, TYPE, DATA) VALUES (2036, 1, '[[32062,0,-7],[32063,-2,-11],[32064,-3,-11],[32065,0,-8],[32066,1,-9],[32067,1,-7],[32068,1,-9],[32069,-1,-11],[32070,1,-11],[32071,-5,-21],[32072,-2,-9],[32073,-5,-15],[32074,-1,-7],[32075,-1,-9],[32076,-2,-11],[32077,-1,-8],[2955,0,0]]');
INSERT INTO part (id, TYPE, DATA) VALUES (2037, 2, '[[32078,5,4],[32079,-3,-5],[32080,1,-5],[32081,0,-3],[32082,0,-4],[32083,-1,-2],[32084,2,-3],[32085,0,-1],[32086,1,-1],[32087,-3,-7],[32088,0,0],[32089,-1,3],[32090,2,-5],[2955,0,0]]');
INSERT INTO head_avatar (head_id, avatar_id) VALUES (2035, 32091) ON DUPLICATE KEY UPDATE avatar_id = 32091;
UPDATE item_template SET head = 2035, body = 2036, leg = 2037 WHERE id = 2046; -- Cheems
INSERT INTO part (id, TYPE, DATA) VALUES (2038, 0, '[[32061,-2,-15],[32062,-1,-12],[2955,0,0]]');
INSERT INTO part (id, TYPE, DATA) VALUES (2039, 1, '[[32063,0,-7],[32064,-2,-11],[32065,-3,-11],[32066,0,-8],[32067,1,-9],[32068,1,-7],[32069,1,-9],[32070,-1,-11],[32071,1,-11],[32072,-5,-21],[32073,-2,-9],[32074,-5,-15],[32075,-1,-7],[32076,-1,-9],[32077,-2,-11],[32078,-1,-8],[2955,0,0]]');
INSERT INTO part (id, TYPE, DATA) VALUES (2040, 2, '[[32079,5,4],[32080,-3,-5],[32081,1,-5],[32082,0,-3],[32083,0,-4],[32084,-1,-2],[32085,2,-3],[32086,0,-1],[32087,1,-1],[32088,-3,-7],[32089,0,0],[32090,-1,3],[32091,2,-5],[2955,0,0]]');
INSERT INTO head_avatar (head_id, avatar_id) VALUES (2038, 32092) ON DUPLICATE KEY UPDATE avatar_id = 32092;
UPDATE item_template SET head = 2038, body = 2039, leg = 2040 WHERE id = 2047; -- Quitela
INSERT INTO part (id, TYPE, DATA) VALUES (2041, 0, '[[32125,-2,-15],[32126,-1,-12],[2955,0,0]]');
INSERT INTO part (id, TYPE, DATA) VALUES (2042, 1, '[[32127,0,-7],[32128,-2,-11],[32129,-3,-11],[32130,0,-8],[32131,1,-9],[32132,1,-7],[32133,1,-9],[32134,-1,-11],[32135,1,-11],[32136,-5,-21],[32137,-2,-9],[32138,-5,-15],[32139,-1,-7],[32140,-1,-9],[32141,-2,-11],[32142,-1,-8],[2955,0,0]]');
INSERT INTO part (id, TYPE, DATA) VALUES (2043, 2, '[[32143,5,4],[32144,-3,-5],[32145,1,-5],[32146,0,-3],[32147,0,-4],[32148,-1,-2],[32149,2,-3],[32150,0,-1],[32151,1,-1],[32152,-3,-7],[32153,0,0],[32154,-1,3],[32155,2,-5],[2955,0,0]]');
INSERT INTO head_avatar (head_id, avatar_id) VALUES (2041, 32156) ON DUPLICATE KEY UPDATE avatar_id = 32156;
UPDATE item_template SET head = 2041, body = 2042, leg = 2043 WHERE id = 2048; -- Ayaka Genshin Impact
INSERT INTO part (id, TYPE, DATA) VALUES (2044, 0, '[[32158,-2,-15],[32159,-1,-12],[2955,0,0]]');
INSERT INTO part (id, TYPE, DATA) VALUES (2045, 1, '[[32160,0,-7],[32161,-2,-11],[32162,-3,-11],[32163,0,-8],[32164,1,-9],[32165,1,-7],[32166,1,-9],[32167,-1,-11],[32168,1,-11],[32169,-5,-21],[32170,-2,-9],[32171,-5,-15],[32172,-1,-7],[32173,-1,-9],[32174,-2,-11],[32175,-1,-8],[2955,0,0]]');
INSERT INTO part (id, TYPE, DATA) VALUES (2046, 2, '[[32176,5,4],[32177,-3,-5],[32178,1,-5],[32179,0,-3],[32180,0,-4],[32181,-1,-2],[32182,2,-3],[32183,0,-1],[32184,1,-1],[32185,-3,-7],[32186,0,0],[32187,-1,3],[32188,2,-5],[2955,0,0]]');
INSERT INTO head_avatar (head_id, avatar_id) VALUES (2044, 32189) ON DUPLICATE KEY UPDATE avatar_id = 32189;
UPDATE item_template SET head = 2044, body = 2045, leg = 2046 WHERE id = 2049; -- Xiao Genshin Impact
INSERT INTO part (id, TYPE, DATA) VALUES (2047, 0, '[[32191,-2,-15],[32192,-1,-12],[2955,0,0]]');
INSERT INTO part (id, TYPE, DATA) VALUES (2048, 1, '[[32193,0,-7],[32194,-2,-11],[32195,-3,-11],[32196,0,-8],[32197,1,-9],[32198,1,-7],[32199,1,-9],[32200,-1,-11],[32201,1,-11],[32202,-5,-21],[32203,-2,-9],[32204,-5,-15],[32205,-1,-7],[32206,-1,-9],[32207,-2,-11],[32208,-1,-8],[2955,0,0]]');
INSERT INTO part (id, TYPE, DATA) VALUES (2049, 2, '[[32209,5,4],[32210,-3,-5],[32211,1,-5],[32212,0,-3],[32213,0,-4],[32214,-1,-2],[32215,2,-3],[32216,0,-1],[32217,1,-1],[32218,-3,-7],[32219,0,0],[32220,-1,3],[32221,2,-5],[2955,0,0]]');
INSERT INTO head_avatar (head_id, avatar_id) VALUES (2047, 32222) ON DUPLICATE KEY UPDATE avatar_id = 32222;
UPDATE item_template SET head = 2047, body = 2048, leg = 2049 WHERE id = 2050; -- HuTao Genshin Impact
INSERT INTO head_avatar (head_id, avatar_id) VALUES (1765, 29259) ON DUPLICATE KEY UPDATE avatar_id = 29259;
UPDATE item_template SET head = 1765, body = 1766, leg = 1767 WHERE id = 2051; -- Omega Thần Long
INSERT INTO head_avatar (head_id, avatar_id) VALUES (1768, 29291) ON DUPLICATE KEY UPDATE avatar_id = 29291;
UPDATE item_template SET head = 1768, body = 1769, leg = 1770 WHERE id = 2052; -- Vegeta Silver Sales
INSERT INTO head_avatar (head_id, avatar_id) VALUES (1771, 29323) ON DUPLICATE KEY UPDATE avatar_id = 29323;
UPDATE item_template SET head = 1771, body = 1772, leg = 1773 WHERE id = 2053; -- Zenots
INSERT INTO head_avatar (head_id, avatar_id) VALUES (1774, 29355) ON DUPLICATE KEY UPDATE avatar_id = 29355;
UPDATE item_template SET head = 1774, body = 1775, leg = 1776 WHERE id = 2054; -- Super Zenots
INSERT INTO head_avatar (head_id, avatar_id) VALUES (1777, 29387) ON DUPLICATE KEY UPDATE avatar_id = 29387;
UPDATE item_template SET head = 1777, body = 1778, leg = 1779 WHERE id = 2055; -- Super Zenots
INSERT INTO head_avatar (head_id, avatar_id) VALUES (1780, 29419) ON DUPLICATE KEY UPDATE avatar_id = 29419;
UPDATE item_template SET head = 1780, body = 1781, leg = 1782 WHERE id = 2056; -- Super Zenots
INSERT INTO head_avatar (head_id, avatar_id) VALUES (1783, 15233) ON DUPLICATE KEY UPDATE avatar_id = 15233;
UPDATE item_template SET head = 1783, body = 1784, leg = 1785 WHERE id = 2057; -- Germa 01
INSERT INTO part (id, TYPE, DATA) VALUES (2050, 0, '[[32416,-2,-15],[32417,-1,-12],[2955,0,0]]');
INSERT INTO part (id, TYPE, DATA) VALUES (2051, 1, '[[32418,0,-7],[32419,-2,-11],[32420,-3,-11],[32421,0,-8],[32422,1,-9],[32423,1,-7],[32424,1,-9],[32425,-1,-11],[32426,1,-11],[32427,-5,-21],[32428,-2,-9],[32429,-5,-15],[32430,-1,-7],[32431,-1,-9],[32432,-2,-11],[32433,-1,-8],[2955,0,0]]');
INSERT INTO part (id, TYPE, DATA) VALUES (2052, 2, '[[32434,5,4],[32435,-3,-5],[32436,1,-5],[32437,0,-3],[32438,0,-4],[32439,-1,-2],[32440,2,-3],[32441,0,-1],[32442,1,-1],[32443,-3,-7],[32444,0,0],[32445,-1,3],[32446,2,-5],[2955,0,0]]');
INSERT INTO head_avatar (head_id, avatar_id) VALUES (2050, 32447) ON DUPLICATE KEY UPDATE avatar_id = 32447;
UPDATE item_template SET head = 2050, body = 2051, leg = 2052 WHERE id = 2058; -- Germa 02
INSERT INTO part (id, TYPE, DATA) VALUES (2053, 0, '[[32448,-2,-15],[32449,-1,-12],[2955,0,0]]');
INSERT INTO part (id, TYPE, DATA) VALUES (2054, 1, '[[32450,0,-7],[32451,-2,-11],[32452,-3,-11],[32453,0,-8],[32454,1,-9],[32455,1,-7],[32456,1,-9],[32457,-1,-11],[32458,1,-11],[32459,-5,-21],[32460,-2,-9],[32461,-5,-15],[32462,-1,-7],[32463,-1,-9],[32464,-2,-11],[32465,-1,-8],[2955,0,0]]');
INSERT INTO part (id, TYPE, DATA) VALUES (2055, 2, '[[32466,5,4],[32467,-3,-5],[32468,1,-5],[32469,0,-3],[32470,0,-4],[32471,-1,-2],[32472,2,-3],[32473,0,-1],[32474,1,-1],[32475,-3,-7],[32476,0,0],[32477,-1,3],[32478,2,-5],[2955,0,0]]');
INSERT INTO head_avatar (head_id, avatar_id) VALUES (2053, 32479) ON DUPLICATE KEY UPDATE avatar_id = 32479;
UPDATE item_template SET head = 2053, body = 2054, leg = 2055 WHERE id = 2059; -- Goku Ultra Instinct
INSERT INTO part (id, TYPE, DATA) VALUES (2056, 0, '[[32480,-2,-15],[32481,-1,-12],[2955,0,0]]');
INSERT INTO part (id, TYPE, DATA) VALUES (2057, 1, '[[32482,0,-7],[32483,-2,-11],[32484,-3,-11],[32485,0,-8],[32486,1,-9],[32487,1,-7],[32488,1,-9],[32489,-1,-11],[32490,1,-11],[32491,-5,-21],[32492,-2,-9],[32493,-5,-15],[32494,-1,-7],[32495,-1,-9],[32496,-2,-11],[32497,-1,-8],[2955,0,0]]');
INSERT INTO part (id, TYPE, DATA) VALUES (2058, 2, '[[32498,5,4],[32499,-3,-5],[32500,1,-5],[32501,0,-3],[32502,0,-4],[32503,-1,-2],[32504,2,-3],[32505,0,-1],[32506,1,-1],[32507,-3,-7],[32508,0,0],[32509,-1,3],[32510,2,-5],[2955,0,0]]');
INSERT INTO head_avatar (head_id, avatar_id) VALUES (2056, 32511) ON DUPLICATE KEY UPDATE avatar_id = 32511;
UPDATE item_template SET head = 2056, body = 2057, leg = 2058 WHERE id = 2060; -- Nakroth
INSERT INTO part (id, TYPE, DATA) VALUES (2059, 0, '[[32576,-2,-15],[32577,-1,-12],[2955,0,0]]');
INSERT INTO part (id, TYPE, DATA) VALUES (2060, 1, '[[32578,0,-7],[32579,-2,-11],[32580,-3,-11],[32581,0,-8],[32582,1,-9],[32583,1,-7],[32584,1,-9],[32585,-1,-11],[32586,1,-11],[32587,-5,-21],[32588,-2,-9],[32589,-5,-15],[32590,-1,-7],[32591,-1,-9],[32592,-2,-11],[32593,-1,-8],[2955,0,0]]');
INSERT INTO part (id, TYPE, DATA) VALUES (2061, 2, '[[32594,5,4],[32595,-3,-5],[32596,1,-5],[32597,0,-3],[32598,0,-4],[32599,-1,-2],[32600,2,-3],[32601,0,-1],[32602,1,-1],[32603,-3,-7],[32604,0,0],[32605,-1,3],[32606,2,-5],[2955,0,0]]');
INSERT INTO head_avatar (head_id, avatar_id) VALUES (2059, 32607) ON DUPLICATE KEY UPDATE avatar_id = 32607;
UPDATE item_template SET head = 2059, body = 2060, leg = 2061 WHERE id = 2061; -- Jiren
