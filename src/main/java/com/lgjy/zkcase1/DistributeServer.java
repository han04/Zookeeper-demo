package com.lgjy.zkcase1;

import org.apache.zookeeper.*;

import java.io.IOException;

public class DistributeServer {
    private  ZooKeeper zk;
    private static String connectString = "hadoop102:2181,hadoop103:2181,hadoop104:2181";
    private static int sessionTimeout = 2000;
    private String parentNode = "/servers";
    DistributeServer server = new DistributeServer();

    public static void main(String[] args) throws IOException, InterruptedException, KeeperException {
        DistributeServer server = new DistributeServer();

        //1. 获取zk连接
        server.getConnect();

        //2.注册服务器到zk集群
        server.register(args[0]);


        //3.启动业务逻辑
        server.business();
    }

    private void business() throws InterruptedException {
        Thread.sleep(Long.MAX_VALUE);
    }

    private void register(String hostname) throws InterruptedException, KeeperException {
        zk.create(parentNode,hostname.getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
        System.out.println(hostname + "is online ~ ");
    }

    private void getConnect() throws IOException {
        zk = new ZooKeeper(connectString,sessionTimeout,new Watcher(){
            @Override
            public void process(WatchedEvent watchedEvent) {

            }
        });
    }

}
