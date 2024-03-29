package br.com.nullexcept.webappmanager.dialog;

import static android.content.Context.MODE_PRIVATE;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.FileUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import org.mozilla.geckoview.GeckoSession;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

import br.com.nullexcept.webappmanager.R;
import br.com.nullexcept.webappmanager.app.MainActivity;
import br.com.nullexcept.webappmanager.config.Config;

public class EditWebAppDialog {
    private MainActivity ctx;
    private final ViewGroup layout;
    private final EditText name;
    private final EditText user_agent;
    private final EditText url;

    private final CheckBox allow_redirects;
    private final CheckBox show_action;
    private final AlertDialog dialog;
    private final Config config;

    @SuppressLint("ClickableViewAccessibility")
    public EditWebAppDialog(Config config, MainActivity ctx){
        this.ctx = ctx;
        this.config = config;
        AlertDialog.Builder builder = new AlertDialog.Builder(ctx, R.style.CardDialog);

        layout = (ViewGroup) ctx.getLayoutInflater().inflate(R.layout.edit_dialog, null, false);
        name = layout.findViewById(R.id.name);
        user_agent = layout.findViewById(R.id.user_agent);
        url = layout.findViewById(R.id.url);

        allow_redirects = layout.findViewById(R.id.allow_redirects);
        show_action = layout.findViewById(R.id.show_action);
        builder.setView(layout);
        dialog = builder.create();

        name.setText(config.name);
        user_agent.setText(config.user_agent);
        url.setText(config.url);
        allow_redirects.setChecked(config.redirects);
        show_action.setChecked(config.action_bar);

        name.setOnKeyListener(this::keyUpdate);
        url.setOnKeyListener(this::keyUpdate);
        user_agent.setOnKeyListener(this::keyUpdate);

        allow_redirects.setOnClickListener(this::clickUpdate);
        show_action.setOnClickListener(this::clickUpdate);
        layout.setOnClickListener(this::clickUpdate);
        name.setOnClickListener(this::clickUpdate);
        url.setOnClickListener(this::clickUpdate);
        user_agent.setOnClickListener(this::clickUpdate);

        allow_redirects.setOnTouchListener(this::touchUpdate);
        show_action.setOnTouchListener(this::touchUpdate);
        layout.setOnTouchListener(this::touchUpdate);
        name.setOnTouchListener(this::touchUpdate);
        url.setOnTouchListener(this::touchUpdate);
        user_agent.setOnTouchListener(this::touchUpdate);

        if (!config.enable){
            addStorage("PRIVATE (SAFE)", ctx.getFilesDir());
            File[] files = ctx.getExternalFilesDirs(null);
            for (File storage :  files){
                String[] path = storage.getAbsolutePath().split("/");
                if (!Objects.equals(path[1], "storage"))continue;
                String name = path[2].toUpperCase();
                addStorage(name, storage);
            }
        } else {
            layout.findViewById(R.id.create_options).setVisibility(View.INVISIBLE);
        }


        if (config.enable){
            layout.findViewById(R.id.delete).setVisibility(View.VISIBLE);
            layout.findViewById(R.id.delete).setOnClickListener(v -> {
                clearAllData();
                dialog.dismiss();
                config.enable = false;
                config.save();
                ctx.refreshList();
            });
        } else {
            layout.findViewById(R.id.delete).setVisibility(View.INVISIBLE);
        }

        layout.findViewById(R.id.cancel).setOnClickListener(v -> {
            dialog.dismiss();
        });
        layout.findViewById(R.id.save).setOnClickListener(v -> save());
        refreshStorageList();
    }


    private final ArrayList<Object[]> storages = new ArrayList<>();
    private void addStorage(String name, File directory){
        ctx.runOnUiThread(()->{
            LinearLayout storage_list = layout.findViewById(R.id.storage_list);
            LinearLayout item = (LinearLayout) ctx.getLayoutInflater().inflate(R.layout.node_item_storage_small, storage_list, false);

            View.OnClickListener click = (view)->{
              config.data_directory = directory.getAbsoluteFile();
              refreshStorageList();
            };

            item.setOnClickListener(click);
            ((CheckBox)item.findViewById(R.id.checkbox)).setOnClickListener(click);

            ((TextView)item.findViewById(R.id.name)).setText(name);
            storage_list.addView(item);
            storages.add(new Object[]{
                    item,
                    directory.getAbsoluteFile()
            });
        });
    }

    private void refreshStorageList(){
        ctx.runOnUiThread(()->{
            for (Object[] items : storages){
                View view = (View) items[0];
                File file = (File) items[1];
                ((CheckBox)view.findViewById(R.id.checkbox))
                        .setChecked(file.getAbsoluteFile().equals(config.data_directory));
            }
        });
    }

    private void save() {
        checkAndContinue();
        if (layout.findViewById(R.id.save).getAlpha() < 1.0)return;
        dialog.dismiss();
        config.name = name.getText()+"";
        config.url = url.getText()+"";
        config.user_agent = user_agent.getText()+"";
        config.action_bar = show_action.isChecked();
        config.redirects  = allow_redirects.isChecked();
        if (!config.enable){
            clearAllData();
            config.enable = true;
        }
        MainActivity.WEBAPPS.put(config.getBase(), config);
        config.save();
        ctx.refreshList();
    }

    private void clearAllData() {
        config.deleteAll();
    }


    private boolean touchUpdate(View vw, MotionEvent motion){
        checkAndContinue();
        return false;
    }

    private void clickUpdate(View vw){
        checkAndContinue();
    }


    private boolean keyUpdate(View vw, int key, KeyEvent event){
        checkAndContinue();
        return false;
    }

    private void checkAndContinue() {
        if (!name.isFocused()){
            String _name = (name.getText()+"").replaceAll(" ", "");
            if (_name.length() == 0){
                _name = "WebApp "+config.getBase().substring(config.getBase().length()-5);
                name.setText(_name);
            }
        }
        if (!user_agent.isFocused()){
            String _userAgent = (user_agent.getText()+"").replaceAll(" ", "");
            if (_userAgent.length() == 0){
                user_agent.setText(GeckoSession.getDefaultUserAgent());
            }
        }
        if (!url.isFocused()){
            String _url = (url.getText()+"").replaceAll(" ", "");

            if (_url.length() > 0){
                if (!(_url.startsWith("http://") || _url.startsWith("https://"))){
                    _url = "https://"+_url;
                }
            }

            url.setText(_url);
        }
        boolean valid = url.getText().length() > 0 && user_agent.getText().length() > 0 && name.getText().length() > 0;
        layout.findViewById(R.id.save).setAlpha(valid ? 1.0f : 0.5f);
    }

    public AlertDialog getDialog() {
        return dialog;
    }
}
