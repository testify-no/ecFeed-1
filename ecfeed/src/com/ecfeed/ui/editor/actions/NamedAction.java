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

import org.eclipse.jface.action.Action;
import org.eclipse.ui.actions.ActionFactory;

public class NamedAction extends Action {

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

	public static final String COPY_ACTION_NAME = "Copy";
	public static final String COPY_ACTION_SHORTCUT = "Ctrl+C";

	public static final String CUT_ACTION_NAME = "Cut";
	public static final String CUT_ACTION_SHORTCUT = "Ctrl+X";

	public static final String PASTE_ACTION_NAME = "Paste";
	public static final String PASTE_ACTION_SHORTCUT = "Ctrl+V";

	public static final String INSERT_ACTION_NAME = "INSERT";

	public static final String DELETE_ACTION_NAME = "Delete";
	public static final String DELETE_ACTION_SHORTCUT = "DEL";

	public static final String SELECT_ALL_ACTION_NAME = "Select All";
	public static final String SELECT_ALL_ACTION_SHORTCUT = "Select All\tCtrl+A";

	public static final String UNDO_ACTION_NAME = "Undo";
	public static final String UNDO_ACTION_SHORTCUT = "Ctrl+Z";

	public static final String REDO_ACTION_NAME = "Redo";
	public static final String REDO_ACTION_SHORTCUT = "Ctrl+Shift+Z";

	public static final String MOVE_UP_ACTION_NAME = "Move Up";
	public static final String MOVE_UP_ACTION_SHORTCUT = "Alt+Up";

	public static final String MOVE_DOWN_ACTION_NAME = "Move Down";
	public static final String MOVE_DOWN_ACTION_SHORTCUT = "Alt+Down";

	public static final String EXPAND_ACTION_NAME = "Expand";
	public static final String EXPAND_ACTION_SHORTCUT = "Ctrl+Shift+E";

	public static final String COLLAPSE_ACTION_NAME = "Collapse";
	public static final String COLLAPSE_ACTION_SHORTCUT = "Ctrl+Shift+W";

	public static final String EXPAND_COLLAPSE_ACTION_NAME = "Expand/Collapse";
	public static final String EXPAND_COLLAPSE_ACTION_SHORTCUT = "Space";

	public static final String JAVADOC_EXPORT_ACTION_NAME = "Export javadoc";
	public static final String JAVADOC_IMPORT_ACTION_NAME = "Import javadoc";
	public static final String JAVADOC_EXPORT_TYPE_ACTION_NAME = "javadoc.exportType";
	public static final String JAVADOC_IMPORT_TYPE_ACTION_NAME = "javadoc.importType";

	protected enum GlobalActions{
		COPY(COPY_ACTION_ID, COPY_ACTION_NAME, COPY_ACTION_SHORTCUT),
		CUT(CUT_ACTION_ID, CUT_ACTION_NAME, CUT_ACTION_SHORTCUT),
		PASTE(PASTE_ACTION_ID, PASTE_ACTION_NAME, PASTE_ACTION_SHORTCUT),
		INSERT(INSERT_ACTION_ID, INSERT_ACTION_NAME, null),
		DELETE(DELETE_ACTION_ID, DELETE_ACTION_NAME, DELETE_ACTION_SHORTCUT),
		SELECT_ALL(SELECT_ALL_ACTION_ID, SELECT_ALL_ACTION_NAME, SELECT_ALL_ACTION_SHORTCUT),
		UNDO(UNDO_ACTION_ID, UNDO_ACTION_NAME, UNDO_ACTION_SHORTCUT),
		REDO(REDO_ACTION_ID, REDO_ACTION_NAME, REDO_ACTION_SHORTCUT),
		MOVE_UP(MOVE_UP_ACTION_ID, MOVE_UP_ACTION_NAME, MOVE_UP_ACTION_SHORTCUT),
		MOVE_DOWN(MOVE_DOWN_ACTION_ID, MOVE_DOWN_ACTION_NAME, MOVE_DOWN_ACTION_SHORTCUT),
		EXPAND(EXPAND_ACTION_ID, EXPAND_ACTION_NAME, EXPAND_ACTION_SHORTCUT),
		COLLAPSE(COLLAPSE_ACTION_ID, COLLAPSE_ACTION_NAME, COLLAPSE_ACTION_SHORTCUT);

		private String fId;
		private String fName;
		private String fShortcut;

		GlobalActions(String actionId, String actionName, String shortcut){
			fId = actionId;
			fName = actionName;
			fShortcut = shortcut;
		}

		public String getDescription(boolean isNameWithShortcut) {
			if (isNameWithShortcut) {
				return fName + "\t" + fShortcut;
			}

			return fName;
		}		

		public String getId(){
			return fId;
		}
	}

	private final String fName;
	private final String fId;

	public NamedAction(String id, String name){
		fId = id;
		fName = name;
	}

	public String getName(){
		return fName;
	}

	@Override
	public String getId(){
		return fId;
	}
}
