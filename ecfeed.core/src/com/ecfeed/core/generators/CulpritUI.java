/*******************************************************************************
 *
 * Copyright (c) 2016 ecFeed AS.                                                
 * All rights reserved. This program and the accompanying materials              
 * are made available under the terms of the Eclipse Public License v1.0         
 * which accompanies this distribution, and is available at                      
 * http://www.eclipse.org/legal/epl-v10.html 
 *  
 *******************************************************************************/

package com.ecfeed.core.generators;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;

import com.ecfeed.core.generators.algorithms.DimensionedString;

public class CulpritUI {
	
	public static void main(String[] args)
	{
		run();
	}
	
	public static void run()
	{
		Display display = new Display();
		Shell shell = new Shell(display);
		shell.setSize(60, 150);
		shell.setText("Test execution report");
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 1;
		shell.setLayout(gridLayout);
		ComboBox(shell);
		createTableContents(shell);
		shell.pack();
		shell.open();
		
		while(!shell.isDisposed())
		{
			if(!display.readAndDispatch())
			{
				display.sleep();
			}
		}
		display.dispose();
	}

	private static void createTableContents(Composite composite) {

		final Table table = new Table(composite, SWT.NONE);
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		TableColumn[] column = new TableColumn[4];
		column[0] = new TableColumn(table, SWT.NONE);
		column[0].setText("Tuple");
		
		column[1] = new TableColumn(table, SWT.NONE);
		column[1].setText("occurences");
		
		column[2] = new TableColumn(table, SWT.NONE);
		column[2].setText("Fails");
		
		column[3] = new TableColumn(table, SWT.NONE);
		column[3].setText("Index");
		
		fillTable(table);
		for(int i = 0; i < column.length; i++)
		{
			column[i].pack();
		}
		
	}

	private static void fillTable(Table table) {
		table.setRedraw(false);
		for(int i = 0; i<10; i++)
		{
			TableItem item = new TableItem(table, SWT.NONE);
			item.setText("-");
		}
		table.setRedraw(true);
	}

	private static void ComboBox(Composite composite) {
		Label label = new Label(composite, SWT.NULL);
		label.setText("Select tuple size for combinatoric analysis \n");
		final Combo combo = new Combo(composite, SWT.VERTICAL | SWT.DROP_DOWN | SWT.BORDER | SWT.READ_ONLY);
		int maxParameters = 10;
		for(int i = 2; i < maxParameters; i++)
		{
			combo.add(Integer.toString(i));
		}
		
	}

	
}

