package dev.thorinwasher.noblockdrops.listener;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.WorldMock;
import be.seeseemelk.mockbukkit.block.BlockMock;
import be.seeseemelk.mockbukkit.entity.PlayerMock;
import dev.thorinwasher.noblockdrops.NoBlockDrops;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PistonEventListenerTest {

    private ServerMock serverMock;
    private NoBlockDrops noBlockDrops;
    private WorldMock world;
    private BlockMock trackedBlock;
    private BlockMock notTrackedBlock;

    @BeforeEach
    void setUp() {
        this.serverMock = MockBukkit.mock();
        this.noBlockDrops = MockBukkit.load(NoBlockDrops.class);
        this.world = serverMock.addSimpleWorld("world");
        this.trackedBlock = new BlockMock(new Location(world, 0, 0, 0));
        this.notTrackedBlock = new BlockMock(new Location(world, 1, 0, 0));
        noBlockDrops.trackBlock(trackedBlock);
    }

    @AfterEach
    void tearDown() {
        MockBukkit.unmock();
    }

    @Test
    void onBlockPistonExtend() {
        BlockPistonExtendEvent blockPistonExtendEvent = new BlockPistonExtendEvent(notTrackedBlock, List.of(trackedBlock, notTrackedBlock), BlockFace.UP);
        assertTrue(blockPistonExtendEvent.callEvent());
        assertFalse(noBlockDrops.blockIsTracked(trackedBlock));
        assertTrue(noBlockDrops.blockIsTracked(trackedBlock.getRelative(BlockFace.UP)));
        assertFalse(noBlockDrops.blockIsTracked(notTrackedBlock.getRelative(BlockFace.UP)));
    }

    @Test
    void onBlockPistonRetract() {
        BlockPistonRetractEvent blockPistonExtendEvent = new BlockPistonRetractEvent(notTrackedBlock, List.of(trackedBlock, notTrackedBlock), BlockFace.UP);
        assertTrue(blockPistonExtendEvent.callEvent());
        assertFalse(noBlockDrops.blockIsTracked(trackedBlock));
        assertTrue(noBlockDrops.blockIsTracked(trackedBlock.getRelative(BlockFace.UP)));
        Block movedNotTrackedBlock = notTrackedBlock.getRelative(BlockFace.UP);
        assertFalse(noBlockDrops.blockIsTracked(movedNotTrackedBlock));
    }
}