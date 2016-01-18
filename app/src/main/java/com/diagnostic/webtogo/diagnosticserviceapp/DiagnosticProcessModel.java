package com.diagnostic.webtogo.diagnosticserviceapp;

/**
 * Created by akshit on 10/9/15.
 */
public class DiagnosticProcessModel {

    int pid; //processs id
    String uid;  //unique id
    long cpu; //cpu %
    String processName;

    DiagnosticProcessModel(int pid, String uid, long cpu, String processName){
        this.pid = pid;
        this.uid = uid;
        this.cpu = cpu;
        this.processName = processName;
    }

}
