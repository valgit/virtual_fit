/*
 * use a chronometer and generate a fit file
 * for an esport activity
 */

import com.garmin.fit.ActivityMesg;
import com.garmin.fit.DateTime;
import com.garmin.fit.DeviceIndex;
import com.garmin.fit.DeviceInfoMesg;
import com.garmin.fit.Event;
import com.garmin.fit.EventMesg;
import com.garmin.fit.EventType;
import com.garmin.fit.File;
import com.garmin.fit.FileEncoder;
import com.garmin.fit.FileIdMesg;
import com.garmin.fit.Fit;
import com.garmin.fit.FitRuntimeException;
import com.garmin.fit.LapMesg;
import com.garmin.fit.Manufacturer;
import com.garmin.fit.Mesg;
import com.garmin.fit.RecordMesg;
import com.garmin.fit.SessionMesg;
import com.garmin.fit.Sport;
import com.garmin.fit.SubSport;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.TimeZone;

/*
 * generic working
// 1. Create the output stream
FileStream stream = new FileStream("/output/path/filename.fit", FileMode.Create, FileAccess.ReadWrite, FileShare.Read);

// 2. Create an instance of an Encode object.
Encode encoder = new Encode(ProtocolVersion.V20);

// 3. Write the FIT header to the output stream.
encoder.Open(stream);

// 4. Write a File Id message to the output stream
var fileIdMesg = new FileIdMesg();
fileIdMesg.SetType(Dynastream.Fit.File.Activity);
fileIdMesg.SetManufacturer(Manufacturer.Development);
fileIdMesg.SetProduct(ProductId);
fileIdMesg.SetSerialNumber(DeviceSerialNumber);
fileIdMesg.SetTimeCreated(startTime);
encoder.Write(fileIdMesg);

// 5. Write messages specific to the file type to the output stream
.
.
.

// 6. Update the data size in the header and calculate the CRC
encoder.Close();

// 7. Close the output stream
stream.Close();
 */

public class virtual_fit {

