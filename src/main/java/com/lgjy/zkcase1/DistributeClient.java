package com.lgjy.zkcase1;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class DistributeClient {
    private static String connectString = "hadoop102:2181,hadoop103:2181,hadoop104:2181";
    private int sessionTimeout = 2000;
    private ZooKeeper zk;
    private String parentNode = "/servers";

    public static void main(String[] args) throws Exception {
        DistributeClient client = new DistributeClient();
        //1.获取连接
        client.getConnect();
        //2.监听 /servers 下子节点增加和删除
        client.getServerList();
        //3.业务逻辑
        client.business();

    }

    private void business() throws InterruptedException {
        System.out.println("client is working~");
        Thread.sleep(Long.MAX_VALUE);
    }

    private void getServerList() throws Exception {
        List<String> children = zk.getChildren(parentNode, true);

        ArrayList<String> servers = new ArrayList<>();

        for (String child : children) {
            byte[] data = zk.getData(parentNode +"/"+child, false, null);

            servers.add(new String(data));
        }

        //打印
        System.out.println(servers);
    }

    private void getConnect() throws IOException {
         zk = new ZooKeeper(connectString, sessionTimeout, new Watcher() {
            @Override
            public void process(WatchedEvent watchedEvent) {
                try {
                    getServerList();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

    }

}
