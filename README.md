# BlockUtil

A library that is meant to assist in block management. Currently it only has 4 api methods, which all relate to whether a block should drop an item or not. This protection is meant to be absolute; every block that has been tagged to not drop any items, will not do so, nor will any blocks generated from the tagged block. To use the project as a dependency you need the jitpack repository, then you can simply do:
```xml
<dependency>
    <groupId>com.github.Thorinwasher</groupId>
    <artifactId>BlockUtil</artifactId>
    <version>main-SNAPSHOT</version>
    <scope>provided</scope>
</dependency>
```
And you need to of course add the project as a dependency or a soft dependency in your `plugin.yml` (Shading is not supported). This is how you use access the API:
```java
@Override
public void onEnable(){
    ServicesManager servicesManager = this.getServer().getServicesManager();
    RegisteredServiceProvider<BlockUtilAPI> blockUtilProvider = servicesManager.getRegistration(BlockUtilAPI.class);
    if (blockUtilProvider != null) {
        this.blockUtilAPI = blockUtilProvider.getProvider();
    } else {
        throw new IllegalStateException("Unable to hook into BlockUil. Make sure the BlockUil plugin is installed " +
                    "and enabled.");
    }
}
```
