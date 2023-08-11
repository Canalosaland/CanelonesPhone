package me.pk2.canalosaland.cphone;

import static me.pk2.canalosaland.cphone.log.LoggerUtil.*;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;

import static me.pk2.canalosaland.cphone.api.CanelonesAPI.*;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import java.net.URLEncoder;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import me.pk2.canalosaland.cphone.api.models.PlayerModel;
import me.pk2.canalosaland.cphone.api.models.PlayerModelAuthenticated;
import me.pk2.canalosaland.cphone.api.session.APISession;

public class MainActivity extends AppCompatActivity {
    public static MainActivity INSTANCE;

    public MainActivity() {
        super();

        Thread.setDefaultUncaughtExceptionHandler((t,throwable) -> Log.e("aaa", "Error", throwable));
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        INSTANCE = this;
        initAPI(this);

        Thread.setDefaultUncaughtExceptionHandler((t,throwable) -> Log.e("aaa", "Error", throwable));

        setContentView(R.layout.mainactivity_layout);

        _LOG_FILE_CLEAR();

        RequestQueue queue = Volley.newRequestQueue(this);

        Button button = findViewById(R.id.buttonLogin);
        button.setOnClickListener((x) -> {
            button.setEnabled(false);
            String username = ((EditText)findViewById(R.id.editTextUsername)).getText().toString();
            String password = ((EditText)findViewById(R.id.editTextPassword)).getText().toString();
            String usernameEnc = URLEncoder.encode(username);
            String passwordEnc = URLEncoder.encode(password);

            getOnlinePlayers(queue, response -> {
                /*if(response.size() < 1) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setMessage(R.string.response_auth_bad_error);
                    builder.setPositiveButton(R.string.button_click_accept, (dialog, which) -> {});
                    builder.create().show();
                }*/

                AtomicBoolean found = new AtomicBoolean(false);
                AtomicReference<PlayerModel> p = new AtomicReference<>(null);
                response.forEach(v -> {
                    if(v.name.contentEquals(username)) {
                        found.set(true);
                        p.set(v);
                    }
                });

                if(!found.get()) {
                    _ALERT(this, R.string.response_auth_bad_uuid);
                    button.setEnabled(true);
                    return;
                }

                APISession.playerModel = new PlayerModelAuthenticated(p.get().uuid, p.get().name);
                APISession.playerModel.session_auth(password, auth -> {
                    switch (auth) {
                        case "UNK_PLAYER":
                        case "OFF_PLAYER":
                            _ALERT(this, R.string.response_auth_bad_uuid);
                            break;
                        case "WRONG_PASS":
                            _ALERT(this, R.string.response_auth_bad_credentials);
                            break;
                        default: {
                            if(auth.length() != 128) {
                                _ALERT(this, R.string.response_auth_bad_unknown);
                                break;
                            }

                            _ALERT(this, R.string.response_auth_ok);

                            Thread.setDefaultUncaughtExceptionHandler((t,throwable) -> Log.e("aaa", "Error", throwable));
                            Intent intent = new Intent(this, PanelActivity.class);
                            startActivity(intent);
                        } break;
                    }

                    button.setEnabled(true);
                });
            });
        });
    }
}