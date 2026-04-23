import type ListDTO from "~/dtos/ListDTO";
import type { UserDTO } from "~/dtos/UserDTO";

const API_URL = "/api/v1/exerciselists";
const PAGE_SIZE = 10;


//userId?  and nameFilter? because is optional
export async function getExerciseListsFromUser(userId?: string, nameFilter?: string): Promise<ListDTO[]> {
    const params = new URLSearchParams(); 
    if(userId) params.append("userId", userId);
    if(nameFilter) params.append("name", nameFilter);

    const res = await fetch(`${API_URL}/?${params.toString()}`);
    if (!res.ok) {
        throw new Error("Failed to fetch exercise lists");
    }
    return await res.json();
}

export async function getExerciseListById(listId: string): Promise<ListDTO> {
    const res = await fetch(`${API_URL}/${listId}`);
    if (!res.ok) {
        throw new Error("Exercise list not found");
    }
    return await res.json();
}

export async function getListsForUserProfile(page: number, user: UserDTO | null): Promise<{ hasMore: boolean; items: ListDTO[] }> {
    try {
        const response = await fetch(
            `${API_URL}/?page=${page}&size=${PAGE_SIZE}&ownerId=${user ? user.id : ""}`
        );
        if (!response.ok) throw new Error("Error en el servidor");

        const data = await response.json();
        const itemsArray: ListDTO[] = Array.isArray(data) ? data : (data.content || data.data || []);
        

        return {
            hasMore: data.page.number < data.page.totalPages,
            items: itemsArray
        };
    } catch (error) {
        console.error("Error fetching lists for user profile:", error);
        throw new Error("Error fetching lists for user profile");
    }
}

export async function addList(
  title: string,
  topic: string,
  description: string,
): Promise<ListDTO> {
  const response = await fetch(`${API_URL}/`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({
      title: title,
      topic: topic,
      description: description,
    }),
  });

  if (!response.ok) {
    console.error("Failed to add list:", await response.text());
    throw new Error("Error adding list");
  }

  return await response.json();
}

export async function updateList(
  id: string,
  title: string,
  topic: string,
  description: string,
): Promise<ListDTO> {
  const response = await fetch(`${API_URL}/${id}`, {
    method: "PUT",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ title, topic, description }),
  });

  if (!response.ok) {
    console.error("Failed to update list:", await response.text());
    throw new Error("Error updating list");
  }

  return await response.json();
}

export async function deleteList(id: string): Promise<void> {
  const response = await fetch(`${API_URL}/${id}`, {
    method: "DELETE",
  });

  if (!response.ok) {
    throw new Error("Error deleting list");
  }
}
