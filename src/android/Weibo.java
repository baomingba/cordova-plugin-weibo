package com.qiudao.cordova.weibo;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.LOG;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.sina.weibo.sdk.auth.AuthInfo;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WeiboAuthListener;
import com.sina.weibo.sdk.auth.sso.SsoHandler;
import com.sina.weibo.sdk.exception.WeiboException;

public class Weibo extends CordovaPlugin {
    private SsoHandler ssoHandler = null;

    public static final int LOGIN_NO_ACCESS_TOKEN = 1;
    public static final int LOGIN_USER_CANCELLED = 2;
    public static final int LOGIN_ERROR = 3;
    public static final int LOGIN_NO_NETWORK = 4;
    public static final int WEIBO_NOT_INSTALLED = 5;

    private static final String TAG = "Cordova-Weibo";

    private static final String APPID_PROPERTY_KEY = "weiboappid";
    private static final String REDIRECT_URI_PROPERTY_KEY = "weiboredirecturi";

    @Override
    public boolean execute(String action, final JSONArray args,
            final CallbackContext context) throws JSONException {
        boolean result = false;
        try {
            if (action.equals("login")) {
                this.login(context);
                result = true;
            }
            else if (action.equals("isInstalled")) {
                this.isInstalled(context);
                result = true;
            }
        }
        catch (Exception e) {
            LOG.e(TAG, e.getMessage(), e);
            context.error(new ErrorMessage(LOGIN_ERROR, e.getMessage()));
        }

        return result;
    }

    protected void isInstalled(final CallbackContext context) {
        SsoHandler handler = getSsoHandler();
        context.success(handler.isWeiboAppInstalled() ? 1 : 0);
    }

    protected void login(final CallbackContext context) {
        Activity activity = this.cordova.getActivity();
        this.cordova.setActivityResultCallback(this);

        final SsoHandler handler = getSsoHandler();
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                handler.authorize(new AuthListener(context));
            }
        });
    }

    protected synchronized SsoHandler getSsoHandler() {
        if (ssoHandler == null) {
            final String appId = webView.getPreferences().getString(APPID_PROPERTY_KEY, "");
            final String redirectURI = webView.getPreferences().getString(REDIRECT_URI_PROPERTY_KEY, "");
            final String scope = "all";

            Activity activity = this.cordova.getActivity();
            AuthInfo authInfo = new AuthInfo(activity, appId, redirectURI, scope);

            ssoHandler = new SsoHandler(activity, authInfo);
        }

        return ssoHandler;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        final SsoHandler handler = getSsoHandler();
        handler.authorizeCallBack(requestCode, resultCode, data);
    }

    /**
     * Error message when any errors happen.
     */
    protected class ErrorMessage extends JSONObject {
        public ErrorMessage(int code, String message) {
            try {
                this.put("code", code);
                this.put("message", message);
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * Listener for login authentication.
     */
    protected class AuthListener implements WeiboAuthListener {
        private CallbackContext context;

        public AuthListener(CallbackContext context) {
            this.context = context;
        }

        @Override
        public void onComplete(Bundle values) {
            Oauth2AccessToken accessToken = Oauth2AccessToken.parseAccessToken(values);
            if (accessToken.isSessionValid()) {
                JSONObject res = new JSONObject();
                try {
                    res.put("uid", accessToken.getUid());
                    res.put("token", accessToken.getToken());
                    res.put("expire_at", accessToken.getExpiresTime());
                    res.put("refresh_token",accessToken.getRefreshToken());
                    context.success(res);
                }
                catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
            else {
                String code = values.getString("code");
                context.error(new ErrorMessage(LOGIN_NO_ACCESS_TOKEN, code));
            }
        }

        @Override
        public void onWeiboException(WeiboException e) {
            String message = e.getMessage();
            context.error(new ErrorMessage(LOGIN_ERROR, message));
            Log.e(TAG, message, e);
        }

        @Override
        public void onCancel() {
            context.error(new ErrorMessage(LOGIN_USER_CANCELLED, "User cancelled"));
            Log.i(TAG, "User cancelled");
        }
    }
}
