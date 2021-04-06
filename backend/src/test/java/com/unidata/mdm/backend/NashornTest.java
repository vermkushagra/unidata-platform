package com.unidata.mdm.backend;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.util.stream.DoubleStream;

import org.junit.Ignore;
import org.junit.Test;

@Ignore
public class NashornTest {

    private static final String FUNCTION = "(function (value) {return %s;} )(%s)";

    private static final ScriptEngine NASHORN = new ScriptEngineManager().getEngineByName("nashorn");

    @Test
    public void divide() throws ScriptException {
        Object stringResult = NASHORN.eval("(function (value) {return value/1000;} )(1500)");
        Double result = Double.valueOf(stringResult.toString());
        assert result.equals(1.5d);
    }

    @Test
    public void multiply() throws ScriptException {
        Object stringResult = NASHORN.eval("(function (value) {return value*1000;} )(0.15)");
        Double result = Double.valueOf(stringResult.toString());
        assert result.equals(150d);
    }

    @Test
    public void add() throws ScriptException {
        Object stringResult = NASHORN.eval("(function (value) {return value+0.5;} )(0.15)");
        Double result = Double.valueOf(stringResult.toString());
        assert result.equals(0.65d);
    }

    @Test
    public void concurrentJs() throws ScriptException {
        for (int limit = 10; limit < 100000000; limit = limit * 10) {
            long start = System.currentTimeMillis();
            String convectionFunction = "var calc = function (value) {return value*3;} ";
            NASHORN.eval(convectionFunction);
            Invocable invocable = (Invocable) NASHORN;
            Double reduce = DoubleStream.iterate(0, n -> n + 2)
                    .limit(limit)
                    .boxed()
                    .parallel()
                    .map(value -> {
                        try {
                            return invocable.invokeFunction("calc", value);
                        } catch (ScriptException | NoSuchMethodException e) {
                            e.printStackTrace();
                            return "0";
                        }
                    })
                    .map(result -> Double.valueOf(result.toString()))
                    .reduce(0d, (a, b) -> a + b);
            Double result = (((limit - 1) * 3d)) * limit;
            assert reduce.equals(result);
            long finish = System.currentTimeMillis();
            System.out.println(finish - start);
        }
    }

    @Test
    public void concurrentJava() throws ScriptException {
        for (int limit = 10; limit < 100000000; limit = limit * 10) {
            long start = System.currentTimeMillis();
            Double reduce = DoubleStream.iterate(0, n -> n + 2)
                    .limit(limit)
                    .boxed()
                    .parallel()
                    .map(value -> value * 3)
                    .map(result -> Double.valueOf(result.toString()))
                    .reduce(0d, (a, b) -> a + b);
            Double result = (((limit - 1) * 3d)) * limit;
            assert reduce.equals(result);
            long finish = System.currentTimeMillis();
            System.out.println(finish - start);
        }
    }

    @Test
    public void measure() throws ScriptException, NoSuchMethodException {
        long start = System.currentTimeMillis();
        for (int i = 0; i < 10; i++) {
            add();
        }
        long finish = System.currentTimeMillis();
        System.out.println(finish - start);

        start = System.currentTimeMillis();
        String convectionFunction = "var calc = function (value) {return value+0.5;} ";
        NASHORN.eval(convectionFunction);
        Invocable invocable = (Invocable) NASHORN;
        for (int i = 0; i < 10; i++) {
            invocable.invokeFunction("calc", 0.15d);
        }
        finish = System.currentTimeMillis();
        System.out.println(finish - start);
    }

    @Test
    public void checkChange() throws ScriptException, NoSuchMethodException {
        Invocable invocable = (Invocable) NASHORN;
        String convectionFunction1 = "var value3unit5 = function (value) {return value+0.5;} ";
        NASHORN.eval(convectionFunction1);
        int input = 1;
        assert invocable.invokeFunction("value3unit5", input).equals(1.5d);
        String convectionFunction2 = "var value3unit5 = function (value) {return value+2.5;} ";
        NASHORN.eval(convectionFunction2);
        assert invocable.invokeFunction("value3unit5", input).equals(3.5d);
    }

    @Test
    public void checkFewFunctions() throws ScriptException, NoSuchMethodException {
        Invocable invocable = (Invocable) NASHORN;
        String convectionFunction1 = "var value3unit5 = function (value) {return value+0.5;}; ";
        NASHORN.eval(convectionFunction1);
        String convectionFunction2 = "var value3unit6 = function (value) {return value+2.5;}; ";
        NASHORN.eval(convectionFunction2);
        int input = 1;
        assert invocable.invokeFunction("value3unit5", input).equals(1.5d);
        assert invocable.invokeFunction("value3unit6", input).equals(3.5d);
    }

    @Test(expected = Exception.class)
    public void checkRemoving() throws ScriptException, NoSuchMethodException {
        Invocable invocable = (Invocable) NASHORN;
        String convectionFunction1 = "var value3unit5 = function (value) {return value+0.5;}; ";
        NASHORN.eval(convectionFunction1);
        NASHORN.eval("var value3unit5 = undefined");
        int input = 1;
        invocable.invokeFunction("value3unit5", input);
    }

}
