package chalmers.tda367.testing.date;

public class MyDate {
    
    private int y;
    private int m;
    private int d;

    /** Skapar ett nytt MyDate objekt för dagen: year, month, day.
        Exempel datumet 2029-07-15 skapas med new MyDate(2029,7,15).
        @throws IllegalArgumentException ett exception ifall datumet inte är ett giltigt datum (t.ex. 2003-02-29). */
    public MyDate(int year, int month, int day) {
        y = year;
        m = month;
        d = day;
        checkIfValid(y,m,d);
    }

    public void checkIfValid(int y, int m, int d){
        if(0 < m && m < 13){
            if(daysInMonth(m, y) < d){
                throw new IllegalArgumentException();
            }
        }
        else{
            throw new IllegalArgumentException()
;        }
    }


    /** Returnerar värdet av year som gavs till construktorn */
    public int getYear() { return y; }

    /** Returnerar värdet av month som gavs till construktorn */
    public int getMonth() { return m; }

    /** Returnerar värdet av day som gavs till construktorn */
    public int getDay() { return d; }

    /** Returnerar en sträng representation av datumet */
    public String toString() {
        String res = y + "-";
        if (m < 10) { res = res + "0"; };
        res = res + m + "-";
        if (d < 10) { res = res + "0"; };
        res = res + d;
        return res;
    }

    /** Returnerar true ifall det givna objektet representerar samma datum */
    public boolean equals(Object other) {
        if (other == null) { return false; }
        if (this.getClass() != other.getClass()) { return false; }
        MyDate x = (MyDate)other;
        return (this == x);
    }

    /** Returnerar -1 ifall this är ett tidigare datum än other.
        Returnerar 1 ifall other är ett tidigare datum än this.
        Returnerar 0 ifall this och other representerar samma datum. */
    public int compareTo(MyDate other) {
        int date1 = this.y + this.m + this.d;
        int date2 = other.y + other.m + other.d;
        if(date1 > date2){
            return 1;
        }
        if(date1 < date2){
            return -1;
        }
        else{
            return 0;
        }
    }


    /** Ger true ifall year är ett skottår */
    private static boolean isLeapYear(int year) {
        if (year % 4 == 0) {
            if (year % 100 == 0) {
                if (year % 400 == 0) {
                    return true;
                } else {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    /** Returnerar hur många dagar det finns i månad m för år y */
    private static int daysInMonth(int m, int y) {
        switch (m) {
          case 4: // apr
          case 6: // jun
          case 9: // sep
          case 11: // nov
              return 30;
          case 2: // feb
              if (isLeapYear(y)) {
                  return 29;
              } else {
                  return 28;
              }
          default: // resten
              return 31;
        }
    }

    /** Returnerar datumet av dagen som kommer direkt efter detta datum */
    public MyDate next() {
        if ((m == 12) && (d == 31)) {
            return new MyDate(y+1,1,1);
        }
        if (d == daysInMonth(m,y)) {
            return new MyDate(y,m+1,1);
        }
        return new MyDate(y,m,d+1);
    }
}
