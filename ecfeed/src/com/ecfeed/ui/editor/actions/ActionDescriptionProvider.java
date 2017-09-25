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

import org.eclipse.swt.SWT;
import org.eclipse.ui.actions.ActionFactory;

import com.ecfeed.core.utils.SystemHelper;


public class ActionDescriptionProvider {

	public static final String COPY_ACTION_ID = ActionFactory.COPY.getId();
	public static final String CUT_ACTION_ID = ActionFactory.CUT.getId();
	public static final String PASTE_ACTION_ID = ActionFactory.PASTE.getId();
	public static final String INSERT_ACTION_ID = "insert";
	public static final String DELETE_ACTION_ID = ActionFactory.DELETE.getId();
	public static final String SELECT_ALL_ACTION_ID = ActionFactory.SELECT_ALL.getId();
	public static final String UNDO_ACTION_ID = ActionFactory.UNDO.getId();
	public static final String REDO_ACTION_ID = ActionFactory.REDO.getId();
	public static final String MOVE_UP_ACTION_ID = "moveUp";
	public static final String MOVE_DOWN_ACTION_ID = "moveDown";
	public static final String EXPAND_ACTION_ID = "expand";
	public static final String COLLAPSE_ACTION_ID = "collapse";
	public static final String EXPAND_COLLAPSE_ACTION_ID = "expand/collapse";
	public static final String JAVADOC_EXPORT_ACTION_ID = "javadoc.export";
	public static final String JAVADOC_IMPORT_ACTION_ID = "javadoc.import";
	public static final String JAVADOC_EXPORT_TYPE_ACTION_ID = "javadoc.exportType";
	public static final String JAVADOC_IMPORT_TYPE_ACTION_ID = "javadoc.importType";
	public static final String SAVE_ACTION_ID = "save";
	public static final String ABOUT_ACTION_ID = "about";
	public static final String CHECK_FOR_UPDATES_ACTION_ID = "checkForUpdates";

	public static final String COPY_ACTION_NAME = "Copy";
	public static final String COPY_ACTION_SHORTCUT = "Ctrl+C";
	public static final String COPY_ACTION_MAC_SHORTCUT = "Cmd+C";

	public static final String CUT_ACTION_NAME = "Cut";
	public static final String CUT_ACTION_SHORTCUT = "Ctrl+X";
	public static final String CUT_ACTION_MAC_SHORTCUT = "Cmd+X";

	public static final String PASTE_ACTION_NAME = "Paste";
	public static final String PASTE_ACTION_SHORTCUT = "Ctrl+V";
	public static final String PASTE_ACTION_MAC_SHORTCUT = "Cmd+V";

	public static final String INSERT_ACTION_NAME = "Insert";
	public static final String INSERT_ACTION_SHORTCUT = "Ctrl+I";
	public static final String INSERT_ACTION_MAC_SHORTCUT = "Cmd+I";

	public static final String DELETE_ACTION_NAME = "Delete";
	public static final String DELETE_ACTION_SHORTCUT = "Ctrl+D";
	public static final String DELETE_ACTION_MAC_SHORTCUT = "Cmd+D";

	public static final String SELECT_ALL_ACTION_NAME = "Select All";
	public static final String SELECT_ALL_ACTION_SHORTCUT = "Ctrl+A";
	public static final String SELECT_ALL_ACTION_MAC_SHORTCUT = "Cmd+A";

	public static final String UNDO_ACTION_NAME = "Undo";
	public static final String UNDO_ACTION_SHORTCUT = "Ctrl+Z";
	public static final String UNDO_ACTION_MAC_SHORTCUT = "Cmd+Z";

	public static final String REDO_ACTION_NAME = "Redo";
	public static final String REDO_ACTION_SHORTCUT = "Ctrl+Shift+Z";
	public static final String REDO_ACTION_MAC_SHORTCUT = "Cmd+Shift+Z";

	public static final String MOVE_UP_ACTION_NAME = "Move Up";
	public static final String MOVE_UP_ACTION_SHORTCUT = "Alt+Up";

	public static final String MOVE_DOWN_ACTION_NAME = "Move Down";
	public static final String MOVE_DOWN_ACTION_SHORTCUT = "Alt+Down";

