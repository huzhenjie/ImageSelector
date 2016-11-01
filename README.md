# ImageSelector

------

图片选择器, 支持多图选择和图片预览等功能。

[![](https://jitpack.io/v/huzhenjie/ImageSelector.svg)](https://jitpack.io/#huzhenjie/ImageSelector)

效果图

------

<img src="https://github.com/huzhenjie/ImageSelector/blob/master/images/3158364398.gif" width="240px" height="427px" />

<img src="https://github.com/huzhenjie/ImageSelector/blob/master/images/device-2016-11-01-164055.png" width="240px" height="427px" />
<img src="https://github.com/huzhenjie/ImageSelector/blob/master/images/device-2016-11-01-164203.png" width="240px" height="427px" />
<img src="https://github.com/huzhenjie/ImageSelector/blob/master/images/device-2016-11-01-164115.png" width="240px" height="427px" />

# How to

**Step 1.** Add the JitPack repository to your build file

Add it in your root build.gradle at the end of repositories:

```
allprojects {
	repositories {
		...
		maven { url "https://jitpack.io" }
	}
}
```

**Step 2.** Add the dependency

```
dependencies {
        compile 'com.github.huzhenjie:ImageSelector:1.0.0'
}
```

**Step 3.** 

Call select image in your code

```
private static final int REQUEST_CODE_SELECT_IMG = 1;

...
ImageSelector.show(this, REQUEST_CODE_SELECT_IMG);
...
```

And override the method `onActivityResult` to get the results

```
@Override
protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    switch (requestCode) {
        case REQUEST_CODE_SELECT_IMG:
            List<String> yourSelectImgPaths = data == null ? Collections.<String>emptyList() : data.getStringArrayListExtra("data");
            Log.d("imgSelector", "paths: " + yourSelectImgPaths);
            break;
        default:
            super.onActivityResult(requestCode, resultCode, data);
            break;
    }
}
```