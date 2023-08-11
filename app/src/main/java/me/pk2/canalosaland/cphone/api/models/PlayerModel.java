package me.pk2.canalosaland.cphone.api.models;

import static me.pk2.canalosaland.cphone.api.CanelonesAPI.*;
import static me.pk2.canalosaland.cphone.log.LoggerUtil.*;

import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import me.pk2.canalosaland.cphone.PanelActivity;
import me.pk2.canalosaland.cphone.api.models.info.PlayerInfoModel;

public class PlayerModel {
    public final String uuid;
    public final String name;
    public PlayerModel(String uuid, String name) {
        this.uuid = uuid;
        this.name = name;
    }

    public void getPlayerInfo(RequestQueue queue, Response.Listener<PlayerInfoModel> res) {
        StringRequest request = new StringRequest(
                Request.Method.GET,
                _API_PLAYER(uuid),
                response -> {
                    switch(response) {
                        case "Invalid player":
                        case "Offline player":
                            res.onResponse(new PlayerInfoModel());
                            break;
                        default: {
                            if (!response.contains(uuid) || !response.contains("\n")) {
                                res.onResponse(new PlayerInfoModel());
                                break;
                            }

                            String[] userSplit = response.split("\n");
                            double balance = Double.parseDouble(userSplit[2]);
                            String carrier = userSplit[3];
                            boolean isCarrierOwner = Boolean.parseBoolean(userSplit[4]);

                            PlayerInfoModel model = new PlayerInfoModel(true, balance, carrier, isCarrierOwner);
                            res.onResponse(model);
                        } break;
                    }

                    //res.onResponse(new PlayerInfoModel());
                },
                error -> res.onResponse(new PlayerInfoModel()));
        queue.add(request);
    }
}