/************************************************************************************
 *
 * CSC220 Programming Project#5
 *
 * Due Date: 23:55pm, Saturday, 11/18/2017 
 *           Upload LispExprEvaluator.java to ilearn 
 *
 * Specification: 
 *
 * Taken from Project 7, Chapter 5, Page 178
 * I have modified specification and requirements of this project
 *
 * Ref: http://www.gigamonkeys.com/book/        (see chap. 10)
 *
 * In the language Lisp, each of the four basic arithmetic operators appears 
 * before an arbitrary number of operands, which are separated by spaces. 
 * The resulting expression is enclosed in parentheses. The operators behave 
 * as follows:
 *
 * (+ a b c ...) returns the sum of all the operands, and (+) returns 0.
 *
 * (- a b c ...) returns a - b - c - ..., and (- a) returns -a. 
 *
 * (* a b c ...) returns the product of all the operands, and (*) returns 1.
 *
 * (/ a b c ...) returns a / b / c / ..., and (/ a) returns 1/a. 
 *
 * Note: + * may have zero operand
 *       - / must have at least one operand
 *
 * You can form larger arithmetic expressions by combining these basic 
 * expressions using a fully parenthesized prefix notation. 
 * For example, the following is a valid Lisp expression:
 *
 * 	(+ (- 6) (* 2 3 4) (/ (+ 3) (* 1) (- 2 3 1)) (+))
 *
 * This expression is evaluated successively as follows:
 *
 *	(+ (- 6) (* 2 3 4) (/ 3 1 -2) (+))
 *	(+ -6 24 -1.5 0.0)
 *	16.5
 *
 * Requirements:
 *
 * - Design and implement an algorithm that uses Java API stacks to evaluate a 
 *   Valid Lisp expression composed of the four basic operators and integer values. 
 * - Valid tokens in an expression are '(',')','+','-','*','/',and positive integers (>=0)
 * - Display result as floting point number with at 2 decimal places
 * - Negative number is not a valid "input" operand, e.g. (+ -2 3) 
 *   However, you may create a negative number using parentheses, e.g. (+ (-2)3)
 * - There may be any number of blank spaces, >= 0, in between tokens
 *   Thus, the following expressions are valid:
 *   	(+   (-6)3)
 *   	(/(+20 30))
 *
 * - Must use Java API Stack class in this project.
 *   Ref: http://docs.oracle.com/javase/7/docs/api/java/util/Stack.html
 * - Must throw LispExprEvaluatorException to indicate errors
 * - Must not add new or modify existing data fields
 * - Must implement these methods : 
 *
 *   	public LispExprEvaluator()
 *   	public LispExprEvaluator(String currentExpression) 
 *      public void reset(String currentExpression) 
 *      public double evaluate()
 *      private void evaluateCurrentOperation()
 *
 * - You may add new private methods
 *
 *************************************************************************************/

package PJ2;
import java.util.*;

public class LispExprEvaluator
{
    // Current input Lisp expression
    private String currentExpr;

    // Main expression stack & current operation stack, see algorithm in evaluate()
    private Stack<Object> exprStack;
    private Stack<Double> computeStack;

    // default constructor
    // set currentExpr to "" 
    // create stack objects
    public LispExprEvaluator()
    {
	// add statements
        currentExpr = "";
        exprStack = new Stack<>();
        computeStack = new Stack<>();
    }

    // constructor with an input expression 
    // set currentExpr to currentExpression 
    // create stack objects
    public LispExprEvaluator(String currentExpression)
    {
	// add statements
        if (currentExpression == null){ throw new LispExprEvaluatorException();}

        currentExpr = currentExpression;
        exprStack = new Stack<>();
        computeStack = new Stack<>();
    }

    // set currentExpr to currentExpression 
    // clear stack objects
    public void reset(String currentExpression)
    {
	// add statements
        if (currentExpression == null){throw new LispExprEvaluatorException();}

        currentExpr = currentExpression;
        exprStack.clear();
        computeStack.clear();
    }


    // This function evaluates current operator with its operands
    // See complete algorithm in evaluate()
    //
    // Main Steps:
    // 		Pop operands from exprStack and push them onto 
    // 			computeStack until you find an operator
    //  	Apply the operator to the operands on computeStack
    //          Push the result into exprStack
    //
    private void evaluateCurrentOperation()
    {
	// add statements
        //make sure exprStack isn't empty
        if (exprStack.empty()) {throw new LispExprEvaluatorException("Nothing found in Stack.");}

        //until there is an operator, add the numbers to computeStack
        while (exprStack.peek() != "+" && exprStack.peek() != "-" &&
                exprStack.peek() != "/" && exprStack.peek() != "*")
        {
            //make sure there's a number
            if (exprStack.peek() == null) {throw new LispExprEvaluatorException("Operator not found.");}

            //push the number into computeStack and check if exprStack is empty
            computeStack.push(Double.parseDouble((String)exprStack.pop()));
            if (exprStack.empty()) {throw new LispExprEvaluatorException("Empty Stack.");}
        }

        //store the operator
        String operator = (String)exprStack.pop();
        double result = 0;

        //based on the operator, add, subtract, divide, or multiply the numbers
        if (operator.equals("+"))
        {
            while (!computeStack.empty())
            {
                result += computeStack.pop();
            }
        }
        else if (operator.equals("-"))
        {
            if (computeStack.size() > 1)
            {
                result = computeStack.pop();
            }
            while (!computeStack.empty())
            {
                result -= computeStack.pop();
            }
        }
        else if (operator.equals("*"))
        {
            result = 1;
            while (!computeStack.empty())
            {
                result *= computeStack.pop();
            }
        }
        else if (operator.equals("/"))
        {
            if (computeStack.empty()){ throw new LispExprEvaluatorException("Empty Stack");}
            result = computeStack.pop();
            if (computeStack.size() == 0 && result != 0) {result = 1.0/result;}
            while (!computeStack.empty())
            {
                result /= computeStack.pop();
            }
        }
        //push into exprStack
        exprStack.push(Double.toString(result));
    }

