package me.pk2.canalosaland.cphone.api.models;

import static me.pk2.canalosaland.cphone.api.CanelonesAPI.*;
import static me.pk2.canalosaland.cphone.log.LoggerUtil.*;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import me.pk2.canalosaland.cphone.MainActivity;

public class PlayerModelAuthenticated extends PlayerModel {
    private String token;
    public RequestQueue queue;

    public PlayerModelAuthenticated(String uuid, String name) {
        super(uuid, name);

        queue = Volley.newRequestQueue(MainActivity.INSTANCE);
        token = "";
    }

    public void session_auth(String pass, Response.Listener<String> res) {
        StringRequest request = new StringRequest(
                Request.Method.GET,
                _API_PLAYER_ACTION_SESSIONAUTH(uuid, pass),
                response -> {
                    switch (response) {
                        case "Invalid player":
                            res.onResponse("UNK_PLAYER");
                            break;
                        case "Offline player":
                            res.onResponse("OFF_PLAYER");
                            break;
                        case "Invalid password":
                            res.onResponse("WRONG_PASS");
                            break;
                        default:
                            res.onResponse(response); // Should be a valid token.
                            token = response;
                            break;
                    }
                },
                error -> res.onResponse("ERROR"));
        queue.add(request);
    }

    public void session_valid(Response.Listener<String> res) {
        StringRequest request = new StringRequest(
                Request.Method.GET,
                _API_PLAYER_ACTION_SESSIONVALID(uuid, token),
                response -> {
                    switch(response) {
                        case "Invalid player":
                            res.onResponse("UNK_PLAYER");
                            break;
                        case "Offline player":
                            res.onResponse("OFF_PLAYER");
                            break;
                        case "Invalid token":
                            res.onResponse("INVALID");
                            break;
                        case "Valid token":
                            res.onResponse("VALID");
                            break;
                        default:
                            res.onResponse(response);
                            break;
                    }
                },
                error -> res.onResponse("ERROR"));
        queue.add(request);
    }

    public void session_close(Response.Listener<String> res) {
        StringRequest request = new StringRequest(
                Request.Method.GET,
                _API_PLAYER_ACTION_SESSIONCLOSE(uuid, token),
                response -> {
                    switch (response) {
                        case "Invalid player":
                            res.onResponse("UNK_PLAYER");
                            break;
                        case "Offline player":
                            res.onResponse("OFF_PLAYER");
                            break;
                        case "Invalid token":
                            res.onResponse("INVALID");
                            break;
                        case "Closed session":
                            res.onResponse("CLOSED");
                            break;
                        default:
                            res.onResponse(response);
                            break;
                    }
                },
                error -> res.onResponse("ERROR"));
        queue.add(request);
    }

    public void carrier_join(String carrier, Response.Listener<String> res) {
        StringRequest request = new StringRequest(
                Request.Method.GET,
                _API_PLAYER_ACTION_CARRIERJOIN(uuid, token, carrier),
                response -> {
                    switch(response) {
                        case "Invalid player":
                            res.onResponse("UNK_PLAYER");
                            break;
                        case "Offline player":
                            res.onResponse("OFF_PLAYER");
                            break;
                        case "Invalid token":
                            res.onResponse("INVALID");
                            break;
                        case "Invalid carrier":
                            res.onResponse("UNK_CARRIER");
                            break;
                        case "Joined carrier":
                            res.onResponse("JOINED");
                            break;
                        default:
                            res.onResponse(response);
                            break;
                    }
                },
                error -> res.onResponse("ERROR"));
        queue.add(request);
    }

    public void carrier_leave(Response.Listener<String> res) {
        StringRequest request = new StringRequest(
                Request.Method.GET,
                _API_PLAYER_ACTION_CARRIERLEAVE(uuid, token),
                response -> {
                    switch (response) {
                        case "Invalid player":
                            res.onResponse("UNK_PLAYER");
                            break;
                        case "Offline player":
                            res.onResponse("OFF_PLAYER");
                            break;
                        case "Invalid token":
                            res.onResponse("INVALID");
                            break;
                        case "You are not in a carrier":
                            res.onResponse("NOT_CARRIER");
                            break;
                        case "You cannot leave your own carrier":
                            res.onResponse("OWN_CARRIER");
                            break;
                        case "Left carrier":
                            res.onResponse("LEFT");
                            break;
                        default:
                            res.onResponse(response);
                            break;
                    }
                },
                error -> res.onResponse("ERROR"));
        queue.add(request);
    }

    public void carrier_signal(Response.Listener<Double> res) {
        StringRequest request = new StringRequest(
                Request.Method.GET,
                _API_PLAYER_ACTION_CARRIERSIGNAL(uuid),
                response -> {
                    switch(response) {
                        case "Invalid player":
                        case "Offline player":
                            res.onResponse(-1d);
                            break;
                        default:
                            res.onResponse(Double.parseDouble(response));
                            break;
                    }
                },
                error -> res.onResponse(-1d));
        queue.add(request);
    }

    public void bizum_send(String to, double amount, Response.Listener<String> res) {
        StringRequest request = new StringRequest(
                Request.Method.GET,
                _API_PLAYER_ACTION_BIZUMSEND(uuid, token, to, String.valueOf(amount)),
                response -> {
                    switch(response) {
                        case "Invalid player":
                            res.onResponse("UNK_PLAYER");
                            break;
                        case "Offline player":
                            res.onResponse("OFF_PLAYER");
                            break;
                        case "Invalid token":
                            res.onResponse("INVALID");
                            break;
                        case "Invalid amount":
                            res.onResponse("UNK_AMOUNT");
                            break;
                        case "Insufficient funds":
                            res.onResponse("NO_FUNDS");
                            break;
                        case "Sent":
                            res.onResponse("SENT");
                            break;
                        default:
                            res.onResponse(response);
                            break;
                    }
                },
                error -> res.onResponse("ERROR"));
        queue.add(request);
    }
}