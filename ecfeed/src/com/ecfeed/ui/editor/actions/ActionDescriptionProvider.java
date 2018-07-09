/*******************************************************************************
 *
 * Copyright (c) 2016 ecFeed AS.                                                
 * All rights reserved. This program and the accompanying materials              
 * are made available under the terms of the Eclipse Public License v1.0         
 * which accompanies this distribution, and is available at                      
 * http://www.eclipse.org/legal/epl-v10.html 
 *  
 *******************************************************************************/

package com.ecfeed.ui.editor.actions;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.plugin.AbstractUIPlugin;

import com.ecfeed.core.utils.SystemHelper;


public class ActionDescriptionProvider {

	private static final String COPY_ACTION_ID = ActionFactory.COPY.getId();
	private static final String CUT_ACTION_ID = ActionFactory.CUT.getId();
	private static final String PASTE_ACTION_ID = ActionFactory.PASTE.getId();
	private static final String INSERT_ACTION_ID = "insert";
	private static final String DELETE_ACTION_ID = ActionFactory.DELETE.getId();
	private static final String SELECT_ALL_ACTION_ID = ActionFactory.SELECT_ALL.getId();
	private static final String UNDO_ACTION_ID = ActionFactory.UNDO.getId();
	private static final String REDO_ACTION_ID = ActionFactory.REDO.getId();
	private static final String MOVE_UP_ACTION_ID = "moveUp";
	private static final String MOVE_DOWN_ACTION_ID = "moveDown";
	private static final String EXPAND_ACTION_ID = "expand";
	private static final String COLLAPSE_ACTION_ID = "collapse";
	private static final String EXPAND_COLLAPSE_ACTION_ID = "expand/collapse";
	private static final String SAVE_ACTION_ID = "save";
	private static final String ABOUT_ACTION_ID = "about";
	private static final String CHECK_FOR_UPDATES_ACTION_ID = "checkForUpdates";

	private static final String COPY_ACTION_NAME = "Copy";
	private static final String COPY_ACTION_SHORTCUT = "Ctrl+C";
	private static final String COPY_ACTION_MAC_SHORTCUT = "Cmd+C";

	private static final String CUT_ACTION_NAME = "Cut";
	private static final String CUT_ACTION_SHORTCUT = "Ctrl+X";
	private static final String CUT_ACTION_MAC_SHORTCUT = "Cmd+X";

	private static final String PASTE_ACTION_NAME = "Paste";
	private static final String PASTE_ACTION_SHORTCUT = "Ctrl+V";
	private static final String PASTE_ACTION_MAC_SHORTCUT = "Cmd+V";

	private static final String INSERT_ACTION_NAME = "Insert";
	private static final String INSERT_ACTION_SHORTCUT = "Ins";
	private static final String INSERT_ACTION_MAC_SHORTCUT = "Ins";

	private static final String DELETE_ACTION_NAME = "Delete";
	private static final String DELETE_ACTION_SHORTCUT = "Del";
	private static final String DELETE_ACTION_MAC_SHORTCUT = "Del";

	private static final String SELECT_ALL_ACTION_NAME = "Select All";
	private static final String SELECT_ALL_ACTION_SHORTCUT = "Ctrl+A";
	private static final String SELECT_ALL_ACTION_MAC_SHORTCUT = "Cmd+A";

	private static final String UNDO_ACTION_NAME = "Undo";
	private static final String UNDO_ACTION_SHORTCUT = "Ctrl+Z";
	private static final String UNDO_ACTION_MAC_SHORTCUT = "Cmd+Z";

	private static final String REDO_ACTION_NAME = "Redo";
	private static final String REDO_ACTION_SHORTCUT = "Ctrl+Shift+Z";
	private static final String REDO_ACTION_MAC_SHORTCUT = "Cmd+Shift+Z";

	private static final String MOVE_UP_ACTION_NAME = "Move Up";
	private static final String MOVE_UP_ACTION_SHORTCUT = "Ctrl+Up";
	private static final String MOVE_UP_ACTION_MAC_SHORTCUT = "Cmd+Up";

	private static final String MOVE_DOWN_ACTION_NAME = "Move Down";
	private static final String MOVE_DOWN_ACTION_SHORTCUT = "Ctrl+Down";
	private static final String MOVE_DOWN_ACTION_MAC_SHORTCUT = "Cmd+Down";

	private static final String EXPAND_ACTION_NAME = "Expand";
	private static final String EXPAND_ACTION_SHORTCUT = "Ctrl+Shift+E";
	private static final String EXPAND_ACTION_MAC_SHORTCUT = "Cmd+Shift+E";

