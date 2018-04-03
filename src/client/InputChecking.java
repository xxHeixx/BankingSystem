package client;

import shared.Currency;
import shared.Request;

public class InputChecking {
	public static final String CORRECT = "Correct syntax";
	public static final String ERR_ACCID = "Account number must be an integer";
	public static final String ERR_PERIOD = "Time period must be an integer";
	public static final String ERR_AMOUNT = "Amount of money must be a double";
	public static final String ERR_PASSWORD = "Password must have exactly 4 characters";
	public static final String ERR_CURRENCY = "Currency must be SGD, USD, EUR or GBP";
	public static final String ERR_PARAM_LACK = "Not enough parameters";
	public static final String ERR_PARAM_REDUNDANT = "Too many parameters";
	public static final String ERR_OPERATION = "Invalid operation";
	
	public static String checkSyntax(String[] params){
		String error;
		switch (params[0]) {
        case Request.SIGN_UP:
        	if (params.length>5){return ERR_PARAM_REDUNDANT;}
        	else if (params.length<5){return ERR_PARAM_LACK;}
        	error = InputChecking.checkPassword(params[2]);
        	if (error!=InputChecking.CORRECT){return error;}
        	error = InputChecking.checkCurrency(params[3]);
        	if (error!=InputChecking.CORRECT){return error;}
        	error = InputChecking.checkAmount(params[4]);
        	if (error!=InputChecking.CORRECT){return error;}
            break;
            
        case Request.CLOSE:
        case Request.BALANCE:
        	if (params.length>4){return ERR_PARAM_REDUNDANT;}
        	else if (params.length<4){return ERR_PARAM_LACK;}
        	error = InputChecking.checkAccId(params[2]);
        	if (error!=InputChecking.CORRECT){return error;}
        	error = InputChecking.checkPassword(params[3]);
        	if (error!=InputChecking.CORRECT){return error;}
            break;
            
        case Request.DEPOSIT:
        case Request.WITHDRAW:
        	if (params.length>6){return ERR_PARAM_REDUNDANT;}
        	else if (params.length<6){return ERR_PARAM_LACK;}
        	error = InputChecking.checkAccId(params[2]);
        	if (error!=InputChecking.CORRECT){return error;}
        	error = InputChecking.checkPassword(params[3]);
        	if (error!=InputChecking.CORRECT){return error;}
        	error = InputChecking.checkCurrency(params[4]);
        	if (error!=InputChecking.CORRECT){return error;}
        	error = InputChecking.checkAmount(params[5]);
        	if (error!=InputChecking.CORRECT){return error;}
            break;
            
        case Request.TRANSFER:
        	if (params.length>7){return ERR_PARAM_REDUNDANT;}
        	else if (params.length<7){return ERR_PARAM_LACK;}
        	error = InputChecking.checkAccId(params[2]);
        	if (error!=InputChecking.CORRECT){return error;}
        	error = InputChecking.checkPassword(params[3]);
        	if (error!=InputChecking.CORRECT){return error;}
        	error = InputChecking.checkAccId(params[4]);
        	if (error!=InputChecking.CORRECT){return error;}
        	error = InputChecking.checkCurrency(params[5]);
        	if (error!=InputChecking.CORRECT){return error;}
        	error = InputChecking.checkAmount(params[6]);
        	if (error!=InputChecking.CORRECT){return error;}
            break;
            
        case Request.MONITOR:
        	if (params.length>2){return ERR_PARAM_REDUNDANT;}
        	else if (params.length<2){return ERR_PARAM_LACK;}
        	error = InputChecking.checkPeriod(params[1]);
        	if (error!=InputChecking.CORRECT){return error;}
        	break;
        case Request.QUIT:
        	break;
        default:
        	return ERR_OPERATION;
		}
		return CORRECT;
	}
	
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
	
	public static String checkPeriod(String period){
		try { 
	        Integer.parseInt(period); 
	    } catch(NumberFormatException e) { 
	        return ERR_PERIOD; 
	    } catch(NullPointerException e) {
	        return ERR_PERIOD;
	    }
	    return CORRECT;
	}
	
	public static String checkPassword(String password){
		if (password.length()!=4){
			return ERR_PASSWORD;
		}
	    return CORRECT;
	}
	
	public static String checkCurrency(String currency){
		if (!Currency.isValid(currency)){
			return ERR_CURRENCY;
		}
		return CORRECT;
	}
	
	public static String checkAmount(String amount){
		try { 
	        Double.parseDouble(amount); 
	    } catch(NumberFormatException e) { 
	        return ERR_AMOUNT; 
	    } catch(NullPointerException e) {
	        return ERR_AMOUNT;
	    }
	    return CORRECT;
	}

}
