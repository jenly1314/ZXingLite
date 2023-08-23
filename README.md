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

ZXingLite for Android 是ZXing的精简极速版，基于ZXing库优化扫码和生成二维码/条形码功能，扫码界面完全支持自定义；使用ZXingLite可快速实现扫码识别相关功能。
> 简单如斯，你不试试？ 

## Gif 展示
![Image](GIF.gif)

> 你也可以直接下载 [演示App](https://raw.githubusercontent.com/jenly1314/ZXingLite/master/app/release/app-release.apk) 体验效果

## 引入

### Gradle:

1. 在Project的 **build.gradle** 或 **setting.gradle** 中添加远程仓库

```gradle
repositories {
    //...
    mavenCentral()
    maven { url 'https://jitpack.io' }
}
```

2. 在Module的 **build.gradle** 里面添加引入依赖项

```gradle
// AndroidX 版本
implementation 'com.github.jenly1314:zxing-lite:3.0.0'

```

### 温馨提示

#### 关于ZXingLite版本与编译的SDK版本要求

> 使用 **v3.x.x** 以上版本时，要求 **compileSdkVersion >= 33**

> 如果 **compileSdkVersion < 33** 请使用 [**v2.x版本**](https://github.com/jenly1314/ZXingLite/tree/2.x/)

## 使用说明

### 3.x版本的变化

从 **2.x** 到 **3.x** 主要变化如下：
* 2.x版本中的 **CameraScan** 相关核心类被移除了；
> 从3.0.0版本开始改为依赖[CameraScan](https://github.com/jenly1314/CameraScan)；（[CameraScan](https://github.com/jenly1314/CameraScan)是一个独立的库，单独进行维护）

* 2.x版本中的 **ViewfinderView** 被移除了；
> 从3.0.0版本开始改为依赖[ViewfinderView](https://github.com/jenly1314/ViewfinderView)；（[ViewfinderView](https://github.com/jenly1314/ViewfinderView)是一个独立的库，单独进行维护）

* 2.x版本中的 **CaptureActivity** 和 **CaptureFragment** 相关基类被移除了；
> 从3.0.0版本开始改为 **BarcodeCameraActivity** 和 **BarcodeCameraFragment**

除了以上几点主要差异变化，3.x版本的整体使用方式和2.x基本类似；3.x版本在2.x版本的基础上再次进行重构，将 **CameraScan** 相关的公共基础类从 **ZXingLite** 中移除后，维护起来更方便了。

> 如果你是从 **2.x** 版本升级至 **3.x** 版本，那么你需要知道上面所说的主要差异；特别是独立出去单独维护的库，其包名都有所变化，这一点需要特别注意；请谨慎升级。

> 如果你使用的是2.x版本的话请直接[查看v2.x分支版本](https://github.com/jenly1314/ZXingLite/tree/2.x/)

### 3.x版本的使用

3.x的实现主要是以[CameraScan](https://github.com/jenly1314/CameraScan)作为基础库去实现具体的分析检测功能，所以你可以先去看下[CameraScan](https://github.com/jenly1314/CameraScan)的使用说明；在了解了[CameraScan](https://github.com/jenly1314/CameraScan)的基本使用方式后，然后再结合当前的使用说明就可以轻松的集成并使用 **ZXingLite**了。

### 主要类说明

#### Analyzer的实现类

内部提供了Analyzer对应的实现，都是为快速实现扫码识别而提供的分析器。

内部提供的分析器有多个；一般情况下，你只需要知道最终实现的 [**MultiFormatAnalyzer**](zxing-lite/src/main/java/com/king/zxing/analyze/MultiFormatAnalyzer.java) 和 [**QRCodeAnalyzer**](zxing-lite/src/main/java/com/king/zxing/analyze/QRCodeAnalyzer.java) 即可：

**MultiFormatAnalyzer** 和 **QRCodeAnalyzer** 的主要区别，从名字大概就能看的出来；一个是可识别多种格式，一个是只识别二维码（具体需要支持识别哪些格式的条码，其实还要看提供的**DecodeConfig**是怎么配置的）。

> 本可以不需要 ****QRCodeAnalyzer****，之所以提供一个 **QRCodeAnalyzer** 是因为有很多需求是只需要识别二维码就行；如果你有连续扫码的需求或不知道怎么选时，推荐直接选择 **MultiFormatAnalyzer** 。

#### DecodeConfig

DecodeConfig：解码配置；主要用于在扫码识别时，提供一些配置，便于扩展。通过配置可决定内置分析器的能力，从而间接的控制并简化扫码识别的流程。一般在使用 **Analyzer** 的实现类时，你可能会用到。

#### DecodeFormatManager

DecodeConfig：解码格式管理器；主要将多种条码格式进行划分与归类，便于提供快捷配置。

#### CodeUtils

工具类 **CodeUtils** 中主要提供；解析条形码/二维码、生成条形码/二维码相关的能力。

**CodeUtils的使用示例（二维码/条形码）**
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

### BarcodeCameraScanActivity

**通过继承BarcodeCameraScanActivity实现扫二维码完整示例**

```java
public class QRCodeScanActivity extends BarcodeCameraScanActivity {

    @Override
    public void initCameraScan(@NonNull CameraScan<Result> cameraScan) {
        super.initCameraScan(cameraScan);
        // 根据需要设置CameraScan相关配置
        cameraScan.setPlayBeep(true);
    }

    @Nullable
    @Override
    public Analyzer<Result> createAnalyzer() {
        // 初始化解码配置
        DecodeConfig decodeConfig = new DecodeConfig();
        decodeConfig.setHints(DecodeFormatManager.QR_CODE_HINTS)//如果只有识别二维码的需求，这样设置效率会更高，不设置默认为DecodeFormatManager.DEFAULT_HINTS
                .setFullAreaScan(false)//设置是否全区域识别，默认false
                .setAreaRectRatio(0.8f)//设置识别区域比例，默认0.8，设置的比例最终会在预览区域裁剪基于此比例的一个矩形进行扫码识别
                .setAreaRectVerticalOffset(0)//设置识别区域垂直方向偏移量，默认为0，为0表示居中，可以为负数
                .setAreaRectHorizontalOffset(0);//设置识别区域水平方向偏移量，默认为0，为0表示居中，可以为负数
        // BarcodeCameraScanActivity默认使用的MultiFormatAnalyzer，这里可以改为使用QRCodeAnalyzer
        return new QRCodeAnalyzer(decodeConfig);
    }

    /**
     * 布局ID；通过覆写此方法可以自定义布局
     *
     * @return 布局ID
     */
    @Override
    public int getLayoutId() {
        return R.layout.activity_qrcode_scan;
    }

    @Override
    public void onScanResultCallback(@NonNull AnalyzeResult<Result> result) {
        // 停止分析
        getCameraScan().setAnalyzeImage(false);
        // 返回结果
        Intent intent = new Intent();
        intent.putExtra(CameraScan.SCAN_RESULT, result.getResult().getText());
        setResult(Activity.RESULT_OK, intent);
        finish();
    }
}

```

> **BarcodeCameraScanFragment** 的使用方式与之类似。

更多使用详情，请查看[app](app)中的源码使用示例或直接查看[API帮助文档](https://jenly1314.github.io/projects/ZXingLite/doc/)

### 其他

#### JDK版本与API脱糖

当使用ZXingLite为 **v2.3.x** 以上版本时，（即：更新zxing至v3.5.1后）；如果要兼容Android 7.0 (N) 以下版本（即：minSdk<24），可通过脱糖获得 Java 8 及更高版本 API。

```gradle
compileOptions {
    // Flag to enable support for the new language APIs
    coreLibraryDesugaringEnabled true
    // Sets Java compatibility to Java 11
    targetCompatibility JavaVersion.VERSION_11
    sourceCompatibility JavaVersion.VERSION_11
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
#### [CameraScan](https://github.com/jenly1314/CameraScan) 一个简化扫描识别流程的通用基础库。
#### [ViewfinderView](https://github.com/jenly1314/ViewfinderView) ViewfinderView一个取景视图：主要用于渲染扫描相关的动画效果。

## 版本记录

#### v3.0.0：2023-8-23
* 将通用基础类拆分移除并进行重构，后续维护更便捷
* 移除 **CameraScan** 相关核心类，改为依赖[CameraScan](https://github.com/jenly1314/CameraScan)
* 移除扫码取景视图 **ViewfinderView**，改为依赖[ViewfinderView](https://github.com/jenly1314/ViewfinderView)
* 移除**CaptureActivity**和****CaptureFragment**，新增**BarcodeCameraScanActivity**和****BarcodeCameraScanFragment**来替代
* 优化扫描分析过程的性能体验（优化帧数据分析过程）

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



