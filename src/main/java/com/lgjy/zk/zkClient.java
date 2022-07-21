package com.lgjy.zk;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

public class zkClient {
    //逗号前后不能有空格
    private String connectString = "hadoop102:2181,hadoop103:2181,hadoop104:2181";
    private int sessionTimeout = 2000;
    private ZooKeeper zkClient;

    @Before
    public void init() throws IOException {
        zkClient = new ZooKeeper(connectString, sessionTimeout, new Watcher() {
            @Override
            public void process(WatchedEvent watchedEvent) {
                List<String> children = null;
                try {
                    children = zkClient.getChildren("/", true);
                    //重复注册 Watcher

                    for (String child : children) {
                        System.out.println(child);
                    }
            } catch(KeeperException e)
            {
                e.printStackTrace();
            } catch(InterruptedException e)
            {
                e.printStackTrace();
            }
            }
        });
    }

    @Test
    //创建子节点
    public void create() throws Exception{
        String nodeCreated = zkClient.create("/lgjy", "zzh".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
    }

    //获取子节点 并 监听节点变化
    @Test
    public void getChildren() throws InterruptedException, KeeperException {
        List<String> children = zkClient.getChildren("/", true);

        //延时阻塞
        Thread.sleep(Long.MAX_VALUE);
    }

    @Test
    public void exists() throws InterruptedException, KeeperException {
        Stat stat = zkClient.exists("/lgjy8", false);
        System.out.println(stat == null ? "not exist" : "exist");
    }

}
