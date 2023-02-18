package br.com.nullexcept.webappmanager.web.delegates;

import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.mozilla.geckoview.GeckoSession;

import br.com.nullexcept.webappmanager.R;
import br.com.nullexcept.webappmanager.web.WebSession;

public class ContentListener implements GeckoSession.ContentDelegate {
    private WebSession session;
    private String TITLE = "WebApp";
    public ContentListener(WebSession session){
        this.session = session;
    }

    @Override
    public void onTitleChange(@NonNull GeckoSession current, @Nullable String title) {
        GeckoSession.ContentDelegate.super.onTitleChange(current, title);
        session.context().runOnUiThread(()->{
            TITLE = title;
            ((TextView)session.context().findViewById(R.id.title)).setText(title);
        });
    }

    public String getTitle() {
        return TITLE;
    }
}
