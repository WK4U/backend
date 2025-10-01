package com.workforyou.backend.service;

import com.workforyou.backend.repository.PrestadorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PrestadorService {

    @Autowired
    private PrestadorRepository prestadorRepository;

}
