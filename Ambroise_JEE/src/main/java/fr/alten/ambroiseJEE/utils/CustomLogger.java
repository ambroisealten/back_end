package fr.alten.ambroiseJEE.utils;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

@Service
public class CustomLogger {
	private static ApplicationContext ctx;
	
	@Autowired
	private ApplicationContext ctxAutowireed;
	
	
	private static LogLevel globalLogMode;

	@PostConstruct
	private void init() {
		ctx = this.ctxAutowireed;
		globalLogMode = Enum.valueOf(LogLevel.class, ctx.getEnvironment().getProperty("LogLevel"));
	}
	
	public static void log(Object o,LogLevel logType) {
		try {
			if(getValue(logType) <= getValue(globalLogMode)) {
				System.out.println(o);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private static int getValue(LogLevel logLevel) throws Exception {
		switch(logLevel) {
		case JOKE:
			return 5;
		case DEVDEBUG:
			return 4;
		case DEBUG:
			return 3;
		case DEV:
			return 2;
		case PROD:
			return 1;
		default:
			throw new Exception("There is no such log level : "+logLevel.toString());
		}
	}
}
