package es.codeurjc.daw.library.service;

import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

import javax.sql.rowset.serial.SerialBlob;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import es.codeurjc.daw.library.model.Exercise;
import es.codeurjc.daw.library.repository.ExerciseRepository;
import jakarta.transaction.Transactional;
import es.codeurjc.daw.library.model.User;
import org.springframework.web.multipart.MultipartFile;
import es.codeurjc.daw.library.model.ExerciseList;

@Service
public class ExerciseService {
    
    @Autowired 
    private ExerciseRepository exerciseRepo;

    @Autowired 
    private ExerciseListService exerciseListService;

    public Exercise findById(Long id) {
        return exerciseRepo.findById(id).orElseThrow(() -> new RuntimeException("Exercise not found"));
    }

    public Page<Exercise> findAll(Pageable pageable){
        return exerciseRepo.findAll(pageable);
    }

     public Page<Exercise> searchExercisesBySimilarTitle(String q, Pageable pageable) {
        return exerciseRepo.searchExercisesBySimilarTitle(q, pageable);
    }

    public List<Exercise> findAllById(List<Long> ids){
        return exerciseRepo.findAllById(ids);
    }

    public Page<Exercise> findByExerciseListId(Pageable pageable, Long listId){
        return exerciseRepo.findByExerciseListId(listId, pageable);
    }

    @Transactional
    public Exercise createExercise(Exercise exercise, User user, MultipartFile pdfFile, Long listId) throws IOException{
        this.validateExerciseFields(exercise);
        
        ExerciseList list = exerciseListService.findById(listId);

        if(!list.getOwner().equals(user)) throw new SecurityException("Action not allowed");

        list.addExercise(exercise);
        exercise.setOwner(user);
        exercise.setNumSolutions(0);

        if (pdfFile != null && !pdfFile.isEmpty()) {
            this.applyPdf(exercise, pdfFile);
        }

        exerciseRepo.save(exercise);
        return exercise;
    }

    @Transactional
    public Exercise updateExercise(Long exerciseId, Exercise edited, User user, MultipartFile pdfFile) {
        Exercise existing = exerciseRepo.findById(exerciseId).orElseThrow();

        if (!existing.getOwner().equals(user)) throw new SecurityException("Not allowed");

        validateExerciseFields(edited);

        existing.setTitle(edited.getTitle());
        existing.setDescription(edited.getDescription());

        if (pdfFile != null && !pdfFile.isEmpty()) {
            this.applyPdf(existing, pdfFile);
        }

        exerciseRepo.save(existing);
        return existing;
    }

    public Exercise deleteExercise(Long exerciseId, User user, boolean isAdmin) {
        Exercise existing = exerciseRepo.findById(exerciseId)
            .orElseThrow(() -> new IllegalArgumentException("Exercise not found"));

        if (!existing.getOwner().equals(user) && !isAdmin) 
            throw new IllegalArgumentException("Not allowed");

        exerciseRepo.delete(existing);

        return existing;
    }

    private void validateExerciseFields(Exercise ex) {
        if (ex == null) {
            throw new IllegalArgumentException("Exercise data is missing");
        }

        String title = ex.getTitle();
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("Name is required");
        }
        if (title.length() > 100) {
            throw new IllegalArgumentException("Name is too long (max 100)");
        }

        String desc = ex.getDescription();
        if (desc == null || desc.trim().isEmpty()) {
            throw new IllegalArgumentException("Description is required");
        }
        if (desc.length() > 2000) {
            throw new IllegalArgumentException("Description is too long (max 2000)");
        }
    }

    public Exercise uploadPdf(Long exerciseId, User user, MultipartFile pdfFile) {
        Exercise exercise = exerciseRepo.findById(exerciseId).orElseThrow();

        if (!exercise.getOwner().getId().equals(user.getId())) throw new SecurityException("Not allowed");
    
        applyPdf(exercise, pdfFile);
        return exerciseRepo.save(exercise);
    }

    public Exercise deletePdf(Long exerciseId, User user, boolean isAdmin) {
        Exercise exercise = exerciseRepo.findById(exerciseId).orElseThrow();

        if (!exercise.getOwner().getId().equals(user.getId()) && !isAdmin) {
            throw new SecurityException("Not allowed");
        }

        if (exercise.getPdfImage() == null) {
            throw new IllegalArgumentException("Exercise does not have a PDF");
        }

        exercise.setPdfImage(null);

        return exerciseRepo.save(exercise);
    }

    private void applyPdf(Exercise ex, MultipartFile pdf){
        this.validatePdf(pdf);

        try {
            byte[] bytes = pdf.getBytes();
            ex.setPdfImage(new SerialBlob(bytes)); 
        } catch (Exception e) { 
            throw new RuntimeException("Error processing uploaded PDF");
        }

    }

    private void validatePdf(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("PDF file is empty");
        }

        if (file.getSize() > 10 * 1024 * 1024) {
            throw new IllegalArgumentException("PDF file is too large (max 10MB)");
        }

        String ct = file.getContentType();
        if (ct != null && !ct.equalsIgnoreCase("application/pdf")) {
            throw new IllegalArgumentException("Uploaded file must be a PDF");
        }

        try {
            byte[] bytes = file.getBytes();
            if (bytes.length < 4 ||
                bytes[0] != 0x25 || bytes[1] != 0x50 || bytes[2] != 0x44 || bytes[3] != 0x46) {
                throw new IllegalArgumentException("Uploaded file is not a valid PDF");
            }
        } catch (Exception e) {
            throw new RuntimeException("Error reading uploaded file");
        }
    }

    public Exercise getExercise(Long id) {
        return exerciseRepo.findById(id).orElseThrow();
    }

}
