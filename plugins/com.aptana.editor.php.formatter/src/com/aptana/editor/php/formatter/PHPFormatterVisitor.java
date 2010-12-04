/**
 * This file Copyright (c) 2005-2010 Aptana, Inc. This program is
 * dual-licensed under both the Aptana Public License and the GNU General
 * Public license. You may elect to use one or the other of these licenses.
 * 
 * This program is distributed in the hope that it will be useful, but
 * AS-IS and WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE, TITLE, or
 * NONINFRINGEMENT. Redistribution, except as permitted by whichever of
 * the GPL or APL you select, is prohibited.
 *
 * 1. For the GPL license (GPL), you can redistribute and/or modify this
 * program under the terms of the GNU General Public License,
 * Version 3, as published by the Free Software Foundation.  You should
 * have received a copy of the GNU General Public License, Version 3 along
 * with this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 * 
 * Aptana provides a special exception to allow redistribution of this file
 * with certain other free and open source software ("FOSS") code and certain additional terms
 * pursuant to Section 7 of the GPL. You may view the exception and these
 * terms on the web at http://www.aptana.com/legal/gpl/.
 * 
 * 2. For the Aptana Public License (APL), this program and the
 * accompanying materials are made available under the terms of the APL
 * v1.0 which accompanies this distribution, and is available at
 * http://www.aptana.com/legal/apl/.
 * 
 * You may view the GPL, Aptana's exception and additional terms, and the
 * APL in the file titled license.html at the root of the corresponding
 * plugin containing this source file.
 * 
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.php.formatter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.php.internal.core.ast.nodes.ASTError;
import org.eclipse.php.internal.core.ast.nodes.ASTNode;
import org.eclipse.php.internal.core.ast.nodes.ArrayAccess;
import org.eclipse.php.internal.core.ast.nodes.ArrayCreation;
import org.eclipse.php.internal.core.ast.nodes.ArrayElement;
import org.eclipse.php.internal.core.ast.nodes.Assignment;
import org.eclipse.php.internal.core.ast.nodes.BackTickExpression;
import org.eclipse.php.internal.core.ast.nodes.Block;
import org.eclipse.php.internal.core.ast.nodes.BreakStatement;
import org.eclipse.php.internal.core.ast.nodes.CastExpression;
import org.eclipse.php.internal.core.ast.nodes.CatchClause;
import org.eclipse.php.internal.core.ast.nodes.ClassDeclaration;
import org.eclipse.php.internal.core.ast.nodes.ClassInstanceCreation;
import org.eclipse.php.internal.core.ast.nodes.ClassName;
import org.eclipse.php.internal.core.ast.nodes.CloneExpression;
import org.eclipse.php.internal.core.ast.nodes.ConditionalExpression;
import org.eclipse.php.internal.core.ast.nodes.ConstantDeclaration;
import org.eclipse.php.internal.core.ast.nodes.ContinueStatement;
import org.eclipse.php.internal.core.ast.nodes.DeclareStatement;
import org.eclipse.php.internal.core.ast.nodes.DoStatement;
import org.eclipse.php.internal.core.ast.nodes.EchoStatement;
import org.eclipse.php.internal.core.ast.nodes.EmptyStatement;
import org.eclipse.php.internal.core.ast.nodes.Expression;
import org.eclipse.php.internal.core.ast.nodes.ExpressionStatement;
import org.eclipse.php.internal.core.ast.nodes.FieldAccess;
import org.eclipse.php.internal.core.ast.nodes.FieldsDeclaration;
import org.eclipse.php.internal.core.ast.nodes.ForEachStatement;
import org.eclipse.php.internal.core.ast.nodes.ForStatement;
import org.eclipse.php.internal.core.ast.nodes.FormalParameter;
import org.eclipse.php.internal.core.ast.nodes.FunctionDeclaration;
import org.eclipse.php.internal.core.ast.nodes.FunctionInvocation;
import org.eclipse.php.internal.core.ast.nodes.FunctionName;
import org.eclipse.php.internal.core.ast.nodes.GlobalStatement;
import org.eclipse.php.internal.core.ast.nodes.GotoLabel;
import org.eclipse.php.internal.core.ast.nodes.GotoStatement;
import org.eclipse.php.internal.core.ast.nodes.Identifier;
import org.eclipse.php.internal.core.ast.nodes.IfStatement;
import org.eclipse.php.internal.core.ast.nodes.IgnoreError;
import org.eclipse.php.internal.core.ast.nodes.InLineHtml;
import org.eclipse.php.internal.core.ast.nodes.Include;
import org.eclipse.php.internal.core.ast.nodes.InfixExpression;
import org.eclipse.php.internal.core.ast.nodes.InstanceOfExpression;
import org.eclipse.php.internal.core.ast.nodes.InterfaceDeclaration;
import org.eclipse.php.internal.core.ast.nodes.LambdaFunctionDeclaration;
import org.eclipse.php.internal.core.ast.nodes.ListVariable;
import org.eclipse.php.internal.core.ast.nodes.MethodDeclaration;
import org.eclipse.php.internal.core.ast.nodes.MethodInvocation;
import org.eclipse.php.internal.core.ast.nodes.NamespaceDeclaration;
import org.eclipse.php.internal.core.ast.nodes.NamespaceName;
import org.eclipse.php.internal.core.ast.nodes.ParenthesisExpression;
import org.eclipse.php.internal.core.ast.nodes.PostfixExpression;
import org.eclipse.php.internal.core.ast.nodes.PrefixExpression;
import org.eclipse.php.internal.core.ast.nodes.Quote;
import org.eclipse.php.internal.core.ast.nodes.Reference;
import org.eclipse.php.internal.core.ast.nodes.ReflectionVariable;
import org.eclipse.php.internal.core.ast.nodes.ReturnStatement;
import org.eclipse.php.internal.core.ast.nodes.Scalar;
import org.eclipse.php.internal.core.ast.nodes.Statement;
import org.eclipse.php.internal.core.ast.nodes.StaticConstantAccess;
import org.eclipse.php.internal.core.ast.nodes.StaticFieldAccess;
import org.eclipse.php.internal.core.ast.nodes.StaticMethodInvocation;
import org.eclipse.php.internal.core.ast.nodes.StaticStatement;
import org.eclipse.php.internal.core.ast.nodes.SwitchCase;
import org.eclipse.php.internal.core.ast.nodes.SwitchStatement;
import org.eclipse.php.internal.core.ast.nodes.ThrowStatement;
import org.eclipse.php.internal.core.ast.nodes.TryStatement;
import org.eclipse.php.internal.core.ast.nodes.TypeDeclaration;
import org.eclipse.php.internal.core.ast.nodes.UnaryOperation;
import org.eclipse.php.internal.core.ast.nodes.UseStatement;
import org.eclipse.php.internal.core.ast.nodes.UseStatementPart;
import org.eclipse.php.internal.core.ast.nodes.Variable;
import org.eclipse.php.internal.core.ast.nodes.VariableBase;
import org.eclipse.php.internal.core.ast.nodes.WhileStatement;
import org.eclipse.php.internal.core.ast.visitor.AbstractVisitor;

import com.aptana.core.util.StringUtil;
import com.aptana.editor.php.formatter.nodes.FormatterPHPBlockNode;
import com.aptana.editor.php.formatter.nodes.FormatterPHPBreakNode;
import com.aptana.editor.php.formatter.nodes.FormatterPHPCaseBodyNode;
import com.aptana.editor.php.formatter.nodes.FormatterPHPCaseColonNode;
import com.aptana.editor.php.formatter.nodes.FormatterPHPDeclarationNode;
import com.aptana.editor.php.formatter.nodes.FormatterPHPElseIfNode;
import com.aptana.editor.php.formatter.nodes.FormatterPHPElseNode;
import com.aptana.editor.php.formatter.nodes.FormatterPHPExpressionWrapperNode;
import com.aptana.editor.php.formatter.nodes.FormatterPHPFunctionBodyNode;
import com.aptana.editor.php.formatter.nodes.FormatterPHPFunctionInvocationNode;
import com.aptana.editor.php.formatter.nodes.FormatterPHPHeredocNode;
import com.aptana.editor.php.formatter.nodes.FormatterPHPIfNode;
import com.aptana.editor.php.formatter.nodes.FormatterPHPImplicitBlockNode;
import com.aptana.editor.php.formatter.nodes.FormatterPHPKeywordNode;
import com.aptana.editor.php.formatter.nodes.FormatterPHPLineStartingNode;
import com.aptana.editor.php.formatter.nodes.FormatterPHPNamespaceBlockNode;
import com.aptana.editor.php.formatter.nodes.FormatterPHPNonBlockedWhileNode;
import com.aptana.editor.php.formatter.nodes.FormatterPHPOperatorNode;
import com.aptana.editor.php.formatter.nodes.FormatterPHPParenthesesNode;
import com.aptana.editor.php.formatter.nodes.FormatterPHPPunctuationNode;
import com.aptana.editor.php.formatter.nodes.FormatterPHPSwitchNode;
import com.aptana.editor.php.formatter.nodes.FormatterPHPTextNode;
import com.aptana.editor.php.formatter.nodes.FormatterPHPTypeBodyNode;
import com.aptana.editor.php.formatter.nodes.NodeTypes.TypeOperator;
import com.aptana.editor.php.formatter.nodes.NodeTypes.TypePunctuation;
import com.aptana.formatter.FormatterDocument;
import com.aptana.formatter.nodes.IFormatterContainerNode;

/**
 * A PHP formatter node builder.
 * 
 * @author Shalom Gibly <sgibly@aptana.com>
 */
public class PHPFormatterVisitor extends AbstractVisitor
{

	// Match words in a string
	private static final Pattern WORD_PATTERN = Pattern.compile("\\w+"); //$NON-NLS-1$
	public static final String INVOCATION_ARROW = "->"; //$NON-NLS-1$
	public static final String STATIC_INVOCATION = "::"; //$NON-NLS-1$
	private static final char[] SEMICOLON_AND_COLON = new char[] { ';', ',' };
	private static final char[] SEMICOLON = new char[] { ';' };

	private FormatterDocument document;
	private PHPFormatterNodeBuilder builder;

