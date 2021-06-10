package de.blu.reportsystem.rest;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import de.blu.reportsystem.rest.listener.*;
import spark.Spark;

@Singleton
public final class RESTInitializer {

    @Inject private Injector injector;

    public void init(){
        Spark.port(8080);

        this.injector.getInstance(ListReports.class).setup();
        this.injector.getInstance(ShowReportsBy.class).setup();
        this.injector.getInstance(InsertReport.class).setup();
        this.injector.getInstance(UpdateReport.class).setup();
        this.injector.getInstance(DeleteReportById.class).setup();

        Spark.awaitInitialization();
    }
}
