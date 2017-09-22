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

import org.eclipse.swt.SWT;
import org.eclipse.ui.actions.ActionFactory;

import com.ecfeed.core.utils.SystemHelper;

class Names {
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
}

public enum ActionDescriptionProvider {

	COPY(Names.COPY_ACTION_ID, 
			Names.COPY_ACTION_NAME, 
			Names.COPY_ACTION_SHORTCUT, 
			Names.COPY_ACTION_MAC_SHORTCUT, 
			'c',
			SWT.CTRL,
			SWT.COMMAND),

	CUT(Names.CUT_ACTION_ID, 
			Names.CUT_ACTION_NAME, 
			Names.CUT_ACTION_SHORTCUT, 
			Names.CUT_ACTION_MAC_SHORTCUT, 
			'x',
			SWT.CTRL,
			SWT.COMMAND),

	PASTE(Names.PASTE_ACTION_ID, 
			Names.PASTE_ACTION_NAME, 
			Names.PASTE_ACTION_SHORTCUT, 
			Names.PASTE_ACTION_MAC_SHORTCUT, 
			'v',
			SWT.CTRL,
			SWT.COMMAND),

	INSERT(Names.INSERT_ACTION_ID, 
			Names.INSERT_ACTION_NAME, 
			Names.INSERT_ACTION_SHORTCUT, 
			Names.INSERT_ACTION_MAC_SHORTCUT, 
			'i',
			SWT.CTRL,
			SWT.COMMAND),

	DELETE(Names.DELETE_ACTION_ID, 
			Names.DELETE_ACTION_NAME, 
			Names.DELETE_ACTION_SHORTCUT, 
			Names.DELETE_ACTION_MAC_SHORTCUT, 
			'd',
			SWT.CTRL,
			SWT.COMMAND),

	SELECT_ALL(Names.SELECT_ALL_ACTION_ID, 
			Names.SELECT_ALL_ACTION_NAME, 
			Names.SELECT_ALL_ACTION_SHORTCUT, 
			Names.SELECT_ALL_ACTION_MAC_SHORTCUT, 
			'a',
			SWT.CTRL,
			SWT.COMMAND),

	UNDO(Names.UNDO_ACTION_ID, 
			Names.UNDO_ACTION_NAME, 
			Names.UNDO_ACTION_SHORTCUT, 
			Names.UNDO_ACTION_MAC_SHORTCUT, 
			'z',
			SWT.CTRL,
			SWT.COMMAND),

	REDO(Names.REDO_ACTION_ID, 
			Names.REDO_ACTION_NAME, 
			Names.REDO_ACTION_SHORTCUT, 
			Names.REDO_ACTION_MAC_SHORTCUT, 
			'z',
			SWT.CTRL | SWT.SHIFT,
			SWT.COMMAND | SWT.SHIFT),

	SAVE(Names.SAVE_ACTION_ID, 
			Names.SAVE_ACTION_NAME, 
			Names.SAVE_ACTION_SHORTCUT, 
			Names.SAVE_ACTION_MAC_SHORTCUT,
			's', 
			SWT.CTRL,
			SWT.COMMAND),

	MOVE_UP(Names.MOVE_UP_ACTION_ID, 
			Names.MOVE_UP_ACTION_NAME, 
			Names.MOVE_UP_ACTION_SHORTCUT, 
			Names.MOVE_UP_ACTION_SHORTCUT, 
			SWT.ARROW_UP,
			SWT.ALT,
			SWT.ALT),

	MOVE_DOWN(Names.MOVE_DOWN_ACTION_ID, 
			Names.MOVE_DOWN_ACTION_NAME, 
			Names.MOVE_DOWN_ACTION_SHORTCUT, 
			Names.MOVE_DOWN_ACTION_SHORTCUT,
			SWT.ARROW_DOWN,
			SWT.ALT,
			SWT.ALT),

	EXPAND(Names.EXPAND_ACTION_ID, 
			Names.EXPAND_ACTION_NAME, 
			Names.EXPAND_ACTION_SHORTCUT, 
			Names.EXPAND_ACTION_MAC_SHORTCUT, 
			SWT.NONE,
			SWT.CTRL,
			SWT.COMMAND),

	COLLAPSE(Names.COLLAPSE_ACTION_ID, 
			Names.COLLAPSE_ACTION_NAME, 
			Names.COLLAPSE_ACTION_SHORTCUT, 
			Names.COLLAPSE_ACTION_MAC_SHORTCUT, 
			SWT.NONE,
			SWT.NONE,
			SWT.NONE),

	EXPAND_COLLAPSE(Names.EXPAND_COLLAPSE_ACTION_ID, 
			Names.EXPAND_COLLAPSE_ACTION_NAME, 
			Names.EXPAND_COLLAPSE_ACTION_SHORTCUT, 
			Names.EXPAND_COLLAPSE_ACTION_SHORTCUT,
			SWT.NONE,
			SWT.NONE,
			SWT.NONE),

	ABOUT(Names.ABOUT_ACTION_ID, 
			Names.ABOUT_ACTION_NAME, 
			null, 
			null,
			SWT.NONE,
			SWT.NONE,
			SWT.NONE),

	CHECK_FOR_UPDATES(Names.CHECK_FOR_UPDATES_ACTION_ID, 
			Names.CHECK_FOR_UPDATES_ACTION_NAME, 
			null, 
			null,
			SWT.NONE,
			SWT.NONE,
			SWT.NONE);

	private String fId;
	private String fName;
	private String fShortcut;
	private String fMacShortcut;
	private int fKeyCode;
	private int fModifier;
	private int fMacModifier;

	ActionDescriptionProvider(
			String actionId, 
			String actionName, 
			String shortcut, 
			String macShortcut, 
			int keyCode, 
			int modifier,
			int macModifier) {

		fId = actionId;
		fName = actionName;
		fShortcut = shortcut;
		fMacShortcut = macShortcut;
		fKeyCode = keyCode;
		fModifier = modifier;
		fMacModifier = macModifier;
	}

	public String getDescription() {

		String shortcut = getShortcut();

		if (shortcut == null) {
			return fName;
		}

		if (SystemHelper.isOperatingSystemMacOs()) {
			return fName + "  (" + shortcut + ")";	
		}
		return fName + "\t" + shortcut;
	}		

	public String getId() {
		return fId;
	}

	public String getShortcut() {

		if (SystemHelper.isOperatingSystemMacOs()) {
			return fMacShortcut;
		}

		return fShortcut;
	}

	public int getKeyCode() {

		return fKeyCode;
	}

	public int getModifier() {

		if (SystemHelper.isOperatingSystemMacOs()) {
			return fMacModifier;
		}

		return fModifier;
	}

}