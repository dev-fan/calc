package ua.dp.altermann.calc.expression;

public class DivExpr extends BaseExpr {

    @Override
    public Double calc(Double arg1, Double arg2) {
        return arg1 / arg2;
    }

}
