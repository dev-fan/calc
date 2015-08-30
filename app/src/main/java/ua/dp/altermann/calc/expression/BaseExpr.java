package ua.dp.altermann.calc.expression;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Базовый класс разбора матвыражения
 */
public abstract class BaseExpr {

    protected static String[] orderArithmetic = new String[]{"\\*|\\/", "\\+|\\-"};

    protected static String patternBrackets = "\\(([^\\(\\)]*?)\\)";

    protected static String patternArithmetic = "(\\-?\\d+(\\.\\d+)?)"
            + "\\s*?" + "([%s])" + "\\s*?"
            + "(\\-?\\d+(\\.\\d+)?)";

    private static final Map<String,BaseExpr> usedAction = new HashMap<>();

    protected static String getPatternArithmetic(String expr) {
        return String.format(patternArithmetic, expr);
    }

    public static BaseExpr getAction(String expr) {
        if (!usedAction.containsKey(expr)) {
            switch (expr) {
                case "*":
                    usedAction.put(expr, new MultiExpr());
                    break;
                case "/":
                    usedAction.put(expr, new DivExpr());
                    break;
                case "+":
                    usedAction.put(expr, new AddExpr());
                    break;
                case "-":
                    usedAction.put(expr, new SubExpr());
                    break;
                default:
                    throw new RuntimeException("Unknown action: " + expr);
            }
        }
        return usedAction.get(expr);
    }

    public static String calc(String expr) {
        // Fix
        expr = Pattern.compile("(\\d+)(\\()").matcher(expr).replaceAll("$1*$2");
        expr = Pattern.compile("(\\))(\\d+)").matcher(expr).replaceAll("$1*$2");
        // Brackets
        if (Pattern.matches(".*?" + patternBrackets + ".*?", expr)) {
            Pattern ptnBracket = Pattern.compile(patternBrackets, Pattern.CASE_INSENSITIVE);
            StringBuffer bfBracket = new StringBuffer();
            Matcher mtrBracket = ptnBracket.matcher(expr);
            while (mtrBracket.find()) {
                mtrBracket.appendReplacement(bfBracket, calc(mtrBracket.group(1)));
            }
            mtrBracket.appendTail(bfBracket);
            expr = calc(bfBracket.toString());
        }
        // Arithmetic operation
        for (String act : orderArithmetic) {
            String pattrExpr = getPatternArithmetic(act);
            Pattern ptnExpr = Pattern.compile(pattrExpr, Pattern.CASE_INSENSITIVE);
            Matcher mtrExpr = ptnExpr.matcher(expr);
            if (mtrExpr.find()) {
                BaseExpr action = getAction(mtrExpr.group(3));
                Double result = action.calc(Double.valueOf(mtrExpr.group(1)), Double.valueOf(mtrExpr.group(4)));
                String tmp = mtrExpr.replaceFirst(String.valueOf(result));
                expr = calc(tmp);
            }
        }

        // Fix view
        if (Math.round(Double.valueOf(expr)) == Double.valueOf(expr)) {
            expr = String.valueOf(Math.round(Double.valueOf(expr)));
        } else if (expr.length() > 8) {
            expr = String.valueOf(Math.round(Math.pow(10, 8) * Double.valueOf(expr)) / Math.pow(10, 8));
        }
        return expr;
    }

    public abstract Double calc(Double arg1, Double arg2);

}
