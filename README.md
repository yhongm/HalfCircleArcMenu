# HalfCircleArcMenu

# 简介:半圆孤行菜单，可以自定义按钮背景，按钮说明，可以实时的根据需求更改按钮的数量

# 效果如下:
<img src="/preview/preview.gif">

# 使用方法
## Step 1. Add the JitPack repository to your build file
## Add it in your root build.gradle at the end of repositories:
```
	allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
```


## Step 2. Add the dependency

```
 	        dependencies {
        	        implementation 'com.github.yhongm:HalfCircleArcMenu:master'
        	}
```
## 1.布局文件添加一下属性:
###  <com.yhongm.arcmenu.ArcMenu
###        app:main_btn_img="@drawable/main_btn"  //中间主按钮的背景
###        android:id="@+id/arcMenu"
###        android:layout_width="match_parent"
###        android:layout_height="match_parent"
###        android:layout_alignParentBottom="true"></com.yhongm.arcmenu.ArcMenu>
###

## 2.java方法:
### arcMenu.addChildArcMenu(当前添加子按钮图片资源id,按钮标题,按钮附带的额外信息可以为空)//添加按钮
### arcMenu.layoutChildMenu()//初始化菜单 
###  arcMenu.setShowMenuBtnNum(显示子按钮数量)//设置当前菜单显示子按钮数量
### arcMenu.setOnMenuItemClickListener(this) 设置点击的监听
