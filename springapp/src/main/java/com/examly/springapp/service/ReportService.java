package com.examly.springapp.service;

import com.examly.springapp.model.Report;
import com.examly.springapp.repository.ReportRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReportService {
    
    @Autowired
    private ReportRepository reportRepository;
    
    public List<Report> getAllReports() {
        return reportRepository.findAll();
    }
    
    public Report createReport(Report report) {
        return reportRepository.save(report);
    }
    
    public Report submitReport(Report report) {
        return reportRepository.save(report);
    }
}