	public static final String EXPAND_ACTION_NAME = "Expand";
	public static final String EXPAND_ACTION_SHORTCUT = "Ctrl+Shift+E";
	public static final String EXPAND_ACTION_MAC_SHORTCUT = "Cmd+Shift+E";

	public static final String COLLAPSE_ACTION_NAME = "Collapse";
	public static final String COLLAPSE_ACTION_SHORTCUT = "Ctrl+Shift+W";
	public static final String COLLAPSE_ACTION_MAC_SHORTCUT = "Cmd+Shift+W";

	public static final String EXPAND_COLLAPSE_ACTION_NAME = "Expand/Collapse";
	public static final String EXPAND_COLLAPSE_ACTION_SHORTCUT = "Space";

	public static final String JAVADOC_EXPORT_ACTION_NAME = "Export javadoc";
	public static final String JAVADOC_IMPORT_ACTION_NAME = "Import javadoc";
	public static final String JAVADOC_EXPORT_TYPE_ACTION_NAME = "javadoc.exportType";
	public static final String JAVADOC_IMPORT_TYPE_ACTION_NAME = "javadoc.importType";

	public static final String SAVE_ACTION_NAME = "Save";
	public static final String SAVE_ACTION_SHORTCUT = "Ctrl+S";
	public static final String SAVE_ACTION_MAC_SHORTCUT = "Cmd+S";

	public static final String ABOUT_ACTION_NAME = "About ecFeed...";
	public static final String CHECK_FOR_UPDATES_ACTION_NAME = "Check for updates...";

	private static class ActionDescription {

		private ActionId fId;
		private String fStrId;
		private String fName;
		private String fShortcut;
		private String fMacShortcut;
		private int fKeyCode;
		private int fModifier;
		private int fMacModifier;

		ActionDescription(
				ActionId id,
				String strId,
				String name,
				String shortcut,
				String macShortcut,
				int keyCode,
				int modifier,
				int macModifier) {

			fId = id;
			fStrId = strId;
			fName = name;
			fShortcut = shortcut;
			fMacShortcut = macShortcut;
			fKeyCode = keyCode;
			fModifier = modifier;
			fMacModifier = macModifier;
		}

	}

	private static class ActionDescriptionShort extends ActionDescription{

		ActionDescriptionShort(ActionId id, String strId, String name) {
			super(id, strId, name, null, null, SWT.NONE, SWT.NONE, SWT.NONE);
		}

	}

	private static ActionDescriptionProvider fActionDescriptionProvider = null;

	private List<ActionDescription> fActionDescriptions;

