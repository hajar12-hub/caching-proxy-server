package com.proxy;


import com.proxy.config.ProxyArgs;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class ProxyRunner implements CommandLineRunner {

    private final ProxyArgs proxyArgs;

    public ProxyRunner(ProxyArgs proxyArgs) {
        this.proxyArgs = proxyArgs;
    }

    @Override
    public void run(String... args) {

        if (proxyArgs.getPort() == null || proxyArgs.getOrigin() == null) {
            System.out.println("Error: --port and --origin are required !");
            return;
        }

        System.out.println("Proxy running on port " + proxyArgs.getPort());
        System.out.println("Forwarding to " + proxyArgs.getOrigin());
    }
}

