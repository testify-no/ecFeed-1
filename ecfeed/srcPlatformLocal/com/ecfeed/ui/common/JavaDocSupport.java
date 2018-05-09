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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.ISourceRange;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.text.edits.DeleteEdit;
import org.eclipse.text.edits.InsertEdit;
import org.eclipse.text.edits.ReplaceEdit;
import org.eclipse.text.edits.TextEdit;

import com.ecfeed.core.model.AbstractNode;
import com.ecfeed.core.model.AbstractParameterNode;
import com.ecfeed.core.model.ChoiceNode;
import com.ecfeed.core.model.ClassNode;
import com.ecfeed.core.model.ConstraintNode;
import com.ecfeed.core.model.GlobalParameterNode;
import com.ecfeed.core.model.IModelVisitor;
import com.ecfeed.core.model.MethodNode;
import com.ecfeed.core.model.MethodParameterNode;
import com.ecfeed.core.model.RootNode;
import com.ecfeed.core.model.TestCaseNode;
import com.ecfeed.core.utils.JavaTypeHelper;
import com.ecfeed.core.utils.SystemLogger;

public class JavaDocSupport {

	private final static String EMPTY_STRING = "";
	private final static String FIRST_COMMENT_LINE_REGEX = "\\s*/\\*\\*";
	private final static String LAST_COMMENT_LINE_REGEX = "\\s*\\*/";
	private final static String COMMENT_LINE_PREFIX_REGEX = "\\s*\\*\\s?";
	private final static String FIRST_COMMENT_LINE = "/**\n";
	private final static String LAST_COMMENT_LINE = "*/";
	private final static String COMMENT_LINE_PREXIF = "* ";


	public static String getTypeJavadoc(AbstractParameterNode node){
		if(JavaTypeHelper.isUserType(node.getType())){
			return getJavadoc(JavaModelAnalyser.getIType(node.getType()));
		}
		return null;
	}

	public static String addJavadocFormatting(String input){
		return addJavadocFormatting(input, EMPTY_STRING);
	}

	public static String addJavadocFormatting(String input, String indent){
		BufferedReader reader = new BufferedReader(new StringReader(input));
		try{
			String output = FIRST_COMMENT_LINE;
			String line;
			while((line = reader.readLine()) != null){
				output += indent + COMMENT_LINE_PREXIF + line + "\n";
			}
			output += indent + LAST_COMMENT_LINE;
			return output;
		}catch(IOException e){SystemLogger.logCatch(e.getMessage());}
		return null;
	}

	public static String getJavadoc(AbstractNode node){
		try{
			return (String)node.accept(new JavadocReader());
		}catch (Exception e) {
			SystemLogger.logCatch(e.getMessage());
		}
		return null;
	}

	public static String importJavadoc(AbstractNode node) {
		try{
			return (String)node.accept(new JavadocImporter());
		}catch (Exception e){SystemLogger.logCatch(e.getMessage());}
		return null;
	}

	public static String importTypeJavadoc(AbstractParameterNode node) {
		String javadoc = getTypeJavadoc(node);
		javadoc = removeJavadocFormating(javadoc);
		javadoc = removeTrailingWhitespaces(javadoc);
		return javadoc;
	}

	public static void exportTypeJavadoc(AbstractParameterNode node){
		if(JavaTypeHelper.isUserType(node.getType())){
			IType type = JavaModelAnalyser.getIType(node.getType());
			try{
				exportJavadoc(type, node.getTypeComments());
			}catch(JavaModelException e){SystemLogger.logCatch(e.getMessage());}
		}
	}

	public static void exportJavadoc(AbstractNode node){
		try{
			node.accept(new JavadocExporter());
		}catch(Exception e){SystemLogger.logCatch(e.getMessage());}
	}

	private static String removeJavadocFormating(String input){
		if(input != null){
			BufferedReader reader = new BufferedReader(new StringReader(input));
			try{
				String output = "";
				String line;
				while((line = reader.readLine()) != null){
					if(line.matches(FIRST_COMMENT_LINE_REGEX) == false && line.matches(LAST_COMMENT_LINE_REGEX) == false){
						line = line.replaceAll(COMMENT_LINE_PREFIX_REGEX, "");
						output+= line + "\n";
					}
				}
				return output;
			}catch(IOException e){SystemLogger.logCatch(e.getMessage());}
		}
		return input;
	}

	private static String getDescriptionBlock(String input){
		input = removeJavadocFormating(input);
		BufferedReader reader = new BufferedReader(new StringReader(input));
		try{
			String output = "";
			String line;
			while((line = reader.readLine()) != null){
				if(line.matches("\\s*@.*")){
					break;
				}
				output+= line + "\n";
			}
			return output;
		}catch(IOException e){SystemLogger.logCatch(e.getMessage());}
		return input;
	}

	private static String removeTrailingWhitespaces(String input){
		if(input != null){
			return input.replaceAll("\\s*\\z", EMPTY_STRING);
		}
		return null;
	}

