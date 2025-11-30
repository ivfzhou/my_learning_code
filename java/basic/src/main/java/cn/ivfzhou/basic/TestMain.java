package cn.ivfzhou.basic;

import java.util.ArrayList;
import java.util.Date;
import java.util.function.BiConsumer;

public final class TestMain {

    static void main(String[] args) {
        Date obj = null;
        Object obj1 = obj;
        System.out.println(obj1 instanceof Date);
        System.out.println(obj.getTime());
    }

    // Java10 的类型自动推断。
    static void typeReference() {
        var list = new ArrayList<String>();
        System.out.println(list.size());
    }

    // Java13 的文本块。
    static void textBlock() {
        var str = """
                你好，
                hello world
                """;
        System.out.println(str);
    }

    // Java17 的 switch 表达式。
    static void switchExpression() {
        var num = 1;
        var data = switch (num) {
            case 1 -> "1";
            case 2 -> "2";
            default -> "";
        };
        System.out.println(data);

        data = switch (num) {
            case 1 -> {
                yield "1";
            }
            case 2 -> {
                yield "2";
            }
            default -> "";
        };
        System.out.println(data);

        Object obj = "Hello, World!";
        String result = switch (obj) {
            case String str -> "It's a string of length: " + str.length();
            case Integer i -> "It's an integer: " + i;
            default -> "Unknown type";
        };

        System.out.println(result);
    }

    // Java14 的模式匹配。
    static void patternMatching() {
        Object obj = 1;
        if (obj instanceof Integer num) {
            System.out.println(num + 1);
        }
    }

    static void recordClass() {
        var p = new Point(1, 2);
        System.out.println(p.x);
        System.out.println(p.y);
        System.out.println(p);

        if (p instanceof Point(int x, int y)) {
            System.out.println(x);
            System.out.println(y);
        }
    }

    // Java21 的数据类解构。
    static void deconstruct() {
        var p = new Point(1, 2);
        if (p instanceof Point(int x, int y)) {
            System.out.println(x);
            System.out.println(y);
        }
    }

    // Java11 的拉姆达参数可使用 var。
    static void lambda() {
        BiConsumer<String, String> consumer = (var a, var b) -> System.out.println(a);
        consumer.accept("a", "b");
    }

    /*
     * Java18 的注释中的代码片段。
     * @code{
     * System.out.println("Hello, World!");
     * }
     * */
    static void snippetCode() {}

    // Java16 的数据类。
    static public record Point(int x, int y) {}

    // Java17 的密封类。
    static public sealed class Shape permits Circle, Square {}

    static public final class Circle extends Shape {}

    static public final class Square extends Shape {}

}
