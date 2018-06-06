package com.pehchevskip.iqearth.model;

public class WifiDevice {

    private String nickname;
    private String ip;

    public WifiDevice(String nickname, String ip) {
        this.nickname = nickname;
        this.ip = ip;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }
}
