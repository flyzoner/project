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
import lexing.ast.DeclarationStatement;
import lexing.ast.Expression;
import lexing.ast.IdentifierExpression;
import lexing.ast.Type;
import lexing.errors.MismatchedDeclarationsException;
import lexing.errors.ParsingException;
import lexing.parsing.Parser;
import lexing.parsing.TokenStream;

/**
 * A DeclarationStatementParser parses out a declaration statement such as:
 * <identifier list> : <type> or <identifer list> : <type> := <expression list>;
 */
public class DeclarationStatementParser implements StatementParser<DeclarationStatement> {

    private List<IdentifierExpression> identifiers;

    protected DeclarationStatementParser(List<IdentifierExpression> identifiers) {
        this.identifiers = identifiers;
    }

    @Override
    public DeclarationStatement parse(Parser parser, TokenStream tokenStream) throws ParsingException {
        // We are assuming that we are starting at the type since we have the identifier list already
        tokenStream.currentMustNotBe(TokenPattern.SYMBOL_ASSIGNMENT);
        tokenStream.currentMustNotBe(TokenPattern.SYMBOL_SEMICOLON);

        Type type = new Type(tokenStream.currentLiteral());
        tokenStream.advance();

        // Declare all the identifiers as this type
        for (IdentifierExpression identifier : identifiers) {
            parser.getIdentifierTable().declareIdentifier(identifier, type);
        }

        // If this declaration includes default values, parse them out here
        if (tokenStream.isCurrent(TokenPattern.SYMBOL_ASSIGNMENT)) {
            tokenStream.advance();
            List<Expression> defaultValues = parser.parseExpressionList();

            // If we dont have the same number of identifiers and default values, error out
            if (identifiers.size() != defaultValues.size()) {
                throw new MismatchedDeclarationsException(identifiers.size(), defaultValues.size());
            }
            return new DeclarationStatement(identifiers, type, defaultValues);
        }

        // Special case for formal parameters declaration
        // If we detect we are the last declaration in a procedure/function's formal
        // parameters
        // then skip the semi-colon check
        if (tokenStream.isCurrent(TokenPattern.SYMBOL_PAREN_RIGHT) && tokenStream.isNext(TokenPattern.KEYWORD_IS)) {
            return new DeclarationStatement(identifiers, type, new ArrayList<>());
        }

        tokenStream.currentMustBe(TokenPattern.SYMBOL_SEMICOLON);
        return new DeclarationStatement(identifiers, type, new ArrayList<>());
    }

}
