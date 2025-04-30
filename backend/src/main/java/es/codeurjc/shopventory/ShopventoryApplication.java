package es.codeurjc.shopventory;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@ComponentScan
@SpringBootApplication
public class ShopventoryApplication {

	public static void main(String[] args) {
		SpringApplication.run(ShopventoryApplication.class, args);
	}

}
