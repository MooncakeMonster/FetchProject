package mooncakemonster.orbitalcalendar.registeruser;

/**
 * Invoked when background task is completed.
 */

interface GetUserCallback {
    public abstract void done(User returnedUser);
}
