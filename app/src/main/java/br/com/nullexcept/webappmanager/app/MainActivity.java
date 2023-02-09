package br.com.nullexcept.webappmanager.app;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ShortcutInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.pm.ShortcutInfoCompat;
import androidx.core.content.pm.ShortcutManagerCompat;
import androidx.core.graphics.drawable.IconCompat;

import org.mozilla.geckoview.GeckoSession;

import java.io.File;
import java.util.HashMap;

import br.com.nullexcept.webappmanager.R;
import br.com.nullexcept.webappmanager.config.Config;
import br.com.nullexcept.webappmanager.dialog.EditWebAppDialog;
import br.com.nullexcept.webappmanager.instances.*;

public class MainActivity extends AppCompatActivity {
    public static final Class[] LIST = new Class[]{
            ZI001.class,
            ZI002.class,
            ZI003.class,
            ZI004.class,
            ZI005.class,
            ZI006.class,
            ZI007.class
    };

    public static HashMap<String, Config> WEBAPPS = new HashMap<>();
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        checkAndCreateDatabase();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{
                    Manifest.permission.INSTALL_SHORTCUT,
                    Manifest.permission.INTERNET
            },10);
        }
        if (savedInstanceState == null){
            loadList();
            findViewById(R.id.create).setOnClickListener((vw)->{
                String key = null;
                for (String ky:WEBAPPS.keySet()){
                    if(!WEBAPPS.get(ky).enable){
                        key = ky;
                        break;
                    }
                }
                showDialog(new Config(key));
            });
        }

    }

    private void showDialog(Config config){
        new EditWebAppDialog(config, this).getDialog().show();
    }

    private void loadImage(Config config) {
    }


    private void loadList() {
        for(Config config: WEBAPPS.values()){
            if(config.enable){
                makeItem(config);
            }
        }
    }

    private Class loadClass(String path){
        try {
            return getClassLoader().loadClass(path);
        }catch (Exception e){}
        return null;
    }

    private void makeItem(Config config) {
        ViewGroup layout =(ViewGroup) getLayoutInflater().inflate(R.layout.item_layout, null, false);
        ((TextView)layout.findViewById(R.id.name)).setText(config.name);
        ((TextView)layout.findViewById(R.id.desc)).setText(config.url);

        ((TextView)layout.findViewById(R.id.instance)).setText(config.getBase().substring(config.getBase().length()-5));

        Intent intent= new Intent(this, loadClass(config.getBase()));;
        intent.setAction(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("app://"+config.getBase()));

        layout.findViewById(R.id.click).setOnClickListener((v)-> startActivity(intent));
        layout.findViewById(R.id.home_screen).setOnClickListener(v -> createShortcut(config, intent));
        layout.findViewById(R.id.settings).setOnClickListener(v -> {
            showDialog(config);
        });
        layout.setTag(config.getBase());
        ((LinearLayout)findViewById(R.id.list)).addView(layout);
    }

    private void createShortcut(Config config, Intent intent) {
        ShortcutInfoCompat.Builder link = new ShortcutInfoCompat.Builder(this, config.getBase()+"/"+config.url);
        link.setIntent(intent);
        link.setLongLabel(config.name);
        link.setShortLabel(config.name);
        File fIcon = new File(getFilesDir(), "instances/"+config.getBase()+"/icon.png");
        link.setIcon(IconCompat.createWithResource(this, R.drawable.ic_launcher_background));
        if (fIcon.exists()){
            try {
                Bitmap bit = BitmapFactory.decodeFile(fIcon.getAbsolutePath());
                if (bit.getWidth() > 0 && bit.getHeight() > 0){
                    link.setIcon(IconCompat.createWithAdaptiveBitmap(bit));
                }
            }catch (Exception e){}
        }
        ShortcutManagerCompat.requestPinShortcut(this, link.build(), null);
        Toast.makeText(this, R.string.make_shortcut, Toast.LENGTH_SHORT).show();
    }

    private void checkAndCreateDatabase() {
        for (Class clazz: LIST){
            SharedPreferences prefs = getSharedPreferences("webapps", Context.MODE_PRIVATE);
            Config config = new Config(clazz.getName());
            config.load(prefs);
            WEBAPPS.put(clazz.getName(), config);
            config.save(prefs);
        }
    }

    public void refreshList() {
        LinearLayout list = findViewById(R.id.list);
        for (Class clazz:LIST){
            String id = clazz.getName();
            Config config = WEBAPPS.get(id);
            if (config.enable && list.findViewWithTag(id) == null){
                makeItem(config);
            } else if ((!config.enable) && list.findViewWithTag(id) != null){
                View item = list.findViewWithTag(id);
                list.removeView(item);
            }
            if (config.enable){
                ViewGroup item = list.findViewWithTag(id);
                ((TextView)item.findViewById(R.id.name)).setText(config.name);
                ((TextView)item.findViewById(R.id.desc)).setText(config.url);
            }
        }
    }
}
