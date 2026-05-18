package org.example.pensionatapp.pensionat.customer.seeder;

import org.example.pensionatapp.pensionat.customer.model.Customer;
import org.example.pensionatapp.pensionat.customer.repository.CustomerRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(value = 2)
public class CustomerSeeder implements CommandLineRunner {

    private CustomerRepository customerRepository;

    public CustomerSeeder(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        if (customerRepository.count() == 0) {
            customerRepository.save(new Customer("Frodo", "Baggins", "frodo@theshire.me", "0761112233"));
            customerRepository.save(new Customer("Samwise", "Gamgee", "sam@theshire.me", "0762223344"));
            customerRepository.save(new Customer("Gandalf", "the Grey", "gandalf@istari.org", "0700000000"));
            customerRepository.save(new Customer("Aragorn", "Elessar", "strider@gondor.gov", "0721118888"));
            customerRepository.save(new Customer("Legolas", "Greenleaf", "legolas@mirkwood.net", "0734445555"));
            customerRepository.save(new Customer("Gimli", "Son of Glóin", "gimli@erebor.min", "0703333333"));
            customerRepository.save(new Customer("Boromir", "of Gondor", "boromir@gondor.gov", "0722229999"));
            customerRepository.save(new Customer("Smeagol", "Gollum", "my.precious@cave.com", "0706666666"));
            customerRepository.save(new Customer("Lady", "Galadriel", "galadriel@lothlorien.com", "0737777777"));
            customerRepository.save(new Customer("Lord", "Elrond", "elrond@rivendell.com", "0738888888"));
        }
    }




}
