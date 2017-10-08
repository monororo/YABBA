package com.latmod.yabba.client;

import com.feed_the_beast.ftbl.lib.icon.Color4I;
import com.feed_the_beast.ftbl.lib.util.StringUtils;
import com.feed_the_beast.ftbl.lib.util.misc.TextureSet;
import com.latmod.yabba.Yabba;
import com.latmod.yabba.api.BarrelSkin;
import com.latmod.yabba.block.BlockBarrelBase;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.IStateMapper;
import net.minecraft.client.renderer.color.IBlockColor;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.client.model.ICustomModelLoader;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.common.model.IModelState;
import net.minecraftforge.common.property.IExtendedBlockState;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * @author LatvianModder
 */
public enum BarrelModelLoader implements IModel, ICustomModelLoader, IBlockColor, IItemColor, IStateMapper
{
	INSTANCE;

	public static final ModelResourceLocation MODEL_LOCATION = new ModelResourceLocation(Yabba.MOD_ID + ":barrel#normal");

	@Override
	public boolean accepts(ResourceLocation modelLocation)
	{
		return modelLocation.getResourceDomain().equals(Yabba.MOD_ID) && modelLocation.getResourcePath().equals("barrel");
	}

	@Override
	public IModel loadModel(ResourceLocation modelLocation) throws Exception
	{
		return this;
	}

	@Override
	public void onResourceManagerReload(IResourceManager resourceManager)
	{
		YabbaClient.loadModelsAndSkins();
	}

	@Override
	public Collection<ResourceLocation> getDependencies()
	{
		return Collections.emptyList();
	}

	@Override
	public Collection<ResourceLocation> getTextures()
	{
		return YabbaClient.TEXTURES;
	}

	@Override
	public IBakedModel bake(IModelState state, VertexFormat format, Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter)
	{
		TextureAtlasSprite particle = bakedTextureGetter.apply(new ResourceLocation("blocks/planks_oak"));

		for (BarrelSkin skin : YabbaClient.ALL_SKINS)
		{
			skin.spriteSet = skin.textures.getSpriteSet(bakedTextureGetter);
		}

		for (BarrelModel model : YabbaClient.ALL_MODELS)
		{
			model.textureMap = new HashMap<>();

			for (Map.Entry<String, TextureSet> entry : model.textures.entrySet())
			{
				model.textureMap.put(entry.getKey(), entry.getValue().getSpriteSet(bakedTextureGetter));
			}
		}

		return new BakedBarrelBlockModel(particle, format);
	}

	@Override
	public int colorMultiplier(IBlockState state, @Nullable IBlockAccess worldIn, @Nullable BlockPos pos, int tintIndex)
	{
		if (tintIndex == 0)
		{
			String id = "";

			if (state instanceof IExtendedBlockState)
			{
				id = StringUtils.emptyIfNull(((IExtendedBlockState) state).getValue(BlockBarrelBase.SKIN));
			}

			Color4I color = YabbaClient.getSkin(id).color;

			if (!color.isEmpty())
			{
				return color.rgba();
			}
		}

		return 0xFFFFFFFF;
	}

	@Override
	public int colorMultiplier(ItemStack stack, int tintIndex)
	{
		if (tintIndex == 0)
		{
			String id = "";

			if (stack.hasTagCompound())
			{
				NBTTagCompound data = stack.getTagCompound().getCompoundTag("BlockEntityTag");
				id = data.getString("Skin");
			}

			Color4I color = YabbaClient.getSkin(id).color;

			if (!color.isEmpty())
			{
				return color.rgba();
			}
		}

		return 0xFFFFFFFF;
	}

	@Override
	public Map<IBlockState, ModelResourceLocation> putStateModelLocations(Block blockIn)
	{
		Map<IBlockState, ModelResourceLocation> map = new HashMap<>();

		for (IBlockState state : blockIn.getBlockState().getValidStates())
		{
			map.put(state, MODEL_LOCATION);
		}

		return map;
	}
}