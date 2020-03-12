package org.wyk.contact.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.wyk.contact.dto.CompanyDto;
import org.wyk.contact.dto.CustomerDto;
import org.wyk.contact.dto.MessageDto;
import org.wyk.contact.exception.CompanyException;
import org.wyk.contact.exception.InvalidIdException;
import org.wyk.contact.repository.*;

import java.util.Optional;
import java.util.UUID;

/**
 * I opted for no Service layer since there is no business logic
 * The controller use the repository layer, which already holds the relationships amongst the entities
 * I only implemented Customer and Company since the others duplicate a similar scenario
 */
@Controller
public class MainController {

    private final Logger log = LoggerFactory.getLogger(MainController.class);

    private final CompanyRepository companyRepository;

    private final CustomerRepository customerRepository;

    private final PersonRepository personRepository;

    private final SupplierRepository supplierRepository;

    public MainController(CompanyRepository companyRepository, CustomerRepository customerRepository, PersonRepository personRepository, SupplierRepository supplierRepository) {
        this.companyRepository = companyRepository;
        this.customerRepository = customerRepository;
        this.personRepository = personRepository;
        this.supplierRepository = supplierRepository;
    }

    @GetMapping("/customer")
    public ResponseEntity<MessageDto> getCustomerById(@PathVariable String id){
        ResponseEntity<MessageDto> error = isUUIDValid(id, "customer");
        if(error != null) {
            return error;
        }
        Optional<Customer> custOption = customerRepository.findById(id);
       if(custOption.isPresent()) {

           Customer customer = custOption.get();

           if(customer.getPersonId() != null) {
                Optional<Person> person = personRepository.findById(customer.getPersonId());
                //We assume the id valid, creating the entity should to the validation
               return new ResponseEntity<>(new CustomerDto(customer, person.get()), HttpStatus.OK);
           }else{
               Optional<Company> company = companyRepository.findById(customer.getCompanyId());
               return new ResponseEntity<>(new CustomerDto(customer, company.get()), HttpStatus.OK);
           }
       }
        return new ResponseEntity<>(new MessageDto("Could not find Customer with UUID " + id), HttpStatus.NOT_FOUND);
    }
    @PostMapping("/customer")
    public ResponseEntity<CustomerDto> createCompany( @RequestBody CustomerDto customer ){

        Customer customerEntity = new Customer(customer);
        Customer result;

        result = customerRepository.save(customerEntity);

        return new ResponseEntity<>(new CustomerDto(result), HttpStatus.OK);
    }

    @GetMapping("/company")
    public ResponseEntity<CompanyDto> getCompanyById(@PathVariable String id){
        Optional<Company> company;
        try {
            company = companyRepository.findById(id);
        }catch (IllegalArgumentException ex){
            return new ResponseEntity<>(new CompanyDto("UUID for the company is not valid " + id), HttpStatus.BAD_REQUEST);
        }

        if(company.isPresent()) {
            try {
                String custId = company.get().getCustomerId();
                if(custId != null) {
                    Optional<Customer> customer = customerRepository.findById(custId);
                }
            }catch (IllegalArgumentException ex){
                return new ResponseEntity<>(new CompanyDto("UUID for customer is not valid"), HttpStatus.BAD_REQUEST);
            }
            return new ResponseEntity<>(new CompanyDto("Error not implemented"), HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    /**
     * Company get Created.
     * New company in request body (JSON). If the customerId or SupplierId is given we assume it is a exiting (customer or supplier), if
     * the (customer or supplier) is part request it is created as a new (customer or supplier)
     */
    @PostMapping("/company")
    public ResponseEntity<CompanyDto> createCompany(@RequestParam(required = false) String customerId, @RequestParam(required = false) String supplierId, @RequestBody CompanyDto company ){

        if(customerId != null){
            //This customer must not be a company
           Optional<Customer> customer = customerRepository.findById(customerId);
           if(customer.get().getCompanyId() != null)
               return new ResponseEntity(new CompanyDto("Customer supplied is also a company"), HttpStatus.CONFLICT );
        }

        if(company.isCustomer()){

            Customer customer = customerRepository.save(new Customer(company.getCustomer()));
            customerId = customer.getId();
        }

        if(company.isSupplier()){

            Supplier supplier = supplierRepository.save(new Supplier(company.getSupplier()));
            supplierId = supplier.getId();
        }

        Company companyEntity = new Company(company, customerId, supplierId);
        Company result;
        try {
            result = companyRepository.save(companyEntity);
            result.setCustomerId(customerId);
            result.setSupplierId(supplierId);
        }catch (CompanyException ex){
            return new ResponseEntity<>(new CompanyDto("Failed to create company"), HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(new CompanyDto(result), HttpStatus.OK);
    }

    @ExceptionHandler(InvalidIdException.class)
    public ResponseEntity<MessageDto> handleNotFound(InvalidIdException e){
        log.error("Could not find entity", e);
        return new ResponseEntity<>(new MessageDto(e.getMessage()), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(Throwable.class)
    public ResponseEntity<String> handleThrowable(Throwable e){
        log.error("Application error",e);
        return new ResponseEntity<>("Server Error occurred", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private ResponseEntity<MessageDto> isUUIDValid(String uuid, String type){
        try {
            UUID.fromString(uuid);
        }catch (IllegalArgumentException ex){
            return new ResponseEntity<>(new MessageDto("UUID for " + type + " is not valid"), HttpStatus.BAD_REQUEST);
        }
        return  null;
    }

}
