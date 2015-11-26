package com.javacafe.pihome;

import java.util.Random;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pi4j.io.serial.Serial;
import com.pi4j.io.serial.SerialFactory;

@RestController
@RequestMapping("/fnd")
public class FNDController extends RunOnPi{
	private final Log log = LogFactory.getLog(getClass());
	static Serial s;
	Boolean showProgress = false;
	
	public FNDController() {
		if(isRunOnPi() == true){
			s = SerialFactory.createInstance();
			s.open("/dev/ttyACM0", 9600);
			log.info("Serial Init : " + s.isOpen() );
		}
	}

	@RequestMapping("/prg")
	public void showProgress() throws InterruptedException {
		if(s != null){
			showProgress = true;
			log.info("Show Progress Called : " + s.isOpen() );
			Random rnd = new Random();
			for(int i = 0 ; i < 100 ; i++){
				s.write(String.format("%02d", rnd.nextInt(100)));
				s.flush();
				Thread.sleep(50);
			}
		}
	}
	
	@RequestMapping("/show/{number}")
	public void showRndNumber(@PathVariable int number){
		if(s != null){
			int i = 0;
			try{
				i = number;
			}catch(NumberFormatException ne){
				i = 0;
			}
			log.info("showRndNumber : " + s.isOpen() + " / " + String.format("%02d", i) + " / " + i);
			s.write(String.format("%02d", i));
			s.flush();
		}
	}
	
	
}
