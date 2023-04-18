# ZXingLite

![Image](app/src/main/ic_launcher-web.png)

[![Download](https://img.shields.io/badge/download-App-blue.svg)](https://raw.githubusercontent.com/jenly1314/ZXingLite/master/app/release/app-release.apk)
[![MavenCentral](https://img.shields.io/maven-central/v/com.github.jenly1314/zxing-lite)](https://repo1.maven.org/maven2/com/github/jenly1314/zxing-lite)
[![JCenter](https://img.shields.io/badge/JCenter-2.0.3-46C018.svg)](https://bintray.com/beta/#/jenly/maven/zxing-lite)
[![JitPack](https://jitpack.io/v/jenly1314/ZXingLite.svg)](https://jitpack.io/#jenly1314/ZXingLite)
[![CI](https://travis-ci.org/jenly1314/ZXingLite.svg?branch=master)](https://travis-ci.org/jenly1314/ZXingLite)
[![CircleCI](https://circleci.com/gh/jenly1314/ZXingLite.svg?style=svg)](https://circleci.com/gh/jenly1314/ZXingLite)
[![API](https://img.shields.io/badge/API-21%2B-blue.svg?style=flat)](https://android-arsenal.com/api?level=21)
[![License](https://img.shields.io/badge/license-Apche%202.0-blue.svg)](http://www.apache.org/licenses/LICENSE-2.0)
[![Blog](https://img.shields.io/badge/blog-Jenly-9933CC.svg)](https://jenly1314.github.io/)
[![QQGroup](https://img.shields.io/badge/QQGroup-20867961-blue.svg)](http://shang.qq.com/wpa/qunwpa?idkey=8fcc6a2f88552ea44b1.1.982c94fd124f7bb3ec227e2a400dbbfaad3dc2f5ad)

ZXingLite for Android 是ZXing的精简极速版，基于ZXing库优化扫码和生成二维码/条形码功能，扫码界面完全支持自定义，也可一行代码使用默认实现的扫码功能。总之你想要的都在这里。
> 简单如斯，你不试试？ 

## Gif 展示
![Image](GIF.gif)

> 你也可以直接下载 [演示App](https://raw.githubusercontent.com/jenly1314/ZXingLite/master/app/release/app-release.apk) 体验效果


## ViewfinderView属性说明

| 属性                     | 属性类型   | 默认值                                | 属性说明                                              |
|:------------------------|:----------|:-------------------------------------|:--------------------------------------------------|
| maskColor               | color     | <font color=#000000>#60000000</font> | 扫描区外遮罩的颜色                                         |
| frameColor              | color     | <font color=#1FB3E2>#7F1FB3E2</font> | 扫描区边框的颜色                                          |
| cornerColor             | color     | <font color=#1FB3E2>#FF1FB3E2</font> | 扫描区边角的颜色                                          |
| laserColor              | color     | <font color=#1FB3E2>#FF1FB3E2</font> | 扫描区激光线的颜色                                         |
| labelText               | string    |                                      | 扫描提示文本信息                                          |
| labelTextColor          | color     | <font color=#C0C0C0>#FFC0C0C0</font> | 提示文本字体颜色                                          |
| labelTextSize           | dimension | 14sp                                 | 提示文本字体大小                                          |
| labelTextPadding        | dimension | 24dp                                 | 提示文本距离扫描区的间距                                      |
| labelTextWidth          | dimension |                                      | 提示文本的宽度，默认为View的宽度                                |
| labelTextLocation       | enum      | bottom                               | 提示文本显示位置                                          |
| frameWidth              | dimension |                                      | 扫码框宽度                                             |
| frameHeight             | dimension |                                      | 扫码框高度                                             |
| laserStyle              | enum      | line                                 | 扫描激光的样式                                           |
| gridColumn              | integer   | 20                                   | 网格扫描激光列数                                          |
| gridHeight              | integer   | 40dp                                 | 网格扫描激光高度，为0dp时，表示动态铺满                             |
| cornerRectWidth         | dimension | 4dp                                  | 扫描区边角的宽                                           |
| cornerRectHeight        | dimension | 16dp                                 | 扫描区边角的高                                           |
| scannerLineMoveDistance | dimension | 2dp                                  | 扫描线每次移动距离                                         |
| scannerLineHeight       | dimension | 5dp                                  | 扫描线高度                                             |
| frameLineWidth          | dimension | 1dp                                  | 边框线宽度                                             |
| scannerAnimationDelay   | integer   | 20                                   | 扫描动画延迟间隔时间，单位：毫秒                                  |
| frameRatio              | float     | 0.625f                               | 扫码框与屏幕占比                                          |
| framePaddingLeft        | dimension | 0                                    | 扫码框左边的内间距                                         |
| framePaddingTop         | dimension | 0                                    | 扫码框上边的内间距                                         |
| framePaddingRight       | dimension | 0                                    | 扫码框右边的内间距                                         |
| framePaddingBottom      | dimension | 0                                    | 扫码框下边的内间距                                         |
| frameGravity            | enum      | center                               | 扫码框对齐方式                                           |
| pointColor              | color     | <font color=#1FB3E2>#FF1FB3E2</font> | 结果点的颜色                                            |
| pointStrokeColor        | color     | <font color=#FFFFFF>#FFFFFFFF</font> | 结果点描边的颜色                                          |
| pointRadius             | dimension | 15dp                                 | 结果点的半径                                            |
| pointStrokeRatio        | float     | 1.2                                  | 结果点描边半径与结果点半径的比例                                  |
| pointDrawable           | reference |                                      | 结果点自定义图片                                          |
| showPointAnim           | boolean   | true                                 | 是否显示结果点的动画                                        |
| laserDrawable           | reference |                                      | 扫描激光自定义图片                                         |
| laserDrawableRatio      | float     | 0.625f                               | 激光扫描图片与屏幕占比                                       |
| viewfinderStyle         | enum      | classic                              | 取景框样式；支持：classic：经典样式（带扫码框那种）、popular：流行样式（不带扫码框） |


## 引入

### Gradle:

1. 在Project的 **build.gradle** 里面添加远程仓库  
          
```gradle
allprojects {
    repositories {
        //...
        mavenCentral()
    }
}
```

2. 在Module的 **build.gradle** 里面添加引入依赖项

```gradle
// AndroidX 版本
implementation 'com.github.jenly1314:zxing-lite:2.4.0'

```

### 温馨提示

#### 关于ZXingLite版本与编译的SDK版本要求

> 使用 **v2.3.x** 以上版本时，要求 **compileSdkVersion >= 33**

> 使用 **v2.2.x** 以上版本时，要求 **compileSdkVersion >= 31**

> 如果 **compileSdkVersion < 31** 请使用 **v2.2.x** 以前的版本

#### 对于需兼容 Android 5.0 (N) 以下版本的老项目（即：minSdk<21），可使用1.x旧版本

**v1.x** 旧版本 [v1.1.9](https://github.com/jenly1314/ZXingLite/tree/androidx)

**Gradle**

1. 在Project的 **build.gradle** 里面添加远程仓库
```gradle
allprojects {
    repositories {
        //...
        jcenter()
    }
}
```
2. 在Module的 **build.gradle** 里面添加引入依赖项
```gradle
// AndroidX 版本
implementation 'com.king.zxing:zxing-lite:1.1.9-androidx'

// Android Support 版本
implementation 'com.king.zxing:zxing-lite:1.1.9'
```
> 对于 **v1.x** 版本，当你看到这里，此时的 **JCenter** 仓库如果已关闭, 可使用 **JitPack** 仓库

## 使用说明

### 快速实现扫码识别有以下几种方式：

> 1、直接使用CaptureActivity或者CaptureFragment。(默认的扫码实现)

> 2、通过继承CaptureActivity或者CaptureFragment并自定义布局。（适用于大多场景，并无需关心扫码相关逻辑，自定义布局时需覆写getLayoutId方法）实现示例：[CustomCaptureActivity](app/src/main/java/com/king/zxing/app/CustomCaptureActivity.java) 和 [QRCodeActivity](app/src/main/java/com/king/zxing/app/QRCodeActivity.java)

> 3、在你项目的Activity或者Fragment中实例化一个CameraScan即可。（适用于想在扫码界面写交互逻辑，又因为项目架构或其它原因，无法直接或间接继承CaptureActivity或CaptureFragment时使用）实现示例：[CustomFullScanActivity](app/src/main/java/com/king/zxing/app/CustomFullScanActivity.java)

> 4、继承CameraScan自己实现一个，可参照默认实现类DefaultCameraScan，其它步骤同方式3。（扩展高级用法，谨慎使用）

### 关于 CameraScan

**CameraScan** 作为相机扫描的（核心）基类；所有与相机扫描相关的都是基于此类来直接或间接进行控制的。

### 关于 CameraConfig

主要是相机相关的配置；如：摄像头的前置后置、相机预览相关、图像分析相关等配置。

> 你可以直接库中内置实现的相机配置： **CameraConfig** 、**AspectRatioCameraConfig** 和 **ResolutionCameraConfig**。

#### 这里简单说下各自的特点：

* **CameraConfig**：默认的相机配置。
* **AspectRatioCameraConfig**：根据纵横比配置相机，使输出分析的图像尽可能的接近屏幕的比例
* **ResolutionCameraConfig**：根据尺寸配置相机的目标图像大小，使输出分析的图像的分辨率尽可能的接近屏幕尺寸

> 你也可以自定义或覆写 **CameraConfig** 中的 **options** 方法，根据需要定制配置。

这里特别温馨提示：默认配置在未配置相机的目标分析图像大小时，会优先使用：横屏：640 * 480 竖屏：480 * 640；

根据这个图像质量顺便说下默认配置的优缺点：

* 优点：因为图像质量不高，所以在低配置的设备上使用也能hold住，这样就能尽可能的适应各种设备；
* 缺点：正是由于图像质量不高，从而可能会对检测识别率略有影响，比如在某些机型上体验欠佳。
* 结论：在适配、性能与体验之间得有所取舍，找到平衡点。

> 当使用默认的 **CameraConfig** 在某些机型上体验欠佳时，你可以尝试使用 **AspectRatioCameraConfig** 或
**ResolutionCameraConfig** 会有意想不到奇效。

### 关于 **Analyzer**

**Analyzer** 为定义的分析器接口；主要用于分析相机预览的帧数据；通过实现 **Analyzer** 可以自定义分析过程。

### 关于 **CaptureActivity** 和 **CaptureFragment**

**CaptureActivity** 和 **CaptureFragment** 作为扫描预览界面的基类，主要目的是便于快速实现扫码识别。

> 扫描预览界面内部持有 **CameraScan**，并处理了 **CameraScan** 的初始化（如：相机权限、相机预览、生命周期等细节）

## 使用示例

### CameraScan配置示例

**CameraScan** 里面包含部分支持链式调用的方法，即调用返回是 **CameraScan** 本身的一些配置建议在调用 **startCamera()** 方法之前调用。

> 如果是通过继承 **CaptureActivity** 或者 **CaptureFragment** 或其子类实现的相机扫描，可以在
**initCameraScan()** 方法中获取 **CameraScan** ，然后根据需要修改相关配置。

示例1：

```java
// 获取CameraScan，扫码相关的配置设置。CameraScan里面包含部分支持链式调用的方法，即调用返回是CameraScan本身的一些配置建议在startCamera之前调用。
getCameraScan().setPlayBeep(true)//设置是否播放音效，默认为false
        .setVibrate(true)//设置是否震动，默认为false
        .setCameraConfig(new ResolutionCameraConfig(this))//设置相机配置信息，CameraConfig可覆写options方法自定义配置
        .setNeedAutoZoom(false)//二维码太小时可自动缩放，默认为false
        .setNeedTouchZoom(true)//支持多指触摸捏合缩放，默认为true
        .setDarkLightLux(45f)//设置光线足够暗的阈值（单位：lux），需要通过{@link #bindFlashlightView(View)}绑定手电筒才有效
        .setBrightLightLux(100f)//设置光线足够明亮的阈值（单位：lux），需要通过{@link #bindFlashlightView(View)}绑定手电筒才有效
        .bindFlashlightView(ivFlashlight)//绑定手电筒，绑定后可根据光线传感器，动态显示或隐藏手电筒按钮
        .setOnScanResultCallback(this)//设置扫码结果回调，需要自己处理或者需要连扫时，可设置回调，自己去处理相关逻辑
        .setAnalyzer(new MultiFormatAnalyzer(new DecodeConfig()))//设置分析器,DecodeConfig可以配置一些解码时的配置信息，如果内置的不满足您的需求，你也可以自定义实现，
        .setAnalyzeImage(true);//设置是否分析图片，默认为true。如果设置为false，相当于关闭了扫码识别功能
        
// 启动预览（如果是通过继承CaptureActivity或CaptureFragment实现的则无需调用startCamera）
getCameraScan().startCamera();

// 设置闪光灯（手电筒）是否开启,需在startCamera之后调用才有效
getCameraScan().enableTorch(torch);

```

示例2：（只需识别二维码的配置示例）
```java
// 初始化解码配置
DecodeConfig decodeConfig = new DecodeConfig();
decodeConfig.setHints(DecodeFormatManager.QR_CODE_HINTS)//如果只有识别二维码的需求，这样设置效率会更高，不设置默认为DecodeFormatManager.DEFAULT_HINTS
    .setFullAreaScan(false)//设置是否全区域识别，默认false
    .setAreaRectRatio(0.8f)//设置识别区域比例，默认0.8，设置的比例最终会在预览区域裁剪基于此比例的一个矩形进行扫码识别
    .setAreaRectVerticalOffset(0)//设置识别区域垂直方向偏移量，默认为0，为0表示居中，可以为负数
    .setAreaRectHorizontalOffset(0);//设置识别区域水平方向偏移量，默认为0，为0表示居中，可以为负数

// 在启动预览之前，设置分析器，只识别二维码
getCameraScan()
        .setCameraConfig(new AspectRatioCameraConfig(this))//设置相机配置，使用 AspectRatioCameraConfig
        .setVibrate(true)//设置是否震动，默认为false
        .setAnalyzer(new MultiFormatAnalyzer(decodeConfig));//设置分析器,如果内置实现的一些分析器不满足您的需求，你也可以自定义去实现
```

### 布局示例

**PreviewView** 用来预览，布局内至少要保证有 **PreviewView**，如果是继承 **CaptureActivity** 或 **CaptureFragment**，控件id可覆写`getPreviewViewId`方法自定义

**ViewfinderView** 用来渲染扫码视图，给用户起到一个视觉效果，本身扫码识别本身没有关系，如果是继承 **CaptureActivity** 或 **CaptureFragment**，控件ID可复写`getViewfinderViewId`方法自定义，默认为 **previewView**，返回0表示无需 **ViewfinderView**

**ivFlashlight** 是布局内置的手电筒，如果是继承 **CaptureActivity** 或 **CaptureFragment**，控件id可复写`getFlashlightId`方法自定义，默认为 **ivFlashlight**。返回0表示无需内置手电筒。您也可以自己去定义

>  可自定义布局（覆写`getLayoutId`方法），布局内至少要保证有 **PreviewView**。

```Xml
<FrameLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent">

    <androidx.camera.view.PreviewView
        android:id="@+id/previewView"
        android:layout_width="match_parent"
        android:layout_height="match_parent" />

    <com.king.zxing.ViewfinderView
        android:id="@+id/viewfinderView"
        android:layout_width="match_parent"
        android:layout_height="match_parent" />

    <ImageView
        android:id="@+id/ivFlashlight"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_gravity="center"
        android:layout_marginTop="@dimen/zxl_flashlight_margin_top"
        android:contentDescription="@null"
        android:src="@drawable/zxl_flashlight_selector" />
</FrameLayout>
```

或在你的布局中添加

```Xml
    <include layout="@layout/zxl_capture"/>
```

### 代码示例

**工具类CodeUtils的使用示例（二维码/条形码）**
```Java

    // 生成二维码
    CodeUtils.createQRCode(content,600,logo);
    // 生成条形码
    CodeUtils.createBarCode(content, BarcodeFormat.CODE_128,800,200);
    // 解析条形码/二维码
    CodeUtils.parseCode(bitmap);
    // 解析二维码
    CodeUtils.parseQRCode(bitmap);
```

**通过继承CaptureActivity实现扫二维码完整示例**

```java
public class QRCodeActivity extends CaptureActivity {


    @Override
    public int getLayoutId() {
        return R.layout.qr_code_activity;
    }

    @Override
    public void initCameraScan() {
        super.initCameraScan();

        //初始化解码配置
        DecodeConfig decodeConfig = new DecodeConfig();
        decodeConfig.setHints(DecodeFormatManager.QR_CODE_HINTS)//如果只有识别二维码的需求，这样设置效率会更高，不设置默认为DecodeFormatManager.DEFAULT_HINTS
            .setFullAreaScan(false)//设置是否全区域识别，默认false
            .setAreaRectRatio(0.8f)//设置识别区域比例，默认0.8，设置的比例最终会在预览区域裁剪基于此比例的一个矩形进行扫码识别
            .setAreaRectVerticalOffset(0)//设置识别区域垂直方向偏移量，默认为0，为0表示居中，可以为负数
            .setAreaRectHorizontalOffset(0);//设置识别区域水平方向偏移量，默认为0，为0表示居中，可以为负数

        //在启动预览之前，设置分析器，只识别二维码
        getCameraScan()
                .setVibrate(true)//设置是否震动，默认为false
                .setNeedAutoZoom(true)//二维码太小时可自动缩放，默认为false
                .setAnalyzer(new MultiFormatAnalyzer(decodeConfig));//设置分析器,如果内置实现的一些分析器不满足您的需求，你也可以自定义去实现
    }


    /**
     * 扫码结果回调
     * @param result
     * @return 返回false表示不拦截，将关闭扫码界面并将结果返回给调用界面；
     *  返回true表示拦截，需自己处理逻辑。当isAnalyze为true时，默认会继续分析图像（也就是连扫）。
     *  如果只是想拦截扫码结果回调，并不想继续分析图像（不想连扫），请在拦截扫码逻辑处通过调
     *  用{@link CameraScan#setAnalyzeImage(boolean)}，
     *  因为{@link CameraScan#setAnalyzeImage(boolean)}方法能动态控制是否继续分析图像。
     *
     */
    @Override
    public boolean onScanResultCallback(Result result) {
        /*
         * 因为setAnalyzeImage方法能动态控制是否继续分析图像。
         *
         * 1. 因为分析图像默认为true，如果想支持连扫，返回true即可。
         * 当连扫的处理逻辑比较复杂时，请在处理逻辑前调用getCameraScan().setAnalyzeImage(false)，
         * 来停止分析图像，等逻辑处理完后再调用getCameraScan().setAnalyzeImage(true)来继续分析图像。
         *
         * 2. 如果只是想拦截扫码结果回调自己处理逻辑，但并不想继续分析图像（即不想连扫），可通过
         * 调用getCameraScan().setAnalyzeImage(false)来停止分析图像。
         */
        return super.onScanResultCallback(result);
    }
}

```

更多使用详情，请查看[app](app)中的源码使用示例或直接查看[API帮助文档](https://jenly1314.github.io/projects/ZXingLite/doc/)

### 其他

#### AndroidManifest

如果你直接使用了默认 **CaptureActivity** ，则需在你项目的AndroidManifest中注册 **CaptureActivity**，配置如下
```Xml
    <activity
        android:name="com.king.zxing.CaptureActivity"
        android:screenOrientation="portrait"
        android:theme="@style/CaptureTheme"/>
```
#### JDK版本

需使用JDK8+编译，在你项目中的build.gradle的android{}中添加配置：

```gradle
compileOptions {
    targetCompatibility JavaVersion.VERSION_1_8
    sourceCompatibility JavaVersion.VERSION_1_8
}

```

#### API脱糖

当使用ZXingLite为 **v2.3.x** 以上版本时，（即：更新zxing至v3.5.1后）；如果要兼容Android 7.0 (N) 以下版本（即：minSdk<24），可通过脱糖获得 Java 8 及更高版本 API。

```gradle
compileOptions {
    // Flag to enable support for the new language APIs
    coreLibraryDesugaringEnabled true
    // Sets Java compatibility to Java 8
    targetCompatibility JavaVersion.VERSION_1_8
    sourceCompatibility JavaVersion.VERSION_1_8
}

```

```gradle
dependencies {
    coreLibraryDesugaring 'com.android.tools:desugar_jdk_libs:1.2.2'
}
```

### 相关推荐

#### [MLKit](https://github.com/jenly1314/MLKit) 一个强大易用的工具包。通过ML Kit您可以很轻松的实现文字识别、条码识别、图像标记、人脸检测、对象检测等功能。    
#### [WeChatQRCode](https://github.com/jenly1314/WeChatQRCode) 基于OpenCV开源的微信二维码引擎移植的扫码识别库。


## 版本说明

### v2.x 基于CameraX进行了重构

#### v2.x 相对于 v1.x 的优势

* v2.x基于CameraX，抽象整体流程，可扩展性更高。
* v2.x基于CameraX通过预览裁剪的方式确保预览界面不变形，无需铺满屏幕，就能适配（v1.x通过遍历Camera支持预览的尺寸，找到与屏幕最接近的比例，减少变形的可能性（需铺满屏幕，才能适配）)

#### v2.x 特别说明

* v2.x如果您是通过继承CaptureActivity或CaptureFragment实现扫码功能，那么动态权限申请相关都已经在CaptureActivity或CaptureFragment处理好了。
* v2.x如果您是通过继承CaptureActivity或CaptureFragment实现扫码功能，如果有想要修改默认配置，可重写**initCameraScan**方法，修改CameraScan的配置即可，如果无需修改配置，直接在跳转原界面的**onActivityResult** 接收扫码结果即可（更多具体详情可参见[app](app)中的使用示例）。

#### v1.x 说明

[【v1.1.9】](https://github.com/jenly1314/ZXingLite/tree/androidx) 如果您正在使用 **1.x** 版本请点击下面的链接查看分支版本，当前 **2.x** 版本已经基于 **CameraX** 进行重构，API变化较大，谨慎升级。

查看AndroidX版 **1.x** 分支 [请戳此处](https://github.com/jenly1314/ZXingLite/tree/androidx)

查看Android Support版 **1.x** 分支 [请戳此处](https://github.com/jenly1314/ZXingLite/tree/android)

查看 [ **1.x** API帮助文档](https://jenly1314.github.io/projects/ZXingLite/doc/)

## 版本记录

#### v2.4.0：2023-4-15
* 优化CameraScan的缺省配置（CameraConfig相关配置）
* 优化ViewfinderView自定义属性（新增laserDrawableRatio）
* 优化ImageAnalyzer中YUV数据的处理
* 更新CameraX至v1.2.2

#### v2.3.1：2023-3-4
* 更新CameraX至v1.2.1
* 更新Gradle至v7.5
* 优化细节

#### v2.3.0：2022-12-11
* 更新CameraX至v1.2.0
* 更新zxing至v3.5.1
* 更新compileSdkVersion至33

#### [查看更多版本记录](change_log.md)

## 赞赏
如果您喜欢ZXingLite，或感觉ZXingLite帮助到了您，可以点右上角“Star”支持一下，您的支持就是我的动力，谢谢 :smiley:<p>
您也可以扫描下面的二维码，请作者喝杯咖啡 :coffee:
<div>
<img src="https://jenly1314.github.io/image/pay/sponsor.png" width="98%">
</div>

## 关于我
Name: <a title="关于作者" href="https://jenly1314.github.io" target="_blank">Jenly</a>

Email: <a title="欢迎邮件与我交流" href="mailto:jenly1314@gmail.com" target="_blank">jenly1314#gmail.com</a> / <a title="给我发邮件" href="mailto:jenly1314@vip.qq.com" target="_blank">jenly1314#vip.qq.com</a>

CSDN: <a title="CSDN博客" href="http://blog.csdn.net/jenly121" target="_blank">jenly121</a>

CNBlogs: <a title="博客园" href="https://www.cnblogs.com/jenly" target="_blank">jenly</a>

GitHub: <a title="GitHub开源项目" href="https://github.com/jenly1314" target="_blank">jenly1314</a>

Gitee: <a title="Gitee开源项目" href="https://gitee.com/jenly1314" target="_blank">jenly1314</a>

加入QQ群: <a title="点击加入QQ群" href="http://shang.qq.com/wpa/qunwpa?idkey=8fcc6a2f88552ea44b1411582c94fd124f7bb3ec227e2a400dbbfaad3dc2f5ad" target="_blank">20867961</a>
   <div>
       <img src="https://jenly1314.github.io/image/jenly666.png">
       <img src="https://jenly1314.github.io/image/qqgourp.png">
   </div>



