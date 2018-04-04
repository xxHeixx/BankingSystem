package client;

// Store all client string resources
public class ClientString {
	public static final String START_MSG = "Starting client...\n";
	public static final String EXIT_MSG = "Exiting client...\n";
	public static final String INSTRUCTION_MSG = "Input your operation (input h for help):\n";
	public static final String HELP_MSG 
		= "List of available operations (parameters must be seperated by ','):\n\n"
		+ "Account sign up: s,<name>,<password>,<currency>,<initial_balance>\n"
		+ "Close account: c,<name>,<account_number>,<password>\n"
		+ "Check balance: b,<name>,<account_number>,<password>\n"
		+ "Deposit money: d,<name>,<account_number>,<password>,<currency>,<amount>\n"
		+ "Withdraw money: w,<name>,<account_number>,<password>,<currency>,<amount>\n"
		+ "Transfer money: t,<name>,<account_number>,<password>,<received_account_number>,<currency>,<amount>\n"
		+ "Monitor accounts: m,<period_in_seconds>\n"
		+ "Quit client: q\n";
	public static final String HELPCODE = "h";
	public static final String ERR_INPUT_MSG = "Input Error: %s\n";
	public static final String ERR_MSG = "Error: %s\n";
	public static final String RESENDING_MSG = "Server takes too long to reply. Resending request...\n";
	
	public static final String SIGN_UP_MSG = "You have successfully created a new account: %s\n";
	public static final String CLOSE_MSG = "Your account has been closed successfully\n";
	public static final String BALANCE_MSG = "Your account's balance: %.2f %s\n";
	public static final String DEPOSIT_MSG = "Your money has been deposited successfully. New balance: %.2f %s\n";
	public static final String WITHDRAW_MSG = "Your money has been withdrawn successfully. New balance: %.2f %s\n";
	public static final String TRANSFER_MSG = "Your money has been transfered successfully. New balance: %.2f %s\n"; 
	
	public static final String START_MONITOR_MSG = "Starting monitoring...\n";
	public static final String MONITOR_SIGN_UP_MSG = "User %s have created new account %s with initial balance of %.2f %s\n";
	public static final String MONITOR_CLOSE_MSG = "User %s have closed account %s\n";
	public static final String MONITOR_DEPOSIT_MSG = "%.2f %s has been deposited into account %s. New balance: %.2f %s\n";
	public static final String MONITOR_WITHDRAW_MSG = "%.2f %s has been withdrew from account %s. New balance: %.2f %s\n";
	public static final String MONITOR_TRANSFER_MSG = "%.2f %s has been transferred from account %s to account %s\n";
	public static final String END_MONITOR_MSG = "End monitoring...\n";
	public static final String NOMATCH_MONITOR_MSG = "This operation is not monitored";
	
	public static final String CORRECT = "Correct syntax";
	public static final String ERR_NAME = "User Name must not be null";
	public static final String ERR_ACCID = "Account number must be an integer";
	public static final String ERR_PERIOD = "Time period must be an integer";
	public static final String ERR_AMOUNT = "Amount of money must be a double";
	public static final String ERR_PASSWORD = "Password must have exactly 4 characters";
	public static final String ERR_CURRENCY = "Currency must be SGD, USD, EUR or GBP";
	public static final String ERR_PARAM_LACK = "Not enough parameters";
	public static final String ERR_PARAM_REDUNDANT = "Too many parameters";
	public static final String ERR_OPERATION = "Invalid operation";
}
