package me.pk2.canalosaland.cphone.api;

import static me.pk2.canalosaland.cphone.log.LoggerUtil.*;

import android.app.AlertDialog;
import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.ArrayList;
import java.util.List;

import me.pk2.canalosaland.cphone.MainActivity;
import me.pk2.canalosaland.cphone.PanelActivity;
import me.pk2.canalosaland.cphone.R;
import me.pk2.canalosaland.cphone.api.models.CarrierModel;
import me.pk2.canalosaland.cphone.api.models.PlayerModel;

public class CanelonesAPI {
    public static RequestQueue REQ_QUEUE;
    public static void initAPI(Context context) {
        REQ_QUEUE = Volley.newRequestQueue(context);
    }

    public static String API_URL = "http://net2.node7.furryporn.fun:25448/api/";
    public static String _API(String text) { return API_URL+text; }
    public static String _API_CARRIERS() { return _API("carriers"); }
    public static String _API_PLAYERS() { return _API("players"); }
    public static String _API_PLAYER(String uuid) { return _API("player/" + uuid); }
    public static String _API_PLAYER_ACTION(String uuid, String action) { return _API_PLAYER(uuid)+"/action"+action; }
    public static String _API_PLAYER_ACTION_SESSIONAUTH(String uuid, String pass) { return _API_PLAYER_ACTION(uuid, "/session_auth?pass=" + pass); }
    public static String _API_PLAYER_ACTION_SESSIONVALID(String uuid, String token) { return _API_PLAYER_ACTION(uuid, "/session_valid?token=" + token); }
    public static String _API_PLAYER_ACTION_SESSIONCLOSE(String uuid, String token) { return _API_PLAYER_ACTION(uuid, "/session_close?token=" + token); }
    public static String _API_PLAYER_ACTION_CARRIERJOIN(String uuid, String token, String carrier) { return _API_PLAYER_ACTION(uuid, "/carrier_join?token=" + token + "&carrier=" + carrier); }
    public static String _API_PLAYER_ACTION_CARRIERLEAVE(String uuid, String token) { return _API_PLAYER_ACTION(uuid, "/carrier_leave?token=" + token); }
    public static String _API_PLAYER_ACTION_CARRIERSIGNAL(String uuid) { return _API_PLAYER_ACTION(uuid, "/carrier_signal"); }
    public static String _API_PLAYER_ACTION_BIZUMSEND(String uuid, String token, String to, String amount) { return _API_PLAYER_ACTION(uuid, "/bizum_send?token=" + token + "&to=" + to + "&amount=" + amount); }

    public static void getOnlinePlayers(RequestQueue queue, Response.Listener<List<PlayerModel>> response) {
        StringRequest request = new StringRequest(Request.Method.GET, _API_PLAYERS(), res -> {
            List<PlayerModel> players = new ArrayList<>();

            playersCheck: {
                if (res == null || res.contentEquals("No players online!"))
                    break playersCheck;

                String[] splitUsers = res.contains("\n")?res.split("\n"):new String[]{res};
                for(String user : splitUsers) {
                    if(!user.contains(" "))
                        continue;
                    String[] splitUser = user.split(" ");
                    String uuid = splitUser[0];
                    String username = splitUser[1];

                    PlayerModel player = new PlayerModel(uuid, username);
                    players.add(player);
                }
            }

            response.onResponse(players);
        }, error -> response.onResponse(new ArrayList<>()));

        queue.add(request);
    }
    public static void getCarriers(RequestQueue queue, Response.Listener<List<CarrierModel>> response) {
        StringRequest request = new StringRequest(
                Request.Method.GET,
                _API_CARRIERS(),
                res -> {
                    List<CarrierModel> carriers = new ArrayList<>();

                    carrierCheck: {
                        if (res == null || res.contentEquals("No carriers"))
                            break carrierCheck;

                        String[] splitCarriers = res.contains("\n")?res.split("\n"):new String[]{res};
                        for(String carrier : splitCarriers) {
                            if(!carrier.contains(" "))
                                continue;
                            String[] splitCarrier = carrier.split(" ");
                            String name = splitCarrier[0];
                            int subscribers = Integer.parseInt(splitCarrier[1]);
                            double pricePerText = Double.parseDouble(splitCarrier[2]);

                            CarrierModel model = new CarrierModel(name, subscribers, pricePerText);
                            carriers.add(model);
                        }
                    }

                    response.onResponse(carriers);
                },
                error -> response.onResponse(new ArrayList<>()));
        queue.add(request);
    }
}