
package me.zouooh.slark;


public class SlarkException extends Exception {

	private static final long serialVersionUID = 1L;

	public SlarkException() {
    }


    public SlarkException(String exceptionMessage) {
       super(exceptionMessage);
    }

    public SlarkException(String exceptionMessage, Throwable reason) {
        super(exceptionMessage, reason);
    }

    public SlarkException(Throwable cause) {
        super(cause);
    }

}
