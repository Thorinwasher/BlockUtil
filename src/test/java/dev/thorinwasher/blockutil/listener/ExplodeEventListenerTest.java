package dev.thorinwasher.blockutil.listener;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.WorldMock;
import be.seeseemelk.mockbukkit.block.BlockMock;
import dev.thorinwasher.blockutil.BlockUtil;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ExplodeEventListenerTest {
    private ServerMock serverMock;
    private BlockUtil blockUtil;
    private WorldMock world;
    private BlockMock trackedBlock;
    private BlockMock notTrackedBlock;

    @BeforeEach
    void setUp() {
        this.serverMock = MockBukkit.mock();
        this.blockUtil = MockBukkit.load(BlockUtil.class);
        this.world = serverMock.addSimpleWorld("world");
        this.trackedBlock = new BlockMock(new Location(world, 0, 0, 0));
        this.notTrackedBlock = new BlockMock(new Location(world, 1, 0, 0));
        blockUtil.disableItemDrops(trackedBlock);
    }

    @AfterEach
    void tearDown() {
        MockBukkit.unmock();
    }

    @Test
    void onBlockExplode() {
        BlockExplodeEvent blockExplodeEvent = new BlockExplodeEvent(notTrackedBlock, List.of(trackedBlock), 1, notTrackedBlock.getState());
        assertTrue(blockExplodeEvent.callEvent());
        assertFalse(blockUtil.blockCanNotDropItems(trackedBlock));
    }

    @Test
    void onEntityExplode() {
        Location location = new Location(world, 0,0,0);
        EntityExplodeEvent entityExplodeEvent = new EntityExplodeEvent(world.spawnEntity(location, EntityType.BAT),location,
                List.of(trackedBlock, notTrackedBlock),1);
        assertTrue(entityExplodeEvent.callEvent());
        assertFalse(blockUtil.blockCanNotDropItems(trackedBlock));
    }
}