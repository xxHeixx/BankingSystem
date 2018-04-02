package client;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;

import shared.Request;

public class ClientMain {
	private static final String INSTRUCTION = "Input your operation or h for help:\n";
    private static final String HELP_INTRO = "LIST OF OPERATIONS (day is from 0 to 6 (Monday to Sunday), Format of time is hh:mm) \n";
    private static final String QUERY_AVAILABLE = "Query availability operation: q facility_name day1,day2,...,dayN (N <= 6)\n";
    private static final String BOOKING = "Book a facility: b facility_name start end (format of start, end, is day:time)\n";
    private static final String EDIT_BOOKING = "Edit a booking: e confirmation_id edit_mode minutes (edit_mode 0: advance (at most 60 minutes), 1: postpone (atmost 30 minutes)\n";
    private static final String MONITORING = "Monitoring a facility: m facility_name end (end is day:time)\n";
    private static final String GET_ALL_AVAILABLE_IN_TIME_RANGE = "Get all available facilities in time range: g day time_start time_end\n";
    private static final String CANCEL_BOOKING = "Cancel a booking: c confirmation_id\n";
    private static final String QUIT_PROGRAM = "Quit client: Q (or Ctrl + C)\n";
	
	
	public static void main(String[] args) throws UnknownHostException {
		InetAddress serverIp = InetAddress.getByName(args[0]);
		int serverPort = Integer.parseInt(args[1]);

        Client client = new Client(serverIp, serverPort);
        try {
            client.start();
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        String[] params;
        String error;
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        while (true) {
            System.out.print(INSTRUCTION);
            params = br.readLine().split(" ");
            if (params[0].equals(ClientUI.HELP)) {
                printHelp();
                continue;
            } else {
                switch (params[0]) {
                case Request.SIGN_UP:
                    error = client.sendRequest(params[0], (ArrayList<String>) Arrays.asList(params));
                    if (error != null) {
                        printError(params[0], error);
                    }
                    break;
                case Request.BOOK:
                    error = client.bookFacility(params[1], params[2], params[3]);
                    if (error != null) {
                        printError(Request.BOOK, error);
                    }
                    break;
                case Request.EDIT:
                    error = client.editBooking(params[1], params[2], params[3]);
                    if (error != null) {
                        printError(Request.EDIT, error);
                    }
                    break;
                case Request.MONITOR:
                    error = client.monitorFacility(params[1], params[2]);
                    if (error != null) {
                        printError(Request.MONITOR, error);
                    }
                    break;
                case Request.CANCEL:
                    error = client.cancelBooking(params[1]);
                    if (error != null) {
                        printError(Request.CANCEL, error);
                    }
                    break;
                case Request.GET_ALL:
                    error = client.getAllAvailableFacilitiesInTimeRange(params[1], params[2],
                            params[3]);
                    if (error != null) {
                        printError(Request.CANCEL, error);
                    }
                    break;
                case Request.QUIT:
                    return;
                default:
                    printError(Request.INVALID, "Invalid operation");
                }
            }
        }

    }
}