	private static String getJavadoc(IMember member){
		if(member != null){
			try {
				ICompilationUnit unit = member.getCompilationUnit();
				ISourceRange range = member.getJavadocRange();
				if(unit != null && range != null){
					String raw = unit.getSource().substring(range.getOffset(), range.getOffset() + range.getLength());
					String trimmed = raw.replaceAll("\\n\\s*\\*", EMPTY_STRING + "\n*");
					return trimmed;
				}
			} catch (JavaModelException e) {SystemLogger.logCatch(e.getMessage());}
		}
		return null;
	}

	private static void exportJavadoc(IMember member, String comments) throws JavaModelException {
		if(member != null){
			if(comments == null || comments.length() == 0){
				removeJavadoc(member);
			}else{
				ISourceRange currentJavaDocRange = member.getJavadocRange();
				TextEdit edit = null;
				String indent = getIndent(member);
				if(currentJavaDocRange != null){
					String javadoc = addJavadocFormatting(comments, indent);
					edit = new ReplaceEdit(currentJavaDocRange.getOffset(), currentJavaDocRange.getLength(), javadoc);
				}else if(member.getSourceRange().getOffset() >= 0){
					boolean moveToNewLine = false;
					if(indent.matches("\\s*") == false){
						indent = trimIndent(indent);
						moveToNewLine = true;
					}
					String javadoc = addJavadocFormatting(comments, indent);
					String comment = javadoc + "\n" + indent;
					if(moveToNewLine){
						comment = "\n" + indent + comment;
					}
					edit = new InsertEdit(member.getSourceRange().getOffset(), comment);
				}
				if(edit != null){
					ICompilationUnit unit = member.getCompilationUnit();
					if(unit != null){
						unit.becomeWorkingCopy(null);
						unit.applyTextEdit(edit, null);
						unit.commitWorkingCopy(true, null);
					}
				}
			}
		}
	}

	private static void removeJavadoc(IMember member) throws JavaModelException{
		ISourceRange currentJavaDocRange = member.getJavadocRange();
		if(currentJavaDocRange != null){
			ICompilationUnit unit = member.getCompilationUnit();
			String source = unit.getSource();
			int offset = currentJavaDocRange.getOffset();
			int length = currentJavaDocRange.getLength();
			while(((Character)source.charAt(offset + length)).toString().matches("\\s")){
				++length;
			}

			unit.applyTextEdit(new DeleteEdit(offset, length), null);
			unit.commitWorkingCopy(false, null);
		}
	}

	private static String getIndent(IMember member) {
		try{
			ISourceRange range = member.getSourceRange();
			String source = member.getCompilationUnit().getSource();
			int begin = range.getOffset();
			while(begin >= 0 && source.charAt(begin) != '\n'){
				--begin;
			}
			String indent = source.substring(begin + 1, range.getOffset());
			return indent;
		}catch(JavaModelException e){SystemLogger.logCatch(e.getMessage());}
		return null;
	}

	private static String trimIndent(String indent){
		int end = 0;
		while(indent.substring(0, end).matches("\\s*")){
			++end;
		}
		indent = indent.substring(0, end > 0 ? end - 1 : 0);
		return indent;
	}
	
	private static class JavadocReader implements IModelVisitor{

		@Override
		public Object visit(MethodParameterNode node) throws Exception {
			String methodDoc = getJavadoc(JavaModelAnalyser.getIMethod(node.getMethod()));
			if (methodDoc == null) {
				return null;
			}

			String searchedTag = "@param " + node.getName();
			int startIndex = methodDoc.indexOf(searchedTag);
			int endIndex = methodDoc.indexOf("@", startIndex + 1);
			if(endIndex == -1){
				endIndex = methodDoc.length() - 1;
			}

			while(methodDoc.charAt(startIndex) != '\n' && methodDoc.charAt(startIndex) != '*'){
				--startIndex;
			}
			while(methodDoc.charAt(endIndex) != '\n'){
				--endIndex;
			}
			methodDoc = methodDoc.substring(startIndex, endIndex);

			return methodDoc;
		}

		@Override
		public Object visit(GlobalParameterNode node) throws Exception {
			return null;
		}

		@Override
		public Object visit(RootNode node) throws Exception {
			return null;
		}

		@Override
		public Object visit(ClassNode node) throws Exception {
			return getJavadoc(JavaModelAnalyser.getIType(node.getName()));
		}

		@Override
		public Object visit(MethodNode node) throws Exception {
			IMethod method = JavaModelAnalyser.getIMethod(node);
			if (method == null) {
				return null;
			}

			return getJavadoc(method);
		}

		@Override
		public Object visit(TestCaseNode node) throws Exception {
			return null;
		}

		@Override
		public Object visit(ConstraintNode node) throws Exception {
			return null;
		}

