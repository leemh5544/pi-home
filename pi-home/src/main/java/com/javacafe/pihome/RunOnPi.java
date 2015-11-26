package com.javacafe.pihome;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public abstract class RunOnPi {
	protected Log log = LogFactory.getLog(getClass());
	
	protected boolean isRunOnPi(){
		/**
		 * FIXME:: 필자는 리눅스에서 돌리면 파이라고 인식하도록 함
		 * 맥이나 실제 리눅스에서 돌리는분은 다른방법으로 구현하셔야해요 
		 */
		return System.getProperty("os.name").equals("Linux") ? true : false;
	}
	
}
