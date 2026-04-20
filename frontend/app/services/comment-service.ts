import type { CommentDTO } from "~/dtos/CommentDTO";

const API_URL = "/api/v1/comments";

export async function getCommentsById(id: string): Promise<CommentDTO[]> {
    const res = await fetch(`${API_URL}/${id}`);
    if (!res.ok) {
        throw new Error("Failed to fetch comment");
    }
    return await res.json();
}