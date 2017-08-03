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
    static int getRoutinePeriod() { return 30000; }
    static int getDiscardPerRoutine() { return 20; }
    static long getTimeOutPeriod() { return 30000; } // 10 sec
    static long getDiscardOldDataPeriod() { return 10000000; } // about 3h
    static int getMaxStorageSupplyData() { return 1000; }
    static String getLogFileName() { return "Lolita_log.txt"; }
    static int getServerId() { return 255; }
    static String getPassFileName() { return "Lolita_pass.txt"; }
    static String getDumpFileName() { return "Lolita_dump.bin"; }
}
