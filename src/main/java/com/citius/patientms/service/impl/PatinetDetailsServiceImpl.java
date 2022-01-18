package com.citius.patientms.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.citius.patientms.entities.Allergy;
import com.citius.patientms.entities.Demographic;
import com.citius.patientms.entities.PatientDetails;
import com.citius.patientms.models.AllergyDto;
import com.citius.patientms.models.DemographicDto;
import com.citius.patientms.models.PatientDetailsDto;
import com.citius.patientms.models.UserDto;
import com.citius.patientms.repositories.PatientDetailsRepository;
import com.citius.patientms.service.AllergyService;
import com.citius.patientms.service.DemographicService;
import com.citius.patientms.service.PatientDetailsService;



@Service
public class PatinetDetailsServiceImpl implements PatientDetailsService{
	@Autowired
	private RestTemplate  restTemplate;
	@Autowired
	private PatientDetailsRepository repo;
	@Autowired
	private DemographicService demographicService;
	
	@Autowired
	private AllergyService allergyService;
	
	@Override
	public PatientDetails savePatientDetails(PatientDetailsDto dto) {
		
		return convertDtoToEntity(dto);
	}
	@Override
	public List<PatientDetailsDto> getAllPatientDetails() {
		
		return convertEntityToDtoList();
	}
	@Override
	public PatientDetailsDto getPatientDetailsByUserId(long userId) {
		
		List<PatientDetails> findAll = repo.findAll();
		PatientDetailsDto dto=new PatientDetailsDto();
		System.out.println("from service"+userId);
		for(PatientDetails p:findAll) {
			if(p.getEmployeeId()==userId) {
				return convertEntityToDto(p);
				
			}
		}
		return null;
		
	}
	@Override
	public List<PatientDetails> getAll() {
	
		return repo.findAll();
	}
	@Override
	public PatientDetailsDto getById(long id) {
		return convertEntityToDto(repo.findById(id).get());
		
	}
	
	PatientDetails convertDtoToEntity(PatientDetailsDto dto){
		PatientDetails patientDetails=new PatientDetails();
		DemographicDto demographicDto=new DemographicDto();
		AllergyDto allergyDto=new AllergyDto();
		Demographic demographic = demographicService.saveDemographic(demographicDto.dtoTOEntity(dto.getDemographic()));
		List<Allergy> saveAllAllergy = allergyService.saveAllAllergy(allergyDto.dtoTOEntityList(dto.getAllergies()));
		patientDetails.setEmployeeId(dto.getUser().getId());
		patientDetails.setDemographic(demographic);
		patientDetails.setAllergies(saveAllAllergy);
		return repo.save(patientDetails);
		
	}
	PatientDetailsDto convertEntityToDto(PatientDetails e) {
		PatientDetailsDto patientDetails=new PatientDetailsDto();
		DemographicDto demographicDto=new DemographicDto();
		AllergyDto allergyDto=new AllergyDto();
		patientDetails.setDemographic(demographicDto.entityToDto(e.getDemographic()));
		patientDetails.setAllergies(allergyDto.entityToDtoList(e.getAllergies()));
		patientDetails.setUser(getUserDtoFromUserMs(e.getEmployeeId()));
		patientDetails.setId(e.getPatientDetails());
		return patientDetails;
	}
	List<PatientDetailsDto> convertEntityToDtoList() {
		List<PatientDetailsDto> dtoList=new ArrayList<>();
		List<PatientDetails> entityList=repo.findAll();
		for(PatientDetails e:entityList) {
			dtoList.add( convertEntityToDto(e));
		}
		return dtoList;
	}
	
	public UserDto getUserDtoFromUserMs(long id) {
		
		ResponseEntity<UserDto> response = restTemplate.getForEntity("http://localhost:8081/users/users/"+id,
				UserDto.class);
		return response.getBody();
		
	}
	@Override
	public PatientDetails getPatientDetailsById(long id) {
		return repo.findById(id).get();
	}
	@Override
	public PatientDetails savePatientDetails(PatientDetails details) {
		Demographic demographic = demographicService.saveDemographic(details.getDemographic());
		List<Allergy> saveAllAllergy = allergyService.saveAllAllergy(details.getAllergies());
		PatientDetails p=new PatientDetails();
		p.setDemographic(demographic);
		p.setEmployeeId(details.getEmployeeId());
		p.setAllergies(saveAllAllergy);
		
		return repo.save(p);
	}
	@Override
	public PatientDetails updatePatientDetails(long id, PatientDetails details) {
		
		AllergyDto allergyDto=new AllergyDto();
		DemographicDto demographicDto=new DemographicDto();
		PatientDetails oldEntry = repo.findById(id).get();
		
		List<Allergy> saveAllAllergy = allergyService.saveAllAllergy(details.getAllergies());

		Demographic updateDemographic = demographicService.updateDemographic(oldEntry.getDemographic().getId(),details.getDemographic());
		oldEntry.setDemographic(updateDemographic);
		oldEntry.setEmployeeId(details.getEmployeeId());
		oldEntry.setAllergies(saveAllAllergy);
		return repo.save(oldEntry);
//		return null;
		
	}
	
	
	

}