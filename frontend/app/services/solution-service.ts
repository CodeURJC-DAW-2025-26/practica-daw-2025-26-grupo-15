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