	public static ActionDescriptionProvider getInstance() {

		if (fActionDescriptionProvider == null) {
			fActionDescriptionProvider = new ActionDescriptionProvider();
		}

		return fActionDescriptionProvider;
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
						SWT.COMMAND));

		fActionDescriptions.add(
				new ActionDescription(
						ActionId.CUT,
						CUT_ACTION_ID, 
						CUT_ACTION_NAME, 
						CUT_ACTION_SHORTCUT, 
						CUT_ACTION_MAC_SHORTCUT, 
						'x',
						SWT.CTRL,
						SWT.COMMAND));

		fActionDescriptions.add(
				new ActionDescription(
						ActionId.PASTE,
						PASTE_ACTION_ID, 
						PASTE_ACTION_NAME, 
						PASTE_ACTION_SHORTCUT, 
						PASTE_ACTION_MAC_SHORTCUT, 
						'v',
						SWT.CTRL,
						SWT.COMMAND));

		fActionDescriptions.add(
				new ActionDescription(
						ActionId.INSERT,
						INSERT_ACTION_ID, 
						INSERT_ACTION_NAME, 
						INSERT_ACTION_SHORTCUT, 
						INSERT_ACTION_MAC_SHORTCUT, 
						'i',
						SWT.CTRL,
						SWT.COMMAND));

		fActionDescriptions.add(
				new ActionDescription(
						ActionId.DELETE,
						DELETE_ACTION_ID, 
						DELETE_ACTION_NAME, 
						DELETE_ACTION_SHORTCUT, 
						DELETE_ACTION_MAC_SHORTCUT, 
						'd',
						SWT.CTRL,
						SWT.COMMAND));

		fActionDescriptions.add(
				new ActionDescription(
						ActionId.SELECT_ALL,
						SELECT_ALL_ACTION_ID, 
						SELECT_ALL_ACTION_NAME, 
						SELECT_ALL_ACTION_SHORTCUT, 
						SELECT_ALL_ACTION_MAC_SHORTCUT, 
						'a',
						SWT.CTRL,
						SWT.COMMAND));

		fActionDescriptions.add(
				new ActionDescription(
						ActionId.UNDO,
						UNDO_ACTION_ID, 
						UNDO_ACTION_NAME, 
						UNDO_ACTION_SHORTCUT, 
						UNDO_ACTION_MAC_SHORTCUT, 
						'z',
						SWT.CTRL,
						SWT.COMMAND));


		fActionDescriptions.add(
				new ActionDescription(
						ActionId.REDO,
						REDO_ACTION_ID, 
						REDO_ACTION_NAME, 
						REDO_ACTION_SHORTCUT, 
						REDO_ACTION_MAC_SHORTCUT, 
						'z',
						SWT.CTRL | SWT.SHIFT,
						SWT.COMMAND | SWT.SHIFT));		

		fActionDescriptions.add(
				new ActionDescription(
						ActionId.SAVE,
						SAVE_ACTION_ID, 
						SAVE_ACTION_NAME, 
						SAVE_ACTION_SHORTCUT, 
						SAVE_ACTION_MAC_SHORTCUT,
						's', 
						SWT.CTRL,
						SWT.COMMAND));

		fActionDescriptions.add(
				new ActionDescription(
						ActionId.MOVE_UP,
						MOVE_UP_ACTION_ID, 
						MOVE_UP_ACTION_NAME, 
						MOVE_UP_ACTION_SHORTCUT, 
						MOVE_UP_ACTION_SHORTCUT, 
						SWT.ARROW_UP,
						SWT.ALT,
						SWT.ALT));

		fActionDescriptions.add(
				new ActionDescription(
						ActionId.MOVE_DOWN,
						MOVE_DOWN_ACTION_ID, 
						MOVE_DOWN_ACTION_NAME, 
						MOVE_DOWN_ACTION_SHORTCUT, 
						MOVE_DOWN_ACTION_SHORTCUT,
						SWT.ARROW_DOWN,
						SWT.ALT,
						SWT.ALT));

		fActionDescriptions.add(
				new ActionDescription(
						ActionId.EXPAND,
						EXPAND_ACTION_ID, 
						EXPAND_ACTION_NAME, 
						EXPAND_ACTION_SHORTCUT, 
						EXPAND_ACTION_MAC_SHORTCUT, 
						SWT.NONE,
						SWT.CTRL,
						SWT.COMMAND));		

		fActionDescriptions.add(
				new ActionDescription(
						ActionId.COLLAPSE,
						COLLAPSE_ACTION_ID, 
						COLLAPSE_ACTION_NAME, 
						COLLAPSE_ACTION_SHORTCUT, 
						COLLAPSE_ACTION_MAC_SHORTCUT, 
						SWT.NONE,
						SWT.NONE,
						SWT.NONE));

		fActionDescriptions.add(
				new ActionDescription(
						ActionId.EXPAND_COLLAPSE,
						EXPAND_COLLAPSE_ACTION_ID, 
						EXPAND_COLLAPSE_ACTION_NAME, 
						EXPAND_COLLAPSE_ACTION_SHORTCUT, 
						EXPAND_COLLAPSE_ACTION_SHORTCUT,
						SWT.NONE,
						SWT.NONE,
						SWT.NONE));

		fActionDescriptions.add(
				new ActionDescriptionShort(
						ActionId.ABOUT,
						ABOUT_ACTION_ID, 
						ABOUT_ACTION_NAME));

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

	public int getModifier(ActionId actionId) {

		if (SystemHelper.isOperatingSystemMacOs()) {
			return getDescriptionRecord(actionId).fMacModifier;
		}

		return getDescriptionRecord(actionId).fModifier;
	}

	public String getShortcut(ActionId actionId) {

		return getShortcut(getDescriptionRecord(actionId));
	}

	private String getShortcut(ActionDescription actionDescription) {

		if (SystemHelper.isOperatingSystemMacOs()) {
			return actionDescription.fMacShortcut;
		}

		return actionDescription.fShortcut;
	}

}