    /**
     * This function evaluates current Lisp expression in currentExpr
     * It return result of the expression
     *
     * The algorithm:
     *
     * Step 1   Scan the tokens in the string.
     * Step 2		If you see an operand, push operand object onto the exprStack
     * Step 3  	    	If you see "(", next token should be an operator
     * Step 4  		If you see an operator, push operator object onto the exprStack
     * Step 5		If you see ")"  // steps in evaluateCurrentOperation() :
     * Step 6			Pop operands and push them onto computeStack
     * 					until you find an operator
     * Step 7			Apply the operator to the operands on computeStack
     * Step 8			Push the result into exprStack
     * Step 9    If you run out of tokens, the value on the top of exprStack is
     *           is the result of the expression.
     */
    public double evaluate()
    {
        // only outline is given...
        // you need to add statements/local variables
        // you may delete or modify any statements in this method


        // use scanner to tokenize currentExpr
        Scanner currentExprScanner = new Scanner(currentExpr);

        // Use zero or more white space as delimiter,
        // which breaks the string into single character tokens
        currentExprScanner = currentExprScanner.useDelimiter("\\s*");

        // Step 1: Scan the tokens in the string.
        while (currentExprScanner.hasNext())
        {

     	    // Step 2: If you see an operand, push operand object onto the exprStack
            if (currentExprScanner.hasNextInt())
            {
                // This force scanner to grab all of the digits
                // Otherwise, it will just get one char
                String dataString = currentExprScanner.findInLine("\\d+");
                exprStack.push(dataString);

   		// more ...
            }
            else
            {
                // Get next token, only one char in string token
                String aToken = currentExprScanner.next();
                char item = aToken.charAt(0);

                switch (item)
                {
     		    // Step 3: If you see "(", next token should be an operator
                    case '(':
                        break;
     		    // Step 4: If you see an operator, push operator object onto the exprStack
                    case '+':
                        exprStack.push("+");
                        break;
                    case '-':
                        exprStack.push("-");
                        break;
                    case '*':
                        exprStack.push("*");
                        break;
                    case '/':
                        exprStack.push("/");
                        break;
     		    // Step 5: If you see ")"  // steps in evaluateCurrentOperation() :
                    case ')':
                        evaluateCurrentOperation();
                        break;
                    default:  // error
                        throw new LispExprEvaluatorException(item + " is not a legal expression operator");
                } // end switch
            } // end else
        } // end while

        // Step 9: If you run out of tokens, the value on the top of exprStack is
        //         is the result of the expression.
        //
        //         return result
	return Double.parseDouble((String)exprStack.peek());  // change this statement

    }

    //=============================================================
    // DO NOT MODIFY ANY STATEMENTS BELOW
    //=============================================================

    // This static method is used by main() only
    private static void evaluateExprTest(String s, LispExprEvaluator expr, String expect)
    {
        Double result;
        System.out.println("Expression " + s);
        System.out.printf("Expected result : %s\n", expect);
	expr.reset(s);
	try {
          result = expr.evaluate();
          System.out.printf("Evaluated result : %.2f\n", result);
	}
	catch (LispExprEvaluatorException e) {
          System.out.println("Evaluated result : "+e);
	}
        System.out.println("-----------------------------");
    }

    // define few test cases, exception may happen
    public static void main (String args[])
    {
        LispExprEvaluator expr= new LispExprEvaluator();
        String test1 = "(+ (- 6) (* 2 3 4) (/ (+ 3) (* 1) (- 2 3 1)) (+))";
        String test2 = "(+ (- 632) (* 21 3 4) (/ (+ 32) (* 1) (- 21 3 1)) (+))";
        String test3 = "(+ (/ 2) (* 2) (/ (+ 1) (+ 1) (- 2 1 ))(*))";
        String test4 = "(+ (/2)(+))";
        String test5 = "(+ (/2 3 0))";
        String test6 = "(+ (/ 2) (* 2) (/ (+ 1) (+ 3) (- 2 1 ))))";
        String test7 = "(+ (/) )";
        String test8 = "(+ (- 6) (* 2 3 4) (/ (+ 3) (* 1) (-)) (+1))";
	evaluateExprTest(test1, expr, "16.50");
	evaluateExprTest(test2, expr, "-378.12");
	evaluateExprTest(test3, expr, "4.50");
	evaluateExprTest(test4, expr, "0.50");
	evaluateExprTest(test5, expr, "Infinity or LispExprEvaluatorException");
	evaluateExprTest(test6, expr, "LispExprEvaluatorException");
	evaluateExprTest(test7, expr, "LispExprEvaluatorException");
	evaluateExprTest(test8, expr, "LispExprEvaluatorException");
    }
}