	private static final String COLLAPSE_ACTION_NAME = "Collapse";
	private static final String COLLAPSE_ACTION_SHORTCUT = "Ctrl+Shift+W";
	private static final String COLLAPSE_ACTION_MAC_SHORTCUT = "Cmd+Shift+W";

	private static final String EXPAND_COLLAPSE_ACTION_NAME = "Expand/Collapse";
	private static final String EXPAND_COLLAPSE_ACTION_SHORTCUT = "Space";

	private static final String SAVE_ACTION_NAME = "Save";
	private static final String SAVE_ACTION_SHORTCUT = "Ctrl+S";
	private static final String SAVE_ACTION_MAC_SHORTCUT = "Cmd+S";

	private static final String ABOUT_ACTION_NAME = "About ecFeed...";
	private static final String CHECK_FOR_UPDATES_ACTION_NAME = "Check for updates...";

	private static final char SPACE = ' ';

	private static class ActionDescription {

		private ActionId fId;
		private String fStrId;
		private String fName;
		private String fShortcut;
		private String fMacShortcut;
		private int fKeyCode;
		private int fKeyModifier;
		private int fMacKeyModifier;
		private ImageDescriptor fImageDescriptor;

		ActionDescription(
				ActionId id,
				String strId,
				String name,
				String shortcut,
				String macShortcut,
				int keyCode,
				int modifier,
				int macModifier,
				ImageDescriptor imageDescriptor) {

			fId = id;
			fStrId = strId;
			fName = name;
			fShortcut = shortcut;
			fMacShortcut = macShortcut;
			fKeyCode = keyCode;
			fKeyModifier = modifier;
			fMacKeyModifier = macModifier;
			fImageDescriptor = imageDescriptor;
		}

	}

	private static class ActionDescriptionShort extends ActionDescription{

		ActionDescriptionShort(ActionId id, String strId, String name) {
			super(id, strId, name, null, null, SWT.NONE, SWT.NONE, SWT.NONE, null);
		}

	}

	private static ActionDescriptionProvider fApplicationInstance = null;

	private List<ActionDescription> fActionDescriptions;

	public static ActionDescriptionProvider getApplicationInstance() {

		if (fApplicationInstance == null) {
			fApplicationInstance = new ActionDescriptionProvider();
		}

		return fApplicationInstance;
	}

	private static ImageDescriptor getImageDescriptor(String imageFilePath) {
		return AbstractUIPlugin.imageDescriptorFromPlugin( "org.eclipse.rap.demo", imageFilePath );
	}

