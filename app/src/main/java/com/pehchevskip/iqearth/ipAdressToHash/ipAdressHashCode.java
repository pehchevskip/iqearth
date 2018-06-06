package com.pehchevskip.iqearth.ipAdressToHash;

public class ipAdressHashCode {

    private String ipAdressHash;
    public ipAdressHashCode(){
    }
    public String transform(String ipAdress){
        int sum;
        StringBuilder builder=new StringBuilder();
        for(int i=0;i<ipAdress.length();i++){
            if(ipAdress.charAt(i)=='.'){
                builder.append('a');
                continue;
            }
            sum=(int)ipAdress.charAt(i);
            sum+=1;
            builder.append((char)sum);
        }
        ipAdressHash=builder.toString();
        return ipAdressHash;
    }
    public String decode(String ipAdress){
        int sum;
        StringBuilder builder=new StringBuilder();
        for(int i=0;i<ipAdress.length();i++){
            if(ipAdress.charAt(i)=='a'){
                builder.append('.');
                continue;
            }
            sum=(int)ipAdress.charAt(i);
            sum-=1;
            builder.append((char)sum);
        }
        String ipAddress=builder.toString();
        return ipAddress;
    }
}
