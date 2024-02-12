package dev.thorinwasher.noblockdrops.listener;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.WorldMock;
import be.seeseemelk.mockbukkit.block.BlockMock;
import be.seeseemelk.mockbukkit.entity.ItemEntityMock;
import be.seeseemelk.mockbukkit.entity.PlayerMock;
import dev.thorinwasher.noblockdrops.NoBlockDrops;
import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Item;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockDropItemEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.block.BlockFadeEvent;
import org.bukkit.event.block.BlockFormEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.LeavesDecayEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BlockEventListenerTest {

    private NoBlockDrops noBlockDrops;
    private BlockMock trackedBlock;
    private WorldMock world;
    private PlayerMock player;
    private ServerMock serverMock;
    private BlockMock notTrackedBlock;

    @BeforeEach
    void setUp() {
        this.serverMock = MockBukkit.mock();
        this.noBlockDrops = MockBukkit.load(NoBlockDrops.class);
        this.world = serverMock.addSimpleWorld("world");
        this.trackedBlock = new BlockMock(new Location(world, 0, 0, 0));
        this.notTrackedBlock = new BlockMock(new Location(world, 1, 0, 0));
        this.player = serverMock.addPlayer();
        noBlockDrops.trackBlock(trackedBlock);
    }

    @AfterEach
    void tearDown() {
        MockBukkit.unmock();
    }

    @Test
    void onBlockDropItem_shouldCancel() {
        List<Item> items = List.of(new ItemEntityMock(serverMock, UUID.randomUUID(), ItemStack.empty()));
        BlockDropItemEvent event = new BlockDropItemEvent(trackedBlock, trackedBlock.getState(), player, items);
        assertFalse(event.callEvent());
    }

    @Test
    void onBlockDropItem_shouldNotCancel() {
        List<Item> items = List.of(new ItemEntityMock(serverMock, UUID.randomUUID(), ItemStack.empty()));
        BlockDropItemEvent event = new BlockDropItemEvent(notTrackedBlock, notTrackedBlock.getState(), player, items);
        assertTrue(event.callEvent());
    }

    @Test
    void onBlockBreak() {
        BlockBreakEvent blockBreakEvent = new BlockBreakEvent(trackedBlock, player);
        assertTrue(blockBreakEvent.callEvent());
        assertFalse(noBlockDrops.blockIsTracked(trackedBlock));
    }

    @Test
    void onBlockBurn() {
        BlockBurnEvent blockBurnEvent = new BlockBurnEvent(trackedBlock, notTrackedBlock);
        assertTrue(blockBurnEvent.callEvent());
        assertFalse(noBlockDrops.blockIsTracked(trackedBlock));
    }

    @Test
    void onBlockFade() {
        BlockFadeEvent blockFadeEvent= new BlockFadeEvent(trackedBlock, trackedBlock.getState());
        assertTrue(blockFadeEvent.callEvent());
        assertFalse(noBlockDrops.blockIsTracked(trackedBlock));
    }

    @Test
    void onBlockForm() {
        BlockFormEvent blockFormEvent = new BlockFormEvent(trackedBlock,trackedBlock.getState());
        assertTrue(blockFormEvent.callEvent());
        assertFalse(noBlockDrops.blockIsTracked(trackedBlock));
    }

    @Test
    void onBlockPlace() {
        BlockPlaceEvent blockPlaceEvent = new BlockPlaceEvent(trackedBlock,trackedBlock.getState(),
                trackedBlock.getRelative(BlockFace.UP),ItemStack.empty(),player,true, EquipmentSlot.HAND);
        assertTrue(blockPlaceEvent.callEvent());
        assertFalse(noBlockDrops.blockIsTracked(trackedBlock));
    }

    @Test
    void onLeavesDecay() {
        LeavesDecayEvent leavesDecayEvent = new LeavesDecayEvent(trackedBlock);
        assertTrue(leavesDecayEvent.callEvent());
        assertFalse(noBlockDrops.blockIsTracked(trackedBlock));
    }
}