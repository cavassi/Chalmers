package chalmers.tda367.testing.date;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MyDateTest {

    @Test
    void testConstructor1() {
        MyDate date = new MyDate(2023, 11, 8);

        assertEquals(date.getYear(), 2023);
        assertEquals(date.getMonth(), 11);
        assertEquals(date.getDay(), 8);
    }
    
    @Test
    void testConstructorThrows() {
        assertThrows(IllegalArgumentException.class, () -> new MyDate(2023, 13, 8));
    }

    @Test
    void testConstructorIllegalLeapYear() {
        assertThrows(IllegalArgumentException.class, () -> new MyDate(2023, 2, 29));
    }

    @Test
    void testToString() {
        MyDate date = new MyDate(2000, 07, 30);
        assertEquals(date.toString(),"2000-07-30");
    }

    @Test
    void testEquals() {
        MyDate date1 = new MyDate(2000, 07, 30);
        MyDate date2 = new MyDate(2000, 07, 31);
        assertEquals(date1.equals(date2), true);
        assertEquals(date1.equals(date2), false);

    }

    @Test
    void testCompareTo() {
        MyDate date1 = new MyDate(2002, 12, 19);
        MyDate date2 = new MyDate(2023, 11, 8);

        assertEquals(date1.compareTo(date2), -1);
        assertEquals(date2.compareTo(date1), 1);
        assertEquals(date1.compareTo(date1), 0);

    }

    @Test
    void testNext() {
        MyDate date1 = new MyDate(2000, 07, 30);
        MyDate date2 = new MyDate(2000, 07, 31);

        assertEquals(date1.next().equals(date2), 1);
        assertEquals(date1.next().equals(date2), 1);

    }
}