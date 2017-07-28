// file:    ParamsProvider.java
// author:  uma
// date:    17-06-17
// detail:  this class works as just a const parameter's container
//          (i want to use easy-to-maintenance data structure like json)

package src;

class ParamsProvider {
    static int getPortNum() { return 1707; }
    static int getMaxThreadNum() { return 20; }
    static int getMaxClientNum() { return 7; }
    static long getTimeOutPeriod() { return 10000; } // 10 sec
    static long getDiscardOldDataPeriod() { return 10000000; } // about 3h
    static String getLogFileName() { return "Lolita_log.txt"; }
    static int getServerId() { return 255; }
    static String getPassFileName() { return "Lolita_pass.txt"; }
}
