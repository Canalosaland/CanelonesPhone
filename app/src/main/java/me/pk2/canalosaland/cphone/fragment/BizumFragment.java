package me.pk2.canalosaland.cphone.fragment;

import static me.pk2.canalosaland.cphone.log.LoggerUtil.*;
import static me.pk2.canalosaland.cphone.api.CanelonesAPI.*;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicReference;

import me.pk2.canalosaland.cphone.MainActivity;
import me.pk2.canalosaland.cphone.PanelActivity;
import me.pk2.canalosaland.cphone.R;
import me.pk2.canalosaland.cphone.api.models.PlayerModel;
import me.pk2.canalosaland.cphone.api.models.PlayerModelAuthenticated;
import me.pk2.canalosaland.cphone.api.session.APISession;

public class BizumFragment extends Fragment implements AdapterView.OnItemSelectedListener {
    private RequestQueue queue;

    public BizumFragment() {
        // Required empty public constructor
        queue = Volley.newRequestQueue(MainActivity.INSTANCE);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TextView balView = view.findViewById(R.id.textViewMoney);
        Spinner userSpinner = view.findViewById(R.id.spinnerUsers);
        EditText bizumMoney = view.findViewById(R.id.editTextBizumMoney);
        Button buttonBizum = view.findViewById(R.id.buttonBizum);
        userSpinner.setOnItemSelectedListener(this);

        ArrayAdapter<CharSequence> usAdapter = ArrayAdapter.createFromResource(view.getContext(), R.array.spinner_layout, android.R.layout.simple_spinner_item);
        usAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        userSpinner.setAdapter(usAdapter);

        PlayerModelAuthenticated player = APISession.playerModel;
        player.getPlayerInfo(queue, pInfo -> {
            if(!pInfo.valid) {
                _ALERT(view.getContext(), R.string.response_auth_bad_uuid);
                return;
            }

            balView.setText(String.format(Locale.ENGLISH, "%.2f$", pInfo.balance));
        });

        AtomicReference<List<PlayerModel>> users = new AtomicReference<>(null);
        getOnlinePlayers(queue, players -> {
            users.set(players);

            String[] pnames = new String[players.size()];
            for(int i = 0; i < players.size(); i++)
                pnames[i] = players.get(i).name;

            userSpinner.setAdapter(new ArrayAdapter<>(view.getContext(), android.R.layout.simple_spinner_dropdown_item, pnames));
            bizumMoney.setEnabled(true);
            buttonBizum.setEnabled(true);
        });

        buttonBizum.setOnClickListener(v -> {
            bizumMoney.setEnabled(false);
            buttonBizum.setEnabled(false);

            double money = Double.parseDouble(bizumMoney.getText().toString());

            player.bizum_send(
                    (String)userSpinner.getSelectedItem(),
                    money,
                    res -> {
                        switch (res) {
                            case "UNK_PLAYER":
                            case "OFF_PLAYER":
                            case "INVALID":
                                _ALERT(view.getContext(), "El jugador seleccionado está desconectado, tu sesión ha caducado o estás desconectado.");
                                break;
                            case "UNK_AMOUNT":
                                _ALERT(view.getContext(), "La cantidad introducida no es correcta. Tiene que ser mayor a 0.");
                                break;
                            case "NO_FUNDS":
                                _ALERT(view.getContext(), "No tienes fondos suficientes para realizar esta operación.");
                                break;
                            case "SENT": {
                                player.getPlayerInfo(queue, pInfo -> {
                                    if(!pInfo.valid) {
                                        _ALERT(view.getContext(), R.string.response_auth_bad_uuid);
                                        return;
                                    }

                                    String text = String.format(Locale.ENGLISH, "%.2f$", pInfo.balance);
                                    balView.setText(text);

                                    NavigationView navigationView = PanelActivity.INSTANCE.findViewById(R.id.navigationView);
                                    View headerView = navigationView.getHeaderView(0);
                                    TextView navMoney = headerView.findViewById(R.id.textViewNavMoney);

                                    navMoney.setText(text);
                                });

                                _ALERT(view.getContext(), "Bizum enviado!");
                                break;
                            }
                            case "ERROR":
                            default:
                                _ALERT(view.getContext(), "El servidor ha devuelto un estado desconocido. Contacte con el administrador.");
                                break;
                        }

                        bizumMoney.setEnabled(true);
                        buttonBizum.setEnabled(true);
                    });
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_bizum, container, false);
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}