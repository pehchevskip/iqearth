package com.pehchevskip.iqearth.ipAddressToHash;

public class ipAddressHashCode {

    private String ipAdressHash;
    public ipAddressHashCode(){
    }
    public String transform(String ipAdress){
        int sum;
        int dif = 'b' - '0';
        StringBuilder builder=new StringBuilder();
        for(int i=0;i<ipAdress.length();i++){
            if(ipAdress.charAt(i)=='.'){
                builder.append('a');
                continue;
            }
//            sum=(int)ipAdress.charAt(i);
//            sum+=1;
            sum = ipAdress.charAt(i) + dif;
            builder.append((char)sum);
        }
        ipAdressHash=builder.toString();
        return ipAdressHash;
    }
    public String decode(String ipAdress){
        int sum;
        int dif = 'b' - '0';
        StringBuilder builder=new StringBuilder();
        for(int i=0;i<ipAdress.length();i++){
            if(ipAdress.charAt(i)=='a'){
                builder.append('.');
                continue;
            }
//            sum=(int)ipAdress.charAt(i);
//            sum-=1;
            sum = ipAdress.charAt(i) - dif;
            builder.append((char)sum);
        }
        String ipAddress=builder.toString();
        return ipAddress;
    }
}
