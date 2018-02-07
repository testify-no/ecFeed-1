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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.viewers.ILabelDecorator;
import org.eclipse.jface.viewers.ILabelProviderListener;
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

public class ModelLabelDecorator implements ILabelDecorator {

	Map<List<Image>, Image> fFusedImages;
	IFileInfoProvider fFileInfoProvider;
	ModelMasterSection fModelMasterSection;

	public ModelLabelDecorator(
			ModelMasterSection modelMasterSection) {
		
		fFusedImages = new HashMap<List<Image>, Image>();
		fModelMasterSection = modelMasterSection;
	}
	
	public void setFileInfoProvider(IFileInfoProvider fileInfoProvider) {
		
		fFileInfoProvider = fileInfoProvider;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Image decorateImage(Image image, Object element) {

		if(!(element instanceof AbstractNode)){
			return image;
		}

		try {
			List<Image> decorations = 
					(List<Image>)((AbstractNode)element).accept(
							new DecorationProvider(
									fFileInfoProvider, 
									fFileInfoProvider.isProjectAvailable()));

			List<Image> all = new ArrayList<Image>(decorations);
			all.add(0, image);

			if (fFusedImages.containsKey(all) == false) {
				Image decorated = new Image(Display.getCurrent(), image.getImageData());
				for(Image decoration : decorations){
					if(decoration != null){
						decorated = fuseImages(decorated, decoration, 0, 0);
					}
				}
				fFusedImages.put(decorations, decorated);
			}
			return fFusedImages.get(decorations);
		} catch(Exception e) {
			SystemLogger.logCatch(e.getMessage());
		}
		return image;
	}


	@Override
	public String decorateText(String text, Object element) {
		return text;
	}

	@Override
	public void addListener(ILabelProviderListener listener) {
	}

	@Override
	public void dispose() {
	}

	@Override
	public boolean isLabelProperty(Object element, String property) {
		return false;
	}

	@Override
	public void removeListener(ILabelProviderListener listener) {
	}

	private Image fuseImages(Image icon, Image decorator, int x, int y){

		ImageData idIcon = (ImageData)icon.getImageData().clone();
		ImageData idDecorator = decorator.getImageData();
		if(idIcon.width <= x || idIcon.height <= y){
			return icon;
		}
		int rbw = (idDecorator.width + x > idIcon.width) ? (idDecorator.width + x - idIcon.width) : idDecorator.width;
		int rbh = (idDecorator.height + y > idIcon.height) ? (idDecorator.height + y - idIcon.height) : idDecorator.height;

		int indexa = y*idIcon.scanlinePad + x;
		int indexb = 0;

		for(int row = 0; row < rbh; row ++){
			for(int col = 0; col < rbw; col++){
				if(idDecorator.alphaData[indexb] < 0){
					idIcon.alphaData[indexa] = (byte)-1;
					idIcon.data[4*indexa]=idDecorator.data[4*indexb];
					idIcon.data[4*indexa+1]=idDecorator.data[4*indexb+1];
					idIcon.data[4*indexa+2]=idDecorator.data[4*indexb+2];
				}
				indexa += 1;
				indexb += 1;
			}
			indexa += x;
		}
		return new Image(Display.getDefault(), idIcon);
	}

	private class DecorationProvider implements IModelVisitor{
		AbstractNodeInterface fNodeInterface;
		boolean fIsProjectAvailable;

		public DecorationProvider(IFileInfoProvider fileInfoProvider, boolean isProjectAvailable){
			fNodeInterface = new AbstractNodeInterface(fModelMasterSection, fileInfoProvider);
			fIsProjectAvailable = isProjectAvailable;
		}

		@Override
		public Object visit(RootNode node) throws Exception {
			return createDecorationList(node);
		}

		private List<Image> createDecorationList(AbstractNode abstractNode) {

			List<Image> images = new ArrayList<Image>();
			images.add(implementationStatusDecoration(abstractNode));

			return images;
		}

		@Override
		public Object visit(ClassNode node) throws Exception {
			return createDecorationList(node);
		}

		@Override
		public Object visit(MethodNode node) throws Exception {
			return createDecorationList(node);
		}

		@Override
		public Object visit(MethodParameterNode node) throws Exception {
			List<Image> decorations = new ArrayList<Image>();
			decorations.add(implementationStatusDecoration(node));
			if(node.isExpected()){
				decorations.add(getImageFromFile("expected.png"));
			}
			if(node.isLinked()){
				decorations.add(getImageFromFile("linked.png"));
			}
			return decorations;
		}

		@Override
		public Object visit(GlobalParameterNode node) throws Exception {
			List<Image> decorations = new ArrayList<Image>();
			decorations.add(implementationStatusDecoration(node));
			decorations.add(getImageFromFile("global.png"));
			return decorations;
		}

		@Override
		public Object visit(TestCaseNode node) throws Exception {
			return createDecorationList(node);
		}

		@Override
		public Object visit(ConstraintNode node) throws Exception {
			return createDecorationList(node);
		}

		@Override
		public Object visit(ChoiceNode node) throws Exception {
			List<Image> decorations = new ArrayList<Image>();
			decorations.add(implementationStatusDecoration(node));
			if(node.isAbstract()){
				decorations.add(getImageFromFile("abstract.png"));
			}
			return decorations;
		}

		private Image implementationStatusDecoration(AbstractNode node) {

			if (!fIsProjectAvailable) {
				return null;
			}

			switch (fNodeInterface.getImplementationStatus(node)) {

			case IMPLEMENTED:
				return getImageFromFile("implemented.png");

			case PARTIALLY_IMPLEMENTED:
				return getImageFromFile("partially_implemented.png");

			case NOT_IMPLEMENTED:
				return getImageFromFile("unimplemented.png");

			case IRRELEVANT:
			default:
				return null;
			}
		}

		private Image getImageFromFile(String file) {
			return ImageManager.getInstance().getImage(file);
		}

	}

}
