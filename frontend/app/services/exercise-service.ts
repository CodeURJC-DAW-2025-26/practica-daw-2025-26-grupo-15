import type { ExerciseDTO } from "~/dtos/ExerciseDTO";

const API_URL = "/api/v1/exercises";

export async function getExercises(): Promise<ExerciseDTO[]> {
  const res = await fetch(`${API_URL}/`);
  return await res.json();
}

export async function getExercise(id: string): Promise<ExerciseDTO> {
  const res = await fetch(`${API_URL}/${id}`);
  if (!res.ok) {
    throw new Error("Exercise not found");
  }
  return await res.json();
}

export async function addExercise(
  title: string,
  description: string,
  listId: string
): Promise<ExerciseDTO> {
 

    const response = await fetch(`/api/v1/exerciselists/${listId}/exercises/`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({
      title: title,
      description: description
    }),
  });

  if (!response.ok) {
    throw new Error("Error adding exercise");
  }

  return await response.json();
}

export async function uploadExercisePDF(exerciseId: number, file: File): Promise<void> {
    const formData = new FormData();
    formData.append("pdfFile", file);
    
    const response = await fetch(`/api/v1/exercises/${exerciseId}/pdf`, {
        method: "PUT",
        body: formData
    })

    if (!response.ok) {
    throw new Error("Error uploading pdf file");
    }

}

export async function getExercisePdf(exerciseId: number): Promise<Blob> {
    try {
        const response = await fetch(`${API_URL}/${exerciseId}/pdf`, {
            method: "GET"
        });
        if (!response.ok) {
            throw new Error(`Error downloading pdf file: ${response.status}`);
        }

        return await response.blob();;
    } catch (error) {
        console.error("Error fetching PDF:", error);
        throw new Error("Error downloading pdf file");
    }
}

export async function deleteExercise(exerciseId: number): Promise<void> {
    const response = await fetch(`${API_URL}/${exerciseId}`, {
        method: "DELETE"
    });
    if (!response.ok) {
        throw new Error("Error deleting exercise");
    }
}

export async function updateExercise(exerciseId: string, title: string, description: string): Promise<void> {
    const response = await fetch(`${API_URL}/${exerciseId}`, {
        method: "PUT",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({
            title: title,
            description: description
        }),
    });
    if (!response.ok) {
        throw new Error("Error updating exercise");
    } 
}