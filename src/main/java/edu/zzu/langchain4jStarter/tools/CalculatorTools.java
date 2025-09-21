package edu.zzu.langchain4jStarter.tools;

import dev.langchain4j.agent.tool.Tool;
import org.springframework.stereotype.Component;

@Component
public class CalculatorTools {

    @Tool("计算两个数字的和")
    public double sum(double a, double b) {
        System.out.println("调用加法运算: " + a + " + " + b);
        return a + b;
    }

    @Tool("计算两个数字的差")
    public double subtract(double a, double b) {
        System.out.println("调用减法运算: " + a + " - " + b);
        return a - b;
    }

    @Tool("计算两个数字的乘积")
    public double multiply(double a, double b) {
        System.out.println("调用乘法运算: " + a + " * " + b);
        return a * b;
    }

    @Tool("计算两个数字的商。如果除数为零，则返回无穷大或NaN。")
    public double divide(double a, double b) {
        System.out.println("调用除法运算: " + a + " / " + b);
        if (b == 0) {
            System.out.println("错误：除数不能为零。");
            // 根据需要可以抛出异常或返回特定值，这里遵循Math.sqrt的行为返回Double.NaN或Infinity
            return Double.NaN; 
        }
        return a / b;
    }

    @Tool("计算一个数字的平方根")
    public double squareRoot(double x) {
        System.out.println("调用平方根运算: sqrt(" + x + ")");
        if (x < 0) {
            System.out.println("错误：不能计算负数的平方根。");
            return Double.NaN;
        }
        return Math.sqrt(x);
    }

    @Tool("计算一个数字的平方")
    public double square(double x) {
        System.out.println("调用平方运算: " + x + "^2");
        return x * x;
    }

    @Tool("计算一个数的N次方 (x的power次方)")
    public double power(double base, double exponent) {
        System.out.println("调用幂运算: " + base + "^" + exponent);
        return Math.pow(base, exponent);
    }
}