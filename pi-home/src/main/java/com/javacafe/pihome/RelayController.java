package com.javacafe.pihome;

import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Component;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;
import com.firebase.client.ValueEventListener;
import com.pi4j.component.relay.Relay;
import com.pi4j.component.relay.RelayState;
import com.pi4j.device.piface.PiFace;
import com.pi4j.device.piface.impl.PiFaceDevice;
import com.pi4j.wiringpi.Spi;

@Component
public class RelayController extends RunOnPi{
	PiFace piface;
	Relay r0 , r1;
	private final Log log = LogFactory.getLog(getClass());
	
	public RelayController() throws IOException{
		if(isRunOnPi() == true){
			piface = new PiFaceDevice(PiFace.DEFAULT_ADDRESS, Spi.CHANNEL_0);
			r0 = piface.getRelay(0);
			r1 = piface.getRelay(1);
		}
		
		Firebase f = new Firebase("https://pi-home.firebaseio.com/state/relay/");
		
		Query qRef = f.orderByKey();
		qRef.addValueEventListener(new ValueEventListener() {
			
			@Override
			public void onDataChange(DataSnapshot ds) {
				
				for(DataSnapshot relays : ds.getChildren()){
					WebRelayState wrs = relays.getValue(WebRelayState.class);
					log.info(String.format("Relay State Changed : %s", wrs));
					setPiRelayState(wrs);
				}
			}
			
			@Override
			public void onCancelled(FirebaseError arg0) {}
		});
	}
	
	/**
	 * 파이의 릴레이의 상태를 변경한다
	 * @param wrs
	 */
	private void setPiRelayState(WebRelayState wrs){
		RelayState rs = wrs.getRelayState();
		log.info(String.format("Relay State Set : %s", wrs));
		if(isRunOnPi() == true){
			switch(wrs.getLoc()){
			case 0:
				r0.setState(rs);
				break;
			case 1:
				r1.setState(rs);
				break;
			}
		}
	}
	
	public void setFirebaseRelayState(WebRelayState wrs){
		Firebase f = new Firebase("https://pi-home.firebaseio.com/state/relay/");
		Firebase ref = f.child(Integer.toString(wrs.getLoc()));
		ref.setValue(wrs);
	}
}
