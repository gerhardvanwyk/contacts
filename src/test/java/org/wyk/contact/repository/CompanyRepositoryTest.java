package org.wyk.contact.repository;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit4.SpringRunner;


import java.util.Optional;
import java.util.UUID;

@RunWith(SpringRunner.class)
@DataJpaTest
public class CompanyRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private CompanyRepository companyRepository;

    @Test
    public void findById(){
        //given
        Company company = new Company();
        company.setName("Coca cola");
        company.setRegistrationNumber("x766t9");
        entityManager.persist(company);
        entityManager.flush();

        //When
        Optional<Company> optionalRest = companyRepository.findById(company.getId());

        //Then
        Assert.assertTrue("The company " + company.getId() + " should exists", optionalRest.isPresent());
        Assert.assertEquals( "x766t9", optionalRest.get().getRegistrationNumber() );
    }
}
