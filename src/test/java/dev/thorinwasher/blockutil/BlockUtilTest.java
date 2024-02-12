package dev.thorinwasher.blockutil;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.WorldMock;
import be.seeseemelk.mockbukkit.block.BlockMock;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.util.BlockVector;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BlockUtilTest {

    private BlockUtil plugin;
    private WorldMock world;
    private ServerMock server;

    @BeforeEach
    void setUp() {
        this.server = MockBukkit.mock();
        this.plugin = MockBukkit.load(BlockUtil.class);
        this.world = server.addSimpleWorld("AWorld");
    }

    @AfterEach
    void tearDown() {
        MockBukkit.unmock();
    }

    @Test
    void trackBlock() {
        Block block = new BlockMock(new Location(world, 0, 2, 3));
        plugin.trackBlock(block);
        assertTrue(plugin.blockIsTracked(block));
    }

    @Test
    void trackBlock_reload() {
        Block block = new BlockMock(new Location(world, 0, 2, 3));
        plugin.trackBlock(block);
        assertTrue(plugin.blockIsTracked(block));
        server.getPluginManager().disablePlugin(plugin);
        server.getScheduler().waitAsyncTasksFinished();
        server.getPluginManager().enablePlugin(plugin);
        assertTrue(plugin.blockIsTracked(block));
    }

    @Test
    void freeBlock() {
        Block block = new BlockMock(new Location(world, 0, 2, 3));
        plugin.trackBlock(block);
        assertTrue(plugin.blockIsTracked(block));
        plugin.freeBlock(block);
        assertFalse(plugin.blockIsTracked(block));
    }

    @Test
    void moveBlock() {
        Block from = new BlockMock(new Location(world, 0, 2, 3));
        Block to = new BlockMock(new Location(world, 1, 2, 3));
        plugin.trackBlock(from);
        assertTrue(plugin.blockIsTracked(from));
        plugin.moveBlock(from, new BlockVector(1,0,0));
        assertTrue(plugin.blockIsTracked(to));
        assertFalse(plugin.blockIsTracked(from));
    }
}