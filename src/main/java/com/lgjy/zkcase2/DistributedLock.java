package com.lgjy.zkcase2;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public class DistributedLock {

   private final String connectString = "hadoop102:2181,hadoop103:2181,hadoop104:2181";
   private final int sessionTime = 2000;
   private final ZooKeeper zk;
   private CountDownLatch connectLatch = new CountDownLatch(1);
   private CountDownLatch waitLatch = new CountDownLatch(1);
   private String waitPath;
   private String currentNode;

   public DistributedLock() throws IOException, InterruptedException, KeeperException {
      //获取连接
      zk = new ZooKeeper(connectString, sessionTime, new Watcher() {
         @Override
         public void process(WatchedEvent watchedEvent) {
            //connectLatch 如果连接上zk 可以释放
            if (watchedEvent.getState() == Event.KeeperState.SyncConnected) {
               connectLatch.countDown();
            }
            //waitLatch 需要等待释放
            if (watchedEvent.getType() == Event.EventType.NodeDeleted && watchedEvent.getType().equals(waitPath)) {
               waitLatch.countDown();
            }
         }
      });

      //等待连接成功，再继续
      connectLatch.await();

      //判断根节点是否存在
      Stat stat = zk.exists("/locks", false);

      //如果不存在，创建
      if (stat == null) {
         zk.create("/locks", "/locks".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
      }

      //判断根节点 /locks是否存在
   }

   //对zk加锁
   public void zkLocks() {
      // 创建临时带序号节点
      try {
         currentNode = zk.create("/locks/" + "seq-", null, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
         // 判断创建的节点是否是最小序号节点
         // 是，获取到锁
         // 否，监听前一序号节点
         List<String> children = zk.getChildren("/locks", false);

         //如果children 只有一个值，直接获取锁； 如果有多个，判断大小
         if (children.size() == 1) {
            return;
         } else {
            Collections.sort(children);

            //获取节点名称
            String thisNode = currentNode.substring("/locks/".length());

            //获取该节点在children 集合中的位置
            int index = children.indexOf(thisNode);
            if (index == -1) {
               System.out.println("数据异常");
            } else if (index == 0) {
               return;
            } else {
               //监听前一个节点的变化
               waitPath = "/locks/" + children.get(index - 1);
               zk.getData(waitPath, false, null);

               //等待监听
               waitLatch.await();
            }
         }
      } catch (KeeperException e) {
         throw new RuntimeException(e);
      } catch (InterruptedException e) {
         throw new RuntimeException(e);
      }

   }

   //解锁
   public void zkUnlock() {
      // 删除节点
      try {
         zk.delete(this.currentNode, -1);
      } catch (InterruptedException | KeeperException e) {
         e.printStackTrace();
      }
   }
}
