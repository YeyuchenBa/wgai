package org.jeecg.modules.tab.controller;

import com.github.xingshuangs.iot.protocol.s7.enums.EPlcType;
import com.github.xingshuangs.iot.protocol.s7.service.S7PLC;

/**
 * @author Administrator
 * @date 2024/3/5 20:31
 */
public class test {
    public static void main(String[] args) {
        S7PLC s7PLC = new S7PLC(EPlcType.S200_SMART, "192.168.0.5",102,0,0);

        boolean boolData = s7PLC.readBoolean("DB1.2.0");
        System.out.println(boolData);
        System.out.println( s7PLC.checkConnected());
    }

}
