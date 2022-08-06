package com.lgjy.zk_config_center;

import org.apache.zookeeper.ZooKeeper;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

public class Utils {
    private static ZooKeeper zooKeeper;
    private static String connectString = "hadoop102:2181,hadoop103:2181,hadoop104:2181";
    private static DefaultWatcher defaultWatcher = new DefaultWatcher();
    private static CountDownLatch latch = new CountDownLatch(1);

    public static ZooKeeper getZooKeeper() throws IOException, InterruptedException {
        zooKeeper = new ZooKeeper(connectString, 3000, defaultWatcher);
        defaultWatcher.setLatch(latch);
        latch.await();
        return zooKeeper;
    }
}
