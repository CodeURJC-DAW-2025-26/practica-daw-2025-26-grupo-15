import type ListDTO from "~/dtos/ListDTO";

const API_URL = "/api/v1/exerciselists";


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
    throw new Error("Error adding book");
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
    throw new Error("Error updating book");
  }

  return await response.json();
}