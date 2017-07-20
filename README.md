# SugarLoader
A lightweight API to add syntax sugar to Android Loader API

## Why SugarLoader ?
Google's [Loader API](https://developer.android.com/guide/components/loaders.html#summary) is great, as it provides a robust way to load data into a fragment or an activity, with no risk 
to duplicate calls, lose result on config changes, or deliver data to a view that is not concerned by the call.
With loader API, on a fragment that needs one call to display data, developers can develop the fragment as a simple 
init -> load -> display page, without having to consider fragment lifecycle (state restoration, etc...). Loader API was designed
for this.

Problem is that Loader API is *verbose*, and needs tons of boilerplate to have it work : loader implementation, loader callbacks, 
loaderManager, technical loading details... and this may be why developers often choose to use alternative libraries, then losing 
the only framework that is aware of fragment and activity lifecycles...

**SugarLoader** is just a naive wrapper that simplifies Loader API to a simple builder chain.

## How to use SugarLoader ?

### Gradle config
Add this line to your app's build.gradle :
```
     compile 'com.github.alexgianq:sugarloader:0.1'
```


### Simplest case
SugarLoader was designed so that data loading should be written as a simple pipeline. Every builder parameter is optional.
Here is the simplest call :
```java
     new SugarLoader<MyDataObject>("Load Data")
          .background(mService::loadData)
          .onSuccess(this::displayData)
          .init();
```
With this code placed inside a *onCreate()* or *onResume()*, the *mService.loadData()* will be called once, and only once for 
this fragment instance, and **all of its instance restorations**. If the device changes its configuration while data is loading, 
the next fragment instance auto-binds itself to the already running loader, and processes to onSuccess. The previously destroyed 
fragment has **no risk to call** the *displayData* method while it is destroyed, so do **not** "protect" your code with if's, 
it's useless.

### Other available callbacks :
If you want to obtain more callbacks, you can use as follow :
```java
  new SugarLoader<MyDataObject>("My Loader name")
          .beforeStart(...) // Executed before connecting or creating a loader (display a progress bar, hide a view...) 
          .beforeCreateLoader(...) // Special callback, invoked only when a loader is effectively created (e.g. log loader effective creation)
          .background(...) // Effective load. May throw exception.
          .beforeDeliver(...) // Always called before data is delivered, however result is result or failure. Use it to hide the progress bar for example.
          .onSuccess(...) // Called when no exception happened
          .onError(...) // Called when an exception happened
          .init();
```

### Other use cases
The sugarLoader object may also be stored into an object attribute, so you can refer to it later to replay your loading :
```java
  public void onResume() {
      mLoader.init();
  }

  ...

  public void onClickMyReloadButton(){
    mLoader.restart();
  }
  
  ...
  
  public void onStaleData(){
    mLoader.destroy();
  }
```

If you have multiple loaders inside the same fragment (load data from different sources), give each loader a name (or an int id) so they 
won't be singleton'ed by Loader API :
```java
  mFooLoader = new SugarLoader("foo"); // named foo
  mBarLoader = new SugarLoader("bar"); // named bar
  
  ...
  
  mFooLoader.init(); // Will create or attach to existing "Foo" loader, with no risk to attach to "Bar" Loader
  mBarLoader.init(); // Will create or attach to existing "Bar" loader, with no risk to attach to "Foo" Loader
  
```

## Is it compatible with my X framework ?
Yes. 

SugarLoader pulls no library, except for android support, so that it works on both standard and support Activities and 
Fragments. So you have no risk to pull unwanted libraries into your project, that would interfere with your libraries.
Supported language version goes down to java7, so you can use official Gradle's java8 support for Android 24- API versions, 
retrolambda, or inner anonymous objects if you like boilerplate.
Min supported sdk is version 9 (Gingerbread).
