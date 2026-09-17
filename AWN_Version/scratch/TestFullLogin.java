import jbcd.data.GodGK;
import network.session.MySession;

public class TestFullLogin {
    public static void main(String[] args) {
        try {
            System.out.println("Testing GodGK.login for atrai...");
            MySession session = new MySession(null);
            GodGK.login(session, "atrai", "1");
            System.out.println("GodGK.login finished without exception!");
            if (session.player != null) {
                System.out.println("Player loaded: " + session.player.name + ", gender: " + session.player.gender + ", map: " + (session.player.zone != null ? session.player.zone.map.mapId : "null"));
            } else {
                System.out.println("session.player is null (may need actual password or client handshake). Check password in DB.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
