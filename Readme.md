# Contacts

The circular reference is looked at in the Controller, not in the DB.
Although circular reference is possible, the application should warn the user.

## Design
I opted for no Service layer since there is no business logic 
The controller use the repository layer, which already holds the relationships amongst the entities
I only implemented Customer and Company since the others duplicate a similar scenario

## Run the App
Open in a IDE then run main in the Application class.
You need a MySQL database

## Test
There are 3 test in the MainControllerTest

1. Create a company as a supplier. New Supplier
    - Simply create a company as a Supplier, and the supplier information is new.
    
2. Create a company as a customer. Circular fails
 - Create company x as a supplier (PASS)
 - Create customer y as company x (PASS)
 - Create company z as customer y (FAIL - company z cannot be company x)
 
3. Create a company as a customer. Existing
  - Create person x as a supplier (PASS)
  - Create customer y as person x (Not implemented)
  - Create company z as person y (Not implemented)
  

The is 1 test in the CompanyRepositoryTest
 - findbyId 
