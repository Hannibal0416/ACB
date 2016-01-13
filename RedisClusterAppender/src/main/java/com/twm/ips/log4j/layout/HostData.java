package com.twm.ips.log4j.layout;

import java.net.UnknownHostException;

public class HostData {

    public String hostName;

    public String getHostName() {
        return hostName;
    }

    public void setHostName(String hostName) {
        this.hostName = hostName;
    }

    public HostData() {
        try {
            this.hostName = java.net.InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            setHostName("unknown-host");
        }
    }
}