package com.proxy.cli;

import com.proxy.cache.CacheManager;
import com.proxy.config.AppConfig;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class CLIHandler implements CommandLineRunner {

    private final AppConfig appConfig;
    private final CacheManager cacheManager;

    public CLIHandler(AppConfig appConfig, CacheManager cacheManager) {
        this.appConfig = appConfig;
        this.cacheManager = cacheManager;
    }

    @Override
    public void run(String... args) throws Exception {

        // Pas d'arguments → afficher l'aide
        if (args.length == 0) {
            printHelp();
            return;
        }

        // --clear-cache
        if (contains(args, "--clear-cache")) {
            cacheManager.clear();
            System.out.println("Cache cleared successfully !");
            return;
        }

        // Parser --port et --origin
        String port = null;
        String origin = null;

        for (int i = 0; i < args.length; i++) {
            if (args[i].equals("--port") && i + 1 < args.length) {
                port = args[i + 1];
            }
            if (args[i].equals("--origin") && i + 1 < args.length) {
                origin = args[i + 1];
            }
        }

        // Vérifier que --port et --origin sont présents
        if (port == null || origin == null) {
            System.out.println("Error: --port and --origin are required !");
            printHelp();
            return;
        }

        // Configurer AppConfig
        appConfig.setPort(Integer.parseInt(port));
        appConfig.setOriginUrl(origin);

        System.out.println("Caching proxy server started !");
        System.out.println("Port   : " + port);
        System.out.println("Origin : " + origin);
    }

    // Vérifier si un argument existe
    private boolean contains(String[] args, String arg) {
        for (String a : args) {
            if (a.equals(arg)) return true;
        }
        return false;
    }

    // Afficher l'aide
    private void printHelp() {
        System.out.println("Usage:");
        System.out.println("  caching-proxy --port <number> --origin <url>");
        System.out.println("  caching-proxy --clear-cache");
    }
}