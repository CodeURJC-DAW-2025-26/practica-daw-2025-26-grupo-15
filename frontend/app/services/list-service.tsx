import type ListDTO from "~/dtos/ListDTO";

const API_URL = "/api/v1/exerciselists";
const API_IMAGES_URL = "/api/v1/images";

//userId?  and nameFilter? because is optional
export async function getExerciseListsFromUser(userId?: number, nameFilter?: string): Promise<ListDTO[]> {
    const params = new URLSearchParams(); 
    if(userId) params.append("userId", userId.toString());
    if(nameFilter) params.append("name", nameFilter);

    const res = await fetch(`${API_URL}/?${params.toString()}`);
    if (!res.ok) {
        throw new Error("Failed to fetch exercise lists");
    }
    return await res.json();
}

export async function getExerciseListById(listId: number): Promise<ListDTO> {
    const res = await fetch(`${API_URL}/${listId}`);
    if (!res.ok) {
        throw new Error("Exercise list not found");
    }
    return await res.json();
}