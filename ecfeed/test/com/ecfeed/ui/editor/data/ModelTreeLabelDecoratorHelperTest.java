/*******************************************************************************
 *
 * All rights reserved. This program and the accompanying materials              
 * are made available under the terms of the Eclipse Public License v1.0         
 * which accompanies this distribution, and is available at                      
 * http://www.eclipse.org/legal/epl-v10.html 
 *  
 * Copyright (c) 2016 ecFeed AS.                                                
 *******************************************************************************/

package com.ecfeed.ui.editor.data;

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
import com.ecfeed.core.utils.SystemHelper;
import com.ecfeed.ui.common.utils.IJavaProjectProvider;
import com.ecfeed.ui.modelif.IModelUpdateContext;


public class ModelTreeLabelDecoratorHelperTest {

	@Test
	public void shouldReturnTheSameImageWhenThereAreNoDecorators() {

		if (!operatingSystemOk()) {
			return;
		}

		Display display = new Display();

		try {
			Map<List<Image>, Image> decoratedImagesCache = new HashMap<List<Image>, Image>();

			Image imageToDecorate = createTestImage(display);

			IModelUpdateContext modelUpdateContext = null;
			IJavaProjectProvider javaProjectProvider = new DummyJavaProjectProvider();

			RootNode rootNode = new RootNode("Root", ModelVersionDistributor.getCurrentSoftwareVersion());

			ModelTreeLabelDecoratorHelper.resetImageCreationCounter();

			Image decoratedImage = ModelTreeLabelDecoratorHelper.decorateImageOfAbstractNode(
					imageToDecorate, rootNode, decoratedImagesCache, modelUpdateContext, javaProjectProvider);

			assertEquals(decoratedImage, imageToDecorate);
			assertEquals(0, decoratedImagesCache.size());

			assertEquals(0, ModelTreeLabelDecoratorHelper.getImageCreationCounter());

		} finally {
			display.dispose();
		}
	}

	@Test
	public void shouldGetTheSameImage() {

		if (!operatingSystemOk()) {
			return;
		}

		Map<List<Image>, Image> decoratedImagesCache = new HashMap<List<Image>, Image>();
		Display display = new Display();

		try {
			Image imageToDecorate1 = createTestImage(display);
			Image decorator1 = createTestImage(display);

			assertNotEquals(imageToDecorate1, decorator1);

			List<Image> decorators1 = new ArrayList<Image>();
			decorators1.add(decorator1);

			ModelTreeLabelDecoratorHelper.resetImageCreationCounter();

			Image decoratedImage1 = 
					ModelTreeLabelDecoratorHelper.getOrCreateDecoratedImage(
							imageToDecorate1, decorators1, decoratedImagesCache);

			assertEquals(1, decoratedImagesCache.size());
			assertEquals(1, ModelTreeLabelDecoratorHelper.getImageCreationCounter());

			Image decoratedImage2 = 
					ModelTreeLabelDecoratorHelper.getOrCreateDecoratedImage(
							imageToDecorate1, decorators1, decoratedImagesCache);

			assertEquals(1, decoratedImagesCache.size());
			assertEquals(1, ModelTreeLabelDecoratorHelper.getImageCreationCounter());

			assertEquals(decoratedImage1, decoratedImage2);

			Image decoratedImage3 = 
					ModelTreeLabelDecoratorHelper.getOrCreateDecoratedImage(
							imageToDecorate1, decorators1, decoratedImagesCache);

			assertEquals(1, decoratedImagesCache.size());
			assertEquals(1, ModelTreeLabelDecoratorHelper.getImageCreationCounter());

			assertEquals(decoratedImage1, decoratedImage3);

		} finally {

			display.dispose();
		}
	}

	@Test
	public void shouldGetDifferentImagesWhenDecoratorDiffers() {

		if (!operatingSystemOk()) {
			return;
		}

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

			ModelTreeLabelDecoratorHelper.resetImageCreationCounter();

			Image decoratedImage1 = 
					ModelTreeLabelDecoratorHelper.getOrCreateDecoratedImage(
							imageToDecorate1, decorators1, decoratedImagesCache);

			assertEquals(1, decoratedImagesCache.size());
			assertEquals(1, ModelTreeLabelDecoratorHelper.getImageCreationCounter());

			Image decoratedImage2 = 
					ModelTreeLabelDecoratorHelper.getOrCreateDecoratedImage(
							imageToDecorate1, decorators2, decoratedImagesCache);

			assertEquals(2, decoratedImagesCache.size());
			assertEquals(2, ModelTreeLabelDecoratorHelper.getImageCreationCounter());

			assertNotEquals(decoratedImage1, decoratedImage2);

		} finally {

			display.dispose();
		}
	}

	@Test
	public void shouldGetDifferentImagesWhenImagesDiffers() {

		if (!operatingSystemOk()) {
			return;
		}

		Map<List<Image>, Image> decoratedImagesCache = new HashMap<List<Image>, Image>();
		Display display = new Display();

		try {
			Image imageToDecorate1 = createTestImage(display);
			Image imageToDecorate2 = createTestImage(display);
			Image decorator1 = createTestImage(display);

			List<Image> decorators1 = new ArrayList<Image>();
			decorators1.add(decorator1);

			Image decoratedImage1 = 
					ModelTreeLabelDecoratorHelper.getOrCreateDecoratedImage(
							imageToDecorate1, decorators1, decoratedImagesCache);

			assertEquals(1, decoratedImagesCache.size());
			assertEquals(1, ModelTreeLabelDecoratorHelper.getImageCreationCounter());

			Image decoratedImage2 = 
					ModelTreeLabelDecoratorHelper.getOrCreateDecoratedImage(
							imageToDecorate2, decorators1, decoratedImagesCache);

			assertEquals(2, decoratedImagesCache.size());
			assertEquals(2, ModelTreeLabelDecoratorHelper.getImageCreationCounter());

			assertNotEquals(decoratedImage1, decoratedImage2);

		} finally {

			display.dispose();
		}
	}


	private boolean operatingSystemOk() {

		if (SystemHelper.isOperatingSystemMacOs()) {
			// On MacOs Infinitest reports exception. Run As Unit Test - passes.
			return false;
		}

		return true;
	}

	private Image createTestImage(Display display) {

		PaletteData paletteData = new PaletteData(0, 0, 0);
		ImageData imageData = new ImageData(1, 1, 8, paletteData);
		imageData.alphaData = new byte[] {0};

		Image imageToDecorate = new Image(display, imageData);
		return imageToDecorate;
	}

	private class DummyJavaProjectProvider implements IJavaProjectProvider {

		@Override
		public Object getProject() {
			return null;
		}

		@Override
		public Object getPackageFragmentRoot() {
			return null;
		}

	}	

}
