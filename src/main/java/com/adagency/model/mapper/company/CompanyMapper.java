package com.adagency.model.mapper.company;

import com.adagency.model.dto.company.CompanyCreateDTO;
import com.adagency.model.dto.company.CompanyView;
import com.adagency.model.entity.Company;
import org.springframework.stereotype.Component;


@Component
public class CompanyMapper {
	public Company fromCompanyCreateDTOToCompany(CompanyCreateDTO companyCreateDTO, Company company){
		return Company.builder().build();
	}

	public CompanyView fromCompanyToCompanyView(Company company){
		return CompanyView.builder()
				.id(company.getId())
				.name(company.getName())
				.fileAlt(company.getLogo().getAlt())
				.fileDescription(company.getLogo().getDescription())
				.fileId(company.getLogo().getId())
				.filePath(company.getLogo().getPath())
				.build();
	}

	public Company updateFromCompanyViewToCompany(Company company, CompanyView companyView){
		company.setName(companyView.getName());
		return company;
	}

}
