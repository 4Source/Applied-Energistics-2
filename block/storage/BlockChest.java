package appeng.block.storage;

import java.util.EnumSet;

import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import appeng.api.AEApi;
import appeng.api.storage.ICellHandler;
import appeng.api.storage.IMEInventoryHandler;
import appeng.api.storage.StorageChannel;
import appeng.block.AEBaseBlock;
import appeng.client.render.BaseBlockRender;
import appeng.client.render.blocks.RenderMEChest;
import appeng.core.features.AEFeature;
import appeng.core.localization.PlayerMessages;
import appeng.core.sync.GuiBridge;
import appeng.helpers.AENoHandler;
import appeng.tile.storage.TileChest;
import appeng.util.Platform;

public class BlockChest extends AEBaseBlock
{

	public BlockChest() {
		super( BlockChest.class, Material.iron );
		setfeature( EnumSet.of( AEFeature.StorageCells, AEFeature.MEChest ) );
		setTileEntiy( TileChest.class );
	}

	@Override
	protected Class<? extends BaseBlockRender> getRenderer()
	{
		return RenderMEChest.class;
	}

	@Override
	public boolean onActivated(World w, int x, int y, int z, EntityPlayer p, int side, float hitX, float hitY, float hitZ)
	{
		TileChest tg = getTileEntity( w, x, y, z );
		if ( tg != null && !p.isSneaking() )
		{
			if ( Platform.isClient() )
				return true;

			if ( side != tg.getUp().ordinal() )
			{
				Platform.openGUI( p, tg, ForgeDirection.getOrientation( side ), GuiBridge.GUI_CHEST );
			}
			else if ( tg.isPowered() )
			{
				ItemStack cell = tg.getStackInSlot( 1 );
				if ( cell != null )
				{
					ICellHandler ch = AEApi.instance().registries().cell().getHander( cell );

					try
					{
						IMEInventoryHandler ih = tg.getHandler( StorageChannel.ITEMS );
						if ( ch != null && ih != null )
						{
							IMEInventoryHandler mine = ih;
							ch.openChestGui( p, tg, ch, mine, cell, StorageChannel.ITEMS );
							return true;
						}

					}
					catch (AENoHandler e)
					{
						// :P
					}

					try
					{
						IMEInventoryHandler fh = tg.getHandler( StorageChannel.FLUIDS );
						if ( ch != null && fh != null )
						{
							IMEInventoryHandler mine = fh;
							ch.openChestGui( p, tg, ch, mine, cell, StorageChannel.FLUIDS );
							return true;
						}
					}
					catch (AENoHandler e)
					{
						// :P
					}
				}

				p.addChatMessage( PlayerMessages.ChestCannotReadStorageCell.get() );
			}
			else
				p.addChatMessage( PlayerMessages.MachineNotPowered.get() );

			return true;
		}

		return false;
	}
}
