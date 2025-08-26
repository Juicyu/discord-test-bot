package weeklyReminder;

import org.javacord.api.DiscordApi;

import java.io.*;
import java.time.*;
import java.time.temporal.TemporalAdjusters;
import java.util.concurrent.*;

public class WeeklyReminder {

    private static final String END_DATE_FILE = "scheduler_enddate.txt";

    /**
     * Startet einen wöchentlichen Reminder, Montag 10:00 Uhr, für die nächsten 4 Wochen.
     *
     * @param api       Dein JDA-Client
     * @param channelId Die ID des Discord-Channels, in dem die Nachricht gepostet werden soll
     */
    public static void startWeeklyReminder(DiscordApi api, String channelId) {
        LocalDateTime endDate = null;
        try {
            endDate = loadOrCreateEndDate();
            WeeklyScheduler scheduler = new WeeklyScheduler(endDate);
            api.getTextChannelById("1150017992697065492").ifPresent(channel -> {
                channel.sendMessage("Next run: " + ZonedDateTime.ofInstant(Instant.ofEpochMilli(System.currentTimeMillis() + scheduler.computeNextDelay()), ZoneId.systemDefault()));
            });
            scheduler.schedule(() -> {
                api.getTextChannelById(channelId)
                        .ifPresent(channel -> {
                            channel.sendMessage("📢 Hallo liebe Gildies," +
                                    "\nder wöchentliche M+-Aufruf ist hier." + "\nBitte schreibt hier im Channel, solltet ihr noch einen +10 Key für die Weekly brauchen, damit sich fleißige Helfer für euch finden lasen können." +
                                    "\nNur keine Scheu, hopp hopp :)").exceptionally(ex -> {
                                api.getTextChannelById("1150017992697065492").ifPresent(log -> log.sendMessage(ex.getMessage()));
                                return null;
                            });
                        });
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
        LocalDateTime finalEndDate = endDate;
        api.getTextChannelById("1150017992697065492").ifPresent(channel -> {
            assert finalEndDate != null;
            channel.sendMessage("End of transmission: " + finalEndDate);
        });
    }

    // Lädt das Enddatum aus Datei oder erstellt es (jetzt + 4 Wochen)
    private static LocalDateTime loadOrCreateEndDate() throws IOException {
        File file = new File(END_DATE_FILE);
        if (file.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                return LocalDateTime.parse(reader.readLine());
            }
        } else {
            LocalDateTime endDate = LocalDateTime.now().plusWeeks(4);
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                writer.write(endDate.toString());
            }
            return endDate;
        }
    }

    // Interner Scheduler
    private static class WeeklyScheduler {
        private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
        private final LocalDateTime endDate;

        public WeeklyScheduler(LocalDateTime endDate) {
            this.endDate = endDate;
        }

        public void schedule(Runnable task) {
            long delay = computeNextDelay();
            if (delay < 0) {
                scheduler.shutdown();
                return;
            }

            scheduler.schedule(() -> {
                try {
                    task.run();
                } finally {
                    schedule(task); // nächsten Termin planen
                }
            }, delay, TimeUnit.MILLISECONDS);
        }

        private long computeNextDelay() {
            ZonedDateTime now = ZonedDateTime.now();
            ZonedDateTime nextRun = now.with(TemporalAdjusters.nextOrSame(DayOfWeek.TUESDAY))
                    .withHour(11).withMinute(18).withSecond(0).withNano(0);

            if (now.compareTo(nextRun) >= 0) {
                nextRun = nextRun.plusWeeks(1);
            }

            if (nextRun.toLocalDateTime().isAfter(endDate)) {
                return -1; // Scheduler beenden
            }

            return Duration.between(now, nextRun).toMillis();
        }
    }
}