package com.ecfeed.ui.editor;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.PaletteData;
import org.eclipse.swt.widgets.Display;
import org.junit.Test;

import com.ecfeed.core.model.ModelVersionDistributor;
import com.ecfeed.core.model.RootNode;
import com.ecfeed.ui.common.utils.IFileInfoProvider;
import com.ecfeed.ui.modelif.IModelUpdateContext;


public class ModelLabelDecoratorHelperTest {

	@Test
	public void shouldReturnTheSameImageWhenThereAreNoDecorators() {

		Display display = new Display();

		try {
			Map<List<Image>, Image> decoratedImagesCache = new HashMap<List<Image>, Image>();

			Image imageToDecorate = createTestImage(display);

			IModelUpdateContext modelUpdateContext = null;
			IFileInfoProvider fileInfoProvider = null;

			RootNode rootNode = new RootNode("Root", ModelVersionDistributor.getCurrentSoftwareVersion());

			ModelLabelDecoratorHelper.resetImageCreationCounter();

			Image decoratedImage = ModelLabelDecoratorHelper.decorateImageOfAbstractNode(
					imageToDecorate, rootNode, decoratedImagesCache, modelUpdateContext, fileInfoProvider);

			assertEquals(decoratedImage, imageToDecorate);
			assertEquals(0, decoratedImagesCache.size());

			assertEquals(0, ModelLabelDecoratorHelper.getImageCreationCounter());

		} finally {
			display.dispose();
		}
	}

	@Test
	public void shouldGetTheSameImage() {

		Map<List<Image>, Image> decoratedImagesCache = new HashMap<List<Image>, Image>();
		Display display = new Display();

		try {
			Image imageToDecorate1 = createTestImage(display);
			Image decorator1 = createTestImage(display);

			assertNotEquals(imageToDecorate1, decorator1);

			List<Image> decorators1 = new ArrayList<Image>();
			decorators1.add(decorator1);

			ModelLabelDecoratorHelper.resetImageCreationCounter();

			Image decoratedImage1 = 
					ModelLabelDecoratorHelper.getOrCreateDecoratedImage(
							imageToDecorate1, decorators1, decoratedImagesCache);

			assertEquals(1, decoratedImagesCache.size());
			assertEquals(1, ModelLabelDecoratorHelper.getImageCreationCounter());

			Image decoratedImage2 = 
					ModelLabelDecoratorHelper.getOrCreateDecoratedImage(
							imageToDecorate1, decorators1, decoratedImagesCache);

			assertEquals(1, decoratedImagesCache.size());
			assertEquals(1, ModelLabelDecoratorHelper.getImageCreationCounter());

			assertEquals(decoratedImage1, decoratedImage2);

			Image decoratedImage3 = 
					ModelLabelDecoratorHelper.getOrCreateDecoratedImage(
							imageToDecorate1, decorators1, decoratedImagesCache);

			assertEquals(1, decoratedImagesCache.size());
			assertEquals(1, ModelLabelDecoratorHelper.getImageCreationCounter());

			assertEquals(decoratedImage1, decoratedImage3);

		} finally {

			display.dispose();
		}
	}

	@Test
	public void shouldGetDifferentImagesWhenDecoratorDiffers() {

		Map<List<Image>, Image> decoratedImagesCache = new HashMap<List<Image>, Image>();
		Display display = new Display();

		try {
			Image imageToDecorate1 = createTestImage(display);
			Image decorator1 = createTestImage(display);
			Image decorator2 = createTestImage(display);

			List<Image> decorators1 = new ArrayList<Image>();
			decorators1.add(decorator1);

			List<Image> decorators2 = new ArrayList<Image>();
			decorators2.add(decorator2);

			ModelLabelDecoratorHelper.resetImageCreationCounter();

			Image decoratedImage1 = 
					ModelLabelDecoratorHelper.getOrCreateDecoratedImage(
							imageToDecorate1, decorators1, decoratedImagesCache);

			assertEquals(1, decoratedImagesCache.size());
			assertEquals(1, ModelLabelDecoratorHelper.getImageCreationCounter());

			Image decoratedImage2 = 
					ModelLabelDecoratorHelper.getOrCreateDecoratedImage(
							imageToDecorate1, decorators2, decoratedImagesCache);

			assertEquals(2, decoratedImagesCache.size());
			assertEquals(2, ModelLabelDecoratorHelper.getImageCreationCounter());

			assertNotEquals(decoratedImage1, decoratedImage2);

		} finally {

			display.dispose();
		}
	}

	@Test
	public void shouldGetDifferentImagesWhenImagesDiffers() {

		Map<List<Image>, Image> decoratedImagesCache = new HashMap<List<Image>, Image>();
		Display display = new Display();

		try {
			Image imageToDecorate1 = createTestImage(display);
			Image imageToDecorate2 = createTestImage(display);
			Image decorator1 = createTestImage(display);

			List<Image> decorators1 = new ArrayList<Image>();
			decorators1.add(decorator1);

			Image decoratedImage1 = 
					ModelLabelDecoratorHelper.getOrCreateDecoratedImage(
							imageToDecorate1, decorators1, decoratedImagesCache);

			assertEquals(1, decoratedImagesCache.size());
			assertEquals(1, ModelLabelDecoratorHelper.getImageCreationCounter());

			Image decoratedImage2 = 
					ModelLabelDecoratorHelper.getOrCreateDecoratedImage(
							imageToDecorate2, decorators1, decoratedImagesCache);

			assertEquals(2, decoratedImagesCache.size());
			assertEquals(2, ModelLabelDecoratorHelper.getImageCreationCounter());

			assertNotEquals(decoratedImage1, decoratedImage2);

		} finally {

			display.dispose();
		}
	}

	private Image createTestImage(Display display) {

		PaletteData paletteData = new PaletteData(0, 0, 0);
		ImageData imageData = new ImageData(1, 1, 8, paletteData);
		imageData.alphaData = new byte[] {0};

		Image imageToDecorate = new Image(display, imageData);
		return imageToDecorate;
	}

}
