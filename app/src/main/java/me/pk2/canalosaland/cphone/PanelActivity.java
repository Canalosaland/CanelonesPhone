package me.pk2.canalosaland.cphone;

import static me.pk2.canalosaland.cphone.log.LoggerUtil.*;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.navigation.NavigationView;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;
import java.util.concurrent.atomic.AtomicReference;

import me.pk2.canalosaland.cphone.api.CanelonesAPI;
import me.pk2.canalosaland.cphone.api.models.PlayerModelAuthenticated;
import me.pk2.canalosaland.cphone.api.session.APISession;
import me.pk2.canalosaland.cphone.fragment.AccountFragment;

public class PanelActivity extends AppCompatActivity {
    public static PanelActivity INSTANCE;

    public PanelActivity() {
        super();

        INSTANCE = this;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        INSTANCE = this;

        setContentView(R.layout.panelactivity_layout);

        DrawerLayout drawerLayout = findViewById(R.id.drawerLayout);
        findViewById(R.id.imageMenu).setOnClickListener(view -> drawerLayout.openDrawer(GravityCompat.START));

        NavigationView navigationView = findViewById(R.id.navigationView);
        navigationView.setItemIconTintList(null);

        NavController navController = Navigation.findNavController(this, R.id.navHostFragment);
        NavigationUI.setupWithNavController(navigationView, navController);

        TextView textTitle = findViewById(R.id.textTitle);
        navController.addOnDestinationChangedListener((controller, destination, bundle) -> {
            textTitle.setText("Canelones Phone - " + destination.getLabel());
        });

        View headerView = navigationView.getHeaderView(0);

        TextView navUser = headerView.findViewById(R.id.textViewNavUser);
        TextView navMoney = headerView.findViewById(R.id.textViewNavMoney);
        //TextView mainUser = headerView.findViewById(R.id.textViewUser);
        RoundedImageView imageProfile = headerView.findViewById(R.id.imageProfile);
        //RoundedImageView imageProfileMain = headerView.findViewById(R.id.imageProfileMain);

        Fragment fragment = AccountFragment.INSTANCE;
        FragmentActivity fragmentActivity = fragment.getActivity();

        RequestQueue queue = Volley.newRequestQueue(this);

        PlayerModelAuthenticated player = APISession.playerModel;
        player.getPlayerInfo(queue, pInfo -> {
            if(!pInfo.valid) {
                _ALERT(this, R.string.response_auth_bad_uuid);
                return;
            }

            navUser.setText(player.name);
            navMoney.setText(String.format(Locale.ENGLISH, "%.2f$", pInfo.balance));
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
                            .resize(70, 70)
                            .centerCrop()
                            .into(imageProfile);
                }, error -> Picasso.get()
                .load("https://crafatar.com/avatars/"+uuid.get())
                .resize(70, 70)
                .centerCrop()
                .into(imageProfile));
        queue.add(uuidRequest);
    }
}