package com.xxw.exception;

public class BusinessException extends RuntimeException{
	
	private String code;
	
	private String message;
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public BusinessException() {
		this("ERROR", "");
	}
	
	protected BusinessException(String code, String message) {
		this.message = message;
		this.code = code;
	}
	
	public static BusinessException error(String code, String message) {
		return new BusinessException(code, message);
	}
	
	public static BusinessException error(String message) {
		return error("ERROR", message);
	}
	

	public static void throwError(String message) {
		throwError("ERROR", message);
	}
	
	public static void throwError(String code, String message) {
		throw error(code, message);
	}
	
	public static void throwDataNotFoundException() {
		throwError("com-4001", "没有该记录！");
	}
	
	public static void throwInternalError() {
		throwError("com-5001", "系统内部错误，请联系管理员！");
	}

	public String getCode() {
		return code;
	}

	public String getMessage() {
		return message;
	}
}
