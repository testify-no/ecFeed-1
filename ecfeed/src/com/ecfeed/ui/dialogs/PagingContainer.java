/*******************************************************************************
 *
 * Copyright (c) 2016 ecFeed AS.                                                
 * All rights reserved. This program and the accompanying materials              
 * are made available under the terms of the Eclipse Public License v1.0         
 * which accompanies this distribution, and is available at                      
 * http://www.eclipse.org/legal/epl-v10.html 
 *  
 *******************************************************************************/

package com.ecfeed.ui.dialogs;

import java.util.ArrayList;
import java.util.List;

public class PagingContainer<RecordType> {
	
	private List<RecordType> fRecords;
	private int fCurrentPage;
	private int fPageSize;
	
	public PagingContainer(int PageSize){
		
		fRecords = new ArrayList<RecordType>();
		fPageSize = PageSize;
		fCurrentPage = 0;
	}
	
	public List<RecordType> getCurrentPage(){
		
		List<RecordType> CurrentPage = new ArrayList<RecordType>();
		
		int start = initialPageCount();
		int count = StepCount(start);
		
		for (int index = start; index < count; index++)
		{
			CurrentPage.add(fRecords.get(index));
		}
		return CurrentPage;
	}
	
	public int StepCount(int start) {
		
		int count = 0;
		
		if(start + fPageSize < fRecords.size())
		{
			count = start + fPageSize;
		}
		else{
			count = fRecords.size();
		}
		return count;
	}

	public int initialPageCount() {
		
		int start = 0;
		
		if(fCurrentPage == 0)
		{
			start = 0;
		}
		else{
			start = fPageSize * fCurrentPage;	
		}
		return start;
	}

	public boolean hasNextPage(){
		
		if (fCurrentPage * fPageSize < fRecords.size())
		{
			return true;
		}
		else{
			return false;
		}
	}
	
	public boolean hasPreviousPage(){
		
		if (fCurrentPage > 0)
		{
			return true;
		}
		else{
			return false;
		}
	}
	
	public void switchToNextPage(){
		
		if (hasNextPage())
		{
			fCurrentPage += 1;	
		}
	}
	
	public void switchToPreviousPage(){
		
		if (hasPreviousPage())
		{
			fCurrentPage -= 1;
		}
	}
	
	public void addItem(RecordType item){
		
		if(item != null)
		{
			fRecords.add(item);
		}
		
	}
	
	public void removeAll(){
		
		fRecords.clear();
	}
}
