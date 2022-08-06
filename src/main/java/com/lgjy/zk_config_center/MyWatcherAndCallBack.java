package com.lgjy.zk_config_center;

import org.apache.zookeeper.AsyncCallback;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

import java.util.concurrent.CountDownLatch;

public class MyWatcherAndCallBack implements Watcher, AsyncCallback.StatCallback,AsyncCallback.DataCallback {
    private ZooKeeper zooKeeper;
    private myConf myconf;
    private CountDownLatch latch = new CountDownLatch(1);

    public void setMyConf(myConf myconf){
        this.myconf = myconf;
    }

    public void setZooKeeper(){
        this.zooKeeper = zooKeeper;
    }

    //DataCallback
    @Override
    public void processResult(int i, String s, Object o, byte[] bytes, Stat stat) {
        if(stat != null){
            myconf.setConfData(new String(bytes));
            latch.countDown();
        }
    }

    // SataCallBack
    @Override
    public void processResult(int i, String s, Object o, Stat stat) {
        // if exists ,get data
        if(stat != null){
            zooKeeper.getData("/AppConf",this,this,"aaa");
        }
    }

    // Watcher
    @Override
    public void process(WatchedEvent watchedEvent) {
        switch (watchedEvent.getType()){
            case None:
            case DataWatchRemoved:
            case ChildWatchRemoved:
            case NodeChildrenChanged:
                break;
            case NodeCreated:
            case NodeDataChanged:
                zooKeeper.getData("/AppConf",this,this,"bbb");
                break;
            case NodeDeleted:
                myconf.setConfData("");
                latch = new CountDownLatch(1);
                break;
        }
    }

    public void aWait() throws InterruptedException {
        zooKeeper.exists("/AppConf",this,this,"123");
        latch.await();
    }
}
