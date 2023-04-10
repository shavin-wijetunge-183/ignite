package com.cache.service;

import com.cache.entity.Organization;
import com.cache.entity.OrganizationType;
import com.cache.entity.Person;
import com.cache.exception.ResourceNotFoundException;
import com.cache.repository.PersonRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.IgniteDataStreamer;
import org.apache.ignite.IgniteDataStreamerTimeoutException;
import org.apache.ignite.IgniteException;
import org.apache.ignite.IgniteInterruptedException;
import org.apache.ignite.lang.IgniteFuture;
import org.apache.ignite.stream.StreamTransformer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.cache.CacheException;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Service
@Slf4j
public class PersonService {

    /** */
    private static final AtomicLong ID_GEN = new AtomicLong();

    @Autowired
    private PersonRepository personRepository;

    @Autowired
    private Ignite igniteInstance;

    public Person addPerson(Person person){
        long id = new Random().nextLong();
        //We cannot use the Spring Crud repository
        return personRepository.save(id,person);
    }

    public Person updatePerson(Person person,long personId){
        return personRepository.findById(personId)
                .map(existingPerson ->{
                    existingPerson.setLastName(person.getLastName());
                    existingPerson.setFirstName(person.getFirstName());
                    existingPerson.setAge(person.getAge());
                    return personRepository.save(personId,existingPerson);
                }).orElseGet(()->{
                    long id = new Random().nextLong();
                    return personRepository.save(id,person);
                });
    }

    public Person getPerson(long id){
        return personRepository.findById(id).orElseThrow(()-> new ResourceNotFoundException("Person not found with id "+id));
    }

    public void deletePerson(Long id){
        personRepository.deleteById(id);
    }

    public List<Person> getPersons(String firstName, Integer age){
        if(firstName != null) {
            return personRepository.findByFirstNameLike(firstName);
        }else if(age != null){
            return personRepository.selectPersonsByAge(age);
        }else{
            List<Person> target = new ArrayList<>();
            personRepository.findAll().forEach(target::add);
            return target;
        }
    }

    public List<Person> getPersonsByOrganization(String organizationName){
        List<?> personList = personRepository.selectPersonByOrganization(organizationName);
        return personList.stream().map(p -> mapPerson(p)).collect(Collectors.toList());
    }

    private Person mapPerson(Object l) {
        System.out.println("PersonService.getPersonsByOrganization: {}");
        return personRepository.findById((Long) l).get();
    }


    /**
     * Fills the repository in with some sample data.
     */
    
    public  void populateRepository() {
    	
    	try {
			System.out.println("PersonService.java:91 > "+(igniteInstance!=null));

			IgniteCache<Long, Organization> orgCache = igniteInstance.cache("ORG_CACHE");
			System.out.println("PersonService.java:94 > "+(orgCache!=null));
			// Clear cache before running the example.
			orgCache.clear();
			
			// Organizations.
			//Organization org1 = Organization.builder().name("ApacheIgnite").type(OrganizationType.NON_PROFIT).id(ID_GEN.incrementAndGet()).build();
			//Organization org2 = Organization.builder().name("Other").type(OrganizationType.GOVERNMENT).id(ID_GEN.incrementAndGet()).build();
			System.out.println("PersonService.java:101 > "+(ID_GEN!=null));
			Organization org1 = new Organization();
			org1.setName("ApacheIgnite");
			org1.setType(OrganizationType.NON_PROFIT);
			org1.setId(ID_GEN.incrementAndGet());
			
			Organization org2 = new Organization();
			org2.setName("Other");
			org2.setType(OrganizationType.GOVERNMENT);
			org2.setId(ID_GEN.incrementAndGet());
			System.out.println("PersonService.java:111 > "+(org1.getId()));
			orgCache.put(org1.getId(), org1);
			orgCache.put(org2.getId(), org2);

			Map<Long, Person> persons = new TreeMap<>();
			System.out.println("PersonService.java:116");
			long offset =(long) Math.random();
			persons.put(1L+offset, Person.builder().id(1L).firstName("John").lastName("Smith").age(30).orgId(org1.getId()).build());
			persons.put(2L+offset, Person.builder().id(2L).firstName("Brad").lastName("Pitt").age(25).orgId(org1.getId()).build());
			persons.put(3L+offset, Person.builder().id(3L).firstName("Mark").lastName("Tomson").age(32).orgId(org1.getId()).build());
			persons.put(4L+offset,Person.builder().id(4L).firstName("Erick").lastName("Smith").age(22).orgId(org2.getId()).build());
			persons.put(5L+offset, Person.builder().id(5L).firstName("John").lastName("Rozenberg").age(35).orgId(org2.getId()).build());
			persons.put(6L+offset, Person.builder().id(6L).firstName("Denis").lastName("Won").age(22).orgId(org1.getId()).build());
			persons.put(7L+offset, Person.builder().id(7L).firstName("Abdula").lastName("Adis").age(40).orgId(org1.getId()).build());
			persons.put(8L+offset, Person.builder().id(8L).firstName("Roman").lastName("Ive").age(27).orgId(org1.getId()).build());

			System.out.println("PersonService.java:127");
			//IgniteCache<Long, Person> personCache = igniteInstance.cache("PersonCache");
			/**
			 * Using IgniteDataStreamer to populate the Cache. IgniteDataStreamer should be used for bulk operation
			 */
			// Create a streamer to stream words into the cache.
			//https://www.javadoc.io/doc/org.apache.ignite/ignite-core/2.5.0/org/apache/ignite/IgniteDataStreamer.html
			try (IgniteDataStreamer<Long, Person> stmr = igniteInstance.dataStreamer("PersonCache")) {
			    // Allow data updates.
			    stmr.allowOverwrite(true);
			    
//			    // Configure data transformation to count random numbers added to the stream.
			    // Who would do this? It just does nothing.... completely screwed me....  this is a prank...
//			    stmr.receiver(StreamTransformer.from((e, arg) -> {
//			        // Get current count.
//			        Person person = e.getValue();
//			        System.out.println("Existing Value ::: " + person);
//
//			        System.out.println("Key Present in Cache " + e.getKey());
//
//			        return null;
//			    }));
			    
			    IgniteFuture<?> future =  stmr.addData(persons);
			    stmr.flush();
			    future.get();
			    System.out.println("PersonService.java:152");
			}
			System.out.println("\n>>> Added " + personRepository.count() + " Persons into the repository.");
		} catch (IgniteDataStreamerTimeoutException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IgniteInterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (CacheException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IgniteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch(Exception ex) {
			ex.printStackTrace();
		}
    }
}
