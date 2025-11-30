package cn.ivfzhou.basic;

import java.util.Arrays;
import java.util.Comparator;

public final class TestLambda {

    public static void main(String[] args) {
        var list = Arrays.asList("1", "2");

        list.sort(new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                return o1.compareTo(o2);
            }
        });

        list.sort((String o1, String o2) -> {
            return o1.compareTo(o2);
        });

        list.sort((o1, o2) -> {
            return o1.compareTo(o2);
        });

        list.sort((o1, o2) -> o1.compareTo(o2));
    }

}
