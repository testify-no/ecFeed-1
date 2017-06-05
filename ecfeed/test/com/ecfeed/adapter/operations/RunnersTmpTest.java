/*******************************************************************************
*
* Copyright (c) 2016 ecFeed AS.                                                
* All rights reserved. This program and the accompanying materials              
* are made available under the terms of the Eclipse Public License v1.0         
* which accompanies this distribution, and is available at                      
* http://www.eclipse.org/legal/epl-v10.html 
*  
*******************************************************************************/
package com.ecfeed.adapter.operations;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.ecfeed.junit.StaticRunner;
import com.ecfeed.junit.annotations.EcModel;

@RunWith(StaticRunner.class)
@EcModel("test/com/ecfeed/adapter/operations/RunnersTmpTest.ect")
public class RunnersTmpTest {
    @Test
    public void testMethod(String arg) {
        System.out.println("RunnersTmpTest.testMethod");
    }
}