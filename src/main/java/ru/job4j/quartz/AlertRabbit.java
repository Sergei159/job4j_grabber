package ru.job4j.quartz;

import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import static java.lang.System.currentTimeMillis;
import static org.quartz.JobBuilder.*;
import static org.quartz.TriggerBuilder.*;
import static org.quartz.SimpleScheduleBuilder.*;

/**
 * Класс повторяет определенную операцию с интервалом времени,
 * который задается в файле rabbit.properties
 */
public class AlertRabbit {

    private static int rabbitInterval;
    private static Connection connection;

    /**
     * метод открывает файл properties.
     * достает оттуда интервал повторения операций и данные для подключения БД
     */
    public void init() {
        try (InputStream in = AlertRabbit.class.getClassLoader().getResourceAsStream("rabbit.properties")) {
            Properties config = new Properties();
            config.load(in);
            rabbitInterval = Integer.parseInt(config.getProperty("rabbit.interval"));
            Class.forName(config.getProperty("driver-class-name"));
            connection = DriverManager.getConnection(
                    config.getProperty("url"),
                    config.getProperty("username"),
                    config.getProperty("password")
            );
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    public static void main(String[] args) {
        AlertRabbit alertRabbit = new AlertRabbit();
        alertRabbit.init();
        try {
            Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
            scheduler.start();
            JobDataMap data = new JobDataMap();
            data.put("connection", connection);
            JobDetail job = newJob(Rabbit.class)
                    .usingJobData(data)
                    .build();
            SimpleScheduleBuilder times = simpleSchedule()
                    .withIntervalInSeconds(rabbitInterval)
                    .repeatForever();
            Trigger trigger = newTrigger()
                    .startNow()
                    .withSchedule(times)
                    .build();
            scheduler.scheduleJob(job, trigger);
            Thread.sleep(10000);
            scheduler.shutdown();
        } catch (Exception se) {
            se.printStackTrace();
        }
    }

    public static class Rabbit implements Job {

        public Rabbit() {
            System.out.println(hashCode());
        }

        /**
         * Метод достает Connection из JobDataMap и воспроизводит с ним sql скрипт
         * @param context
         * @throws JobExecutionException
         */
        @Override
        public void execute(JobExecutionContext context) throws JobExecutionException {
            System.out.println("Rabbit runs here ...");
            try (Connection cn = (Connection) context.getJobDetail().getJobDataMap().get("connection")) {
                try (PreparedStatement preparedStatement = cn.prepareStatement(
                                             "insert into rabbit(created_date) values(?);")) {
                    preparedStatement.setLong(1, currentTimeMillis());
                    preparedStatement.execute();
                }
            } catch (SQLException exception) {
                exception.printStackTrace();
            }
        }
    }
}
