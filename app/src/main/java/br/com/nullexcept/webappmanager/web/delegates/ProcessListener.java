package br.com.nullexcept.webappmanager.web.delegates;

import android.view.View;

import androidx.annotation.NonNull;

import org.mozilla.geckoview.GeckoSession;

import br.com.nullexcept.webappmanager.R;
import br.com.nullexcept.webappmanager.web.WebSession;

import br.com.nullexcept.webappmanager.widget.ProgressBar;
public class ProcessListener implements GeckoSession.ProgressDelegate {
    private WebSession session;
    public ProcessListener(WebSession session){
        this.session = session;
    }

    @Override
    public void onProgressChange(@NonNull GeckoSession current, int progress) {
        GeckoSession.ProgressDelegate.super.onProgressChange(current, progress);
        session.context().runOnUiThread(()->{
            ((ProgressBar)session.context().findViewById(R.id.loading_bar)).setProgress(progress);
        });
    }

    @Override
    public void onPageStop(@NonNull GeckoSession current, boolean success) {
        GeckoSession.ProgressDelegate.super.onPageStop(current, success);
        session.context().runOnUiThread(()->{
            ((ProgressBar)session.context().findViewById(R.id.loading_bar)).setVisibility(View.INVISIBLE);
        });
    }

    @Override
    public void onPageStart(@NonNull GeckoSession current, @NonNull String url) {
        GeckoSession.ProgressDelegate.super.onPageStart(current, url);
        session.context().runOnUiThread(()->{
            ((ProgressBar)session.context().findViewById(R.id.loading_bar)).setVisibility(View.VISIBLE);
            ((ProgressBar)session.context().findViewById(R.id.loading_bar)).setProgress(0);
        });
    }
}