	ActionDescriptionProvider() {
		fActionDescriptions = new ArrayList<ActionDescription>();

		fActionDescriptions.add(
				new ActionDescription(
						ActionId.COPY,
						COPY_ACTION_ID, 
						COPY_ACTION_NAME, 
						COPY_ACTION_SHORTCUT, 
						COPY_ACTION_MAC_SHORTCUT, 
						'c',
						SWT.CTRL,
						SWT.COMMAND,
						getImageDescriptor("icons/copy.png")));

		fActionDescriptions.add(
				new ActionDescription(
						ActionId.CUT,
						CUT_ACTION_ID, 
						CUT_ACTION_NAME, 
						CUT_ACTION_SHORTCUT, 
						CUT_ACTION_MAC_SHORTCUT, 
						'x',
						SWT.CTRL,
						SWT.COMMAND,
						null));

		fActionDescriptions.add(
				new ActionDescription(
						ActionId.PASTE,
						PASTE_ACTION_ID, 
						PASTE_ACTION_NAME, 
						PASTE_ACTION_SHORTCUT, 
						PASTE_ACTION_MAC_SHORTCUT, 
						'v',
						SWT.CTRL,
						SWT.COMMAND,
						getImageDescriptor("icons/paste.png")));

		fActionDescriptions.add(
				new ActionDescription(
						ActionId.INSERT,
						INSERT_ACTION_ID, 
						INSERT_ACTION_NAME, 
						INSERT_ACTION_SHORTCUT, 
						INSERT_ACTION_MAC_SHORTCUT, 
						SWT.INSERT,
						SWT.NONE,
						SWT.NONE,
						null));

		fActionDescriptions.add(
				new ActionDescription(
						ActionId.DELETE,
						DELETE_ACTION_ID, 
						DELETE_ACTION_NAME, 
						DELETE_ACTION_SHORTCUT, 
						DELETE_ACTION_MAC_SHORTCUT, 
						SWT.DEL,
						SWT.NONE,
						SWT.NONE,
						null));

		fActionDescriptions.add(
				new ActionDescription(
						ActionId.SELECT_ALL,
						SELECT_ALL_ACTION_ID, 
						SELECT_ALL_ACTION_NAME, 
						SELECT_ALL_ACTION_SHORTCUT, 
						SELECT_ALL_ACTION_MAC_SHORTCUT, 
						'a',
						SWT.CTRL,
						SWT.COMMAND,
						null));

		fActionDescriptions.add(
				new ActionDescription(
						ActionId.UNDO,
						UNDO_ACTION_ID, 
						UNDO_ACTION_NAME, 
						UNDO_ACTION_SHORTCUT, 
						UNDO_ACTION_MAC_SHORTCUT, 
						'z',
						SWT.CTRL,
						SWT.COMMAND,
						getImageDescriptor("icons/undo.png")));


		fActionDescriptions.add(
				new ActionDescription(
						ActionId.REDO,
						REDO_ACTION_ID, 
						REDO_ACTION_NAME, 
						REDO_ACTION_SHORTCUT, 
						REDO_ACTION_MAC_SHORTCUT, 
						'z',
						SWT.CTRL | SWT.SHIFT,
						SWT.COMMAND | SWT.SHIFT,
						getImageDescriptor("icons/redo.png")));		

		fActionDescriptions.add(
				new ActionDescription(
						ActionId.SAVE,
						SAVE_ACTION_ID, 
						SAVE_ACTION_NAME, 
						SAVE_ACTION_SHORTCUT, 
						SAVE_ACTION_MAC_SHORTCUT,
						's', 
						SWT.CTRL,
						SWT.COMMAND,
						getImageDescriptor("icons/save.png")));

		fActionDescriptions.add(
				new ActionDescription(
						ActionId.MOVE_UP,
						MOVE_UP_ACTION_ID, 
						MOVE_UP_ACTION_NAME, 
						MOVE_UP_ACTION_SHORTCUT, 
						MOVE_UP_ACTION_MAC_SHORTCUT, 
						SWT.ARROW_UP,
						SWT.CTRL,
						SWT.COMMAND,
						null));

		fActionDescriptions.add(
				new ActionDescription(
						ActionId.MOVE_DOWN,
						MOVE_DOWN_ACTION_ID, 
						MOVE_DOWN_ACTION_NAME, 
						MOVE_DOWN_ACTION_SHORTCUT, 
						MOVE_DOWN_ACTION_MAC_SHORTCUT,
						SWT.ARROW_DOWN,
						SWT.CTRL,
						SWT.COMMAND,
						null));

		fActionDescriptions.add(
				new ActionDescription(
						ActionId.EXPAND,
						EXPAND_ACTION_ID, 
						EXPAND_ACTION_NAME, 
						EXPAND_ACTION_SHORTCUT, 
						EXPAND_ACTION_MAC_SHORTCUT, 
						SWT.NONE,
						SWT.CTRL,
						SWT.COMMAND,
						null));		

		fActionDescriptions.add(
				new ActionDescription(
						ActionId.COLLAPSE,
						COLLAPSE_ACTION_ID, 
						COLLAPSE_ACTION_NAME, 
						COLLAPSE_ACTION_SHORTCUT, 
						COLLAPSE_ACTION_MAC_SHORTCUT, 
						SWT.NONE,
						SWT.NONE,
						SWT.NONE,
						null));

		fActionDescriptions.add(
				new ActionDescription(
						ActionId.EXPAND_COLLAPSE,
						EXPAND_COLLAPSE_ACTION_ID, 
						EXPAND_COLLAPSE_ACTION_NAME, 
						EXPAND_COLLAPSE_ACTION_SHORTCUT, 
						EXPAND_COLLAPSE_ACTION_SHORTCUT,
						SPACE,
						SWT.NONE,
						SWT.NONE,
						null));

		fActionDescriptions.add(
				new ActionDescription(
						ActionId.ABOUT,
						ABOUT_ACTION_ID, 
						ABOUT_ACTION_NAME,
						null, 
						null,
						SWT.NONE,
						SWT.NONE,
						SWT.NONE,
						getImageDescriptor("icons/help.gif")));

		fActionDescriptions.add(
				new ActionDescription(
						ActionId.IMPORT_MODEL,
						"importModel", 
						"Import model",
						null, 
						null,
						SWT.NONE,
						SWT.NONE,
						SWT.NONE,
						getImageDescriptor("icons/help.gif")));

		fActionDescriptions.add(
				new ActionDescription(
						ActionId.EXPORT_MODEL,
						"exportModel", 
						"Export model",
						null, 
						null,
						SWT.NONE,
						SWT.NONE,
						SWT.NONE,
						getImageDescriptor("icons/help.gif")));

		fActionDescriptions.add(
				new ActionDescriptionShort(
						ActionId.CHECK_FOR_UPDATES,
						CHECK_FOR_UPDATES_ACTION_ID, 
						CHECK_FOR_UPDATES_ACTION_NAME));		

		fActionDescriptions.add(
				new ActionDescriptionShort(
						ActionId.IMPLEMENT,
						"implement", 
						"Implement"));

		fActionDescriptions.add(
				new ActionDescriptionShort(
						ActionId.TEST_ONLINE,
						"testOnline", 
						"Test online"));		

		fActionDescriptions.add(
				new ActionDescriptionShort(
						ActionId.EXECUTE_TEST_CASE,
						"execute", 
						"Execute"));

		fActionDescriptions.add(
				new ActionDescriptionShort(
						ActionId.EXPORT_ONLINE,
						"exportOnline", 
						"Export online"));		

		fActionDescriptions.add(
				new ActionDescriptionShort(
						ActionId.GO_TO_IMPLEMENTATION,
						"goToImpl", 
						"Go to implementation"));		

		fActionDescriptions.add(
				new ActionDescriptionShort(
						ActionId.ADD_GLOBAL_PARAMETER,
						"addGlobalParameter", 
						"Add global parameter"));		

		fActionDescriptions.add(
				new ActionDescriptionShort(
						ActionId.ADD_CLASS,
						"addClass", 
						"Add class"));

		fActionDescriptions.add(
				new ActionDescriptionShort(
						ActionId.ADD_METHOD,
						"addMethod", 
						"Add method"));		

		fActionDescriptions.add(
				new ActionDescriptionShort(
						ActionId.ADD_METHOD_PARAMETER,
						"addMethodParameter", 
						"Add parameter"));		

		fActionDescriptions.add(
				new ActionDescriptionShort(
						ActionId.ADD_CHOICE,
						"addChoice", 
						"Add choice"));

		fActionDescriptions.add(
				new ActionDescriptionShort(
						ActionId.ADD_CONSTRAINT,
						"addConstraint", 
						"Add constraint"));	


		fActionDescriptions.add(
				new ActionDescriptionShort(
						ActionId.ADD_TEST_CASE,
						"addTestCase", 
						"Add test case"));	

		fActionDescriptions.add(
				new ActionDescriptionShort(
						ActionId.ADD_TEST_SUITE,
						"generateTestSuite", 
						"Generate test suite"));	
	}

