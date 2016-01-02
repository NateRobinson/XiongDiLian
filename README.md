

> 兄弟连是自己突发的一个点子，因为现在的社区类APP十分火爆，打着各种各样旗号的社交APP层出不穷，文采不咋样的我曾为这个APP的主题死了N多脑细胞，最终定成了“兄弟连",名字也许有点”丑陋“，但是我觉得的确可以用来做为一个社交APP的名字，所以，那就这么定啦。从下面开始 ，就不需要纠结名字了。

## 一 后台的选择：  ##

一款移动社交APP肯定少不了一个多功能的后台，当时有考虑过喊自己的程序猿小伙伴一起搞这个后台，但是因为时间、和精力的原因，我最终放弃了此想法，因为我发现了一个比之更便捷的方式，使用第三方云后台服务器，经过几重筛选，我选择了Bmob：[http://www.bmob.cn/](http://www.bmob.cn/ "http://www.bmob.cn/")，它们官网的口号是：Bmob后端云让移动开发更简单。。。那么就选他家了~

## 二 App主题颜色配置选择： ##
这里要向大家推荐一个非常实用的网站了：[http://www.materialpalette.com/teal/light-green](http://www.materialpalette.com/teal/light-green "http://www.materialpalette.com/teal/light-green")，这个网站可以快速的帮大家定制一款APP的主题色，并且可以下载对应的配色方案到本地，对于程序猿们简直一大福利。

## 三 App各种icon图标的选择： ##
说到icon，不得不提到阿里巴巴矢量图库：[http://iconfont.cn/repositories ](http://iconfont.cn/repositories  "http://iconfont.cn/repositories ")，这个网站不但适用UI设计师，也同样适用于想独自完成一款App设计的程序猿们，在里面，你可以发掘到任何想要的icon，并且在下载的时候可以设置各种颜色，配合上面的样色配置网站，简直实用到家~

## 四 APP几个模块分析： ##

**2.1 首页：**

首页采用黄金比例分割的方式，将页面分成了四个部分：兄弟连，好友中心，图片故事，个人中心；
并且为四个布局的点击添加了缩放动画效果，使整个页面在简洁的同时又不失酷炫。

![首页图片](https://github.com/NateRobinson/XiongDiLian/blob/master/imgs/1.png?raw=true)


**2.2 兄弟连:**

没有加入过任何兄弟连的时候：会给用户相关友好性的提示。并且点击页面的加号或者右上角的加号之后，会弹出选择对话框。

![首页图片](https://github.com/NateRobinson/XiongDiLian/blob/master/imgs/2.png?raw=true)
![首页图片](https://github.com/NateRobinson/XiongDiLian/blob/master/imgs/3.png?raw=true)
                   
创建和查找功能的页面尽量做到了简洁，并且为查找设置了热门推荐功能和名称查找功能。

![首页图片](https://github.com/NateRobinson/XiongDiLian/blob/master/imgs/4.png?raw=true)
![首页图片](https://github.com/NateRobinson/XiongDiLian/blob/master/imgs/5.png?raw=true)
![首页图片](https://github.com/NateRobinson/XiongDiLian/blob/master/imgs/6.png?raw=true)
                  
加入兄弟连过兄弟连之后：这里加入了谷歌自带的SwipeRefreshLayout，通过手势向下拖动，可以进行下拉刷新的操作

![首页图片](https://github.com/NateRobinson/XiongDiLian/blob/master/imgs/8.png?raw=true)
                  
点击兄弟连列表，可以进入某一个兄弟连的详情界面，点击成员数目，可以进入成员列表

![首页图片](https://github.com/NateRobinson/XiongDiLian/blob/master/imgs/9.png?raw=true)
                  
点击右上角的发帖按钮，可以跳转到发帖界面，进行发帖操作：

![首页图片](https://github.com/NateRobinson/XiongDiLian/blob/master/imgs/10.png?raw=true)

发帖成功之后会自动跳转到上一个页面，并做自动刷新操作。点击发的帖子，进入帖子详情界面：在详情页面可以对帖子内容、评论进行查看，并且可以进行收藏、评论的操作。并且点击帖子图片，可以进入图片查看界面。在首页和聊天主页均会有聊天提醒功能。并可以在个人中心设置提醒开关。

![首页图片](https://github.com/NateRobinson/XiongDiLian/blob/master/imgs/11.png?raw=true)
![首页图片](https://github.com/NateRobinson/XiongDiLian/blob/master/imgs/12.png?raw=true)
![首页图片](https://github.com/NateRobinson/XiongDiLian/blob/master/imgs/13.png?raw=true)

**2.3 好友中心：**

好友中心集成了最近聊天消息列表，联系人列表，添加好友入口，新朋友列表入口，附近的人列表入口。联系人页面的搜索为本地搜索功能。短按消息列表可以进入聊天界面，长按聊天列表项，弹出是否删除该聊天对话框。

![首页图片](https://github.com/NateRobinson/XiongDiLian/blob/master/imgs/14.png?raw=true)
![首页图片](https://github.com/NateRobinson/XiongDiLian/blob/master/imgs/15.png?raw=true)
![首页图片](https://github.com/NateRobinson/XiongDiLian/blob/master/imgs/16.png?raw=true)

**2.4 图片故事：**

图片故事这个模块实现了图片即时分享，用户可以在右上角的快速入口进入发布图片故事入口，首页为最近的图片故事列表。

![首页图片](https://github.com/NateRobinson/XiongDiLian/blob/master/imgs/17.png?raw=true)
![首页图片](https://github.com/NateRobinson/XiongDiLian/blob/master/imgs/18.png?raw=true)

**2.5 个人中心：**

个人中心里面有：个人资料，黑名单，接收消息控制，退出账号等功能模块。这块比较复杂的是个人资料界面，里面又集成了性别设置，昵称设置，城市设置功能模块。

## 五 引用的开源库介绍： ##

**1.Sweet Alert Dialog**
> 
> 地址：[https://github.com/pedant/sweet-alert-dialog](https://github.com/pedant/sweet-alert-dialog "https://github.com/pedant/sweet-alert-dialog")
> 
> 介绍：Android版的SweetAlert，清新文艺，快意灵动的甜心弹框

**2.Butter Knife**
> 
> 地址：[https://github.com/JakeWharton/butterknife](https://github.com/JakeWharton/butterknife "https://github.com/JakeWharton/butterknife")
> 
> 介绍：Android视图注入库，配合AS的插件使用，简直高效

**3.FlycoDialog-Master**
> 
> 地址：[https://github.com/H07000223/FlycoDialog_Master](https://github.com/H07000223/FlycoDialog_Master "https://github.com/H07000223/FlycoDialog_Master")
> 
> 介绍：一个强大的Android对话框库,简化自定义对话框.支持2.2+.

**4.android-crop**
> 
> 地址：[https://github.com/jdamcd/android-crop](https://github.com/jdamcd/android-crop "https://github.com/jdamcd/android-crop")
> 
> 介绍：An Android library project that provides a simple image cropping Activity, based on code from AOSP.

**5.google-gson**
>
> 地址：[https://github.com/google/gson](https://github.com/google/gson "https://github.com/google/gson")
>
> 介绍：Gson is a Java library that can be used to convert Java Objects into their JSON representation. It can also be used to convert a JSON string to an equivalent Java object. Gson can work with arbitrary Java objects including pre-existing objects that you do not have source-code of.

**6.EventBus**
>
> 地址：[https://github.com/greenrobot/EventBus](https://github.com/greenrobot/EventBus "https://github.com/greenrobot/EventBus")
>
> 介绍：EventBus is publish/subscribe event bus optimized for Android.

**7.Material Dialog v1.2.2**
>
> 地址：[https://github.com/drakeet/MaterialDialog](https://github.com/drakeet/MaterialDialog "https://github.com/drakeet/MaterialDialog")
>
> 介绍：This is an Android library, I call it MaterialDialog. It's very easy to use. Just new it & call show() method, then the beautiful AlertDialog will show automatically. It is artistic, conforms to Google Material Design. I hope that you will like it, and enjoys it.

**8.SwitchButton**
>
> 地址：[https://github.com/kyleduo/SwitchButton](https://github.com/kyleduo/SwitchButton "https://github.com/kyleduo/SwitchButton")
>
> 介绍：This project provides you a convenient way to use and customise a SwitchButton widget in Android. With just resources changed and attrs set, you can create a lifelike SwitchButton of Android 5.0+, iOS, MIUI, or Flyme and so on.

**9.Universal Image Loader**
>
> 地址：[https://github.com/nostra13/Android-Universal-Image-Loader](https://github.com/nostra13/Android-Universal-Image-Loader "https://github.com/nostra13/Android-Universal-Image-Loader")
>
> 介绍：Android library #1 on GitHub. UIL aims to provide a powerful, flexible and highly customizable instrument for image loading, caching and displaying. It provides a lot of configuration options and good control over the image loading and caching process.

**10.Picasso**
>
> 地址：[https://github.com/square/picasso](https://github.com/square/picasso "https://github.com/square/picasso")
>
> 介绍：A powerful image downloading and caching library for Android.
