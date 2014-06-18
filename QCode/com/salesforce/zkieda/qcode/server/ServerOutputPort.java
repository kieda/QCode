package com.salesforce.zkieda.qcode.server;

import java.io.OutputStream;

import com.salesforce.zkieda.util.MultiBufferedOutputStream;

public class ServerOutputPort extends OutputStream{
    private MultiBufferedOutputStream outputInternal;
    public ServerOutputPort() {
        outputInternal = new MultiBufferedOutputStream();
    }
    public void name() {
        
    }
}
