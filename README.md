# BlockUtil

A library that is meant to assist in block management.

```kts
repositories {
    maven("https://jitpack.io")
}

dependencies {
    implementation("com.github.Thorinwasher.BlockUtil:blockutil:v2.1.3")
}
```

```java

@Override
public void onEnable() {
    // Initialize block util and get the API
    BlockUtilAPI blockUtilAPI = new BlockUtilAPI.Builder()
            .withConnectionSupplier(() -> myDatabaseHandler.getConnection())
            .withDropEventHandler(event -> handleEvent(event))
            .withPluginOwner(this)
            .build();
    // Example use case
    blockUtilAPI.disableItemDrops(myBlock);
}
```