    public static void main(String[] args) {
        try {
            // Check if filename is provided
            if (args.length < 1) {
                System.out.println("Please provide a filename as a command line argument.");
                return;
            }

            // Use the filename from the command line arguments
            String filename = args[0];

            java.io.File outputFile = new java.io.File(filename);
            FileEncoder  fileEncoder = new FileEncoder(outputFile, Fit.ProtocolVersion.V2_0);
            //fileEncoder.Open(stream);

            // ready to generate fit data
            CreateTimeBasedActivity(fileEncoder);
            //CreateLapSwimActivity();

            // flush all data to the file
            fileEncoder.close();
            //outputFile.close();

            System.out.println("wrote file, can be found at: " + filename);
        } catch (Exception e) {
            System.out.println("Exception encoding activity: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void CreateTimeBasedActivity(FileEncoder fileEncoder) {
        List<Mesg> messages = new ArrayList<Mesg>();

        System.out.println("run the loop");

        // The starting timestamp for the activity
        DateTime startTime = new DateTime(new Date());

        // add start event mesg
        EventMesg startEventMesg = createEvent(EventType.START);
        messages.add(startEventMesg);

        System.out.println("run loop");
        // infinite loop with a sleep time of 1 sec
        for (int i = 0; i < 10; i++) {
            try {
                // create a new fit record
                RecordMesg record = new RecordMesg();
                record.setTimestamp(new DateTime(new Date()));

                // add data of various type
                record.setDistance(100.0f);
                record.setHeartRate((short)100);

                // Write the Record message to the output stream
                messages.add(record);

                Thread.sleep(1000);
                System.out.println("sleeping");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.println("end loop");
        // add stop event msg
        EventMesg stopEventMesg = createEvent(EventType.STOP);
        messages.add(stopEventMesg);

        // add a minimum lap message
        LapMesg lapMesg = addLap();
        messages.add(lapMesg);
        System.out.println("lap mesg");

        // add a session message
        SessionMesg sessionMesg = newSession(startEventMesg.getTimestamp(), stopEventMesg.getTimestamp());
        messages.add(sessionMesg);
        System.out.println("session mesg");

        // add the activity message
        ActivityMesg activityMesg = newActivity();
        messages.add(activityMesg);

        // save the file ?
        //TODO: to do it on the flow
        for (Mesg message : messages) {
            fileEncoder.write(message);
        }

    }

    /**
     * Creates a new EventMesg object with the specified event type.
     *
     * @param eventType The type of the event. This can be either EventType.START or EventType.STOP_DISABLE.
     * @return An EventMesg object representing the event.
     */
    public static EventMesg createEvent(EventType eventType) {
        EventMesg eventMesg = new EventMesg();
        eventMesg.setTimestamp(new DateTime(new Date()));
        eventMesg.setEvent(Event.TIMER);
        eventMesg.setEventType(eventType);
        eventMesg.setEventGroup((short)0);
        //eventMesg.setDeviceIndex((short)DeviceIndex.MAIN);
        System.out.println(eventType == EventType.START ? "start event mesg" : "stop event mesg");
        return eventMesg;
    }

    public static LapMesg addLap() {
        LapMesg lapMesg = new LapMesg();
        lapMesg.setTimestamp(new DateTime(new Date().getTime()));
        lapMesg.setEvent(Event.LAP);
        lapMesg.setEventType(EventType.STOP);

        //TODO:
        lapMesg.setTotalElapsedTime(1000.0f);
        lapMesg.setTotalTimerTime(1000.0f);

        //TODO: lapMesg.setTrigger(Event.LAP_TRIGGER_MANUAL);
        //TODO: not def !
        // 63 Sport.E_SPORTS);
        lapMesg.setSport(Sport.SAILING);
        // VIRTUAL_ACTIVITY // 77 SubSport.E_SPORTS);
        lapMesg.setSubSport(SubSport.VIRTUAL_ACTIVITY);
        return lapMesg;
    }

    /**
     * Creates a new SessionMesg object with the specified start and stop times.
     *
     * @param startTime The start time of the session.
     * @param stopTime The stop time of the session.
     * @return A SessionMesg object representing the session.
     */
    public static SessionMesg newSession(com.garmin.fit.DateTime startTime, com.garmin.fit.DateTime stopTime) {
        SessionMesg session = new SessionMesg();

        // Every FIT ACTIVITY file MUST contain Record messages



        // Set the timestamp of the session to the start time
        session.setTimestamp(new DateTime(new Date()));
        session.setEvent(Event.TIMER);
        session.setEventType(EventType.START);
        session.setSport(Sport.SAILING);
        session.setSubSport(SubSport.VIRTUAL_ACTIVITY);
        session.setFirstLapIndex(0);
        session.setNumLaps(1);
        // Set the start time of the session
        session.setStartTime(startTime);
        // Calculate and set the total elapsed time of the session
        session.setTotalElapsedTime((float)(stopTime.getTimestamp() - startTime.getTimestamp()) / 1000);
        session.setTotalTimerTime((float)(stopTime.getTimestamp() - startTime.getTimestamp()) / 1000);

        return session;
    }

    public static ActivityMesg newActivity() {
        ActivityMesg activityMesg = new ActivityMesg();

        activityMesg.setTimestamp(new DateTime(new Date().getTime()));
        activityMesg.setNumSessions(1);
        TimeZone timeZone = TimeZone.getTimeZone("Europe/Paris");
        long timezoneOffset = (timeZone.getRawOffset() + timeZone.getDSTSavings()) / 1000;
        activityMesg.setLocalTimestamp(new DateTime(new Date()).getTimestamp() + timezoneOffset);
        activityMesg.setTotalTimerTime((float) 3600.0f);
        return activityMesg;
    }

    public static void CreateActivityFile(List<Mesg> messages, String filename, DateTime startTime) {
        // The combination of file type, manufacturer id, product id, and serial number should be unique.
        // When available, a non-random serial number should be used.
        File fileType = File.ACTIVITY;
        short manufacturerId = Manufacturer.DEVELOPMENT;
        short productId = 0;
        float softwareVersion = 1.0f;

        Random random = new Random();
        int serialNumber = random.nextInt();

        // Every FIT file MUST contain a File ID message
        FileIdMesg fileIdMesg = new FileIdMesg();
        fileIdMesg.setType(fileType);
        fileIdMesg.setManufacturer((int) manufacturerId);
        fileIdMesg.setProduct((int) productId);
        fileIdMesg.setTimeCreated(startTime);
        fileIdMesg.setSerialNumber((long) serialNumber);

        // A Device Info message is a BEST PRACTICE for FIT ACTIVITY files
        DeviceInfoMesg deviceInfoMesg = new DeviceInfoMesg();
        deviceInfoMesg.setDeviceIndex(DeviceIndex.CREATOR);
        deviceInfoMesg.setManufacturer(Manufacturer.DEVELOPMENT);
        deviceInfoMesg.setProduct((int) productId);
        deviceInfoMesg.setProductName("FIT Cookbook"); // Max 20 Chars
        deviceInfoMesg.setSerialNumber((long) serialNumber);
        deviceInfoMesg.setSoftwareVersion(softwareVersion);
        deviceInfoMesg.setTimestamp(startTime);

        // Create the output stream
        FileEncoder encode;

        try {
            encode = new FileEncoder(new java.io.File(filename), Fit.ProtocolVersion.V2_0);
        } catch (FitRuntimeException e) {
            System.err.println("Error opening file " + filename);
            e.printStackTrace();
            return;
        }

        encode.write(fileIdMesg);
        encode.write(deviceInfoMesg);

        for (Mesg message : messages) {
            encode.write(message);
        }

        // Close the output stream
        try {
            encode.close();
        } catch (FitRuntimeException e) {
            System.err.println("Error closing encode.");
            e.printStackTrace();
            return;
        }
        System.out.println("Encoded FIT Activity file " + filename);
    }

}
