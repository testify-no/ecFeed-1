/*******************************************************************************
 *
 * Copyright (c) 2016 ecFeed AS.                                                
 * All rights reserved. This program and the accompanying materials              
 * are made available under the terms of the Eclipse Public License v1.0         
 * which accompanies this distribution, and is available at                      
 * http://www.eclipse.org/legal/epl-v10.html 
 *  
 *******************************************************************************/
package com.ecfeed.core.generators.algorithms;


public class ModelXmlVeilarbPerson {

	public static final String getXml() {

		StringBuilder sb = new StringBuilder(); 

		sb.append("<?xml version='1.0' encoding='UTF-8'?>\n");
		sb.append("<Model name='veilarbperson' version='2'>\n");
		sb.append("    <Class name='no.nav.fo.veilarbperson.VeilarbPersonEcfeed'>\n");
		sb.append("		        <Properties>\n");
		sb.append("		            <Property name='runOnAndroid' type='boolean' value='false'/>\n");
		sb.append("		        </Properties>\n");
		sb.append("		        <Method name='validateVeilArbPerson'>\n");
		sb.append("		            <Properties>\n");
		sb.append("		                <Property name='methodRunner' type='String' value='Java Runner'/>\n");
		sb.append("		                <Property name='wbMapBrowserToParam' type='boolean' value='false'/>\n");
		sb.append("		                <Property name='wbBrowser' type='String' value='Chrome'/>\n");
		sb.append("		                <Property name='wbMapStartUrlToParam' type='boolean' value='false'/>\n");
		sb.append("		            </Properties>\n");
		sb.append("		            <Parameter name='identnummer' type='String' isExpected='false' expected='VALUE' linked='false'>\n");
		sb.append("		                <Properties>\n");
		sb.append("		                    <Property name='wbIsOptional' type='boolean' value='false'/>\n");
		sb.append("		                </Properties>\n");
		sb.append("		                <Comments>\n");
		sb.append("		                    <TypeComments/>\n");
		sb.append("		                </Comments>\n");
		sb.append("		                <Choice name='foedselsnummer' value='VALUE'>\n");
		sb.append("		                    <Choice name='female' value='String'>\n");
		sb.append("		                        <Choice name='FODSELSNUMMER_19' value='15083002678'/>\n");
		sb.append("		                        <Choice name='FODSELSNUMMER_190' value='15084092678'/>\n");
		sb.append("		                        <Choice name='FODSELSNUMMER_20' value='28023999899'/>\n");
		sb.append("		                        <Choice name='FODSELSNUMMER_18' value='31125450019'/>\n");
		sb.append("		                    </Choice>\n");
		sb.append("		                    <Choice name='male' value=''>\n");
		sb.append("		                        <Choice name='FODSELSNUMMER_19' value='17059948768'/>\n");
		sb.append("		                        <Choice name='FODSELSNUMMER_20' value='01011356733'/>\n");
		sb.append("		                        <Choice name='FODSELSNUMMER_200' value='16030099978'/>\n");
		sb.append("		                    </Choice>\n");
		sb.append("		                </Choice>\n");
		sb.append("		                <Choice name='dnummer' value=''>\n");
		sb.append("		                    <Choice name='ma' value=''>\n");
		sb.append("		                        <Choice name='D_NUMMER_20' value='56030099961'/>\n");
		sb.append("		                        <Choice name='D_NUMMER_200' value='41011356727'/>\n");
		sb.append("		                        <Choice name='D_NUMMER_19' value='57059948751'/>\n");
		sb.append("		                    </Choice>\n");
		sb.append("		                    <Choice name='fe' value=''>\n");
		sb.append("		                        <Choice name='D_NUMMER_19' value='55084092823'/>\n");
		sb.append("		                        <Choice name='D_NUMMER_20' value='68023999882'/>\n");
		sb.append("		                        <Choice name='D_NUMMER_18' value='71125450002'/>\n");
		sb.append("		                        <Choice name='D_NUMMER_190' value='55083002823'/>\n");
		sb.append("		                    </Choice>\n");
		sb.append("		                </Choice>\n");
		sb.append("		                <Choice name='hnummer' value=''>\n");
		sb.append("		                    <Choice name='kv' value=''>\n");
		sb.append("		                        <Choice name='H_NUMMER_194' value='15484092812'/>\n");
		sb.append("		                        <Choice name='H_NUMMER_203' value='28423999871'/>\n");
		sb.append("		                        <Choice name='H_NUMMER_193' value='15483002812'/>\n");
		sb.append("		                        <Choice name='H_NUMMER_185' value='315254500101'/>\n");
		sb.append("		                    </Choice>\n");
		sb.append("		                    <Choice name='ma' value=''>\n");
		sb.append("		                        <Choice name='H_NUMMER_200' value='16430099950'/>\n");
		sb.append("		                        <Choice name='H_NUMMER_201' value='01411356716'/>\n");
		sb.append("		                        <Choice name='H_NUMMER_199' value='17459948902'/>\n");
		sb.append("		                    </Choice>\n");
		sb.append("		                </Choice>\n");
		sb.append("		                <Choice name='ugyldignummer' value='String'>\n");
		sb.append("		                    <Choice name='UGYLDIG_FODSELSNUMMER_1' value='01025075087'/>\n");
		sb.append("		                    <Choice name='UGYLDIG_FODSELSNUMMER_2' value='01014055033'/>\n");
		sb.append("		                </Choice>\n");
		sb.append("		            </Parameter>\n");
		sb.append("		            <Parameter name='foedselsdato' type='String' isExpected='false' expected='0' linked='false'>\n");
		sb.append("		                <Properties>\n");
		sb.append("		                    <Property name='wbIsOptional' type='boolean' value='false'/>\n");
		sb.append("		                </Properties>\n");
		sb.append("		                <Comments>\n");
		sb.append("		                    <TypeComments/>\n");
		sb.append("		                </Comments>\n");
		sb.append("		                <Choice name='FODSELSDATO_2039' value='2039-02-28'/>\n");
		sb.append("		                <Choice name='FODSELSDATO_2013' value='2013-01-01'/>\n");
		sb.append("		                <Choice name='FODSELSDATO_2000' value='2000-03-16'/>\n");
		sb.append("		                <Choice name='FODSELSDATO_1999' value='1999-05-17'/>\n");
		sb.append("		                <Choice name='FODSELSDATO_1930' value='1930-08-15'/>\n");
		sb.append("		                <Choice name='FODSELSDATO_1940' value='1940-08-15'/>\n");
		sb.append("		                <Choice name='FODSELSDATO_1854' value='1854-12-31'/>\n");
		sb.append("		            </Parameter>\n");
		sb.append("		            <Parameter name='kjoenn' type='String' isExpected='false' expected='0' linked='false'>\n");
		sb.append("		                <Properties>\n");
		sb.append("		                    <Property name='wbIsOptional' type='boolean' value='false'/>\n");
		sb.append("		                </Properties>\n");
		sb.append("		                <Comments>\n");
		sb.append("		                    <TypeComments/>\n");
		sb.append("		                </Comments>\n");
		sb.append("		                <Choice name='male' value='M'/>\n");
		sb.append("		                <Choice name='female' value='K'/>\n");
		sb.append("		            </Parameter>\n");
		sb.append("		            <Parameter name='gyldigFoedselsnummer' type='boolean' isExpected='true' expected='true' linked='false'>\n");
		sb.append("		                <Properties>\n");
		sb.append("		                    <Property name='wbIsOptional' type='boolean' value='false'/>\n");
		sb.append("		                </Properties>\n");
		sb.append("		                <Comments>\n");
		sb.append("		                    <TypeComments/>\n");
		sb.append("		                </Comments>\n");
		sb.append("		            </Parameter>\n");
		sb.append("		            <Constraint name='constraint'>\n");
		sb.append("		                <Premise>\n");
		sb.append("		                    <StatementArray operator='or'>\n");
		sb.append("		                        <Statement choice='foedselsnummer:female:FODSELSNUMMER_20' parameter='identnummer' relation='='/>\n");
		sb.append("		                        <Statement choice='dnummer:fe:D_NUMMER_20' parameter='identnummer' relation='='/>\n");
		sb.append("		                        <Statement choice='hnummer:kv:H_NUMMER_203' parameter='identnummer' relation='='/>\n");
		sb.append("		                    </StatementArray>\n");
		sb.append("		                </Premise>\n");
		sb.append("		                <Consequence>\n");
		sb.append("		                    <Statement choice='FODSELSDATO_2039' parameter='foedselsdato' relation='='/>\n");
		sb.append("		                </Consequence>\n");
		sb.append("		            </Constraint>\n");
		sb.append("		            <Constraint name='constraint'>\n");
		sb.append("		                <Premise>\n");
		sb.append("		                    <StatementArray operator='or'>\n");
		sb.append("		                        <Statement choice='foedselsnummer:male:FODSELSNUMMER_20' parameter='identnummer' relation='='/>\n");
		sb.append("		                        <Statement choice='dnummer:ma:D_NUMMER_200' parameter='identnummer' relation='='/>\n");
		sb.append("		                        <Statement choice='hnummer:ma:H_NUMMER_201' parameter='identnummer' relation='='/>\n");
		sb.append("		                    </StatementArray>\n");
		sb.append("		                </Premise>\n");
		sb.append("		                <Consequence>\n");
		sb.append("		                    <Statement choice='FODSELSDATO_2013' parameter='foedselsdato' relation='='/>\n");
		sb.append("		                </Consequence>\n");
		sb.append("		            </Constraint>\n");
		sb.append("		            <Constraint name='constraint'>\n");
		sb.append("		                <Premise>\n");
		sb.append("		                    <StatementArray operator='or'>\n");
		sb.append("		                        <Statement choice='foedselsnummer:male:FODSELSNUMMER_200' parameter='identnummer' relation='='/>\n");
		sb.append("		                        <Statement choice='dnummer:ma:D_NUMMER_20' parameter='identnummer' relation='='/>\n");
		sb.append("		                        <Statement choice='hnummer:ma:H_NUMMER_200' parameter='identnummer' relation='='/>\n");
		sb.append("		                    </StatementArray>\n");
		sb.append("		                </Premise>\n");
		sb.append("		                <Consequence>\n");
		sb.append("		                    <Statement choice='FODSELSDATO_2000' parameter='foedselsdato' relation='='/>\n");
		sb.append("		                </Consequence>\n");
		sb.append("		            </Constraint>\n");
		sb.append("		            <Constraint name='constraint'>\n");
		sb.append("		                <Premise>\n");
		sb.append("		                    <StatementArray operator='or'>\n");
		sb.append("		                        <Statement choice='foedselsnummer:male:FODSELSNUMMER_19' parameter='identnummer' relation='='/>\n");
		sb.append("		                        <Statement choice='dnummer:ma:D_NUMMER_19' parameter='identnummer' relation='='/>\n");
		sb.append("		                        <Statement choice='hnummer:ma:H_NUMMER_199' parameter='identnummer' relation='='/>\n");
		sb.append("		                    </StatementArray>\n");
		sb.append("		                </Premise>\n");
		sb.append("		                <Consequence>\n");
		sb.append("		                    <Statement choice='FODSELSDATO_1999' parameter='foedselsdato' relation='='/>\n");
		sb.append("		                </Consequence>\n");
		sb.append("		            </Constraint>\n");
		sb.append("		            <Constraint name='constraint'>\n");
		sb.append("		                <Premise>\n");
		sb.append("		                    <StatementArray operator='or'>\n");
		sb.append("		                        <Statement choice='foedselsnummer:female:FODSELSNUMMER_19' parameter='identnummer' relation='='/>\n");
		sb.append("		                        <Statement choice='dnummer:fe:D_NUMMER_190' parameter='identnummer' relation='='/>\n");
		sb.append("		                        <Statement choice='hnummer:kv:H_NUMMER_193' parameter='identnummer' relation='='/>\n");
		sb.append("		                    </StatementArray>\n");
		sb.append("		                </Premise>\n");
		sb.append("		                <Consequence>\n");
		sb.append("		                    <Statement choice='FODSELSDATO_1930' parameter='foedselsdato' relation='='/>\n");
		sb.append("		                </Consequence>\n");
		sb.append("		            </Constraint>\n");
		sb.append("		            <Constraint name='constraint'>\n");
		sb.append("		                <Premise>\n");
		sb.append("		                    <StatementArray operator='or'>\n");
		sb.append("		                        <Statement choice='foedselsnummer:female:FODSELSNUMMER_190' parameter='identnummer' relation='='/>\n");
		sb.append("		                        <Statement choice='dnummer:fe:D_NUMMER_19' parameter='identnummer' relation='='/>\n");
		sb.append("		                        <Statement choice='hnummer:kv:H_NUMMER_194' parameter='identnummer' relation='='/>\n");
		sb.append("		                    </StatementArray>\n");
		sb.append("		                </Premise>\n");
		sb.append("		                <Consequence>\n");
		sb.append("		                    <Statement choice='FODSELSDATO_1940' parameter='foedselsdato' relation='='/>\n");
		sb.append("		                </Consequence>\n");
		sb.append("		            </Constraint>\n");
		sb.append("		            <Constraint name='constraint'>\n");
		sb.append("		                <Premise>\n");
		sb.append("		                    <StatementArray operator='or'>\n");
		sb.append("		                        <Statement choice='foedselsnummer:female:FODSELSNUMMER_18' parameter='identnummer' relation='='/>\n");
		sb.append("		                        <Statement choice='dnummer:fe:D_NUMMER_18' parameter='identnummer' relation='='/>\n");
		sb.append("		                        <Statement choice='hnummer:kv:H_NUMMER_185' parameter='identnummer' relation='='/>\n");
		sb.append("		                    </StatementArray>\n");
		sb.append("		                </Premise>\n");
		sb.append("		                <Consequence>\n");
		sb.append("		                    <Statement choice='FODSELSDATO_1854' parameter='foedselsdato' relation='='/>\n");
		sb.append("		                </Consequence>\n");
		sb.append("		            </Constraint>\n");
		sb.append("		            <Constraint name='constraint'>\n");
		sb.append("		                <Premise>\n");
		sb.append("		                    <StatementArray operator='or'>\n");
		sb.append("		                        <Statement choice='ugyldignummer:UGYLDIG_FODSELSNUMMER_1' parameter='identnummer' relation='='/>\n");
		sb.append("		                        <Statement choice='ugyldignummer:UGYLDIG_FODSELSNUMMER_2' parameter='identnummer' relation='='/>\n");
		sb.append("		                    </StatementArray>\n");
		sb.append("		                </Premise>\n");
		sb.append("		                <Consequence>\n");
		sb.append("		                    <ExpectedValueStatement parameter='gyldigFoedselsnummer' value='false'/>\n");
		sb.append("		                </Consequence>\n");
		sb.append("		            </Constraint>\n");
		sb.append("		            <Constraint name='constraint'>\n");
		sb.append("		                <Premise>\n");
		sb.append("		                    <StatementArray operator='or'>\n");
		sb.append("		                        <Statement choice='foedselsnummer:female' parameter='identnummer' relation='='/>\n");
		sb.append("		                        <Statement choice='dnummer:fe' parameter='identnummer' relation='='/>\n");
		sb.append("		                        <Statement choice='hnummer:kv' parameter='identnummer' relation='='/>\n");
		sb.append("		                    </StatementArray>\n");
		sb.append("		                </Premise>\n");
		sb.append("		                <Consequence>\n");
		sb.append("		                    <Statement choice='female' parameter='kjoenn' relation='='/>\n");
		sb.append("		                </Consequence>\n");
		sb.append("		            </Constraint>\n");
		sb.append("		            <Constraint name='constraint'>\n");
		sb.append("		                <Premise>\n");
		sb.append("		                    <StatementArray operator='or'>\n");
		sb.append("		                        <Statement choice='foedselsnummer:male' parameter='identnummer' relation='='/>\n");
		sb.append("		                        <Statement choice='dnummer:ma' parameter='identnummer' relation='='/>\n");
		sb.append("		                        <Statement choice='hnummer:ma' parameter='identnummer' relation='='/>\n");
		sb.append("		                    </StatementArray>\n");
		sb.append("		                </Premise>\n");
		sb.append("		                <Consequence>\n");
		sb.append("		                    <Statement choice='male' parameter='kjoenn' relation='='/>\n");
		sb.append("		                </Consequence>\n");
		sb.append("		            </Constraint>\n");
		sb.append("		        </Method>\n");
		sb.append("		    </Class>\n");
		sb.append("		</Model>\n");

		String xml = sb.toString();
		xml = xml.replace("'", "\"");

		return xml;
	}

	public static final String getMethodName() {
		return "validateVeilArbPerson";
	}

}
