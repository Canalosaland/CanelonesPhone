package me.pk2.canalosaland.cphone.fragment;

import static me.pk2.canalosaland.cphone.api.CanelonesAPI.*;
import static me.pk2.canalosaland.cphone.log.LoggerUtil._ALERT;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;
import java.util.concurrent.atomic.AtomicReference;

import me.pk2.canalosaland.cphone.MainActivity;
import me.pk2.canalosaland.cphone.R;
import me.pk2.canalosaland.cphone.api.models.PlayerModelAuthenticated;
import me.pk2.canalosaland.cphone.api.session.APISession;


public class AccountFragment extends Fragment {
    public static AccountFragment INSTANCE;
    private RequestQueue queue;

    public AccountFragment() {
        // Required empty public constructor
        INSTANCE = this;
        queue = Volley.newRequestQueue(MainActivity.INSTANCE);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Thread.setDefaultUncaughtExceptionHandler((t,throwable) -> throwable.printStackTrace());

        TextView mainUser = view.findViewById(R.id.textViewUser);
        RoundedImageView imageProfileMain = view.findViewById(R.id.imageProfileMain);

        PlayerModelAuthenticated player = APISession.playerModel;
        mainUser.setText(player.name);


        player.getPlayerInfo(queue, pInfo -> {
            TextView textViewMoney = view.findViewById(R.id.textViewMoney);
            TextView textViewInternet = view.findViewById(R.id.textViewInternet);
            TextView textViewOwner = view.findViewById(R.id.textViewOwner);
            ImageView imageViewOwner = view.findViewById(R.id.imageViewOwner);

            if(pInfo == null || !pInfo.valid) {
                _ALERT(getActivity(), "El jugador está fuera de línea o la sesión ha expirado, no se actualizará nada.");
                return;
            }

            textViewMoney.setText(String.format(Locale.ENGLISH, "%.2f$", pInfo.balance));
            textViewInternet.setText(pInfo.carrier.contentEquals("null")?"Sin compañía telefónica":pInfo.carrier);
            textViewOwner.setText(pInfo.isCarrierOwner?
                    R.string.wifi_transmitter:
                    R.string.wifi_no_transmitter);

            textViewOwner.setTextColor(pInfo.isCarrierOwner?
                    getResources().getColor(R.color.blue30):
                    getResources().getColor(R.color.darkblue30));

            imageViewOwner.setImageResource(pInfo.isCarrierOwner?
                    R.drawable.ic_transmitter:
                    R.drawable.ic_not_transmitter);
        });

        AtomicReference<String> uuid = new AtomicReference<>("8667ba71b85a4004af54457a9734eed7");
        StringRequest uuidRequest = new StringRequest(
                Request.Method.GET,
                "https://api.mojang.com/users/profiles/minecraft/"+player.name,
                response -> {
                    tryThing: {
                        try {
                            JSONObject json = new JSONObject(response);
                            if(json.has("errorMessage"))
                                break tryThing;

                            uuid.set(json.getString("id"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    Picasso.get()
                            .load("https://crafatar.com/avatars/"+uuid.get())
                            .resize(150, 150)
                            .centerCrop()
                            .into(imageProfileMain);
                }, error -> Picasso.get()
                .load("https://crafatar.com/avatars/"+uuid.get())
                .resize(150, 150)
                .centerCrop()
                .into(imageProfileMain));
        player.queue.add(uuidRequest);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_account, container, false);
    }
}