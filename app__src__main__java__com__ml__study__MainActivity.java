package com.ml.study;

import android.app.Activity;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 * 唯一界面：铺满全屏的 WebView，加载打包进 assets 的单文件学习应用。
 *
 * 设计原则（故意的）：
 *  - 零权限、零网络：assets/index.html 是自包含单文件，所有样式/脚本/数据都在里面；
 *  - 不介入 HTML 的交互逻辑，全部交给前端（返回键除外）；
 *  - 明暗主题由 HTML 决定，再通过 JavascriptInterface 回调原生状态栏颜色，
 *    这样切换主题时上下两条状态栏也跟着变，不会出现「白色页面 + 黑色状态栏」。
 */
public class MainActivity extends Activity {

    private static final String PAGE = "file:///android_asset/index.html";

    private WebView web;

    /** HTML 通过 window.AndroidHost.applyChrome('#RRGGBB', lightIcons) 调用 */
    public final class HostBridge {
        @JavascriptInterface
        public void applyChrome(final String hex, final boolean lightIcons) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    int c;
                    try {
                        c = Color.parseColor(hex);
                    } catch (Throwable t) {
                        return;
                    }
                    applyChromeColor(c, lightIcons);
                }
            });
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        applyWindowStyle();

        web = new WebView(this);
        web.setBackgroundColor(0xFFF7F8FA);

        WebSettings s = web.getSettings();
        s.setJavaScriptEnabled(true);            // 全部交互靠 JS
        s.setDomStorageEnabled(true);            // localStorage：记住主题与阅读进度
        s.setDatabaseEnabled(true);
        s.setAllowFileAccess(true);              // 从 file:// 读 assets
        s.setAllowContentAccess(false);
        s.setSupportZoom(false);                 // 缩放交给页面内的字号按钮，避免手势冲突
        s.setBuiltInZoomControls(false);
        s.setDisplayZoomControls(false);
        s.setTextZoom(100);                      // 不跟随系统字号，否则公式排版会乱
        s.setLoadWithOverviewMode(false);
        s.setUseWideViewPort(true);              // 让 viewport meta 生效，等同手机浏览器
        s.setCacheMode(WebSettings.LOAD_NO_CACHE);
        if (Build.VERSION.SDK_INT >= 21) {
            s.setMixedContentMode(WebSettings.MIXED_CONTENT_NEVER_ALLOW);
        }

        web.setWebViewClient(new WebViewClient());
        web.setWebChromeClient(new WebChromeClient());
        web.setOverScrollMode(View.OVER_SCROLL_NEVER);
        web.addJavascriptInterface(new HostBridge(), "AndroidHost");

        setContentView(web);
        web.loadUrl(PAGE);
    }

    /** 启动时的窗口样式：背景跟随 values / values-night 的资源 */
    private void applyWindowStyle() {
        Window w = getWindow();
        int bg = getResources().getColor(R.color.bg_launch);
        boolean lightIcons = isNightMode();
        applyChromeColor(bg, !lightIcons);
    }

    private boolean isNightMode() {
        int mask = getResources().getConfiguration().uiMode
                & android.content.res.Configuration.UI_MODE_NIGHT_MASK;
        return mask == android.content.res.Configuration.UI_MODE_NIGHT_YES;
    }

    /** lightIcons=true 表示状态栏图标用深色（浅色背景时） */
    private void applyChromeColor(int color, boolean lightIcons) {
        Window w = getWindow();
        if (Build.VERSION.SDK_INT >= 21) {
            w.setStatusBarColor(color);
            w.setNavigationBarColor(color);
        }
        View decor = w.getDecorView();
        int flags = decor.getSystemUiVisibility();
        int LIGHT = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
        if (Build.VERSION.SDK_INT >= 26) {
            LIGHT |= View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR;
        }
        if (lightIcons) {
            flags |= LIGHT;
        } else {
            flags &= ~LIGHT;
        }
        decor.setSystemUiVisibility(flags);
    }

    /** 返回键：页面内能后退就后退，否则交回系统（退出） */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && web != null && web.canGoBack()) {
            web.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        if (web != null) {
            web.loadUrl("about:blank");
            web.removeAllViews();
            web.destroy();
            web = null;
        }
        super.onDestroy();
    }
}
