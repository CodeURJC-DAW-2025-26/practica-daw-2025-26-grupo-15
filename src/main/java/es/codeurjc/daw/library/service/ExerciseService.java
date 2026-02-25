package es.codeurjc.daw.library.service;

import org.springframework.stereotype.Service;

import java.io.IOException;

import javax.sql.rowset.serial.SerialBlob;

import org.springframework.beans.factory.annotation.Autowired;
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

    @Transactional
    public Exercise createExercise(Exercise exercise, User user, MultipartFile pdfFile, Long listId) throws IOException{
        this.validateExerciseFields(exercise);
        
        ExerciseList list = exerciseListService.findById(listId);

        if(!list.getOwner().equals(user)) throw new IllegalArgumentException("Action not allowed");

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
        Exercise existing = exerciseRepo.findById(exerciseId)
            .orElseThrow(() -> new IllegalArgumentException("Exercise not found"));

        if (!existing.getOwner().equals(user)) throw new IllegalArgumentException("Not allowed");

        validateExerciseFields(edited);

        existing.setTitle(edited.getTitle());
        existing.setDescription(edited.getDescription());

        if (pdfFile != null && !pdfFile.isEmpty()) {
            this.applyPdf(existing, pdfFile);
        }

        exerciseRepo.save(existing);
        return existing;
    }

    private void validateExerciseFields(Exercise ex) {
        if (ex == null) {
            throw new IllegalArgumentException("Exercise data is missing");
        }

        // ajusta a tus nombres reales: title vs name, etc.
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

    private void applyPdf(Exercise ex, MultipartFile pdf){
        this.validatePdf(pdf);

        try {
            byte[] bytes = pdf.getBytes();
            ex.setPdfImage(new SerialBlob(bytes)); 
        } catch (Exception e) { // IOException / SerialException / SQLException
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

}
