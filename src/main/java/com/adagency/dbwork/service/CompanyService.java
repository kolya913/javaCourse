package com.adagency.dbwork.service;

import com.adagency.config.MvcConfig;
import com.adagency.dbwork.jparepo.CompanyRepository;
import com.adagency.model.dto.company.CompanyCreateDTO;
import com.adagency.model.dto.company.CompanyView;
import com.adagency.model.entity.Company;
import com.adagency.model.mapper.company.CompanyMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import javax.servlet.ServletContext;
import javax.transaction.Transactional;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
public class CompanyService {
	private final CompanyRepository companyRepository;
	private final MediaFileService mediaFileService;
	private final CompanyMapper companyMapper;
	
	@Autowired
	public CompanyService(CompanyRepository companyRepository, MediaFileService mediaFileService,
						  ServletContext servletContext, CompanyMapper companyMapper){
		this.companyRepository = companyRepository;
		this.mediaFileService = mediaFileService;
		this.companyMapper = companyMapper;
	}

	public List<Company> getAll(){
		return  companyRepository.findAll();
	}
	
	@Transactional
	public void create(CompanyCreateDTO companyCreateDTO) throws Exception {
		Company company = Company.builder()
				.name(companyCreateDTO.getName())
				.build();
		companyRepository.save(company);
		
		/*String appRootPath = System.getProperty("catalina.home");

		String resourcePath = appRootPath + "/webapps/ADAgency2/resources/";

		String fullPath = resourcePath  +
				"images/Company/" + company.getId() + "/" + companyCreateDTO.getFile().getFile().getOriginalFilename();*/
		
		company.setLogo(mediaFileService.testCreateWithTransferFileToPathServer(
				companyCreateDTO.getFile(),
				"CompanyLogo",
				companyCreateDTO.getFile().getDescription(),
				MvcConfig.RESOURCE_PATH +
						"images/Company/" + company.getId() + "/" + companyCreateDTO.getFile().getFile().getOriginalFilename(),
				companyCreateDTO.getFile().getAlt()
		));
		companyRepository.save(company);
	}
	
	
	public String toRelativePath(String absolutePath) {
		return absolutePath.replaceFirst(".*?/ADAgency2/", "");
	}

	@Transactional
	public void delete(Long id) throws IOException, EntityNotFoundException {
		Optional<Company> company = companyRepository.findById(id);
		if(company.isPresent()){
			mediaFileService.delete(company.get().getLogo());
			companyRepository.delete(company.get());
		}else {
			throw new EntityNotFoundException("CompanyWithId" + id);
		}
	}

	@Transactional
	public CompanyView getCompanyView(Long id){
		Optional<Company> company =  companyRepository.findById(id);
		if(company.isPresent()){
			CompanyView companyView = companyMapper.fromCompanyToCompanyView(company.get());
			companyView.setFilePath(toRelativePath(company.get().getLogo().getPath()));
			return  companyView;
		}else {
			throw new EntityNotFoundException("CompanyWithId" + id);
		}
	}
	
	@Transactional
	public void update(CompanyView companyView) throws IllegalArgumentException, IOException {
		if (companyView.getId() == null) {
			throw new IllegalArgumentException("Company ID must not be null");
		}
		if (companyView.getFileId() == null) {
			throw new IllegalArgumentException("Company.FileId ID must not be null");
		}
		Optional<Company> company = companyRepository.findById(companyView.getId());
		if(company.isPresent()){
			if(companyView.getFileId() != null && company.get().getLogo().getId() != companyView.getFileId()){
				throw  new IllegalArgumentException ("FileWithId=" + companyView.getFileId() + "NotSameInDb");
			}
			if(companyView.getId() != null && !companyView.getId().equals(company.get().getId())){
				throw  new IllegalArgumentException ("CompanyWithId=" + companyView.getFileId() + "NotSameInDb");
			}
			//mediaFileService.update(companyView.getId(), companyView.getFileDescription(), companyView.getFileAlt(), companyView.getFile());
			company.get().setName(companyView.getName());
			company.get().setLogo(mediaFileService.update(companyView.getFileId(), companyView.getFileDescription(), companyView.getFileAlt(), companyView.getFile()));
			companyRepository.save(company.get());
		}else{
			throw  new EntityNotFoundException("CompanyWithId=" + companyView.getId() + "NotFound");
		}
	}

}
