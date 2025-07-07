## 版本日志

#### v3.3.0：2025-7-7
* 迁移发布至 **Central Portal** [相关公告](https://central.sonatype.org/pages/ossrh-eol/#logging-in-to-central-portal)
* 更新CameraScan至v1.3.1
* 更新ViewfinderView至v1.4.0
* 更新Gradle至v8.5

#### v3.2.0：2024-7-16
* 更新CameraScan至v1.2.0
* 更新ViewfinderView至v1.2.0
* 优化细节

#### v3.1.1：2024-4-29
* 更新CameraScan至v1.1.1
* 更新zxing至v3.5.3

#### v3.1.0：2023-12-31
* 更新CameraScan至v1.1.0
* 更新zxing至v3.5.2
* 更新compileSdkVersion至34
* 更新Gradle至v8.0

#### v3.0.1：2023-9-13
* 更新CameraScan至v1.0.1
* 更新ViewfinderView至v1.1.0

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

#### v2.2.1：2022-6-22
* 更新CameraX至v1.1.0-rc02

#### v2.2.0：2022-5-31
* 更新CameraX至v1.1.0-rc01
* 更新compileSdkVersion至31
* 更新Gradle至v7.2

#### v2.1.1：2021-8-4
* 更新CameraX至v1.0.1
* 优化CameraConfig的一些默认配置

#### v2.1.0：2021-6-30 (从v2.1.0开始不再发布至JCenter)
* 更新CameraX至v1.0.0
* 优化细节
* 发布至MavenCentral

#### v2.0.3：2021-3-26
* 更新CameraX至v1.0.0-rc03
* 优化一些默认配置

#### v2.0.2：2021-1-14
* **ViewfinderView** 新增 **labelTextWidth** 属性

#### v2.0.1：2020-12-30
* 更新CameraX至v1.0.0-rc01
* 新增支持点击预览区域对焦目标
* 修改一些默认配置
* 优化细节

#### v2.0.0：2020-12-24
* 基于CameraX进行重构
* 抽象整体流程，可扩展性更高
* 从2.x开始只支持AndroidX
* minSdk要求从 **16+** 改为 **21+**

#### v1.1.9：2020-4-28
* 修复1.1.8版本优化细节时，不小心改出个Bug(fix #86)

#### v1.1.8：2020-4-27
*  统一日志管理
*  优化细节

#### v1.1.7：2020-3-29
*  优化一些默认参数配置
*  修复扫码界面开启闪光灯并切到后台时，手电筒按钮状态未同步问题（fix #81）

#### v1.1.6：2019-12-27
*  生成条形码/二维码时支持自定义配置颜色
*  支持识别反色码（增强识别率，默认不支持，需通过 **CaptureHelper.supportLuminanceInvert(true)** 开启）

#### v1.1.5：2019-12-16
*  优化Camera初始化相关策略，减少出现卡顿的可能性

#### v1.1.4：2019-11-18
*  内置手电筒按钮,当光线太暗时，自动显示手电筒 (fix #58)
*  生成二维码时Logo支持自定义大小 (fix #62)

#### v1.1.3：2019-9-24
*  支持真实识别区域比例和识别区域偏移量可配置
*  对外暴露更多可配置参数

#### v1.1.2：2019-6-27
*  优化部分细节，为迁移至AndroidX做准备
*  支持AndroidX对应版本

#### v1.1.1：2019-5-20
*  支持扫二维码过小时，自动缩放
*  支持识别垂直条形码（增强条形码识别，默认不支持，需通过 **CaptureHelper.supportVerticalCode(true)** 开启）

#### v1.1.0：2019-4-19
*  将扫码相关逻辑与界面分离，ZXingLite使用更容易扩展
*  新增CaptureFragment

#### v1.0.7：2019-4-9
*  新增网格样式的扫描激光（类似支付宝扫码样式）
*  升级Gradle至v4.6

#### v1.0.6：2019-1-16
*  支持连续扫码
*  支持横屏扫码(主要为了支持Pad)

#### v1.0.5：2018-12-29
*  支持自定义扫码框宽高

#### v1.0.4：2018-12-19
*  修改text相关自定义属性，如：text->labelText

#### v1.0.3：2018-11-20
*  支持触摸缩放变焦

#### v1.0.2：2018-9-12
*  支持条形码下方显示显示code
*  优化相机预览尺寸遍历策略，从而降低预览变形的可能性

#### v1.0.1：2018-8-23
*  优化扫码识别速度

#### v1.0.0：2018-8-9
*  ZXingLite初始版本
