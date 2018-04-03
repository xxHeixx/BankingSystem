package client;

public class InputChecking {
	public static final String CORRECT = "Correct syntax";
	public static final String ERR_ACCID = "Account number must be an integer";
	public static final String ERR_PASSWORD = "Password must have exactly 4 characters";
	
	public static String checkAccId(String accId){
		try { 
	        Integer.parseInt(accId); 
	    } catch(NumberFormatException e) { 
	        return ERR_ACCID; 
	    } catch(NullPointerException e) {
	        return ERR_ACCID;
	    }
	    return CORRECT;
	}
	
	public static String checkPassword(String password){
		if (password.length()!=4){
			return ERR_PASSWORD;
		}
	    return CORRECT;
	}
}
