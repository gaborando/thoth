package com.thoth.server.service;

import com.mysema.commons.lang.Assert;
import com.thoth.server.beans.IAuthenticationFacade;
import com.thoth.server.controller.view.SecuredResourceView;
import com.thoth.server.model.domain.Secret;
import com.thoth.server.model.domain.Template;
import com.thoth.server.model.domain.security.ResourcePermission;
import com.thoth.server.model.domain.security.SecuredResource;
import com.thoth.server.model.repository.SecretRepository;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.*;

@Service
public class SecretService {

    private final SecretRepository secretRepository;

    private final String secret;

    private final IAuthenticationFacade authenticationFacade;

    public SecretService(SecretRepository secretRepository,
                         @Value("${thoth.secret.secret}") String secret,
                         IAuthenticationFacade authenticationFacade) {
        this.secretRepository = secretRepository;
        this.secret = secret;
        this.authenticationFacade = authenticationFacade;

    }

    @SneakyThrows
    private String encrypt(String value) {
        SecretKeySpec secretKey = new SecretKeySpec(secret.getBytes(), "AES");
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        byte[] encryptedBytes = cipher.doFinal(value.getBytes());
        return Base64.getEncoder().encodeToString(encryptedBytes);
    }


    @SneakyThrows
    private String decrypt(String encryptedText){
        SecretKeySpec secretKey = new SecretKeySpec(secret.getBytes(), "AES");
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        byte[] decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(encryptedText));
        return new String(decryptedBytes);
    }
    public Secret create(String name, String value) {
        Assert.isTrue(!secretRepository.existsById(name), "Secret with this name already exists");
        var secret = new Secret();
        secret.setName(name);
        secret.setSalt(UUID.randomUUID().toString());
        secret.setValue(encrypt(value + "_SALT_" + secret.getSalt()));
        secret.setCreatedAt(Instant.now());
        authenticationFacade.fillSecuredResource(secret);
        return secretRepository.save(secret);
    }

    @PreAuthorize("@authenticationFacade.canWrite(#secret)")
    public void delete(Secret secret){
        secretRepository.delete(secret);
    }

    public Page<Secret> search(Specification<Secret> specification, Pageable pageable){
        return secretRepository.findAll(authenticationFacade.securedSpecification(specification, Secret.class), pageable);
    }

    @PreAuthorize("@authenticationFacade.canWrite(#secret)")
    public Secret update(Secret secret, List<ResourcePermission> allowedUserList, List<ResourcePermission> allowedOrganizationList){
        secret.setAllowedUserList(allowedUserList);
        secret.setAllowedOrganizationList(allowedOrganizationList);
        return secretRepository.save(secret);
    }

    @PostAuthorize("@authenticationFacade.canRead(returnObject)")
    public Optional<Secret> getById(String identifier) {
        return secretRepository.findById(identifier);
    }

    public String getValue(String name, boolean checkAuth) {
        var o = secretRepository.findById(name);
        if(o.isEmpty()) return "";
        var secret = o.get();
        Assert.isTrue(!checkAuth || authenticationFacade.canRead(secret), "You do not have access to read this secret: " + name);
        return decrypt(secret.getValue()).replace("_SALT_" + secret.getSalt(), "");
    }
}
