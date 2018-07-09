/*******************************************************************************
 *
 * Copyright (c) 2016 ecFeed AS.                                                
 * All rights reserved. This program and the accompanying materials              
 * are made available under the terms of the Eclipse Public License v1.0         
 * which accompanies this distribution, and is available at                      
 * http://www.eclipse.org/legal/epl-v10.html 
 *  
 *******************************************************************************/

package com.ecfeed.ui.common;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;

import com.ecfeed.application.SessionDataStore;
import com.ecfeed.core.utils.CommonConstants;
import com.ecfeed.core.utils.SessionAttributes;

public class ImageManager {

	private Map<String, ImageDescriptor> fDescriptorCache;
	private Map<String, Image> fImageCache;

	private ImageManager(){
		fDescriptorCache = new HashMap<>();
		fImageCache = new HashMap<>();
	}

	public static ImageManager getSessionInstance() {

		ImageManager imageManager = (ImageManager)SessionDataStore.get(SessionAttributes.SA_IMAGE_MANAGER);

		if (imageManager == null) {
			imageManager = new ImageManager();
			SessionDataStore.set(SessionAttributes.SA_IMAGE_MANAGER, imageManager);
		}

		return imageManager;
	}

	public static Image getImageFromFile(String file) {

		return ImageManager.getSessionInstance().getImage(file);
	}

	public ImageDescriptor getImageDescriptor(String fileName) {

		String path = CommonConstants.ICONS_FOLDER_NAME + "/" + fileName;

		if (fDescriptorCache.containsKey(path) == false) {

			Bundle bundle = FrameworkUtil.getBundle(this.getClass());
			URL url = FileLocator.find(bundle, new Path(path), null);
			fDescriptorCache.put(path, ImageDescriptor.createFromURL(url));
		}

		return fDescriptorCache.get(path);
	}

	public Image getImage(String fileName) {

		Image imageInCache = fImageCache.get(fileName);

		if (imageInCache == null) {
			fImageCache.put(fileName, getImageDescriptor(fileName).createImage());
		}

		return fImageCache.get(fileName);
	}


}
