package com.workforyou.backend.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
public class CloudinaryService {

    @Autowired
    private Cloudinary cloudinary;

    public String uploadImagem(MultipartFile file) {
        try {
            // Faz o upload enviando os bytes do arquivo
            // O "ObjectUtils.asMap" permite passar parâmetros extras se precisar
            Map uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.emptyMap());

            // Retorna a URL segura (HTTPS) para salvar no banco
            return uploadResult.get("secure_url").toString();

        } catch (IOException e) {
            throw new RuntimeException("Erro ao fazer upload para o Cloudinary", e);
        }
    }
}