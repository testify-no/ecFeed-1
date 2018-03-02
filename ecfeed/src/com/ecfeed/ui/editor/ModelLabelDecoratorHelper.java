/*******************************************************************************
 *
 * Copyright (c) 2016 ecFeed AS.                                                
 * All rights reserved. This program and the accompanying materials              
 * are made available under the terms of the Eclipse Public License v1.0         
 * which accompanies this distribution, and is available at                      
 * http://www.eclipse.org/legal/epl-v10.html 
 *  
 *******************************************************************************/

package com.ecfeed.ui.editor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.widgets.Display;

import com.ecfeed.core.model.AbstractNode;
import com.ecfeed.core.model.ChoiceNode;
import com.ecfeed.core.model.ClassNode;
import com.ecfeed.core.model.ConstraintNode;
import com.ecfeed.core.model.GlobalParameterNode;
import com.ecfeed.core.model.IModelVisitor;
import com.ecfeed.core.model.MethodNode;
import com.ecfeed.core.model.MethodParameterNode;
import com.ecfeed.core.model.RootNode;
import com.ecfeed.core.model.TestCaseNode;
import com.ecfeed.core.utils.SystemLogger;
import com.ecfeed.ui.common.ImageManager;
import com.ecfeed.ui.common.utils.IFileInfoProvider;
import com.ecfeed.ui.modelif.AbstractNodeInterface;
import com.ecfeed.ui.modelif.IModelUpdateContext;

public class ModelLabelDecoratorHelper  {

	static int fImageCreationCounter = 0;
	static boolean fErrorReported = false;

	public static Image decorateImageOfAbstractNode(
			Image imageToDecorate, 
			AbstractNode abstractNode,
			Map<List<Image>, Image> decoratedImagesCache,
			IModelUpdateContext modelUpdateContext,
			IFileInfoProvider fileInfoProvider) {

		try {
			return decorateImageOfAbstractNodeIntr(
					imageToDecorate, 
					abstractNode,
					decoratedImagesCache,
					modelUpdateContext,
					fileInfoProvider);
		} catch (Exception e) {

			if (!fErrorReported) {
				SystemLogger.logCatch("Can not decorate image. Icon implementation status etc. will not be available.");
				fErrorReported = true;
			}

			return imageToDecorate;
		}
	}

	public static Image decorateImageOfAbstractNodeIntr(
			Image imageToDecorate, 
			AbstractNode abstractNode,
			Map<List<Image>, Image> decoratedImagesCache,
			IModelUpdateContext modelUpdateContext,
			IFileInfoProvider fileInfoProvider) {

		List<Image> decorators = 
				ModelLabelDecoratorHelper.getDecoratorsForNode(
						abstractNode, modelUpdateContext, fileInfoProvider);

		if (decorators == null) {
			return imageToDecorate;
		}

		return ModelLabelDecoratorHelper.getOrCreateDecoratedImage(
				imageToDecorate, decorators, decoratedImagesCache);
	}

	public static Image getOrCreateDecoratedImage(
			Image imageToDecorate, 
			List<Image> decorators, 
			Map<List<Image>, Image> decoratedImagesCache) {

		List<Image> decoratedImageKey = createDecoratedImageKey(imageToDecorate, decorators);

		if (decoratedImagesCache.containsKey(decoratedImageKey)) {
			return decoratedImagesCache.get(decoratedImageKey);
		}

		Image newDecoratedImage = createDecoratedImage(imageToDecorate, decorators);
		decoratedImagesCache.put(decoratedImageKey, newDecoratedImage);

		return newDecoratedImage;
	}

	public static void resetImageCreationCounter() {
		fImageCreationCounter = 0;
	}

	public static int getImageCreationCounter() {
		return fImageCreationCounter;
	}

	@SuppressWarnings("unchecked")
	private static List<Image> getDecoratorsForNode(
			AbstractNode abstractNode, 
			IModelUpdateContext modelUpdateContext,
			IFileInfoProvider fileInfoProvider) {

		Object result = null;

		try {
			result =
					abstractNode.accept(
							new DecoratorImageListProvider(
									fileInfoProvider, 
									fileInfoProvider.isProjectAvailable(),
									modelUpdateContext));
		} catch (Exception e) {
			SystemLogger.logCatch(e.getMessage());
			return null;
		}

		return (List<Image>)result;
	}

	private static Image createDecoratedImage(Image imageToDecorate, List<Image> decorators) {

		ImageData newImageData = (ImageData)imageToDecorate.getImageData().clone();

		for (Image decorator : decorators) {

			if (decorator != null) {
				aplyOneDecorator(newImageData, decorator);
			}
		}

		fImageCreationCounter++;
		return new Image(Display.getCurrent(), newImageData);
	}

	private static void aplyOneDecorator(
			ImageData inOutImageToDecorateData, 
			Image decorator) {

		ImageData decoratorData = decorator.getImageData();

		int maxCol = calculateMaxCol(inOutImageToDecorateData, decoratorData);
		int maxRow = calculateMaxRow(inOutImageToDecorateData, decoratorData);

		decorateImageData(inOutImageToDecorateData, decoratorData, maxCol, maxRow);
	}

