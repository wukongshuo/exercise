package com.xxw.exception;

import lombok.Data;

/**
 * 抛出这个错误说明需要运维人员来解决
 * 
 * @author chenmd
 *
 */
@Data
public class InternalServerError extends RuntimeException{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String code;
	
	private String message;

	protected InternalServerError(String code, String message) {
		this.message = message;
		this.code = code;
	}
	
	public static void throwInternalError() {
		throw new InternalServerError("com-5001", "系统内部错误，请联系管理员！");
	}
	
	public static void throwInternalError(String detail) {
		throw new InternalServerError("com-5001", "系统内部错误，请联系管理员！" + detail);
	}
}