		@Override
		public Object visit(ChoiceNode node) throws Exception {
			try{
				if(node.isAbstract() == false && JavaTypeHelper.isUserType(node.getParameter().getType())) {
					IType type = JavaModelAnalyser.getIType(node.getParameter().getType());
					if (type == null) {
						return EMPTY_STRING;
					}
					
					for(IField field : type.getFields()){
						if(field.isEnumConstant() && field.getElementName().equals(node.getValueString())){
							return getJavadoc(field);
						}
					}
				}
			}catch(JavaModelException e){SystemLogger.logCatch(e.getMessage());}
			return EMPTY_STRING;
		}

	}
	
	private static class JavadocImporter implements IModelVisitor{

		@Override
		public Object visit(MethodParameterNode node) throws Exception {
			String javadoc = getJavadoc(node);
			String tag = "@param "  + node.getName();
			int beginning = javadoc.indexOf(tag);
			String imported = javadoc.substring(beginning + tag.length(), javadoc.length());
			return imported;
		}

		@Override
		public Object visit(GlobalParameterNode node) throws Exception {
			return null;
		}

		@Override
		public Object visit(RootNode node) throws Exception {
			return null;
		}

		@Override
		public Object visit(ClassNode node) throws Exception {
			return removeTrailingWhitespaces(removeJavadocFormating(getJavadoc(node)));
		}

		@Override
		public Object visit(MethodNode node) throws Exception {
			String javadoc = getJavadoc(JavaModelAnalyser.getIMethod(node));
			javadoc = removeJavadocFormating(javadoc);
			javadoc = getDescriptionBlock(javadoc);
			javadoc = removeTrailingWhitespaces(javadoc);
			return javadoc;
		}

		@Override
		public Object visit(TestCaseNode node) throws Exception {
			return null;
		}

		@Override
		public Object visit(ConstraintNode node) throws Exception {
			return null;
		}

		@Override
		public Object visit(ChoiceNode node) throws Exception {
			String javadoc = getJavadoc(node);
			if(javadoc != null){
				javadoc = removeJavadocFormating(javadoc);
				javadoc = removeTrailingWhitespaces(javadoc);
				return javadoc;
			}
			return null;
		}

	}
	
	private static class JavadocExporter implements IModelVisitor{

		@Override
		public Object visit(MethodParameterNode node) throws Exception {
			exportJavadoc(node.getMethod());
			//			IMethod method = JavaModelAnalyser.getIMethod(node.getMethod());
			//			ICompilationUnit unit = method.getCompilationUnit();
			//			String currentJavadoc = getJavadoc(node);
			//			String newComments = node.getDescription() != null ? node.getDescription() : "";
			//			String newJavadoc = "* @param " + node.getName() + " " + newComments;
			//			String source = unit.getSource();
			//
			//			TextEdit edit = null;
			//			if(currentJavadoc != null){
			//				int offset = source.indexOf("* @param " + node.getName());
			//				int length = currentJavadoc.length();
			//				edit = new ReplaceEdit(offset, length, newJavadoc);
			//			}else {
			//				String methodJavadoc = getJavadoc(node.getMethod());
			//				if(methodJavadoc == null){
			//					exportJavadoc(node.getMethod());
			//				}
			//				int offset = source.indexOf(methodJavadoc) + methodJavadoc.length();
			//				edit = new InsertEdit(offset, newJavadoc);
			//			}
			//			unit.becomeWorkingCopy(null);
			//			unit.applyTextEdit(edit, null);
			//			unit.commitWorkingCopy(false, null);
			return null;
		}

		@Override
		public Object visit(GlobalParameterNode node) throws Exception {
			return null;
		}

		@Override
		public Object visit(RootNode node) throws Exception {
			return null;
		}

		@Override
		public Object visit(ClassNode node) throws Exception {
			IType type = JavaModelAnalyser.getIType(node.getName());
			exportJavadoc(type, node.getDescription());
			return null;
		}

		@Override
		public Object visit(MethodNode node) throws Exception {
			IMethod method = JavaModelAnalyser.getIMethod(node);
			String javadoc = node.getDescription() != null ? node.getDescription() : "";
			if(node.getParameters().size() > 0){
				if(javadoc.equals("") == false){
					javadoc +="\n\n";
				}
				for(MethodParameterNode parameter : node.getMethodParameters()){
					String parameterComments = parameter.getDescription() != null ? parameter.getDescription() : "";
					javadoc += "@param " + parameter.getName() + " " + parameterComments + "\n";
				}
			}
			exportJavadoc(method, javadoc);
			return null;
		}

		@Override
		public Object visit(TestCaseNode node) throws Exception {
			return null;
		}

		@Override
		public Object visit(ConstraintNode node) throws Exception {
			return null;
		}

		@Override
		public Object visit(ChoiceNode node) throws Exception {
			if(node.isAbstract() == false){
				IType type = JavaModelAnalyser.getIType(node.getParameter().getType());
				if(type != null && type.isEnum()){
					for(IField field : type.getFields()){
						if(field.isEnumConstant() && field.getElementName().equals(node.getValueString())){
							exportJavadoc(field, node.getDescription());
							return null;
						}
					}
				}
			}
			return null;
		}
	}
	
}
