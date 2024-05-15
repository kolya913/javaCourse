package com.adagency.model.mapper.client;

import com.adagency.model.dto.client.ClientRegistrationDTO;
import com.adagency.model.dto.person.UserProfileForm;
import com.adagency.model.entity.Client;
import org.springframework.stereotype.Component;

@Component
public class ClientMapperImpl {

    public Client fromRegistrationDtoToCleint(ClientRegistrationDTO clientRegistrationDTO) {
        return Client.builder()
                .email(clientRegistrationDTO.getEmail())
                .password(clientRegistrationDTO.getPassword())
                .phoneNumber(clientRegistrationDTO.getPhoneNumber())
                .name(clientRegistrationDTO.getName())
                .middleName(clientRegistrationDTO.getMiddleName())
                .lastName(clientRegistrationDTO.getLastName())
                .build();
    }
    public UserProfileForm fromClientToUserProfileForm(Client client){
        return UserProfileForm.builder()
                .id(client.getId())
                .email(client.getEmail())
                .password(null)
                .type(client.getType())
                .phoneNumber(client.getPhoneNumber())
                .name(client.getName())
                .middleName(client.getMiddleName())
                .lastName(client.getLastName())
                .company(client.getCompany())
                .role(client.getRole())
                .build();
    }
    
    public Client fromUserProfileFormToClient(UserProfileForm userProfileForm, Client client){
        client.setName(userProfileForm.getName());
        client.setMiddleName(userProfileForm.getMiddleName());
        client.setLastName(userProfileForm.getLastName());
        client.setEmail(userProfileForm.getEmail());
        client.setPhoneNumber(userProfileForm.getPhoneNumber());
        return client;
    }
    
}
