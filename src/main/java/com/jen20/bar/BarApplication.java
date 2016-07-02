package com.jen20.bar;

import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.jen20.bar.health.TemplateHealthCheck;
import com.jen20.bar.resources.BarResource;
import com.orbitz.consul.Consul;
import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

public class BarApplication extends Application<BarConfiguration> {
    public static void main(String[] args) throws Exception {
        new BarApplication().run(args);
    }

    @Override
    public String getName() {
        return "bar-service";
    }

    @Override
    public void initialize(Bootstrap<BarConfiguration> bootstrap) {
        super.initialize(bootstrap);
        bootstrap.getObjectMapper().registerModule(new Jdk8Module());
    }

    @Override
    public void run(BarConfiguration configuration, Environment environment) {
        final BarResource resource = new BarResource(
                configuration.getTemplate(),
                configuration.getDefaultName()
        );
        environment.jersey().register(resource);

        final TemplateHealthCheck healthCheck = new TemplateHealthCheck(
                configuration.getTemplate()
        );
        environment.healthChecks().register("template", healthCheck);

        configuration.getConsul().buildAndRegisterServices(environment);
    }
}
