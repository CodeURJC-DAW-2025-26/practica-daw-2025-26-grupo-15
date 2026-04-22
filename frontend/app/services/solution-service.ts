import type  { SolutionDTO }  from "~/dtos/SolutionDTO";
import type { CommentDTO } from "~/dtos/CommentDTO";

const API_URL = "/api/v1/solutions";

export async function getSolution(id: string): Promise<SolutionDTO> {
    const res = await fetch(`${API_URL}/${id}`);
    if (!res.ok) {
        throw new Error("Solution not found");
    }
    return await res.json();
}

export async function findCommentsBySolutionId(id: string): Promise<CommentDTO[]> {
    const res = await fetch(`${API_URL}/${id}/comments`);
    if (!res.ok) {
        throw new Error("Failed to fetch comments");
    }
    return await res.json();
}

export async function addComment(solutionId: number, text: string) {
    const res = await fetch(`${API_URL}/${solutionId}/comment`, {
        method: "POST",
        headers: { "Content-Type": "application/json",},
        body: JSON.stringify({ text }),
    });
    
    if (!res.ok) {
        throw new Error("Failed to add comment");
    }
    return await res.json();
}

export async function addSolution(exerciseId: string, name: string, description: string): Promise<SolutionDTO> {
    const res = await fetch(`/api/v1/exercises/${exerciseId}/solutions/`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ name, description }),
    });
    if (!res.ok) {
        throw new Error("Failed to add solution");
    }
    return await res.json();
}

export async function uploadSolutionImage(solutionId: number, imageFile: File): Promise<void> {
    const formData = new FormData();
    formData.append("imageFile", imageFile);

    const res = await fetch(`${API_URL}/${solutionId}/images`, {
        method: "POST",
        body: formData,
    });

    if (!res.ok) {
        throw new Error("Failed to upload solution image");
    }
}