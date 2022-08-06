package com.lgjy.zk_config_center;

import org.apache.zookeeper.ZooKeeper;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class TestConfig {
    private ZooKeeper zooKeeper;
    private final MyWatcherAndCallBack watcherAndCallBack = new MyWatcherAndCallBack();
    //accept data
    MyConf myconf = new MyConf();

    @Before
    public void conn() throws IOException, InterruptedException {
        zooKeeper = Utils.getZooKeeper();
    }

    @After
    public void close() throws InterruptedException {
        zooKeeper.close();
    }

    @Test
    public void getConf() throws Exception {
        watcherAndCallBack.setZooKeeper(zooKeeper);
        watcherAndCallBack.setMyConf(myconf);
        watcherAndCallBack.aWait();
        while (true) {
            if (myconf.getConfData().equals("")) {
                System.out.println("empty data ~");
                watcherAndCallBack.aWait();
            }
            System.out.println(myconf.getConfData());
            TimeUnit.SECONDS.sleep(2);
        }

    }
}
