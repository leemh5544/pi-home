package com.javacafe.pihome;

import com.pi4j.component.relay.RelayState;

public class WebRelayState {
	private int loc = 0;
	//true : Relay Close(Connected) , False : Relay Open(Disconnected)
	private boolean flag = false;
	private String relayState;
	
	public WebRelayState(){};
	
	public WebRelayState(int loc, boolean flag){
		this.loc = loc;
		this.flag = flag;
	}

	public int getLoc() {
		return loc;
	}

	public void setLoc(int loc) {
		this.loc = loc;
	}

	public boolean isFlag() {
		return flag;
	}

	public void setFlag(boolean flag) {
		this.flag = flag;
	}
	
	public RelayState getRelayState(){
		return flag == true ? RelayState.CLOSED : RelayState.OPEN;
	}
	
	public void setRelayState(String relayState) {
		this.relayState = relayState;
	}

	@Override
	public String toString() {
		return "RelaySate [loc=" + loc + ", flag=" + flag + "]";
	}
	
	
}
