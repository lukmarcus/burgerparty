package com.agateau.burgerparty.screens;

import com.agateau.burgerparty.Assets;
import com.agateau.burgerparty.BurgerPartyGame;
import com.agateau.burgerparty.model.LevelWorld;
import com.agateau.burgerparty.model.Level;
import com.agateau.burgerparty.utils.Anchor;
import com.agateau.burgerparty.utils.AnchorGroup;
import com.agateau.burgerparty.utils.FileUtils;
import com.agateau.burgerparty.utils.GridGroup;
import com.agateau.burgerparty.utils.HorizontalGroup;
import com.agateau.burgerparty.utils.RefreshHelper;
import com.agateau.burgerparty.utils.UiUtils;
import com.agateau.burgerparty.view.BurgerPartyUiBuilder;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.XmlReader;

public class LevelListScreen extends BurgerPartyScreen {
	private static final int COL_COUNT = 3;
	private static final float CELL_SIZE = 130;

	private static final float SURPRISE_ROTATE_ANGLE = 5f;
	private static final float SURPRISE_ROTATE_DURATION = 0.8f;

	public LevelListScreen(BurgerPartyGame game, int worldIndex) {
		super(game);
		TextureAtlas atlas = getTextureAtlas();
		Image bgImage = new Image(atlas.findRegion("ui/menu-bg"));
		setBackgroundActor(bgImage);

		mWorldIndex = worldIndex;

		mStarOff = atlas.findRegion("ui/star-off");
		mStarOn = atlas.findRegion("ui/star-on");
		mLock = atlas.findRegion("ui/lock-key");
		mSurpriseRegion = atlas.findRegion("ui/surprise");
		setupWidgets();

		new RefreshHelper(getStage()) {
			@Override
			protected void refresh() {
				getGame().showLevelListScreen(mWorldIndex);
				dispose();
			}
		};
	}

	private class Builder extends BurgerPartyUiBuilder {
		public Builder(Assets assets) {
			super(assets);
		}

		@Override
		protected Actor createActorForElement(XmlReader.Element element) {
			if (element.getName().equals("LevelGrid")) {
				return createLevelButtonGridGroup();
			} else {
				return super.createActorForElement(element);
			}
		}
	}

	private void setupWidgets() {
		BurgerPartyUiBuilder builder = new Builder(getGame().getAssets());
		builder.build(FileUtils.assets("screens/levellist.gdxui"));
		AnchorGroup root = builder.getActor("root");
		getStage().addActor(root);
		root.setFillParent(true);

		builder.<ImageButton>getActor("backButton").addListener(new ChangeListener() {
			public void changed(ChangeListener.ChangeEvent Event, Actor actor) {
				onBackPressed();
			}
		});
	}

	private GridGroup createLevelButtonGridGroup() {
		GridGroup gridGroup = new GridGroup();
		gridGroup.setSpacing(UiUtils.SPACING);
		gridGroup.setColumnCount(COL_COUNT);
		gridGroup.setCellSize(CELL_SIZE, CELL_SIZE);

		LevelWorld levelWorld = getGame().getUniverse().get(mWorldIndex);
		for (int idx=0; idx < levelWorld.getLevelCount(); idx++) {
			Actor levelButton = createLevelButton(mWorldIndex, idx);
			gridGroup.addActor(levelButton);
		}
		return gridGroup;
	}

	class LevelButton extends TextButton {
		public LevelButton(int levelWorldIndex, int levelIndex, Skin skin) {
			super("", skin, "level-button");
			this.levelWorldIndex = levelWorldIndex;
			this.levelIndex = levelIndex;

			mGroup = new AnchorGroup();
			addActor(mGroup);
			mGroup.setFillParent(true);
		}

		public void createStars(int stars) {
			setText(String.valueOf(levelWorldIndex + 1) + "-" + String.valueOf(levelIndex + 1));
			HorizontalGroup starGroup = new HorizontalGroup();
			starGroup.setSpacing(4);
			for (int n = 1; n <= 3; ++n) {
				Image image = new Image(n > stars ? mStarOff : mStarOn);
				starGroup.addActor(image);
			}
			starGroup.setScale(0.8f);
			starGroup.pack();
			mGroup.addRule(starGroup, Anchor.BOTTOM_CENTER, mGroup, Anchor.BOTTOM_CENTER, 0, 8);
		}

		@Override
		public void draw(SpriteBatch batch, float parentAlpha) {
			if (isDisabled()) {
				batch.setShader(getGame().getAssets().getDisabledShader());
				super.draw(batch, parentAlpha);
				batch.setShader(null);
				float posX = getX() + (getWidth() - mLock.getRegionWidth()) / 2;
				float posY = getY() + (getHeight() - mLock.getRegionHeight()) / 2;
				batch.draw(mLock, posX, posY);
			} else {
				super.draw(batch, parentAlpha);
			}
		}

		private AnchorGroup mGroup;

		public int levelWorldIndex;
		public int levelIndex;
	}

	private Actor createLevelButton(int levelWorldIndex, int levelIndex) {
		LevelWorld world = getGame().getUniverse().get(levelWorldIndex);
		Level level = world.getLevel(levelIndex);
		LevelButton button = new LevelButton(levelWorldIndex, levelIndex, getGame().getAssets().getSkin());
		button.setSize(CELL_SIZE, CELL_SIZE);

		AnchorGroup group = new AnchorGroup();
		group.addRule(button, Anchor.TOP_LEFT, group, Anchor.TOP_LEFT);
		if (level.isLocked()) {
			button.setDisabled(true);
		} else {
			button.createStars(level.getStars());
		}
		if (level.hasBrandNewItem()) {
			createSurpriseImage(group);
		}

		button.addListener(new ChangeListener() {
			public void changed(ChangeListener.ChangeEvent Event, Actor actor) {
				getGame().getAssets().getSoundAtlas().findSound("click").play();
				LevelButton button = (LevelButton)actor;
				getGame().startLevel(button.levelWorldIndex, button.levelIndex);
			}
		});
		return group;
	}

	private void createSurpriseImage(AnchorGroup group) {
		Image image = new Image(mSurpriseRegion);
		image.setOrigin(mSurpriseRegion.getRegionWidth() / 2, mSurpriseRegion.getRegionHeight() / 2);
		group.addRule(image, Anchor.BOTTOM_RIGHT, group, Anchor.BOTTOM_RIGHT, -2f, 2f);
		float variation = MathUtils.random(0.9f, 1.1f);
		image.addAction(
			Actions.forever(
				Actions.sequence(
					Actions.delay(MathUtils.random(1f, 5f)),
					Actions.rotateTo(SURPRISE_ROTATE_ANGLE, SURPRISE_ROTATE_DURATION * variation / 2, Interpolation.sine),
					Actions.rotateTo(-SURPRISE_ROTATE_ANGLE, SURPRISE_ROTATE_DURATION * variation, Interpolation.sine),
					Actions.rotateTo(0, SURPRISE_ROTATE_DURATION * variation / 2, Interpolation.sine)
				)
			)
		);
	}

	@Override
	public void onBackPressed() {
		getGame().showWorldListScreen();
	}

	private int mWorldIndex;
	private TextureRegion mStarOff;
	private TextureRegion mStarOn;
	private TextureRegion mLock;
	private TextureRegion mSurpriseRegion;
}
