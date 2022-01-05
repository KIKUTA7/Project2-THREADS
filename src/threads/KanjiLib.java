package threads;

import java.util.*;
import java.util.stream.Collector;

public class KanjiLib {

    private final static Map<Integer, Character> KANJI_MAP = new HashMap<>();

    static {
        KanjiLib.KANJI_MAP.put(0, '零');
        KanjiLib.KANJI_MAP.put(1, '一');
        KanjiLib.KANJI_MAP.put(2, '二');
        KanjiLib.KANJI_MAP.put(3, '三');
        KanjiLib.KANJI_MAP.put(4, '四');
        KanjiLib.KANJI_MAP.put(5, '五');
        KanjiLib.KANJI_MAP.put(6, '六');
        KanjiLib.KANJI_MAP.put(7, '七');
        KanjiLib.KANJI_MAP.put(8, '八');
        KanjiLib.KANJI_MAP.put(9, '九');
        KanjiLib.KANJI_MAP.put(10, '十');
        KanjiLib.KANJI_MAP.put(100, '百');
        KanjiLib.KANJI_MAP.put(1_000, '千');
        KanjiLib.KANJI_MAP.put(10_000, '万');
    }

    public static String convert(int n) {
        if (n < 0 || n > 99_999_999) {
            throw new IllegalArgumentException("'n' must be in range [0, 99.999.999].");
        }

        if (n == 0) return KANJI_MAP.get(0).toString();

        Deque<Character> parts = new ArrayDeque<>((int) (Math.log10(n) + 1) * 2);
        String higherOrderParts = "";
        for (int i = 1; n > 0; n /= 10, i *= 10) {
            int part = n % 10;
            if (i >= 10_000) {
                higherOrderParts = convert(n) + KANJI_MAP.get(10_000);
                break;
            }
            if (part == 0) continue;
            if (i > 1) {
                parts.addFirst(KANJI_MAP.get(i));
                if (part != 1) {
                    parts.addFirst(KANJI_MAP.get(part));
                }
            } else {
                parts.addFirst(KANJI_MAP.get(part));
            }
        }
        return higherOrderParts + parts.stream()
                .collect(Collector.of(
                        StringBuilder::new,
                        StringBuilder::append,
                        StringBuilder::append,
                        StringBuilder::toString));
    }

    public static void assertEquals(Object result, Object expected) {
        if (!Objects.equals(result, expected)) {
            throw new AssertionError(String.format("| Expected: %s | Result: %s |", expected, result));
        }
    }

    public static void assertTrue(boolean value) {
        if (!value) {
            throw new AssertionError("Assertion failed.");
        }
    }

    public static void main(String[] args) {
        // @formatter:off
        assertEquals(convert(        0), "零");
        assertEquals(convert(        1), "一");
        assertEquals(convert(       10), "十");
        assertEquals(convert(       11), "十一");
        assertEquals(convert(      101), "百一");
        assertEquals(convert(      111), "百十一");
        assertEquals(convert(      150), "百五十");
        assertEquals(convert(     1800), "千八百");
        assertEquals(convert(     1111), "千百十一");
        assertEquals(convert(     5420), "五千四百二十");
        assertEquals(convert(     9999), "九千九百九十九");
        assertEquals(convert(   1_0000), "一万");
        assertEquals(convert(   1_0001), "一万一");
        assertEquals(convert(   1_0111), "一万百十一");
        assertEquals(convert(   9_9999), "九万九千九百九十九");
        assertEquals(convert(  16_8000), "十六万八千");
        assertEquals(convert(  12_3000), "十二万三千");
        assertEquals(convert(  11_1001), "十一万千一");
        assertEquals(convert(  71_4000), "七十一万四千");
        assertEquals(convert( 101_1001), "百一万千一");
        assertEquals(convert( 320_0000), "三百二十万");
        assertEquals(convert( 321_0000), "三百二十一万");
        assertEquals(convert(3900_0000), "三千九百万");
        assertEquals(convert(5700_0000), "五千七百万");
        assertEquals(convert(1000_0000), "千万");
        assertEquals(convert(1111_1111), "千百十一万千百十一");
        assertEquals(convert(1001_1001), "千一万千一");
        assertEquals(convert(5555_5555), "五千五百五十五万五千五百五十五");
        assertEquals(convert(9999_9999), "九千九百九十九万九千九百九十九");
        // @formatter:on
    }
}