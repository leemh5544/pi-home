package com.javacafe.pihome;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.firebase.client.AuthData;
import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.Firebase.AuthResultHandler;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;
import com.firebase.client.ValueEventListener;

@RestController
public class TemperatureController extends RunOnPi {
	@Autowired
	private RelayController relayController;
	private boolean runAutomated = true;
	private TemperatureState tsSetting;
	private TemperatureState tsLast;
	
	
	public TemperatureController(){
		Firebase base = new Firebase("https://pi-home.firebaseio.com/state/temp");
		base.authWithPassword("pi-home@gmail.com", "javacafe", new AuthResultHandler() {
			
			@Override
			public void onAuthenticationError(FirebaseError fe) {
				log.error("Login Error : " +  fe);
			}
			
			@Override
			public void onAuthenticated(AuthData data) {
				log.info("Login Success : " +  data);
			}
		});

		Firebase r = base.child("runAutomated");
		r.addValueEventListener(new ValueEventListener() {
			@Override
			public void onDataChange(DataSnapshot ds) {
				boolean rslt = (boolean)ds.getValue();
				runAutomated = rslt;
				log.info("RunAutomated Changed :" + rslt);
				if(runAutomated == true) controlRelays();
			}
			
			@Override
			public void onCancelled(FirebaseError arg0) {}
		});
		
		Firebase s = base.child("usrSetting");
		s.addValueEventListener(new ValueEventListener() {
			@Override
			public void onDataChange(DataSnapshot ds) {
				TemperatureState ts = ds.getValue(TemperatureState.class);
				log.info("User Temperature Setting Changed :" + ts);
				tsSetting = ts;
				controlRelays();
			}
			
			@Override
			public void onCancelled(FirebaseError arg0) {}
		});
		
		
		
		Firebase history = base.child("history"); 
		Query qRef = history.limitToLast(1);
		
		qRef.addChildEventListener(new ChildEventListener() {
			
			@Override
			public void onCancelled(FirebaseError arg0) {}
			
			@Override
			public void onChildRemoved(DataSnapshot arg0) {}
			
			@Override
			public void onChildMoved(DataSnapshot arg0, String arg1) {}
			
			@Override
			public void onChildChanged(DataSnapshot arg0, String arg1) {}
			
			@Override
			public void onChildAdded(DataSnapshot snapshot, String pck) {
				TemperatureState ts = snapshot.getValue(TemperatureState.class);
				tsLast = ts;
				log.info("Temperature Infomation Added : " + ts);
				controlRelays();				
			}
		});
		
	}
	
	@RequestMapping("/temp/{mode}/{temp}")
	public void test(@PathVariable String mode, @PathVariable float temp){
		TemperatureState ts = new TemperatureState();
		ts.setTemp(temp);
		
		Firebase f = new Firebase("https://pi-home.firebaseio.com/state/temp");
		
		switch(mode){
		case "usr" :
			f.child("usrSetting").setValue(ts);
			break;
		case "add" :
			f.child("history").push().setValue(ts);
			break;
		case "auto" :
			f.child("runAutomated").setValue(temp > 0 ? true : false);
		}
	}
	
	private void controlRelays(){
		if(tsSetting == null || tsLast == null || runAutomated == false){
			return;
		}
		
		float usr = tsSetting.getTemp();
		float temp = tsLast.getTemp();
		
		log.info(String.format("Current/User Settings Temperature : %.1f / %.1f",temp,usr));
		//R0가 히터, R1이 팬
		if(temp > usr){
			log.info("Temperature is High, Run Fan, Off Heater");
			relayController.setFirebaseRelayState(new WebRelayState(0,true));
			relayController.setFirebaseRelayState(new WebRelayState(1,false));
		}else if(temp < usr){
			log.info("Temperature is Low, Off Fan, Run Heater");
			relayController.setFirebaseRelayState(new WebRelayState(0,false));
			relayController.setFirebaseRelayState(new WebRelayState(1,true));
		}
		
	}
}
