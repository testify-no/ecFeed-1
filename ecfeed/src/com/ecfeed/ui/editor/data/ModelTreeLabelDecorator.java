/*******************************************************************************
 *
 * Copyright (c) 2016 ecFeed AS.                                                
 * All rights reserved. This program and the accompanying materials              
 * are made available under the terms of the Eclipse Public License v1.0         
 * which accompanies this distribution, and is available at                      
 * http://www.eclipse.org/legal/epl-v10.html 
 *  
 *******************************************************************************/

package com.ecfeed.ui.editor.data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.viewers.ILabelDecorator;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.widgets.Display;

import com.ecfeed.application.ApplicationContext;
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
import com.ecfeed.ui.common.utils.IJavaProjectProvider;
import com.ecfeed.ui.modelif.AbstractNodeInterface;
import com.ecfeed.ui.modelif.IModelUpdateContext;

public class ModelTreeLabelDecorator implements ILabelDecorator {

	private IJavaProjectProvider fJavaProjectProvider;
	private Map<List<Image>, Image> fFusedImages;
	IModelUpdateContext fUpdateContext;

	public ModelTreeLabelDecorator(
			IModelUpdateContext updateContext, 
			IJavaProjectProvider javaProjectProvider) {

		fUpdateContext = updateContext;
		fJavaProjectProvider = javaProjectProvider;
		fFusedImages = new HashMap<List<Image>, Image>();
	}

	@SuppressWarnings("unchecked")
	@Override
	public Image decorateImage(Image image, Object element) {
		if(!(element instanceof AbstractNode)){
			return image;
		}

		try {
			List<Image> decorations = (List<Image>)((AbstractNode)element).accept(
					new DecorationProvider(
							fUpdateContext, 
							fJavaProjectProvider, 
							ApplicationContext.isProjectAvailable()));

			List<Image> all = new ArrayList<Image>(decorations);
			all.add(0, image);
			if(fFusedImages.containsKey(all) == false){
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

		if (ApplicationContext.isApplicationTypeRemoteRap()) {
			return icon; // TODO
		}

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

		public DecorationProvider(
				IModelUpdateContext updateContext, 
				IJavaProjectProvider javaProjectProvider, 
				boolean isProjectAvailable) {

			fNodeInterface = new AbstractNodeInterface(updateContext, javaProjectProvider);
			fIsProjectAvailable = isProjectAvailable;
		}

		@Override
		public Object visit(RootNode node) throws Exception {
			return Arrays.asList(new Image[]{implementationStatusDecoration(node)});
		}

		@Override
		public Object visit(ClassNode node) throws Exception {
			return Arrays.asList(new Image[]{implementationStatusDecoration(node)});
		}

		@Override
		public Object visit(MethodNode node) throws Exception {
			return Arrays.asList(new Image[]{implementationStatusDecoration(node)});
		}

		@Override
		public Object visit(MethodParameterNode node) throws Exception {
			List<Image> decorations = new ArrayList<Image>();
			decorations.add(implementationStatusDecoration(node));
			if(node.isExpected()){
				decorations.add(ImageManager.getImageFromFile("expected.png"));
			}
			if(node.isLinked()){
				decorations.add(ImageManager.getImageFromFile("linked.png"));
			}
			return decorations;
		}

		@Override
		public Object visit(GlobalParameterNode node) throws Exception {
			List<Image> decorations = new ArrayList<Image>();
			decorations.add(implementationStatusDecoration(node));
			decorations.add(ImageManager.getImageFromFile("global.png"));
			return decorations;
		}

		@Override
		public Object visit(TestCaseNode node) throws Exception {
			return Arrays.asList(new Image[]{implementationStatusDecoration(node)});
		}

		@Override
		public Object visit(ConstraintNode node) throws Exception {
			return Arrays.asList(new Image[]{implementationStatusDecoration(node)});
		}

		@Override
		public Object visit(ChoiceNode node) throws Exception {
			List<Image> decorations = new ArrayList<Image>();
			decorations.add(implementationStatusDecoration(node));
			if(node.isAbstract()){
				decorations.add(ImageManager.getImageFromFile("abstract.png"));
			}
			return decorations;
		}

		private Image implementationStatusDecoration(AbstractNode node) {
			if (!fIsProjectAvailable) {
				return null;
			}

			switch (fNodeInterface.getImplementationStatus(node)){
			case IMPLEMENTED:
				return ImageManager.getImageFromFile("implemented.png");
			case PARTIALLY_IMPLEMENTED:
				return ImageManager.getImageFromFile("partially_implemented.png");
			case NOT_IMPLEMENTED:
				return ImageManager.getImageFromFile("unimplemented.png");
			case IRRELEVANT:
			default:
				return null;
			}
		}
	}

}
