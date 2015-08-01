package mooncakemonster.orbitalcalendar.tests;

import android.test.InstrumentationTestCase;

import java.text.SimpleDateFormat;

import mooncakemonster.orbitalcalendar.database.Constant;

/*************************************************************************************************
 * Regression Test for database (mooncakemonster.orbitalcalendar.database) package
 *
 * Test the following classes:
 * (1) Constant.java
 **************************************************************************************************/
public class databaseTest extends InstrumentationTestCase {

    public void testForCorrectDateMillisecondOutput() throws Exception {
        //(a) Tests for date to millisecond output
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy, EEE");
        String dateInput[] =    {
                                    "12/04/2005, Tue",
                                    "18/06/2011, Sat",
                                    "23/06/2012, Sat",
                                    "06/08/2019, Tue",
                                    "22/03/2020, Sun",

                                    "24/10/2023, Tue",
                                    "29/02/2000, Sat",
                                    "28/02/2030, Thu",
                                    "13/01/2045, Fri",
                                    "21/03/2048, Sat",

                                    "02/03/2053, Sun",
                                    "06/01/2093, Tue",
                                    "19/05/2093, Tue",
                                    "25/11/2093, Wed",
                                    "29/02/2004, Sun"
                                };

        long expectedDateMillisecondOutput[] = {
                                    1113235200000L,
                                    1308326400000L,
                                    1340380800000L,
                                    1565020800000L,
                                    1584806400000L,

                                    1698076800000L,
                                     951753600000L,
                                    1898438400000L,
                                    2367849600000L,
                                    2468332800000L,

                                    2624457600000L,
                                    3882009600000L,
                                    3893500800000L,
                                    3909916800000L,
                                    1077984000000L
                                };

        int len = expectedDateMillisecondOutput.length;

        for(int i = 0; i < len ;i++) {
            long generatedMillisecond = Constant.stringToMillisecond(dateInput[i], dateFormat);
            assertEquals(expectedDateMillisecondOutput[i], generatedMillisecond);
        }


        //(b) Tests for date to millisecond output
        SimpleDateFormat dateTimeFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        String dateTimeInput[] ={
                                    "01/01/2000 00:00",
                                    "01/01/2000 02:45",
                                    "01/01/2000 03:24",
                                    "01/01/2000 15:59",
                                    "01/01/2000 23:59",
                                };

        long expectedDateTimeMillisecondOutput[] = {
                                    1072886400000L,
                                    1072896300000L,
                                    1072898640000L,
                                    1072943940000L,
                                };

        len = expectedDateMillisecondOutput.length;

        for(int i = 0; i < len ;i++) {
            long generatedMillisecond = Constant.stringToMillisecond(dateTimeInput[i], dateFormat);
            assertEquals(expectedDateTimeMillisecondOutput[i], generatedMillisecond);
        }

    }
}
