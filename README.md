# cordova-plugin-weibo

A Cordova plugin for Sina Weibo SDK.

## 1. Features

1. 支持新浪微博开放平台授权登录，以及退出；
2. TODO 分享 (建议社交平台分享使用[SocialSharing-PhoneGap-Plugin](https://github.com/EddyVerbruggen/SocialSharing-PhoneGap-Plugin.git)，理论上支持所有社交平台)。

## 2. 插件使用

### IOS/Android添加插件

1. 添加插件

        cordova plugin add https://github.com/baomingba/cordova-plugin-weibo

2. 在`config.xml`中添加`WeiboAppId`和`WeiboRedirectURI`两个`preference`。`WeiboAppId`是微博开发平台创建的应用的app id；`WeiboRedirectURI`需要与开发平台应用设置的回调url一致。注意，虽然移动应用授权登录并不真正回调该url，但仍需正确设置。

        <preference name="WeiboAppId" value="1234567890" />
        <preference name="WeiboRedirectURI" value="http://www.example.com/login/weibo" />

3. 编译iOS/Android平台程序

        cordova build ios
        cordova build android
        
4. 对于**IOS平台**，需要额外修改生成的`Resouces/*-Info.plist`文件中的`CFBundleURLTypes`属性，把`com.weibo`对应的`CFBundleURLSchemes`从默认的`YOUR_WEIBO_APP_ID`改为`wb+appid`。比如app id为`1234567890`，则该属性为`wb1234567890`。

### 移除插件

        cordova plugin remove com.qiudao.cordova.weibo


## 3. JS使用说明

### 微博授权登录

    window.Weibo.login(function (response) {
        alert(JSON.stringify(response));
    }, function (reason) {
        alert('Failed: ' + JSON.stringify(reason));
    });
    