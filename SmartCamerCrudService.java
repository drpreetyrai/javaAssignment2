
package com.tymoshenko.controller.repository;

import com.tymoshenko.model.Whisky;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Exposes CRUD API for Whisky entity.
 *
 * @author Yakiv Tymoshenko
 * @since 15.03.2016
 */
@Service
public class SmartCameraCrudService implements CrudService<Whisky> {

    @Autowired
    private SmartCameraRepository whiskyRepository;

    public  save(SmartCamera smartcamera) {
        return smartcameraRepository.saveAndFlush(smartcamera);
    }

    public SmartCamera readOne(Long id) {
        return smartcameraRepository.findOne(id);
    }

    public List<SmartCamera> readAll() {
        return smartcameraRepository.findAll();
    }

    public void delete(Long id) {
        smartcameraRepository.delete(id);
    }
}


