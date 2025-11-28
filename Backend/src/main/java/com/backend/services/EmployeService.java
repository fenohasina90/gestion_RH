package com.backend.services;

import com.backend.entities.Employe;
import com.backend.entities.Contrat;
import com.backend.repositories.EmployeRepository;
import com.backend.repositories.ContratRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class EmployeService {

    @Autowired
    private EmployeRepository employeRepository;
    
    @Autowired
    private ContratRepository contratRepository;

    public List<Employe> getAllEmployes() {
        return employeRepository.findAll();
    }

    public Employe getEmployeById(Integer id) {
        return employeRepository.findById(id).orElse(null);
    }

    public List<Contrat> getFilteredEmployeeContracts(String nom, Integer departementId, String poste, 
                                                     String typeContrat, Double salaireMin, LocalDate dateDebutFrom) {
        return contratRepository.findFilteredEmployeeContracts(nom, departementId, poste, typeContrat, salaireMin, dateDebutFrom);
    }

    public List<Employe> getEmployesByDepartement(Integer departementId) {
        return employeRepository.findByIddept_Id(departementId);
    }

    public Employe saveEmploye(Employe employe) {
        return employeRepository.save(employe);
    }

    public void deleteEmploye(Integer id) {
        employeRepository.deleteById(id);
    }
}
