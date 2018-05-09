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
	
	public void setCurrentPageIndex(int currentPage){
		fCurrentPage = currentPage;
	}

	public List<RecordType> getCurrentPage() {

		List<RecordType> CurrentPage = new ArrayList<RecordType>();

		int firstRecordIndex = getFirstRecordIndex();
		int recordsOnPage = getCountOfRecordsOnCurrentPage(firstRecordIndex);

		for (int index = firstRecordIndex; index < recordsOnPage; index++){
			CurrentPage.add(fRecords.get(index));
		}
		return CurrentPage;
	}

	public List<RecordType> getRecordsList(){

		return fRecords;
	}

	public int getCountOfRecordsOnCurrentPage(int firstRecordIndex) {

		int recordsCount = 0;

		if (firstRecordIndex + fPageSize < fRecords.size()){
			recordsCount = firstRecordIndex + fPageSize;
		} else {
			recordsCount = fRecords.size();
		}
		return recordsCount;
	}

	public int getFirstRecordIndex() {

		int index = 0;

		if (fCurrentPage == 0){
			index = 0;
		} else {
			index = fPageSize * fCurrentPage;	
		}
		return index;
	}

	public boolean hasNextPage() {

		if ((fCurrentPage + 1) * fPageSize < fRecords.size()){
			return true;
		} else {
			return false;
		}
	}

	public boolean hasPreviousPage(){

		if (fCurrentPage > 0){
			return true;
		} else {
			return false;
		}
	}

	public void switchToNextPage(){

		if (hasNextPage()){
			fCurrentPage += 1;	
		}
	}

	public void switchToPreviousPage(){

		if (hasPreviousPage()){
			fCurrentPage -= 1;
		}
	}

	public void addItem(RecordType item){

		if (item != null){
			fRecords.add(item);
		}
	}

	public void removeAllRecords(){

		fRecords.clear();
	}
}
