package com.github.derpynewbie.report.util;

import com.github.derpynewbie.report.Report;
import org.bukkit.Bukkit;

import java.util.function.Consumer;

public abstract class DerpyScheduler implements Runnable {

    private final Double seconds;
    private Consumer<DerpyScheduler> updateNotifier = null;
    private Integer taskId;
    private Double secondsLeft;

    public DerpyScheduler(Double inSeconds) {
        this.seconds = inSeconds;
        this.secondsLeft = this.seconds;
    }

    public DerpyScheduler(Double inSeconds, Consumer<DerpyScheduler> updateNotifier) {
        this(inSeconds);
        this.updateNotifier = updateNotifier;
    }

    public abstract void onRun();

    public void onStart() {

    }

    public void onEnd() {

    }

    public final void start() {
        this.taskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(Report.getInstance(), this, 0L, 2L);
    }

    public final void cancel() {
        Bukkit.getScheduler().cancelTask(taskId);
    }

    public final Double getSecondsLeft() {
        return this.secondsLeft;
    }

    public final Double getTotalSeconds() {
        return this.seconds;
    }

    public final Integer getTaskId() {
        return this.taskId;
    }

    @Override
    public final void run() {
        if (this.updateNotifier != null)
            this.updateNotifier.accept(this);
        if (secondsLeft.equals(seconds))
            this.onStart();
        if (secondsLeft <= 0) {
            this.onEnd();
            cancel();
            return;
        }
        this.onRun();
        this.secondsLeft -= 0.1;
    }
}