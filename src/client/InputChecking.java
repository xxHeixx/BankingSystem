package client;

import client.ClientString;
import shared.Currency;
import shared.Request;

public class InputChecking {
	// This function will check for syntax error in user input
	public static String checkSyntax(String[] params){
		String error;
		switch (params[0]) {
        case Request.SIGN_UP:
        	if (params.length>5){return ClientString.ERR_PARAM_REDUNDANT;}
        	else if (params.length<5){return ClientString.ERR_PARAM_LACK;}
        	error = checkName(params[1]);
        	if (error!=ClientString.CORRECT){return error;}
        	error = checkPassword(params[2]);
        	if (error!=ClientString.CORRECT){return error;}
        	error = checkCurrency(params[3]);
        	if (error!=ClientString.CORRECT){return error;}
        	error = checkAmount(params[4]);
        	if (error!=ClientString.CORRECT){return error;}
            break;
            
        case Request.CLOSE:
        case Request.BALANCE:
        	if (params.length>4){return ClientString.ERR_PARAM_REDUNDANT;}
        	else if (params.length<4){return ClientString.ERR_PARAM_LACK;}
        	error = checkName(params[1]);
        	if (error!=ClientString.CORRECT){return error;}
        	error = checkAccId(params[2]);
        	if (error!=ClientString.CORRECT){return error;}
        	error = checkPassword(params[3]);
        	if (error!=ClientString.CORRECT){return error;}
            break;
            
        case Request.DEPOSIT:
        case Request.WITHDRAW:
        	if (params.length>6){return ClientString.ERR_PARAM_REDUNDANT;}
        	else if (params.length<6){return ClientString.ERR_PARAM_LACK;}
        	error = checkName(params[1]);
        	if (error!=ClientString.CORRECT){return error;}
        	error = checkAccId(params[2]);
        	if (error!=ClientString.CORRECT){return error;}
        	error = checkPassword(params[3]);
        	if (error!=ClientString.CORRECT){return error;}
        	error = checkCurrency(params[4]);
        	if (error!=ClientString.CORRECT){return error;}
        	error = checkAmount(params[5]);
        	if (error!=ClientString.CORRECT){return error;}
            break;
            
        case Request.TRANSFER:
        	if (params.length>7){return ClientString.ERR_PARAM_REDUNDANT;}
        	else if (params.length<7){return ClientString.ERR_PARAM_LACK;}
        	error = checkName(params[1]);
        	if (error!=ClientString.CORRECT){return error;}
        	error = checkAccId(params[2]);
        	if (error!=ClientString.CORRECT){return error;}
        	error = checkPassword(params[3]);
        	if (error!=ClientString.CORRECT){return error;}
        	error = checkAccId(params[4]);
        	if (error!=ClientString.CORRECT){return error;}
        	error = checkCurrency(params[5]);
        	if (error!=ClientString.CORRECT){return error;}
        	error = checkAmount(params[6]);
        	if (error!=ClientString.CORRECT){return error;}
            break;
            
        case Request.MONITOR:
        	if (params.length>2){return ClientString.ERR_PARAM_REDUNDANT;}
        	else if (params.length<2){return ClientString.ERR_PARAM_LACK;}
        	error = checkPeriod(params[1]);
        	if (error!=ClientString.CORRECT){return error;}
        	break;
        case Request.QUIT:
        	break;
        default:
        	return ClientString.ERR_OPERATION;
		}
		return ClientString.CORRECT;
	}
	
	// Name must not be null
	private static String checkName(String name){
		if (name!=null){
			return ClientString.CORRECT;
	    } else {
			return ClientString.ERR_NAME;
		}
	}
	
	// AccountID must be an integer
	private static String checkAccId(String accId){
		try { 
	        Integer.parseInt(accId); 
	    } catch(NumberFormatException e) { 
	        return ClientString.ERR_ACCID; 
	    } catch(NullPointerException e) {
	        return ClientString.ERR_ACCID;
	    }
	    return ClientString.CORRECT;
	}
	
	// Time period must be an integer
	private static String checkPeriod(String period){
		try { 
	        Integer.parseInt(period); 
	    } catch(NumberFormatException e) { 
	        return ClientString.ERR_PERIOD; 
	    } catch(NullPointerException e) {
	        return ClientString.ERR_PERIOD;
	    }
	    return ClientString.CORRECT;
	}
	
	// Password must have 4 characters
	private static String checkPassword(String password){
		if (password.length()!=4){
			return ClientString.ERR_PASSWORD;
		}
	    return ClientString.CORRECT;
	}
	
	// Currency must be correct
	private static String checkCurrency(String currency){
		if (!Currency.isValid(currency)){
			return ClientString.ERR_CURRENCY;
		}
		return ClientString.CORRECT;
	}
	
	// Amount of money must be double
	private static String checkAmount(String amount){
		try { 
	        Double.parseDouble(amount); 
	    } catch(NumberFormatException e) { 
	        return ClientString.ERR_AMOUNT; 
	    } catch(NullPointerException e) {
	        return ClientString.ERR_AMOUNT;
	    }
	    return ClientString.CORRECT;
	}
}