	private static void decorateImageData(
			ImageData inOutImageData, 
			ImageData decoratorData, 
			int maxCol,
			int maxRow) {

		int imageIndex = 0;
		int decoratorIndex = 0;

		for(int row = 0; row < maxRow; row ++){
			for(int col = 0; col < maxCol; col++){

				decorateOnePixel(inOutImageData, decoratorData, imageIndex, decoratorIndex);
				imageIndex += 1;
				decoratorIndex += 1;
			}
		}
	}

	private static void decorateOnePixel(
			ImageData inOutImageData,
			ImageData decoratorData, 
			int imageIndex, 
			int decoratorIndex) {

		if (decoratorData.alphaData[decoratorIndex] < 0) {

			inOutImageData.alphaData[imageIndex] = (byte)-1;

			inOutImageData.data[4 * imageIndex] = decoratorData.data[4 * decoratorIndex];
			inOutImageData.data[4 * imageIndex + 1] = decoratorData.data[4 * decoratorIndex + 1];
			inOutImageData.data[4 * imageIndex + 2] = decoratorData.data[4 * decoratorIndex + 2];
		}
	}

	private static List<Image> createDecoratedImageKey(Image image, List<Image> decorators) {

		List<Image> imageKey = new ArrayList<Image>(decorators);
		imageKey.add(0, image);

		return imageKey;
	}

	private static int calculateMaxRow(ImageData imageToDecorateData, ImageData decoratorData) {

		return calculateMaxSize(imageToDecorateData.height, decoratorData.height);
	}

	private static int calculateMaxCol(ImageData imageToDecorateData, ImageData decoratorData) {

		return calculateMaxSize(imageToDecorateData.width, decoratorData.width);
	}

	private static int calculateMaxSize(int imageToDecorateDataSize, int decoratorDataSize) {

		if (decoratorDataSize > imageToDecorateDataSize) { 
			return decoratorDataSize - imageToDecorateDataSize; 
		}

		return decoratorDataSize;
	}

	private static class DecoratorImageListProvider implements IModelVisitor {

		AbstractNodeInterface fNodeInterface;
		boolean fIsProjectAvailable;

		public DecoratorImageListProvider(
				IFileInfoProvider fileInfoProvider, 
				boolean isProjectAvailable, 
				IModelUpdateContext modelUpdateContext) {

			fIsProjectAvailable = isProjectAvailable;

			fNodeInterface = new AbstractNodeInterface(modelUpdateContext, fileInfoProvider);
		}

		@Override
		public Object visit(RootNode node) throws Exception {

			return createDecoratorList(node, fNodeInterface, fIsProjectAvailable);
		}

		@Override
		public Object visit(ClassNode node) throws Exception {

			return createDecoratorList(node, fNodeInterface, fIsProjectAvailable);
		}

		@Override
		public Object visit(MethodNode node) throws Exception {

			return createDecoratorList(node, fNodeInterface, fIsProjectAvailable);
		}

		@Override
		public Object visit(MethodParameterNode node) throws Exception {

			List<Image> decorators = new ArrayList<Image>();

			decorators.add(getDecoratorForImplementationStatus(node, fNodeInterface, fIsProjectAvailable));

			if (node.isExpected()) {
				decorators.add(getImageFromManager("expected.png"));
			}

			if (node.isLinked()) {
				decorators.add(getImageFromManager("linked.png"));
			}

			return decorators;
		}

		@Override
		public Object visit(GlobalParameterNode node) throws Exception {

			List<Image> decorators = new ArrayList<Image>();

			decorators.add(getDecoratorForImplementationStatus(node, fNodeInterface, fIsProjectAvailable));
			decorators.add(getImageFromManager("global.png"));

			return decorators;
		}

		@Override
		public Object visit(TestCaseNode node) throws Exception {

			return createDecoratorList(node, fNodeInterface, fIsProjectAvailable);
		}

		@Override
		public Object visit(ConstraintNode node) throws Exception {

			return createDecoratorList(node, fNodeInterface, fIsProjectAvailable);
		}

		@Override
		public Object visit(ChoiceNode node) throws Exception {

			List<Image> decorators = new ArrayList<Image>();

			decorators.add(getDecoratorForImplementationStatus(node, fNodeInterface, fIsProjectAvailable));

			if(node.isAbstract()){
				decorators.add(getImageFromManager("abstract.png"));
			}

			return decorators;
		}

		private List<Image> createDecoratorList(
				AbstractNode abstractNode,
				AbstractNodeInterface nodeInterface,
				boolean isProjectAvailable) {

			List<Image> images = new ArrayList<Image>();

			images.add(getDecoratorForImplementationStatus(abstractNode, nodeInterface, isProjectAvailable));

			return images;
		}

		private static Image getDecoratorForImplementationStatus(
				AbstractNode node, 
				AbstractNodeInterface nodeInterface, 
				boolean isProjectAvailable) {

			if (!isProjectAvailable) {
				return null;
			}

			switch (nodeInterface.getImplementationStatus(node)) {

			case IMPLEMENTED:
				return getImageFromManager("implemented.png");

			case PARTIALLY_IMPLEMENTED:
				return getImageFromManager("partially_implemented.png");

			case NOT_IMPLEMENTED:
				return getImageFromManager("unimplemented.png");

			case IRRELEVANT:
			default:
				return null;
			}
		}

		private static Image getImageFromManager(String file) {
			return ImageManager.getInstance().getImage(file);
		}

	}

}
