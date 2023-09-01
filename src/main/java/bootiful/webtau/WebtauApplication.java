package bootiful.webtau;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.annotation.Id;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Collection;

@SpringBootApplication
public class WebtauApplication {

    public static void main(String[] args) {
        SpringApplication.run(WebtauApplication.class, args);
    }

}

@Controller
@ResponseBody
class CustomerHttpController {

    private final CustomerRepository customerRepository;

    CustomerHttpController(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @GetMapping("/customers")
    Collection<Customer> customers() {
        return this.customerRepository.findAll();
    }
}

interface CustomerRepository extends ListCrudRepository<Customer, Integer> {
}

record Customer(@Id Integer id, String name) {
}