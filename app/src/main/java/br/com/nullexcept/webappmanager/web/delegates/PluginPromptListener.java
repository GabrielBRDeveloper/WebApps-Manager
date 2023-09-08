package br.com.nullexcept.webappmanager.web.delegates;

import android.content.DialogInterface;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import org.mozilla.geckoview.AllowOrDeny;
import org.mozilla.geckoview.GeckoResult;
import org.mozilla.geckoview.WebExtension;
import org.mozilla.geckoview.WebExtensionController;

import br.com.nullexcept.webappmanager.R;
import br.com.nullexcept.webappmanager.app.BaseWebAppActivity;
import br.com.nullexcept.webappmanager.web.WebSession;

public class PluginPromptListener implements WebExtensionController.PromptDelegate {
    private WebSession session;
    public PluginPromptListener(WebSession session){
        this.session = session;
    }

    @Nullable
    @Override
    public GeckoResult<AllowOrDeny> onInstallPrompt(@NonNull WebExtension extension) {
        GeckoResult<AllowOrDeny> result =  new GeckoResult<AllowOrDeny>();

        session.log("REQUIRES INSTALL ADDON");

        session.context().runOnUiThread(()->{
            BaseWebAppActivity activity = (BaseWebAppActivity) session.context();
            activity.installExtension(extension, result);
        });

        return result;
    }
}
