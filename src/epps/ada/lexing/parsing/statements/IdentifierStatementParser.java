/**
 * Course: Concepts of Programming Languages
 * Section: W01
 * Professor: Jose Garrido
 * Date: November 3rd, 2020
 * Author: Michael Epps
 * Assignment: CPL Project, Deliverable 
 */
package lexing.parsing.statements;

import java.util.ArrayList;
import java.util.List;

import grammar.TokenPattern;
import lexing.ast.Expression;
import lexing.ast.IdentifierExpression;
import lexing.ast.IdentifierStatement;
import lexing.errors.ExpectedSymbolException;
import lexing.errors.InvalidExpressionException;
import lexing.errors.ParsingException;
import lexing.parsing.Parser;
import lexing.parsing.TokenStream;

/**
 * An IdentifierStatementParser will parse out one of many possible statements that begin with an identifier list
 * such as a DeclarationStatement or an AssignmentStatement.
 */
public class IdentifierStatementParser implements StatementParser<IdentifierStatement> {

    @Override
    public IdentifierStatement parse(Parser parser, TokenStream tokenStream) throws ParsingException {

        if(tokenStream.isNext(TokenPattern.SYMBOL_PAREN_LEFT)) {
            return new CallStatementParser().parse(parser, tokenStream);
        }
 
        List<IdentifierExpression> identifiers = this.parseIdentifierList(parser, tokenStream);
        if (tokenStream.isCurrent(TokenPattern.SYMBOL_COLON)) {
            tokenStream.advance();
            return new DeclarationStatementParser(identifiers).parse(parser, tokenStream);
        } else if (tokenStream.isCurrent(TokenPattern.SYMBOL_ASSIGNMENT)) {
            tokenStream.advance();
            return new AssignmentStatementParser(identifiers).parse(parser, tokenStream);
        }

        throw new ExpectedSymbolException(TokenPattern.SYMBOL_COLON, tokenStream.currentLiteral());
    }

    /**
     * Parses out the expression list and then converts the expressions to identifiers
     * @return The list of identifier expressions
     * @throws ParsingException Thrown if any parsed expression is not an identifier
     */
    private List<IdentifierExpression> parseIdentifierList(Parser parser, TokenStream tokenStream)
            throws ParsingException {
        List<Expression> expressions = parser.parseExpressionList();

        // Convert expressions to identifiers
        List<IdentifierExpression> identifiers = new ArrayList<>();
        for (Expression expression : expressions) {
            if (expression instanceof IdentifierExpression) {
                identifiers.add((IdentifierExpression) expression);
            } else {
                throw new InvalidExpressionException(expression);
            }
        }
        return identifiers;
    }
}
