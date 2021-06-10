package de.blu.reportsystem;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import de.blu.reportsystem.command.ReportCommand;
import de.blu.reportsystem.command.ReportsCommand;
import de.blu.reportsystem.config.MainConfig;
import de.blu.reportsystem.util.ReportWebExecutor;
import org.bukkit.plugin.java.JavaPlugin;

import javax.inject.Singleton;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Singleton
public final class ReportPlugin extends JavaPlugin {

  @Override
  public void onEnable() {
    Injector injector =
        Guice.createInjector(
            new AbstractModule() {
              @Override
              protected void configure() {
                bind(JavaPlugin.class).toInstance(ReportPlugin.this);
                bind(ReportWebExecutor.class).toInstance(new ReportWebExecutor());
                bind(ExecutorService.class).toInstance(Executors.newCachedThreadPool());
              }
            });

    injector.injectMembers(this);
    this.init(injector);
  }

  private void init(Injector injector) {
    injector.getInstance(MainConfig.class).init();

    // Register Commands
    Objects.requireNonNull(this.getCommand("report"))
        .setExecutor(injector.getInstance(ReportCommand.class));
    Objects.requireNonNull(this.getCommand("reports"))
        .setExecutor(injector.getInstance(ReportsCommand.class));
  }
}
