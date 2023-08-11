package me.pk2.canalosaland.cphone.fragment;

import static me.pk2.canalosaland.cphone.api.CanelonesAPI.*;
import static me.pk2.canalosaland.cphone.log.LoggerUtil.*;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import java.util.Objects;

import me.pk2.canalosaland.cphone.MainActivity;
import me.pk2.canalosaland.cphone.R;
import me.pk2.canalosaland.cphone.api.models.CarrierModel;
import me.pk2.canalosaland.cphone.api.models.PlayerModelAuthenticated;
import me.pk2.canalosaland.cphone.api.session.APISession;

public class InternetFragment extends Fragment {
    RequestQueue queue;

    public InternetFragment() {
        queue = Volley.newRequestQueue(MainActivity.INSTANCE);
    }

    private void updateData(View view) {
        TextView textViewProvider = view.findViewById(R.id.textViewProvider);
        TextView textViewProviderOwner = view.findViewById(R.id.textViewProviderOwner);

        Spinner spinner = view.findViewById(R.id.spinnerProvider);

        Button buttonUnSubscribe = view.findViewById(R.id.buttonProviderUnsubscribe);
        Button buttonSubscribe = view.findViewById(R.id.buttonProviderSubscribe);

        getCarriers(queue, carriers -> {
            String[] array = new String[carriers.size()];
            for(int i = 0; i < array.length; i++)
                array[i] = carriers.get(i).name + " " + carriers.get(i).pricePerText + "$";

            if(array.length == 0)
                array = new String[]{"No existen proveedores"};

            spinner.setAdapter(new ArrayAdapter<String>(
                    view.getContext(),
                    android.R.layout.simple_spinner_dropdown_item,
                    array));
        });

        PlayerModelAuthenticated player = APISession.playerModel;
        player.getPlayerInfo(queue, pInfo -> {
            if(!pInfo.valid) {
                _ALERT(view.getContext(), R.string.response_auth_bad_uuid);
                return;
            }

            textViewProvider.setText(pInfo.carrier.contentEquals("null")?
                    getResources().getString(R.string.wifi_layout_mini):
                    pInfo.carrier);
            textViewProviderOwner.setText(pInfo.isCarrierOwner?
                    R.string.wifi_transmitter_mini:
                    R.string.wifi_no_transmitter_mini);

            textViewProviderOwner.setTextColor(pInfo.isCarrierOwner?
                    getResources().getColor(R.color.blue30):
                    getResources().getColor(R.color.darkblue30));

            buttonUnSubscribe.setEnabled(!pInfo.carrier.contentEquals("null") && !pInfo.isCarrierOwner);
            buttonSubscribe.setEnabled(pInfo.carrier.contentEquals("null"));
        });
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        queue = Volley.newRequestQueue(view.getContext());

        TextView textViewProvider = view.findViewById(R.id.textViewProvider);
        TextView textViewProviderOwner = view.findViewById(R.id.textViewProviderOwner);

        Spinner spinner = view.findViewById(R.id.spinnerProvider);

        Button buttonUnSubscribe = view.findViewById(R.id.buttonProviderUnsubscribe);
        Button buttonSubscribe = view.findViewById(R.id.buttonProviderSubscribe);

        PlayerModelAuthenticated player = APISession.playerModel;

        updateData(view);
        buttonUnSubscribe.setOnClickListener(v -> {
            buttonSubscribe.setEnabled(false);
            buttonUnSubscribe.setEnabled(false);

            player.carrier_leave(res -> {
                switch(res) {
                    case "UNK_PLAYER":
                    case "OFF_PLAYER":
                        _ALERT(v.getContext(), R.string.response_auth_bad_uuid);
                        break;
                    case "INVALID":
                        _ALERT(v.getContext(), "Sesión expirada, por favor vuelva a iniciar sesión.");
                        break;
                    case "NOT_CARRIER":
                        _ALERT(v.getContext(), "No estas en ningún proveedor.");
                        break;
                    case "OWN_CARRIER":
                        _ALERT(v.getContext(), "No te puedes dar de baja de tu propio proveedor.");
                        break;
                    case "LEFT":
                        _ALERT(v.getContext(), "Te has dado de baja!");
                        break;
                    default:
                        _ALERT(v.getContext(), "Respuesta desconocida del servidor, por favor, contacta al desarrollador.");
                        break;
                }

                updateData(view);
            });
        });

        buttonSubscribe.setOnClickListener(v -> {
            buttonSubscribe.setEnabled(false);
            buttonUnSubscribe.setEnabled(false);

            String carrier = spinner.getSelectedItem().toString().split(" ")[0];
            player.carrier_join(carrier, res -> {
                switch(res) {
                    case "UNK_PLAYER":
                    case "OFF_PLAYER":
                        _ALERT(v.getContext(), R.string.response_auth_bad_uuid);
                        break;
                    case "INVALID":
                        _ALERT(v.getContext(), "Sesión expirada, por favor vuelva a iniciar sesión.");
                        break;
                    case "UNK_CARRIER":
                        _ALERT(v.getContext(), "El proveedor que ha seleccionado no existe! Por favor, compruebe si existe.");
                        break;
                    case "JOINED":
                        _ALERT(v.getContext(), "Te has unido al proveedor!");
                        break;
                    default:
                        _ALERT(v.getContext(), "Respuesta desconocida del servidor, por favor, contacta al desarrollador.");
                        break;
                }

                updateData(view);
            });
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_internet, container, false);
    }
}