	private ActionDescription getDescriptionRecord(ActionId actionId) {

		for (ActionDescription actionDescription : fActionDescriptions) {
			if (actionDescription.fId == actionId) {
				return actionDescription;
			}
		}

		return null;
	}

	public String getStrId(ActionId actionId) {

		return getDescriptionRecord(actionId).fStrId;
	}

	public String getName(ActionId actionId) {

		return getDescriptionRecord(actionId).fName;
	}	

	public String getDescriptionWithShortcut(ActionId actionId) {

		ActionDescription actionDescription = getDescriptionRecord(actionId);

		String shortcut = getShortcut(actionDescription);

		if (shortcut == null) {
			return actionDescription.fName;
		}

		if (SystemHelper.isOperatingSystemMacOs()) {
			return actionDescription.fName + "  (" + shortcut + ")";	
		}

		return actionDescription.fName + "\t" + shortcut;
	}

	public int getKeyCode(ActionId actionId) {

		return getDescriptionRecord(actionId).fKeyCode;		
	}

	public int getKeyModifier(ActionId actionId) {

		if (SystemHelper.isOperatingSystemMacOs()) {
			return getDescriptionRecord(actionId).fMacKeyModifier;
		}

		return getDescriptionRecord(actionId).fKeyModifier;
	}

	public String getShortcut(ActionId actionId) {

		return getShortcut(getDescriptionRecord(actionId));
	}

	public ImageDescriptor getImageDescriptor(ActionId actionId) {
		return getDescriptionRecord(actionId).fImageDescriptor;
	}

	private String getShortcut(ActionDescription actionDescription) {

		if (SystemHelper.isOperatingSystemMacOs()) {
			return actionDescription.fMacShortcut;
		}

		return actionDescription.fShortcut;
	}

}

