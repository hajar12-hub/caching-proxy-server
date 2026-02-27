package com.proxy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class CachingProxyServerApplication {

	public static void main(String[] args) {
		// Affiche tous les arguments pour vérifier qu'ils sont bien passés
		System.out.println("==== Arguments passés au programme ====");
		for (String arg : args) {
			System.out.println(arg);
		}
		System.out.println("=====================================");

		// Variables par défaut si arguments manquants
		int port = 8080;
		String origin = "http://localhost:3000";

		// Lecture des arguments passés
		for (int i = 0; i < args.length; i++) {
			if (args[i].equals("--port") && i + 1 < args.length) {
				port = Integer.parseInt(args[i + 1]);
			} else if (args[i].equals("--origin") && i + 1 < args.length) {
				origin = args[i + 1];
			}
		}

		// Affiche les valeurs utilisées
		System.out.println("\nCaching proxy server started !");
		System.out.println("Port    : " + port);
		System.out.println("Origin  : " + origin);
		System.out.println("Proxy running on port " + port);
		System.out.println("Forwarding to " + origin);

		// Démarre Spring Boot
		SpringApplication.run(CachingProxyServerApplication.class, args);
	}
}