package com.RWL;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by parans on 12/5/16.
 */
public class ReaderWriterLock {
    private ReentrantLock waitMutex = new ReentrantLock();
    private Condition readersPresent = waitMutex.newCondition();
    private Condition writersPresnt = waitMutex.newCondition();

    private ReentrantLock criticalSectionMutex = new ReentrantLock();

    //This DS is read by readers and modified by writers
    private Integer writerCount = new Integer(0);
    //This DS is modified by readers and read by writers
    private Integer readerCount = new Integer(0);

    public void readLock() {
        waitMutex.lock();
        //System.out.println("Reader:Inside readerWaitMutex CS");
        while (writerCount.intValue() > 0) {
            try {
                //System.out.println("Reader:Writer present gonna block now");
                writersPresnt.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        readerCount++;
        waitMutex.unlock();
    }

    public void writeLock() {
        waitMutex.lock();
        //System.out.println("Writer: Inside writerWaitMutex CS");
        while(readerCount > 0) {
            try {
                //System.out.println("Writer:readers present gonna block now");
                readersPresent.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        writerCount++;
        waitMutex.unlock();
        //System.out.println("Writer:No more readers CS");
        criticalSectionMutex.lock();
    }

    public void writeUnlock(){
        criticalSectionMutex.unlock();
        //This will notify all the readers waiting on this condition
        //System.out.println("Writer:Notifying waiting readers");
        waitMutex.lock();
        writerCount--;
        if(writerCount == 0) {
            writersPresnt.signalAll();
        }
        waitMutex.unlock();
    }

    public void readUnlock() {
        waitMutex.lock();
        readerCount--;
        if(readerCount == 0) {
            readersPresent.signalAll();
        }
        waitMutex.unlock();
    }
}