	/**
	 * @param builder
	 * @param document
	 */
	public PHPFormatterVisitor(FormatterDocument document, PHPFormatterNodeBuilder builder)
	{
		this.document = document;
		this.builder = builder;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * org.eclipse.php.internal.core.ast.visitor.AbstractVisitor#visit(org.eclipse.php.internal.core.ast.nodes.IfStatement
	 * )
	 */
	@Override
	public boolean visit(IfStatement ifStatement)
	{
		Statement falseStatement = ifStatement.getFalseStatement();
		Statement trueStatement = ifStatement.getTrueStatement();

		boolean isEmptyFalseBlock = (falseStatement == null);
		boolean hasTrueBlock = (trueStatement.getType() == ASTNode.BLOCK);
		boolean hasFalseBlock = (!isEmptyFalseBlock && falseStatement.getType() == ASTNode.BLOCK);
		// First, construct the if condition node
		int start = ifStatement.getStart();
		FormatterPHPIfNode conditionNode = new FormatterPHPIfNode(document, hasTrueBlock, ifStatement);
		conditionNode.setBegin(builder.createTextNode(document, start, start + 2));
		builder.push(conditionNode);
		// push the condition elements that appear in parentheses
		pushNodeInParentheses('(', ')', start + 2, trueStatement.getStart(), ifStatement.getCondition());
		// Construct the 'true' part of the 'if' and visit its children
		if (hasTrueBlock)
		{
			visitBlockNode((Block) trueStatement, ifStatement, isEmptyFalseBlock);
		}
		else
		{
			// Wrap with an empty block node and visit the children
			wrapInImplicitBlock(trueStatement, false);
		}
		builder.checkedPop(conditionNode, trueStatement.getEnd());

		if (!isEmptyFalseBlock)
		{
			// Construct the 'false' part if exist.
			// Note that the PHP parser does not provide us with the start offset of the 'else' keyword, so we need
			// to locate it in between the end of the 'true' block and the begin of the 'false' block.
			// However, in case we have an 'elseif' case, the offset of the false block points to the start of the
			// 'elseif' word.
			int trueBlockEnd = trueStatement.getEnd();
			int falseBlockStart = falseStatement.getStart();
			String segment = (trueBlockEnd != falseBlockStart) ? document.get(trueBlockEnd + 1, falseBlockStart)
					: StringUtil.EMPTY;
			int elsePos = segment.toLowerCase().indexOf("else"); //$NON-NLS-1$
			boolean isElseIf = (falseStatement.getType() == ASTNode.IF_STATEMENT);
			boolean isConnectedElsif = (isElseIf && elsePos < 0);
			FormatterPHPElseNode elseNode = null;
			if (!isConnectedElsif)
			{
				int elseBlockStart = elsePos + trueBlockEnd + 1;
				int elseBlockDeclarationEnd = elseBlockStart + 4; // +4 for the keyword 'else'
				elseNode = new FormatterPHPElseNode(document, hasFalseBlock, isElseIf, hasTrueBlock);
				elseNode.setBegin(builder.createTextNode(document, elseBlockStart, elseBlockDeclarationEnd));
				builder.push(elseNode);
			}
			if (!isConnectedElsif && hasFalseBlock)
			{
				visitBlockNode((Block) falseStatement, ifStatement, true);
			}
			else
			{
				if (isElseIf)
				{
					// Wrap the incoming 'if' with an Else-If node that will allow us later to break it and indent
					// it.
					FormatterPHPElseIfNode elseIfNode = new FormatterPHPElseIfNode(document);
					elseIfNode.setBegin(builder.createTextNode(document, falseBlockStart, falseBlockStart));
					builder.push(elseIfNode);
					falseStatement.accept(this);
					int falseBlockEnd = falseStatement.getEnd();
					builder.checkedPop(elseIfNode, falseBlockEnd);
					int end = elseIfNode.getEndOffset();
					elseIfNode.setEnd(builder.createTextNode(document, end, end));
				}
				else
				{
					// Wrap with an empty block node and visit the children
					wrapInImplicitBlock(falseStatement, false);
				}
			}
			if (elseNode != null)
			{
				builder.checkedPop(elseNode, falseStatement.getEnd());
			}
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * org.eclipse.php.internal.core.ast.visitor.AbstractVisitor#visit(org.eclipse.php.internal.core.ast.nodes.ArrayAccess
	 * )
	 */
	@Override
	public boolean visit(ArrayAccess arrayAccess)
	{
		Expression index = arrayAccess.getIndex();
		VariableBase name = arrayAccess.getName();
		name.accept(this);
		if (arrayAccess.getArrayType() == ArrayAccess.VARIABLE_HASHTABLE)
		{
			// push a curly brackets and visit the index
			pushNodeInParentheses('{', '}', name.getEnd(), arrayAccess.getEnd(), index);
		}
		else
		{
			// push a square brackets and visit the index
			pushNodeInParentheses('[', ']', name.getEnd(), arrayAccess.getEnd(), index);
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * org.eclipse.php.internal.core.ast.visitor.AbstractVisitor#visit(org.eclipse.php.internal.core.ast.nodes.ArrayCreation
	 * )
	 */
	@Override
	public boolean visit(ArrayCreation arrayCreation)
	{
		// we need to make sure we do not add a new line in front of the 'array' in some cases,
		// therefore, we push a common declaration. We set the 'hasBlockedBody' to true to avoid
		// indentation.
		int declarationEndOffset = arrayCreation.getStart() + 5;
		visitCommonDeclaration(arrayCreation, declarationEndOffset, true);
		List<ArrayElement> elements = arrayCreation.elements();
		pushParametersInParentheses(declarationEndOffset, arrayCreation.getEnd(), elements);
		return false;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * org.eclipse.php.internal.core.ast.visitor.AbstractVisitor#visit(org.eclipse.php.internal.core.ast.nodes.ArrayElement
	 * )
	 */
	@Override
	public boolean visit(ArrayElement arrayElement)
	{
		Expression key = arrayElement.getKey();
		Expression value = arrayElement.getValue();
		List<ASTNode> leftNodes = new ArrayList<ASTNode>(1);
		List<ASTNode> rightNodes = null;
		if (key == null)
		{
			leftNodes.add(value);
		}
		else
		{
			leftNodes.add(key);
			rightNodes = new ArrayList<ASTNode>(1);
			rightNodes.add(value);
		}
		visitNodeLists(leftNodes, rightNodes, TypeOperator.KEY_VALUE, null);
		return false;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * org.eclipse.php.internal.core.ast.visitor.AbstractVisitor#visit(org.eclipse.php.internal.core.ast.nodes.Assignment
	 * )
	 */
	@Override
	public boolean visit(Assignment assignment)
	{
		VariableBase leftHandSide = assignment.getLeftHandSide();
		Expression rightHandSide = assignment.getRightHandSide();
		String operationString = assignment.getOperationString();
		visitLeftRightExpression(assignment, leftHandSide, rightHandSide, operationString);
		return false;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * org.eclipse.php.internal.core.ast.visitor.AbstractVisitor#visit(org.eclipse.php.internal.core.ast.nodes.ASTError)
	 */
	@Override
	public boolean visit(ASTError astError)
	{
		builder.setHasErrors(true);
		return false;
	}

	/*
	 * (non-Javadoc)
	 * @seeorg.eclipse.php.internal.core.ast.visitor.AbstractVisitor#visit(org.eclipse.php.internal.core.ast.nodes.
	 * BackTickExpression)
	 */
	@Override
	public boolean visit(BackTickExpression backTickExpression)
	{
		visitTextNode(backTickExpression, true, 0);
		return false;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * org.eclipse.php.internal.core.ast.visitor.AbstractVisitor#visit(org.eclipse.php.internal.core.ast.nodes.Block)
	 */
	@Override
	public boolean visit(Block block)
	{
		// the default visit for a block assumes that there is an open char for that block, but not necessarily a
		// closing char. See also visitBlockNode() for other block visiting options.
		FormatterPHPBlockNode blockNode = new FormatterPHPBlockNode(document,
				block.getParent().getType() == ASTNode.PROGRAM);
		blockNode.setBegin(builder.createTextNode(document, block.getStart(), block.getStart() + 1));
		builder.push(blockNode);
		block.childrenAccept(this);
		int end = block.getEnd();
		builder.checkedPop(blockNode, end - 1);
		if (block.isCurly())
		{
			int endWithSemicolon = locateCharMatchInLine(end, SEMICOLON_AND_COLON, document, false);
			blockNode.setEnd(builder.createTextNode(document, end - 1, endWithSemicolon));
		}
		else
		{
			blockNode.setEnd(builder.createTextNode(document, end, end));
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * @seeorg.eclipse.php.internal.core.ast.visitor.AbstractVisitor#visit(org.eclipse.php.internal.core.ast.nodes.
	 * BreakStatement)
	 */
	@Override
	public boolean visit(BreakStatement breakStatement)
	{
		Expression expression = breakStatement.getExpression();
		int start = breakStatement.getStart();
		int end = breakStatement.getEnd();
		if (expression == null)
		{
			FormatterPHPBreakNode breakNode = new FormatterPHPBreakNode(document, breakStatement.getParent());
			breakNode.setBegin(builder.createTextNode(document, start, end));
			builder.push(breakNode);
			builder.checkedPop(breakNode, -1);
		}
		else
		{
			// treat it as we treat the 'continue' statement
			// push the 'break' keyword.
			pushKeyword(start, 5, true);
			// visit the break expression
			expression.accept(this);
			pushSpaceConsumingNode(end - 1);
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * @seeorg.eclipse.php.internal.core.ast.visitor.AbstractVisitor#visit(org.eclipse.php.internal.core.ast.nodes.
	 * ClassDeclaration)
	 */
	@Override
	public boolean visit(ClassDeclaration classDeclaration)
	{
		visitTypeDeclaration(classDeclaration);
		return false;
	}

	/*
	 * (non-Javadoc)
	 * @seeorg.eclipse.php.internal.core.ast.visitor.AbstractVisitor#visit(org.eclipse.php.internal.core.ast.nodes.
	 * ClassInstanceCreation)
	 */
	@Override
	public boolean visit(ClassInstanceCreation classInstanceCreation)
	{
		ClassName className = classInstanceCreation.getClassName();
		int creationEnd = classInstanceCreation.getEnd();
		boolean hasParentheses = creationEnd != className.getEnd();
		// push the 'new' keyword. We push it as a text node and not as a keyword to handle cases
		// were we have a reference preceding the new-instance creation ('&new MyClass')
		int start = classInstanceCreation.getStart();
		visitTextNode(start, start + 3, true, 0);
		className.accept(this);
		if (hasParentheses)
		{
			// create a constructor node
			List<Expression> ctorParams = classInstanceCreation.ctorParams();
			pushParametersInParentheses(className.getEnd(), classInstanceCreation.getEnd(), ctorParams);
		}
		// check and push a semicolon (if appears after the end of this instance creation)
		// pushSemicolon(creationEnd, false, true);
		return false;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * org.eclipse.php.internal.core.ast.visitor.AbstractVisitor#visit(org.eclipse.php.internal.core.ast.nodes.ClassName
	 * )
	 */
	@Override
	public boolean visit(ClassName className)
	{
		visitTextNode(className, true, 1);
		return false;
	}

	/*
	 * (non-Javadoc)
	 * @seeorg.eclipse.php.internal.core.ast.visitor.AbstractVisitor#visit(org.eclipse.php.internal.core.ast.nodes.
	 * CloneExpression)
	 */
	@Override
	public boolean visit(CloneExpression cloneExpression)
	{
		// push the 'clone' as invocation.
		int cloneStart = cloneExpression.getStart();
		pushFunctionInvocationName(cloneExpression, cloneStart, cloneStart + 5);
		// push the expression as if it's in a parentheses expression
		List<ASTNode> expressionInList = new ArrayList<ASTNode>(1);
		expressionInList.add(cloneExpression.getExpression());
		pushParametersInParentheses(cloneStart + 5, cloneExpression.getEnd(), expressionInList);
		return false;
	}

	/*
	 * (non-Javadoc)
	 * @seeorg.eclipse.php.internal.core.ast.visitor.AbstractVisitor#visit(org.eclipse.php.internal.core.ast.nodes.
	 * ConditionalExpression)
	 */
	@Override
	public boolean visit(ConditionalExpression conditionalExpression)
	{
		Expression condition = conditionalExpression.getCondition();
		condition.accept(this);
		Expression ifTrue = conditionalExpression.getIfTrue();
		Expression ifFalse = conditionalExpression.getIfFalse();
		// push the conditional operator
		int conditionalOpOffset = condition.getEnd() + document.get(condition.getEnd(), ifTrue.getStart()).indexOf('?');
		pushTypeOperator(TypeOperator.CONDITIONAL, conditionalOpOffset, false);
		// visit the true part
		ifTrue.accept(this);
		// push the colon separator
		int colonOffset = ifTrue.getEnd() + document.get(ifTrue.getEnd(), ifFalse.getStart()).indexOf(':');
		pushTypeOperator(TypeOperator.CONDITIONAL_COLON, colonOffset, false);
		// visit the false part
		ifFalse.accept(this);
		return false;
	}

	/*
	 * (non-Javadoc)
	 * @seeorg.eclipse.php.internal.core.ast.visitor.AbstractVisitor#visit(org.eclipse.php.internal.core.ast.nodes.
	 * ConstantDeclaration)
	 */
	@Override
	public boolean visit(ConstantDeclaration classConstantDeclaration)
	{
		// push the 'const' keyword.
		pushKeyword(classConstantDeclaration.getStart(), 5, true);
		// Push the declarations. Each has an assignment char and they are separated by commas.
		List<? extends ASTNode> leftNodes = classConstantDeclaration.names();
		List<? extends ASTNode> rightNodes = classConstantDeclaration.initializers();
		visitNodeLists(leftNodes, rightNodes, TypeOperator.ASSIGNMENT, TypePunctuation.COMMA);
		// locate the semicolon at the end of the expression. If exists, push it as a node.
		int end = rightNodes.get(rightNodes.size() - 1).getEnd();
		findAndPushPunctuationNode(TypePunctuation.SEMICOLON, end, false, true);
		return false;
	}

	/**
	 * This method is used to visit and push nodes that are separated with some delimiter, and potentially have
	 * operators between them.<br>
	 * For example, const A='a', B='b';
	 * 
	 * @param leftNodes
	 *            A list of ASTNodes.
	 * @param rightNodes
	 *            A list of ASTNodes that are pairing with the leftNodes. In case there are no pairs, this list may be
	 *            null. However, if there are pairs (such as assignments), the size of this group must match the size of
	 *            the left group.
	 * @param pairsOperator
	 *            The operator {@link TypeOperator} that should appear between the left and the right pair, when there
	 *            are pairs. May be null only when the rightNodes are null.
	 * @param pairsSeparator
	 *            A separator that appears between the leftNodes. If there are pairs, the separator appears between one
	 *            pair to the other (may only be null in case a separator is not needed, e.g. we have only one
	 *            item/pair)
	 */
	private void visitNodeLists(List<? extends ASTNode> leftNodes, List<? extends ASTNode> rightNodes,
			TypeOperator pairsOperator, TypePunctuation pairsSeparator)
	{
		// push the expressions one at a time, with comma nodes between them.
		int leftSize = leftNodes.size();
		for (int i = 0; i < leftSize; i++)
		{
			ASTNode left = leftNodes.get(i);
			ASTNode right = (rightNodes != null) ? rightNodes.get(i) : null;
			left.accept(this);
			if (right != null && pairsOperator != null)
			{
				int startIndex = left.getEnd();
				String text = document.get(startIndex, right.getStart());
				String typeStr = pairsOperator.toString();
				startIndex += text.indexOf(typeStr);
				pushTypeOperator(pairsOperator, startIndex, false);
				right.accept(this);
			}
			// add a separator if needed
			if (i + 1 < leftNodes.size())
			{
				int startIndex = left.getEnd();
				String text = document.get(startIndex, leftNodes.get(i + 1).getStart());
				String separatorStr = pairsSeparator.toString();
				startIndex += text.indexOf(separatorStr);
				pushTypePunctuation(pairsSeparator, startIndex);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * @seeorg.eclipse.php.internal.core.ast.visitor.AbstractVisitor#visit(org.eclipse.php.internal.core.ast.nodes.
	 * ContinueStatement)
	 */
	@Override
	public boolean visit(ContinueStatement continueStatement)
	{
		// push the 'continue' keyword.
		int continueStart = continueStatement.getStart();
		pushKeyword(continueStart, 8, true);
		// visit the continue expression, if exists
		Expression expression = continueStatement.getExpression();
		if (expression != null)
		{
			expression.accept(this);
		}
		else
		{
			pushSpaceConsumingNode(continueStatement.getEnd() - 1);
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * @seeorg.eclipse.php.internal.core.ast.visitor.AbstractVisitor#visit(org.eclipse.php.internal.core.ast.nodes.
	 * DeclareStatement)
	 */
	@Override
	public boolean visit(DeclareStatement declareStatement)
	{
		Statement body = declareStatement.getBody();
		List<Identifier> directiveNames = declareStatement.directiveNames();
		List<Expression> directiveValues = declareStatement.directiveValues();
		// push the declare keyword as a function invocation
		int start = declareStatement.getStart();
		pushFunctionInvocationName(declareStatement, start, start + 7);
		// push the parentheses with the names and the values that we have inside
		int openParen = builder.locateCharForward(document, '(', start + 7);
		int closeParen = builder.locateCharBackward(document, ')', (body != null) ? body.getStart() : declareStatement
				.getEnd());
		FormatterPHPParenthesesNode parenthesesNode = new FormatterPHPParenthesesNode(document);
		parenthesesNode.setBegin(builder.createTextNode(document, openParen, openParen + 1));
		builder.push(parenthesesNode);
		// push the list of names and values
		visitNodeLists(directiveNames, directiveValues, TypeOperator.ASSIGNMENT, TypePunctuation.COMMA);
		builder.checkedPop(parenthesesNode, -1);
		parenthesesNode.setEnd(builder.createTextNode(document, closeParen, closeParen + 1));
		if (body != null)
		{
			body.accept(this);
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * org.eclipse.php.internal.core.ast.visitor.AbstractVisitor#visit(org.eclipse.php.internal.core.ast.nodes.EchoStatement
	 * )
	 */
	@Override
	public boolean visit(EchoStatement echoStatement)
	{
		// push the 'echo' invocation.
		int echoStart = echoStatement.getStart();
		pushFunctionInvocationName(echoStatement, echoStart, echoStart + 4);
		// push the expressions one at a time, with comma nodes between them.
		List<Expression> expressions = echoStatement.expressions();
		pushParametersInParentheses(echoStart + 4, echoStatement.getEnd(), expressions);
		// locate the semicolon at the end of the expression. If exists, push it as a node.
		int end = expressions.get(expressions.size() - 1).getEnd();
		findAndPushPunctuationNode(TypePunctuation.SEMICOLON, end, false, true);
		return false;
	}

	/*
	 * (non-Javadoc)
	 * @seeorg.eclipse.php.internal.core.ast.visitor.AbstractVisitor#visit(org.eclipse.php.internal.core.ast.nodes.
	 * EmptyStatement)
	 */
	@Override
	public boolean visit(EmptyStatement emptyStatement)
	{
		visitTextNode(emptyStatement, true, 0);
		return false;
	}

	/*
	 * (non-Javadoc)
	 * @seeorg.eclipse.php.internal.core.ast.visitor.AbstractVisitor#visit(org.eclipse.php.internal.core.ast.nodes.
	 * ExpressionStatement)
	 */
	@Override
	public boolean visit(ExpressionStatement expressionStatement)
	{
		int expressionEnd = expressionStatement.getEnd();
		boolean endsWithSemicolon = document.charAt(expressionEnd - 1) == ';';
		FormatterPHPExpressionWrapperNode expressionNode = new FormatterPHPExpressionWrapperNode(document);
		int start = expressionStatement.getStart();
		int end = expressionEnd;
		if (endsWithSemicolon)
		{
			end--;
		}
		expressionNode.setBegin(builder.createTextNode(document, start, start));
		builder.push(expressionNode);
		expressionStatement.childrenAccept(this);
		expressionNode.setEnd(builder.createTextNode(document, end, end));
		builder.checkedPop(expressionNode, -1);
		// push a semicolon if we have one
		if (endsWithSemicolon)
		{
			findAndPushPunctuationNode(TypePunctuation.SEMICOLON, end, false, true);
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * org.eclipse.php.internal.core.ast.visitor.AbstractVisitor#visit(org.eclipse.php.internal.core.ast.nodes.FieldAccess
	 * )
	 */
	@Override
	public boolean visit(FieldAccess fieldAccess)
	{
		VariableBase dispatcher = fieldAccess.getDispatcher();
		VariableBase member = fieldAccess.getMember();
		visitLeftRightExpression(fieldAccess, dispatcher, member, INVOCATION_ARROW);
		return false;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * org.eclipse.php.internal.core.ast.visitor.AbstractVisitor#visit(org.eclipse.php.internal.core.ast.nodes.ForStatement
	 * )
	 */
	@Override
	public boolean visit(ForStatement forStatement)
	{
		List<Expression> initializers = forStatement.initializers();
		List<Expression> conditions = forStatement.conditions();
		List<Expression> updaters = forStatement.updaters();
		Statement body = forStatement.getBody();
		// visit the 'for' keyword
		int declarationEndOffset = forStatement.getStart() + 3;
		visitCommonDeclaration(forStatement, declarationEndOffset, true);
		// visit the elements in the parentheses
		int expressionEndOffset = (body != null) ? body.getStart() : forStatement.getEnd();
		int openParen = builder.locateCharForward(document, '(', declarationEndOffset);
		int closeParen = builder.locateCharBackward(document, ')', expressionEndOffset);
		FormatterPHPParenthesesNode parenthesesNode = new FormatterPHPParenthesesNode(document);
		parenthesesNode.setBegin(builder.createTextNode(document, openParen, openParen + 1));
		builder.push(parenthesesNode);
		// visit the initializers, the conditions and the updaters.
		// between them, push the semicolons
		visitNodeLists(initializers, null, null, TypePunctuation.COMMA);
		int semicolonOffset = builder.locateCharForward(document, ';', declarationEndOffset);
		pushTypePunctuation(TypePunctuation.FOR_SEMICOLON, semicolonOffset);
		visitNodeLists(conditions, null, null, TypePunctuation.COMMA);
		semicolonOffset = builder.locateCharForward(document, ';', semicolonOffset + 1);
		pushTypePunctuation(TypePunctuation.FOR_SEMICOLON, semicolonOffset);
		visitNodeLists(updaters, null, null, TypePunctuation.COMMA);
		// close the parentheses node.
		builder.checkedPop(parenthesesNode, -1);
		parenthesesNode.setEnd(builder.createTextNode(document, closeParen, closeParen + 1));
		// in case we have a 'body', visit it.
		commonVisitBlockBody(forStatement, body);
		return false;
	}

	/*
	 * (non-Javadoc)
	 * @seeorg.eclipse.php.internal.core.ast.visitor.AbstractVisitor#visit(org.eclipse.php.internal.core.ast.nodes.
	 * ForEachStatement)
	 */
	@Override
	public boolean visit(ForEachStatement forEachStatement)
	{
		Expression expression = forEachStatement.getExpression();
		Expression key = forEachStatement.getKey(); // the 'key' is optional
		Expression value = forEachStatement.getValue();
		Statement body = forEachStatement.getStatement();
		// visit the 'foreach' keyword
		int declarationEndOffset = forEachStatement.getStart() + 7;
		visitCommonDeclaration(forEachStatement, declarationEndOffset, true);
		// visit the elements in the parentheses
		int expressionEndOffset = (body != null) ? body.getStart() : forEachStatement.getEnd();
		int openParen = builder.locateCharForward(document, '(', declarationEndOffset);
		int closeParen = builder.locateCharBackward(document, ')', expressionEndOffset);
		FormatterPHPParenthesesNode parenthesesNode = new FormatterPHPParenthesesNode(document);
		parenthesesNode.setBegin(builder.createTextNode(document, openParen, openParen + 1));
		builder.push(parenthesesNode);
		// push the expression
		visitTextNode(expression, true, 0);
		// add the 'as' node (it's between the expression and the key/value)
		int endLookupForAs = (key != null) ? key.getStart() : value.getStart();
		String txt = document.get(expression.getEnd(), endLookupForAs);
		int asStart = expression.getEnd() + txt.toLowerCase().indexOf("as"); //$NON-NLS-1$
		visitTextNode(asStart, asStart + 2, true, 1, 1);
		// push the key and the value.
		if (key != null)
		{
			visitLeftRightExpression(null, key, value, TypeOperator.KEY_VALUE.toString());
		}
		else
		{
			// push only the value as a text node
			visitTextNode(value, true, 1);
		}
		// close the parentheses node.
		builder.checkedPop(parenthesesNode, -1);
		parenthesesNode.setEnd(builder.createTextNode(document, closeParen, closeParen + 1));

		// in case we have a 'body', visit it.
		commonVisitBlockBody(forEachStatement, body);
		return false;
	}

	/*
	 * (non-Javadoc)
	 * @seeorg.eclipse.php.internal.core.ast.visitor.AbstractVisitor#visit(org.eclipse.php.internal.core.ast.nodes.
	 * WhileStatement)
	 */
	@Override
	public boolean visit(WhileStatement whileStatement)
	{
		visitCommonLoopBlock(whileStatement, whileStatement.getStart() + 5, whileStatement.getBody(), whileStatement
				.getCondition());
		return false;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * org.eclipse.php.internal.core.ast.visitor.AbstractVisitor#visit(org.eclipse.php.internal.core.ast.nodes.DoStatement
	 * )
	 */
	@Override
	public boolean visit(DoStatement doStatement)
	{
		Statement body = doStatement.getBody();
		// First, push the 'do' declaration node and its body
		visitCommonLoopBlock(doStatement, doStatement.getStart() + 2, body, null);
		// now deal with the 'while' condition part. we need to include the word 'while' that appears
		// somewhere between the block-end and the condition start.
		// We wrap this node as a begin-end node that will hold the condition internals as children
		FormatterPHPNonBlockedWhileNode whileNode = new FormatterPHPNonBlockedWhileNode(document);
		// Search for the exact 'while' start offset
		int whileBeginOffset = builder.locateCharForward(document, 'w', body.getEnd());
		int conditionEnd = locateCharMatchInLine(doStatement.getEnd(), SEMICOLON, document, true);
		whileNode.setBegin(builder.createTextNode(document, whileBeginOffset, conditionEnd));
		builder.push(whileNode);
		builder.checkedPop(whileNode, -1);
		return false;
	}

	/*
	 * (non-Javadoc)
	 * @seeorg.eclipse.php.internal.core.ast.visitor.AbstractVisitor#visit(org.eclipse.php.internal.core.ast.nodes.
	 * FunctionDeclaration)
	 */
	@Override
	public boolean visit(FunctionDeclaration functionDeclaration)
	{
		visitFunctionDeclaration(functionDeclaration, functionDeclaration.getFunctionName(), functionDeclaration
				.formalParameters(), functionDeclaration.getBody());
		return false;
	}

	/*
	 * (non-Javadoc)
	 * @seeorg.eclipse.php.internal.core.ast.visitor.AbstractVisitor#visit(org.eclipse.php.internal.core.ast.nodes.
	 * FunctionInvocation)
	 */
	@Override
	public boolean visit(FunctionInvocation functionInvocation)
	{
		visitFunctionInvocation(functionInvocation);
		return false;
	}

	/*
	 * (non-Javadoc)
	 * @seeorg.eclipse.php.internal.core.ast.visitor.AbstractVisitor#visit(org.eclipse.php.internal.core.ast.nodes.
	 * GlobalStatement)
	 */
	@Override
	public boolean visit(GlobalStatement globalStatement)
	{
		pushKeyword(globalStatement.getStart(), 6, true);
		List<Variable> variables = globalStatement.variables();
		visitNodeLists(variables, null, null, TypePunctuation.COMMA);
		// we also need to push the semicolon for the global
		findAndPushPunctuationNode(TypePunctuation.SEMICOLON, globalStatement.getEnd() - 1, false, true);
		return false;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * org.eclipse.php.internal.core.ast.visitor.AbstractVisitor#visit(org.eclipse.php.internal.core.ast.nodes.GotoLabel
	 * )
	 */
	@Override
	public boolean visit(GotoLabel gotoLabel)
	{
		// To Goto label is setting the end by including the spaces between the last non-white char
		// and the terminating colon. We trim those spaces in the formatting.
		FormatterPHPLineStartingNode lineStartingNode = new FormatterPHPLineStartingNode(document);
		int start = gotoLabel.getStart();
		int end = gotoLabel.getEnd();
		int trimmedLength = document.get(start, end - 1).trim().length();
		int labelEnd = end - (end - start - trimmedLength);
		lineStartingNode.setBegin(builder.createTextNode(document, start, labelEnd));
		builder.push(lineStartingNode);
		builder.checkedPop(lineStartingNode, -1);
		findAndPushPunctuationNode(TypePunctuation.GOTO_COLON, end - 1, false, true);
		return false;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * org.eclipse.php.internal.core.ast.visitor.AbstractVisitor#visit(org.eclipse.php.internal.core.ast.nodes.GotoStatement
	 * )
	 */
	@Override
	public boolean visit(GotoStatement gotoStatement)
	{
		Identifier label = gotoStatement.getLabel();
		pushKeyword(gotoStatement.getStart(), 4, true);
		label.accept(this);
		findAndPushPunctuationNode(TypePunctuation.SEMICOLON, gotoStatement.getEnd() - 1, false, true);
		return false;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * org.eclipse.php.internal.core.ast.visitor.AbstractVisitor#visit(org.eclipse.php.internal.core.ast.nodes.Identifier
	 * )
	 */
	@Override
	public boolean visit(Identifier identifier)
	{
		visitTextNode(identifier, true, 0);
		return false;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * org.eclipse.php.internal.core.ast.visitor.AbstractVisitor#visit(org.eclipse.php.internal.core.ast.nodes.IgnoreError
	 * )
	 */
	@Override
	public boolean visit(IgnoreError ignoreError)
	{
		// push the first sign ('@') as a simple text node.
		int start = ignoreError.getStart();
		int end = start + 1;
		visitTextNode(start, end, true, 0);
		// visit the expression
		ignoreError.getExpression().accept(this);
		return false;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * org.eclipse.php.internal.core.ast.visitor.AbstractVisitor#visit(org.eclipse.php.internal.core.ast.nodes.Include)
	 */
	@Override
	public boolean visit(Include include)
	{
		// push the 'include' keyword.
		int includeStart = include.getStart();
		int keywordLength = Include.getType(include.getIncludeType()).length();
		pushKeyword(includeStart, keywordLength, true);
		// visit the include expression.
		Expression expression = include.getExpression();
		expression.accept(this);
		return false;
	}

	/*
	 * (non-Javadoc)
	 * @seeorg.eclipse.php.internal.core.ast.visitor.AbstractVisitor#visit(org.eclipse.php.internal.core.ast.nodes.
	 * InfixExpression)
	 */
	@Override
	public boolean visit(InfixExpression infixExpression)
	{
		String operatorString = InfixExpression.getOperator(infixExpression.getOperator());
		ASTNode left = infixExpression.getLeft();
		ASTNode right = infixExpression.getRight();
		visitLeftRightExpression(infixExpression, left, right, operatorString);
		return false;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * org.eclipse.php.internal.core.ast.visitor.AbstractVisitor#visit(org.eclipse.php.internal.core.ast.nodes.InLineHtml
	 * )
	 */
	@Override
	public boolean visit(InLineHtml inLineHtml)
	{
		visitTextNode(inLineHtml, false, 0);
		return false;
	}

	/*
	 * (non-Javadoc)
	 * @seeorg.eclipse.php.internal.core.ast.visitor.AbstractVisitor#visit(org.eclipse.php.internal.core.ast.nodes.
	 * InstanceOfExpression)
	 */
	@Override
	public boolean visit(InstanceOfExpression instanceOfExpression)
	{
		Expression expression = instanceOfExpression.getExpression();
		ClassName className = instanceOfExpression.getClassName();
		// visit the left expression
		expression.accept(this);
		// locate the word 'instanceof' in the gap between the expression and the class name.
		int exprEnd = expression.getEnd();
		String txt = document.get(exprEnd, className.getStart());
		int instanceOfStart = exprEnd + txt.toLowerCase().indexOf("instanceof"); //$NON-NLS-1$
		visitTextNode(instanceOfStart, instanceOfStart + 10, true, 1);
		// visit the right class name
		className.accept(this);
		return false;
	}

	/*
	 * (non-Javadoc)
	 * @seeorg.eclipse.php.internal.core.ast.visitor.AbstractVisitor#visit(org.eclipse.php.internal.core.ast.nodes.
	 * InterfaceDeclaration)
	 */
	@Override
	public boolean visit(InterfaceDeclaration interfaceDeclaration)
	{
		visitTypeDeclaration(interfaceDeclaration);
		return false;
	}

	/*
	 * (non-Javadoc)
	 * @seeorg.eclipse.php.internal.core.ast.visitor.AbstractVisitor#visit(org.eclipse.php.internal.core.ast.nodes.
	 * LambdaFunctionDeclaration)
	 */
	@Override
	public boolean visit(LambdaFunctionDeclaration lambdaFunctionDeclaration)
	{
		visitFunctionDeclaration(lambdaFunctionDeclaration, null, lambdaFunctionDeclaration.formalParameters(),
				lambdaFunctionDeclaration.getBody());
		return false;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * org.eclipse.php.internal.core.ast.visitor.AbstractVisitor#visit(org.eclipse.php.internal.core.ast.nodes.ListVariable
	 * )
	 */
	@Override
	public boolean visit(ListVariable listVariable)
	{
		List<VariableBase> variables = listVariable.variables();
		int start = listVariable.getStart();
		pushFunctionInvocationName(listVariable, start, start + 4);
		pushParametersInParentheses(start + 4, listVariable.getEnd(), variables);
		return false;
	}

	/*
	 * (non-Javadoc)
	 * @seeorg.eclipse.php.internal.core.ast.visitor.AbstractVisitor#visit(org.eclipse.php.internal.core.ast.nodes.
	 * MethodDeclaration)
	 */
	@Override
	public boolean visit(MethodDeclaration methodDeclaration)
	{
		FunctionDeclaration function = methodDeclaration.getFunction();
		visitModifiers(methodDeclaration, function);
		function.accept(this);
		return false;
	}

	/*
	 * (non-Javadoc)
	 * @seeorg.eclipse.php.internal.core.ast.visitor.AbstractVisitor#visit(org.eclipse.php.internal.core.ast.nodes.
	 * FieldsDeclaration)
	 */
	@Override
	public boolean visit(FieldsDeclaration fieldsDeclaration)
	{
		// A class field declaration is treated in a similar way we treat a class method declaration
		Variable[] variableNames = fieldsDeclaration.getVariableNames();
		Variable firstVariable = variableNames[0];
		visitModifiers(fieldsDeclaration, firstVariable);
		// visit the variables and their values
		Expression[] initialValues = fieldsDeclaration.getInitialValues();
		// visit the variables and their initial values
		List<? extends ASTNode> variablesList = Arrays.asList(variableNames);
		List<? extends ASTNode> valuesList = (initialValues != null) ? Arrays.asList(initialValues) : null;
		visitNodeLists(variablesList, valuesList, TypeOperator.ASSIGNMENT, TypePunctuation.COMMA);
		// locate the push the semicolon at the end
		findAndPushPunctuationNode(TypePunctuation.SEMICOLON, fieldsDeclaration.getEnd() - 1, false, true);
		return false;
	}

	/*
	 * (non-Javadoc)
	 * @seeorg.eclipse.php.internal.core.ast.visitor.AbstractVisitor#visit(org.eclipse.php.internal.core.ast.nodes.
	 * MethodInvocation)
	 */
	@Override
	public boolean visit(MethodInvocation methodInvocation)
	{
		visitLeftRightExpression(methodInvocation, methodInvocation.getDispatcher(), methodInvocation.getMethod(),
				INVOCATION_ARROW);
		// note: we push the semicolon as part of the function-invocation that we have in this node.
		return false;
	}

	/*
	 * (non-Javadoc)
	 * @seeorg.eclipse.php.internal.core.ast.visitor.AbstractVisitor#visit(org.eclipse.php.internal.core.ast.nodes.
	 * StaticMethodInvocation)
	 */
	@Override
	public boolean visit(StaticMethodInvocation staticMethodInvocation)
	{
		visitLeftRightExpression(staticMethodInvocation, staticMethodInvocation.getClassName(), staticMethodInvocation
				.getMethod(), STATIC_INVOCATION);
		// note: we push the semicolon as part of the function-invocation that we have in this node.
		return false;
	}

	/*
	 * (non-Javadoc)
	 * @seeorg.eclipse.php.internal.core.ast.visitor.AbstractVisitor#visit(org.eclipse.php.internal.core.ast.nodes.
	 * NamespaceDeclaration)
	 */
	@Override
	public boolean visit(NamespaceDeclaration namespaceDeclaration)
	{
		pushKeyword(namespaceDeclaration.getStart(), 9, true);
		NamespaceName namespaceName = namespaceDeclaration.getName();
		namespaceName.accept(this);
		findAndPushPunctuationNode(TypePunctuation.SEMICOLON, namespaceName.getEnd(), false, true);
		// visit the namespace body block. This block is invisible one, but we wrap it in a special
		// namespace block to allow indentation customization.
		FormatterPHPNamespaceBlockNode bodyNode = new FormatterPHPNamespaceBlockNode(document);
		Block body = namespaceDeclaration.getBody();
		int start = body.getStart();
		int end = body.getEnd();
		bodyNode.setBegin(builder.createTextNode(document, start, start));
		builder.push(bodyNode);
		body.childrenAccept(this);
		bodyNode.setEnd(builder.createTextNode(document, end, end));
		builder.checkedPop(bodyNode, namespaceDeclaration.getEnd());
		return false;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * org.eclipse.php.internal.core.ast.visitor.AbstractVisitor#visit(org.eclipse.php.internal.core.ast.nodes.NamespaceName
	 * )
	 */
	@Override
	public boolean visit(NamespaceName namespaceName)
	{
		List<Identifier> segments = namespaceName.segments();
		int start = namespaceName.getStart();
		if (namespaceName.isGlobal())
		{
			// look for the '\' that came before the name and push it separately.
			start = builder.locateCharBackward(document, '\\', start);
			pushTypePunctuation(TypePunctuation.NAMESPACE_SEPARATOR, start);
		}
		// Push the rest of the segments as a list of nodes.
		visitNodeLists(segments, null, null, TypePunctuation.NAMESPACE_SEPARATOR);
		return false;
	}

	/*
	 * (non-Javadoc)
	 * @seeorg.eclipse.php.internal.core.ast.visitor.AbstractVisitor#visit(org.eclipse.php.internal.core.ast.nodes.
	 * ParenthesisExpression)
	 */
	@Override
	public boolean visit(ParenthesisExpression parenthesisExpression)
	{
		FormatterPHPParenthesesNode parenthesesNode = new FormatterPHPParenthesesNode(document);
		int start = parenthesisExpression.getStart();
		parenthesesNode.setBegin(builder.createTextNode(document, start, start + 1));
		builder.push(parenthesesNode);
		parenthesisExpression.childrenAccept(this);
		builder.checkedPop(parenthesesNode, -1);
		int end = parenthesisExpression.getEnd();
		parenthesesNode.setEnd(builder.createTextNode(document, end - 1, end));
		return false;
	}

	/*
	 * (non-Javadoc)
	 * @seeorg.eclipse.php.internal.core.ast.visitor.AbstractVisitor#visit(org.eclipse.php.internal.core.ast.nodes.
	 * PostfixExpression)
	 */
	@Override
	public boolean visit(PostfixExpression postfixExpression)
	{
		VariableBase var = postfixExpression.getVariable();
		TypeOperator op;
		if (postfixExpression.getOperator() == PostfixExpression.OP_INC)
		{
			op = TypeOperator.POSTFIX_INCREMENT;
		}
		else
		{
			op = TypeOperator.POSTFIX_DECREMENT;
		}
		var.accept(this);
		int leftOffset = var.getEnd();
		int operatorOffset = document.get(leftOffset, postfixExpression.getEnd()).indexOf(op.toString()) + leftOffset;
		pushTypeOperator(op, operatorOffset, false);
		return false;
	}

	/*
	 * (non-Javadoc)
	 * @seeorg.eclipse.php.internal.core.ast.visitor.AbstractVisitor#visit(org.eclipse.php.internal.core.ast.nodes.
	 * PrefixExpression)
	 */
	@Override
	public boolean visit(PrefixExpression prefixExpression)
	{
		VariableBase var = prefixExpression.getVariable();
		TypeOperator op;
		if (prefixExpression.getOperator() == PrefixExpression.OP_INC)
		{
			op = TypeOperator.PREFIX_INCREMENT;
		}
		else
		{
			op = TypeOperator.PREFIX_DECREMENT;
		}
		int leftOffset = prefixExpression.getStart();
		int operatorOffset = document.get(leftOffset, var.getStart()).indexOf(op.toString()) + leftOffset;
		pushTypeOperator(op, operatorOffset, false);
		var.accept(this);
		return false;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * org.eclipse.php.internal.core.ast.visitor.AbstractVisitor#visit(org.eclipse.php.internal.core.ast.nodes.Quote)
	 */
	@Override
	public boolean visit(Quote quote)
	{
		int quoteType = quote.getQuoteType();
		if (quoteType == Quote.QT_HEREDOC || quoteType == Quote.QT_NOWDOC)
		{
			FormatterPHPHeredocNode heredocNode = new FormatterPHPHeredocNode(document, quote.getStart(), quote
					.getEnd());
			IFormatterContainerNode parentNode = builder.peek();
			parentNode.addChild(heredocNode);
		}
		else
		{
			visitTextNode(quote, true, 0);
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * org.eclipse.php.internal.core.ast.visitor.AbstractVisitor#visit(org.eclipse.php.internal.core.ast.nodes.Reference
	 * )
	 */
	@Override
	public boolean visit(Reference reference)
	{
		// push the first reference sign ('&') as a simple text node.
		int start = reference.getStart();
		int end = start + 1;
		visitTextNode(start, end, true, 0);
		// visit the referenced expression
		reference.getExpression().accept(this);
		return false;
	}

	/*
	 * (non-Javadoc)
	 * @seeorg.eclipse.php.internal.core.ast.visitor.AbstractVisitor#visit(org.eclipse.php.internal.core.ast.nodes.
	 * ReflectionVariable)
	 */
	@Override
	public boolean visit(ReflectionVariable reflectionVariable)
	{
		// push the first dollar sign as a simple text node.
		int start = reflectionVariable.getStart();
		int end = start + 1;
		visitTextNode(start, end, true, 0);
		// visit the name variable
		reflectionVariable.getName().accept(this);
		return false;
	}

	/*
	 * (non-Javadoc)
	 * @seeorg.eclipse.php.internal.core.ast.visitor.AbstractVisitor#visit(org.eclipse.php.internal.core.ast.nodes.
	 * ReturnStatement)
	 */
	@Override
	public boolean visit(ReturnStatement returnStatement)
	{
		// push the 'return' keyword.
		int returnStart = returnStatement.getStart();
		pushKeyword(returnStart, 6, true);
		// visit the return expression.
		Expression expression = returnStatement.getExpression();
		if (expression != null)
		{
			expression.accept(this);
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * org.eclipse.php.internal.core.ast.visitor.AbstractVisitor#visit(org.eclipse.php.internal.core.ast.nodes.Scalar)
	 */
	@Override
	public boolean visit(Scalar scalar)
	{
		visitTextNode(scalar, true, 0);
		return false;
	}

	/*
	 * (non-Javadoc)
	 * @seeorg.eclipse.php.internal.core.ast.visitor.AbstractVisitor#visit(org.eclipse.php.internal.core.ast.nodes.
	 * StaticConstantAccess)
	 */
	@Override
	public boolean visit(StaticConstantAccess classConstantAccess)
	{
		visitLeftRightExpression(classConstantAccess, classConstantAccess.getClassName(), classConstantAccess
				.getConstant(), TypeOperator.STATIC_INVOCATION.toString());
		return false;
	}

	/*
	 * (non-Javadoc)
	 * @seeorg.eclipse.php.internal.core.ast.visitor.AbstractVisitor#visit(org.eclipse.php.internal.core.ast.nodes.
	 * StaticFieldAccess)
	 */
	@Override
	public boolean visit(StaticFieldAccess staticFieldAccess)
	{
		visitLeftRightExpression(staticFieldAccess, staticFieldAccess.getClassName(), staticFieldAccess.getField(),
				TypeOperator.STATIC_INVOCATION.toString());
		return false;
	}

	/*
	 * (non-Javadoc)
	 * @seeorg.eclipse.php.internal.core.ast.visitor.AbstractVisitor#visit(org.eclipse.php.internal.core.ast.nodes.
	 * StaticStatement)
	 */
	@Override
	public boolean visit(StaticStatement staticStatement)
	{
		pushKeyword(staticStatement.getStart(), 6, true);
		List<Expression> expressions = staticStatement.expressions();
		visitNodeLists(expressions, null, null, TypePunctuation.COMMA);
		// push the ending semicolon
		findAndPushPunctuationNode(TypePunctuation.SEMICOLON, staticStatement.getEnd() - 1, false, true);
		return false;
	}

	/*
	 * (non-Javadoc)
	 * @seeorg.eclipse.php.internal.core.ast.visitor.AbstractVisitor#visit(org.eclipse.php.internal.core.ast.nodes.
	 * SwitchStatement)
	 */
	@Override
	public boolean visit(SwitchStatement switchStatement)
	{
		Block body = switchStatement.getBody();
		// In case the body is not curly, we are dealing with an alternative syntax (e.g. colon and 'endswitch' instead
		// of curly open and close for the body).
		boolean isAlternativeSyntax = !body.isCurly();
		// Push the switch-case declaration node
		FormatterPHPDeclarationNode switchNode = new FormatterPHPDeclarationNode(document, true, switchStatement);
		int rightParenthesis = builder.locateCharBackward(document, ')', body.getStart());
		switchNode.setBegin(builder.createTextNode(document, switchStatement.getStart(), rightParenthesis + 1));
		builder.push(switchNode);
		builder.checkedPop(switchNode, -1);

		// push a switch-case body node
		int blockStart = body.getStart();
		FormatterPHPSwitchNode blockNode = new FormatterPHPSwitchNode(document);
		blockNode.setBegin(builder.createTextNode(document, blockStart, blockStart + 1));
		builder.push(blockNode);
		// visit the children under that block node
		body.childrenAccept(this);
		int endingOffset = switchStatement.getEnd();
		endingOffset--;
		if (isAlternativeSyntax)
		{
			// deduct the 'endswitch' length.
			// we already removed 1 offset above, so we remove the extra 8.
			endingOffset -= 8;
		}
		blockNode.setEnd(builder.createTextNode(document, endingOffset, endingOffset + 1));
		// pop the block node
		builder.checkedPop(blockNode, -1);
		return false;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * org.eclipse.php.internal.core.ast.visitor.AbstractVisitor#visit(org.eclipse.php.internal.core.ast.nodes.SwitchCase
	 * )
	 */
	@Override
	public boolean visit(SwitchCase switchCase)
	{
		List<Statement> actions = switchCase.actions();
		boolean hasBlockedChild = (actions.size() == 1 && actions.get(0).getType() == ASTNode.BLOCK);
		// compute the colon position
		int colonOffset;
		if (actions.size() > 0)
		{
			colonOffset = builder.locateCharBackward(document, ':', actions.get(0).getStart());
		}
		else
		{
			colonOffset = builder.locateCharForward(document, ':', switchCase.getValue().getEnd());
		}
		// push the case/default node till the colon.
		// We create a begin-end node that will hold a case-colon node as an inner child to manage its spacing.
		FormatterPHPExpressionWrapperNode caseNode = new FormatterPHPExpressionWrapperNode(document);
		// get the value-end offset. In case it's a 'default' case, set the end at the end offset of the word 'default'
		int valueEnd = switchCase.isDefault() ? switchCase.getStart() + 7 : switchCase.getValue().getEnd();
		caseNode.setBegin(builder.createTextNode(document, switchCase.getStart(), valueEnd));
		caseNode.setEnd(builder.createTextNode(document, colonOffset + 1, colonOffset + 1));
		builder.push(caseNode);
		// push the colon node
		FormatterPHPCaseColonNode caseColonNode = new FormatterPHPCaseColonNode(document, hasBlockedChild);
		caseColonNode.setBegin(builder.createTextNode(document, colonOffset, colonOffset + 1));
		builder.push(caseColonNode);
		builder.checkedPop(caseColonNode, -1);
		builder.checkedPop(caseNode, -1);
		// push the case/default content
		FormatterPHPCaseBodyNode caseBodyNode = new FormatterPHPCaseBodyNode(document, hasBlockedChild);
		if (hasBlockedChild)
		{
			Block body = (Block) actions.get(0);
			// we have a 'case' with a curly-block
			caseBodyNode.setBegin(builder.createTextNode(document, body.getStart(), body.getStart() + 1));
			builder.push(caseBodyNode);
			body.childrenAccept(this);
			int endingOffset = body.getEnd() - 1;
			builder.checkedPop(caseBodyNode, endingOffset);
			int end = locateCharMatchInLine(endingOffset + 1, SEMICOLON_AND_COLON, document, false);
			caseBodyNode.setEnd(builder.createTextNode(document, endingOffset, end));
		}
		else
		{
			if (!actions.isEmpty())
			{
				int start = actions.get(0).getStart();
				int end = actions.get(actions.size() - 1).getEnd();
				caseBodyNode.setBegin(builder.createTextNode(document, start, start));
				builder.push(caseBodyNode);
				for (Statement st : actions)
				{
					st.accept(this);
				}
				builder.checkedPop(caseBodyNode, switchCase.getEnd());
				caseBodyNode.setEnd(builder.createTextNode(document, end, end));
			}
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * @seeorg.eclipse.php.internal.core.ast.visitor.AbstractVisitor#visit(org.eclipse.php.internal.core.ast.nodes.
	 * CastExpression)
	 */
	@Override
	public boolean visit(CastExpression castExpression)
	{
		Expression expression = castExpression.getExpression();
		// push the parentheses with the case type inside them
		int castCloserOffset = builder.locateCharBackward(document, ')', expression.getStart());
		visitTextNode(castExpression.getStart(), castCloserOffset, true, 0);
		// push the expression
		expression.accept(this);
		return false;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * org.eclipse.php.internal.core.ast.visitor.AbstractVisitor#visit(org.eclipse.php.internal.core.ast.nodes.CatchClause
	 * )
	 */
	@Override
	public boolean visit(CatchClause catchClause)
	{
		int declarationEnd = catchClause.getClassName().getEnd();
		declarationEnd = builder.locateCharForward(document, ')', declarationEnd) + 1;
		visitCommonDeclaration(catchClause, declarationEnd, true);
		visitBlockNode(catchClause.getBody(), catchClause, true);
		return false;
	}

	/*
	 * (non-Javadoc)
	 * @seeorg.eclipse.php.internal.core.ast.visitor.AbstractVisitor#visit(org.eclipse.php.internal.core.ast.nodes.
	 * ThrowStatement)
	 */
	@Override
	public boolean visit(ThrowStatement throwStatement)
	{
		pushKeyword(throwStatement.getStart(), 5, true);
		Expression expression = throwStatement.getExpression();
		expression.accept(this);
		findAndPushPunctuationNode(TypePunctuation.SEMICOLON, expression.getEnd(), false, true);
		return false;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * org.eclipse.php.internal.core.ast.visitor.AbstractVisitor#visit(org.eclipse.php.internal.core.ast.nodes.TryStatement
	 * )
	 */
	@Override
	public boolean visit(TryStatement tryStatement)
	{
		visitCommonDeclaration(tryStatement, tryStatement.getStart() + 3, true);
		visitBlockNode(tryStatement.getBody(), tryStatement, true);
		List<CatchClause> catchClauses = tryStatement.catchClauses();
		for (CatchClause catchClause : catchClauses)
		{
			catchClause.accept(this);
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * @seeorg.eclipse.php.internal.core.ast.visitor.AbstractVisitor#visit(org.eclipse.php.internal.core.ast.nodes.
	 * UnaryOperation)
	 */
	@Override
	public boolean visit(UnaryOperation unaryOperation)
	{
		Expression expression = unaryOperation.getExpression();
		String operationString = unaryOperation.getOperationString();
		TypeOperator typeOperator = TypeOperator.getTypeOperator(operationString);
		pushTypeOperator(typeOperator, unaryOperation.getStart(), true);
		expression.accept(this);
		return false;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * org.eclipse.php.internal.core.ast.visitor.AbstractVisitor#visit(org.eclipse.php.internal.core.ast.nodes.UseStatement
	 * )
	 */
	@Override
	public boolean visit(UseStatement useStatement)
	{
		pushKeyword(useStatement.getStart(), 3, true);
		List<UseStatementPart> parts = useStatement.parts();
		visitNodeLists(parts, null, null, TypePunctuation.COMMA);
		findAndPushPunctuationNode(TypePunctuation.SEMICOLON, useStatement.getEnd() - 1, false, true);
		return false;
	}

	/*
	 * (non-Javadoc)
	 * @seeorg.eclipse.php.internal.core.ast.visitor.AbstractVisitor#visit(org.eclipse.php.internal.core.ast.nodes.
	 * UseStatementPart)
	 */
	@Override
	public boolean visit(UseStatementPart useStatementPart)
	{
		NamespaceName namespaceName = useStatementPart.getName();
		Identifier alias = useStatementPart.getAlias();
		// visit the namespace name
		namespaceName.accept(this);
		// in case it has an alias, add the 'as' node and then visit the alias name.
		if (alias != null)
		{
			String text = document.get(namespaceName.getEnd(), alias.getStart());
			int asOffset = text.toLowerCase().indexOf("as"); //$NON-NLS-1$
			asOffset += namespaceName.getEnd();
			pushKeyword(asOffset, 2, false);
			alias.accept(this);
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * org.eclipse.php.internal.core.ast.visitor.AbstractVisitor#visit(org.eclipse.php.internal.core.ast.nodes.Variable)
	 */
	@Override
	public boolean visit(Variable variable)
	{
		visitTextNode(variable, true, 0);
		return false;
	}

	// ###################### Helper Methods ###################### //

	/**
	 * Visit and push a class/interface declaration
	 * 
	 * @param typeDeclaration
	 */
	private void visitTypeDeclaration(TypeDeclaration typeDeclaration)
	{
		// Locate the end offset of the declaration (before the block starts)
		Block body = typeDeclaration.getBody();
		int declarationBeginEnd = body.getStart() - 1;
		List<Identifier> interfaces = typeDeclaration.interfaces();
		if (interfaces != null && !interfaces.isEmpty())
		{
			declarationBeginEnd = interfaces.get(interfaces.size() - 1).getEnd();
		}
		else if (typeDeclaration.getType() == ASTNode.CLASS_DECLARATION
				&& ((ClassDeclaration) typeDeclaration).getSuperClass() != null)
		{
			declarationBeginEnd = ((ClassDeclaration) typeDeclaration).getSuperClass().getEnd();
		}
		else
		{
			declarationBeginEnd = typeDeclaration.getName().getEnd();
		}
		FormatterPHPDeclarationNode typeNode = new FormatterPHPDeclarationNode(document, true, typeDeclaration);
		typeNode.setBegin(builder.createTextNode(document, typeDeclaration.getStart(), declarationBeginEnd));
		builder.push(typeNode);
		builder.checkedPop(typeNode, -1);

		// add the class body
		FormatterPHPTypeBodyNode typeBodyNode = new FormatterPHPTypeBodyNode(document);
		typeBodyNode.setBegin(builder.createTextNode(document, body.getStart(), body.getStart() + 1));
		builder.push(typeBodyNode);
		body.childrenAccept(this);
		int end = body.getEnd();
		builder.checkedPop(typeBodyNode, end - 1);
		int endWithSemicolon = locateCharMatchInLine(end, SEMICOLON_AND_COLON, document, false);
		typeBodyNode.setEnd(builder.createTextNode(document, end - 1, endWithSemicolon));
	}

	/**
	 * A visit for a function invocation. This visit can be performed in numerous occasions, so we have it as a separate
	 * method that will be called in those occasions.
	 * 
	 * @param functionInvocation
	 */
	private void visitFunctionInvocation(FunctionInvocation functionInvocation)
	{
		FunctionName functionName = functionInvocation.getFunctionName();
		// push the function's name
		pushFunctionInvocationName(functionInvocation, functionName.getStart(), functionName.getEnd());
		// push the parenthesis and the parameters (if exist)
		List<Expression> invocationParameters = functionInvocation.parameters();
		pushParametersInParentheses(functionName.getEnd(), functionInvocation.getEnd(), invocationParameters);
	}

	/**
	 * Push the name part of a function invocation.
	 * 
	 * @param invocationNode
	 * @param nameStart
	 * @param nameEnd
	 */
	private void pushFunctionInvocationName(ASTNode invocationNode, int nameStart, int nameEnd)
	{
		FormatterPHPFunctionInvocationNode node = new FormatterPHPFunctionInvocationNode(document, invocationNode);
		node.setBegin(builder.createTextNode(document, nameStart, nameEnd));
		builder.push(node);
		builder.checkedPop(node, -1);
	}

	/**
	 * Push a FormatterPHPParenthesesNode that contains a parameters array. <br>
	 * Each parameter in the parameters list is expected to be separated from the others with a comma.
	 * 
	 * @param declarationEndOffset
	 * @param expressionEndOffset
	 * @param parameters
	 */
	private void pushParametersInParentheses(int declarationEndOffset, int expressionEndOffset,
			List<? extends ASTNode> parameters)
	{
		// in some cases, we get a ParethesisExpression inside a single parameter.
		// for those cases, we skip the parentheses node push and go straight to the
		// push of the ParethesisExpression, which should handle the rest.
		boolean pushParenthesisNode = parameters.size() != 1 || parameters.size() == 1
				&& parameters.get(0).getType() != ASTNode.PARENTHESIS_EXPRESSION;
		FormatterPHPParenthesesNode parenthesesNode = null;
		if (pushParenthesisNode)
		{
			int openParen = getNextNonWhiteCharOffset(document, declarationEndOffset);
			if (document.charAt(openParen) == '(')
			{
				parenthesesNode = new FormatterPHPParenthesesNode(document);
				parenthesesNode.setBegin(builder.createTextNode(document, openParen, openParen + 1));
			}
			else
			{
				parenthesesNode = new FormatterPHPParenthesesNode(document, true);
				parenthesesNode.setBegin(builder.createTextNode(document, openParen, openParen));
			}
			builder.push(parenthesesNode);
		}

		if (parameters != null && parameters.size() > 0)
		{
			visitNodeLists(parameters, null, null, TypePunctuation.COMMA);
		}
		if (pushParenthesisNode)
		{
			int closeParenStart = expressionEndOffset;
			int closeParenEnd = expressionEndOffset;
			if (!parenthesesNode.isAsWrapper())
			{
				closeParenStart = builder.locateCharBackward(document, ')', expressionEndOffset);
				closeParenEnd = closeParenStart + 1;
			}
			builder.checkedPop(parenthesesNode, -1);
			parenthesesNode.setEnd(builder.createTextNode(document, closeParenStart, closeParenEnd));
		}
	}

	/**
	 * Returns the next non-white char offset. In case all the chars after the given start offset are white-spaces,
	 * return the given start offset.
	 * 
	 * @param document
	 * @param startOffset
	 * @return The next non-white char; Or the given start-offset if all the chars right to the start offset were
	 *         whitespaces.
	 */
	private int getNextNonWhiteCharOffset(FormatterDocument document, int startOffset)
	{
		int length = document.getLength();
		for (int offset = startOffset; offset < length; offset++)
		{
			char charAt = document.charAt(offset);
			if (!Character.isWhitespace(charAt))
			{
				return offset;
			}
		}
		return startOffset;
	}

	/**
	 * Push a FormatterPHPParenthesesNode that contains an ASTNode (expression). <br>
	 * 
	 * @param openChar
	 *            The parentheses open char (e.g. '(', '[' etc.)
	 * @param closeChar
	 *            The parentheses close char (e.g. ')', ']' etc.)
	 * @param declarationEndOffset
	 * @param expressionEndOffset
	 * @param node
	 */
	private void pushNodeInParentheses(char openChar, char closeChar, int declarationEndOffset,
			int expressionEndOffset, ASTNode node)
	{
		int openParen = builder.locateCharForward(document, openChar, declarationEndOffset);
		int closeParen = builder.locateCharBackward(document, closeChar, expressionEndOffset);
		FormatterPHPParenthesesNode parenthesesNode = new FormatterPHPParenthesesNode(document);
		parenthesesNode.setBegin(builder.createTextNode(document, openParen, openParen + 1));
		builder.push(parenthesesNode);
		if (node != null)
		{
			node.accept(this);
		}
		builder.checkedPop(parenthesesNode, -1);
		parenthesesNode.setEnd(builder.createTextNode(document, closeParen, closeParen + 1));
	}

	/**
	 * Visits and push a modifiers section. This section can appear before a method or a variable in a class, before
	 * class definitions etc.
	 * 
	 * @param node
	 *            The node that holds the modifier
	 * @param nextNode
	 *            The next node that appears right after the modifiers.
	 */
	private void visitModifiers(ASTNode node, ASTNode nextNode)
	{
		// The gap between the start and the function holds the modifiers (if exist).
		// We create a node for each of these modifiers to remove any extra spaces they have between them.
		int startOffset = node.getStart();
		String modifiers = document.get(startOffset, nextNode.getStart());
		Matcher matcher = WORD_PATTERN.matcher(modifiers);
		boolean isFirst = true;
		while (matcher.find())
		{
			FormatterPHPKeywordNode modifierNode = new FormatterPHPKeywordNode(document, isFirst);
			modifierNode.setBegin(builder.createTextNode(document, matcher.start() + startOffset, matcher.end()
					+ startOffset));
			builder.push(modifierNode);
			builder.checkedPop(modifierNode, -1);
			isFirst = false;
		}
		if (isFirst)
		{
			// if we got to this point with the 'isFirst' as 'true', we know that the modifiers are empty.
			// in this case, we need to push an empty modifiers node.
			FormatterPHPKeywordNode emptyModifier = new FormatterPHPKeywordNode(document, isFirst);
			emptyModifier.setBegin(builder.createTextNode(document, startOffset, startOffset));
			builder.push(emptyModifier);
			builder.checkedPop(emptyModifier, -1);
		}
	}

	/**
	 * @param node
	 * @param declarationEndOffset
	 * @param body
	 */
	private void visitCommonLoopBlock(ASTNode node, int declarationEndOffset, Statement body, ASTNode condition)
	{
		visitCommonDeclaration(node, declarationEndOffset, true);
		// if we have conditions, visit them as well
		if (condition != null)
		{
			int conditionEnd = (body != null) ? body.getStart() : node.getEnd();
			pushNodeInParentheses('(', ')', declarationEndOffset, conditionEnd, condition);
		}
		// visit the body
		commonVisitBlockBody(node, body);
	}

	/**
	 * A common visit for a body ASTNode, which can be a Block or a different statement that will be wrapped in an
	 * implicit block node.
	 * 
	 * @param parent
	 * @param body
	 */
	private void commonVisitBlockBody(ASTNode parent, ASTNode body)
	{
		boolean hasBlockedBody = (body != null && body.getType() == ASTNode.BLOCK);
		boolean emptyBody = (body != null && body.getType() == ASTNode.EMPTY_STATEMENT);
		if (hasBlockedBody)
		{
			visitBlockNode((Block) body, parent, true);
		}
		else if (body != null)
		{
			if (!emptyBody)
			{
				wrapInImplicitBlock(body, true);
			}
			else
			{
				// create and push a special node that represents this empty statement.
				// When visiting a loop, this statement will probably only holds a semicolon char, so we make sure
				// we attach the char to the end of the previous node.
				body.accept(this);
			}
		}
	}

	/**
	 * A simple visit and push of a node that pushes a PHP text node which consumes any white-spaces before that node by
	 * request.
	 * 
	 * @param node
	 * @param consumePreviousWhitespaces
	 * @param spacesCountBefore
	 * @see #visitTextNode(int, int, boolean, int)
	 */
	private void visitTextNode(ASTNode node, boolean consumePreviousWhitespaces, int spacesCountBefore)
	{
		visitTextNode(node.getStart(), node.getEnd(), consumePreviousWhitespaces, spacesCountBefore);
	}

	/**
	 * A simple visit and push of a node that pushes a PHP text node which consumes any white-spaces before that node by
	 * request.
	 * 
	 * @param startOffset
	 * @param endOffset
	 * @param consumePreviousWhitespaces
	 * @param spacesCountBefore
	 * @see #visitTextNode(ASTNode, boolean, int)
	 */
	private void visitTextNode(int startOffset, int endOffset, boolean consumePreviousWhitespaces, int spacesCountBefore)
	{
		visitTextNode(startOffset, endOffset, consumePreviousWhitespaces, spacesCountBefore, 0);
	}

	/**
	 * A simple visit and push of a node that pushes a PHP text node which consumes any white-spaces before that node by
	 * request.
	 * 
	 * @param startOffset
	 * @param endOffset
	 * @param consumePreviousWhitespaces
	 * @param spacesCountBefore
	 * @param spacesCountAfter
	 */
	private void visitTextNode(int startOffset, int endOffset, boolean consumePreviousWhitespaces,
			int spacesCountBefore, int spacesCountAfter)
	{
		FormatterPHPTextNode textNode = new FormatterPHPTextNode(document, consumePreviousWhitespaces,
				spacesCountBefore, spacesCountAfter);
		textNode.setBegin(builder.createTextNode(document, startOffset, endOffset));
		builder.push(textNode);
		builder.checkedPop(textNode, endOffset);
	}

	/**
	 * Visit and push a FormatterPHPBlockNode. <br>
	 * The given body can represent a curly-braces body, or even an alternative syntax body. This method will check the
	 * block to see if it's curly, and if not, it will try to match the alternative syntax closer according to the given
	 * parent node type.
	 * 
	 * @param block
	 *            The block
	 * @param parent
	 *            The block's parent
	 * @see http://www.php.net/manual/en/control-structures.alternative-syntax.php
	 */
	private void visitBlockNode(Block block, ASTNode parent, boolean consumeEndingSemicolon)
	{
		boolean isAlternativeSyntaxBlock = !block.isCurly();
		FormatterPHPBlockNode blockNode = new FormatterPHPBlockNode(document, false);
		blockNode.setBegin(builder.createTextNode(document, block.getStart(), block.getStart() + 1));
		builder.push(blockNode);
		// visit the children
		block.childrenAccept(this);
		int end = block.getEnd();
		int closingStartOffset = end;
		if (!isAlternativeSyntaxBlock)
		{
			closingStartOffset--;
		}
		if (isAlternativeSyntaxBlock)
		{
			String alternativeSyntaxCloser = getAlternativeSyntaxCloser(parent);
			int alternativeCloserLength = alternativeSyntaxCloser.length();
			if (closingStartOffset - alternativeCloserLength >= 0
					&& document.get(closingStartOffset - alternativeCloserLength, closingStartOffset).toLowerCase()
							.equals(alternativeSyntaxCloser))
			{
				closingStartOffset -= alternativeCloserLength;
			}
		}

		// pop the block node
		builder.checkedPop(blockNode, Math.min(closingStartOffset, end));
		blockNode.setEnd(builder.createTextNode(document, closingStartOffset, Math.max(closingStartOffset, end)));
	}

	/**
	 * Visit and push a function declaration. The declaration can be a 'regular' function or can be a lambda function.
	 * 
	 * @param functionDeclaration
	 * @param functionName
	 * @param parameters
	 * @param body
	 */
	private void visitFunctionDeclaration(ASTNode functionDeclaration, Identifier functionName,
			List<FormalParameter> parameters, Block body)
	{
		// First, push the function declaration node
		int declarationEnd = functionDeclaration.getStart() + 8;
		visitCommonDeclaration(functionDeclaration, declarationEnd, true);
		// push the function name node, if exists
		if (functionName != null)
		{
			visitTextNode(functionName, true, 1);
			declarationEnd = functionName.getEnd();
		}
		// push the function parameters
		pushParametersInParentheses(declarationEnd, body.getStart(), parameters);
		// Then, push the body
		FormatterPHPFunctionBodyNode bodyNode = new FormatterPHPFunctionBodyNode(document);
		bodyNode.setBegin(builder.createTextNode(document, body.getStart(), body.getStart() + 1));
		builder.push(bodyNode);
		body.childrenAccept(this);
		int bodyEnd = body.getEnd();
		builder.checkedPop(bodyNode, bodyEnd - 1);
		bodyNode.setEnd(builder.createTextNode(document, bodyEnd - 1, bodyEnd));
	}

	/**
	 * Visit and push a common declaration part of an expression.s
	 * 
	 * @param node
	 * @param declarationEndOffset
	 * @param hasBlockedBody
	 */
	private void visitCommonDeclaration(ASTNode node, int declarationEndOffset, boolean hasBlockedBody)
	{
		FormatterPHPDeclarationNode declarationNode = new FormatterPHPDeclarationNode(document, hasBlockedBody, node);
		declarationNode.setBegin(builder.createTextNode(document, node.getStart(), declarationEndOffset));
		builder.push(declarationNode);
		builder.checkedPop(declarationNode, -1);
	}

	/**
	 * Visit an expression with left node, right node and an operator in between.<br>
	 * Note that the left <b>or</b> the right may be null.
	 * 
	 * @param left
	 * @param right
	 * @param operatorString
	 */
	private void visitLeftRightExpression(ASTNode parentNode, ASTNode left, ASTNode right, String operatorString)
	{
		int leftOffset;
		int rightOffset;
		if (left != null)
		{
			left.accept(this);
			leftOffset = left.getEnd();
		}
		else
		{
			leftOffset = parentNode.getStart();
		}
		if (right != null)
		{
			rightOffset = right.getStart();
		}
		else
		{
			rightOffset = parentNode.getEnd();
		}
		int operatorOffset = document.get(leftOffset, rightOffset).indexOf(operatorString) + leftOffset;
		TypeOperator typeOperator = TypeOperator.getTypeOperator(operatorString);
		pushTypeOperator(typeOperator, operatorOffset, false);
		if (right != null)
		{
			right.accept(this);
		}
	}

	/**
	 * Push a text node that will consume all the spaces before it.
	 * 
	 * @param offset
	 *            The offset to mark the start and end offset of the node. Any whitespace that appears before this node
	 *            should be consumed.
	 */
	private void pushSpaceConsumingNode(int offset)
	{
		FormatterPHPTextNode textNode = new FormatterPHPTextNode(document, true, 0, 0);
		textNode.setBegin(builder.createTextNode(document, offset, offset));
		builder.push(textNode);
		builder.checkedPop(textNode, offset);
	}

	private void pushTypeOperator(TypeOperator operator, int startOffset, boolean isUnary)
	{
		FormatterPHPOperatorNode node = new FormatterPHPOperatorNode(document, operator, isUnary);
		node.setBegin(builder.createTextNode(document, startOffset, startOffset + operator.toString().length()));
		builder.push(node);
		builder.checkedPop(node, -1);
	}

	private void pushTypePunctuation(TypePunctuation punctuation, int startOffset)
	{
		FormatterPHPPunctuationNode node = new FormatterPHPPunctuationNode(document, punctuation);
		node.setBegin(builder.createTextNode(document, startOffset, startOffset + punctuation.toString().length()));
		builder.push(node);
		builder.checkedPop(node, -1);
	}

	/**
	 * Returns the string value that represents the closing of an alternative syntax block. In case non exists, this
	 * method returns an empty string.
	 * 
	 * @param parent
	 * @return The alternative syntax block-closing string.
	 */
	private String getAlternativeSyntaxCloser(ASTNode parent)
	{
		switch (parent.getType())
		{
			case ASTNode.IF_STATEMENT:
				return "endif"; //$NON-NLS-1$
			case ASTNode.WHILE_STATEMENT:
				return "endwhile"; //$NON-NLS-1$
			case ASTNode.FOR_EACH_STATEMENT:
				return "endforeach"; //$NON-NLS-1$
			case ASTNode.FOR_STATEMENT:
				return "endfor"; //$NON-NLS-1$
			case ASTNode.SWITCH_STATEMENT:
				return "endswitch"; //$NON-NLS-1$
			default:
				return StringUtil.EMPTY;
		}
	}

	/**
	 * Push a keyword (e.g. 'const', 'echo', 'private' etc.)
	 * 
	 * @param start
	 * @param keywordLength
	 * @param isFirstInLine
	 */
	private void pushKeyword(int start, int keywordLength, boolean isFirstInLine)
	{
		FormatterPHPKeywordNode keywordNode = new FormatterPHPKeywordNode(document, isFirstInLine);
		keywordNode.setBegin(builder.createTextNode(document, start, start + keywordLength));
		builder.push(keywordNode);
		builder.checkedPop(keywordNode, -1);
	}

	/**
	 * Locate and push a punctuation char node.
	 * 
	 * @param offsetToSearch
	 *            - The offset that will be used as the start for the search of the semicolon.
	 * @param ignoreNonWhitespace
	 *            indicate that a non-whitespace chars that appear before the semicolon will be ignored. If this flag is
	 *            false, and a non-whitespace appear between the given offset and the semicolon, the method will
	 *            <b>not</b> push a semicolon node.
	 * @param isLineTerminating
	 *            Indicates that this semicolon is a line terminating one.
	 */
	private void findAndPushPunctuationNode(TypePunctuation type, int offsetToSearch, boolean ignoreNonWhitespace,
			boolean isLineTerminating)
	{
		char punctuationType = type.toString().charAt(0);
		int punctuationOffset = builder.locateCharForward(document, punctuationType, offsetToSearch);
		if (punctuationOffset != offsetToSearch || document.charAt(punctuationOffset) == punctuationType)
		{
			String segment = document.get(offsetToSearch, punctuationOffset);
			if (!ignoreNonWhitespace && segment.trim().length() > 0)
			{
				return;
			}
			if (isLineTerminating)
			{
				// We need to make sure that the termination only happens when the line does not
				// have a terminator already.
				int lineEnd = locateWhitespaceLineEndingOffset(punctuationOffset + 1);
				isLineTerminating = lineEnd < 0;
			}
			FormatterPHPPunctuationNode punctuationNode = new FormatterPHPPunctuationNode(document, type,
					isLineTerminating);
			punctuationNode.setBegin(builder.createTextNode(document, punctuationOffset, punctuationOffset + 1));
			builder.push(punctuationNode);
			builder.checkedPop(punctuationNode, -1);
		}
	}

	/**
	 * Wrap a given node in an implicit block node and visit the node to insert it as a child of that block.
	 * 
	 * @param node
	 *            The node to wrap and visit.
	 * @param indent
	 */
	private void wrapInImplicitBlock(ASTNode node, boolean indent)
	{
		FormatterPHPImplicitBlockNode emptyBlock = new FormatterPHPImplicitBlockNode(document, false, indent, 0);
		int start = node.getStart();
		int end = node.getEnd();
		emptyBlock.setBegin(builder.createTextNode(document, start, start));
		builder.push(emptyBlock);
		node.accept(this);
		builder.checkedPop(emptyBlock, -1);
		emptyBlock.setEnd(builder.createTextNode(document, end, end));
	}

	/**
	 * Locate a line ending offset. The line should only contain whitespace characters.
	 * 
	 * @return The line ending offset, or -1 in case not found.
	 */
	private int locateWhitespaceLineEndingOffset(int start)
	{
		int length = document.getLength();
		for (int offset = start; offset < length; offset++)
		{
			char c = document.charAt(offset);
			if (c == '\n' || c == '\r')
			{
				return offset;
			}
			if (!Character.isWhitespace(c))
			{
				return -1;
			}
		}
		return -1;
	}

	/**
	 * Scan for a list of char terminator located at the <b>same line</b>. Return the given offset if non is found.<br>
	 * <b>See important note in the @return tag.</b>
	 * 
	 * @param offset
	 * @param chars
	 *            An array of chars to match
	 * @param document
	 * @param ignoreNonWhitespace
	 *            In case this flag is false, any non-whitespace char that appear before we located a requested char
	 *            will stop the search. In case it's true, the search will continue till the end of the line.
	 * @return The first match offset; The given offset if a match not found. <b>Note that the returned offset is in a
	 *         +1 position from the real character offset. This is to ease the caller process of adapting it to the
	 *         formatter-document's offsets.</b>
	 */
	private int locateCharMatchInLine(int offset, char[] chars, FormatterDocument document, boolean ignoreNonWhitespace)
	{
		int i = offset;
		int size = document.getLength();
		for (; i < size; i++)
		{
			char c = document.charAt(i);
			for (char toMatch : chars)
			{
				if (c == toMatch)
				{
					return i + 1;
				}
			}
			if (c == '\n' || c == '\r')
			{
				break;
			}
			if (!ignoreNonWhitespace && (c != ' ' || c != '\t'))
			{
				break;
			}
		}
		return offset;
	}
}