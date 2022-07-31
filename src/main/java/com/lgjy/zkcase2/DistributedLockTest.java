package com.lgjy.zkcase2;

import org.apache.zookeeper.KeeperException;

import java.io.IOException;

public class DistributedLockTest {
   public static void main(String[] args) throws IOException, InterruptedException, KeeperException {
      final DistributedLock lock1 = new DistributedLock();
      final DistributedLock lock2 = new DistributedLock();
      final DistributedLock lock3 = new DistributedLock();


      new Thread(new Runnable() {
         @Override
         public void run() {
            try {
               lock1.zkLocks();
               System.out.println("线程1启动！获取到lock~");
               Thread.sleep(5 * 1000);

               lock1.zkUnlock();
               System.out.println("线程1 释放锁");
            }catch (InterruptedException e){
               e.printStackTrace();
            }
         }
      }).start();

      new Thread(new Runnable() {
         @Override
         public void run() {
            try {
               lock2.zkLocks();
               System.out.println("线程2启动！获取到lock~");
               Thread.sleep(5 * 1000);

               lock2.zkUnlock();
               System.out.println("线程2 释放锁");
            }catch (InterruptedException e){
               e.printStackTrace();
            }
         }
      }).start();


      new Thread(new Runnable() {
         @Override
         public void run() {
            try {
               lock3.zkLocks();
               System.out.println("线程3启动！获取到lock~");
               Thread.sleep(5 * 1000);

               lock3.zkUnlock();
               System.out.println("线程3 释放锁");
            }catch (InterruptedException e){
               e.printStackTrace();
            }
         }
      }).start();

   }
}
