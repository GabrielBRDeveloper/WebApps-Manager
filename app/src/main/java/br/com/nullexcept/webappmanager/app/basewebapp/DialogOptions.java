package br.com.nullexcept.webappmanager.app.basewebapp;

import android.view.View;

import androidx.appcompat.app.AlertDialog;

import br.com.nullexcept.webappmanager.R;
import br.com.nullexcept.webappmanager.app.BaseWebAppActivity;

public class DialogOptions {
    private AlertDialog dialog;
    public DialogOptions(BaseWebAppActivity ctx){
        ctx.runOnUiThread(()->{
            AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
            View view = ctx.getLayoutInflater().inflate(R.layout.dialog_actions,null, false);

            view.findViewById(R.id.opt_addons_store).setOnClickListener(v -> {
                dialog.dismiss();
                ctx.loadUrl("https://addons.mozilla.org/pt-BR/android/");
            });
            view.findViewById(R.id.opt_gecko_settings).setOnClickListener(v -> {
                dialog.dismiss();
                ctx.loadUrl("about:preferences");
            });
            builder.setView(view);
            dialog = builder.create();
        });
    }

    public AlertDialog getDialog() {
        return dialog;
    }
}
