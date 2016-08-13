package main;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;

import static java.util.concurrent.TimeUnit.MINUTES;

/**
 * Created by Stijn on 13/08/2016.
 */
public class AutoSaveControl {
    private final ScheduledExecutorService scheduler;
    private ScheduledFuture saveHandle;

    public AutoSaveControl() {
        scheduler = Executors.newScheduledThreadPool(1);
        ScheduledThreadPoolExecutor executor = (ScheduledThreadPoolExecutor) scheduler;
        executor.setRemoveOnCancelPolicy(true);
    }

    public void enable() {
        if (saveHandle == null) {
            final Runnable save = new Runnable() {
                @Override
                public void run() {
                    // perform save
                }
            };
            saveHandle = scheduler.scheduleAtFixedRate(save, 15, 15, MINUTES);
        } else {
            System.err.println("AutoSave enable called while it is already active.");
        }
    }

    public void disable() {
        if (!(saveHandle == null)) {
            saveHandle.cancel(false);
            saveHandle = null;
        } else {
            System.err.println("AutoSave disable called while it is already deactivated.");
        }
    }

}
