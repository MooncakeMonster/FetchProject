package mooncakemonster.orbitalcalendar.importexternals;

import mooncakemonster.orbitalcalendar.database.Appointment;

/**
 * Created by MunKeat_2 on 22/7/2015.
 */
public class ImportedAppointment extends Appointment {
    private boolean toImport = true;

    public void toggle() {
        toImport = (toImport == true) ? false : true;
    }

    public void setToImport() {
        toImport = true;
    }

    public void notToImport() {
        toImport = false;
    }

    public boolean isToImport() {
        return toImport;
    }
}
