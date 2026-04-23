import type { CommentDTO } from "~/dtos/CommentDTO";

const API_URL = "/api/v1/comments";

export async function getCommentsById(id: string): Promise<CommentDTO[]> {
    const res = await fetch(`${API_URL}/${id}`);
    if (!res.ok) {
        throw new Error("Failed to fetch comment");
    }
    return await res.json();
}

export async function deleteComment(commentId: string): Promise<void> {
    const response = await fetch(`${API_URL}/${commentId}`, {
        method: "DELETE"
    });
    if (!response.ok) {
        throw new Error("Error deleting comment");
    }   
}