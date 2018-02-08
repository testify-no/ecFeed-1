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

public class ModelLabelDecoratorHelper  {

	public static Image decorateImageOfAbstractNode(
			Image imageToDecorate, 
			AbstractNode abstractNode,
			ModelMasterSection modelMasterSection,
			IFileInfoProvider fileInfoProvider,
			Map<List<Image>, Image> decoratedImages) {

		List<Image> decorators = 
				ModelLabelDecoratorHelper.getDecoratorsForNode(
						abstractNode, modelMasterSection, fileInfoProvider);

		if (decorators == null) {
			return imageToDecorate;
		}

		return ModelLabelDecoratorHelper.getOrCreateDecoratedImage(
				imageToDecorate, decorators, decoratedImages);
	}


	@SuppressWarnings("unchecked")
	private static List<Image> getDecoratorsForNode(
			AbstractNode abstractNode, 
			ModelMasterSection modelMasterSection,
			IFileInfoProvider fileInfoProvider) {

		Object result = null;

		try {
			result =
					abstractNode.accept(
							new DecoratorImageListProvider(
									fileInfoProvider, 
									fileInfoProvider.isProjectAvailable(),
									modelMasterSection));
		} catch (Exception e) {
			SystemLogger.logCatch(e.getMessage());
			return null;
		}

		return (List<Image>)result;
	}

	private static Image getOrCreateDecoratedImage(
			Image imageToDecorate, 
			List<Image> decorators, 
			Map<List<Image>, Image> decoratedImages) {

		List<Image> decoratedImageKey = createDecoratedImageKey(imageToDecorate, decorators);

		if (decoratedImages.containsKey(decoratedImageKey)) {
			return decoratedImages.get(decorators);
		}

		Image decoratedImage = createDecoratedImage(imageToDecorate, decorators);

		decoratedImages.put(decorators, decoratedImage);

		return decoratedImage;
	}

	private static Image createDecoratedImage(Image imageToDecorate, List<Image> decorators) {

		Image decoratedImage = new Image(Display.getCurrent(), imageToDecorate.getImageData());

		for (Image decorator : decorators) {

			if (decorator != null) {
				decoratedImage = aplyOneDecorator(decoratedImage, decorator);
			}
		}

		return decoratedImage;
	}

	private static List<Image> createDecoratedImageKey(Image image, List<Image> decorators) {

		List<Image> imageKey = new ArrayList<Image>(decorators);
		imageKey.add(0, image);

		return imageKey;
	}

	private static Image aplyOneDecorator(
			Image imageToDecorate, 
			Image decorator) {

		ImageData imageToDecorateData = (ImageData)imageToDecorate.getImageData().clone();
		ImageData decoratorData = decorator.getImageData();

		int maxCol = calculateMaxCol(imageToDecorateData, decoratorData);
		int maxRow = calculateMaxRow(imageToDecorateData, decoratorData);

		decorateImageData(imageToDecorateData, decoratorData, maxCol, maxRow);

		return new Image(Display.getDefault(), imageToDecorateData);
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
		ModelMasterSection fModelMasterSection;

		public DecoratorImageListProvider(
				IFileInfoProvider fileInfoProvider, 
				boolean isProjectAvailable, 
				ModelMasterSection modelMasterSection) {

			fIsProjectAvailable = isProjectAvailable;
			fModelMasterSection = modelMasterSection;

			fNodeInterface = new AbstractNodeInterface(fModelMasterSection, fileInfoProvider);
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
