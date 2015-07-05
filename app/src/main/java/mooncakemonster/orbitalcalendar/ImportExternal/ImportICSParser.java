package mooncakemonster.orbitalcalendar.ImportExternal;

import android.app.Activity;
import android.os.Bundle;

import net.fortuna.ical4j.data.CalendarParserImpl;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.regex.Pattern;

/**
 * Read all .ics extension
 */
public class ImportICSParser extends Activity{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        CalendarParserImpl parse;
    }

    public static void icsReader(String file)
    {
        int counter;

        String event = null;
        String location = null;
        long startMillisec = 0;
        long endMillisec = 0;
        String notes = null;

        try
        {
            //Open resources
            FileReader dataFile = new FileReader(file);
            BufferedReader bufferedDataFile = new BufferedReader(dataFile);

            //Read and obtain the number of lines in file
            String line = bufferedDataFile.readLine();
            final int numLine = Integer.parseInt(line);

            for(counter = 0; counter < numLine ; counter++)
            {
                line = bufferedDataFile.readLine();

                //Split string
                String[] split = line.split(Pattern.quote(":") , 2);

                String field = split[0];
                String value = split[1];

            }
            //Close resources
            bufferedDataFile.close();
            dataFile.close();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    //Helper method for processing rules in ics
    private static void processRule(String rule)
    {

    }


    //Need a builder to take in inputs and create appointments

    //Once appointments are created, let user verify themselves personally
}
